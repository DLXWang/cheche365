package com.cheche365.cheche.externalpayment.service

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.message.RedisPublisher
import com.cheche365.cheche.core.model.OrderSourceType
import com.cheche365.cheche.core.model.Payment
import com.cheche365.cheche.core.model.PaymentChannel
import com.cheche365.cheche.core.model.PaymentStatus
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.repository.PaymentRepository
import com.cheche365.cheche.core.repository.PurchaseOrderRepository
import com.cheche365.cheche.core.service.callback.AbstractCallBackService
import com.cheche365.cheche.core.service.callback.CallBackType
import com.cheche365.cheche.core.service.callback.ICallBackService
import groovy.json.JsonSlurper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import javax.servlet.http.HttpServletRequest

@Service
class BaoxianCallBackService extends AbstractCallBackService implements ICallBackService {

    private static final String SUCCESS = "success"

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository
    @Autowired
    private PaymentRepository paymentRepository
    @Autowired
    private RedisPublisher redisPublisher

    @Override
    Payment getPayment(Map<String, String> params){
        String outTradeNo = getOutTradeNo(params);
        PurchaseOrder po = purchaseOrderRepository.findByOrderSourceId(outTradeNo, OrderSourceType.Enum.PLANTFORM_BX_5)[0]
        if(!po){
            throw new BusinessException(BusinessException.Code.OBJECT_NOT_EXIST, '订单不存在')
        }

        List<Payment> payments = paymentRepository.findByPurchaseOrder(po)
        List<Payment> notPayment = payments.findAll {it.status==PaymentStatus.Enum.NOTPAYMENT_1}
        if(notPayment.size()==0){
            throw new BusinessException(BusinessException.Code.OBJECT_NOT_EXIST, '没有未支付订单')
        }
        if(notPayment.size()>1){
            throw new BusinessException(BusinessException.Code.OPERATION_NOT_ALLOWED, '存在多笔未支付订单')
        }
        notPayment[0]
    }
    @Override
    void publish(Payment payment){
        redisPublisher.publish(payment)
    }
    @Override
    boolean verify(Map<String, String> params) {
        return true
    }

    @Override
    String getOutTradeNo(Map<String, String> params) {
        return params.taskId
    }

    @Override
    String getThirdpartyTradeNo(Map<String, String> params) {
        return null
    }

    @Override
    boolean isSuccess(Map<String, String> params) {
        return "0".equals(params.code)
    }

    @Override
    String result(boolean isSuccess) {
        return SUCCESS
    }

    @Override
    String callBack(HttpServletRequest request, CallBackType callBackType) {
        return super.execute(getAllRequestParam(request),callBackType,PaymentChannel.Enum.BAOXIAN_PAY_16,null)
    }

    @Override
    boolean support(PaymentChannel pc) {
        return pc == PaymentChannel.Enum.BAOXIAN_PAY_16
    }

    private Map<String, String> getAllRequestParam(final HttpServletRequest request) {
        Map<String, String> res = new HashMap<>()
        BufferedReader  reader = new BufferedReader(new InputStreamReader(request.getInputStream(), "utf-8"))
        String line = ""
        StringBuilder sb = new StringBuilder()
        while ((line = reader.readLine()) != null)
        {
            sb.append(line)
        }
        new JsonSlurper().parseText(sb.toString())
    }
}
