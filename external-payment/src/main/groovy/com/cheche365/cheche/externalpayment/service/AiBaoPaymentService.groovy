package com.cheche365.cheche.externalpayment.service

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.Channel
import static com.cheche365.cheche.core.model.QuoteSource.Enum.API_4
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.PICC_10000
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.service.QuoteRecordCacheService
import com.cheche365.cheche.core.service.ThirdPartyPaymentTemplate
import com.cheche365.cheche.core.util.FormUtil
import com.cheche365.cheche.externalapi.model.AiBaoProposal
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Created by wen on 2018/9/10.
 */
@Service
class AiBaoPaymentService implements ThirdPartyPaymentTemplate{

    @Autowired
    QuoteRecordCacheService cacheService

    @Override
    boolean acceptable(QuoteRecord quoteRecord) {
        return API_4 == quoteRecord.type && PICC_10000 == quoteRecord.insuranceCompany
    }

    @Override
    Object prePay(PurchaseOrder purchaseOrder, Channel channel, QuoteRecord quoteRecord) {
        AiBaoProposal proposal = new AiBaoProposal(purchaseOrder,cacheService)
        if(!proposal?.payUrl()){
            throw new BusinessException(BusinessException.Code.PAY_NOT_ALLOWED,'支付申请失败')
        }
        return FormUtil.buildForm(proposal?.payUrl(),null,'POST')
    }
}
