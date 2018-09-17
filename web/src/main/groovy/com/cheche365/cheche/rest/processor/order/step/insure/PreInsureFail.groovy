package com.cheche365.cheche.rest.processor.order.step.insure

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.LogType
import com.cheche365.cheche.core.model.MoApplicationLog
import com.cheche365.cheche.core.model.OrderStatus
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.model.QuoteSource
import com.cheche365.cheche.core.service.QuoteConfigService
import com.cheche365.cheche.rest.processor.order.step.TPlaceOrderStep
import groovy.util.logging.Slf4j
import org.apache.commons.lang3.exception.ExceptionUtils
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.core.exception.BusinessException.Code.INTERNAL_SERVICE_ERROR
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.HN_150000
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.HN_150000
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.SINOSAFE_205000
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.ZHONGAN_50000
import static com.cheche365.cheche.rest.processor.quote.QuoteExceptionHandler.checkKnownReasonError
import static com.cheche365.cheche.rest.processor.quote.QuoteExceptionHandler.knownReasonError

/**
 * Created by zhengwei on 20/03/2018.
 * 核保失败前处理步骤
 */

@Component
@Slf4j
class PreInsureFail implements TPlaceOrderStep {

    @Override
    Object run(Object context) {
        Exception e = context.insureFailException
        PurchaseOrder order = context.order

        log.debug("There is an exception during insure process: \n" + ExceptionUtils.getStackTrace(e))
        saveLog(context)
        if (OrderStatus.Enum.PENDING_PAYMENT_1 == order.status) {//防止已支付订单回退到核保失败状态
            order.status = OrderStatus.Enum.INSURE_FAILURE_7
        }

        return getContinueFSRV(dispatch(context))
    }

    static saveLog(Object context) {
        MoApplicationLog appLog = MoApplicationLog.applicationLogByPurchaseOrder(context.order, LogType.Enum.INSURE_FAILURE_1)
        appLog.setLogMessage(ExceptionUtils.getStackTrace(context.insureFailException.with {
            checkKnownReasonError(it) ? knownReasonError(null, it).with { _0, _1, status, message ->
                new BusinessException(INTERNAL_SERVICE_ERROR, status, message, it)
            } : it
        }))
        context.logRepository.save(appLog)
    }

   static dispatch(Object context) {
       QuoteConfigService quoteConfigService = context.quoteConfigService
       QuoteRecord quoteRecord = context.quoteRecord
       Exception e = context.insureFailException

       if(!(e instanceof BusinessException)) {
           return 'NON_MATCH'
       }

       if(quoteConfigService.isBaoXian(quoteRecord.channel, quoteRecord.area, quoteRecord.insuranceCompany)) {
           return QuoteSource.Enum.PLANTFORM_BX_6
       }
       if(ZHONGAN_50000==quoteRecord.insuranceCompany){
           return ZHONGAN_50000
       }
       if(SINOSAFE_205000 == quoteRecord.insuranceCompany){
           return SINOSAFE_205000
       }
       if(HN_150000 == quoteRecord.insuranceCompany){
           return  HN_150000
       }
       if(quoteConfigService.isBotpy(quoteRecord)){
           return QuoteSource.Enum.PLATFORM_BOTPY_11
       } else {
           return 'NON_MATCH'
       }
   }

}
