package com.cheche365.cheche.core.model;

import com.cheche365.cheche.core.repository.BaseEntity;
import com.cheche365.cheche.core.service.listener.EntityChangeListener;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by sunhuazhong on 2015/4/28.
 */
@Entity
@EntityListeners(EntityChangeListener.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderOperationInfo extends BaseEntity {

    private static final long serialVersionUID = -552530776737269836L;
    private PurchaseOrder purchaseOrder;//订单
    private OrderTransmissionStatus originalStatus;//原出单状态
    private OrderTransmissionStatus currentStatus;//当前出单状态
    private InternalUser assigner;//指定人
    private InternalUser operator;//最后操作人
    private Date payTime;//收款时间
    private String payPeriod;//收款时间段
    private Date sendTime;//派送时间
    private String sendPeriod;//收款时间段
    private Date reConfirmDate;//再次确认时间
    private Date confirmOrderDate;//确认出单时间
    private String confirmNo;
    private String comment;
    private InternalUser insuranceInputter;//保单录入者
    private InternalUser owner;//订单当前所有者


    @OneToOne
    @JoinColumn(name = "purchaseOrder", foreignKey=@ForeignKey(name="FK_ORDER_OPERATION_INFO_REF_ORDER", foreignKeyDefinition="FOREIGN KEY (purchase_order) REFERENCES purchase_order(id)"))
    public PurchaseOrder getPurchaseOrder() {
        return purchaseOrder;
    }

    public void setPurchaseOrder(PurchaseOrder purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

    @ManyToOne
    @JoinColumn(name = "originalStatus", foreignKey=@ForeignKey(name="FK_ORDER_OPERATION_INFO_ORIGINAL_REF_INSURE_STATUS", foreignKeyDefinition="FOREIGN KEY (original_status) REFERENCES order_transmission_status(id)"))
    public OrderTransmissionStatus getOriginalStatus() {
        return originalStatus;
    }

    public void setOriginalStatus(OrderTransmissionStatus originalStatus) {
        this.originalStatus = originalStatus;
    }

    @ManyToOne
    @JoinColumn(name = "currentStatus", foreignKey=@ForeignKey(name="FK_ORDER_OPERATION_INFO_CURRENT_REF_INSURE_STATUS", foreignKeyDefinition="FOREIGN KEY (current_status) REFERENCES order_transmission_status(id)"))
    public OrderTransmissionStatus getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(OrderTransmissionStatus currentStatus) {
        this.currentStatus = currentStatus;
    }

    @ManyToOne
    @JoinColumn(name = "assigner", foreignKey=@ForeignKey(name="FK_ORDER_OPERATION_INFO_ASSIGNER_REF_INTERNAL_USER", foreignKeyDefinition="FOREIGN KEY (assigner) REFERENCES internal_user(id)"))
    public InternalUser getAssigner() {
        return assigner;
    }

    public void setAssigner(InternalUser assigner) {
        this.assigner = assigner;
    }

    @ManyToOne
    @JoinColumn(name = "operator", foreignKey=@ForeignKey(name="FK_ORDER_OPERATION_INFO_OPERATOR_REF_INTERNAL_USER", foreignKeyDefinition="FOREIGN KEY (operator) REFERENCES internal_user(id)"))
    public InternalUser getOperator() {
        return operator;
    }

    public void setOperator(InternalUser operator) {
        this.operator = operator;
    }

    @Column(columnDefinition = "DATE")
    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    @Column(columnDefinition = "VARCHAR(20)")
    public String getPayPeriod() {
        return payPeriod;
    }

    public void setPayPeriod(String payPeriod) {
        this.payPeriod = payPeriod;
    }

    @Column(columnDefinition = "DATE")
    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    @Column(columnDefinition = "VARCHAR(20)")
    public String getSendPeriod() {
        return sendPeriod;
    }

    public void setSendPeriod(String sendPeriod) {
        this.sendPeriod = sendPeriod;
    }

    @Column(columnDefinition = "DATE")
    public Date getReConfirmDate() {
        return reConfirmDate;
    }

    public void setReConfirmDate(Date reConfirmDate) {
        this.reConfirmDate = reConfirmDate;
    }

    @Column(columnDefinition = "VARCHAR(100)")
    public String getConfirmNo() {
        return confirmNo;
    }

    public void setConfirmNo(String confirmNo) {
        this.confirmNo = confirmNo;
    }

    @ManyToOne
    @JoinColumn(name = "insuranceInputter", foreignKey=@ForeignKey(name="FK_ORDER_OPERATION_INFO_REF_INTERNAL_USER_INSURANCE_INPUTTER", foreignKeyDefinition="FOREIGN KEY (insurance_inputter) REFERENCES internal_user(id)"))
    public InternalUser getInsuranceInputter() {
        return insuranceInputter;
    }

    public void setInsuranceInputter(InternalUser insuranceInputter) {
        this.insuranceInputter = insuranceInputter;
    }

    @Column(columnDefinition = "VARCHAR(2000)")
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @ManyToOne
    @JoinColumn(name = "owner", foreignKey=@ForeignKey(name="FK_ORDER_OPERATION_INFO_REF_INTERNAL_USER_OWNER", foreignKeyDefinition="FOREIGN KEY (owner) REFERENCES internal_user(id)"))
    public InternalUser getOwner() {
        return owner;
    }

    public void setOwner(InternalUser owner) {
        this.owner = owner;
    }

    @Column(columnDefinition = "DATETIME")
    public Date getConfirmOrderDate() {
        return confirmOrderDate;
    }

    public void setConfirmOrderDate(Date confirmOrderDate) {
        this.confirmOrderDate = confirmOrderDate;
    }

    @Transient
    public Boolean isFanhua(){
        return purchaseOrder.getOrderSourceType() != null && purchaseOrder.getOrderSourceType().getId().equals(OrderSourceType.Enum.PLANTFORM_BX_5.getId());
    }

    @Transient
    public Boolean suppleAmend(){
        ApiPartner apiPartner=purchaseOrder.getSourceChannel().getApiPartner();
        return (apiPartner == null || apiPartner.supportAmend()) &&  !isFanhua() && OrderTransmissionStatus.Enum.supportAmendStatus().contains(this.currentStatus.getId());
    }
}
