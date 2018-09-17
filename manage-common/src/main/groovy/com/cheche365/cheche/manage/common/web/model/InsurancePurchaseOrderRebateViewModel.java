package com.cheche365.cheche.manage.common.web.model;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.InsurancePurchaseOrderRebate;
import com.cheche365.cheche.core.model.RebateChannel;
import com.cheche365.cheche.core.util.BeanUtil;
import org.apache.commons.beanutils.BeanUtils;

/**
 * Created by xu.yelong on 2016-05-31.
 */
public class InsurancePurchaseOrderRebateViewModel {

    private Long id;
    private Long purchaseOrderId;//订单
    private RebateChannel upRebateChannel;//上游佣金渠道
    private Long upChannelId;//上游佣金渠道id
    private Double upCommercialAmount;//上游商业险佣金
    private Double upCompulsoryAmount;//上游交强险佣金
    private Double upCommercialRebate;//上游商业险费率
    private Double upCompulsoryRebate;//上游交强险费率
    private RebateChannel downRebateChannel;//下游佣金渠道
    private Long downChannelId;//下游佣金渠道id
    private Double downCommercialAmount;//下游商业险佣金
    private Double downCompulsoryAmount;//下游交强险佣金
    private Double downCommercialRebate;//下游商业险费率
    private Double downCompulsoryRebate;//下游交强险费率
    private String createTime;//创建时间
    private String updateTime;//修改时间
    private Double commercialPremium;//商业险保费
    private Double compulsoryPremium;//交强险保费
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPurchaseOrderId() {
        return purchaseOrderId;
    }

    public void setPurchaseOrderId(Long purchaseOrderId) {
        this.purchaseOrderId = purchaseOrderId;
    }

    public Long getUpChannelId() {
        return upChannelId;
    }

    public void setUpChannelId(Long upChannelId) {
        this.upChannelId = upChannelId;
    }

    public Double getUpCommercialAmount() {
        return upCommercialAmount;
    }

    public void setUpCommercialAmount(Double upCommercialAmount) {
        this.upCommercialAmount = upCommercialAmount;
    }

    public Double getUpCompulsoryAmount() {
        return upCompulsoryAmount;
    }

    public void setUpCompulsoryAmount(Double upCompulsoryAmount) {
        this.upCompulsoryAmount = upCompulsoryAmount;
    }

    public Double getUpCommercialRebate() {
        return upCommercialRebate;
    }

    public void setUpCommercialRebate(Double upCommercialRebate) {
        this.upCommercialRebate = upCommercialRebate;
    }

    public Double getUpCompulsoryRebate() {
        return upCompulsoryRebate;
    }

    public void setUpCompulsoryRebate(Double upCompulsoryRebate) {
        this.upCompulsoryRebate = upCompulsoryRebate;
    }

    public Long getDownChannelId() {
        return downChannelId;
    }

    public void setDownChannelId(Long downChannelId) {
        this.downChannelId = downChannelId;
    }

    public Double getDownCommercialAmount() {
        return downCommercialAmount;
    }

    public void setDownCommercialAmount(Double downCommercialAmount) {
        this.downCommercialAmount = downCommercialAmount;
    }

    public Double getDownCompulsoryAmount() {
        return downCompulsoryAmount;
    }

    public void setDownCompulsoryAmount(Double downCompulsoryAmount) {
        this.downCompulsoryAmount = downCompulsoryAmount;
    }

    public Double getDownCommercialRebate() {
        return downCommercialRebate;
    }

    public void setDownCommercialRebate(Double downCommercialRebate) {
        this.downCommercialRebate = downCommercialRebate;
    }

    public Double getDownCompulsoryRebate() {
        return downCompulsoryRebate;
    }

    public void setDownCompulsoryRebate(Double downCompulsoryRebate) {
        this.downCompulsoryRebate = downCompulsoryRebate;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public Double getCommercialPremium() {
        return commercialPremium;
    }

    public void setCommercialPremium(Double commercialPremium) {
        this.commercialPremium = commercialPremium;
    }

    public Double getCompulsoryPremium() {
        return compulsoryPremium;
    }

    public void setCompulsoryPremium(Double compulsoryPremium) {
        this.compulsoryPremium = compulsoryPremium;
    }

    public RebateChannel getUpRebateChannel() {
        return upRebateChannel;
    }

    public void setUpRebateChannel(RebateChannel upRebateChannel) {
        this.upRebateChannel = upRebateChannel;
    }

    public RebateChannel getDownRebateChannel() {
        return downRebateChannel;
    }

    public void setDownRebateChannel(RebateChannel downRebateChannel) {
        this.downRebateChannel = downRebateChannel;
    }

    public static InsurancePurchaseOrderRebateViewModel createViewModel(InsurancePurchaseOrderRebate insurancePurchaseOrderRebate){
        String[] properties={"id","upChannelId","upCommercialAmount","upCompulsoryAmount","upCommercialRebate",
            "upCompulsoryRebate","downChannelId","downCommercialAmount","downCompulsoryAmount","downCommercialRebate","downCompulsoryRebate"};
        InsurancePurchaseOrderRebateViewModel viewModel=new InsurancePurchaseOrderRebateViewModel();
        BeanUtil.copyPropertiesContain(insurancePurchaseOrderRebate,viewModel,properties);
        viewModel.setPurchaseOrderId(insurancePurchaseOrderRebate.getPurchaseOrder().getId());
        viewModel.setUpRebateChannel(insurancePurchaseOrderRebate.getUpRebateChannel());
        viewModel.setDownRebateChannel(insurancePurchaseOrderRebate.getDownRebateChannel());
        viewModel.setCreateTime(DateUtils.getDateString(insurancePurchaseOrderRebate.getCreateTime(),DateUtils.DATE_LONGTIME24_PATTERN));
        viewModel.setUpdateTime(DateUtils.getDateString(insurancePurchaseOrderRebate.getUpdateTime(),DateUtils.DATE_LONGTIME24_PATTERN));
        return viewModel;
    }
}
