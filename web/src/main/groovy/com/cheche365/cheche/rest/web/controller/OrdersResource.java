package com.cheche365.cheche.rest.web.controller;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.constants.WebConstants;
import com.cheche365.cheche.core.exception.BusinessException;
import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.model.OrderStatus;
import com.cheche365.cheche.core.model.OrderType;
import com.cheche365.cheche.core.model.PaymentChannel;
import com.cheche365.cheche.core.model.PlaceOrderResult1_1;
import com.cheche365.cheche.core.model.PurchaseOrder;
import com.cheche365.cheche.core.model.PurchaseOrderAmend;
import com.cheche365.cheche.core.model.QuoteRecord;
import com.cheche365.cheche.core.model.SimpleOrderResult;
import com.cheche365.cheche.core.repository.CompulsoryInsuranceRepository;
import com.cheche365.cheche.core.repository.InsuranceRepository;
import com.cheche365.cheche.core.repository.PurchaseOrderAmendRepository;
import com.cheche365.cheche.core.repository.QuoteRecordRepository;
import com.cheche365.cheche.core.repository.WebPurchaseOrderRepository;
import com.cheche365.cheche.core.service.OrderCancelReasonService;
import com.cheche365.cheche.core.service.OrderRelatedService;
import com.cheche365.cheche.core.service.QuoteConfigService;
import com.cheche365.cheche.core.service.qrcode.QRCodeService;
import com.cheche365.cheche.core.service.sms.ConditionTriggerHandler;
import com.cheche365.cheche.core.service.sms.ConditionTriggerUtil;
import com.cheche365.cheche.core.util.CacheUtil;
import com.cheche365.cheche.core.util.RuntimeUtil;
import com.cheche365.cheche.internal.integration.na.api.PayParams;
import com.cheche365.cheche.rest.model.PaymentWrapper;
import com.cheche365.cheche.rest.service.InitPaymentService;
import com.cheche365.cheche.web.ContextResource;
import com.cheche365.cheche.web.model.SimpleMessageResult;
import com.cheche365.cheche.web.response.RestResponseEnvelope;
import com.cheche365.cheche.web.service.UserCallbackService;
import com.cheche365.cheche.core.service.WebPurchaseOrderService;
import com.cheche365.cheche.web.service.order.ClientOrderService;
import com.cheche365.cheche.web.service.system.OrderImageURL;
import com.cheche365.cheche.web.service.system.RenewalURL;
import com.cheche365.cheche.web.service.system.SelfPaymentURL;
import com.cheche365.cheche.web.service.system.SystemUrlGenerator;
import com.cheche365.cheche.web.util.ClientTypeUtil;
import com.cheche365.cheche.web.version.VersionedResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.cheche365.cheche.core.exception.BusinessException.Code.OBJECT_NOT_EXIST;
import static com.cheche365.cheche.core.serializer.SerializerUtil.toMap;
import static com.cheche365.cheche.web.util.PaymentValidationUtil.checkBeforePay;


/**
 * Created by zhengwei on 4/1/15.
 */

@RestController
@RequestMapping("/" + ContextResource.VERSION_NO + "/orders")
@VersionedResource(from = "1.0")
public class OrdersResource extends ContextResource {

    private Logger logger = LoggerFactory.getLogger(OrdersResource.class);

    @Autowired
    private ClientOrderService clientOrderService;
    @Autowired
    private WebPurchaseOrderService webOrderService;
    @Autowired
    private WebPurchaseOrderRepository webOrderRepository;
    @Autowired
    private QuoteRecordRepository quoteRecordRepository;
    @Autowired
    private InsuranceRepository insuranceRepository;
    @Autowired
    private CompulsoryInsuranceRepository compulsoryInsuranceRepository;
    @Autowired
    private OrderCancelReasonService orderCancelReasonService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ConditionTriggerHandler conditionTriggerHandler;
    @Autowired
    private PurchaseOrderAmendRepository orderAmendRepository;
    @Autowired
    private OrderRelatedService orService;
    @Autowired
    private InitPaymentService initPaymentService;
    @Autowired
    private QuoteConfigService quoteConfigService;
    @Autowired
    private QRCodeService qrCodeService;
    @Autowired
    private SystemUrlGenerator systemUrlGenerator;
    @Autowired
    private RenewalURL renewalPage;

