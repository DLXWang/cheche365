package com.cheche365.cheche.rest.web.controller;

import com.cheche365.cheche.alipay.AliPayHandler;
import com.cheche365.cheche.alipay.AlipayService;
import com.cheche365.cheche.alipay.util.AliPayConstant;
import com.cheche365.cheche.alipay.util.AlipayCore;
import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.model.PaymentChannel;
import com.cheche365.cheche.core.model.PurchaseOrder;
import com.cheche365.cheche.core.util.RuntimeUtil;
import com.cheche365.cheche.internal.integration.na.NACallbackService;
import com.cheche365.cheche.web.service.PaymentCallbackURLHandler;
import com.cheche365.cheche.web.util.ClientTypeUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static com.cheche365.cheche.core.service.PaymentSerialNumberGenerator.getPurchaseNo;

/**
 * Created by chenxiaozhe on 15-8-17.
 */
@Controller
@RequestMapping("/web/alipay")
public class AliPayResource extends AliPayBaseResource {

    @Autowired
    private AlipayService aliPayHandlerBuilder;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private PaymentCallbackURLHandler paymentCallbackURLHandler;

    @Autowired
    protected AlipayCore alipayCore;


    @RequestMapping(value = "/iospay/notify", method = RequestMethod.POST)
    public void iosPayNotify(HttpServletRequest request, HttpServletResponse response) {
        super.payNotify(request, response, Channel.Enum.IOS_4);
    }

    @RequestMapping(value = "/wappay/notify", method = RequestMethod.POST)
    public void wapPayNotify(HttpServletRequest request, HttpServletResponse response) {
        String out_trade_no = alipayCore.getParameter(request, "out_trade_no");
        if(StringUtils.isNotEmpty(out_trade_no)) {
            super.payNotify(request, response, Channel.Enum.WAP_8);
        }else{
            super.refundNotify(request, response, Channel.Enum.WAP_8);
        }
    }

    @RequestMapping(value = "/directpay/return", method = RequestMethod.GET)
    public ModelAndView directPayReturn(HttpServletRequest request) {
        String debugWord = "";
        //验证结构
        boolean verifyRes = false;
        //订单号
        String outTradeNo = "";
        //获取支付宝GET过来反馈信息
        Map<String, String> params = alipayCore.convertRequestParamToMap(request, AliPayConstant.INPUT_CHARSET, false);
        //商户订单号
        outTradeNo = alipayCore.getParameter(request, "out_trade_no");

        if(RuntimeUtil.isNonAuto(outTradeNo)){
            logger.info("非车支付宝同步回调开始, {}", outTradeNo);
            String naRedirectUrl = NACallbackService.syncCallback(PaymentChannel.Enum.ALIPAY_1, params);
            logger.info("非车跳转链接 {}", naRedirectUrl);
            return new ModelAndView("redirect:" + naRedirectUrl);
        }

        PurchaseOrder purchaseOrder = purchaseOrderService.getFirstPurchaseOrderByNo(getPurchaseNo(outTradeNo));
        //支付宝交易号
        String tradeNo = alipayCore.getParameter(request, "trade_no");
        alipayCore.logResult(outTradeNo, params.toString());
        alipayCore.logResult(outTradeNo, params.toString());
        //计算得出通知验证结果
        AliPayHandler aliPayHandler = aliPayHandlerBuilder.findByChannel(ClientTypeUtil.getChannel(request));
        boolean verifyResult = aliPayHandler.verify(params, outTradeNo);
        if (verifyResult) {//验证成功
            debugWord = String.format("支付宝交易单号[%s]同步回调验证成功,订单号[%s]=====success=====", tradeNo, outTradeNo);
            verifyRes = true;
        } else {
            debugWord = String.format("支付宝交易单号[%s]同步回调验证失败,订单号[%s]=====fail=====", tradeNo, outTradeNo);
        }
        alipayCore.logResult(outTradeNo, debugWord);

        String redirectUrl;
        if(verifyRes){
            redirectUrl = paymentCallbackURLHandler.toFrontCallBackPage(purchaseOrder, request);
        } else {
            redirectUrl = paymentCallbackURLHandler.toFrontCallBackPage(purchaseOrder, request);
        }

        logger.info("支付宝回调跳转页面url: {}", redirectUrl);
        return new ModelAndView("redirect:" + redirectUrl);
    }

}
