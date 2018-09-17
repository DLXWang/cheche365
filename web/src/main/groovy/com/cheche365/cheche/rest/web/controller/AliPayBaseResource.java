package com.cheche365.cheche.rest.web.controller;

import com.cheche365.cheche.alipay.AliPayHandler;
import com.cheche365.cheche.alipay.AlipayService;
import com.cheche365.cheche.alipay.util.AliPayConstant;
import com.cheche365.cheche.alipay.util.AlipayCore;
import com.cheche365.cheche.alipay.util.AlipayNotify;
import com.cheche365.cheche.core.exception.ConcurrentApiCallLockException;
import com.cheche365.cheche.core.message.RedisPublisher;
import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.model.OrderStatus;
import com.cheche365.cheche.core.model.Payment;
import com.cheche365.cheche.core.model.PaymentChannel;
import com.cheche365.cheche.core.model.PaymentStatus;
import com.cheche365.cheche.core.model.PaymentType;
import com.cheche365.cheche.core.model.PurchaseOrder;
import com.cheche365.cheche.core.model.QuoteRecord;
import com.cheche365.cheche.core.repository.PaymentRepository;
import com.cheche365.cheche.core.repository.PurchaseOrderRepository;
import com.cheche365.cheche.core.repository.QuoteRecordRepository;
import com.cheche365.cheche.core.service.PurchaseOrderRefundStatusHandler;
import com.cheche365.cheche.core.service.PurchaseOrderService;
import com.cheche365.cheche.core.service.QuoteConfigService;
import com.cheche365.cheche.core.service.sms.ConditionTriggerHandler;
import com.cheche365.cheche.core.service.sms.ConditionTriggerUtil;
import com.cheche365.cheche.core.util.RuntimeUtil;
import com.cheche365.cheche.internal.integration.answern.api.PaymentCallback;
import com.cheche365.cheche.internal.integration.na.NACallbackService;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chenxiaozhe on 15-8-25.
 */
@Controller
@RequestMapping("/web/alipay")
public class AliPayBaseResource {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    protected AlipayCore alipayCore;

    @Autowired
    protected AlipayNotify alipayNotify;

    @Autowired
    protected AlipayService aliPayHandlerBuilder;

    @Autowired
    protected PurchaseOrderService purchaseOrderService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private ConditionTriggerHandler conditionTriggerHandler;

    @Autowired
    private QuoteRecordRepository quoteRecordRepository;

    @Autowired
    private PurchaseOrderRefundStatusHandler refundStatusHandler;

    @Autowired
    private RedisPublisher redisPublisher;

    @Autowired
    private QuoteConfigService quoteConfigService;

    private static String NOTIFY_SUCCESS = "success";

    private static String NOTIFY_FAIL = "fail";

    private static String NOTIFY_REFUND_SUCCESS = "SUCCESS";

    public void refundNotify(HttpServletRequest request, HttpServletResponse response, Channel clientType) {
        String batchNo = alipayCore.getParameter(request, "batch_no");
        Payment payment = paymentRepository.findByOutTradeNo(batchNo);
        PurchaseOrder purchaseOrder = payment.getPurchaseOrder();
        String resultDetails = alipayCore.getParameter(request, "result_details");
        boolean paymentSuccess = true;
        String debugWord = null;
        String[] result = resultDetails.split("\\^");
        Map<String, String> params = alipayCore.convertRequestParamToMap(request, AliPayConstant.INPUT_CHARSET, false);
        boolean verifyResult = alipayNotify.verify(params,batchNo);
        if(responseToAli(payment, result[2], response)){  //放在verify之后是为了重用verify方法里的log功能，后续会优化
            return;
        }
        try {
            if (verifyResult&&NOTIFY_REFUND_SUCCESS.equals(result[2])) {
                logger.info("支付宝退款接口异步回调验证成功 OutTradeNo:{}", payment.getOutTradeNo());
            }else{
                logger.info("支付宝退款接口异步回调验证失败 OutTradeNo:{} verifyResult:{}", payment.getOutTradeNo(), verifyResult);
                paymentSuccess = false;
            }
            modifyStatus(payment, paymentSuccess ? PaymentStatus.Enum.PAYMENTSUCCESS_2 : PaymentStatus.Enum.PAYMENTFAILED_3);
            List<Payment> childPayments = paymentRepository.findByPurchaseOrder(payment.getPurchaseOrder());
            payment = refundStatusHandler.changeOrderStatus(childPayments, payment.getPurchaseOrderAmend(), purchaseOrder);
            if(null!=payment) {
                try {
                    redisPublisher.publish(payment);
                } catch (ConcurrentApiCallLockException e) {
                    logger.info("该支付单已经被锁定");
                }
            }
        }catch(Exception e){
            logger.info("支付宝退款接口异步回调验证失败 OutTradeNo:{}",payment.getOutTradeNo());
            e.printStackTrace();
            paymentSuccess = false;
        }finally{
            logger.info("支付宝退款接口异步回调 resultDetails:{}",resultDetails);
        }

        if(paymentSuccess) {
            debugWord = String.format("客户端类型[%s] 异步回调验证成功,订单号[%s] 支付宝result_details [%s]=====success=====", clientType.toString(), purchaseOrder.getOrderNo(),resultDetails);
        }else{
            debugWord = String.format("客户端类型[%s] 异步回调验证失败,订单号[%s] 支付宝result_details [%s]", clientType.toString(), purchaseOrder.getOrderNo(), resultDetails);
        }
        alipayCore.logResult(batchNo, debugWord);// 记录日志
    }

