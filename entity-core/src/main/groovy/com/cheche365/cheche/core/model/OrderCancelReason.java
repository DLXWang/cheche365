package com.cheche365.cheche.core.model;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by zhaozhong on 2015/9/2.
 */

@Entity
@Table(name = "order_cancel_reason")
public class OrderCancelReason {

    private Long id;
    private OrderCancelReasonType orderCancelReasonType;
    private Timestamp cancelTime;
    private PurchaseOrder purchaseOrder;
    private String description;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_cancel_reason_type_id", columnDefinition = "order_cancel_reason_type_id", referencedColumnName = "id")
    public OrderCancelReasonType getOrderCancelReasonType() {
        return orderCancelReasonType;
    }

    public void setOrderCancelReasonType(OrderCancelReasonType orderCancelReasonType) {
        this.orderCancelReasonType = orderCancelReasonType;
    }

    @Column(nullable = false)
    public Timestamp getCancelTime() {
        return cancelTime;
    }

    public void setCancelTime(Timestamp cancelTime) {
        this.cancelTime = cancelTime;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", columnDefinition = "order_id", referencedColumnName = "id")
    public PurchaseOrder getPurchaseOrder() {
        return purchaseOrder;
    }

    public void setPurchaseOrder(PurchaseOrder purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

    @Column(length = 400)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
