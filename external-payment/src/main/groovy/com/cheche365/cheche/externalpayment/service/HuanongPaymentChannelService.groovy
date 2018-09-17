package com.cheche365.cheche.externalpayment.service

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.InsuranceCompany
import com.cheche365.cheche.core.model.PaymentChannel
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.service.ThirdPartyPaymentTemplate
import org.springframework.stereotype.Service

/**
 * Created by wen on 2018/8/6.
 */
@Service
class HuanongPaymentChannelService implements ThirdPartyPaymentTemplate{
    @Override
    boolean acceptable(QuoteRecord quoteRecord) {
       InsuranceCompany.Enum.HN_150000 ==quoteRecord.insuranceCompany
    }

    @Override
    Object prePay(PurchaseOrder purchaseOrder, Channel channel, QuoteRecord quoteRecord) {
        def currentPaymentChannels = PaymentChannel.Enum.ALL.findAll {it.parentId ==  PaymentChannel.Enum.HUANONG_59.id}

        if(!currentPaymentChannels){
            throw new BusinessException(BusinessException.Code.PAY_NOT_ALLOWED,'该渠道不支持在线支付')
        }

        currentPaymentChannels
    }



}
