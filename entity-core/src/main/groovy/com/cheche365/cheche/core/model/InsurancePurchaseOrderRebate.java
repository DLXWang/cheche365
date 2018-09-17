package com.cheche365.cheche.core.model;

import com.cheche365.cheche.core.repository.BaseEntity;

import javax.persistence.*;

/**
 * 保单订单佣金费率表，下单时记录代理人或BD渠道的费率信息，录入保单时记录出单机构或保险公司的费率，计算各个渠道的佣金，该表记录了订单上游和下游的佣金费率信息
 * Created by sunhuazhong on 2016/5/25.
 */
@Entity
public class InsurancePurchaseOrderRebate extends BaseEntity {
    private PurchaseOrder purchaseOrder;//订单
    private RebateChannel upRebateChannel;//上游佣金渠道
    private Long upChannelId;//上游佣金渠道id
    private Double upCommercialAmount = 0.0;//上游商业险佣金
    private Double upCompulsoryAmount = 0.0;//上游交强险佣金
    private Double upCommercialRebate = 0.0;//上游商业险费率
    private Double upCompulsoryRebate = 0.0;//上游交强险费率
    private RebateChannel downRebateChannel;//下游佣金渠道
    private Long downChannelId;//下游佣金渠道id
    private Double downCommercialAmount = 0.0;//下游商业险佣金
    private Double downCompulsoryAmount = 0.0;//下游交强险佣金
    private Double downCommercialRebate = 0.0;//下游商业险费率
    private Double downCompulsoryRebate = 0.0;//下游交强险费率
    private Double companyAmount = 0.0;//保险公司返佣

    @OneToOne
    @JoinColumn(name = "purchaseOrder", foreignKey = @ForeignKey(name = "FK_INSURANCE_PURCHASE_ORDER_REBATE_REF_PURCHASE_ORDER", foreignKeyDefinition = "FOREIGN KEY (purchase_order) REFERENCES purchase_order(id)"))
    public PurchaseOrder getPurchaseOrder() {
        return purchaseOrder;
    }

    public void setPurchaseOrder(PurchaseOrder purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

    @ManyToOne
    @JoinColumn(name = "upRebateChannel", foreignKey = @ForeignKey(name = "FK_INSURANCE_PURCHASE_ORDER_REBATE_REF_UP_REBATE_CHANNEL", foreignKeyDefinition = "FOREIGN KEY (up_rebate_channel) REFERENCES rebate_channel(id)"))
    public RebateChannel getUpRebateChannel() {
        return upRebateChannel;
    }

    public void setUpRebateChannel(RebateChannel upRebateChannel) {
        this.upRebateChannel = upRebateChannel;
    }

    @Column(columnDefinition = "BIGINT(20)")
    public Long getUpChannelId() {
        return upChannelId;
    }

    public void setUpChannelId(Long upChannelId) {
        this.upChannelId = upChannelId;
    }

    @Column(columnDefinition = "Decimal(18,2)")
    public Double getUpCommercialAmount() {
        return upCommercialAmount;
    }

    public void setUpCommercialAmount(Double upCommercialAmount) {
        this.upCommercialAmount = upCommercialAmount;
    }

    @Column(columnDefinition = "Decimal(18,2)")
    public Double getUpCompulsoryAmount() {
        return upCompulsoryAmount;
    }

    public void setUpCompulsoryAmount(Double upCompulsoryAmount) {
        this.upCompulsoryAmount = upCompulsoryAmount;
    }

    @Column(columnDefinition = "Decimal(18,2)")
    public Double getUpCommercialRebate() {
        return upCommercialRebate;
    }

    public void setUpCommercialRebate(Double upCommercialRebate) {
        this.upCommercialRebate = upCommercialRebate;
    }

    @Column(columnDefinition = "Decimal(18,2)")
    public Double getUpCompulsoryRebate() {
        return upCompulsoryRebate;
    }

    public void setUpCompulsoryRebate(Double upCompulsoryRebate) {
        this.upCompulsoryRebate = upCompulsoryRebate;
    }

    @ManyToOne
    @JoinColumn(name = "downRebateChannel", foreignKey = @ForeignKey(name = "FK_INSURANCE_PURCHASE_ORDER_REBATE_REF_DOWN_REBATE_CHANNEL", foreignKeyDefinition = "FOREIGN KEY (down_rebate_channel) REFERENCES rebate_channel(id)"))
    public RebateChannel getDownRebateChannel() {
        return downRebateChannel;
    }

    public void setDownRebateChannel(RebateChannel downRebateChannel) {
        this.downRebateChannel = downRebateChannel;
    }

    @Column(columnDefinition = "BIGINT(20)")
    public Long getDownChannelId() {
        return downChannelId;
    }

    public void setDownChannelId(Long downChannelId) {
        this.downChannelId = downChannelId;
    }

    @Column(columnDefinition = "Decimal(18,2)")
    public Double getDownCommercialAmount() {
        return downCommercialAmount;
    }

    public void setDownCommercialAmount(Double downCommercialAmount) {
        this.downCommercialAmount = downCommercialAmount;
    }

    @Column(columnDefinition = "Decimal(18,2)")
    public Double getDownCompulsoryAmount() {
        return downCompulsoryAmount;
    }

    public void setDownCompulsoryAmount(Double downCompulsoryAmount) {
        this.downCompulsoryAmount = downCompulsoryAmount;
    }

    @Column(columnDefinition = "Decimal(18,2)")
    public Double getDownCommercialRebate() {
        return downCommercialRebate;
    }

    public void setDownCommercialRebate(Double downCommercialRebate) {
        this.downCommercialRebate = downCommercialRebate;
    }

    @Column(columnDefinition = "Decimal(18,2)")
    public Double getDownCompulsoryRebate() {
        return downCompulsoryRebate;
    }

    public void setDownCompulsoryRebate(Double downCompulsoryRebate) {
        this.downCompulsoryRebate = downCompulsoryRebate;
    }

    @Column(columnDefinition = "Decimal(18,2)")
    public Double getCompanyAmount() {
        return companyAmount;
    }

    public void setCompanyAmount(Double companyAmount) {
        this.companyAmount = companyAmount;
    }
}
