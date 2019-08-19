package com.xl.miaosha.service;

import com.xl.miaosha.domain.MiaoshaOrder;
import com.xl.miaosha.domain.MiaoshaUser;
import com.xl.miaosha.domain.OrderInfo;
import com.xl.miaosha.redis.GoodsKey;
import com.xl.miaosha.redis.MiaoshaKey;
import com.xl.miaosha.redis.RedisService;
import com.xl.miaosha.utils.Md5Util;
import com.xl.miaosha.utils.UUIDUtil;
import com.xl.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

@Service
public class MiaoshaService {

    @Autowired
    OrderService orderService;
    @Autowired
    GoodsService goodsService;
    @Autowired
    RedisService redisService;

    private static char[] ops = {'+','-','*'};

    @Transactional
    public OrderInfo miaosha(MiaoshaUser user, GoodsVo goods){
        boolean res = goodsService.reduceStock(goods);
        if(res){
            return orderService.createOrder(user, goods);
        }else {
            setGoodsOver(goods.getId());
            return null;
        }
    }

    public long getResult(long userId,long goodsId){
        MiaoshaOrder order = orderService.getByUserIdAndGoodsId(userId, goodsId);
        if(order!=null){
            return order.getOrderId();
        }else {
            boolean over = getGoodsOver(goodsId);
            if(over){
                return -1;
            }else {
                return 0;
            }
        }
    }

    public void setGoodsOver(long goodsId){
        redisService.set(GoodsKey.goodsOver,""+goodsId,true);
    }

    public boolean getGoodsOver(long goodsId){
        return redisService.exists(GoodsKey.goodsOver,""+goodsId);
    }

    public String createPath(long userId,long goodsId){
        String path = Md5Util.md5(UUIDUtil.random()+"123456");
        redisService.set(MiaoshaKey.getPath,userId+"-"+goodsId,path);
        return path;
    }

    public boolean checkPath(long userId,long goodsId,String path){
        if(StringUtils.isEmpty(path)){
            return false;
        }
        String oldPath = redisService.get(MiaoshaKey.getPath, userId + "-" + goodsId, String.class);
        return path.equals(oldPath);
    }

//    生存图形验证码
    public BufferedImage generateVerifyCode(MiaoshaUser user,long goodsId){
        int width = 80;
        int height = 32;
        //create the image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        // set the background color
        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0, 0, width, height);
        // draw the border
        g.setColor(Color.black);
        g.drawRect(0, 0, width - 1, height - 1);
        // create a random instance to generate the codes
        Random rdm = new Random();
        // make some confusion
        for (int i = 0; i < 50; i++) {
            int x = rdm.nextInt(width);
            int y = rdm.nextInt(height);
            g.drawOval(x, y, 0, 0);
        }
        // generate a random code
        String verifyCode = generateVerifyCode(rdm);
        g.setColor(new Color(0, 100, 0));
        g.setFont(new Font("Candara", Font.BOLD, 24));
        g.drawString(verifyCode, 8, 24);
        g.dispose();
        //把验证码存到redis中
        int rnd = calc(verifyCode);
        redisService.set(MiaoshaKey.getMiaoshaVerifyCode, user.getId()+"-"+goodsId, rnd);
        //输出图片
        return image;
    }

//    接收客户端验证码进行验证
    public boolean checkVerifyCode(long userId,long goodsId,Integer code){
        Integer verifyCode = redisService.get(MiaoshaKey.getMiaoshaVerifyCode, userId + "-" + goodsId, Integer.class);
        if(verifyCode==null || verifyCode!=code){
            return false;
        }
//        用完删除
        redisService.del(MiaoshaKey.getMiaoshaVerifyCode, userId + "-" + goodsId);
        return true;
    }

//    计算验证码的结果
    private int calc(String verifyCode) {
        ScriptEngineManager engine = new ScriptEngineManager();
        ScriptEngine javaScript = engine.getEngineByName("JavaScript");
        try {
            return (int)javaScript.eval(verifyCode);
        } catch (ScriptException e) {
            e.printStackTrace();
            return 0;
        }
    }

//生成验证码上的字符串
    private String generateVerifyCode(Random rdm) {
        int num1 = rdm.nextInt(10);
        int num2 = rdm.nextInt(10);
        int num3 = rdm.nextInt(10);
        char op1 = ops[rdm.nextInt(3)];
        char op2 = ops[rdm.nextInt(3)];
        return ""+num1 + op1 + num2 + op2 + num3;
    }

}
