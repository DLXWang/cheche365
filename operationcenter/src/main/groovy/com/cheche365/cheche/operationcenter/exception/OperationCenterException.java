package com.cheche365.cheche.operationcenter.exception;

/**
 * 订单管理异常
 * Created by sunhuazhong on 2015/4/28.
 */
public class OperationCenterException extends RuntimeException {
    private String code;
    private String message;

    public OperationCenterException() {}

    public OperationCenterException(String code) {
        this.code = code;
    }

    public OperationCenterException(String code, String message) {
        super(code);
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
