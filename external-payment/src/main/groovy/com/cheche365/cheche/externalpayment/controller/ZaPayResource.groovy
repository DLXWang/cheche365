package com.cheche365.cheche.externalpayment.controller

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.MoApplicationLog
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.repository.PaymentRepository
import com.cheche365.cheche.core.repository.PurchaseOrderRepository
import com.cheche365.cheche.core.service.DoubleDBService
import com.cheche365.cheche.externalpayment.service.ZaCallBackService
import com.cheche365.cheche.externalpayment.util.ZaSignUtil
import com.cheche365.cheche.web.ContextResource
import com.cheche365.cheche.web.service.PaymentCallbackURLHandler
import groovy.json.JsonSlurper
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest

import static com.cheche365.cheche.core.service.PaymentSerialNumberGenerator.getPurchaseNo
import static com.cheche365.cheche.externalpayment.constants.ZaCashierConstant.APP_KEY

/**
 * Created by wenling on 2017/11/27.
 */
@Controller
@RequestMapping("/web/zapay")
public class ZaPayResource extends ContextResource {

    private Logger logger = LoggerFactory.getLogger(ZaPayResource.class);

    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private PaymentCallbackURLHandler paymentCallbackURLHandler;
    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;
    @Autowired
    private DoubleDBService doubleDBService;
    @Autowired
    ZaCallBackService callBackService;


    @RequestMapping(value = "/back/front")
    public ModelAndView frontCall(HttpServletRequest req) throws UnsupportedEncodingException {
        logger.info("接收众安支付前台通知报文开始...");
        String redirectUrl;
        req.setCharacterEncoding("ISO-8859-1");
        Map<String, String> respParam = this.getAllRequestParam(req);
        String orderNo = respParam.get("out_trade_no");

        PurchaseOrder order = paymentRepository.findByOutTradeNo(orderNo)?.purchaseOrder
        try {
            logger.info("众安支付前台通知报文参数 -> {}", respParam.toString());
            if (respParam.isEmpty() || StringUtils.isBlank(getPurchaseNo(orderNo)))
                throw new RuntimeException("众安支付前台通知报文为空或没有orderId参数");

            if (null == order)
                throw new BusinessException(BusinessException.Code.OBJECT_NOT_EXIST, "订单" + order.orderNo + "不存在");

            if (!ZaSignUtil.verify(respParam, APP_KEY)) {
                saveLog(order.orderNo, "众安支付前台通知报文验签失败");
                throw new RuntimeException("众安支付前台通知报文验签失败");
            }

            if (respParam.get("pay_result") == "S") {
                redirectUrl = paymentCallbackURLHandler.toFrontCallBackPage(order, req);
            } else {
                redirectUrl = paymentCallbackURLHandler.toFrontCallBackPage(order, req);
            }
        } catch (Exception ex) {
            logger.error("接收众安支付前台通知报文异常", ex);
            redirectUrl = paymentCallbackURLHandler.toFrontCallBackPage(order, req);
        }

        logger.info("众安前台通知报文处理完成跳转页面url: {}", redirectUrl);
        return new ModelAndView("redirect:" + redirectUrl);
    }

    @RequestMapping(value = "/back/order")
    @ResponseBody
    public String backOrder( HttpServletRequest request) throws UnsupportedEncodingException {
        def req=getRequestParam(request)
        logger.info("众安保单推送通知报文原始参数 -> {}", req);
        return callBackService.callbackOrder(req);
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

    public void saveLog(String orderId, String msg){
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findFirstByOrderNo(orderId);
        MoApplicationLog log = MoApplicationLog.applicationLogByPurchaseOrder(purchaseOrder);
        log.setLogMessage(msg);
        doubleDBService.saveApplicationLog(log);
    }


    private Map getRequestParam(final HttpServletRequest request) {
        BufferedReader  reader = new BufferedReader(new InputStreamReader(request.getInputStream(), "utf-8"))
        String line = ""
        StringBuilder sb = new StringBuilder()
        while ((line = reader.readLine()) != null)
        {
            sb.append(line)
        }
        return  new JsonSlurper().parseText(sb.toString())
    }
}
