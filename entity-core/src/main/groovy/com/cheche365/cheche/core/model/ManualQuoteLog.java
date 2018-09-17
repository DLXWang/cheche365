package com.cheche365.cheche.core.model;

import javax.persistence.*;
import java.util.Date;

/**
 * 手动报价日志
 * Created by wangshaobin on 2016/9/12.
 */
@Entity
public class ManualQuoteLog {
    private Long id;
    private PurchaseOrder purchaseOrder;
    private String errorCode;
    private InternalUser operator;
    private Date operateTime;

    public void setId(Long id) {
        this.id = id;
    }

    public void setPurchaseOrder(PurchaseOrder purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public void setOperator(InternalUser operator) {
        this.operator = operator;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    @OneToOne
    @JoinColumn(name = "purchaseOrder", foreignKey = @ForeignKey(name = "FK_MANUAL_QUOTE_LOG_REF_ORDER", foreignKeyDefinition = "FOREIGN KEY (purchase_order) REFERENCES purchase_order(id)"))
    public PurchaseOrder getPurchaseOrder() {
        return purchaseOrder;
    }

    @Column
    public String getErrorCode() {
        return errorCode;
    }

    @ManyToOne
    @JoinColumn(name = "operator", foreignKey=@ForeignKey(name="FK_MANUAL_QUOTE_LOG_REF_INTERNAL_USER", foreignKeyDefinition="FOREIGN KEY (operator) REFERENCES internal_user(id)"))
    public InternalUser getOperator() {
        return operator;
    }

    @Column
    public Date getOperateTime() {
        return operateTime;
    }
}
