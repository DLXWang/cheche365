package com.cheche365.cheche.externalapi.model.sinosafe

import com.cheche365.cheche.common.Constants
import com.cheche365.cheche.core.model.CompulsoryInsurance
import com.cheche365.cheche.core.model.Insurance


/**
 * Created by zhengwei on 30/01/2018.
 */
class SinosafeQueryResponse extends SinosafeResponse {

    static final Range APP_NO_KEY_RANGE = 7..10
    static final INSURANCE_POLICY_NO='0302'
    static final COMPULSORY_INSURANCE_POLICY_NO='0301'
    static final SINOSAFE_ORDER_STATUS_SUCCESS = '1'

    SinosafeQueryResponse(Map response) {
        super(response)
    }


    boolean containsInsurance(){
        data().any { isInsuranceAppNo(it.PLY_NO) }
    }

    boolean containsCompulsoryInsurance(){
        data().any { isCompulsoryInsuranceAppNo(it.PLY_NO) }
    }

    Map findInsurance(){
        data().find{ isInsuranceAppNo(it.PLY_NO as String) }
    }

    Map findCompulsoryInsurance() {
        data().find{ isCompulsoryInsuranceAppNo(it.PLY_NO as String) }
    }

    void mergeInsurance(Insurance dbInsurance){
        doMerge(findInsurance(), dbInsurance)
    }

    void mergeCompulsoryInsurance(CompulsoryInsurance dbCompulsoryInsurance){
        doMerge(findCompulsoryInsurance(), dbCompulsoryInsurance)
    }

    static doMerge(Map source, target){
        target.policyNo = source.PLY_NO
        target.updateTime=new Date()
        target.effectiveDate=Constants.get_DATE_FORMAT3().parse(source.INSRNC_BGN_TM as String)
        target.expireDate = Constants.get_DATE_FORMAT3().parse(source.INSRNC_END_TM as String)
    }


    boolean everyBillSuccess(){
        data().every { SINOSAFE_ORDER_STATUS_SUCCESS == it.UDR_MRK}
    }

    List data(){
        response.PACKET.BODY.DATA instanceof List ? response.PACKET.BODY.DATA : [response.PACKET.BODY.DATA]
    }

    static boolean isInsuranceAppNo(String appNo){
        appNo && INSURANCE_POLICY_NO == appNo[APP_NO_KEY_RANGE]
    }

    static boolean isCompulsoryInsuranceAppNo(String appNo){
        appNo && COMPULSORY_INSURANCE_POLICY_NO == appNo[APP_NO_KEY_RANGE]
    }


}
