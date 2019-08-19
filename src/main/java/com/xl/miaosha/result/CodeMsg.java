package com.xl.miaosha.result;

public class CodeMsg {

    private int code;
    private String msg;

//    通用异常
    public static CodeMsg BIND_ERROR = new CodeMsg(500100,"参数校验异常:%s");
    public static CodeMsg SERVER_ERROR = new CodeMsg(500101,"服务端异常");
    public static CodeMsg REQUEST_ELLEGAL = new CodeMsg(500102,"请求非法");
    public static CodeMsg VERIFYCODE_ERROR = new CodeMsg(500103,"验证码错误");
    //    登录异常
    public static CodeMsg MOBILE_NOT_EXISTS = new CodeMsg(500200,"手机号不存在");
    public static CodeMsg PASSWORD_ERROR = new CodeMsg(500201,"密码错误");
    public static CodeMsg SESSION_ERROR = new CodeMsg(500202,"session失效或者不存在");

//    订单异常
public static CodeMsg ORDER_NOT_EXISTS= new CodeMsg(500300,"订单不存在");

//    秒杀异常
    public static CodeMsg STOCK_EMPTY = new CodeMsg(500400,"库存不足");
    public static CodeMsg REPEATE_MIAOSHA = new CodeMsg(500401,"重复下单");
    public static CodeMsg ACCESS_LIMIT = new CodeMsg(500402,"访问太频繁");

    private CodeMsg(int code,String msg){
        this.code = code;
        this.msg = msg;
    }

    public CodeMsg fillArgs(Object... args){
        int code = this.code;
        String msg = String.format(this.msg, args);
        return new CodeMsg(code,msg);
    }


    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
