package com.xl.miaosha.redis;

public abstract class BaseKeyPrefix implements KeyPrefix{

    private int expireSeconds;
    private String prefix;

    public BaseKeyPrefix(String prefix) {
        this(0,prefix);
    }

    public BaseKeyPrefix(int expireSeconds, String prefix) {
        this.expireSeconds = expireSeconds;
        this.prefix = prefix;
    }

    @Override
    public int expireSeconds() {
        return expireSeconds;
    }

    @Override
    public String getPrefix() {
        String name = getClass().getName();
        return name + ":" + prefix;
    }

}
