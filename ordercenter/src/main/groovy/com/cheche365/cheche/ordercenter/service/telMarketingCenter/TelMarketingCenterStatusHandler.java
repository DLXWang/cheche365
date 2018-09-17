package com.cheche365.cheche.ordercenter.service.telMarketingCenter;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.exception.BusinessException;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.*;
import com.cheche365.cheche.core.service.InternalUserService;
import com.cheche365.cheche.manage.common.model.*;
import com.cheche365.cheche.manage.common.repository.TelMarketingCenterHistoryRepository;
import com.cheche365.cheche.manage.common.repository.TelMarketingCenterOrderRepository;
import com.cheche365.cheche.manage.common.repository.TelMarketingCenterRepository;
import com.cheche365.cheche.ordercenter.service.order.OrderTransmissionStatusHandler;
import com.cheche365.cheche.ordercenter.service.user.IInternalUserManageService;
import com.cheche365.cheche.common.util.StringUtil;
import com.cheche365.cheche.manage.common.web.model.ResultModel;
import com.cheche365.cheche.ordercenter.web.model.telMarketingCenter.TelMarketingCenterViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.List;

/**
 * Created by yinJianBin on 2016/10/24.
 */
@Component
class RefundCancel {
    private Logger logger = LoggerFactory.getLogger(RefundCancel.class);

    @Autowired
    private OrderTransmissionStatusHandler orderTransmissionStatusHandler;
    @Autowired
    private PurchaseOrderAmendRepository PurchaseOrderAmendRepository;
    @Autowired
    private InternalUserService internalUserService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    protected void handle(TelMarketingCenter telMarketingCenter) {
        if (!telMarketingCenter.getSource().getId().equals(TelMarketingCenterSource.Enum.ORDERS_REFUND.getId())) {
            throw new BusinessException(BusinessException.Code.OPERATION_NOT_ALLOWED, "非法操作,只有退款来源的数据才能进行此操作!");
        }

        PurchaseOrderAmend purchaseOrderAmend = PurchaseOrderAmendRepository.findByMobileFromTelMarketingCenter(PurchaseOrderAmendStatus.Enum.CREATE.getId(), telMarketingCenter.getMobile(), PurchaseOrderAmend.TABLE_NAME);
        if (purchaseOrderAmend == null) {
            return;
        }

        OrderOperationInfo orderOperationInfo = purchaseOrderAmend.getOrderOperationInfo();
        Boolean isMember = stringRedisTemplate.opsForSet().isMember("syn.refunding.order.id", orderOperationInfo.getPurchaseOrder().getOrderNo());
        if (isMember) {
            throw new BusinessException(BusinessException.Code.OPERATION_NOT_ALLOWED, "正在退款中,不能取消!");
        }

        logger.debug("电销中心修改状态为退款取消,更新出单中心状态为未确认,orderNo-->[{}],amendId-->[{}]", orderOperationInfo.getPurchaseOrder().getOrderNo(), purchaseOrderAmend.getId());
        //更新出单中心以及订单状态
        orderTransmissionStatusHandler.request(orderOperationInfo, OrderTransmissionStatus.Enum.UNCONFIRMED);
    }
}


@Component
class OrderCenterStatusHandler {

    @Autowired
    private RefundCancel refundCancel;

    protected void handle(TelMarketingCenter telMarketingCenter, TelMarketingCenterStatus status) {
        if (status.getId().equals(TelMarketingCenterStatus.Enum.REFUND_CANCEL.getId())) {
            refundCancel.handle(telMarketingCenter);
        }
    }
}

@Component
public class TelMarketingCenterStatusHandler {

    @Autowired
    private TelMarketingCenterRepository telMarketingCenterRepository;
    @Autowired
    private TelMarketingCenterHistoryRepository telMarketingCenterHistoryRepository;
    @Autowired
    private IInternalUserManageService internalUserManageService;
    @Autowired
    private OrderCenterStatusHandler orderCenterStatusHandler;
    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;
    @Autowired
    private TelMarketingCenterOrderRepository telMarketingCenterOrderRepository;

    @Transactional
    public ResultModel request(TelMarketingCenter telMarketingCenter, TelMarketingCenterStatus status, TelMarketingCenterViewModel viewModel) {
        ResultModel result = preHandle(status, viewModel);
        if(!result.isPass()){
            return result;
        }


        //处理出单中心状态
        orderCenterStatusHandler.handle(telMarketingCenter, status);
        //保存电销的处理状态
        this.refreshTelMarketingCenterStatus(telMarketingCenter, status, viewModel, result);
        afterHandle(telMarketingCenter,viewModel);

        return result;
    }

