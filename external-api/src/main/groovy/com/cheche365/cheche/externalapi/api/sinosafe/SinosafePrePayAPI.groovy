package com.cheche365.cheche.externalapi.api.sinosafe

import com.cheche365.cheche.core.service.OrderRelatedService.OrderRelated
import com.cheche365.cheche.externalapi.model.sinosafe.SinosafePrePayResponse
import org.springframework.stereotype.Service


/**
 * Created by zhengwei on 09/02/2018.
 */

@Service
class SinosafePrePayAPI extends SinosafeAPI {


    SinosafePrePayResponse call(OrderRelated or, String verifyCode, String callBackUrl) {

        def APP_INFO=[]
        if(or.insurance){
            APP_INFO << [
                APPLICANTNO    : or.insurance.proposalNo,
                AMOUNT         : or.insurance.premium * 100,  //华安的单位=分d
                APPLICANTNAME  : or.insurance.applicantName,
                STARTDATE      : or.insurance.effectiveDate,
                DEPARTMENTCODE : or.po.area.id
            ]
        }
        if(or.ci){
            APP_INFO << [
                APPLICANTNO    : or.ci.proposalNo,
                AMOUNT         : or.ci.compulsoryPremium * 100,
                APPLICANTNAME  : or.ci.applicantName,
                STARTDATE      : or.ci.effectiveDate,
                DEPARTMENTCODE : or.po.area.id
            ]
        }

        def base = [APP_INFO:APP_INFO, BACKURL: callBackUrl << or.po.orderNo]

        if(verifyCode){
            base << [TEL_VERIFY_CODE: verifyCode] //给客户的手机验证码(北京必传)
        }

        super.call([
            BASE: base
        ]).with {
            new SinosafePrePayResponse(it)
        }
    }

    @Override
    String transCode() {
        '100010'
    }
}
