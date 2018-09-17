package com.cheche365.cheche.rest.web.controller;

import com.cheche365.cheche.core.exception.BusinessException;
import com.cheche365.cheche.core.message.RedisPublisher;
import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.model.OrderStatus;
import com.cheche365.cheche.core.model.Payment;
import com.cheche365.cheche.core.model.PurchaseOrder;
import com.cheche365.cheche.core.repository.PaymentRepository;
import com.cheche365.cheche.core.repository.PurchaseOrderRepository;
import com.cheche365.cheche.core.repository.QuoteRecordRepository;
import com.cheche365.cheche.core.service.PurchaseOrderService;
import com.cheche365.cheche.core.service.sms.ConditionTriggerHandler;
import com.cheche365.cheche.core.util.RuntimeUtil;
import com.cheche365.cheche.unionpay.UnionPayState;
import com.cheche365.cheche.web.ContextResource;
import com.cheche365.cheche.web.service.PaymentCallbackURLHandler;
import com.umpay.api.exception.VerifyException;
import com.umpay.api.util.DataUtil;
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
import java.util.Map;

import static com.cheche365.cheche.core.model.PaymentStatus.Enum.PAYMENTFAILED_3;
import static com.cheche365.cheche.core.model.PaymentStatus.Enum.PAYMENTSUCCESS_2;
import static com.cheche365.cheche.core.service.PaymentSerialNumberGenerator.getPurchaseNo;
import static com.cheche365.cheche.core.service.sms.ConditionTriggerUtil.sendPaymentSuccessMessage;
import static com.umpay.api.paygate.v40.Mer2Plat_v40.merNotifyResData;
import static com.umpay.api.paygate.v40.Plat2Mer_v40.getPlatNotifyData;

/**
 * Created by mjg on 2017/6/19.
 */
@Controller
@RequestMapping("/web/soopay")
public class SoopayResource extends ContextResource {

    private Logger logger = LoggerFactory.getLogger(SoopayResource.class);

    @Autowired
    private PurchaseOrderService orderService;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentCallbackURLHandler paymentCallbackURLHandler;

    @Autowired
    private RedisPublisher redisPublisher;

    @Autowired
    private ConditionTriggerHandler conditionTriggerHandler;

    @Autowired
    private QuoteRecordRepository quoteRecordRepository;



    @RequestMapping(value = "/back/front")
    public ModelAndView frontCall(HttpServletRequest req) throws UnsupportedEncodingException {
        logger.info("接收联动优势支付前台通知报文开始...");
        String redirectUrl;
        req.setCharacterEncoding("ISO-8859-1");
        String encoding = req.getParameter("encoding");
        Map<String, String> respParam = this.getAllRequestParam(req);
        String orderNo = respParam.get("orderId");

        PurchaseOrder order = orderService.getFirstPurchaseOrderByNo(getPurchaseNo(orderNo));
        try {
            logger.info("联动优势支付前台通知报文参数 -> {}", respParam.toString());
            if (respParam.isEmpty() || StringUtils.isBlank(getPurchaseNo(orderNo)))
                throw new RuntimeException("联动优势支付前台通知报文为空或没有orderId参数");

            if (null == order)
                throw new BusinessException(BusinessException.Code.OBJECT_NOT_EXIST, "订单" + getPurchaseNo(orderNo) + "不存在");

            if (!SDKUtil.validate(respParam, encoding)) {
                logger.warn("联动优势支付前台通知报文验签失败");
                throw new RuntimeException("联动优势支付前台通知报文验签失败");
            }

            Channel channel = Channel.toChannel(Long.valueOf(respParam.get("reqReserved")));
            if (null == channel)
                throw new BusinessException(BusinessException.Code.INTERNAL_SERVICE_ERROR, "无法确定支付客户端类型[" + respParam.get("reqReserved") + "]");

//            frontTradeHandler.callFront(channel, respParam);

            if (UnionPayState.isPaySuccess(respParam.get("respCode"))) {
                redirectUrl = paymentCallbackURLHandler.toFrontCallBackPage(order, req);
            } else {
                redirectUrl = paymentCallbackURLHandler.toFrontCallBackPage(order, req);
            }
        } catch (Exception ex) {
            logger.error("接收联动优势支付前台通知报文异常", ex);
            redirectUrl = paymentCallbackURLHandler.toFrontCallBackPage(order, req);
        }

        logger.info("联动优势前台通知报文处理完成跳转页面url: {}", redirectUrl);
        return new ModelAndView("redirect:" + redirectUrl);
    }


