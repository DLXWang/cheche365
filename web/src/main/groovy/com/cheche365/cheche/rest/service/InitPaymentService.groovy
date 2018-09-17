package com.cheche365.cheche.rest.service

import com.cheche365.cheche.core.constants.WebConstants
import com.cheche365.cheche.core.model.Payment
import com.cheche365.cheche.core.model.PaymentChannel
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.User
import com.cheche365.cheche.core.repository.ChannelRepository
import com.cheche365.cheche.core.repository.PaymentRepository
import com.cheche365.cheche.core.repository.PurchaseOrderRepository
import com.cheche365.cheche.core.service.OrderRelatedService
import com.cheche365.cheche.core.service.PaymentSerialNumberGenerator
import com.cheche365.cheche.core.util.IpUtil
import com.cheche365.cheche.core.service.WebPurchaseOrderService
import com.cheche365.cheche.core.service.spi.IPayService
import com.cheche365.cheche.core.util.RuntimeUtil
import com.cheche365.cheche.internal.integration.na.api.PayParams
import com.cheche365.cheche.web.service.PaymentCallbackURLHandler
import com.cheche365.cheche.web.service.PingPlusCnService
import com.cheche365.cheche.web.util.ClientTypeUtil
import com.cheche365.cheche.web.version.Version
import org.springframework.stereotype.Service
import javax.servlet.http.HttpSession
import javax.servlet.http.HttpServletRequest
import static com.cheche365.cheche.core.model.PaymentChannel.Enum.WECHAT_4

/**
 * Created by zhengwei on 6/20/17.
 */
@Service
class InitPaymentService {

    List<IPayService> payServices
    WebPurchaseOrderService poService
    PaymentSerialNumberGenerator serialNumberGenerator
    PurchaseOrderRepository poRepo
    ChannelRepository channelRepo
    PaymentCallbackURLHandler urlHandler
    PaymentRepository paymentRepository
    HttpSession session
    PingPlusCnService pingPlusCnService
    InitPaymentService(List<IPayService> payServices, WebPurchaseOrderService poService, PaymentSerialNumberGenerator serialNumberGenerator,
                       PurchaseOrderRepository poRepo, ChannelRepository channelRepo, PaymentCallbackURLHandler urlHandler, PaymentRepository paymentRepository,HttpSession session,PingPlusCnService pingPlusCnService) {
        this.payServices = payServices
        this.poService = poService
        this.serialNumberGenerator = serialNumberGenerator
        this.poRepo = poRepo
        this.channelRepo = channelRepo
        this.urlHandler = urlHandler
        this.paymentRepository = paymentRepository
        this.session = session
        this.pingPlusCnService = pingPlusCnService
    }

    def initPaymentParam(String orderNo, OrderRelatedService.OrderRelated or, User user, HttpServletRequest request, PaymentChannel pc) {
        def channel = ClientTypeUtil.getChannel(request)
        def inputParams
        def payment
        if(RuntimeUtil.isNonAuto(orderNo)){
            def naResponse = PayParams.call(orderNo)
            inputParams = [
                orderNo: orderNo,
                serialNumber: naResponse.serialNumber,
                amount: naResponse.amount as Double,
                channel: channelRepo.findOne(naResponse.channel as Long),
                dialogTitle: naResponse.dialogTitle
            ]

        } else {
            PurchaseOrder po = or.po
            payment = or.findPending()
            poService.updateChannelAndClientType(po, payment, channel, pc)
            if(Version.getVersion(request).compareTo(new Version("1.7")) >=0){
                serialNumberGenerator.setOutTradeNo(payment)
            }else{
                serialNumberGenerator.next(payment)
            }
            clearWechatInfo(pc, payment)
            paymentRepository.save(payment)
            Object openId = session.getAttribute(WebConstants.SESSION_KEY_WECHAT_OPEN_ID)
            inputParams = [
                orderNo           : orderNo,
                amount            : payment.amount,
                serialNumber      : WECHAT_4 == pc ? wechatSerialNumber(po, payment.outTradeNo) : payment.outTradeNo,
                channel           : channel,
                user              : user,
                dialogTitle       : "车险服务订单",
                payment           : payment,
                clientIp          : IpUtil.getIP(request),
                area              : or.qr?.area,
                quoteRecord       : or.qr,
                paymentChannel    : pc,
                openId            : openId?.toString(),
                paymentChannelName: pingPlusCnService.getPpChannelName(pc.id, channel, ClientTypeUtil.inWechat(request)),
                redirectDetailUrl : urlHandler.toServerCallbackUrl(po, request),
                frontCallBackUrl  : urlHandler.toFrontCallBackPage(po, request)
            ]
        }

        payServices.find {it.support(pc)}.prePay(inputParams)
    }



    String wechatSerialNumber(PurchaseOrder po, String original){
        poRepo.save(po.oneMoreWechatPaymentCall()).with {
            it.currentWechatOrderNo(original)
        }

    }

    static clearWechatInfo(PaymentChannel pc, Payment payment){
        if(WECHAT_4 != pc){
            payment.mchId = null
            payment.appId = null
        }

    }

}
