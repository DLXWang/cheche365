package com.cheche365.cheche.externalpayment.service

import com.cheche365.cheche.common.util.DoubleUtils
import com.cheche365.cheche.core.model.Payment
import com.cheche365.cheche.core.model.PaymentChannel
import com.cheche365.cheche.core.repository.PaymentRepository
import com.cheche365.cheche.core.service.qrcode.QRCodeService
import com.cheche365.cheche.core.service.spi.IPayService
import com.cheche365.cheche.externalpayment.constants.PingPlusConstant
import com.pingplusplus.Pingpp
import com.pingplusplus.model.Charge
import com.pingplusplus.model.Order
import com.pingplusplus.model.OrderRefund
import com.pingplusplus.model.OrderRefundCollection
import com.pingplusplus.model.Refund
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

import static com.cheche365.cheche.core.model.Channel.Enum.WEB_5

@Component
@Slf4j
class PingPlusService implements IPayService{

    @Autowired
    private PingPlusRoyaltyService pingPlusRoyaltyService
    @Autowired
    private PaymentRepository paymentRepo
    @Autowired
    private QRCodeService qrCodeService

    @PostConstruct
    private void init(){
        Pingpp.apiKey = PingPlusConstant.APP_KEY
        Pingpp.privateKey = PingPlusConstant.PRIVATE_KEY
    }

    private Order createOrder(Map<String, Object> params){
        Map<String, Object> createParams = [
            app              : PingPlusConstant.APP_ID,
            merchant_order_no: params.serialNumber,
            amount           : DoubleUtils.displayDoubleValue(params.amount * 100).intValue(),
            currency         : "cny",
            client_ip        : params.clientIp,
            subject          : params.dialogTitle,
            body             : params.dialogTitle,
            royalty_users    : pingPlusRoyaltyService.calculateRoyalty(params, true)
        ]
        log.info("create order params:{}",createParams)
        Order.create(createParams)
    }

    private Order payOrder(Order order, Map<String, Object> params) {

        Map<String,Object> payParams = [
            charge_amount : order.actualAmount,
            channel : params.paymentChannelName,
            extra : getExtraByChannel(params)
        ]

        log.info("pay order params:{}",payParams)
        Order.pay(order.id, payParams)
    }

    private Charge createCharge(Map<String, Object> params) {
        Map<String, Object> chargeParams = [
            order_no         : params.serialNumber,
            amount           : DoubleUtils.displayDoubleValue(params.amount * 100).intValue(),
            app              : [id: PingPlusConstant.APP_ID],
            channel          : 'wx_pub_qr',
            currency         : 'cny',
            client_ip        : params.clientIp,
            subject          : params.dialogTitle,
            body             : params.dialogTitle,
            extra            : [product_id: params.serialNumber]
        ]

        log.info("create charge params:{}", chargeParams)
        Charge.create(chargeParams)
    }

    private String getQrCodeUrl(Map<String, Object> params) {
        def charge = createCharge(params)
        def codeUrl = charge.credential.wx_pub_qr
        qrCodeService.generateQRCode(codeUrl, params.orderNo)
    }

    private Map<String,Object> getExtraByChannel(Map<String, Object> params){
        Map<String,Object> extra = [:]
        if(params.paymentChannelName == "alipay_wap"){
            extra.success_url = params.redirectDetailUrl
            extra.cancel_url = params.redirectDetailUrl
        }else if(params.paymentChannelName == "wx_pub"){
            extra.open_id = params.openId
        }else if(params.paymentChannelName == "wx_lite"){
            extra.open_id = params.openId
        }else if(params.paymentChannelName == "wx_wap"){
            extra.result_url = params.redirectDetailUrl
        }else if(params.paymentChannelName == "upacp_wap"){//银联不能使用重定向链接，无法跳回车车页面
            extra.result_url = params.frontCallBackUrl
        }else if(params.paymentChannelName == "upacp_pc"){
            extra.result_url = params.frontCallBackUrl
        }else if(params.paymentChannelName == "bfb_wap"){
            extra.result_url = params.redirectDetailUrl
            extra.bfb_login = Boolean.TRUE
        }
        extra
    }

    @Override
    def prePay(Map<String, Object> params) {
        if (params.paymentChannelName == "wx_pub_qr") {
            return [qrCodeUrl: getQrCodeUrl(params)]
        }

        Order order = createOrder(params)
        order = payOrder(order,params)

        Payment payment = params.payment
        payment.thirdpartyPaymentNo = order.charge
        payment.itpNo = order.id
        paymentRepo.save(payment)

        order?.toString()
    }

    @Override
    def refund(Map<String, Object> params) {
        Payment refundPayment = params.payment
        Payment paidPayment = refundPayment.upstreamId
        Boolean webScanPay = (WEB_5 == paidPayment.clientType && !paidPayment.itpNo)
        def thirdPartyPaymentNo = webScanPay ? chargeRefund(params) : orderRefund(params)
        if (thirdPartyPaymentNo) {
            refundPayment.thirdpartyPaymentNo = thirdPartyPaymentNo
            paymentRepo.save(refundPayment)
        }
        return true
    }

    def orderRefund(Map<String, Object> params) {
        Payment refundPayment = params.payment
        Payment paidPayment = refundPayment.upstreamId
        params.area = paidPayment.purchaseOrder.area
        params.amount = refundPayment.amount

        Map<String, Object> refundParams = [
            charge       : paidPayment.thirdpartyPaymentNo,
            charge_amount: DoubleUtils.displayDoubleValue(refundPayment.amount * 100).intValue(),
            description  : "车险服务退款",
            refund_mode  : "to_source",
            royalty_users: pingPlusRoyaltyService.calculateRoyalty(params, false)
        ]
        log.info("refund order params:{}", refundParams)

        OrderRefundCollection orderRefundCollection = OrderRefund.create(paidPayment.itpNo, refundParams)
        orderRefundCollection?.data?.get(0)?.id
    }

    def chargeRefund(Map<String, Object> params) {
        Payment refundPayment = params.payment
        Payment paidPayment = refundPayment.upstreamId

        Charge chargeRefund = Charge.retrieve(paidPayment.thirdpartyPaymentNo)

        Map<String, Object> refundParams = [
            amount     : DoubleUtils.displayDoubleValue(refundPayment.amount * 100).intValue(),
            description: "车险服务退款"
        ]
        log.info("refund charge params:{}", refundParams)

        Refund refund = chargeRefund.getRefunds().create(refundParams)
        refund?.id
    }

    @Override
    def syncCallback(Map<String, Object> params) {
        return null
    }

    @Override
    def asyncCallback(Map<String, Object> params) {
        return null
    }

    @Override
    boolean support(PaymentChannel pc) {
        return PaymentChannel.Enum.isPingPlusPay(pc)
    }
}
