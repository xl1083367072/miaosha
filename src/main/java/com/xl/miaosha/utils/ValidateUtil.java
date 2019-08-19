package com.xl.miaosha.utils;

import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidateUtil {

    private static Pattern pattern = Pattern.compile("1\\d{10}");

    public static boolean isMobile(String mobile){
        if(StringUtils.isEmpty(mobile)){
            return false;
        }
        Matcher matcher = pattern.matcher(mobile);
        return matcher.matches();
    }

}
