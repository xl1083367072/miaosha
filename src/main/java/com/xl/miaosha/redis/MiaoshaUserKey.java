package com.xl.miaosha.redis;

public class MiaoshaUserKey extends BaseKeyPrefix{

    private static final int USER_EXPIRE = 60*60*24*2;

    private MiaoshaUserKey(int expireSeconds,String prefix) {
        super(expireSeconds,prefix);
    }

    public static MiaoshaUserKey token = new MiaoshaUserKey(USER_EXPIRE,"token");
    public static MiaoshaUserKey id = new MiaoshaUserKey(USER_EXPIRE,"id");
}
