package org.linlinjava.litemall.core.config;

import org.linlinjava.litemall.core.util.ResponseUtil;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseBody
    public Object argumentHandler(MethodArgumentTypeMismatchException e){
        e.printStackTrace();
        return ResponseUtil.badArgumentValue();
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public Object httpMessageNotReadableHandler(HttpMessageNotReadableException e){
        e.printStackTrace();
        return ResponseUtil.badArgumentValue();
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Object exceptionHandler(Exception e){
        e.printStackTrace();
        return ResponseUtil.serious();
    }
}
