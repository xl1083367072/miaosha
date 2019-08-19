package com.xl.miaosha.controller;

import com.xl.miaosha.domain.MiaoshaUser;
import com.xl.miaosha.redis.GoodsKey;
import com.xl.miaosha.redis.RedisService;
import com.xl.miaosha.result.Result;
import com.xl.miaosha.service.GoodsService;
import com.xl.miaosha.service.MiaoshaUserService;
import com.xl.miaosha.vo.GoodsDetailVo;
import com.xl.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    MiaoshaUserService miaoshaUserService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    RedisService redisService;

    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;

    @RequestMapping(value = "/toList",produces = "text/html")
    @ResponseBody
    public String toList(HttpServletRequest request, HttpServletResponse response,Model model, MiaoshaUser user){
        model.addAttribute("user",user);
//        页面级缓存
//        取页面缓存
        String html = redisService.get(GoodsKey.goodsList, "", String.class);
        if(!StringUtils.isEmpty(html)){
            return html;
        }
        List<GoodsVo> list = goodsService.list();
        model.addAttribute("goodsList",list);
//        如果没有缓存，则手动渲染并放入缓存
        WebContext context = new WebContext(request,response,request.getServletContext(),request.getLocale(),model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goods_list", context);
        redisService.set(GoodsKey.goodsList,"",html);
        return html;
    }

//    Url级缓存
    /*@RequestMapping("/toDetail/{id}")
    @ResponseBody
    public String toDetail(HttpServletRequest request, HttpServletResponse response,Model model, MiaoshaUser user, @PathVariable("id")long id){
        model.addAttribute("user",user);
        String html = redisService.get(GoodsKey.goodsDetail, id + "", String.class);
        if(!StringUtils.isEmpty(html)){
            return html;
        }
        GoodsVo goods = goodsService.getByGoodsId(id);
        long startDate = goods.getStartDate().getTime();
        long endDate = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();
        int miaoshaStatus = 0;
        long remainSeconds = 0;
        if(startDate>now){//未开始
            miaoshaStatus = 0;
            remainSeconds = (startDate-now)/1000;
        }else if(endDate<now){//已结束
            miaoshaStatus = 2;
            remainSeconds = -1;
        }else {//进行中
            miaoshaStatus = 1;
            remainSeconds = 0;
        }
        model.addAttribute("goods",goods);
        model.addAttribute("miaoshaStatus",miaoshaStatus);
        model.addAttribute("remainSeconds",remainSeconds);
//        url级缓存
        WebContext context = new WebContext(request,response,request.getServletContext(),request.getLocale(),model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goods_detail", context);
        redisService.set(GoodsKey.goodsDetail,""+id,html);
        return html;
    }*/

//    页面静态化（前后端分离）
    @RequestMapping("/toDetail/{id}")
    @ResponseBody
    public Result<GoodsDetailVo> toDetail(MiaoshaUser user, @PathVariable("id")long id){
        GoodsVo goods = goodsService.getByGoodsId(id);
        long startDate = goods.getStartDate().getTime();
        long endDate = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();
        int miaoshaStatus = 0;
        long remainSeconds = 0;
        if(startDate>now){//未开始
            miaoshaStatus = 0;
            remainSeconds = (startDate-now)/1000;
        }else if(endDate<now){//已结束
            miaoshaStatus = 2;
            remainSeconds = -1;
        }else {//进行中
            miaoshaStatus = 1;
            remainSeconds = 0;
        }
        GoodsDetailVo vo = new GoodsDetailVo();
        vo.setGoodsVo(goods);
        vo.setMiaoshaStatus(miaoshaStatus);
        vo.setRemainSeconds(remainSeconds);
        vo.setUser(user);
        return Result.success(vo);
    }

}
