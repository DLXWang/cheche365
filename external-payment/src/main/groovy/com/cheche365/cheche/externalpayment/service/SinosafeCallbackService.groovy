package com.cheche365.cheche.externalpayment.service

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.*
import com.cheche365.cheche.core.mongodb.repository.MoApplicationLogRepository
import com.cheche365.cheche.core.repository.CompulsoryInsuranceRepository
import com.cheche365.cheche.core.repository.InsuranceRepository
import com.cheche365.cheche.core.repository.PurchaseOrderRepository
import com.cheche365.cheche.core.service.OrderOperationInfoService
import com.cheche365.cheche.core.service.PurchaseOrderImageService
import com.cheche365.cheche.core.service.ThirdServiceFailService
import com.cheche365.cheche.core.service.callback.AbstractCallBackService
import com.cheche365.cheche.core.service.callback.CallBackType
import com.cheche365.cheche.core.service.callback.ICallBackService
import com.cheche365.cheche.externalapi.model.sinosafe.SinosafeQueryResponse
import com.cheche365.cheche.externalapi.api.sinosafe.SinosafeQueryAPI
import com.cheche365.cheche.externalpayment.util.RequestUtil
import com.cheche365.cheche.web.service.security.throttle.APICallLimitChecker
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

import javax.servlet.http.HttpServletRequest

import static com.cheche365.cheche.core.model.OrderStatus.Enum.INSURE_FAILURE_7
import static com.cheche365.cheche.core.model.OrderStatus.Enum.PENDING_PAYMENT_1
import static com.cheche365.cheche.externalpayment.constants.SinosafeConstant.*
import static com.cheche365.cheche.web.service.security.throttle.APICallLimitChecker.API_ID_SINOSAFE_INSURE_CALLBACK


@Component
class SinosafeCallbackService extends AbstractCallBackService implements ICallBackService{

    private final Logger logger = LoggerFactory.getLogger(SinosafeCallbackService.class);
    @Autowired
    private SinosafeQueryAPI queryStateHandler
    @Autowired
    private OrderOperationInfoService orderOperationInfoService
    @Autowired
    private PurchaseOrderRepository porRepo
    @Autowired
    private MoApplicationLogRepository logRepository
    @Autowired
    private InsuranceRepository iRepo
    @Autowired
    private CompulsoryInsuranceRepository ciRepo
    @Autowired
    private ThirdServiceFailService thirdServiceFailService
    @Autowired
    private APICallLimitChecker limitChecker
    @Autowired
    private PurchaseOrderImageService poiService
    
    @Override
    String callBack(HttpServletRequest request, CallBackType callBackType) {
        def req = RequestUtil.getAllRequestParam(request)
        logger.info("华安支付回调原始参数 ${req}")
        return super.execute(req,callBackType,PaymentChannel.Enum.SINOSAFE_PAY_49,null)
    }

    @Override
    void order(PurchaseOrder order, Insurance insurance, CompulsoryInsurance compulsoryInsurance, Payment payment,Map params) {

            SinosafeQueryResponse result = queryStateHandler.call(insurance,compulsoryInsurance)
            if(result.success()){

                if(result.containsInsurance()){
                    logger.debug('回调报文包含商业险内容，合并商业险纪录')
                    result.mergeInsurance(insurance)
                }

                if(result.containsCompulsoryInsurance()){
                    logger.debug('回调报文包含交强险内容，合并交强险纪录')
                    result.mergeCompulsoryInsurance(compulsoryInsurance)
                }

                order.statusDisplay=null
                if(result.everyBillSuccess() && order.status == OrderStatus.Enum.PAID_3){
                    updateInsurance(insurance, compulsoryInsurance)
                    orderOperationInfoService.updatePurchaseOrderStatusForServiceSuccess(order)
                    logger.debug("华安保单查询返回已承保状态， 车车订单号 : ${order.orderNo}, 订单状态 : ${order.status.id}")
                } else {
                    order.subStatus = OrderSubStatus.Enum.FAILED_1
                    syncPurchaseOrderStatus(order)
                    logger.debug("华安保单查询返回非预期承保状态，车车订单号 : ${order.orderNo},订单状态 ${ order.status.id}")
                }

            }else{
                logger.error("华安保单查询接口失败 : ${result.errorMessage()} ,订单号 : ${order.orderNo} ")
                thirdServiceFailService.saveThirdServiceFail(insurance.getInsuranceCompany().getId(), order.getId(), result.errorMessage())
                throw new BusinessException(BusinessException.Code.PAY_NOT_ALLOWED,'华安保单查询接口失败 ')
            }

    }

    @Transactional
    def syncPurchaseOrderStatus(order){
        porRepo.save(order)
        syncOrderOperationInfo(order)
    }

    @Transactional
    def updateInsurance(insurance,compulsoryInsurance){
        if(insurance){
            iRepo.save(insurance);
            logger.debug("save insurance success")
        }
        if(compulsoryInsurance){
            ciRepo.save(compulsoryInsurance)
            logger.debug("save compulsoryInsurance success")
        }
    }

