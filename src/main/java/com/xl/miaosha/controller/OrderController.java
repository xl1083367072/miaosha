package com.xl.miaosha.controller;

import com.xl.miaosha.domain.MiaoshaUser;
import com.xl.miaosha.domain.OrderInfo;
import com.xl.miaosha.result.CodeMsg;
import com.xl.miaosha.result.Result;
import com.xl.miaosha.service.GoodsService;
import com.xl.miaosha.service.OrderService;
import com.xl.miaosha.vo.GoodsVo;
import com.xl.miaosha.vo.OrderDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    OrderService orderService;
    @Autowired
    GoodsService goodsService;

    @RequestMapping("/detail/{orderId}")
    @ResponseBody
    public Result<OrderDetailVo> orderDetail(@PathVariable("orderId")long orderId, MiaoshaUser user){
        if(user==null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        OrderInfo orderInfo = orderService.getById(orderId);
        if(orderInfo==null){
            return Result.error(CodeMsg.ORDER_NOT_EXISTS);
        }
        GoodsVo goodsVo = goodsService.getByGoodsId(orderInfo.getGoodsId());
        OrderDetailVo vo = new OrderDetailVo();
        vo.setGoodsVo(goodsVo);
        vo.setOrderInfo(orderInfo);
        return Result.success(vo);
    }

}
