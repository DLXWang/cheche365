package com.cheche365.cheche.wechat.payment

import com.cheche365.cheche.core.WechatConstant
import com.cheche365.cheche.core.constants.WebConstants
import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.MoApplicationLog
import com.cheche365.cheche.core.model.OrderStatus
import com.cheche365.cheche.core.model.Payment
import com.cheche365.cheche.core.model.PaymentChannel
import com.cheche365.cheche.core.model.PaymentType
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.User
import com.cheche365.cheche.core.model.WechatUserChannel
import com.cheche365.cheche.core.repository.PaymentRepository
import com.cheche365.cheche.core.repository.WechatUserChannelRepository
import com.cheche365.cheche.core.service.DoubleDBService
import com.cheche365.cheche.core.util.IpUtil
import com.cheche365.cheche.core.util.RuntimeUtil
import com.cheche365.cheche.internal.integration.answern.api.PaymentCallback
import com.cheche365.cheche.internal.integration.na.NACallbackService
import com.cheche365.cheche.wechat.PaymentManager
import com.cheche365.cheche.wechat.TradeState
import com.cheche365.cheche.wechat.TradeType
import com.cheche365.cheche.wechat.message.UnifiedOrderRequest
import com.cheche365.cheche.wechat.message.UnifiedOrderResponse
import com.cheche365.cheche.wechat.util.Signature
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession
import java.text.DateFormat
import java.text.SimpleDateFormat

import static com.cheche365.cheche.core.model.Channel.Enum.WAP_8
import static com.cheche365.cheche.core.model.Channel.Enum.WEB_5
import static com.cheche365.cheche.core.model.Channel.Enum.WE_CHAT_3
import static com.cheche365.cheche.core.model.Channel.Enum.WE_CHAT_APP_39

/**
 * Created by zhengwei on 5/12/15.
 */

@Service
class OrderPaymentManager extends PaymentManager {

    private Logger logger = LoggerFactory.getLogger(OrderPaymentManager.class);

    @Autowired
    WechatUserChannelRepository wechatUserChannelRepository;

    @Autowired
    private DoubleDBService mongoDBService;

    @Autowired
    private PaymentCallbackHandler paymentCallbackHandler;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired(required = false)
    public HttpSession session;

    @Autowired(required = false)
    HttpServletRequest request

    @Transactional
    void savePaymentMchId(Payment payment) {
        paymentRepository.save(payment);
    }


    UnifiedOrderResponse unifiedOrder(User user, String outTradeNo, Double amount, String body, Channel channel) {
        TradeType tradeType;
        switch (channel) {
            case Channel.selfApp():
                tradeType = TradeType.APP
                break
            case WAP_8:
                tradeType = TradeType.MWEB
                break
            case WEB_5:
                tradeType = TradeType.NATIVE
                break
            default:
                tradeType = TradeType.JSAPI
        }

        UnifiedOrderRequest unifiedOrder = this.toUnifiedOrderRequest(outTradeNo, user, body, amount, tradeType, channel);

        String response = messageSender.postPayMessage("pay/unifiedorder", new HashMap<String, Object>(), unifiedOrder);
        logger.debug("received payment result: " + response);
        return UnifiedOrderResponse.parseMessage(response);
    }


