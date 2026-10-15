package org.linlinjava.litemall.core.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.springframework.core.annotation.Order;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.util.Set;

/**
 * 全局异常处理器
 *
 * <p>统一拦截 Controller 层抛出的异常，避免异常堆栈直接暴露给前端。
 * 所有被捕获的异常都会被转换成 {@link ResponseUtil} 约定的统一 JSON 结构
 * （错误码 + 提示消息），同时打印错误日志便于后端排查。</p>
 *
 * <p>方法命名统一为 badArgument / serious 两类：参数类问题返回“参数错误”，
 * 其余未预期异常统一返回“系统错误”。</p>
 *
 * @author litemall
 * @since 1.0
 */
@ControllerAdvice
@Order
public class GlobalExceptionHandler {

    private Log logger = LogFactory.getLog(GlobalExceptionHandler.class);

    /** 非法入参：业务代码主动抛出的 IllegalArgumentException */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public Object badArgumentHandler(IllegalArgumentException e) {
        logger.error(e.getMessage(), e);
        return ResponseUtil.badArgumentValue();
    }

    /** 参数类型不匹配：如预期 Integer 却传入了字符串 */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseBody
    public Object badArgumentHandler(MethodArgumentTypeMismatchException e) {
        logger.error(e.getMessage(), e);
        return ResponseUtil.badArgumentValue();
    }

    /** 缺少必填的请求参数 */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    public Object badArgumentHandler(MissingServletRequestParameterException e) {
        logger.error(e.getMessage(), e);
        return ResponseUtil.badArgumentValue();
    }

    /** 请求体不可读：JSON 格式错误导致无法反序列化 */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public Object badArgumentHandler(HttpMessageNotReadableException e) {
        logger.error(e.getMessage(), e);
        return ResponseUtil.badArgumentValue();
    }

    /** 参数校验异常：提取第一个校验失败字段及其提示信息，返回 402 */
    @ExceptionHandler(ValidationException.class)
    @ResponseBody
    public Object badArgumentHandler(ValidationException e) {
        logger.error(e.getMessage(), e);
        if (e instanceof ConstraintViolationException) {
            ConstraintViolationException exs = (ConstraintViolationException) e;
            Set<ConstraintViolation<?>> violations = exs.getConstraintViolations();
            for (ConstraintViolation<?> item : violations) {
                String message = ((PathImpl) item.getPropertyPath()).getLeafNode().getName() + item.getMessage();
                return ResponseUtil.fail(402, message);
            }
        }
        return ResponseUtil.badArgumentValue();
    }

    /** 兜底处理：所有未被上面匹配的异常，统一按系统错误返回 */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Object seriousHandler(Exception e) {
        logger.error(e.getMessage(), e);
        return ResponseUtil.serious();
    }
}
