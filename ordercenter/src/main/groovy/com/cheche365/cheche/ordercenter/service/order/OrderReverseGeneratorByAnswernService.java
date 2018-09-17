//package com.cheche365.cheche.ordercenter.service.order;
//
//import com.cheche365.cheche.core.model.*;
//import com.cheche365.cheche.core.repository.AreaRepository;
//import com.cheche365.cheche.core.repository.PurchaseOrderRepository;
//import com.cheche365.cheche.core.repository.QuoteRecordRepository;
//import com.cheche365.cheche.core.repository.UserRepository;
//import com.cheche365.cheche.core.service.AutoService;
//import com.cheche365.cheche.manage.common.service.InsurancePurchaseOrderRebateManageService;
//import com.cheche365.cheche.manage.common.web.model.InsurancePurchaseOrderRebateViewModel;
//import com.cheche365.cheche.ordercenter.web.model.order.OrderInsuranceViewModel;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
///**
// * Created by wangshaobin on 2017/8/30.
// */
//@Service
//public class OrderReverseGeneratorByAnswernService extends OrderReverseGeneratorService {
//    private Logger logger = LoggerFactory.getLogger(OrderReverseGeneratorByAnswernService.class);
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private AreaRepository areaRepository;
//
//    @Autowired
//    private AutoService autoService;
//
//    @Autowired
//    private InsurancePurchaseOrderRebateManageService insurancePurchaseOrderRebateManageService;
//
//    @Autowired
//    private QuoteRecordRepository quoteRecordRepository;
//    @Autowired
//    private PurchaseOrderRepository purchaseOrderRepository;
//
//    @Transactional(rollbackFor = Exception.class)
//    @Override
//    public void saveOrderAndInsurance(OrderInsuranceViewModel model) {
//        User user = userRepository.findOne(model.getUserId());
//        Area area = areaRepository.findOne(model.getArea());
//        if (null == area)
//            throw new RuntimeException("can not find area by area -> " + model.getArea());
//
//        Auto auto = autoService.saveOrMerge(super.createAuto(model, area), user, new StringBuilder());
//        QuoteRecord quoteRecord = super.createQuoteRecord(null, model, user, auto, area);
//        PurchaseOrder purchaseOrder = super.createPurchaseOrder(null, model, quoteRecord);
//        logger.info("generate order finish, orderNo -> {}", purchaseOrder.getOrderNo());
//        quoteRecord.setChannel(Channel.Enum.getById(model.getChannel()));
//        purchaseOrder.setSourceChannel(Channel.Enum.getById(model.getChannel()));
//        quoteRecordRepository.save(quoteRecord);
//        purchaseOrderRepository.save(purchaseOrder);
//
//        super.createPayment(purchaseOrder);
//        super.createInsurance(model, purchaseOrder, quoteRecord);
//        super.createOrderOperationInfo(purchaseOrder);
//        //保存安心回录的佣金费率：只保存下游的费率信息
//        this.createInsurancePurchaseOrderRebate(purchaseOrder, model);
//
//    }
//
//    @Override
//    protected void createInsurancePurchaseOrderRebate(PurchaseOrder purchaseOrder, OrderInsuranceViewModel model) {
//        InsurancePurchaseOrderRebateViewModel insurancePurchaseOrderRebateViewModel = model.getInsurancePurchaseOrderRebateViewModel();
//        insurancePurchaseOrderRebateViewModel.setDownRebateChannel(RebateChannel.Enum.REBATE_CHANNEL_INSTITUTION);
//        insurancePurchaseOrderRebateViewModel.setDownChannelId(model.getInstitution());
//        insurancePurchaseOrderRebateViewModel.setCommercialPremium(model.getCommercialPremium());
//        insurancePurchaseOrderRebateViewModel.setCompulsoryPremium(model.getCompulsoryPremium());
//        insurancePurchaseOrderRebateViewModel.setPurchaseOrderId(purchaseOrder.getId());
//        // insurancePurchaseOrderRebateManageService.savePurchaseOrderRebateByAnswern(insurancePurchaseOrderRebateViewModel);
//    }
//}