    @Transactional
    boolean processOrderQueryResponse(Map<String, Object> response) {

        String orderNoFromWechat = (String) response.get("out_trade_no");
        if(RuntimeUtil.isNonAuto(orderNoFromWechat)){
            logger.info("非车微信异步回调开始, {}", orderNoFromWechat);
            NACallbackService.asyncCallback(PaymentChannel.Enum.WECHAT_4, response);
            return true;
        }

        logger.info("微信支付回调orderNoFromWechat:{}", orderNoFromWechat);
        String orderNoInCheChe = PurchaseOrder.toOrderNo(orderNoFromWechat);

        if (logger.isDebugEnabled()) {
            logger.debug("wechat call back with order no: {}, after trim the suffix the order no: {}", orderNoFromWechat, orderNoInCheChe);
        }

        Payment payment = paymentRepository.findByOutTradeNo(orderNoInCheChe);
        logger.info("微信支付回调orderNoFromWechat:" + orderNoInCheChe + "payment:" + payment);
        payment.setChannel(PaymentChannel.Enum.WECHAT_4);
        PurchaseOrder purchaseOrder = payment.getPurchaseOrder();
        if (null == payment) {
            logger.warn(String.format("Can't find purchase order [%s]", orderNoInCheChe));
            throw new BusinessException(BusinessException.Code.OBJECT_NOT_EXIST, "Could not find purchase order by order number [" + orderNoInCheChe + "]");
        }

        String processed = alreadyProcessed(orderNoInCheChe);  //TODO may use boolean
        if (StringUtils.isNotEmpty(processed)) {
            logger.warn("the purchase order: " + orderNoInCheChe + " has already processed, wechat send duplicate callback message, will ignore it. ");
            return Boolean.valueOf(processed);
        }

        if (!"SUCCESS".equals(response.get("return_code"))) {
            logger.info("微信支付回调不成功Outtradeno:{}  msg:{}", orderNoInCheChe, response.get("return_msg"));
            throw new BusinessException(BusinessException.Code.EXTERNAL_SERVICE_ERROR, (String) response.get("return_msg"));
        }

        boolean isSign = verifySignature(response);
        if (!isSign) {
            MoApplicationLog log = MoApplicationLog.applicationLogByPurchaseOrder(purchaseOrder);
            log.setLogMessage("invalidate wechat sign, purchase order number is: " + orderNoInCheChe + ", expected: " + Signature.getSign(response) + ", but get: " + response.get("sign"));
            mongoDBService.saveApplicationLog(log);
            throw new BusinessException(BusinessException.Code.EXTERNAL_SERVICE_ERROR, "invalid signature");
        }

        if(PaymentType.Enum.DAILY_RESTART_PAY_7 == payment.getPaymentType()){
            PaymentCallback.call(PaymentChannel.Enum.WECHAT_4, [
                outTradeNo: orderNoInCheChe,
                payResult: "SUCCESS".equals(response.get("result_code"))

            ]);
            return true;
        }

        if (!"SUCCESS".equals(response.get("result_code"))) {
            MoApplicationLog log = MoApplicationLog.applicationLogByPurchaseOrder(purchaseOrder);
            log.setLogMessage("wechat payment failure, the purchase order number is " + orderNoInCheChe + ", the error desc from wechat is " + response.get("err_code_desc"));
            mongoDBService.saveApplicationLog(log);
            throw new BusinessException(BusinessException.Code.EXTERNAL_SERVICE_ERROR, (String) response.get("err_code_desc"));
        }

        TradeState tradeState;
        boolean locked = redisTemplate.opsForHash().putIfAbsent("wechat:payment", orderNoInCheChe, "processing");
        if (locked) {
            try {
                if (OrderStatus.Enum.PENDING_PAYMENT_1.getId().equals(purchaseOrder.getStatus().getId())) {
                    String tradeStateString = (String) response.get("trade_state");
                    tradeState = toTradeState(tradeStateString);
                    if (null == tradeState) {
                        MoApplicationLog log = MoApplicationLog.applicationLogByPurchaseOrder(purchaseOrder);
                        log.setLogMessage("there is an unexpected trade state from wechat for purchase order " + orderNoInCheChe + " : " + tradeStateString);
                        mongoDBService.saveApplicationLog(log);
                        throw new BusinessException(BusinessException.Code.INTERNAL_SERVICE_ERROR, "unexpected trade state from wechat " + tradeStateString + ", see application log for more");
                    }
                    purchaseOrder.appendDescription("收到微信支付回调微信端流水号为" + orderNoFromWechat + ";");
                    purchaseOrder.setWechatPaymentSuccessOrderNo(orderNoFromWechat);
                    paymentCallbackHandler.setOrderQueryResponse(response).setPayment(payment);
                    paymentCallbackHandler.doService(tradeState);
                    return TradeState.processFinishedStates().contains(tradeStateString);
                } else {
                    logger.info(String.format(
                        "purchase order [%s] has been processed by other application/instance/thread, this request will be discarded.", orderNoInCheChe));
                }
            } finally {
                redisTemplate.opsForHash().delete("wechat:payment", orderNoInCheChe);
            }
        } else {
            logger.info(String.format("purchase order [%s] is processing by other application/instance/thread, this request will be discarded.", orderNoInCheChe));
        }
        return false;
    }

