package com.cheche365.cheche.rest.web.controller;

import com.cheche365.cheche.core.exception.BusinessException;
import com.cheche365.cheche.core.exception.ConcurrentApiCallLockException;
import com.cheche365.cheche.core.message.RedisPublisher;
import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.model.Payment;
import com.cheche365.cheche.core.model.PaymentChannel;
import com.cheche365.cheche.core.model.PurchaseOrder;
import com.cheche365.cheche.core.repository.PaymentRepository;
import com.cheche365.cheche.core.service.PurchaseOrderRefundStatusHandler;
import com.cheche365.cheche.core.service.PurchaseOrderService;
import com.cheche365.cheche.core.util.RuntimeUtil;
import com.cheche365.cheche.internal.integration.na.NACallbackService;
import com.cheche365.cheche.unionpay.UnionPayState;
import com.cheche365.cheche.unionpay.payment.UnionPayProcessor;
import com.cheche365.cheche.unionpay.payment.front.UnionPayService;
import com.cheche365.cheche.web.ContextResource;
import com.cheche365.cheche.web.service.PaymentCallbackURLHandler;
import com.unionpay.acp.sdk.SDKUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.cheche365.cheche.core.service.PaymentSerialNumberGenerator.getPurchaseNo;

/**
 * Created by wangfei on 2015/7/9.
 */
@Controller
@RequestMapping("/web/unionpay")
public class UnionPayResource extends ContextResource {
    private Logger logger = LoggerFactory.getLogger(UnionPayResource.class);

    @Autowired
    private PurchaseOrderService orderService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UnionPayService frontTradeContext;

    @Autowired
    private UnionPayProcessor unionPayProcessor;

    @Autowired
    private PaymentCallbackURLHandler paymentCallbackURLHandler;

    @Autowired
    private RedisPublisher redisPublisher;

    @Autowired
    private PurchaseOrderRefundStatusHandler refundStatusHandler;



    @RequestMapping(value = "/back/front")
    public ModelAndView frontCall(HttpServletRequest req) throws UnsupportedEncodingException {
        logger.info("接收银联支付前台通知报文开始...");
        String redirectUrl;
        req.setCharacterEncoding("ISO-8859-1");
        String encoding = req.getParameter("encoding");
        Map<String, String> respParam = this.getAllRequestParam(req);
        String orderNo = respParam.get("orderId");

        if(RuntimeUtil.isNonAuto(orderNo)){
            logger.info("非车银联同步回调开始, {}", orderNo);
            redirectUrl = NACallbackService.syncCallback(PaymentChannel.Enum.UNIONPAY_3, respParam);
            logger.info("非车跳转链接 {}", redirectUrl);
            return new ModelAndView("redirect:" + redirectUrl);
        }

        PurchaseOrder order = orderService.getFirstPurchaseOrderByNo(getPurchaseNo(orderNo));
        try {
            logger.info("银联支付前台通知报文参数 -> {}", respParam.toString());
            if (respParam.isEmpty() || StringUtils.isBlank(getPurchaseNo(orderNo)))
                throw new RuntimeException("银联支付前台通知报文为空或没有orderId参数");

            if (null == order)
                throw new BusinessException(BusinessException.Code.OBJECT_NOT_EXIST, "订单" + getPurchaseNo(orderNo) + "不存在");

            if(RuntimeUtil.isProductionEnv()) {
                if (!SDKUtil.validate(respParam, encoding)) {
                    unionPayProcessor.saveUnionPayLog(getPurchaseNo(orderNo), "银联支付前台通知报文验签失败");
                    throw new RuntimeException("银联支付前台通知报文验签失败");
                }
            }

            Channel channel = Channel.toChannel(Long.valueOf(respParam.get("reqReserved")));
            if (null == channel)
                throw new BusinessException(BusinessException.Code.INTERNAL_SERVICE_ERROR, "无法确定支付客户端类型[" + respParam.get("reqReserved") + "]");

            frontTradeContext.callFront(channel, respParam);

            if (UnionPayState.isPaySuccess(respParam.get("respCode"))) {
                redirectUrl = paymentCallbackURLHandler.toFrontCallBackPage(order, req);
            } else {
                redirectUrl = paymentCallbackURLHandler.toFrontCallBackPage(order, req);
            }
        } catch (Exception ex) {
            logger.error("接收银联支付前台通知报文异常", ex);
            redirectUrl = paymentCallbackURLHandler.toFrontCallBackPage(order, req);
        }

        logger.info("银联前台通知报文处理完成跳转页面url: {}", redirectUrl);
        return new ModelAndView("redirect:" + redirectUrl);
    }

