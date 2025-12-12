package com.zunf.tankbattlebackend.handler;


import com.zunf.tankbattlebackend.common.BaseResp;
import com.zunf.tankbattlebackend.common.BusinessException;
import com.zunf.tankbattlebackend.common.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 *
 * @author ZunF
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public BaseResp<?> businessExceptionHandler(BusinessException e) {
        log.error("BusinessException", e);
        return BaseResp.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResp<?> runtimeExceptionHandler(RuntimeException e) {
        log.error("RuntimeException", e);
        return BaseResp.error(ErrorCode.SYSTEM_ERROR, "系统错误");
    }
}