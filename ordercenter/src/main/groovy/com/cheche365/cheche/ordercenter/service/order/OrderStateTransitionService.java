package com.cheche365.cheche.ordercenter.service.order;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.*;
import com.cheche365.cheche.core.service.AddressService;
import com.cheche365.cheche.core.service.DoubleDBService;
import com.cheche365.cheche.core.service.GiftService;
import com.cheche365.cheche.ordercenter.constants.OrderCenterConstants;
import com.cheche365.cheche.ordercenter.exception.OrderCenterException;
import com.cheche365.cheche.ordercenter.service.message.IMessageService;
import com.cheche365.cheche.ordercenter.service.user.IInternalUserManageService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * Created by sunhuazhong on 2015/5/8.
 */
public class OrderStateTransitionService implements IOrderStateTransitionService {

    private Logger logger = LoggerFactory.getLogger(OrderStateTransitionService.class);

    @Autowired
    private IInternalUserManageService internalUserManageService;

    @Autowired
    private IMessageService messageService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private OrderOperationInfoRepository orderOperationInfoRepository;

    @Autowired
    private OrderTransmissionStatusRepository orderTransmissionStatusRepository;

    @Autowired
    private DoubleDBService doubleDBService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private InternalUserRelationRepository internalUserRelationRepository;

    @Autowired
    private CompulsoryInsuranceRepository compulsoryInsuranceRepository;

    @Autowired
    private QuoteRecordRepository quoteRecordRepository;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private GiftService giftService;

    /**
     * 订单基本状态变更，适合于客服角色
     * @param orderOperationInfo
     * @param currentStatusId
     * @param date
     * @param reason
     * @param confrimNo
     * @return
     */
    @Override
    public String transitStatusForCustomer(OrderOperationInfo orderOperationInfo, Long currentStatusId, Date date, String reason, String confrimNo) {
        String successFlag;
        try {
            successFlag = resetOrderStatus(orderOperationInfo, currentStatusId, date, reason, confrimNo);
        } catch (Exception ex) {
            logger.error("OrderStateTransitionService.transitStatusForCustomer:order status change error.", ex);
            throw new OrderCenterException(OrderCenterConstants.EXCEPTION_RESET_ORDER_STATUS,
                OrderCenterConstants.EXCEPTION_RESET_ORDER_STATUS_MESSAGE);
        }
        return successFlag;
    }

    /**
     * 订单特定状态变更，适合于内勤角色
     * @param orderOperationInfo
     * @param currentStatusId
     * @param date
     * @param reason
     * @param confrimNo
     * @return
     */
    @Override
    public String transitStatusForInternal(OrderOperationInfo orderOperationInfo, Long currentStatusId, Date date, String reason, String confrimNo) {
        String successFlag;
        try {
            successFlag = resetOrderStatus(orderOperationInfo, currentStatusId, date, reason, confrimNo);
        } catch (Exception ex) {
            logger.error("OrderStateTransitionService.transitStatusForInternal:order status change error.", ex);
            throw new OrderCenterException(OrderCenterConstants.EXCEPTION_RESET_ORDER_STATUS,
                OrderCenterConstants.EXCEPTION_RESET_ORDER_STATUS_MESSAGE);
        }
        return successFlag;
    }

