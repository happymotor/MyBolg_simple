package com.myblog.exception;

import com.myblog.Dto.Result;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalException {
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e){
        e.printStackTrace();
        return Result.fail(StringUtils.hasLength(e.getMessage())?e.getMessage():"操作失败");
    }
}
