package com.cheche365.cheche.core.exception;

/**
 * Created by mahong on 2016/5/18.
 */
public class ASAPNotifyException extends BusinessException {

//    private boolean needThrowOut; // 是否继续向外抛

    public ASAPNotifyException(BusinessException cause) {
        super(cause.getCode(), -1, cause.getMessage(), cause);
    }

    public ASAPNotifyException(BusinessException cause, Object errorObject) {
        super(cause.getCode(), -1, cause.getMessage(), cause,errorObject);
    }


}
