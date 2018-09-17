package com.cheche365.cheche.externalapi.api.huanong

import com.cheche365.cheche.core.model.Area
import com.cheche365.cheche.core.model.QuoteRecord
import org.springframework.stereotype.Service

/**
 * Created by wen on 2018/8/23.
 */
@Service
class HuanongTokenAPI extends HuanongAPI{

    def call(QuoteRecord quoteRecord){
        def prefixes = [quoteRecord.channel.apiPartner?.code, quoteRecord.insuranceCompany.id, quoteRecord.area.id].toArray()
        super.call(
            [
                UserCode    : envPropertyNew('userCode',prefixes),
                ComCode     : envPropertyNew('comCode',prefixes),
                AgentCode   : envPropertyNew('agentCode',prefixes),
                AgreementNo : envPropertyNew('agreementNo',prefixes),
                producerCode: ''
            ]
        )
    }

    @Override
    String transCode() {
        'LOGIN'
    }
}
