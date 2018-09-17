package com.cheche365.cheche.externalpayment.service

import com.cheche365.cheche.core.WechatConstant
import com.cheche365.cheche.core.model.LogType
import com.cheche365.cheche.core.model.MoApplicationLog
import com.cheche365.cheche.core.model.Payment
import com.cheche365.cheche.core.model.PaymentChannel
import com.cheche365.cheche.core.repository.PaymentRepository
import com.cheche365.cheche.core.service.PaymentSerialNumberGenerator
import com.cheche365.cheche.core.service.callback.AbstractCallBackService
import com.cheche365.cheche.core.service.callback.CallBackType
import com.cheche365.cheche.core.util.CacheUtil
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import static com.cheche365.cheche.core.model.PaymentChannel.Enum.PING_PLUS_WX_23
import static com.cheche365.cheche.core.model.PaymentType.Enum.PAY_TYPES

/**
 * @Author shanxf
 * @Date 2018/1/3  14:05
 */

@Service
@Slf4j
class PingPlusCallBackService extends AbstractCallBackService{

    @Autowired
    private PaymentRepository paymentRepository

    @Autowired
    private PaymentSerialNumberGenerator paymentSerialNumberGenerator

    @Autowired
    private PingPlusRoyaltyService pingPlusRoyaltyService

    @Override
    Payment getPayment(Map<String, String> params){

        log.info("ping plus call back params:{}",CacheUtil.doJacksonSerialize(params))

        pingPlusRoyaltyService.royalty(params)

        Payment payment

        if(params.isOrderPaySuccess){
            //支付成功回掉 从redis中取paymentId 根据paymentId 查询payment
            Long paymentId = paymentSerialNumberGenerator.getPaymentIdByOutTradeNo(params.outTradeNo)
            log.info("paymentId :{}",paymentId)
            payment = paymentRepository.findOne(paymentId)

        } else if (params.isChargeRefundSuccess) {
            payment = paymentRepository.findFirstByThirdpartyPaymentNo(getThirdpartyTradeNo(params))
        } else{
            //退款成功回掉 直接根据ping++回传的  chargeId  查询payment
            log.info("third party payment no :{}",getThirdpartyTradeNo(params))
            List<Payment> paymentList = paymentRepository.findRefundPaymentByTpn(getThirdpartyTradeNo(params))
            if(paymentList?.size()>1){
                log.info("ping++ 退款回调查到多于一笔未完成退款的 payment,thirdPartyTradeNo:{}",getThirdpartyTradeNo(params))
                saveMal(params)
            }else {
                payment =paymentList?.get(0)
            }
        }
        return payment
    }


    @Override
    boolean verify(Map<String, String> params) {
        return true
    }

    @Override
    boolean isSuccess(Map<String, String> params) {
        return true
    }

    @Override
    String result(boolean isSuccess) {
        return null
    }

    @Override
    String getOutTradeNo(Map<String, String> params) {
        return params.outTradeNo
    }

    @Override
    String getThirdpartyTradeNo(Map<String, String> params) {
        return params.thirdpartyPaymentNo
    }

    String call(Map<String, String> params,CallBackType callBackType){
        super.execute(params,callBackType,null,null)
    }

    @Override
    public void updatePayment(Payment payment, Map<String, String> params, PaymentChannel paymentChannel) {
        //只有支付成功回掉需要更新 payment
        if (params.isOrderPaySuccess) {
            if(params.itpNo){
                payment.setItpNo(params.itpNo)
            }
            def outTradeNo = getOutTradeNo(params)
            if(outTradeNo){
                payment.setOutTradeNo(outTradeNo)
            }
            def thirdPartyPaymentNo = getThirdpartyTradeNo(params)
            if(thirdPartyPaymentNo){
                payment.setThirdpartyPaymentNo(thirdPartyPaymentNo)
            }
            if (PING_PLUS_WX_23 == paymentChannel && PAY_TYPES.contains(payment.getPaymentType())) {
                payment.setAppId(WechatConstant.FANHUA_APP_ID)
                payment.setMchId(WechatConstant.FANHUA_MCH_ID)
            }
        }
    }

    private void saveMal(Map<String, String> params) {
        MoApplicationLog log = new MoApplicationLog()
        log.setLogType(LogType.Enum.ORDER_RELATED_3)
        log.setLogMessage("ping++ 退款回调查到多于一笔未完成退款的 payment,thirdPartyTradeNo:" + getThirdpartyTradeNo(params))
        log.setCreateTime(Calendar.getInstance().getTime())
        log.setInstanceNo(getThirdpartyTradeNo(params))
        log.setObjTable("payment")
        mongoDBService.saveApplicationLog(log)
    }
}
