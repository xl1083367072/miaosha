package com.xl.miaosha.redis;

public class GoodsKey extends BaseKeyPrefix{

    private GoodsKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static GoodsKey goodsList= new GoodsKey(60,"goodsList");
    public static GoodsKey goodsDetail= new GoodsKey(60,"goodsDetail");
    public static GoodsKey goodsStock= new GoodsKey(0,"goodsStock");
    public static GoodsKey goodsOver= new GoodsKey(0,"goodsOver");
}
