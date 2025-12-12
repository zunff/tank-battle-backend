package com.zunf.tankbattlebackend.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用返回类
 *
 * @param <T>
 * @author ZunF
 */
@Data
public class BaseResp<T> implements Serializable {

    private int code;

    private T data;

    private String message;

    public BaseResp(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public BaseResp(int code, T data) {
        this(code, data, "");
    }

    public BaseResp(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage());
    }

    /**
     * 成功
     *
     * @param <T>
     * @return
     */
    public static <T> BaseResp<T> success() {
        return new BaseResp<>(0, null, "ok");
    }



    /**
     * 成功
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T> BaseResp<T> success(T data) {
        return new BaseResp<>(0, data, "ok");
    }

    /**
     * 失败
     *
     * @param errorCode
     * @return
     */
    public static BaseResp error(ErrorCode errorCode) {
        return new BaseResp<>(errorCode);
    }

    /**
     * 失败
     *
     * @param code
     * @param message
     * @return
     */
    public static BaseResp error(int code, String message) {
        return new BaseResp(code, null, message);
    }

    /**
     * 失败
     *
     * @param errorCode
     * @return
     */
    public static BaseResp error(ErrorCode errorCode, String message) {
        return new BaseResp(errorCode.getCode(), null, message);
    }
}