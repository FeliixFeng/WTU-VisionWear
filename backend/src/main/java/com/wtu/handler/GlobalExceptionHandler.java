package com.wtu.handler;

import com.wtu.exception.AuthException;
import com.wtu.exception.BaseException;
import com.wtu.exception.BusinessException;
import com.wtu.exception.ValidationException;
import com.wtu.result.Result;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
@Order(1)
@Slf4j
public class GlobalExceptionHandler {

    // 处理业务异常
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        return Result.error(e.getMessage());
    }

    // 处理认证异常
    @ExceptionHandler(AuthException.class)
    public Result<?> handleAuthException(AuthException e) {
        log.warn("认证异常: {}", e.getMessage());
        return Result.error(e.getMessage());
    }

    // 处理JWT过期异常
    @ExceptionHandler(ExpiredJwtException.class)
    public Result<?> handleExpiredJwtException(ExpiredJwtException e) {
        log.warn("JWT过期: {}", e.getMessage());
        return Result.error(AuthException.JWT_TOKEN_EXPIRED);
    }

    // 处理JWT签名异常
    @ExceptionHandler(SignatureException.class)
    public Result<?> handleSignatureException(SignatureException e) {
        log.warn("JWT签名验证失败: {}", e.getMessage());
        return Result.error(AuthException.JWT_SIGNATURE_FAILED);
    }

    // 处理JWT格式异常
    @ExceptionHandler({MalformedJwtException.class, UnsupportedJwtException.class})
    public Result<?> handleJwtFormatException(Exception e) {
        log.warn("JWT格式不正确: {}", e.getMessage());
        return Result.error(AuthException.JWT_ILLEGAL_ARGUMENT);
    }

    // 处理验证异常
    @ExceptionHandler(ValidationException.class)
    public Result<?> handleValidationException(ValidationException e) {
        log.warn("验证异常: {}", e.getMessage());
        return Result.error(e.getMessage());
    }

    // 处理参数校验异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldError().getDefaultMessage();
        log.warn("参数校验失败: {}", message);
        return Result.error(message);
    }

    // 处理请求参数缺失异常
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Result<?> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.warn("参数缺失: {}", e.getMessage());
        return Result.error("缺少必要参数: " + e.getParameterName());
    }

    // 处理请求参数类型不匹配异常
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Result<?> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.warn("参数类型不匹配: {}", e.getMessage());
        return Result.error("参数类型不匹配: " + e.getName());
    }

    // 处理请求体解析异常
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.warn("请求体解析失败: {}", e.getMessage());
        return Result.error("请求体格式错误");
    }

    // 处理资源未找到异常
    @ExceptionHandler(NoHandlerFoundException.class)
    public Result<?> handleNoHandlerFoundException(NoHandlerFoundException e) {
        log.warn("接口 [{}] 不存在", e.getRequestURL());
        return Result.error("接口 [" + e.getRequestURL() + "] 不存在");
    }

    // 处理接口方法不允许异常
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result<?> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.warn("不支持 [{}] 请求方法", e.getMethod());
        return Result.error("不支持 [" + e.getMethod() + "] 请求方法");
    }

    // 处理所有未知异常
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        log.error("系统异常: {}", e.getMessage(), e);
        return Result.error("系统繁忙，请稍后再试");
    }
}