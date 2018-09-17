package com.cheche365.cheche.core.exception

class OrderingZhonganException extends NonFatalBusinessException{


    OrderingZhonganException(String msg, Object errorObject) {
        super(Code.INSURANCE_BOTH_NOT_ALLOWED, msg, errorObject);
    }
}
