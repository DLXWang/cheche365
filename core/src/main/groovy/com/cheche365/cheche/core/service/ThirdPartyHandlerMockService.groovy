package com.cheche365.cheche.core.service

import com.cheche365.cheche.core.model.CompulsoryInsurance
import com.cheche365.cheche.core.model.Insurance
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.QuoteRecord

import static java.lang.Thread.sleep as zzz
import static org.apache.commons.lang3.RandomUtils.nextLong as randomLong

/**
 * 假报价服务
 */
class ThirdPartyHandlerMockService implements IThirdPartyHandlerService {

    @Override
    void quote(QuoteRecord quoteRecord, Map<String, Object> additionalParameters) throws RuntimeException {
        mock additionalParameters
    }

    @Override
    void insure(PurchaseOrder order, Insurance insurance, CompulsoryInsurance compulsoryInsurance, Map<String, Object> additionalParameters) throws RuntimeException {
        mock additionalParameters
    }
    @Override
    void order(PurchaseOrder order, Insurance insurance, CompulsoryInsurance compulsoryInsurance, Map<String, Object> additionalParameters) throws RuntimeException {}

    private static void mock(additionalParameters) {
        def callback = additionalParameters.flowParticipant
        callback?.sendMessage '连接官网（mock）'
        zzz randomLong(1000L, 2000L)
        callback?.sendMessage '报价中...（mock）'
        zzz randomLong(6000L, 10000L)
    }

}
