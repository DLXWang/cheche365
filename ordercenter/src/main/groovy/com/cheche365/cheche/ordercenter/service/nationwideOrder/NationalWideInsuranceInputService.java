package com.cheche365.cheche.ordercenter.service.nationwideOrder;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.common.util.DoubleUtils;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.model.Insurance;
import com.cheche365.cheche.core.repository.*;
import com.cheche365.cheche.core.service.ResourceService;
import com.cheche365.cheche.core.util.BeanUtil;
import com.cheche365.cheche.manage.common.service.reverse.OrderReverse;
import com.cheche365.cheche.ordercenter.service.DataHistoryLogService;
import com.cheche365.cheche.manage.common.service.OrderInsurancePackageService;
import com.cheche365.cheche.ordercenter.service.user.IInternalUserManageService;
import com.cheche365.cheche.manage.common.web.model.ResultModel;
import com.cheche365.cheche.ordercenter.web.model.OrderReAssignViewModel;
import com.cheche365.cheche.ordercenter.web.model.order.OrderInsuranceViewModel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by taguangyao on 2015/11/23.
 */
@Service
@Transactional
public class NationalWideInsuranceInputService extends DataHistoryLogService<OrderOperationInfo, OrderInsuranceViewModel> {
    private Logger logger = LoggerFactory.getLogger(NationalWideInsuranceInputService.class);

    @Autowired
    private InsuranceRepository insuranceRepository;

    @Autowired
    private CompulsoryInsuranceRepository compulsoryInsuranceRepository;

    @Autowired
    private QuoteRecordRepository quoteRecordRepository;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private InstitutionQuoteRecordRepository institutionQuoteRecordRepository;

    @Autowired
    private DeliveryInfoRepository deliveryInfoRepository;

    @Autowired
    private OrderCooperationInfoRepository orderCooperationInfoRepository;

    @Autowired
    private IInternalUserManageService internalUserManageService;

    @Autowired
    private OrderCooperationStatusHandler orderCooperationStatusHandler;

    @Autowired
    private OrderInsurancePackageService orderInsurancePackageService;

    @Autowired
    private ResourceService resourceService;

    public OrderInsuranceViewModel findModelByOrderCooperationNo(String orderNo) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findFirstByOrderNo(orderNo);
        OrderCooperationInfo orderCooperationInfo = orderCooperationInfoRepository.findFirstByPurchaseOrder(purchaseOrder);
        return this.createOrderInsuranceRecordViewData(orderCooperationInfo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateOrderInsuranceRecord(OrderReverse model) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findFirstByOrderNo(model.getOrderNo());
        OrderCooperationInfo orderCooperationInfo = orderCooperationInfoRepository.findFirstByPurchaseOrder(purchaseOrder);
        // 车辆信息
//        autoService.saveOrMerge(this.createAuto(model, orderCooperationInfo.getArea()), purchaseOrder.getApplicant(), new StringBuilder());
        // 快递信息
        DeliveryInfo deliveryInfo = this.createDeliveryInfo(purchaseOrder, model);
        purchaseOrder.setDeliveryInfo(deliveryInfo);
        purchaseOrderRepository.save(purchaseOrder);
        // 保单信息
        QuoteRecord quoteRecord = quoteRecordRepository.findOne(purchaseOrder.getObjId());
        this.createInsurance(model, purchaseOrder, quoteRecord);
        // 出单机构报价
        this.saveInstitutionQuoteRecord(model, orderCooperationInfo);
        // 订单合作信息
        orderCooperationStatusHandler.request(orderCooperationInfo, OrderCooperationStatus.Enum.FINISHED, null);
    }

    public ResultModel validRepetitivePolicyNo(String orderNo, String commercialPolicyNo, String compulsoryPolicyNo) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findFirstByOrderNo(orderNo);
        QuoteRecord quoteRecord = quoteRecordRepository.findOne(purchaseOrder.getObjId());
        if (StringUtils.isNotBlank(commercialPolicyNo)) {
            if (null != insuranceRepository.findByPolicyNoAndQuoteRecordNot(StringUtils.trimToEmpty(commercialPolicyNo), quoteRecord)) {
                logger.warn("policyNo {} already exists in insurance.", commercialPolicyNo);
                return new ResultModel(false, "该商业险保单号已存在");
            }
        }