    private void refreshTelMarketingCenterStatus(TelMarketingCenter telMarketingCenter, TelMarketingCenterStatus status, TelMarketingCenterViewModel viewModel, ResultModel result) {
        telMarketingCenter.setStatus(status);
        telMarketingCenter.setProcessedNumber(telMarketingCenter.getProcessedNumber() == null ?0 : telMarketingCenter.getProcessedNumber() + 1);
        telMarketingCenter.setTriggerTime(viewModel.getTriggerTime() != null ?
            DateUtils.getDate(viewModel.getTriggerTime(), DateUtils.DATE_LONGTIME24_PATTERN) : null);
        if (viewModel.getStatusId() != null) {
            telMarketingCenter.setPriority(0);
        }
        telMarketingCenter.setDisplay(false);
        telMarketingCenter.setExpireTime(DateUtils.getDate(viewModel.getExpireTime(), DateUtils.DATE_SHORTDATE_PATTERN));
        telMarketingCenter.setUpdateTime(Calendar.getInstance().getTime());
        telMarketingCenter.setDisplay(false);
        telMarketingCenterRepository.save(telMarketingCenter);
    }

    private void saveTelMarketingCenterOrder(TelMarketingCenterHistory history, String orderNo){
        InternalUser user = internalUserManageService.getCurrentInternalUser();
        PurchaseOrder order = purchaseOrderRepository.findFirstByOrderNo(orderNo);
        TelMarketingCenterOrder telMarketingCenterOrder = new TelMarketingCenterOrder();
        telMarketingCenterOrder.setOperator(user);
        telMarketingCenterOrder.setPurchaseOrder(order);
        telMarketingCenterOrder.setTelMarketingCenterHistory(history);
        telMarketingCenterOrder.setCreateTime(Calendar.getInstance().getTime());
        telMarketingCenterOrder.setUpdateTime(Calendar.getInstance().getTime());
        telMarketingCenterOrderRepository.save(telMarketingCenterOrder);
    }

    /**
     * 创建历史对象
     *
     * @param telMarketingCenter
     * @param comment
     * @param resultDetail
     * @return
     */
    private TelMarketingCenterHistory saveHistoryForResult(TelMarketingCenter telMarketingCenter, String comment, String resultDetail) {
        TelMarketingCenterStatus status = telMarketingCenter.getStatus();
        TelMarketingCenterHistory telMarketingCenterHistory = new TelMarketingCenterHistory();
        telMarketingCenterHistory.setType(1);
        //判断此次是否是成单操作，如果是，则判断以前此号码是否有成单操作
        if (status.getId().equals(TelMarketingCenterStatus.Enum.ORDER.getId())) {
            List<TelMarketingCenterHistory> hisList = telMarketingCenterHistoryRepository
                .findByTelMarketingCenterOrderByCreateTimeDesc(telMarketingCenter);
            //如果在此次操作成单之前有成单的，则遍历出来，type置为1
            for (TelMarketingCenterHistory tmch : hisList) {
                if (tmch.getType() == 4) {
                    tmch.setType(1);
                }
                telMarketingCenterHistoryRepository.save(tmch);
            }
            telMarketingCenterHistory.setType(4);//然后将此次type置为4
        }
        telMarketingCenterHistory.setDealResult(status.getName());
        telMarketingCenterHistory.setComment(comment);
        telMarketingCenterHistory.setTelMarketingCenter(telMarketingCenter);
        telMarketingCenterHistory.setStatus(status);
        telMarketingCenterHistory.setOperator(internalUserManageService.getCurrentInternalUser());
        telMarketingCenterHistory.setCreateTime(Calendar.getInstance().getTime());
        telMarketingCenterHistory.setResultDetail(resultDetail);
        telMarketingCenterHistoryRepository.save(telMarketingCenterHistory);
        return telMarketingCenterHistory;
    }

    public ResultModel preHandle( TelMarketingCenterStatus status, TelMarketingCenterViewModel viewModel){
        ResultModel resultModel = new ResultModel();
        if(status.getId().equals(TelMarketingCenterStatus.Enum.ORDER.getId())){
            if(StringUtil.isNull( viewModel.getOrderNo())){
                resultModel.setPass(false);
                resultModel.setMessage("请输入订单号");
            }
            PurchaseOrder order = purchaseOrderRepository.findFirstByOrderNo(viewModel.getOrderNo());
            if(order == null){
                resultModel.setPass(false);
                resultModel.setMessage("错误订单号，请核对");
            }
        }
        return resultModel;
    }

    public void afterHandle(TelMarketingCenter telMarketingCenter,TelMarketingCenterViewModel viewModel){
        //保存操作历史记录
        TelMarketingCenterHistory history = saveHistoryForResult(telMarketingCenter, viewModel.getComment(), viewModel.getResultDetail());
        if(telMarketingCenter.getStatus().getId().equals(TelMarketingCenterStatus.Enum.ORDER.getId())){
            saveTelMarketingCenterOrder(history,viewModel.getOrderNo());
        }
    }
}
