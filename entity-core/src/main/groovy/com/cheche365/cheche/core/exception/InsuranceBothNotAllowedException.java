package com.cheche365.cheche.core.exception;

/**
 * Created by mahong on 2015/7/29.
 */
public class InsuranceBothNotAllowedException extends NonFatalBusinessException  {

    public InsuranceBothNotAllowedException(String msg, Object errorObject) {
        super(Code.INSURANCE_BOTH_NOT_ALLOWED, msg, errorObject);
    }

}
