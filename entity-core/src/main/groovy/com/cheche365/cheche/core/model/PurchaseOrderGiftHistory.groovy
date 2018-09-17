package com.cheche365.cheche.core.model

import com.fasterxml.jackson.annotation.JsonIgnore

import javax.persistence.*

/**
 * Created by mahong on 2016/9/22.
 */
@Entity
class PurchaseOrderGiftHistory extends DescribableEntity {
    private Gift gift;
    private boolean givenAfterOrder;
    private PurchaseOrderHistory purchaseOrderHistory;

    @ManyToOne
    @JoinColumn(name = "gift", foreignKey = @ForeignKey(name = "FK_ORDER_GIFT_HIS_REF_GIFT", foreignKeyDefinition = "FOREIGN KEY (gift) REFERENCES gift(id)"))
    public Gift getGift() {
        return gift;
    }

    public void setGift(Gift gift) {
        this.gift = gift;
    }

    public void setGivenAfterOrder(boolean givenAfterOrder) {
        this.givenAfterOrder = givenAfterOrder;
    }

    @Column(columnDefinition = "tinyint(1)")
    public boolean getGivenAfterOrder() {
        return givenAfterOrder
    }

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "purchaseOrderHistory", foreignKey = @ForeignKey(name = "FK_ORDER_GIFT_HIS_REF_ORDER_HIS", foreignKeyDefinition = "FOREIGN KEY (purchase_order_history) REFERENCES purchase_order_history(id)"))
    public PurchaseOrderHistory getPurchaseOrderHistory() {
        return purchaseOrderHistory
    }

    public void setPurchaseOrderHistory(PurchaseOrderHistory purchaseOrderHistory) {
        this.purchaseOrderHistory = purchaseOrderHistory
    }
}
