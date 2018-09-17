package com.cheche365.cheche.externalapi.model.huanong

/**
 * Created by wen on 2018/8/7.
 */
class HuanongPaymentResponse {

    Map response

    def HuanongPaymentResponse(Map param){
        response = param
    }

    def head(){
        response?.head
    }

    def isSuccess(){
        head()?.responseCode == '#0000' ?: false
    }

    def responseMsg(){
        head()?.responseMsg
    }

    def token(){
        response?.token
    }

    def payUrl(){
        response?.payUrl
    }

    def payLmg(){
        response?.payLmg
    }

    Double totalAmount(){
        response?.totalAmount ? Double.valueOf(response.totalAmount) : null
    }



}
