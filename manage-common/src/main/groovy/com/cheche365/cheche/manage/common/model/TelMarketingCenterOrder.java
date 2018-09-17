package com.cheche365.cheche.manage.common.model;

import com.cheche365.cheche.core.model.InternalUser;
import com.cheche365.cheche.core.model.PurchaseOrder;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by xu.yelong on 2016/11/24.
 */
@Entity
public class TelMarketingCenterOrder {
    private Long id;
    private TelMarketingCenterHistory telMarketingCenterHistory;
    private PurchaseOrder purchaseOrder;
    private InternalUser operator;
    private Date createTime;
    private Date updateTime;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "tel_marketing_center_history", foreignKey=@ForeignKey(name="FK_TEL_MARKETING_CENTER_ORDER_REF_TEL_MARKETING_CENTER_HISTORY", foreignKeyDefinition="FOREIGN KEY (tel_marketing_center_history) REFERENCES tel_marketing_center_history(id)"))
    public TelMarketingCenterHistory getTelMarketingCenterHistory() {
        return telMarketingCenterHistory;
    }

    public void setTelMarketingCenterHistory(TelMarketingCenterHistory telMarketingCenterHistory) {
        this.telMarketingCenterHistory = telMarketingCenterHistory;
    }

    @ManyToOne
    @JoinColumn(name = "purchase_order", foreignKey=@ForeignKey(name="FK_TEL_MARKETING_CENTER_ORDER_REF_PURCHASE_ORDER", foreignKeyDefinition="FOREIGN KEY (purchase_order) REFERENCES purchase_order(id)"))
    public PurchaseOrder getPurchaseOrder() {
        return purchaseOrder;
    }

    public void setPurchaseOrder(PurchaseOrder purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

    @ManyToOne
    @JoinColumn(name = "operator", foreignKey=@ForeignKey(name="FK_TEL_MARKETING_CENTER_ORDER_REF_INTERNAL_USER", foreignKeyDefinition="FOREIGN KEY (operator) REFERENCES internal_user(id)"))
    public InternalUser getOperator() {
        return operator;
    }

    public void setOperator(InternalUser operator) {
        this.operator = operator;
    }

    @Column(columnDefinition = "DATETIME")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Column(columnDefinition = "DATETIME")
    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
