package org.linlinjava.litemall.core.config;

import org.hibernate.validator.internal.engine.path.PathImpl;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.util.Set;

@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseBody
    public Object argumentHandler(MethodArgumentTypeMismatchException e){
        e.printStackTrace();
        return ResponseUtil.badArgumentValue();
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    public Object argumentHandler(MissingServletRequestParameterException e){
        e.printStackTrace();
        return ResponseUtil.badArgumentValue();
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public Object httpMessageNotReadableHandler(HttpMessageNotReadableException e){
        e.printStackTrace();
        return ResponseUtil.badArgumentValue();
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseBody
    public Object handle(ValidationException e) {
        e.printStackTrace();
        if(e instanceof ConstraintViolationException){
            ConstraintViolationException exs = (ConstraintViolationException) e;
            Set<ConstraintViolation<?>> violations = exs.getConstraintViolations();
            for (ConstraintViolation<?> item : violations) {
                String message = ((PathImpl)item.getPropertyPath()).getLeafNode().getName() +item.getMessage();
                return ResponseUtil.fail(402, message);
            }
        }
        return ResponseUtil.badArgumentValue();
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Object exceptionHandler(Exception e){
        e.printStackTrace();
        return ResponseUtil.serious();
    }
}
