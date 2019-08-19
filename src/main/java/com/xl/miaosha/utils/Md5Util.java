package com.xl.miaosha.utils;

import org.apache.commons.codec.digest.DigestUtils;

public class Md5Util {

    private static final String salt = "1a2b3c4d";

    public static String md5(String src){
        return DigestUtils.md5Hex(src);
    }

    public static String inputPassToFormPass(String inputPass) {
        String str = ""+salt.charAt(0)+salt.charAt(2) + inputPass +salt.charAt(5) + salt.charAt(4);
        System.out.println(str);
        return md5(str);
    }

    public static String form2DB(String formPass,String salt){
        String str = ""+salt.charAt(0)+salt.charAt(2) + formPass +salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }

    public static String input2DB(String inputPass,String saltDB){
        String formPass = inputPassToFormPass(inputPass);
        String dbPass = form2DB(formPass, saltDB);
        return dbPass;
    }

    public static void main(String[] args) {
        String s = input2DB("123456", "1a2b3c4d");
        System.out.println(s);
    }

}
