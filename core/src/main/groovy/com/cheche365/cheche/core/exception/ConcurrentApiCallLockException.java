package com.cheche365.cheche.core.exception;

/**
 * Created by tongsong on 2017/1/4 0004.
 * 并发注解发生错误抛出该异常
 */
public class ConcurrentApiCallLockException extends BusinessException {

    public ConcurrentApiCallLockException(Code code, String msg) {
        super(code, msg);
    }
}
