package com.xl.miaosha.rabbitmq;

import com.xl.miaosha.domain.MiaoshaOrder;
import com.xl.miaosha.domain.MiaoshaUser;
import com.xl.miaosha.redis.RedisService;
import com.xl.miaosha.service.GoodsService;
import com.xl.miaosha.service.MiaoshaService;
import com.xl.miaosha.service.OrderService;
import com.xl.miaosha.vo.GoodsVo;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQReveiver {

    @Autowired
    GoodsService goodsService;
    @Autowired
    OrderService orderService;
    @Autowired
    MiaoshaService miaoshaService;

    @RabbitListener(queues = MQConfig.queue)
    public void handleMiaosha(String msg){
        MiaoshaMessage mm = RedisService.stringToBean(msg, MiaoshaMessage.class);
        long goodsId = mm.getGoodsId();
        MiaoshaUser user = mm.getUser();
        GoodsVo goods = goodsService.getByGoodsId(goodsId);
        int stock = goods.getGoodsStock();
        if(stock<=0){
            return;
        }
        MiaoshaOrder order = orderService.getByUserIdAndGoodsId(user.getId(), goodsId);
        if(order!=null){
            return;
        }
        miaoshaService.miaosha(user,goods);
    }

}
