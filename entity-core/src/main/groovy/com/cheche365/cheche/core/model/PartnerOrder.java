package com.cheche365.cheche.core.model;

import com.cheche365.cheche.core.service.listener.EntityChangeListener;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by zhaozhong on 2016/1/14.
 */
@Entity
@EntityListeners(EntityChangeListener.class)
public class PartnerOrder {

    private Integer id;
    private ApiPartner ApiPartner;
    private String partnerOrderNo;
    private PartnerUser partnerUser;
    private PurchaseOrder purchaseOrder;
    private String channel;
    private Date createTime;
    private Date updateTime;
    private String notifyInfo;
    private String state;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "partner_third", columnDefinition = "partner_third", referencedColumnName = "id", nullable = false)
    public ApiPartner getApiPartner() {
        return ApiPartner;
    }

    public void setApiPartner(ApiPartner apiPartner) {
        this.ApiPartner = apiPartner;
    }

    @Column
    public String getPartnerOrderNo() {
        return partnerOrderNo;
    }

    public void setPartnerOrderNo(String partnerOrderNo) {
        this.partnerOrderNo = partnerOrderNo;
    }

    @ManyToOne
    @JoinColumn(columnDefinition = "partner_user_id", referencedColumnName = "id", name = "partner_user_id")
    public PartnerUser getPartnerUser() {
        return partnerUser;
    }

    public void setPartnerUser(PartnerUser partnerUser) {
        this.partnerUser = partnerUser;
    }

    @ManyToOne
    @JoinColumn(columnDefinition = "purchase_order_id", referencedColumnName = "id", name = "purchase_order_id")
    public PurchaseOrder getPurchaseOrder() {
        return purchaseOrder;
    }

    public void setPurchaseOrder(PurchaseOrder purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

    @Column
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Column
    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Column
    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
    @Column(name = "notify_info",columnDefinition = "VARCHAR(1024)")
    public String getNotifyInfo() {
        return notifyInfo;
    }

    public void setNotifyInfo(String notifyInfo) {
        this.notifyInfo = notifyInfo;
    }

    @Column(name = "state",columnDefinition = "VARCHAR(1024)")
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PartnerOrder that = (PartnerOrder) o;

        return id.equals(that.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }


}
