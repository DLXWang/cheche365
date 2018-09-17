package com.cheche365.cheche.rest.web.controller

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.repository.CompulsoryInsuranceRepository
import com.cheche365.cheche.core.repository.InsuranceRepository
import com.cheche365.cheche.core.repository.PaymentRepository
import com.cheche365.cheche.core.repository.PurchaseOrderRepository
import com.cheche365.cheche.externalpayment.service.SinosafeCallbackService
import com.cheche365.cheche.externalpayment.util.RequestUtil
import com.cheche365.cheche.web.ContextResource
import com.cheche365.cheche.web.response.RestResponseEnvelope
import com.cheche365.cheche.web.service.PaymentCallbackURLHandler
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest

/**
 * 华安支付回调
 * Created by Administrator on 2018/1/9.
 */
@Controller
@RequestMapping(path = ['/web/SinosafePay', '/web/sinosafepay'])
class SinosafePayResource extends ContextResource {

    private Logger logger = LoggerFactory.getLogger(SinosafePayResource.class);
    @Autowired
    PurchaseOrderRepository poRepo;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private PaymentCallbackURLHandler paymentCallbackURLHandler;
    @Autowired
    SinosafeCallbackService sinosafeCallbackService;
    @Autowired
    CompulsoryInsuranceRepository ciRepo;
    @Autowired
    InsuranceRepository iRepo

    @RequestMapping(value = "/back/front/{orderNo}")
    public ModelAndView frontCall(HttpServletRequest request,@PathVariable String orderNo) throws UnsupportedEncodingException {
        logger.info("接收华安支付前台通知报文开始...");
        logger.info("华安回调参数:${orderNo}")
        String redirectUrl;
        PurchaseOrder order= poRepo.findFirstByOrderNo(orderNo)
        try {
            if ( StringUtils.isBlank(orderNo))
                throw new RuntimeException("华安支付前台通知报文为空或没有orderNo参数");

            if (null == order)
                throw new BusinessException(BusinessException.Code.OBJECT_NOT_EXIST, "订单" + orderNo + "不存在");

            redirectUrl = paymentCallbackURLHandler.toFrontCallBackPage(order, request);

        } catch (Exception ex) {
            logger.error("接收华安支付前台通知报文异常", ex);
            redirectUrl = paymentCallbackURLHandler.toFrontCallBackPage(order, request);
        }

        logger.info("华安前台通知报文处理完成跳转页面url: {}", redirectUrl);
        return new ModelAndView("redirect:" + redirectUrl);
    }

    @RequestMapping(value = "/back/insurance",method = RequestMethod.GET)
    public ResponseEntity insurance(HttpServletRequest request) throws UnsupportedEncodingException {
        def params=RequestUtil.getAllRequestParam(request)
        if(params.isEmpty()){
            logger.info("接收华安人工核保回调空报文:${params}，忽略");
            return
        }

        logger.info("接收华安人工核保回调报文参数:${params}");
        sinosafeCallbackService.insurance(params)

        return new ResponseEntity<>(new RestResponseEnvelope(["payload": ["code": 200]]), HttpStatus.OK);
    }

}
