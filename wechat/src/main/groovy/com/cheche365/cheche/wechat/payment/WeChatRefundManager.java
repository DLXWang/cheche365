package com.cheche365.cheche.wechat.payment;

import com.cheche365.cheche.core.WechatConstant;
import com.cheche365.cheche.core.exception.ConcurrentApiCallLockException;
import com.cheche365.cheche.core.message.RedisPublisher;
import com.cheche365.cheche.core.model.Payment;
import com.cheche365.cheche.core.model.PaymentChannel;
import com.cheche365.cheche.core.model.PaymentStatus;
import com.cheche365.cheche.core.model.PurchaseOrder;
import com.cheche365.cheche.core.repository.PaymentRepository;
import com.cheche365.cheche.core.repository.PurchaseOrderRepository;
import com.cheche365.cheche.core.service.PaymentSerialNumberGenerator;
import com.cheche365.cheche.core.service.PurchaseOrderRefundStatusHandler;
import com.cheche365.cheche.core.service.UnifiedRefundHandler;
import com.cheche365.cheche.wechat.MessageSender;
import com.cheche365.cheche.wechat.message.RefundRequest;
import com.cheche365.cheche.wechat.message.RefundResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cheche on 3/3/16.
 */
@Service
public class WeChatRefundManager extends UnifiedRefundHandler{

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentSerialNumberGenerator paymentSerialNumberGenerator;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private PurchaseOrderRefundStatusHandler refundStatusHandler;

    @Autowired
    private RedisPublisher redisPublisher;

    @Autowired
    protected MessageSender messageSender;
    private Logger logger = LoggerFactory.getLogger(WeChatRefundManager.class);
    private static final String REFUND_URL = "https://api.mch.weixin.qq.com/secapi/pay/refund";
    private static final String BALANCEREFUNDACCOUNT = "REFUND_SOURCE_RECHARGE_FUNDS";//余额
    private static final String NOTSETTLEDREFUNDACCOUNT = "REFUND_SOURCE_UNSETTLED_FUNDS";//未结算

    public boolean support(Payment payment){
        return payment.getChannel().equals(PaymentChannel.Enum.WECHAT_4);
    }

    @Transactional
    public Map<Long,Boolean> refund(List<Payment> payments) {
        Map<Long,Boolean> resultMap = new HashMap<Long,Boolean>();
        for(Payment payment:payments){
            resultMap.put(payment.getId(),refund(payment));
        }
        return resultMap;
    }

    @Transactional
    public boolean refund(Payment payment) {
        String mchId = payment.getUpstreamId().getMchId();
        String appId = payment.getUpstreamId().getAppId();
        RefundRequest refundRequest = createRefundRequest(payment, mchId, appId);
        boolean result = false;
        logger.info(name()+"退款mchId：{} 支付单id:{}",mchId,payment.getId());
        String refundResponseString = messageSender.refund(REFUND_URL, refundRequest, mchId);
        RefundResponse refundResponse = RefundResponse.parseMessage(refundResponseString);
        String returnCode = refundResponse.getReturn_code();
        if (WechatConstant.WECHAT_SUCCESS.equals(returnCode)) {
            logger.info(name()+"退款接口调用成功 OutTradeNo{}",payment.getOutTradeNo());
            if(WechatConstant.WECHAT_SUCCESS.equals(refundResponse.getResult())){
                logger.info(name() + "退款接口调用成功验证成功 OutTradeNo{}", payment.getOutTradeNo());
                payment.setStatus(PaymentStatus.Enum.PAYMENTSUCCESS_2);
                PurchaseOrder purchaseOrder = payment.getPurchaseOrder();
                result =  true;
            }else {
                payment.setStatus(PaymentStatus.Enum.CANCEL_4);
                logger.info(name()+"退款接口调用成功验证失败 OutTradeNo{}",payment.getOutTradeNo());
            }
        }else{
            payment.setStatus(PaymentStatus.Enum.CANCEL_4);
            logger.info(name()+"退款接口调用失败 OutTradeNo:{} refundResponseString:{}",payment.getOutTradeNo(),refundResponseString);
        }
        paymentRepository.save(payment);
        List<Payment> childPayments = paymentRepository.findByPurchaseOrder(payment.getPurchaseOrder());
        payment = refundStatusHandler.changeOrderStatus(childPayments, payment.getPurchaseOrderAmend(), payment.getPurchaseOrder());
        if (null != payment) {
            try {
                redisPublisher.publish(payment);
            }catch(ConcurrentApiCallLockException e){
                logger.info("该支付单已经被锁定");
            }
        }
        return result;
    }

    @Override
    public Map<Long, Boolean> callPlatform(String orderNo, Map<Long, Map> sendMap) {
        return null;
    }

    @Override
    public Map<String, String> createMap(Payment payment) {
        return null;
    }

    @Override
    public String name() {
        return "微信支付";
    }

    private RefundRequest createRefundRequest(Payment payment,String mchId,String appId) {
        String transactionId = payment.getUpstreamId().getThirdpartyPaymentNo();
        RefundRequest refundRequest = new RefundRequest();
        refundRequest.setMch_id(payment.getUpstreamId().getMchId());
        refundRequest.setAppid(payment.getUpstreamId().getAppId());
        refundRequest.setNonce_str(RandomStringUtils.randomAlphanumeric(32).toUpperCase());
        refundRequest.setTransaction_id(transactionId);
        refundRequest.setOut_refund_no(paymentSerialNumberGenerator.next(payment));
        refundRequest.setRefund_account(WeChatRefundManager.BALANCEREFUNDACCOUNT);
        refundRequest.setTotal_fee((long) (payment.getUpstreamId().getAmount() * 100l));
        refundRequest.setRefund_fee((long)(payment.getAmount()*100l));
        refundRequest.setOp_user_id(refundRequest.getMch_id());
        return refundRequest;
    }

    private int formatAmount2Fen(String amount) {
        String fen = "";
        NumberFormat format = NumberFormat.getInstance();
        try {
            Number number = format.parse(amount);
            double temp = number.doubleValue() * 100.0;
            format.setGroupingUsed(false);
            format.setMaximumFractionDigits(0);
            fen = format.format(temp);
        } catch (ParseException e) {
            e.printStackTrace();
            logger.error("formatAmount2Fen fail.", ExceptionUtils.getRootCauseMessage(e));
        }
        return Integer.valueOf(fen);
    }

}
