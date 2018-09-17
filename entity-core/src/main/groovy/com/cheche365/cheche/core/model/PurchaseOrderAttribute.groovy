package com.cheche365.cheche.core.model

import com.cheche365.cheche.core.repository.BaseEntity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.ForeignKey
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

/**
 * Created by wen on 2018/6/14.
 */
@Entity
class PurchaseOrderAttribute extends BaseEntity{

    private PurchaseOrder purchaseOrder;
    private AttributeType type;
    private String value;

    @ManyToOne
    @JoinColumn(name = "purchase_order", foreignKey = @ForeignKey(name = "FK1_PURCHASE_ORDER_ATTRIBUTE", foreignKeyDefinition = "FOREIGN KEY (purchase_order) REFERENCES purchase_order(id)"))
    public PurchaseOrder getPurchaseOrder() {
        return purchaseOrder;
    }

    public void setPurchaseOrder(PurchaseOrder purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

    @ManyToOne
    @JoinColumn(name = "type", foreignKey = @ForeignKey(name = "FK2_ATTRIBUTE_TYPE", foreignKeyDefinition = "FOREIGN KEY (type) REFERENCES attribute_type(id)"))
    public AttributeType getType() {
        return type;
    }

    public void setType(AttributeType extendType) {
        this.type = extendType;
    }

    @Column(columnDefinition = "VARCHAR(500)")
    String getValue() {
        return value
    }

    void setValue(String value) {
        this.value = value
    }
}
