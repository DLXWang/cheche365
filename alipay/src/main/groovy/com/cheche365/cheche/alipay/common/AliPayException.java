/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package com.cheche365.cheche.alipay.common;

/**
 * 自定义异常
 * 
 * @author taixu.zqq
 * @version $Id: MyException.java, v 0.1 2014年7月24日 下午6:58:39 taixu.zqq Exp $
 */
public class AliPayException extends Exception{

    private static final long serialVersionUID = -5710488447295073398L;
    
    public AliPayException(){
    }   
    
    public AliPayException(String message) {
        super(message);           
    }
    
    public AliPayException(Throwable throwable){
        super(throwable);
    }
    
    public AliPayException(String message, Throwable throwable){
        super(message, throwable);
    }
}
