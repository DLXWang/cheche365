package com.cheche365.cheche.core.model;

import com.cheche365.cheche.core.service.listener.EntityChangeListener;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Created by Administrator on 2016/9/7 0007.
 */
@Entity
@EntityListeners(EntityChangeListener.class)
public class PurchaseOrderAmend extends DescribableEntity {

    private static final long serialVersionUID = -4133057269016222671L;
    private OrderOperationInfo orderOperationInfo;
    private PurchaseOrder purchaseOrder;
    private PurchaseOrderHistory purchaseOrderHistory;//更新订单后的历史记录
    private QuoteRecord originalQuoteRecord;
    private QuoteRecord newQuoteRecord;
    private PaymentType paymentType;
    private PurchaseOrderAmendStatus purchaseOrderAmendStatus;

    @ManyToOne
    @JoinColumn(name = "order_operation_info", foreignKey=@ForeignKey(name="purchase_order_amend_ibk4", foreignKeyDefinition="FOREIGN KEY (order_operation_info) REFERENCES order_operation_info(id)"))
    public OrderOperationInfo getOrderOperationInfo() {
        return orderOperationInfo;
    }

    public void setOrderOperationInfo(OrderOperationInfo orderOperationInfo) {
        this.orderOperationInfo = orderOperationInfo;
    }

    @ManyToOne
    @JoinColumn(name = "purchase_order", foreignKey=@ForeignKey(name="purchase_order_amend_ibk2", foreignKeyDefinition="FOREIGN KEY (purchase_order) REFERENCES purchase_order(id)"))
    public PurchaseOrder getPurchaseOrder() {
        return purchaseOrder;
    }

    public void setPurchaseOrder(PurchaseOrder purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

    @ManyToOne
    @JoinColumn(name = "original_quote_record", foreignKey=@ForeignKey(name="purchase_order_amend_ibk5", foreignKeyDefinition="FOREIGN KEY (original_quote_record) REFERENCES quote_record(id)"))
    public QuoteRecord getOriginalQuoteRecord() {
        return originalQuoteRecord;
    }

    public void setOriginalQuoteRecord(QuoteRecord originalQuoteRecord) {
        this.originalQuoteRecord = originalQuoteRecord;
    }

    @ManyToOne
    @JoinColumn(name = "new_quote_record", foreignKey=@ForeignKey(name="purchase_order_amend_ibk6", foreignKeyDefinition="FOREIGN KEY (new_quote_record) REFERENCES quote_record(id)"))
    public QuoteRecord getNewQuoteRecord() {
        return newQuoteRecord;
    }

    public void setNewQuoteRecord(QuoteRecord newQuoteRecord) {
        this.newQuoteRecord = newQuoteRecord;
    }

    @ManyToOne
    @JoinColumn(name = "payment_type",foreignKey=@ForeignKey(name="purchase_order_amend_ibk1", foreignKeyDefinition="FOREIGN KEY(payment_type) REFERENCES payment_type(id)"))
    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    @ManyToOne
    @JoinColumn(name = "purchase_order_amend_status",foreignKey=@ForeignKey(name="purchase_order_amend_ibk7", foreignKeyDefinition="FOREIGN KEY(purchase_order_amend_status) REFERENCES purchase_order_amend_status(id)"))
    public PurchaseOrderAmendStatus getPurchaseOrderAmendStatus(){
        return this.purchaseOrderAmendStatus;
    }

    public void setPurchaseOrderAmendStatus(PurchaseOrderAmendStatus purchaseOrderAmendStatus){
        this.purchaseOrderAmendStatus = purchaseOrderAmendStatus;
    }

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "purchase_order_history",foreignKey=@ForeignKey(name="purchase_order_amend_ibk8", foreignKeyDefinition="FOREIGN KEY(purchase_order_history) REFERENCES purchase_order_history(id)"))
    public PurchaseOrderHistory getPurchaseOrderHistory() {
        return purchaseOrderHistory;
    }

    public void setPurchaseOrderHistory(PurchaseOrderHistory purchaseOrderHistory) {
        this.purchaseOrderHistory = purchaseOrderHistory;
    }

    @Override
    public boolean equals(Object o) {
        return null!=o&&this.getId().equals(((PurchaseOrderAmend)o).getId());
    }

    @Override
    public int hashCode(){
        return HashCodeBuilder.reflectionHashCode(this);
    }

    public static String TABLE_NAME = "purchase_order_amend";
}