    /**
     * 重新设置出单状态
     * @param orderOperationInfo
     * @param currentStatusId
     * @param date
     * @param reason
     * @param confirmNo
     */
    private String resetOrderStatus(OrderOperationInfo orderOperationInfo, Long currentStatusId, Date date, String reason, String confirmNo) {
        if(logger.isDebugEnabled()) {
            logger.debug("reset order status is starting.");
        }

        // 订单原状态，即订单当前状态
        OrderTransmissionStatus originalStatus = orderOperationInfo.getCurrentStatus();
        // 订单新状态
        OrderTransmissionStatus currentStatus = orderTransmissionStatusRepository.findOne(currentStatusId);
        // 出单对应的订单
        PurchaseOrder purchaseOrder = orderOperationInfo.getPurchaseOrder();
        // 订单对应的支付
        Payment payment = paymentRepository.findFirstByPurchaseOrder(purchaseOrder);

        logger.info("订单原状态为" + originalStatus.getStatus() + ",新状态为" + currentStatus.getStatus()
            + ",确认单号为" + confirmNo + ",返回原因为" + reason);

        // 修改订单状态
        if(StringUtils.isNotEmpty(confirmNo)) {
            orderOperationInfo.setConfirmNo(confirmNo);//确认单号
        }
        orderOperationInfo.setOriginalStatus(originalStatus);//原出单状态
        orderOperationInfo.setCurrentStatus(currentStatus);//当前出单状态
        orderOperationInfo.setOperator(internalUserManageService.getCurrentInternalUser());//最后操作人
        orderOperationInfo.setUpdateTime(Calendar.getInstance().getTime());//修改时间

        String sendFlag = OrderCenterConstants.MESSAGE_SEND_RESULT_NO_SEND;//不需要发送邮件
        boolean isSuccess = true;//发送邮件或者短信结果

        saveApplicationLog(orderOperationInfo, reason);

        // 保存订单状态为出单完成的日志
        saveInsuranceCompleteApplicationLog(orderOperationInfo);

        // 取消订单更新礼品表状态
        updateGiftStatus(orderOperationInfo);

        // 保存出单对象最新状态
        orderOperationInfoRepository.save(orderOperationInfo);

        // 保存订单数据
        purchaseOrderRepository.save(purchaseOrder);

        // 保存支付数据
        if(payment != null){
            paymentRepository.save(payment);
        }


        if(logger.isDebugEnabled()) {
            logger.debug("reset order status is finished.");
        }

        return sendFlag;
    }

    private void updateGiftStatus(OrderOperationInfo orderOperationInfo) {
        //if (OrderTransmissionStatus.Enum.USERCANCELED.getId().equals(orderOperationInfo.getCurrentStatus().getId())) {
        if (OrderTransmissionStatus.Enum.CANCELED.getId().equals(orderOperationInfo.getCurrentStatus().getId())) {
            giftService.resetOrderGift(orderOperationInfo.getPurchaseOrder());
        }
    }

    /**
     * 保存出单完成工作日志
     * @param orderOperationInfo
     */
    private void saveInsuranceCompleteApplicationLog(OrderOperationInfo orderOperationInfo) {
        OrderTransmissionStatus currentStatus = orderOperationInfo.getCurrentStatus();
        if(OrderTransmissionStatus.Enum.PAID_AND_FINISH_ORDER.getId().equals(currentStatus.getId())) {
            MoApplicationLog insuranceCompleteLog = new MoApplicationLog();
            insuranceCompleteLog.setLogLevel(2);//日志的级别 1debug  2info 3warn 4error
            insuranceCompleteLog.setLogMessage(DateUtils.getDateString(Calendar.getInstance().getTime(), DateUtils.DATE_LONGTIME24_PATTERN));//日志信息
            insuranceCompleteLog.setLogType(LogType.Enum.ORDER_INSURANCE_COMPLETE_TIME_6);//订单出单完成时间
            insuranceCompleteLog.setObjId(orderOperationInfo.getId() + "");//对象id
            insuranceCompleteLog.setObjTable("order_operation_info");//对象表名
            insuranceCompleteLog.setOpeartor(internalUserManageService.getCurrentInternalUser().getId());//操作人
            insuranceCompleteLog.setCreateTime(Calendar.getInstance().getTime());//创建时间
            doubleDBService.saveApplicationLog(insuranceCompleteLog);
        }
    }

    /**
     * 保存状态变更工作日志
     * @param orderOperationInfo
     * @param reason
     */
    private void saveApplicationLog(OrderOperationInfo orderOperationInfo, String reason) {
        String confirmNo = orderOperationInfo.getConfirmNo();
        OrderTransmissionStatus originalStatus = orderOperationInfo.getOriginalStatus();
        OrderTransmissionStatus currentStatus = orderOperationInfo.getCurrentStatus();
        MoApplicationLog applicationLog = new MoApplicationLog();
        applicationLog.setLogLevel(2);//日志的级别 1debug  2info 3warn 4error
        applicationLog.setLogMessage("订单由" + originalStatus.getStatus() + "变更为" + currentStatus.getStatus() + ".");//日志信息
        if(!StringUtils.isBlank(reason)) {
            applicationLog.setLogMessage(applicationLog.getLogMessage() + "原因为" + reason);//日志信息
        }
        if(!StringUtils.isBlank(confirmNo)) {
            applicationLog.setLogMessage(applicationLog.getLogMessage() + "确认号为" + confirmNo);//日志信息
        }
        applicationLog.setLogType(LogType.Enum.INSURANCE_STATUS_TRANSITION_25);//出单状态变更
        applicationLog.setObjId(orderOperationInfo.getId() + "");//对象id
        applicationLog.setObjTable("order_operation_info");//对象表名
        applicationLog.setOpeartor(internalUserManageService.getCurrentInternalUser().getId());//操作人
        applicationLog.setCreateTime(Calendar.getInstance().getTime());//创建时间
        doubleDBService.saveApplicationLog(applicationLog);
    }

