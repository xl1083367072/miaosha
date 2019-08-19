package com.xl.miaosha.redis;

public class MiaoshaKey extends BaseKeyPrefix{

    public MiaoshaKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static MiaoshaKey getPath = new MiaoshaKey(60,"path");
    public static MiaoshaKey getMiaoshaVerifyCode = new MiaoshaKey(60*5,"verifyCode");

    public static MiaoshaKey getLimitKey(int expireSeconds){
        return new MiaoshaKey(expireSeconds,"accessLimit");
    }
}
