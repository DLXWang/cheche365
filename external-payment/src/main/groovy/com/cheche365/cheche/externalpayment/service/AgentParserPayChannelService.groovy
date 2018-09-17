package com.cheche365.cheche.externalpayment.service

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.AttributeType
import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.Payment
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.model.QuoteSource
import com.cheche365.cheche.core.repository.CompulsoryInsuranceRepository
import com.cheche365.cheche.core.repository.InsuranceRepository
import com.cheche365.cheche.core.repository.PaymentRepository
import com.cheche365.cheche.core.service.IThirdPartyPaymentService
import com.cheche365.cheche.core.service.OrderAttributeService
import com.cheche365.cheche.core.service.QuoteRecordCacheService
import com.cheche365.cheche.core.service.ThirdPartyPaymentTemplate
import com.cheche365.cheche.core.util.MockUrlUtil
import com.cheche365.cheche.externalpayment.handler.QrUploadHandler
import org.apache.commons.codec.binary.Base64
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import javax.servlet.http.HttpSession

import static com.cheche365.cheche.core.service.QuoteRecordCacheService.persistQRParamHashKey

/**
 * Created by Administrator on 2018/3/6.
 */
@Service
class AgentParserPayChannelService implements ThirdPartyPaymentTemplate {

    private Logger logger = LoggerFactory.getLogger(AgentParserPayChannelService.class);

    @Autowired
    AgentParserPaymentService paymentService

    @Override
    boolean acceptable(QuoteRecord quoteRecord) {
        return QuoteSource.Enum.AGENTPARSER_9 == quoteRecord.getType()
    }

    @Override
    Object prePay(PurchaseOrder purchaseOrder, Channel channel, QuoteRecord quoteRecord) {

        IThirdPartyPaymentService thirdPartyPaymentService = paymentService.findService(quoteRecord)

        def paymentParams = paymentService.buildPaymentParams(quoteRecord,purchaseOrder)
        def paymentChannels = thirdPartyPaymentService.getPaymentChannels(paymentParams[0],paymentParams[1])

        logger.debug("小鳄鱼返回的支付方式:${paymentChannels},orderNo:${purchaseOrder.orderNo}")

        if(!paymentChannels?.newPaymentChannels?.channels){
            throw new BusinessException(BusinessException.Code.PAY_NOT_ALLOWED, "${quoteRecord.insuranceCompany.name}未匹配到支付方式")
        }

        paymentChannels.newPaymentChannels.channels
    }

}
