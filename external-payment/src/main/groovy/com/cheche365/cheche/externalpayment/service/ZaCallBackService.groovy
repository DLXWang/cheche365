package com.cheche365.cheche.externalpayment.service

import com.cheche365.cheche.common.util.DateUtils
import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.CompulsoryInsurance
import com.cheche365.cheche.core.model.Insurance
import com.cheche365.cheche.core.model.OrderStatus
import com.cheche365.cheche.core.model.OrderSubStatus
import com.cheche365.cheche.core.model.OrderTransmissionStatus
import com.cheche365.cheche.core.model.Payment
import com.cheche365.cheche.core.model.PaymentChannel
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.repository.CompulsoryInsuranceRepository
import com.cheche365.cheche.core.repository.InsuranceRepository
import com.cheche365.cheche.core.repository.PaymentRepository
import com.cheche365.cheche.core.repository.PurchaseOrderRepository
import com.cheche365.cheche.core.service.OrderAttributeService
import com.cheche365.cheche.core.service.OrderOperationInfoService
import com.cheche365.cheche.core.service.PurchaseOrderService
import com.cheche365.cheche.core.service.ThirdServiceFailService
import com.cheche365.cheche.core.service.callback.AbstractCallBackService
import com.cheche365.cheche.core.service.callback.CallBackType
import com.cheche365.cheche.core.service.callback.ICallBackService

import com.cheche365.cheche.externalpayment.util.ZaSignUtil
import com.cheche365.cheche.zhongan.service.ZhonganService
import com.zhongan.scorpoin.signature.SignatureUtils
import groovy.json.JsonOutput
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.exception.ExceptionUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession
import java.text.SimpleDateFormat
import static com.cheche365.cheche.externalpayment.constants.ZaCashierConstant.*

@Service
class ZaCallBackService extends AbstractCallBackService implements ICallBackService{

    private final Logger logger = LoggerFactory.getLogger(ZaCallBackService.class);

    @Autowired
    ZhonganService zhonganService;
    @Autowired
    private ThirdServiceFailService thirdServiceFailService
    @Autowired
    CompulsoryInsuranceRepository ciRepo;
    @Autowired
    InsuranceRepository iRepo;
    @Autowired
    private OrderOperationInfoService orderOperationInfoService
    @Autowired
    private PurchaseOrderService orderService;
    @Autowired
    private PurchaseOrderRepository porRepo
    @Autowired
    private PaymentRepository payRepo;
    @Autowired(required = false)
    private HttpSession session
    @Autowired
    OrderAttributeService orderAttributeService

    @Override
    String callBack(HttpServletRequest request, CallBackType callBackType) {
        return super.execute(getParams(request),callBackType,PaymentChannel.Enum.ZA_PAY_20,null)
    }

    private  Map<String, String> getParams(HttpServletRequest request){
        Map<String, String[]> requestParams = request.getParameterMap();
        logger.info("众安支付异步通知原始参数: ${requestParams}")
        Map<String, String> requestMap = new HashMap<String, String>();
        for (String key : requestParams.keySet()) {
            if (requestParams.get(key).length == 0) {
                requestMap.put(key, "");
            } else {
                requestMap.put(key, URLDecoder.decode(requestParams.get(key)[0],CHARSET));
            }
        }
        requestMap
    }
    @Override
    boolean verify(Map<String, String> params) {
        return ZaSignUtil.verify(params, APP_KEY)
    }

    @Override
    String getOutTradeNo(Map<String, String> params) {
        return params.out_trade_no
    }

    @Override
    String getThirdpartyTradeNo(Map<String, String> params) {
        return params.trade_no
    }

    @Override
    boolean isSuccess(Map<String, String> params) {
        return params.get("pay_result").equals("S")
    }

    @Override
    boolean isOrder(){
        return true;
    }


    @Override
    String result(boolean isSuccess) {
        if(isSuccess){
            return "success"
        }else{
            return "fail"
        }
    }

    @Override
    boolean support(PaymentChannel pc) {
        return pc == PaymentChannel.Enum.ZA_PAY_20
    }

    @Override
    String getItpNo(Map<String, String> params) {
        return params.za_order_no
    }

