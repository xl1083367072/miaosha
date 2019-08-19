package com.xl.miaosha.vo;

import com.xl.miaosha.domain.OrderInfo;

public class OrderDetailVo {

    private OrderInfo orderInfo;

    private GoodsVo goodsVo;

    public OrderInfo getOrderInfo() {
        return orderInfo;
    }

    public void setOrderInfo(OrderInfo orderInfo) {
        this.orderInfo = orderInfo;
    }

    public GoodsVo getGoodsVo() {
        return goodsVo;
    }

    public void setGoodsVo(GoodsVo goodsVo) {
        this.goodsVo = goodsVo;
    }
}
