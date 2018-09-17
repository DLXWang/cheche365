package com.cheche365.cheche.core.service.callback

enum CallBackType {
    PAYMENT,REFUNDS;

    public static CallBackType getCallBackType(String callBackType){
        values().find {it.toString().equalsIgnoreCase(callBackType)}
    }
}
