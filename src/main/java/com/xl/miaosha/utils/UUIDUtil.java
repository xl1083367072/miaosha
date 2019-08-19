package com.xl.miaosha.utils;

import java.util.UUID;

public class UUIDUtil {

    public static String random(){
        return UUID.randomUUID().toString().replace("-","");
    }

}
