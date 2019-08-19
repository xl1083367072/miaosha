package com.xl.miaosha.config;

import com.xl.miaosha.domain.MiaoshaUser;

public class UserThreadLocal {

    private static ThreadLocal<MiaoshaUser> threadLocal = new ThreadLocal<>();

    public static MiaoshaUser getUser(){
        return threadLocal.get();
    }

    public static void setUser(MiaoshaUser user){
        threadLocal.set(user);
    }

}
