package com.cheche365.cheche.core.model;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by sunhuazhong on 2015/11/20.
 */
@Entity
public class PurchaseOrderRefund {
    private Long id;
    private PurchaseOrder purchaseOrder;//订单
    private Boolean userCheck;//'是否车车退款给用户'
    private Boolean checheCheck;//'是否出单机构退款给车车'
    private Boolean rebateCheck;//'是否退佣金给出单机构'
    private Boolean userStatus;//'退款给用户状态，0-未退款，1-已退款'
    private Boolean checheStatus;//'退款给车车状态，0-未退款，1-已退款'
    private Date createTime;//创建时间
    private Date updateTime;//修改时间
    private InternalUser operator;//操作人

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "purchaseOrder", foreignKey=@ForeignKey(name="FK_PURCHASE_ORDER_REFUND_REF_PURCHASE_ORDER", foreignKeyDefinition="FOREIGN KEY (purchase_order) REFERENCES purchase_order(id)"))
    public PurchaseOrder getPurchaseOrder() {
        return purchaseOrder;
    }

    public void setPurchaseOrder(PurchaseOrder purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

    @Column(columnDefinition = "tinyint(1)")
    public Boolean getUserCheck() {
        return userCheck;
    }

    public void setUserCheck(Boolean userCheck) {
        this.userCheck = userCheck;
    }

    @Column(columnDefinition = "tinyint(1)")
    public Boolean getChecheCheck() {
        return checheCheck;
    }

    public void setChecheCheck(Boolean checheCheck) {
        this.checheCheck = checheCheck;
    }

    @Column(columnDefinition = "tinyint(1)")
    public Boolean getRebateCheck() {
        return rebateCheck;
    }

    public void setRebateCheck(Boolean rebateCheck) {
        this.rebateCheck = rebateCheck;
    }

    @Column(columnDefinition = "tinyint(1)")
    public Boolean getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(Boolean userStatus) {
        this.userStatus = userStatus;
    }

    @Column(columnDefinition = "tinyint(1)")
    public Boolean getChecheStatus() {
        return checheStatus;
    }

    public void setChecheStatus(Boolean checheStatus) {
        this.checheStatus = checheStatus;
    }

    @Column(columnDefinition = "datetime")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Column(columnDefinition = "datetime")
    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @ManyToOne
    @JoinColumn(name = "operator", foreignKey=@ForeignKey(name="FK_PURCHASE_ORDER_REFUND_REF_OPERATOR", foreignKeyDefinition="FOREIGN KEY (operator) REFERENCES internal_user(id)"))
    public InternalUser getOperator() {
        return operator;
    }

    public void setOperator(InternalUser operator) {
        this.operator = operator;
    }

    public static String convertCheckToString(PurchaseOrderRefund purchaseOrderRefund) {
        if (null == purchaseOrderRefund) {
            return "";
        }
        StringBuffer buffer = new StringBuffer("");
        buffer.append(purchaseOrderRefund.getUserCheck() ? "1" : "")
            .append(purchaseOrderRefund.getChecheCheck() ? ",2" : "")
            .append(purchaseOrderRefund.getRebateCheck() ? ",3" : "");
        return buffer.toString();
    }
}
