package com.xl.miaosha.rabbitmq;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MQConfig {

    public static final String queue = "miaosha.queue";

    @Bean
    public Queue queue(){
        return new Queue(queue,true);
    }

}
