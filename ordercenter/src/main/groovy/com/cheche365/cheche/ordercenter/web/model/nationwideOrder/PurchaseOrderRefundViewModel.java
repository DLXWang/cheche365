package com.cheche365.cheche.ordercenter.web.model.nationwideOrder;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.PurchaseOrderRefund;

/**
 * Created by sunhuazhong on 2015/11/20.
 */
public class PurchaseOrderRefundViewModel {
    private Long id;
    private Boolean userCheck;//'是否车车退款给用户'
    private Boolean checheCheck;//'是否出单机构退款给车车'
    private Boolean rebateCheck;//'是否退佣金给出单机构'
    private Boolean userStatus;//'退款给用户状态，0-未退款，1-已退款'
    private Boolean checheStatus;//'退款给车车状态，0-未退款，1-已退款'
    private String createTime;//创建时间
    private String updateTime;//修改时间
    private String operatorName;//操作人

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getUserCheck() {
        return userCheck;
    }

    public void setUserCheck(Boolean userCheck) {
        this.userCheck = userCheck;
    }

    public Boolean getChecheCheck() {
        return checheCheck;
    }

    public void setChecheCheck(Boolean checheCheck) {
        this.checheCheck = checheCheck;
    }

    public Boolean getRebateCheck() {
        return rebateCheck;
    }

    public void setRebateCheck(Boolean rebateCheck) {
        this.rebateCheck = rebateCheck;
    }

    public Boolean getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(Boolean userStatus) {
        this.userStatus = userStatus;
    }

    public Boolean getChecheStatus() {
        return checheStatus;
    }

    public void setChecheStatus(Boolean checheStatus) {
        this.checheStatus = checheStatus;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public static PurchaseOrderRefundViewModel createViewModel(PurchaseOrderRefund purchaseOrderRefund) {
        if(purchaseOrderRefund == null) {
            return null;
        }
        PurchaseOrderRefundViewModel viewModel = new PurchaseOrderRefundViewModel();
        viewModel.setUserCheck(purchaseOrderRefund.getUserCheck());
        viewModel.setChecheCheck(purchaseOrderRefund.getChecheCheck());
        viewModel.setRebateCheck(purchaseOrderRefund.getRebateCheck());
        viewModel.setUserStatus(purchaseOrderRefund.getUserStatus());
        viewModel.setChecheStatus(purchaseOrderRefund.getChecheStatus());
        viewModel.setCreateTime(DateUtils.getDateString(purchaseOrderRefund.getCreateTime(), DateUtils.DATE_LONGTIME24_PATTERN));
        viewModel.setUpdateTime(DateUtils.getDateString(purchaseOrderRefund.getUpdateTime(), DateUtils.DATE_LONGTIME24_PATTERN));
        viewModel.setOperatorName(purchaseOrderRefund.getOperator() != null ?
            purchaseOrderRefund.getOperator().getName() : "");
        return viewModel;
    }
}
