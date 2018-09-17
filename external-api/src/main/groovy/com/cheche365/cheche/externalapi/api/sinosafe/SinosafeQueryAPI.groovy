package com.cheche365.cheche.externalapi.api.sinosafe

import com.cheche365.cheche.core.model.CompulsoryInsurance
import com.cheche365.cheche.core.model.Insurance
import com.cheche365.cheche.externalapi.model.sinosafe.SinosafeQueryResponse
import org.springframework.stereotype.Service

/**
 * Created by zhengwei on 09/02/2018.
 */

@Service
class SinosafeQueryAPI extends SinosafeAPI {


    SinosafeQueryResponse call(Insurance insurance, CompulsoryInsurance compulsoryInsurance) {

        def appInfo = []
        if(insurance){
            appInfo  << [PLY_APP_NO: insurance.proposalNo]
        }
        if(compulsoryInsurance){
            appInfo  << [PLY_APP_NO: compulsoryInsurance.proposalNo]
        }

        return super.call(
            [
                APP_INFO:  appInfo
            ]
        ).with {
            new SinosafeQueryResponse(it)
        }
    }


    @Override
    String transCode() {
        '100007'
    }
}
