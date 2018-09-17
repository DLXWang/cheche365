package com.cheche365.cheche.core.model.agent;

import com.cheche365.cheche.core.model.DescribableEntity;
import com.cheche365.cheche.core.model.PurchaseOrder;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import groovy.transform.Canonical;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@Canonical
public class ChannelAgentPurchaseOrderRebate extends DescribableEntity {

    private Logger logger = LoggerFactory.getLogger(ChannelAgentPurchaseOrderRebate.class);

    private PurchaseOrder purchaseOrder;

    private ChannelAgent channelAgent;

    private Double commercialAmount;

    private Double compulsoryAmount;

    private Double commercialRebate;

    private Double compulsoryRebate;

    @ManyToOne
    public PurchaseOrder getPurchaseOrder() {
        return purchaseOrder;
    }

    public void setPurchaseOrder(PurchaseOrder purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

    @ManyToOne
    public ChannelAgent getChannelAgent() {
        return channelAgent;
    }

    public void setChannelAgent(ChannelAgent channelAgent) {
        this.channelAgent = channelAgent;
    }

    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getCommercialAmount() {
        return commercialAmount;
    }

    public void setCommercialAmount(Double commercialAmount) {
        this.commercialAmount = commercialAmount;
    }

    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getCompulsoryAmount() {
        return compulsoryAmount;
    }

    public void setCompulsoryAmount(Double compulsoryAmount) {
        this.compulsoryAmount = compulsoryAmount;
    }

    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getCommercialRebate() {
        return commercialRebate;
    }

    public void setCommercialRebate(Double commercialRebate) {
        this.commercialRebate = commercialRebate;
    }

    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getCompulsoryRebate() {
        return compulsoryRebate;
    }

    public void setCompulsoryRebate(Double compulsoryRebate) {
        this.compulsoryRebate = compulsoryRebate;
    }


    @PrePersist
    public void preSave(){
        logger.info("ChannelAgentPurchaseOrderRebate持久化之前 compulsoryAmount:{}, commercialAmount:{}", this.compulsoryAmount, this.commercialAmount);
    }

    @PostPersist
    public void postSave(){
        logger.info("ChannelAgentPurchaseOrderRebate持久化之后 compulsoryAmount:{}, commercialAmount:{}", this.compulsoryAmount, this.commercialAmount);
    }

    @PreUpdate
    public void preUpdate(){
        logger.info("ChannelAgentPurchaseOrderRebate更新之前 compulsoryAmount:{}, commercialAmount:{}", this.compulsoryAmount, this.commercialAmount);
    }

    @PostUpdate
    public void postUpdate(){
        logger.info("ChannelAgentPurchaseOrderRebate更新之后 compulsoryAmount:{}, commercialAmount:{}", this.compulsoryAmount, this.commercialAmount);
    }


}
