package com.cheche365.cheche.core.exception;

/**
 * Created by shanxf on 2/28/17.
 * 第三方调用车车api，所用token超时，需要重新获取token外抛
 */
public class TokenTimeOutException extends BusinessException {



    public TokenTimeOutException(String msg, Object errorObject) {
        super(Code.PARTNER_TOKEN_OUT_TIME, -1, msg, null,errorObject);
    }

    public TokenTimeOutException(Code code, String msg, Object errorObject) {
        super(code, -1, msg, null,errorObject);
    }


}