        if (StringUtils.isNotBlank(compulsoryPolicyNo)) {
            if (null != compulsoryInsuranceRepository.findByPolicyNoAndQuoteRecordNot(StringUtils.trimToEmpty(compulsoryPolicyNo), quoteRecord)) {
                logger.warn("policyNo {} already exists in compulsory insurance.", compulsoryPolicyNo);
                return new ResultModel(false, "该交强险保单号已存在");
            }
        }

        return null;
    }

    public ResultModel validPremium(OrderReverse model) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findFirstByOrderNo(model.getOrderNo());
        InstitutionQuoteRecord institutionQuoteRecord = institutionQuoteRecordRepository.findFirstByPurchaseOrder(purchaseOrder);
        if (institutionQuoteRecord == null) {
            logger.warn("no institution quote record by order no:{}", model.getOrderNo());
            return new ResultModel(false, "无法录入保单，该保单无法查询出单机构报价！");
        }
        if (institutionQuoteRecord.getCommercialPremium() != null && !institutionQuoteRecord.getCommercialPremium().equals(model.getCommercialPremium())) {
            logger.warn("commercial premium by insurance is not equal to institution commercial premium by order no:{}", model.getOrderNo());
            return new ResultModel(false, "该商业险总保费(" + model.getCommercialPremium() + ")与出单机构商业险报价(" + institutionQuoteRecord.getCommercialPremium() + ")不符！");
        }
        if (institutionQuoteRecord.getCompulsoryPremium() != null && !institutionQuoteRecord.getCompulsoryPremium().equals(model.getCompulsoryPremium())) {
            logger.warn("compulsory premium by insurance is not equal to institution compulsory premium by order no:{}", model.getOrderNo());
            return new ResultModel(false, "该交强险总保费(" + model.getCompulsoryPremium() + ")与出单机构交强险报价(" + institutionQuoteRecord.getCompulsoryPremium() + ")不符！");
        }
        if (institutionQuoteRecord.getAutoTax() != null && !institutionQuoteRecord.getAutoTax().equals(model.getAutoTax())) {
            logger.warn("auto tax by insurance is not equal to institution auto tax by order no:{}", model.getOrderNo());
            return new ResultModel(false, "该车船使用税(" + model.getAutoTax() + ")与出单机构车船使用税报价(" + institutionQuoteRecord.getAutoTax() + ")不符！");
        }

        return null;
    }

    private void saveInstitutionQuoteRecord(OrderReverse model, OrderCooperationInfo orderCooperationInfo) {
        InstitutionQuoteRecord institutionQuoteRecord = institutionQuoteRecordRepository
                    .findFirstByPurchaseOrder(orderCooperationInfo.getPurchaseOrder());
        String[] properties = new String[]{
            "thirdPartyPremium", "thirdPartyAmount", "theftPremium", "theftAmount", "damagePremium", "damageAmount",
            "driverPremium", "driverAmount", "passengerPremium", "passengerAmount", "passengerCount", "enginePremium",
            "glassPremium", "scratchPremium", "scratchAmount", "spontaneousLossPremium", "spontaneousLossAmount",
            "thirdPartyIop", "theftIop", "damageIop", "driverIop", "passengerIop", "engineIop", "scratchIop"
        };
        BeanUtil.copyPropertiesContain(model, institutionQuoteRecord, properties);
        institutionQuoteRecord.setUpdateTime(Calendar.getInstance().getTime());
        institutionQuoteRecord.setOperator(internalUserManageService.getCurrentInternalUser());
        institutionQuoteRecordRepository.save(institutionQuoteRecord);
    }

    private Auto createAuto(OrderInsuranceViewModel model, Area area) {
        AutoType autoType = new AutoType();
        autoType.setModel(model.getBrand());
        Auto auto = new Auto();
        auto.setAutoType(autoType);
        auto.setEngineNo(model.getEngineNo());
        auto.setVinNo(model.getVinNo());
        auto.setIdentity(model.getIdentity());
        auto.setIdentityType(IdentityType.Enum.IDENTITYCARD);
        auto.setLicensePlateNo(model.getLicensePlateNo());
        auto.setOwner(model.getOwner());
        auto.setEnrollDate(DateUtils.getDate(model.getEnrollDate(), DateUtils.DATE_SHORTDATE_PATTERN));
        auto.setArea(area);
        return auto;
    }

    private void createInsurance(OrderReverse model, PurchaseOrder purchaseOrder, QuoteRecord quoteRecord) {
        OrderCooperationInfo orderCooperationInfo = orderCooperationInfoRepository.findFirstByPurchaseOrder(purchaseOrder);
        Insurance insurance = insuranceRepository.findFirstByQuoteRecordOrderByCreateTimeDesc(quoteRecord);
        CompulsoryInsurance compulsoryInsurance = compulsoryInsuranceRepository.findFirstByQuoteRecordOrderByCreateTimeDesc(quoteRecord);
        // 获取保单套餐，同出单机构报价套餐
        InsurancePackage insurancePackage = orderInsurancePackageService.createInsurancePackage(model);
        // 商业险
        if (model.getCommercialPremium() > 0) {
            Insurance newInsurance;
            if (null != insurance) {
                newInsurance = insurance;
                logger.debug("commercial premium is more than 0, update commercial insurance.");
            } else {
                newInsurance = new Insurance();
                logger.debug("commercial premium is more than 0, save commercial insurance.");
            }
            this.saveInsurance(model, newInsurance, quoteRecord, insurancePackage, orderCooperationInfo);
        } else {
            if (null != insurance) {
                insuranceRepository.delete(insurance);
            }
        }
        // 交强险
        if (model.getCompulsoryPremium() > 0) {
            CompulsoryInsurance newCompulsoryInsurance;
            if (null != compulsoryInsurance) {
                logger.debug("compulsory premium is more than 0, update compulsory insurance.");
                newCompulsoryInsurance = compulsoryInsurance;
            } else {
                logger.debug("compulsory premium is more than 0, save compulsory insurance.");
                newCompulsoryInsurance = new CompulsoryInsurance();
            }
            this.saveCompulsoryInsurance(model, newCompulsoryInsurance, quoteRecord, insurancePackage, orderCooperationInfo);
        } else {
            if (null != compulsoryInsurance) {
                compulsoryInsuranceRepository.delete(compulsoryInsurance);
            }
        }
    }

    private void saveInsurance(OrderReverse model, Insurance insurance,
                               QuoteRecord quoteRecord, InsurancePackage insurancePackage, OrderCooperationInfo orderCooperationInfo) {
        String[] contains = new String[]{"thirdPartyPremium",
            "thirdPartyAmount", "damagePremium", "damageAmount", "theftPremium",  "enginePremium",
            "engineAmount", "driverPremium", "driverAmount", "passengerPremium", "passengerAmount","passengerCount", "spontaneousLossPremium",
            "glassPremium", "glassAmount", "scratchAmount", "scratchPremium", "damageIop", "thirdPartyIop",
            "theftIop", "engineIop", "driverIop", "passengerIop", "scratchIop", "insuredIdNo", "insuredName", "insuranceImage","discount","applicantName","applicantIdNo"};
        BeanUtil.copyPropertiesContain(model, insurance, contains);
        //super.createLog(insurance,contains);
        insurance.setPremium(model.getCommercialPremium());
        insurance.setPolicyNo(model.getCommercialPolicyNo());
        insurance.setEffectiveDate(DateUtils.getDate(model.getCommercialEffectiveDate(), DateUtils.DATE_SHORTDATE_PATTERN));
        insurance.setEffectiveHour(model.getCommercialEffectiveHour());
        insurance.setExpireDate(DateUtils.getDate(model.getCommercialExpireDate(), DateUtils.DATE_SHORTDATE_PATTERN));
        insurance.setExpireHour(model.getCompulsoryExpireHour());
        insurance.setInsuranceImage(getInsuranceImageUrl(model.getInsuranceImage()));
        if (insurance.getId() != null) {
            insurance.setUpdateTime(new Date());
        } else {
            insurance.setCreateTime(new Date());
            insurance.setUpdateTime(new Date());
        }
        insurance.setApplicant(orderCooperationInfo.getPurchaseOrder().getApplicant());
        insurance.setAuto(orderCooperationInfo.getPurchaseOrder().getAuto());
        insurance.setQuoteRecord(quoteRecord);
        insurance.setInsurancePackage(insurancePackage);
        insurance.setInsuranceCompany(orderCooperationInfo.getInsuranceCompany());
        insurance.setInstitution(orderCooperationInfo.getInstitution());
        insurance.setOperator(internalUserManageService.getCurrentInternalUser());
        if(model.getTheftAmount()>0){
            insurance.setTheftAmount(model.getTheftAmount());
        }
        if(model.getSpontaneousLossAmount()>0){
            insurance.setSpontaneousLossAmount(model.getSpontaneousLossAmount());
        }
        insuranceRepository.save(insurance);
    }

    private void saveCompulsoryInsurance(OrderReverse model, CompulsoryInsurance compulsoryInsurance,
                                         QuoteRecord quoteRecord, InsurancePackage insurancePackage, OrderCooperationInfo orderCooperationInfo) {
        String[] contains = new String[]{"compulsoryPremium", "autoTax", "insuredIdNo", "insuredName", "insuranceImage","applicantName","applicantIdNo"};
        //super.createLog(compulsoryInsurance,contains);
        BeanUtil.copyPropertiesContain(model, compulsoryInsurance, contains);
        compulsoryInsurance.setPolicyNo(model.getCompulsoryPolicyNo());
        compulsoryInsurance.setEffectiveDate(DateUtils.getDate(model.getCompulsoryEffectiveDate(), DateUtils.DATE_SHORTDATE_PATTERN));
        compulsoryInsurance.setEffectiveHour(model.getCompulsoryEffectiveHour());
        compulsoryInsurance.setExpireDate(DateUtils.getDate(model.getCompulsoryExpireDate(), DateUtils.DATE_SHORTDATE_PATTERN));
        compulsoryInsurance.setExpireHour(model.getCompulsoryExpireHour());
        compulsoryInsurance.setInsuranceImage(getInsuranceImageUrl(model.getCompulsoryInsuranceImage()));
        compulsoryInsurance.setDiscount(model.getDiscountCI());
        if (compulsoryInsurance.getId() != null) {
            compulsoryInsurance.setUpdateTime(new Date());
        } else {
            compulsoryInsurance.setCreateTime(new Date());
            compulsoryInsurance.setUpdateTime(new Date());
        }
        compulsoryInsurance.setApplicant(orderCooperationInfo.getPurchaseOrder().getApplicant());
        compulsoryInsurance.setAuto(orderCooperationInfo.getPurchaseOrder().getAuto());
        compulsoryInsurance.setQuoteRecord(quoteRecord);
        compulsoryInsurance.setInsurancePackage(insurancePackage);
        compulsoryInsurance.setInsuranceCompany(orderCooperationInfo.getInsuranceCompany());
        compulsoryInsurance.setInstitution(orderCooperationInfo.getInstitution());
        compulsoryInsurance.setOperator(internalUserManageService.getCurrentInternalUser());

        compulsoryInsuranceRepository.save(compulsoryInsurance);
    }

    private OrderInsuranceViewModel createOrderInsuranceRecordViewData(OrderCooperationInfo orderCooperationInfo) {
        OrderInsuranceViewModel model = new OrderInsuranceViewModel();
        PurchaseOrder purchaseOrder = orderCooperationInfo.getPurchaseOrder();
        Auto auto = purchaseOrder.getAuto();
        QuoteRecord quoteRecord = quoteRecordRepository.findOne(purchaseOrder.getObjId());
        Insurance insurance = insuranceRepository.findFirstByQuoteRecordOrderByCreateTimeDesc(quoteRecord);
        CompulsoryInsurance compulsoryInsurance = compulsoryInsuranceRepository.findFirstByQuoteRecordOrderByCreateTimeDesc(quoteRecord);
        InstitutionQuoteRecord institutionQuoteRecord = institutionQuoteRecordRepository.findFirstByPurchaseOrder(purchaseOrder);

        // 车辆信息
        String[] autoContains = new String[]{"licensePlateNo", "engineNo", "owner", "vinNo", "identity"};
        BeanUtil.copyPropertiesContain(auto, model, autoContains);
        model.setEnrollDate(auto.getEnrollDate() == null ?
            "" : DateUtils.getDateString(auto.getEnrollDate(), DateUtils.DATE_SHORTDATE_PATTERN));
        model.setBrand(auto.getAutoType() == null? "" : auto.getAutoType().getModel());

        // 付款信息
        model.setInsuranceCompany(orderCooperationInfo.getInsuranceCompany().getId());
        model.setOriginalPremium(purchaseOrder.getPayableAmount());
        model.setRebateExceptPremium(purchaseOrder.getPaidAmount());
        // 快递信息/投保区域/出单机构
        model.setArea(orderCooperationInfo.getArea().getId());
        if (purchaseOrder.getDeliveryInfo() != null) {
            model.setTrackingNo(purchaseOrder.getDeliveryInfo().getTrackingNo());
            model.setExpressCompany(purchaseOrder.getDeliveryInfo().getExpressCompany());
        }
        if (orderCooperationInfo.getInstitution() != null) {
            model.setInstitution(orderCooperationInfo.getInstitution().getId());
        }

        // 投保险种信息
        if (null != compulsoryInsurance) {
            String[] compulsoryInsuranceContains = new String[]{"compulsoryPremium", "autoTax", "insuredIdNo", "insuredName","applicantName","applicantIdNo"};
            BeanUtil.copyPropertiesContain(compulsoryInsurance, model, compulsoryInsuranceContains);
            model.setCompulsoryPolicyNo(compulsoryInsurance.getPolicyNo());
            model.setCompulsoryEffectiveDate(compulsoryInsurance.getEffectiveDate() == null ?
                "" : DateUtils.getDateString(compulsoryInsurance.getEffectiveDate(), DateUtils.DATE_SHORTDATE_PATTERN));
            model.setCompulsoryEffectiveHour(compulsoryInsurance.getEffectiveHour());
            model.setCompulsoryExpireDate(compulsoryInsurance.getExpireDate() == null ?
                "" : DateUtils.getDateString(compulsoryInsurance.getExpireDate(), DateUtils.DATE_SHORTDATE_PATTERN));
            model.setCompulsoryExpireHour(compulsoryInsurance.getExpireHour());
            model.setCompulsoryInsuranceImage(getResourceAbsoluteUrl(compulsoryInsurance.getInsuranceImage()));
            model.setDiscountCI(compulsoryInsurance.getDiscount());
        }
        if(institutionQuoteRecord != null && institutionQuoteRecord.getCompulsoryPolicyNo() != null) {
            model.setQuoteCompulsoryPolicyNo(institutionQuoteRecord.getCompulsoryPolicyNo());
            if (model.getCompulsoryPolicyNo() == null || model.getCompulsoryPolicyNo().trim().equals("")) {
                model.setCompulsoryPolicyNo(institutionQuoteRecord.getCompulsoryPolicyNo());
            }
        }
        if (null != insurance) {
            String[] insuranceContains = new String[]{"thirdPartyPremium",
                "thirdPartyAmount", "damagePremium", "damageAmount", "theftPremium", "theftAmount", "enginePremium",
                "engineAmount", "driverPremium", "driverAmount", "passengerPremium", "passengerAmount","passengerCount", "spontaneousLossPremium",
                "spontaneousLossAmount", "glassPremium", "glassAmount", "scratchAmount", "scratchPremium", "insuredIdNo", "insuredName","discount","applicantName","applicantIdNo"};
            BeanUtil.copyPropertiesContain(insurance, model, insuranceContains);

            model.setGlassType(insurance.getInsurancePackage().getGlassType() == null ?
                null : insurance.getInsurancePackage().getGlassType().getId());
            model.setCommercialPremium(insurance.getPremium());
            model.setCommercialPolicyNo(insurance.getPolicyNo());
            model.setCommercialEffectiveDate(insurance.getEffectiveDate() == null ?
                "" : DateUtils.getDateString(insurance.getEffectiveDate(), DateUtils.DATE_SHORTDATE_PATTERN));
            model.setCommercialEffectiveHour(insurance.getEffectiveHour());
            model.setCommercialExpireDate(insurance.getExpireDate() == null ?
                "" : DateUtils.getDateString(insurance.getExpireDate(), DateUtils.DATE_SHORTDATE_PATTERN));
            model.setCommercialExpireHour(insurance.getExpireHour());
            model.setInsuranceImage(getResourceAbsoluteUrl(insurance.getInsuranceImage()));
            model.setDamageIop(DoubleUtils.positiveDouble(insurance.getDamageIop()));
            model.setThirdPartyIop(DoubleUtils.positiveDouble(insurance.getThirdPartyIop()));
            model.setTheftIop(DoubleUtils.positiveDouble(insurance.getTheftIop()));
            model.setEngineIop(DoubleUtils.positiveDouble(insurance.getEngineIop()));
            model.setDriverIop(DoubleUtils.positiveDouble(insurance.getDriverIop()));
            model.setPassengerIop(DoubleUtils.positiveDouble(institutionQuoteRecord.getPassengerIop()));
            model.setScratchIop(DoubleUtils.positiveDouble(insurance.getScratchIop()));
            if(!insurance.getInsurancePackage().isTheft()){
                model.setTheftAmount(0.00);
            }
            if(!insurance.getInsurancePackage().isSpontaneousLoss()){
                model.setSpontaneousLossAmount(0.00);
            }
        }
        if(institutionQuoteRecord != null && institutionQuoteRecord.getCommercialPolicyNo() != null ) {
            model.setQuoteCommercialPolicyNo(institutionQuoteRecord.getCommercialPolicyNo());
            if (model.getCommercialPolicyNo() == null || model.getCommercialPolicyNo().trim().equals(""))
                model.setCommercialPolicyNo(institutionQuoteRecord.getCommercialPolicyNo());
        }

        model.setOrderNo(purchaseOrder.getOrderNo());
        model.setInsuranceOperator(orderCooperationInfo.getOperator() == null ? "" : orderCooperationInfo.getOperator().getName());
        model.setPurchaseOrderId(purchaseOrder.getId());
        model.setCreateTime(DateUtils.getDateString(orderCooperationInfo.getCreateTime(), DateUtils.DATE_LONGTIME24_PATTERN));
        model.setUpdateTime(DateUtils.getDateString(orderCooperationInfo.getUpdateTime(), DateUtils.DATE_LONGTIME24_PATTERN));

        return model;
    }

    public DeliveryInfo createDeliveryInfo(PurchaseOrder purchaseOrder, OrderReverse model) {
        DeliveryInfo deliveryInfo = purchaseOrder.getDeliveryInfo();
        if (purchaseOrder.getDeliveryInfo() == null) {
            deliveryInfo = new DeliveryInfo();
            deliveryInfo.setCreateTime(Calendar.getInstance().getTime());
        }
        deliveryInfo.setExpressCompany(model.getExpressCompany());
        deliveryInfo.setTrackingNo(model.getTrackingNo());
        deliveryInfo.setUpdateTime(Calendar.getInstance().getTime());
        deliveryInfo.setOperator(internalUserManageService.getCurrentInternalUser());
        purchaseOrder.setTrackingNo(model.getTrackingNo());
        return deliveryInfoRepository.save(deliveryInfo);
    }

    private String getInsuranceImageUrl(String resourceAbsoluteUrl){
        if(StringUtils.isBlank(resourceAbsoluteUrl)){
            return null;
        }
        String insurancePath=resourceService.getProperties().getInsurancePath();
        int index;
        if((index=resourceAbsoluteUrl.indexOf(insurancePath))>-1){
            return resourceAbsoluteUrl.substring(index, resourceAbsoluteUrl.length());
        }
        return null;
    }

    private String getResourceAbsoluteUrl(String insuranceImageUrl){
        if(StringUtils.isBlank(insuranceImageUrl)){
            return null;
        }
        String insurancePath=resourceService.getProperties().getInsurancePath();
        int index;
        if((index=insuranceImageUrl.indexOf(insurancePath))>-1){
            return resourceService.absoluteUrl(resourceService.getResourceAbsolutePath(insurancePath),insuranceImageUrl.substring(index+insurancePath.length(),insuranceImageUrl.length()));
        }
        return null;
    }
}
