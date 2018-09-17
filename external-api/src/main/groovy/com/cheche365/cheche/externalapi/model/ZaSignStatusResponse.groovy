package com.cheche365.cheche.externalapi.model

import com.cheche365.cheche.core.model.IdentityType

/**
 * Created by wen on 2018/5/29.
 */
class ZaSignStatusResponse {

    Map resp

    ZaSignStatusResponse(Map resp){
        this.resp = resp
    }

    boolean isSuccess(){
        resp.result == '0'
    }

    String message(){
        resp.resultMessage
    }

    boolean isUnsigned(){
        resp.policySignStatus == '0'
    }

    boolean isNotUploaded(){
        resp.fileUploadStatus == '0'
    }

    boolean needLink() {
        isSuccess() && (isNotUploaded() || isUnsigned())
    }

    //是否上传图片和签名都已完成
    boolean isFlowFinished() {
        isSuccess() && (!isNotUploaded() && !isUnsigned())
    }

    static IDENTITY_TYPE_TO_ZA_MAPPING = [
        (IdentityType.Enum.IDENTITYCARD)                 : 'I',
        (IdentityType.Enum.PASSPORT)                     : 'P',
        (IdentityType.Enum.OFFICERARD)                   : 'M',
        (IdentityType.Enum.PASSPORT)                     : 'D',
        (IdentityType.Enum.HONGKONG_MACAO_LAISSEZ_PASSER): 'GA',
        (IdentityType.Enum.TAIWAN_LAISSEZ_PASSER)        : 'TB',
        (IdentityType.Enum.UNIFIED_SOCIAL_CREDIT_CODE)   : 'TY'
    ]
}
