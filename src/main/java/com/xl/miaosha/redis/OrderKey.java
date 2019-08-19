package com.xl.miaosha.redis;

public class OrderKey extends BaseKeyPrefix{

    private OrderKey(String prefix) {
        super(prefix);
    }

    public static OrderKey orderInfo = new OrderKey("orderInfo");
}
