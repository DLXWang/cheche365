package com.cheche365.cheche.wechat

import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.Payment
import com.cheche365.cheche.core.model.PaymentChannel
import com.cheche365.cheche.core.repository.PurchaseOrderRepository
import com.cheche365.cheche.core.service.qrcode.QRCodeService
import com.cheche365.cheche.core.service.spi.IPayService
import com.cheche365.cheche.core.util.CacheUtil
import com.cheche365.cheche.web.util.ClientTypeUtil
import com.cheche365.cheche.wechat.message.UnifiedOrderResponse
import com.cheche365.cheche.wechat.model.PrePayResult
import com.cheche365.cheche.wechat.payment.OrderPaymentManager
import com.cheche365.cheche.wechat.util.Signature
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import javax.servlet.http.HttpServletRequest
import static com.cheche365.cheche.core.model.Channel.Enum.WEB_5


/**
 * Created by zhengwei on 5/11/15.
 */
@Service
class WechatPayService implements IPayService {

    private Logger logger = LoggerFactory.getLogger(WechatPayService.class);
    @Autowired
    private OrderPaymentManager paymentManager

    @Autowired
    private PurchaseOrderRepository poRepo

    @Autowired(required = false)
    private HttpServletRequest request

    @Autowired
    private QRCodeService qrCodeService

    @Override
    def prePay(Map<String, Object> params) {

        UnifiedOrderResponse response = paymentManager.unifiedOrder(
            params.user,
            params.serialNumber,
            params.amount,
            params.dialogTitle,
            params.channel
        )

        if (logger.isDebugEnabled()) {
            logger.debug("prepayid is : {}, code_url is : {}, return msg is: {}, mweb_url is: {}" + response.prepay_id, response.code_url, response.return_msg, response.mweb_url)
        }

        PrePayResult prepayResult = new PrePayResult(response, params.channel);

        if (Channel.selfApp().contains(params.channel)) {
            prepayResult.package = 'Sign=WXPay'
        } else if (ClientTypeUtil.inWechat(request)) { //包括车车公众号，第三方公众号和小程序以及在公众号打开的M站链接
            prepayResult.package = "prepay_id=" + response.getPrepay_id()
        }

        if (params.channel == WEB_5 && response.code_url) {
            prepayResult.QRImageUrl = qrCodeService.generateQRCode(response.code_url, params.orderNo)
        }

        prepayResult.paySign = Signature.getSign(params.channel, prepayResult, request)

        if (params.payment) {
            setMchIdAndAppId(params.payment, prepayResult);
        }

        logger.debug(String.format("PAY_TAG: request param [%s]", CacheUtil.doJacksonSerialize(prepayResult)))
        return prepayResult;
    }

    @Override
    def refund(Map<String, Object> params) {
        return null
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
        return PaymentChannel.Enum.WECHAT_4 == pc
    }


    void setMchIdAndAppId(Payment payment, PrePayResult prepayResult){
        payment.setMchId(prepayResult.partnerId);
        payment.setAppId(prepayResult.appId);
        paymentManager.savePaymentMchId(payment);
    }
}