    @RequestMapping(value = "/back/refund")
    @ResponseBody
    public String refundNotice(HttpServletRequest req) throws UnsupportedEncodingException {
        logger.info("接收联动优势支退款通知");
        req.setCharacterEncoding("ISO-8859-1");

        Map<String, String> body = new HashMap<>();
        body.put("met_id", "50024");
        body.put("sign_type", "RSA");
        body.put("version", "4.0");

        Map respParam = null;
        try {
            respParam = getPlatNotifyData(req);
            body.put("ret_code", "0000");
            body.put("ret_msg", "成功获取回调");
        } catch (VerifyException e) {
            body.put("ret_code", "0001");
            body.put("ret_msg", "验签失败");
        }
        logger.info("联动优势支付后台通知报文参数 -> {}", respParam.toString());
        logger.info("退费金额:{}", respParam.get("refund_amt"));


        String outTradeNO = (String) respParam.get("order_id");
        Payment payment = paymentRepository.findByOutTradeNo(outTradeNO);
        PurchaseOrder purchaseOrder = payment.getPurchaseOrder();

        String refundState = (String) respParam.get("refund_state");
        if ("REFUND_PROCESS".equals(refundState)) {
            logger.info("payment流水号{},paymment类型:{},支付状态:处理中", outTradeNO, payment.getPaymentType().getName());
        } else if ("REFUND_SUCCESS".equals(refundState)) {
            payment.setStatus(PAYMENTSUCCESS_2);


            payment.setThirdpartyPaymentNo((String) respParam.get("trade_no"));


            purchaseOrder.setStatus(OrderStatus.Enum.PAID_3);
            paymentRepository.save(payment);
            purchaseOrderRepository.save(purchaseOrder);
            logger.info("payment流水号{},paymment类型:{},支付状态:支付成功", outTradeNO, payment.getPaymentType().getName());
            redisPublisher.publish(payment);
        } else if ("REFUND_FAIL".equals(refundState)) {
            payment.setStatus(PAYMENTFAILED_3);
            paymentRepository.save(payment);
            logger.info("payment流水号{},paymment类型:{},支付状态:支付失败", outTradeNO, payment.getPaymentType().getName());
        } else {
            logger.info("退款状态非法！");
        }

        return "<META NAME=\"MobilePayPlatform\" CONTENT= " + merNotifyResData(body) + ">";

    }


    @ResponseBody
    @RequestMapping(value = "/back/notice")
    public String backNotice(HttpServletRequest req) throws UnsupportedEncodingException {

        logger.info("接收联动优势支付后台通知报文开始...");
        req.setCharacterEncoding("ISO-8859-1");

        Map<String,String> body = new HashMap<>();
        Map respParam = null;
        body.put("mer_id","50024");
        body.put("sign_type","RSA");
        body.put("version","4.0");
        try {
            if(RuntimeUtil.isProductionEnv()){
                respParam = getPlatNotifyData(req);
            } else {
                respParam = DataUtil.getData(req);
                logger.debug("支付结果通知请求数据为:" + respParam);
                logger.info("非生产环境签名验证");
            }
            logger.warn("联动支付支付验签成功");
            body.put("mer_date",(String) respParam.get("mer_date"));
            body.put("order_id",(String)respParam.get("order_id"));
            body.put("ret_code", "0000");
            body.put("ret_msg", "车车验签成功");
        } catch (VerifyException e) {
            logger.warn("联动支付支付验签错误");
            body.put("ret_code", "0001");
            body.put("ret_msg", "车车验签失败");
        }
        if("0000".equals(body.get("ret_code"))){
            String outTradeNO = (String) respParam.get("order_id");
            Payment payment = paymentRepository.findByOutTradeNo(outTradeNO);
            logger.info("联动优势支付后台通知报文参数 -> {}", respParam.toString());

            PurchaseOrder purchaseOrder = payment.getPurchaseOrder();
            if (!PAYMENTSUCCESS_2.equals(payment.getStatus())) {
                if ("TRADE_SUCCESS".equals(respParam.get("trade_state"))) {
                    logger.info("payment流水号{}支付成功", outTradeNO);
                    payment.setStatus(PAYMENTSUCCESS_2);
                    payment.setThirdpartyPaymentNo((String) respParam.get("trade_no"));
                    purchaseOrder.setStatus(OrderStatus.Enum.PAID_3);
                    paymentRepository.save(payment);
                    orderService.saveOrder(purchaseOrder);
                    sendPaymentSuccessMessage(conditionTriggerHandler, quoteRecordRepository.findOne(purchaseOrder.getObjId()), purchaseOrder);
                    redisPublisher.publish(payment);
                } else if("TRADE_FAIL".equals(respParam.get("trade_state"))){
                    logger.info("payment流水号{}支付失败", outTradeNO);
                    payment.setStatus(PAYMENTFAILED_3);
                    paymentRepository.save(payment);
                    redisPublisher.publish(payment);
                } else if("TRADE_CLOSE".equals(respParam.get("trade_state"))){
                    logger.info("payment流水号{}：交易已经关闭，支付已经过期的订单", outTradeNO);
                }
            } else {
                logger.warn("payment流水号{}已经支付过，不再更新订单", outTradeNO);
            }
        }

        return "<META NAME=\"MobilePayPlatform\" CONTENT=\"" + merNotifyResData(body) + "\">";

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