    @Override
    void order(PurchaseOrder order, Insurance insurance, CompulsoryInsurance compulsoryInsurance, Payment payment, Map params) {

        try {
            Map additional = toOrderParams(payment)
            zhonganService.order(
                order,
                insurance,
                compulsoryInsurance,
                additional
            )

            def response=additional?.result
            logger.debug("调用众安承保服务接口返回参数,订单号:{},参数:{}", order.getOrderNo(),response)

            def businessPolicyStatus=response?.businessPolicyStatus //商业险
            def compelPolicyStatus=response?.compelPolicyStatus //交强险

            def notNullStatus = [businessPolicyStatus,compelPolicyStatus].findAll {it}
            if(!notNullStatus){
                logger.debug("众安承保回调异常，两张保单状态都为空，不做处理")
                return
            }

            insurance?.policyNo= response?.businessPolicyNo
            compulsoryInsurance?.policyNo= response?.compelPolicyNo
            logger.debug("订单号:{},商业险出单状态:{},交强险出单状态:{},订单状态:{},订单子状态:{}", order.getOrderNo(),businessPolicyStatus,compelPolicyStatus,order?.status?.status,order?.subStatus?.name)

            updateOrder(insurance,compulsoryInsurance)
            updateStatus(notNullStatus,order)

        }catch (BusinessException e) {
            logger.error("调用众安承保服务失败,订单号:{},交易号：{}, errorMessage:{}", order.getOrderNo(), e.errorObject.result.requestNo,e.errorObject.result.resultMessage)
            thirdServiceFailService.saveThirdServiceFail(insurance.getInsuranceCompany().getId(), order.getId(), e.errorObject.result.resultMessage)
        }
    }

    @Transactional
    def syncOrderOperationInfo(PurchaseOrder order){
        porRepo.save(order)
        OrderTransmissionStatus newStatus=ORDER_STATUS_MAPPING.get(order.status.id as String)
        orderOperationInfoService.updateOrderTransmissionStatus(order,newStatus)
        logger.debug("众安承保完成后修改订单和出单状态,订单号:{},订单状态:{},出单中心状态:{}", order.getOrderNo(),order?.status?.status,newStatus?.status);
    }

    Map toOrderParams(Payment payment) {
        [
            payTradeNo : payment.thirdpartyPaymentNo,
            outTradeNo : payment.outTradeNo,
            tradeNo : payment.itpNo
        ]
    }

    void updateStatus(notNullStatus, PurchaseOrder order){

        if(notNullStatus.any {ZA_FAILED_STATUS.contains(it)}){
            order.subStatus = OrderSubStatus.Enum.FAILED_1
            porRepo.save(order)
            logger.debug('众安承保回调收到出单失败状态， 众安状态码 {}, 车车订单状态 {},子状态 {}',notNullStatus, order.status.id,order?.orderSubStatus?.id)
        } else if (notNullStatus.every {ZA_SUCCESS_STATUS.contains(it)}){
            orderOperationInfoService.updatePurchaseOrderStatusForServiceSuccess(order)
            logger.debug('众安承保回调收到出单成功状态， 众安状态码 {}, 车车订单状态 {}',notNullStatus, order.status.id)
        } else {
            if(notNullStatus.any {ORDER_CALLBACK_IGNORE_STATUS.contains(it)}){
                logger.debug('众安承保回调收到可忽略状态， 众安状态码 {}, 车车订单状态 {}',notNullStatus, order.status.id)
            } else if(notNullStatus.any {ZA_TO_CHECHE_ORDER_STATUS.keySet().contains(it)}) {
                def endStatus = notNullStatus.find {!ZA_SUCCESS_STATUS.contains(it)}
                order.status = ZA_TO_CHECHE_ORDER_STATUS.get(endStatus)

                syncOrderOperationInfo(order)
                logger.debug('众安承保回调收到终结状态， 众安状态码 {}, 车车订单状态 {}',notNullStatus, order.status.id)
            }else{
                logger.debug('众安承保回调收到非预期状态， 众安状态码 {}, 车车订单状态 {}',notNullStatus, order.status.id)
            }
        }
    }

    def updateOrder(insurance,compulsoryInsurance){
        if(insurance){
            insurance.updateTime=new Date()
            iRepo.save(insurance);
            logger.debug("save insurance success")
        }
        if(compulsoryInsurance){
            compulsoryInsurance.updateTime=new Date()
            ciRepo.save(compulsoryInsurance)
            logger.debug("save compulsoryInsurance success")
        }
    }



