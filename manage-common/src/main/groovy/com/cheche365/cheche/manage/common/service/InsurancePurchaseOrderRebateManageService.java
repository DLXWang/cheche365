package com.cheche365.cheche.manage.common.service;

import com.cheche365.cheche.common.util.DoubleUtils;
import com.cheche365.cheche.core.model.InsurancePurchaseOrderRebate;
import com.cheche365.cheche.core.model.PurchaseOrder;
import com.cheche365.cheche.core.repository.InsurancePurchaseOrderRebateRepository;
import com.cheche365.cheche.core.service.PurchaseOrderService;
import com.cheche365.cheche.manage.common.web.model.InsurancePurchaseOrderRebateViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by xu.yelong on 2016-05-30.
 */
@Service
public class InsurancePurchaseOrderRebateManageService {
    @Autowired
    private InsurancePurchaseOrderRebateRepository insurancePurchaseOrderRebateRepository;

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    /**
     * 保存订单佣金费率
     * @param model
     */
    public void savePurchaseOrderRebate(InsurancePurchaseOrderRebateViewModel model){
        savePurchaseOrderUpRebate(model);
        savePurchaseOrderDownRebate(model);
    }

    private InsurancePurchaseOrderRebate getRebate(InsurancePurchaseOrderRebateViewModel model){
        PurchaseOrder purchaseOrder=purchaseOrderService.findById(model.getPurchaseOrderId());
        InsurancePurchaseOrderRebate insurancePurchaseOrderRebate=insurancePurchaseOrderRebateRepository.findFirstByPurchaseOrder(purchaseOrder);
        if(insurancePurchaseOrderRebate==null){
            insurancePurchaseOrderRebate=new InsurancePurchaseOrderRebate();
            insurancePurchaseOrderRebate.setCreateTime(new Date());
            insurancePurchaseOrderRebate.setPurchaseOrder(purchaseOrder);
        }
        insurancePurchaseOrderRebate.setUpdateTime(new Date());
        return insurancePurchaseOrderRebate;
    }

    /**
     * TODO: 无法判断是否意图清零amount信息
     * 设置上游费率信息
     * **/
    public void savePurchaseOrderUpRebate(InsurancePurchaseOrderRebateViewModel model) {
        InsurancePurchaseOrderRebate insurancePurchaseOrderRebate = getRebate(model);
        Double upCommercialRebate = model.getUpCommercialRebate();
        Double upCompulsoryRebate = model.getUpCompulsoryRebate();
        Double upCommercialAmount = 0.0;
        Double upCompulsoryAmount = 0.0;
        Double commercialPremium = model.getCommercialPremium();
        Double compulsoryPremium = model.getCompulsoryPremium();
        if (commercialPremium != null && commercialPremium > 0 && upCommercialRebate != null && upCommercialRebate > 0) {
            upCommercialAmount = DoubleUtils.mul(upCommercialRebate, commercialPremium, 2) / 100;
        }
        if (compulsoryPremium != null && compulsoryPremium > 0 && upCompulsoryRebate != null && upCompulsoryRebate > 0) {
            upCompulsoryAmount = DoubleUtils.mul(upCompulsoryRebate, compulsoryPremium, 2) / 100;
        }
        if (model.getUpChannelId() != null) {
            insurancePurchaseOrderRebate.setUpChannelId(model.getUpChannelId());
        }
        if (model.getUpRebateChannel() != null) {
            insurancePurchaseOrderRebate.setUpRebateChannel(model.getUpRebateChannel());
        }
        if (insurancePurchaseOrderRebate.getUpCommercialAmount() == null) {
            insurancePurchaseOrderRebate.setUpCommercialAmount(upCommercialAmount);
        }
        if (insurancePurchaseOrderRebate.getUpCompulsoryAmount() == null) {
            insurancePurchaseOrderRebate.setUpCompulsoryAmount(upCompulsoryAmount);
        }
        if (model.getUpCommercialRebate() != null) {
            insurancePurchaseOrderRebate.setUpCommercialRebate(DoubleUtils.doubleValue(upCommercialRebate));
        }
        if (model.getUpCompulsoryRebate() != null) {
            insurancePurchaseOrderRebate.setUpCompulsoryRebate(DoubleUtils.doubleValue(upCompulsoryRebate));
        }
        insurancePurchaseOrderRebateRepository.save(insurancePurchaseOrderRebate);
    }

    /**
     * 设置下游费率信息
     * **/
    public void savePurchaseOrderDownRebate(InsurancePurchaseOrderRebateViewModel model){
        InsurancePurchaseOrderRebate insurancePurchaseOrderRebate=getRebate(model);
        Double downCommercialRebate=model.getDownCommercialRebate();
        Double downCompulsoryRebate=model.getDownCompulsoryRebate();
        Double downCommercialAmount=0.0;
        Double downCompulsoryAmount=0.0;
        Double commercialPremium=model.getCommercialPremium();
        Double compulsoryPremium=model.getCompulsoryPremium();
        if(commercialPremium!=null&&commercialPremium>0&&downCommercialRebate!=null&&downCommercialRebate>0){
            downCommercialAmount= DoubleUtils.mul(downCommercialRebate,commercialPremium,2)/100;
        }
        if(compulsoryPremium!=null&&compulsoryPremium>0&&downCompulsoryRebate!=null&&downCompulsoryRebate>0){
            downCompulsoryAmount=DoubleUtils.mul(downCompulsoryRebate,compulsoryPremium,2)/100;
        }
        insurancePurchaseOrderRebate.setDownRebateChannel(model.getDownRebateChannel());
        insurancePurchaseOrderRebate.setDownChannelId(model.getDownChannelId());
        insurancePurchaseOrderRebate.setDownCommercialAmount(downCommercialAmount);
        insurancePurchaseOrderRebate.setDownCompulsoryAmount(downCompulsoryAmount);
        insurancePurchaseOrderRebate.setDownCommercialRebate(downCommercialRebate);
        insurancePurchaseOrderRebate.setDownCompulsoryRebate(downCompulsoryRebate);
        insurancePurchaseOrderRebateRepository.save(insurancePurchaseOrderRebate);
    }




}
