package com.cheche365.cheche.admin.exception;

/**
 * 操作不被容许的异常
 * Created by wangfei on 2016/5/31.
 */
public class OperationNotAllowedException extends RuntimeException {

    public OperationNotAllowedException(String msg) {
        super(msg);
    }
}