    @Transactional
    private Boolean modifyStatus(Payment payment, PaymentStatus paymentStatus) {
        payment.setStatus(paymentStatus);
        paymentRepository.save(payment);
        String debugWord = String.format("订单号:[%s] , payment状态:[%s] ,purchaseOrder状态:[%s] ", payment.getOutTradeNo(), String.valueOf(paymentStatus.getId()), String.valueOf(payment.getPurchaseOrder().getStatus().getId()));
        alipayCore.logResult(payment.getOutTradeNo(), debugWord);
        return Boolean.TRUE;
    }

    public void payNotify(HttpServletRequest request, HttpServletResponse response, Channel clientType) {

        try {

            String debugWord = "";
            //验证结构
            boolean verifyRes = false;
            //payment状态
            PaymentStatus paymentStatus = null;
            //order状态
            OrderStatus orderStatus = null;
            //获取支付宝POST过来反馈信息
            Map<String, String> params = alipayCore.convertRequestParamToMap(request, AliPayConstant.INPUT_CHARSET, false);
            //商户订单号
            final String outTradeNo = alipayCore.getParameter(request, "out_trade_no");

            if(RuntimeUtil.isNonAuto(outTradeNo)){
                logger.info("非车支付宝异步回调开始, {}", outTradeNo);
                NACallbackService.asyncCallback(PaymentChannel.Enum.ALIPAY_1, params);
                return;
            }

            //支付宝交易号
            String tradeNo = alipayCore.getParameter(request, "trade_no");
            //交易状态
            String tradeStatus = alipayCore.getParameter(request, "trade_status");
            alipayCore.logResult(outTradeNo, params.toString());

            AliPayHandler aliPayHandler = aliPayHandlerBuilder.findByChannel(clientType);
            //计算得出通知验证结果
            boolean verifyResult;
            if (RuntimeUtil.isProductionEnv()) {
                verifyResult = aliPayHandler.verify(params, outTradeNo);
            } else {
                logger.info("非生产环境签名验证");
                verifyResult = true;
            }

            Payment payment = paymentRepository.findByOutTradeNo(outTradeNo);

            if(responseToAli(payment, tradeStatus, response)){
                return;
            }

            if(PaymentType.Enum.DAILY_RESTART_PAY_7.equals(payment.getPaymentType())){
                PaymentCallback.call(PaymentChannel.Enum.ALIPAY_1, new HashMap(){{
                    put("outTradeNo", outTradeNo);
                    put("payResult", verifyResult && successCallback(tradeStatus));

                }});
                return;
            }

            payment.setChannel(PaymentChannel.Enum.ALIPAY_1);
            PurchaseOrder purchaseOrder = payment.getPurchaseOrder();
            if (verifyResult) {
                if (successCallback(tradeStatus)) {
                    verifyRes = true;
                    paymentStatus = PaymentStatus.Enum.PAYMENTSUCCESS_2;
                    orderStatus = OrderStatus.Enum.PAID_3;
                }
            } else {
                paymentStatus = PaymentStatus.Enum.PAYMENTFAILED_3;
                orderStatus = purchaseOrder.getOperator() == null ? OrderStatus.Enum.PENDING_PAYMENT_1 : OrderStatus.Enum.HANDLING_2;
            }
            Boolean modifySuccess = Boolean.FALSE;
            if (paymentStatus != null && orderStatus != null) {
                modifySuccess = modifyStatus(payment, paymentStatus, orderStatus, params);// 更新payment和purchaseOrder的状态
            }

            if (verifyRes && modifySuccess) {
                QuoteRecord quoteRecord = quoteRecordRepository.findOne(purchaseOrder.getObjId());
                ConditionTriggerUtil.sendPaymentSuccessMessage(conditionTriggerHandler, quoteRecord, purchaseOrder);
                redisPublisher.publish(payment);
            }
            if (verifyRes) {
                debugWord = String.format("客户端类型[%s] 支付宝交易单号[%s]异步回调验证成功,订单号[%s]=====success=====", clientType.toString(), tradeNo, outTradeNo);
                response.getWriter().write(NOTIFY_SUCCESS);
            } else {
                debugWord = String.format("客户端类型[%s] 支付宝交易单号[%s]异步回调验证失败,订单号[%s]=====fail=====", clientType.toString(), tradeNo, outTradeNo);
                response.getWriter().write(NOTIFY_FAIL);
            }
            alipayCore.logResult(outTradeNo, debugWord);// 记录日志
        } catch (Exception e) {
            String exceptinString = String.format("客户端类型[%s] 异步回调 exception info: %s", clientType.toString(), ExceptionUtils.getStackTrace(e));
            alipayCore.logResult("", exceptinString);
        }
    }