    /**
     * 发送短信或者邮件
     * @param sendType
     * @param operateStatus
     * @param params
     * @param toMobiles
     * @return
     */
    private boolean sendMessage(String sendType, String operateStatus, Map<String, String> params, String[] toMobiles) {
        boolean isSuccess = true;
        try {
            messageService.sendMessage(sendType, operateStatus, params, toMobiles);
            logger.info("order status transition and send message success.");
        } catch (Exception ex) {
            logger.error("order status transition and send message error.", ex);
            isSuccess = false;
        }

        return isSuccess;
    }


    /**
     * 发送订单完成邮件
     * @param orderOperationInfo
     */
    private boolean sendEmailForOrderComplete(OrderOperationInfo orderOperationInfo) {
        // 模板参数：订单编号-orderNo
        Map<String, String> params = new HashMap<>();
        params.put("orderNo", orderOperationInfo.getPurchaseOrder().getOrderNo());//订单编号

        // 客服
        String[] toEmails = getToCustomerEmails();

        boolean isSuccess = sendMessage(OrderCenterConstants.SEND_TYPE_EMAIL, OrderCenterConstants.OPERATE_STATUS_EMAIL_ORDER_COMPLETE,
            params, toEmails);
        return isSuccess;
    }

    /**
     * 发送打回邮件
     * 根据打回状态，发送给客服或者内勤
     * @param orderOperationInfo
     * @param reason
     */
    private boolean sendEmailForReturn(OrderOperationInfo orderOperationInfo, String reason) {
        // 模板参数：订单编号-orderNo
        Map<String, String> params = new HashMap<>();
        params.put("orderNo", orderOperationInfo.getPurchaseOrder().getOrderNo());//订单编号
        params.put("originalStatus", orderOperationInfo.getOriginalStatus().getStatus());//原状态
        params.put("currentStatus", orderOperationInfo.getCurrentStatus().getStatus());//当前状态
        params.put("reason", reason);//打回原因

        // 打回邮件接收人，可能是客服，可能是内勤
        String[] toEmails = getToReturnEmails(orderOperationInfo);

        boolean isSuccess = sendMessage(OrderCenterConstants.SEND_TYPE_EMAIL, OrderCenterConstants.OPERATE_STATUS_EMAIL_RETURN,
            params, toEmails);
        return isSuccess;
    }

    /**
     * 发送录入保单邮件，邮件给内勤
     * @param orderOperationInfo
     */
    private boolean sendEmailForInputInsurance(OrderOperationInfo orderOperationInfo) {
        // 模板参数：订单编号-orderNo
        Map<String, String> params = new HashMap<>();
        params.put("orderNo", orderOperationInfo.getPurchaseOrder().getOrderNo());//订单编号
        // 客服对应的内勤人员
        String[] toEmails = getToInternalEmails();
        boolean isSuccess = sendMessage(OrderCenterConstants.SEND_TYPE_EMAIL, OrderCenterConstants.OPERATE_STATUS_EMAIL_INPUT_INSURANCE,
            params, toEmails);
        return isSuccess;
    }

    /**
     * 发送核保完成邮件，邮件给客服
     * @param orderOperationInfo
     */
    private boolean sendEmailForUnderwritingComplete(OrderOperationInfo orderOperationInfo, String confirmNo) {
        // 模板参数：订单编号-orderNo
        Map<String, String> params = new HashMap<>();
        params.put("orderNo", orderOperationInfo.getPurchaseOrder().getOrderNo());//订单编号
        params.put("confirmNo", confirmNo);//确认单号
        // 内勤对应的客服人员
        String[] toEmails = getToCustomerEmails();
        boolean isSuccess = sendMessage(OrderCenterConstants.SEND_TYPE_EMAIL, OrderCenterConstants.OPERATE_STATUS_EMAIL_UNDERWRITING_COMPLETE_REMIND,
            params, toEmails);
        return isSuccess;
    }