    @Autowired
    @Qualifier("selfPaymentURL")
    private SelfPaymentURL selfPaymentPage;
    @Autowired
    private OrderImageURL orderImagePage;

    @Autowired
    private UserCallbackService userCallbackService;

    @VersionedResource(from = "1.4")
    @RequestMapping(value = "/{orderNo}", method = RequestMethod.GET)
    public HttpEntity getOrderByNo(@PathVariable String orderNo) {

        if(RuntimeUtil.isNonAuto(orderNo)){
            return new HttpEntity(PayParams.call(orderNo));
        }
        PurchaseOrder purchaseOrder = this.webOrderService.findFirstByOrderNo(orderNo, this.currentUserWithCallback(orderNo));
        if (null == purchaseOrder) {
            throw new BusinessException(BusinessException.Code.OBJECT_NOT_EXIST, "无订单号为" + orderNo + "的订单。");
        }
        Channel channel = ClientTypeUtil.getChannel(request);
        return new HttpEntity(clientOrderService.purchaseOrderBills(purchaseOrder, channel));
    }

    @RequestMapping(value = "/{orderNo}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateOrder(@PathVariable String orderNo, @RequestBody @Valid PurchaseOrder purchaseOrder) {

        PurchaseOrder oldOrder = this.webOrderService.findFirstByOrderNo(orderNo, this.currentUser());
        if (null == oldOrder){
            throw new BusinessException(OBJECT_NOT_EXIST,  "订单号： "+orderNo+" 不存在");
        }

        PurchaseOrder afterUpdate = this.clientOrderService.update(oldOrder, purchaseOrder);

        return new ResponseEntity<>(this.toPlaceOrderResult(afterUpdate), HttpStatus.OK);

    }


    @RequestMapping(value = "/{orderNo}", method = RequestMethod.DELETE)
    public ResponseEntity<?> cancelOrder(@PathVariable String orderNo, @RequestParam(value = "reasonId", required = false) Long orderCancelReasonId) {


        PurchaseOrder order = this.webOrderRepository.findFirstByOrderNoAndTypeAndApplicant(orderNo, OrderType.Enum.INSURANCE, this.currentUser());
        if(null == order){
            throw new BusinessException(OBJECT_NOT_EXIST, "订单号： "+orderNo+" 不存在");
        }

        PurchaseOrder purchaseOrder = this.clientOrderService.cancel(order);
        //add save order cancel reason
        orderCancelReasonService.saveCancelReason(orderNo, orderCancelReasonId);

        QuoteRecord quoteRecord = this.quoteRecordRepository.findOne(purchaseOrder.getObjId());
        if (!ConditionTriggerUtil.sendMsgNotAllowed(quoteRecord)) {
            ConditionTriggerUtil.sendOrderCancelMessage(conditionTriggerHandler, purchaseOrder, quoteRecord);
        }

        RestResponseEnvelope envelope = new RestResponseEnvelope(new SimpleMessageResult().setMessage("订单已取消，" + orderNo));

        return new ResponseEntity(envelope, HttpStatus.OK);
    }

