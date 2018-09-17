package com.cheche365.cheche.core.service.callback;

import com.cheche365.cheche.core.exception.BusinessException;
import com.cheche365.cheche.core.message.RedisPublisher;
import com.cheche365.cheche.core.model.CompulsoryInsurance;
import com.cheche365.cheche.core.model.Insurance;
import com.cheche365.cheche.core.model.MoApplicationLog;
import com.cheche365.cheche.core.model.Payment;
import com.cheche365.cheche.core.model.PaymentChannel;
import com.cheche365.cheche.core.model.PaymentStatus;
import com.cheche365.cheche.core.model.PurchaseOrder;
import com.cheche365.cheche.core.model.QuoteRecord;
import com.cheche365.cheche.core.repository.CompulsoryInsuranceRepository;
import com.cheche365.cheche.core.repository.InsuranceRepository;
import com.cheche365.cheche.core.repository.PaymentRepository;
import com.cheche365.cheche.core.repository.QuoteRecordRepository;
import com.cheche365.cheche.core.service.DoubleDBService;
import com.cheche365.cheche.core.service.QuoteConfigService;
import com.cheche365.cheche.core.service.sms.ConditionTriggerHandler;
import com.cheche365.cheche.core.service.sms.ConditionTriggerUtil;
import com.cheche365.cheche.core.util.CacheUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpSession;
import java.util.Map;

import static com.cheche365.cheche.core.model.PaymentType.Enum.REFUND_TYPES;

public abstract class AbstractCallBackService{

    private final Logger logger = LoggerFactory.getLogger(AbstractCallBackService.class);

    @Autowired
    private DoubleDBService mongoDBService;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private ConditionTriggerHandler conditionTriggerHandler;
    @Autowired
    private QuoteRecordRepository quoteRecordRepository;
    @Autowired
    private RedisPublisher redisPublisher;
    @Autowired
    private QuoteConfigService quoteConfigService;
    @Autowired
    private IBizHandler paymentBizHandler;
    @Autowired
    private IBizHandler refundBizHandler;
    @Autowired
    protected CompulsoryInsuranceRepository ciRepo;
    @Autowired
    protected InsuranceRepository iRepo;
    @Autowired(required = false)
    HttpSession session;

    protected String execute(Map<String, String> params,CallBackType callBackType, PaymentChannel paymentChannel, CallBackProcessorChain processorChain) throws Exception {

        //获取订单编号
        String outTradeNo = getOutTradeNo(params);
        logger.debug("out trade no:{}",outTradeNo);
        //需要前置处理的业务逻辑
        logger.debug("begin to run processor chain!");
        if(processorChain!=null && !processorChain.doProcessor(outTradeNo,params)){
            logger.debug("end to run current call back! redirect to call other processor");
            return null;
        }


        Payment payment = getPayment(params);
        if(payment == null){
            throw new BusinessException(BusinessException.Code.OBJECT_NOT_EXIST,"无效的outTradeNo");
        }
        if(paymentChannel == null){
            paymentChannel = payment.getChannel();
        }
        log(payment,paymentChannel,callBackType);

        //支付状态验证
        if(PaymentStatus.Enum.PAYMENTSUCCESS_2.equals(payment.getStatus())){
            logger.info("支付异步回调已经处理完成:{}", outTradeNo);
            return result(true);
        }

        //验证签名
        if(!verify(params)){
            logger.error("verify params failed!!!", CacheUtil.doJacksonSerialize(params));
            throw new BusinessException(BusinessException.Code.EXTERNAL_SERVICE_ERROR,"验签失败");
        }else{
            logger.debug("verify params success!!!");
        }

        updatePayment(payment,params,paymentChannel);

        //处理业务逻辑
        boolean isSuccess = false;
        if(CallBackType.PAYMENT == callBackType){
            isSuccess = paymentBizHandler.handler(payment,isSuccess(params));
        }else if(CallBackType.REFUNDS == callBackType){
            isSuccess = refundBizHandler.handler(payment,isSuccess(params));
        }

        if(isSuccess(params) && isSuccess){
            publish(payment);
            if(isOrder()){
                PurchaseOrder order=payment.getPurchaseOrder();
                Insurance insurance = iRepo.findByQuoteRecordId(order.getObjId());
                CompulsoryInsurance compulsoryInsurance = ciRepo.findByQuoteRecordId(order.getObjId());
                order(order, insurance, compulsoryInsurance, payment,params);
            }
        }
        return result(isSuccess);
    }



    /**
     * 回调消息验签
     */
    public abstract boolean verify(Map<String, String> params);

    /**
     * 获取交易号
     * @param params
     * @return
     */
    public abstract String getOutTradeNo(Map<String, String> params);

    /**
     * 支付第三方交易号
     * @param params
     * @return
     */
    public abstract String getThirdpartyTradeNo(Map<String, String> params);

    /**
     * 聚合支付流水号
     * @param params
     * @return
     */
    public String getItpNo(Map<String, String> params){
           return null;
    }

    /**
     * 是否成功   不同支付类型判断规则不一样
     * @return
     */
    public abstract boolean isSuccess(Map<String, String> params);

    /**
     * 返回数据格式   不同支付类型返回数据格式不一样
     * @return
     */
    public abstract String result(boolean isSuccess);

    /**
     * 更新payment
     * @return
     */
    public void updatePayment(Payment payment,Map<String, String> params,PaymentChannel paymentChannel){
        payment.setItpNo(getItpNo(params));
        payment.setThirdpartyPaymentNo(getThirdpartyTradeNo(params));
        payment.setChannel(paymentChannel);
    }
    /**
     * 获取payment
     */
    public Payment getPayment(Map<String, String> params){
        String outTradeNo =getOutTradeNo(params);
        return paymentRepository.findByOutTradeNo(outTradeNo);
    }

    /**
     * 是否需要承保
     * */
    public boolean isOrder(){
        return false;
    }

    /**
     * 承保
     * */
    public void order(PurchaseOrder order, Insurance insurance, CompulsoryInsurance compulsoryInsurance, Payment payment,Map params){
    }

    /**
     * 发送消息
     */
    public void publish(Payment payment){
        logger.info("begin to publish message", CacheUtil.doJacksonSerialize(payment));
        PurchaseOrder purchaseOrder = payment.getPurchaseOrder();
        QuoteRecord quoteRecord = quoteRecordRepository.findOne(purchaseOrder.getObjId());
        if(!REFUND_TYPES.contains(payment.getPaymentType())){
            ConditionTriggerUtil.sendPaymentSuccessMessage(conditionTriggerHandler, quoteRecord, purchaseOrder);
        }
        redisPublisher.publish(payment);
    }

    /**
     * 记录日志
     */
    public void log(Payment payment,PaymentChannel paymentChannel,CallBackType callBackType) {
        logger.info("begin to add application log:{}", payment.getOutTradeNo());

        String message = String.format("支付异步回调处理日志:订单号[%s],回调类型:[%s],支付渠道:[%s]", payment.getOutTradeNo(),callBackType.toString(),paymentChannel.getName());
        MoApplicationLog log = MoApplicationLog.applicationLogByPurchaseOrder(payment.getPurchaseOrder());
        log.setLogMessage(message);

        mongoDBService.saveApplicationLog(log);
    }

}