    /**
     * 发送出单提醒邮件，邮件给内勤
     * @param orderOperationInfo
     */
    private boolean sendEmailForInsureRemind(OrderOperationInfo orderOperationInfo) {
        // 模板参数：订单编号-orderNo
        Map<String, String> params = new HashMap<>();
        params.put("orderNo", orderOperationInfo.getPurchaseOrder().getOrderNo());//订单编号
        // 客服对应的内勤人员
        String[] toEmails = getToInternalEmails();
        boolean isSuccess = sendMessage(OrderCenterConstants.SEND_TYPE_EMAIL, OrderCenterConstants.OPERATE_STATUS_EMAIL_INSURE_REMIND,
            params, toEmails);
        return isSuccess;
    }

    /**
     * 发送用户取消邮件，发送给客服+管理员
     * @param orderOperationInfo
     */
    private boolean sendEmailForUserCancel(OrderOperationInfo orderOperationInfo) {
        // 模板参数：订单编号-orderNo
        Map<String, String> params = new HashMap<>();
        params.put("orderNo", orderOperationInfo.getPurchaseOrder().getOrderNo());//订单编号
        // 客服+管理员
        String[] toEmails = getToCustomerAndAdminEmails();
        boolean isSuccess = sendMessage(OrderCenterConstants.SEND_TYPE_EMAIL, OrderCenterConstants.OPERATE_STATUS_EMAIL_ORDER_CANCEL,
            params, toEmails);
        return isSuccess;
    }

    private String[] getToFinanceEmails() {
        // 线上付款管理员邮箱
        String adminEmail = OrderCenterConstants.ONLINE_PAYMENT_EMAIL;
        String[] adminEmails = adminEmail.split(",");
        return adminEmails;
    }

    private String[] getToPaymentMobiles() {
        // 客服
        InternalUser customer = internalUserManageService.getCurrentInternalUser();
        // 客服内勤外勤关系
        List<InternalUserRelation> internalUserRelations = internalUserRelationRepository.findByCustomerUser(customer);
        List<String> toMobileList = new ArrayList<>();
        for(InternalUserRelation internalUserRelation : internalUserRelations) {
            if(!toMobileList.contains(internalUserRelation.getExternalUser().getMobile())) {
                toMobileList.add(internalUserRelation.getExternalUser().getMobile());
            }
        }
        String[] toMobiles = new String[toMobileList.size()];
        toMobiles = toMobileList.toArray(toMobiles);
        return toMobiles;
    }

