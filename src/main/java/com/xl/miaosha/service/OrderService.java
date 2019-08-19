package com.xl.miaosha.service;

import com.xl.miaosha.dao.OrderDao;
import com.xl.miaosha.domain.MiaoshaOrder;
import com.xl.miaosha.domain.MiaoshaUser;
import com.xl.miaosha.domain.OrderInfo;
import com.xl.miaosha.redis.OrderKey;
import com.xl.miaosha.redis.RedisService;
import com.xl.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class OrderService {

    @Autowired
    OrderDao orderDao;

    @Autowired
    RedisService redisService;

    public MiaoshaOrder getByUserIdAndGoodsId(Long userId,Long goodsId){
        return redisService.get(OrderKey.orderInfo,userId+"-"+goodsId,MiaoshaOrder.class);
    }

    public OrderInfo getById(long id){
        return orderDao.getById(id);
    }

    @Transactional
    public OrderInfo createOrder(MiaoshaUser user, GoodsVo goods){
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateDate(new Date());
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goods.getId());
        orderInfo.setGoodsName(goods.getGoodsName());
        orderInfo.setGoodsPrice(goods.getMiaoshaPrice());
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0);
        orderInfo.setUserId(user.getId());
        orderDao.insertOrder(orderInfo);
        MiaoshaOrder miaoshaOrder = new MiaoshaOrder();
        miaoshaOrder.setGoodsId(goods.getId());
        miaoshaOrder.setOrderId(orderInfo.getId());
        miaoshaOrder.setUserId(user.getId());
        orderDao.insertMiaoshaOrder(miaoshaOrder);
        redisService.set(OrderKey.orderInfo,user.getId()+"-"+goods.getId(),miaoshaOrder);
        return orderInfo;
    }

}