    @ResponseBody
    @RequestMapping(value = "/back/notice")
    public void backNotice(HttpServletRequest req) throws UnsupportedEncodingException {
        logger.info("接收银联支付退款后台通知报文开始...");
        req.setCharacterEncoding("ISO-8859-1");
        String encoding = req.getParameter("encoding");
        Map<String, String> respParam =  this.getAllRequestParam(req);
        String refundOrPay = UnionPayProcessor.isUnionPayRefundTrade(respParam.get("txnType"))?"退款":"支付";
        String orderId = respParam.get("orderId");

        if(RuntimeUtil.isNonAuto(orderId)){
            logger.info("非车银联异步回调开始, {}", orderId);
            NACallbackService.asyncCallback(PaymentChannel.Enum.UNIONPAY_3, respParam);
            return;
        }

        logger.info("银联"+refundOrPay+"后台通知报文参数 -> {}", respParam.toString());
        if (respParam.isEmpty() || StringUtils.isBlank(orderId))
            throw new RuntimeException("银联支付后台通知报文为空或没有orderId参数");

        if(RuntimeUtil.isProductionEnv()) {
            if (!SDKUtil.validate(respParam, encoding)) {
                unionPayProcessor.saveUnionPayLog(orderId, "银联"+refundOrPay+"后台通知报文验签失败");
                throw new RuntimeException("银联支付后台通知报文验签失败");
            }
        }
        if (UnionPayProcessor.isUnionPayRevokeTrade(respParam.get("txnType"))) {
            unionPayProcessor.saveUnionPayLog(orderId, "银联支付撤销交易后台通知报文");
            logger.info("非预期回调类型 订单号:{} txnType:{}",orderId,respParam.get("txnType"));
            return;
        }

        PurchaseOrder order = orderService.getFirstPurchaseOrderByNo(getPurchaseNo(orderId));
        if (null == order)
            throw new BusinessException(BusinessException.Code.OBJECT_NOT_EXIST, "订单" + getPurchaseNo(orderId) + "不存在");

        Channel channel = Channel.toChannel(Long.valueOf(respParam.get("reqReserved")));
        if (null == channel)
            throw new BusinessException(BusinessException.Code.INTERNAL_SERVICE_ERROR, "无法确定支付客户端类型[" + respParam.get("reqReserved") + "]");

        frontTradeContext.callBack(channel, respParam);

        Payment payment = paymentRepository.findFirstByOutTradeNo(orderId);

        order = orderService.getFirstPurchaseOrderByNo(getPurchaseNo(orderId));//向第三方发送报文前,从新查询order

        if (UnionPayProcessor.isUnionPayRefundTrade(respParam.get("txnType"))) {
            List<Payment> childPayments = paymentRepository.findByPurchaseOrder(payment.getPurchaseOrder());
            payment = refundStatusHandler.changeOrderStatus(childPayments, payment.getPurchaseOrderAmend(), order);
        }
        if(null!=payment) {
            try {
                redisPublisher.publish(payment);
            }catch(ConcurrentApiCallLockException e){
                logger.info("该支付单已经被锁定");
            }
        }

        logger.info("接收银联"+refundOrPay+"后台通知报文结束...");
    }

    private Map<String, String> getAllRequestParam(final HttpServletRequest request) {
        Map<String, String> res = new HashMap<>();
        Enumeration<?> temp = request.getParameterNames();
        if (null != temp) {
            while (temp.hasMoreElements()) {
                String en = (String) temp.nextElement();
                String value = request.getParameter(en);
                res.put(en, value);
                if (res.get(en) == null || "".equals(res.get(en))) {
                    res.remove(en);
                }
            }
        }
        return res;
    }

}