    private String[] getToReturnEmails(OrderOperationInfo orderOperationInfo) {
        // 原状态
        OrderTransmissionStatus originalStatus = orderOperationInfo.getOriginalStatus();
        // 当前状态
        OrderTransmissionStatus currentStatus = orderOperationInfo.getCurrentStatus();
//        // 去出单变更为未确认，接收人为客服
//        if(orderTransmissionStatusEnum.INSURANCE.getId().equals(originalStatus.getId())
//            && orderTransmissionStatusEnum.UNCONFIRMED.getId().equals(currentStatus.getId())) {
//            // 客服
//            return getCurrentUserEmail();
//        }
//        // 核保完成变更为去出单，接收人为内勤
//        else if(orderTransmissionStatusEnum.UNDERWRITINGCOMPLETE.getId().equals(originalStatus.getId())
//            && orderTransmissionStatusEnum.INSURANCE.getId().equals(currentStatus.getId())) {
//            return getToInternalEmails();
//        }
//        // 去收款变更为去出单，接收人为内勤
//        else if(orderTransmissionStatusEnum.PAYMENT.getId().equals(originalStatus.getId())
//            && orderTransmissionStatusEnum.INSURANCE.getId().equals(currentStatus.getId())) {
//            return getToInternalEmails();
//        }
//        // 再次收款变更为去出单，接收人为内勤
//        else if(orderTransmissionStatusEnum.REPAYMENT.getId().equals(originalStatus.getId())
//            && orderTransmissionStatusEnum.INSURANCE.getId().equals(currentStatus.getId())) {
//            return getToInternalEmails();
//        }
//        // 出单完成变更为去收款，接收人为客服
//        else if(orderTransmissionStatusEnum.INSURANCECOMPLETE.getId().equals(originalStatus.getId())
//            && orderTransmissionStatusEnum.PAYMENT.getId().equals(currentStatus.getId())) {
//            // 客服
//            return getCurrentUserEmail();
//        }
//        // 派送变更为出单完成，接收人为客服
//        else if(orderTransmissionStatusEnum.DELIVERY.getId().equals(originalStatus.getId())
//            && orderTransmissionStatusEnum.INSURANCECOMPLETE.getId().equals(currentStatus.getId())) {
//            // 客服
//            return getCurrentUserEmail();
//        }
//        // 再次派送变更为出单完成，接收人为客服
//        else if(orderTransmissionStatusEnum.REDELIVERY.getId().equals(originalStatus.getId())
//            && orderTransmissionStatusEnum.INSURANCECOMPLETE.getId().equals(currentStatus.getId())) {
//            // 客服
//            return getCurrentUserEmail();
//        }
//        // 录入保单变更为去派送，接收人为客服
//        else if(orderTransmissionStatusEnum.INPUTINSURANCE.getId().equals(originalStatus.getId())
//            && orderTransmissionStatusEnum.DELIVERY.getId().equals(currentStatus.getId())) {
//            return getToCustomerEmails();
//        }
//        // 订单完成变更为录入保单，接收人为内勤
//        else if(orderTransmissionStatusEnum.ORDERCOMPLETE.getId().equals(originalStatus.getId())
//            && orderTransmissionStatusEnum.INPUTINSURANCE.getId().equals(currentStatus.getId())) {
//            return getToInternalEmails();
//        }
//        // 用户取消变更为未确认，接收人为客服
//        else if(orderTransmissionStatusEnum.USERCANCELED.getId().equals(originalStatus.getId())
//            && orderTransmissionStatusEnum.UNCONFIRMED.getId().equals(currentStatus.getId())) {
//            return getCurrentUserEmail();
//
//        }
        return null;
    }

    private String[] getCurrentUserEmail() {
        // 客服
        InternalUser customer = internalUserManageService.getCurrentInternalUser();
        String[] toEmails = new String[1];
        toEmails[0] = customer.getEmail();
        return toEmails;
    }

    private String[] getToCustomerEmails() {
        // 内勤
        InternalUser internal = internalUserManageService.getCurrentInternalUser();
        // 客服内勤外勤关系
        List<InternalUserRelation> internalUserRelations = internalUserRelationRepository.findByInternalUser(internal);
        List<String> toEmailList = new ArrayList<>();
        for(InternalUserRelation internalUserRelation : internalUserRelations) {
            if(!toEmailList.contains(internalUserRelation.getCustomerUser().getEmail())) {
                toEmailList.add(internalUserRelation.getCustomerUser().getEmail());
            }
        }
        String[] toEmails = new String[toEmailList.size()];
        toEmails = toEmailList.toArray(toEmails);
        return toEmails;
    }

    private String[] getToInternalEmails() {
        // 客服
        InternalUser customer = internalUserManageService.getCurrentInternalUser();
        // 客服内勤外勤关系
        List<InternalUserRelation> internalUserRelations = internalUserRelationRepository.findByCustomerUser(customer);
        List<String> toEmailList = new ArrayList<>();
        for(InternalUserRelation internalUserRelation : internalUserRelations) {
            if(!toEmailList.contains(internalUserRelation.getInternalUser().getEmail())) {
                toEmailList.add(internalUserRelation.getInternalUser().getEmail());
            }
        }
        String[] toEmails = new String[toEmailList.size()];
        toEmails = toEmailList.toArray(toEmails);
        return toEmails;
    }

    private String[] getToCustomerAndAdminEmails() {
        List<String> toEmailList = new ArrayList<>();
        // 客服
        InternalUser customer = internalUserManageService.getCurrentInternalUser();
        // 客服邮箱
        String customerEmail = customer.getEmail();
        toEmailList.add(customerEmail);
        // 用户取消管理员邮箱
        String adminEmail = OrderCenterConstants.USER_CANCEL_EMAIL;
        List<String> adminEmails = Arrays.asList(adminEmail.split(","));
        toEmailList.addAll(adminEmails);

        String[] toEmails = new String[toEmailList.size()];
        toEmails = toEmailList.toArray(toEmails);
        return toEmails;
    }
}
