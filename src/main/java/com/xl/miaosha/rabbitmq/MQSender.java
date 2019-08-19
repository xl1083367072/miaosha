package com.xl.miaosha.rabbitmq;

import com.xl.miaosha.redis.RedisService;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQSender {

    @Autowired
    AmqpTemplate template;

    public void sendMiaosha(MiaoshaMessage msg){
        String message = RedisService.beanToString(msg);
        template.convertAndSend(MQConfig.queue,message);
    }

}