    private TradeState toTradeState(String tsInString) {
        if (StringUtils.isBlank(tsInString)) {
            logger.debug("the trade state is blank, may called from wechat payment callback, not order query, will use success state.");
            return TradeState.SUCCESS; //Since trade_state may not be included in request. We always treat it as success if both return_code and err_code are SUCCESS and trade_state is null
        } else {
            if (!TradeState.contains(tsInString)) {
                logger.debug("fail to map the incoming trade state to existing states: " + tsInString);
                return null;
            }
            return TradeState.valueOf(tsInString);
        }
    }

    private String alreadyProcessed(String purchaseOrderNo) {
        return (String) redisTemplate.opsForHash().get(PROCESSED_ORDER_KEY, purchaseOrderNo);
    }

    UnifiedOrderRequest toUnifiedOrderRequest(String outTradeNo, User user, String body, Double amount, TradeType tradeType, Channel channel) {
        UnifiedOrderRequest request = getWechatPayTemplate(channel, user, body, tradeType.name())
            .setOut_trade_no(outTradeNo)
            .setSpbill_create_ip(IpUtil.getIP(request))
            .setNotify_url(WechatConstant.getPayCallBackUrl())
            .setProduct_id(outTradeNo);

        request.setTotal_fee(((Double) (amount * 100)).longValue());

        return request;
    }

    UnifiedOrderRequest getWechatPayTemplate(Channel channel, User user, String body, String tradeType){
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar now = Calendar.getInstance();
        UnifiedOrderRequest unifiedOrder = new UnifiedOrderRequest(channel).setTime_start(dateFormat.format(now.getTime())).setBody(body);
        unifiedOrder.setTrade_type(tradeType);
        if (TradeType.JSAPI.name() == unifiedOrder.getTrade_type()) {
            if(null != session.getAttribute(WebConstants.SESSION_KEY_WECHAT_OPEN_ID)){
                unifiedOrder.setOpenid(session.getAttribute(WebConstants.SESSION_KEY_WECHAT_OPEN_ID).toString());
            } else {
                if(user){
                    wechatUserInfoRepository.findFirstByUser(user)?.with {
                        WechatUserChannel userChannel = this.wechatUserChannelRepository.findByWechatUserInfoAndChannel(it, WE_CHAT_APP_39 == channel ? WE_CHAT_APP_39 : WE_CHAT_3);
                        unifiedOrder.setOpenid(userChannel?.getOpenId());
                    }
                }

            }

            if(!unifiedOrder.openid) {
                throw new BusinessException(BusinessException.Code.OPERATION_NOT_ALLOWED, "JSAPI微信支付时未找到openid ");
            }
        }

        now.add(Calendar.MINUTE, 10);
        return unifiedOrder.setTime_expire(dateFormat.format(now.getTime()));
    }


    private boolean verifySignature(Map response) {
        String receivedSign = (String) response.get("sign");
        response.put("sign", null);
        String sign = Signature.getSign(response);
        if (logger.isDebugEnabled()) {
            logger.debug("received sign:{}, calculated sign:{}", receivedSign, sign);
        }
        response.put("sign", receivedSign);
        if (RuntimeUtil.isProductionEnv()) {
            return sign.equals(receivedSign);
        } else {
            logger.info("非生产环境签名验证");
            return true;
        }
    }

}