    void insurance(Map req){

        String reqInString = req.collect {"${it.key}:${it.value}"}.join('_')
        if(limitChecker.meetLimit(API_ID_SINOSAFE_INSURE_CALLBACK, reqInString)){
            logger.debug('华安回调达到上限, 回调内容: {}', reqInString)
            return
        }
        limitChecker.oneMore(API_ID_SINOSAFE_INSURE_CALLBACK, reqInString)


        Insurance insurance
        CompulsoryInsurance compulsoryInsurance
        PurchaseOrder order

        if(req?.sy_ply_app_no){
            insurance = iRepo.findByProposalNo(req.sy_ply_app_no)
            if(insurance && req?.sy_udr_mrk){
                insurance.insuranceStatus = toCheCheStatus(req.sy_udr_mrk)
                insurance.updateTime=new Date()
                iRepo.save(insurance)
            }
        }
        if(req?.jq_ply_app_no){
            compulsoryInsurance = ciRepo.findByProposalNo(req.jq_ply_app_no)
            if(compulsoryInsurance && req?.jq_udr_mrk){
                compulsoryInsurance.ciStatus = toCheCheStatus(req.jq_udr_mrk)
                compulsoryInsurance.updateTime=new Date()
                ciRepo.save(compulsoryInsurance)
            }
        }

        if(insurance ||  compulsoryInsurance){
            order = insurance ? porRepo.findByQuoteRecordId(insurance.quoteRecord.id) : porRepo.findByQuoteRecordId(compulsoryInsurance.quoteRecord.id)
        }

        if(!order){
            throw new BusinessException(BusinessException.Code.OBJECT_NOT_EXIST, "订单不存在");
        }

        //再查一遍保单信息，以防华安只推了单条核保成功的保单,而车车有多条保单，从而造成核保成功的假象
        if(!insurance){
            insurance = iRepo.findByQuoteRecordId(order.objId)
        }
        if(!compulsoryInsurance){
            compulsoryInsurance = ciRepo.findByQuoteRecordId(order.objId)
        }

        doService(req,order,insurance,compulsoryInsurance)

    }

    def doService(Map req,PurchaseOrder order,Insurance insurance,CompulsoryInsurance compulsoryInsurance){

        logger.debug("华安人工核保返回商业险投保单号 : ${req?.sy_ply_app_no}，交强险投保单号 : ${req?.jq_ply_app_no}，车车订单号：${order.orderNo}")
        order.statusDisplay=null

        def policy=[insurance,compulsoryInsurance].findAll {it}
        def currentInsuranceStatus=[ insurance?.insuranceStatus,compulsoryInsurance?.ciStatus ].findAll {it}
        def reqInsuranceStatus=[req?.sy_udr_mrk , req?.jq_udr_mrk].findAll {it}

        if(policy && currentInsuranceStatus.every {it == PENDING_PAYMENT_1}){

            if(order.status ==INSURE_FAILURE_7) {
                order.status=OrderStatus.Enum.PENDING_PAYMENT_1
                porRepo.save(order)
                syncOrderOperationInfo(order)
                logger.debug("华安核保回调状态为核保成功，车车订单状态:${order.status.id}")
            } else {
                logger.debug('华安人工核保重复回调，忽略')
            }

            return

        }else if(policy && reqInsuranceStatus.any {it as String  == SINOSAFE_INSURE_IMAGE}){
            String msg = [req?.jq_udr_msg , req?.sy_udr_msg].findAll {it}.join(';')
            if (msg) {
                poiService.persistCustomImage(order, msg)
            }
            saveApplicationLog(req,order)
            logger.debug("华安核保回调状态为退回修改,车车订单状态:${order.status.id}")
        }else{
            logger.debug("华安核保回调状态为非预期状态,车车订单状态:${order.status.id}")
        }

        porRepo.save(order)

    }

    static toCheCheStatus(String sinoSafeStatus) {
        def checheStatus = SINOSAFE_ORDER_STATUS_MAPPING.get(sinoSafeStatus)
        if(!checheStatus){
            throw new BusinessException("华安人工核保回调非预期报文状态: $sinoSafeStatus")
        }
        return checheStatus
    }
    
    def syncOrderOperationInfo(PurchaseOrder order) {
        OrderTransmissionStatus newStatus = CHECHE_OPERATION_CENTER_ORDER_STATUS_MAPPING.get(order.status)
        if(newStatus){
            OrderOperationInfo operationInfo=orderOperationInfoService.updateOrderTransmissionStatus(order, newStatus)
            logger.debug("华安订单状态同步更新出单中心,订单号:{},订单状态:{},出单中心状态:{}", order.getOrderNo(), order?.status?.status, operationInfo?.currentStatus?.description);
        }
    }

    def saveApplicationLog(Map req,order){
        MoApplicationLog appLog = MoApplicationLog.applicationLogByPurchaseOrder(order, LogType.Enum.INSURE_FAILURE_1);
        String message=''
        if(req?.sy_udr_msg){
            message+="商业险核保意见 : " + req.sy_udr_msg
        }
        if(req?.jq_udr_msg){
            message+="交强险核保意见 : " +  req.jq_udr_msg
        }
        appLog.setLogMessage(message);
        appLog.setCreateTime(Calendar.getInstance().getTime());//创建时间
        logRepository.save(appLog);
    }

    @Override
    boolean support(PaymentChannel pc) {
        return pc == PaymentChannel.Enum.SINOSAFE_PAY_49
    }

    @Override
    boolean verify(Map<String, String> params) {
        return true
    }

    @Override
    String getOutTradeNo(Map<String, String> params) {
        return params.pay_app_no
    }

    @Override
    String getThirdpartyTradeNo(Map<String, String> params) {
        return params.pay_app_no
    }

    @Override
    boolean isSuccess(Map<String, String> params) {
        return params.get("result") == "0"
    }

    @Override
    boolean isOrder(){
        return true;
    }

    @Override
    String result(boolean isSuccess) {
        isSuccess ? 'success' : 'fail'
    }

    @Override
    public void publish(Payment payment){
        logger.debug("华安支付订单不做同步第三方渠道和出单中心的处理")
    }
}
