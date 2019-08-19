package com.xl.miaosha.result;

//封装返回的结果信息
public class Result<T> {

    private int code;
    private String msg;
    private T data;

    public static <T> Result<T> success(T data){
        return new Result<>(data);
    }

    public static <T> Result<T> error(CodeMsg cm){
        return new Result<>(cm);
    }

    private Result(CodeMsg cm){
        if(cm==null)
            return;
        this.code = cm.getCode();
        this.msg = cm.getMsg();
    }

    private Result(T data){
        this.code = 0;
        this.msg = "success";
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }
}
