package com.cheche365.cheche.core.model;

/**
 * Created by mahong on 2015/6/23.
 */

import javax.persistence.*;


@Entity
public class PurchaseOrderGift {

    private Long id;
    private PurchaseOrder purchaseOrder;
    private Gift gift;
    private boolean givenAfterOrder;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "purchaseOrder", foreignKey = @ForeignKey(name = "FK_PURCHASE_ORDER_GIFT_REF_PURCHASE_ORDER", foreignKeyDefinition = "FOREIGN KEY (PURCHASE_ORDER) REFERENCES PURCHASE_ORDER(id)"))
    public PurchaseOrder getPurchaseOrder() {
        return purchaseOrder;
    }

    public void setPurchaseOrder(PurchaseOrder purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

    @ManyToOne
    @JoinColumn(name = "gift", foreignKey = @ForeignKey(name = "FK_PURCHASE_ORDER_GIFT_REF_GIFT", foreignKeyDefinition = "FOREIGN KEY (GIFT) REFERENCES GIFT(id)"))
    public Gift getGift() {
        return gift;
    }

    public void setGift(Gift gift) {
        this.gift = gift;
    }

    @Column(columnDefinition = "tinyint(1)")
    public boolean isGivenAfterOrder() {
        return givenAfterOrder;
    }

    public void setGivenAfterOrder(boolean givenAfterOrder) {
        this.givenAfterOrder = givenAfterOrder;
    }
}


