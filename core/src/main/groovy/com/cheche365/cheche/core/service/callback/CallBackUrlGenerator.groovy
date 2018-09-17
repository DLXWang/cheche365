package com.cheche365.cheche.core.service.callback

import com.cheche365.cheche.core.model.PaymentChannel

class CallBackUrlGenerator {

    public static String getUrl(PaymentChannel pc,CallBackType type){
        CallBackConstant.getCallBackUrl() + "/" + pc.name + type.toString().toLowerCase()
    }
}
