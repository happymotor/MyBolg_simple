package com.myblog.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> {
    private Integer code;//响应码 0-成功 1-失败
    private  String msg;//操作提示信息
    private T data;//响应内容
    private Long timestamp;//时间戳

    //成功带信息
    public static <E> Result<E> success(E data){
        return new Result<>(0,"操作成功",data,System.currentTimeMillis());
    }

    //成功不带信息
    public static Result success(){
        return new Result<>(0,"操作成功",null,System.currentTimeMillis());
    }

    //失败带错误信息
    public static Result fail(String msg){
        return new Result<>(1,msg,null,System.currentTimeMillis());
    }

}
