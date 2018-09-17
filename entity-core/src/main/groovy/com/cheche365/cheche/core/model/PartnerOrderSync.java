package com.cheche365.cheche.core.model;



import com.cheche365.cheche.core.service.listener.EntityChangeListener;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

/**
 * Created by Shanxf on 2016/8/12.
 */
@Entity
@EntityListeners(EntityChangeListener.class)
@JsonIgnoreProperties( value = {"sendSyncParent"})
public class PartnerOrderSync extends DescribableEntity {

    private PartnerOrder partnerOrder;
    private Integer status; // 0:未同步; 1:同步成功; 2:同步失败
    private String syncBody;
    private String sendSyncMessage;
    private String receiveSyncMessage;


    private OrderStatus purchaseOrderStatus;
    private PartnerOrderSync sendSyncParent;
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition="TEXT", nullable=true)
    public String getSyncBody() {
        return syncBody;
    }

    public void setSyncBody(String syncBody) {
        this.syncBody = syncBody;
    }
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition="TEXT", nullable=true)
    public String getSendSyncMessage() {
        return sendSyncMessage;
    }

    public void setSendSyncMessage(String sendSyncMessage) {
        this.sendSyncMessage = sendSyncMessage;
    }

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition="TEXT", nullable=true)
    public String getReceiveSyncMessage() {
        return receiveSyncMessage;
    }

    public void setReceiveSyncMessage(String receiveSyncMessage) {
        this.receiveSyncMessage = receiveSyncMessage;
    }




    @ManyToOne
    @JoinColumn(name = "partner_order", foreignKey = @ForeignKey(name = "FK_PARTNER_ORDER_SYNC_REF_PARTNER_ORDER", foreignKeyDefinition = "FOREIGN KEY (partner_order) REFERENCES partner_order (id)"))
    public PartnerOrder getPartnerOrder() {
        return partnerOrder;
    }

    public void setPartnerOrder(PartnerOrder partnerOrder) {
        this.partnerOrder = partnerOrder;
    }

    @Column(columnDefinition = "int(2)")
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }


    @ManyToOne
    @JoinColumn(name = "purchase_order_status", foreignKey = @ForeignKey(name = "FK_PARTNER_ORDER_STATUS_REF_ORDER_STATUS", foreignKeyDefinition = "FOREIGN KEY (purchase_order_status) REFERENCES order_status (id)"))
    public OrderStatus getPurchaseOrderStatus() {
        return purchaseOrderStatus;
    }

    public void setPurchaseOrderStatus(OrderStatus purchaseOrderStatus) {
        this.purchaseOrderStatus = purchaseOrderStatus;
    }

    @ManyToOne
    @JoinColumn(name = "partner_order_sync_request_parent", foreignKey = @ForeignKey(name = "FK_PARTNER_ORDER_REQUEST_PARENT_REF_PARTNER_ORDER_SYNC", foreignKeyDefinition = "FOREIGN KEY (partner_order_sync_request_parent) REFERENCES partner_order_sync (id)"))
    public PartnerOrderSync getSendSyncParent() {
        return sendSyncParent;
    }

    public void setSendSyncParent(PartnerOrderSync sendSyncParent) {
        this.sendSyncParent = sendSyncParent;
    }

    public static class  Enum{
        public static Integer SUCCESSE=1;
        public static Integer FAIL=2;
    }
}
