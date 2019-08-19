package com.xl.miaosha.redis;

public interface KeyPrefix {

    int expireSeconds();

    String getPrefix();

}