    @VersionedResource(from = "1.4", to = "1.7")
    @RequestMapping(value = "", method = RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope> getOrderList11(HttpServletRequest request,
                                                           @RequestParam(value = "status") String status,
                                                           @RequestParam(value = "page", required = false) Integer page,
                                                           @RequestParam(value = "size", required = false) Integer size) {

        Pageable pageable = new PageRequest(toPageStart(page), toPageSize(size), new Sort(Sort.Direction.DESC,"createTime"));

        List<OrderStatus> formattedStatus = OrderStatus.Enum.format(Arrays.asList(status.split(",")));

        Channel channel = ClientTypeUtil.getChannel(request);
        Page<PurchaseOrder>  orders = webOrderService.findByUserAndChannelsAndStatus(this.currentUser(), channel, formattedStatus, pageable);

        Page<Map> afterConvert = orders.map(order -> clientOrderService.purchaseOrderBills(order, channel));

        return new ResponseEntity<>(new RestResponseEnvelope(afterConvert), HttpStatus.OK);
    }

    @VersionedResource(from = "1.8")
    @RequestMapping(value = "", method = RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope> getOrderList18(HttpServletRequest request,
                                                           @RequestParam(value = "status") String status,
                                                           @RequestParam(value = "page", required = false) Integer page,
                                                           @RequestParam(value = "size", required = false) Integer size) {

        Pageable pageable = new PageRequest(toPageStart(page), toPageSize(size), new Sort(Sort.Direction.DESC,"createTime"));

        List<OrderStatus> formattedStatus = OrderStatus.Enum.format(Arrays.asList(status.split(",")));

        Channel channel = ClientTypeUtil.getChannel(request);

        Page<SimpleOrderResult>  orders = webOrderService.findSimplifiedOrders(this.currentUser(), channel, formattedStatus, pageable);

        return new ResponseEntity<>(new RestResponseEnvelope(orders), HttpStatus.OK);
    }

    @RequestMapping(value = "/{orderNo}/payment", method = RequestMethod.POST)
    public HttpEntity<RestResponseEnvelope> payOrder(HttpServletRequest request, @PathVariable String orderNo, @RequestBody @Valid PaymentWrapper payment)  {

        PaymentChannel targetChannel = PaymentChannel.Enum.toPaymentChannel(payment.getId());
        if (null == targetChannel) {
            throw new BusinessException(BusinessException.Code.OPERATION_NOT_ALLOWED, "支付渠道不能为空");
        }

        handleSmsPay(payment, orderNo);

        OrderRelatedService.OrderRelated or = null;
        if (!RuntimeUtil.isNonAuto(orderNo)) {
            or = orService.initByOrderNo(orderNo);
            if (!or.getInnerPay()){
                throw new BusinessException(BusinessException.Code.PAY_NOT_ALLOWED,"支付方式不被允许");
            }
            checkBeforePay(or, ClientTypeUtil.getChannel(request), PaymentChannel.Enum.isPingPlusPay(targetChannel), request);
        }

        Object prepayParams = initPaymentService.initPaymentParam(orderNo, or, this.safeGetCurrentUser(), request, targetChannel);
        return getResponseEntity(prepayParams);
    }


    @RequestMapping(value = "/{orderNo}/prepay", method = RequestMethod.GET)
    public ModelAndView prepay(@PathVariable String orderNo, @RequestParam(value = "uuid") String uuid) {
        logger.debug("prepay order no is {}, uuid is {}", orderNo, uuid);
        PurchaseOrder order = this.webOrderService.getFirstPurchaseOrderByNo(orderNo);
        if (null == order) {
            throw new BusinessException(BusinessException.Code.OBJECT_NOT_EXIST, orderNo + "订单号不存在。");
        }

        if (!selfPaymentPage.isCachedValue(orderNo, uuid)){
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "链接已失效");
        }

        userCallbackService.cacheUserCallbackInfo(request, uuid, orderNo, order.getApplicant(), order.getSourceChannel());

        return new ModelAndView("redirect:" + selfPaymentPage.toClientPage(order, uuid));
    }

    private PlaceOrderResult1_1 toPlaceOrderResult(PurchaseOrder purchaseOrder) {
        PlaceOrderResult1_1 result = new PlaceOrderResult1_1();
        result.setId(purchaseOrder.getId());
        result.setPurchaseOrderNo(purchaseOrder.getOrderNo());
        result.setStatus(purchaseOrder.getStatus());

        result.setCreateTime(DateUtils.getDateString(purchaseOrder.getCreateTime(), DateUtils.DATE_LONGTIME24_PATTERN));
        result.setExpireTime(DateUtils.getDateString(purchaseOrder.getExpireTime(), DateUtils.DATE_LONGTIME24_PATTERN));

        result.setInsurance(this.insuranceRepository.findByQuoteRecordId(purchaseOrder.getObjId()));
        result.setCompulsoryInsurance(this.compulsoryInsuranceRepository.findByQuoteRecordId(purchaseOrder.getObjId()));

        result.setPayableAmount(purchaseOrder.getPayableAmount());
        result.setPaidAmount(purchaseOrder.getPaidAmount());
        QuoteRecord quoteRecord=this.quoteRecordRepository.findOne(purchaseOrder.getObjId());
        result.setInnerPay(quoteConfigService.isInnerPay(quoteRecord, purchaseOrder));
        return result;
    }

    /**
     * 短信支付订单，先模拟用户登录
     */
    private void handleSmsPay(PaymentWrapper payment, String orderNo) {

        if (!payment.isSmspay()) {
            logger.debug("非短信支付订单，{}", orderNo);
            return;
        }

        logger.debug("开始处理短信支付订单，订单号: {}", orderNo);
        PurchaseOrder order = this.webOrderRepository.findFirstByOrderNo(orderNo);
        if (null == order) {
            logger.debug("短信支付订单，订单号不存在，停止处理: {}", orderNo);
            return;
        }

        if (!selfPaymentPage.isCachedValue(orderNo, payment.getUuid())) {
            throw new BusinessException(BusinessException.Code.OPERATION_NOT_ALLOWED, "订单已过期，不能支付");
        }

        CacheUtil.cacheUser(request.getSession(), order.getApplicant());
        logger.debug("短信支付订单，当前session添加user: {}", order.getApplicant().getId());
    }

    @RequestMapping(value="/count", method = RequestMethod.GET)
    public int getOrderCount(){
        doInit();
        int currentValue = (int)CacheUtil.getValueToObject(redisTemplate, WebConstants.PUT_VALUE_ORDER_COUNT);
        int newValue =  currentValue + (int)(Math.random()*10+1);
        if(newValue > currentValue){
            CacheUtil.putValue(redisTemplate, WebConstants.PUT_VALUE_ORDER_COUNT, newValue);
        }
        return Math.max(newValue,currentValue);
    }

    private void doInit(){
        if(CacheUtil.getValueToObject(redisTemplate, WebConstants.PUT_VALUE_ORDER_COUNT) == null ){
            int initNumber = 23421;
            CacheUtil.putValue(redisTemplate, WebConstants.PUT_VALUE_ORDER_COUNT, initNumber);
        }
    }


    /**
     * 【订单详情-图片上传页】链接重定向:
     * from        : /v1.3/orders/callback/detail/upload/T20160219000001?uuid=xxx
     * redirect to : /m/index.html?uuid=xxx#mine/order/T20160826000001/1
     */
    @RequestMapping(value = "/callback/detail/upload/{orderNo}", method = RequestMethod.GET)
    public ModelAndView orderDetailUploadCallback(@PathVariable("orderNo") String orderNo,
                                                    @RequestParam(value = "uuid") String uuid,
                                                    HttpServletRequest request) {

        logger.debug("callback order detail upload page,orderNo is {}, uuid is {}", orderNo, uuid);
        PurchaseOrder order = this.webOrderRepository.findFirstByOrderNo(orderNo);
        if (null == order) {
            logger.debug("订单号不存在，停止处理: {}", orderNo);
            throw new BusinessException(BusinessException.Code.OBJECT_NOT_EXIST, orderNo + "订单号不存在。");
        }
        if (!orderImagePage.isCachedValue(orderNo, uuid)) {
            logger.debug("短信图片上传uuid: {}和orderNo: {}不匹配", uuid, orderNo);
            throw new BusinessException(BusinessException.Code.NONE_VALID_SESSION, "短信图片上传链接错误");
        }

        userCallbackService.cacheUserCallbackInfo(request, uuid, orderNo, order.getApplicant(), order.getSourceChannel());
        logger.debug("短信图片上传回调链接，向当前session添加user: {}", order.getApplicant().getId());

        return new ModelAndView("redirect:" + orderImagePage.toClientPage(order, uuid));
    }

    /**
     * 根据订单号查询最新两次报价详情对比
     */
    @RequestMapping(value = "/{orderNo}/diff", method = RequestMethod.GET)
    public HttpEntity getQuoteRecordDiffByOrderNo(@PathVariable String orderNo) {

        PurchaseOrder purchaseOrder = this.webOrderService.findFirstByOrderNo(orderNo, this.currentUserWithCallback(orderNo));
        if (null == purchaseOrder) {
            throw new BusinessException(BusinessException.Code.OBJECT_NOT_EXIST, "订单号不存在， " + orderNo);
        }
        PurchaseOrderAmend orderAmend = orderAmendRepository.findLatestAmendNotFullRefundNotCancel(purchaseOrder);
        RestResponseEnvelope responseEnvelope = new RestResponseEnvelope(
            toMap(
                orderAmend,
                "id,paidAmount,payableAmount,gift,orderOperationInfo,purchaseOrder,paymentType,description,purchaseOrderAmendStatus,createTime,updateTime,purchaseOrderHistory")
        );
        return new ResponseEntity<>(responseEnvelope, HttpStatus.OK);
    }

    /**
     * 根据订单号生成支付二维码
     */
    @RequestMapping(value = "/{orderNo}/qrcode", method = RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope<Map>> generateOrderQRCode(@PathVariable String orderNo) {

        PurchaseOrder purchaseOrder = this.webOrderService.findFirstByOrderNo(orderNo, this.currentUser());
        if (null == purchaseOrder) {
            throw new BusinessException(BusinessException.Code.OBJECT_NOT_EXIST, "订单号不存在， " + orderNo);
        }
        Map<String, String> result = new HashMap<>();
        String text = systemUrlGenerator.toPaymentUrlOriginal(purchaseOrder.getOrderNo());
        result.put("qrCodePayUrl", qrCodeService.generateQRCode(text, purchaseOrder.getOrderNo()));
        return new ResponseEntity<>(new RestResponseEnvelope(result), HttpStatus.OK);
    }

    @RequestMapping(value = "/summary", method = RequestMethod.GET)
    @VersionedResource(from = "1.5")
    public HttpEntity<RestResponseEnvelope> getOrderSummaryInfo() {
        return getResponseEntity(this.webOrderService.getOrderSummaryInfo(this.currentUser(), ClientTypeUtil.getChannel(request)));
    }

    @RequestMapping(value = "/{orderNo}/renewal",method = RequestMethod.GET)
    public ModelAndView renewalOrder(@PathVariable String orderNo, @RequestParam(value = "uuid") String uuid){

        logger.info("短信链接续保参数为,orderNo:{}, uuid：{}",orderNo, uuid);

        if(!renewalPage.isCachedValue(orderNo, uuid)){
            throw new BusinessException(BusinessException.Code.OBJECT_NOT_EXIST, orderNo + "链接已失效。");
        }

        PurchaseOrder purchaseOrder = this.webOrderService.getFirstPurchaseOrderByNo(orderNo);
        if(purchaseOrder == null){
            throw new BusinessException(BusinessException.Code.OBJECT_NOT_EXIST, orderNo + "订单号不存在。");
        }

        CacheUtil.cacheUser(request.getSession(), purchaseOrder.getApplicant());

        return new ModelAndView("redirect:" + renewalPage.toClientPage(orderNo));

    }

    @RequestMapping(value = "/qr/{qrId}", method = RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope> getOrderByObjId(@PathVariable Long qrId) {
        PurchaseOrder order = webOrderRepository.findFirstByApplicantAndObjId(currentUser(), qrId);
        if (order == null) {
            throw new BusinessException(BusinessException.Code.OBJECT_NOT_EXIST, "订单不存在");
        }
        return getResponseEntity(order.getOrderNo());
    }

    @RequestMapping(value = "/{orderNo}/status", method = RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope> getStatusByOrderNo(@PathVariable String orderNo) {
        PurchaseOrder order = this.webOrderRepository.findFirstByOrderNo(orderNo);
        if (order == null) {
            throw new BusinessException(BusinessException.Code.OBJECT_NOT_EXIST, "订单不存在");
        }
        return getResponseEntity(order.getStatus());
    }
}
