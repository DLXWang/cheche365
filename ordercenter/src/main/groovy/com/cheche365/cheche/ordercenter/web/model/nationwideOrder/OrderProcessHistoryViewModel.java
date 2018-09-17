package com.cheche365.cheche.ordercenter.web.model.nationwideOrder;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.OrderProcessHistory;

/**
 * Created by wangfei on 2015/11/20.
 */
public class OrderProcessHistoryViewModel {
    private Long id;
    private Long purchaseOrderId;
    private Long originalStatus;
    private Long currentStatus;
    private String comment;//备注
    private String createTime;//创建时间
    private String operatorName;//操作人

    public Long getPurchaseOrderId() {
        return purchaseOrderId;
    }

    public void setPurchaseOrderId(Long purchaseOrderId) {
        this.purchaseOrderId = purchaseOrderId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOriginalStatus() {
        return originalStatus;
    }

    public void setOriginalStatus(Long originalStatus) {
        this.originalStatus = originalStatus;
    }

    public Long getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(Long currentStatus) {
        this.currentStatus = currentStatus;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public static OrderProcessHistoryViewModel createViewModel(OrderProcessHistory orderProcessHistory) {
        if (null == orderProcessHistory) {
            return null;
        }
        OrderProcessHistoryViewModel viewModel = new OrderProcessHistoryViewModel();
        viewModel.setId(orderProcessHistory.getId());
        viewModel.setPurchaseOrderId(orderProcessHistory.getPurchaseOrder().getId());
        viewModel.setCurrentStatus(orderProcessHistory.getCurrentStatus());
        viewModel.setOriginalStatus(orderProcessHistory.getOriginalStatus());
        viewModel.setComment(orderProcessHistory.getComment());
        viewModel.setCreateTime(DateUtils.getDateString(orderProcessHistory.getCreateTime(), DateUtils.DATE_LONGTIME24_PATTERN));
        viewModel.setOperatorName(orderProcessHistory.getOperator() == null ? "" : orderProcessHistory.getOperator().getName());
        return viewModel;
    }

}