    def callbackOrder(Map callbackParams)throws Exception{
        def success
        try {
            def decryptedParams=decrypt(callbackParams)
            logger.info("众安保单推送通知报文明文参数 -> {}", decryptedParams);

            String callBackOrderNo = decryptedParams.orderNo ?: decryptedParams.vehiclePolicyOrderNo  //众安文档和测试环境对这个字段描述不一致，文档叫orderNo， 测试环境叫vehiclePolicyOrderNo
            if (StringUtils.isBlank(callBackOrderNo)){
                logger.error("众安保单推送通知报文没有orderNo参数");
                throw new BusinessException(BusinessException.Code.EXTERNAL_SERVICE_ERROR,"众安保单推送通知报文没有orderNo参数");
            }

            Payment payment=payRepo.findFirstByOutTradeNo(callBackOrderNo)
            if (!payment)
                throw new BusinessException(BusinessException.Code.OBJECT_NOT_EXIST, "订单" + callBackOrderNo + "不存在,该笔订单可能是众安批改订单");

            PurchaseOrder order = payment.purchaseOrder
            if(OrderStatus.Enum.PAID_3 == order.status || OrderStatus.Enum.FINISHED_5 == order.status){
                logger.info("众安推送的订单状态为预期中状态，订单号:${order.orderNo},订单状态:${order.status.status}")
                def afterInsurance=updateInsurance(decryptedParams,order)

                if(OrderStatus.Enum.PAID_3 == order.status ){
                    def insurance=[afterInsurance?.insurance,afterInsurance?.compulsoryInsurance].findAll {it}
                    def policyNos=[afterInsurance?.insurance?.policyNo,afterInsurance?.compulsoryInsurance?.policyNo].findAll {it}
                    if(insurance.size()==policyNos.size()){
                        logger.info("众安推送的订单状态为出单中，需同步出单中心状态")
                        orderOperationInfoService.updatePurchaseOrderStatusForServiceSuccess(order)
                    }
                    logger.info("订单状态更新完毕,订单号:${order.orderNo},订单状态为:${order.status.status}")
                }else {
                    logger.info("众安推送的订单状态为:${order.status.id},此次不做处理!")
                }


            }else {
                logger.info("众安推送的订单状态为:${order.status.id},此次不做处理!")
            }
            success="0"

        } catch (Exception ex) {
            logger.error("接收众安保单推送通知异常{}",ExceptionUtils.getStackTrace(ex) );
            success="1"
        }finally{
            return JsonOutput.toJson([result:[success:success, message:""]])
        }
    }

    Map decrypt(Map req){
        try{
            def privateKey=zhonganService.env.getProperty( 'zhongan.private_key','')
            return req.collectEntries {
                [(it.key): it.value ? SignatureUtils.rsaDecrypt(it.value, privateKey, "UTF-8") : '']
            }

        }catch (Exception e){
            logger.error("众安保单推送通知报文解密失败:${ExceptionUtils.getStackTrace(e)}")
            throw new BusinessException(BusinessException.Code.EXTERNAL_SERVICE_ERROR,"众安保单推送通知报文解密失败");
        }

    }

    def updateInsurance(Map decryptedParams,PurchaseOrder order){
        Insurance insurance=iRepo.findByQuoteRecordId(order.objId)

        SimpleDateFormat sdf=new SimpleDateFormat(DateUtils.DATE_SHORTDATE_PATTERN)
        if(insurance && decryptedParams.businessPolicyNo) {
            insurance?.policyNo = decryptedParams.businessPolicyNo
            insurance?.effectiveDate = decryptedParams.vehiclePolicyEffectiveDate ? sdf.parse(decryptedParams.vehiclePolicyEffectiveDate) : null
            insurance?.expireDate = decryptedParams.vehiclePolicyExpiryDate ? sdf.parse(decryptedParams.vehiclePolicyExpiryDate) : null
        }

        CompulsoryInsurance compulsoryInsurance=ciRepo.findByQuoteRecordId(order.objId)
        if(compulsoryInsurance && decryptedParams.compelPolicyNo){
            compulsoryInsurance?.policyNo=decryptedParams.compelPolicyNo
            compulsoryInsurance?.effectiveDate=decryptedParams.compelEffectiveDate ? sdf.parse(decryptedParams.compelEffectiveDate) : null
            compulsoryInsurance?.expireDate=decryptedParams.compelExpiryDate ? sdf.parse(decryptedParams.compelExpiryDate) : null
        }

        updateOrder(insurance,compulsoryInsurance)
        [insurance:insurance,compulsoryInsurance:compulsoryInsurance]
    }

    @Override
    void publish(Payment payment){
        logger.debug("众安支付订单不做同步第三方渠道和出单中心的处理")
    }
}
