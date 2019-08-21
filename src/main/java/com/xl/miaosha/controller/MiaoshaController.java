package com.xl.miaosha.controller;

import com.xl.miaosha.config.AccessLimit;
import com.xl.miaosha.domain.MiaoshaOrder;
import com.xl.miaosha.domain.MiaoshaUser;
import com.xl.miaosha.rabbitmq.MQSender;
import com.xl.miaosha.rabbitmq.MiaoshaMessage;
import com.xl.miaosha.redis.GoodsKey;
import com.xl.miaosha.redis.RedisService;
import com.xl.miaosha.result.CodeMsg;
import com.xl.miaosha.result.Result;
import com.xl.miaosha.service.GoodsService;
import com.xl.miaosha.service.MiaoshaService;
import com.xl.miaosha.service.OrderService;
import com.xl.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/miaosha")
public class MiaoshaController implements InitializingBean {

    @Autowired
    MiaoshaService miaoshaService;
    @Autowired
    GoodsService goodsService;
    @Autowired
    OrderService orderService;
    @Autowired
    RedisService redisService;
    @Autowired
    MQSender sender;

    private static Map<Long,Boolean> map = new HashMap<>();

    private static Map<Long,Integer> originalStock = new HashMap<>();

//    1.将商品数量预加载到缓存中
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> list = goodsService.list();
        for (GoodsVo goodsVo:list){
            redisService.set(GoodsKey.goodsStock,""+goodsVo.getId(),goodsVo.getStockCount());
            originalStock.put(goodsVo.getId(),goodsVo.getStockCount());
            map.put(goodsVo.getId(),false);
        }
    }

//    QPS:2800
    @AccessLimit(seconds = 5,maxCount = 5,needLogin = true)
    @RequestMapping("/{path}/miaosha")
    @ResponseBody
    public Result<Integer> miaosha(@PathVariable("path")String path, @RequestParam("id")long id, MiaoshaUser user, Model model){
        if(user==null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        boolean check = miaoshaService.checkPath(user.getId(),id,path);
        if(!check){
            return Result.error(CodeMsg.REQUEST_ELLEGAL);
        }
        Boolean flag = map.get(id);
        if(flag){
            return Result.error(CodeMsg.STOCK_EMPTY);
        }
//        2.缓存中减库存
        long res = redisService.decr(GoodsKey.goodsStock, "" + id);
        if(res<0){
            map.put(id,true);
            return Result.error(CodeMsg.STOCK_EMPTY);
        }
       /* GoodsVo goods = goodsService.getByGoodsId(id);
        int stockCount = goods.getStockCount();
        if(stockCount<=0){
            return Result.error(CodeMsg.STOCK_EMPTY);
        }*/
//       是否重复下单
        MiaoshaOrder miaoshaOrder = orderService.getByUserIdAndGoodsId(user.getId(), id);
        if(miaoshaOrder!=null){
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }
//        异步下单
        MiaoshaMessage msg =  new MiaoshaMessage();
        msg.setGoodsId(id);
        msg.setUser(user);
        sender.sendMiaosha(msg);
//        立即返回排队中
        return Result.success(0);
    }

    public static Integer getOriginalStock(long goodsId){
        Integer stock = originalStock.get(goodsId);
        if(stock==null)
            return 0;
        return stock;
    }

    @RequestMapping("/result")
    @ResponseBody
    public Result<Long> getResult(@RequestParam("goodsId")long goodsId,MiaoshaUser user){
        if(user==null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        long result = miaoshaService.getResult(user.getId(), goodsId);
        return Result.success(result);
    }

    @AccessLimit(seconds = 5,maxCount = 5,needLogin = true)
    @RequestMapping("/path")
    @ResponseBody
    public Result<String> getPath(@RequestParam("goodsId")long goodsId,MiaoshaUser user,@RequestParam(value = "verifyCode",defaultValue = "0")int verifyCode){
        if(user==null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        boolean check = miaoshaService.checkVerifyCode(user.getId(), goodsId, verifyCode);
        if(!check){
            return Result.error(CodeMsg.SERVER_ERROR);
        }
        String path = miaoshaService.createPath(user.getId(), goodsId);
        return Result.success(path);
    }

    @AccessLimit(seconds = 5,maxCount = 5,needLogin = true)
    @RequestMapping("/verifyCode")
    @ResponseBody
    public Result generateVerifyCode(@RequestParam("goodsId")long goodsId, MiaoshaUser user, HttpServletResponse response){
        if(user==null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        BufferedImage bf = miaoshaService.generateVerifyCode(user, goodsId);
        try (OutputStream os = response.getOutputStream()) {
            ImageIO.write(bf,"JPEG",os);
            os.flush();
            return null;
        }catch (Exception e){
            e.printStackTrace();
            return Result.error(CodeMsg.SERVER_ERROR);
        }
    }

}