    private boolean responseToAli(Payment payment, String tradeStatus, HttpServletResponse response) {
        logger.info("流水号为:{}的payment开始判断支付状态", payment.getOutTradeNo());
        if(PaymentStatus.Enum.PAYMENTSUCCESS_2.equals(payment.getStatus())){
            try {
                response.getOutputStream().print("success");
                logger.info("流水号为:{}的payment已经支付完成返回支付宝success",payment.getOutTradeNo());
            } catch (IOException e) {
                e.printStackTrace();
                logger.info("流水号为:{}的payment返回支付宝异常",payment.getOutTradeNo());
                return false;
            }
        } else if (skipCallback(tradeStatus)) {
            logger.info("流水号为:{}的tradeStatus为{}，跳过回调处理步骤", payment.getOutTradeNo(), tradeStatus);
            return true;
        }else{
            logger.info("流水号为:{}的payment支付状态未修改完成",payment.getOutTradeNo());
            return false;
        }
        return true;
    }

    /**
     * 更新payment和purchaseOrder的状态
     *
     * @param paymentStatus
     * @param orderStatus
     * @param paraMap
     */
    @Transactional
    private Boolean modifyStatus(Payment payment, PaymentStatus paymentStatus, OrderStatus orderStatus, Map<String, String> paraMap) {
        String outTradeNo = paraMap.get("out_trade_no");
        String aliPayTradeNo = paraMap.get("trade_no");
        PurchaseOrder purchaseOrder = payment.getPurchaseOrder();

        if (PaymentStatus.Enum.PAYMENTSUCCESS_2.equals(payment.getStatus())) {
            String sWord = String.format("订单[%s]已经更新过payment和purchaseOrder状态.", outTradeNo);
            alipayCore.logResult(outTradeNo, sWord);
            return Boolean.FALSE;
        }
        //update payment
        payment.setStatus(paymentStatus);
        payment.setThirdpartyPaymentNo(aliPayTradeNo);
        paymentRepository.save(payment);

        //update purchaseOrder
        purchaseOrder.setStatus(orderStatus);
        purchaseOrderRepository.save(purchaseOrder);
        String debugWord = String.format("订单号:[%s] , payment状态:[%s] ,purchaseOrder状态:[%s] ", outTradeNo, String.valueOf(paymentStatus.getId()), String.valueOf(orderStatus.getId()));
        alipayCore.logResult(outTradeNo, debugWord);
        return Boolean.TRUE;
    }

    private static boolean successCallback(String tradeStatus){
        return tradeStatus.equals("TRADE_FINISHED") || tradeStatus.equals("TRADE_SUCCESS");
    }

    private static boolean skipCallback(String tradeStatus){
        return tradeStatus.equals("WAIT_BUYER_PAY");
    }
}
