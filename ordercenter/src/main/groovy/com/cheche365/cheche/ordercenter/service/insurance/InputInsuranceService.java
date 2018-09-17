package com.cheche365.cheche.ordercenter.service.insurance;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.common.util.DoubleUtils;
import com.cheche365.cheche.core.constants.AnswernConstant;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.*;
import com.cheche365.cheche.core.service.*;
import com.cheche365.cheche.core.util.BeanUtil;
import com.cheche365.cheche.manage.common.service.InsurancePurchaseOrderRebateManageService;
import com.cheche365.cheche.manage.common.service.OrderInsurancePackageService;
import com.cheche365.cheche.manage.common.service.reverse.OrderReverse;
import com.cheche365.cheche.manage.common.web.model.InsurancePurchaseOrderRebateViewModel;
import com.cheche365.cheche.ordercenter.constants.OrderCenterConstants;
import com.cheche365.cheche.ordercenter.exception.OrderCenterException;
import com.cheche365.cheche.ordercenter.service.DataHistoryLogService;
import com.cheche365.cheche.ordercenter.service.user.OrderCenterInternalUserManageService;
import com.cheche365.cheche.common.util.StringUtil;
import com.cheche365.cheche.manage.common.web.model.ModelAndViewResult;
import com.cheche365.cheche.ordercenter.web.model.order.OrderInsuranceViewModel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 录入保单业务逻辑
 * Created by sunhuazhong on 2015/2/28.
 */
@Service
@Transactional
public class InputInsuranceService extends DataHistoryLogService {

    Logger logger = LoggerFactory.getLogger(InputInsuranceService.class);

    @Autowired
    private OrderCenterInternalUserManageService orderCenterInternalUserManageService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private InsuranceRepository insuranceRepository;

    @Autowired
    private CompulsoryInsuranceRepository compulsoryInsuranceRepository;

    @Autowired
    private InsuranceCompanyRepository insuranceCompanyRepository;

    @Autowired
    private AutoRepository autoRepository;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;


    @Autowired
    private DeliveryInfoRepository deliveryInfoRepository;

    @Autowired
    private QuoteRecordRepository quoteRecordRepository;


    @Autowired
    private InsurancePurchaseOrderRebateRepository insurancePurchaseOrderRebateRepository;

    @Autowired
    private InsurancePurchaseOrderRebateManageService insurancePurchaseOrderRebateService;

    @Autowired
    private OrderOperationInfoService orderOperationInfoService;

    @Autowired
    private OrderInsurancePackageService orderInsurancePackageService;


    /**
     * 校验是否更改商业险保费
     * @param premiumBI
     * @param purchaseOrder
     * @return
     */
    public boolean checkPremium(Double premiumBI, Double premiumCI, Double autoTax, PurchaseOrder purchaseOrder){
        if(purchaseOrder.getPayableAmount().compareTo(DoubleUtils.displayDoubleValue(premiumBI + premiumCI + autoTax))!=0){
            return false;
        }
        return true;
    }

    /**
     * 校验是是否更改快递单号
     * @param trackingNo
     * @param purchaseOrder
     * @return
     */
    private boolean checkTrackingNo(String trackingNo,String expressCompany,PurchaseOrder purchaseOrder){
        if(purchaseOrder.getTrackingNo()==null){
            return false;
        }
        if(!purchaseOrder.getTrackingNo().equals(StringUtil.convertNull(trackingNo))||
            (purchaseOrder.getDeliveryInfo()!=null&&!purchaseOrder.getDeliveryInfo().getExpressCompany().equals(expressCompany))){
            return false;
        }
        return true;
    }



    public void saveInsurance(OrderReverse model) {
        // 验证车主是否存在
        if(!checkAutoByIdentity(model.getIdentity(),model.getIdentityType())){
            throw new OrderCenterException(
                ModelAndViewResult.RESULT_FAIL_NO_IDENTITY_CODE, ModelAndViewResult.RESULT_FAIL_NO_IDENTITY_MESSAGE);
        }

        // 验证车辆是否存在
        if(!checkAutoByLicenseNo(model.getLicensePlateNo())){
            throw new OrderCenterException(
                ModelAndViewResult.RESULT_FAIL_NO_LICENSE_NO_CODE, ModelAndViewResult.RESULT_FAIL_NO_LICENSE_NO_MESSAGE);
        }

        // 验证保险公司是否存在
        if(!checkInsuranceCompanyByName(model.getInsuranceCompany())){
            throw new OrderCenterException(
                ModelAndViewResult.RESULT_FAIL_NO_INSURANCE_COMPANY_CODE, ModelAndViewResult.RESULT_FAIL_NO_INSURANCE_COMPANY_MESSAGE);
        }

        // 验证订单是否存在
        if(!checkPurchaseOrderById(model.getPurchaseOrderId())){
            throw new OrderCenterException(
                ModelAndViewResult.RESULT_FAIL_NO_PURCHASE_ORDER_CODE, ModelAndViewResult.RESULT_FAIL_NO_PURCHASE_ORDER_MESSAGE);
        }

        PurchaseOrder purchaseOrder=purchaseOrderRepository.findOne(model.getPurchaseOrderId());
        //保存保险公司
        saveInsuranceCompany(model);

        //保存报价
        saveQuoteRecord(model);

        // 保存商业险保单
        saveCommercialInsurance(model);

        // 保存交强险保单
        saveCompulsoryInsurance(model);
        // 判断订单数据是否改变（保费,快递单号,快递公司,保险公司)
        if (!checkPurchaseOrderByChange(purchaseOrder, model)) {
            savePurchaseOrder(purchaseOrder, model);
        }
        //保存订单佣金费率信息
        createInsurancePurchaseOrderRebate(purchaseOrder,model);
    }
    private void saveInsuranceCompany(OrderReverse model){
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findOne(model.getPurchaseOrderId());
        QuoteRecord quoteRecord = quoteRecordRepository.findOne(purchaseOrder.getObjId());
        if(!quoteRecord.getInsuranceCompany().getId().equals(model.getInsuranceCompany())){
            InsuranceCompany insuranceCompany=insuranceCompanyRepository.findOne(model.getInsuranceCompany());
            logger.debug("update purchase order insurance company, orderNo:{}, original insurance company:{}, current insurance company:{}",
                purchaseOrder.getOrderNo(), quoteRecord.getInsuranceCompany().getName(), insuranceCompany.getName());
            quoteRecord.setInsuranceCompany(insuranceCompany);
            quoteRecordRepository.save(quoteRecord);
            String comment=purchaseOrder.getComment()==null?"":purchaseOrder.getComment()+";";
            purchaseOrder.setComment(new StringBuffer(comment)
                .append(DateUtils.getDateString(new Date(), DateUtils.DATE_LONGTIME24_PATTERN))
                .append(",").append(orderCenterInternalUserManageService.getCurrentInternalUser().getName()).append(",")
                .append(quoteRecord.getInsuranceCompany().getName()).append("->")
                .append(insuranceCompany.getName()).toString());
            purchaseOrderRepository.save(purchaseOrder);
        }
    }

    private boolean checkPurchaseOrderByChange(PurchaseOrder purchaseOrder,OrderReverse model){
        if(!checkPremium(model.getCommercialPremium(), model.getCompulsoryPremium(),
            model.getAutoTax(), purchaseOrder)){
            return false;
        }
        if(!checkTrackingNo(model.getTrackingNo(), model.getExpressCompany(), purchaseOrder)){
            return false;
        }
        return true;
    }

    private boolean checkPurchaseOrderById(Long purchaseOrderId) {
        if(purchaseOrderId == null) {
            return false;
        }
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findOne(purchaseOrderId);
        if(purchaseOrder != null) {
            return true;
        } else {
            return false;
        }
    }

    private boolean checkInsuranceCompanyByName(Long insuranceCompanyId) {
        InsuranceCompany insuranceCompany = insuranceCompanyRepository.findOne(insuranceCompanyId);
        if(insuranceCompany != null) {
            return true;
        } else {
            return false;
        }
    }

    private boolean checkAutoByIdentity(String identity,IdentityType identityType) {
        // 证件类型：身份证
        Auto auto = autoRepository.findFirstByIdentityAndIdentityType(identity, identityType);
        return auto == null? false : true;
    }

    private boolean checkAutoByLicenseNo(String licenseNo) {
        List<Auto> autos = autoRepository.findByLicensePlateNo(licenseNo);
        if(autos != null && autos.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    private QuoteRecord saveQuoteRecord(OrderReverse model){
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findOne(model.getPurchaseOrderId());
        QuoteRecord quoteRecord = quoteRecordRepository.findOne(purchaseOrder.getObjId());
        InsuranceCompany company = insuranceCompanyRepository.findOne(model.getInsuranceCompany());
        quoteRecord.setInsuranceCompany(company);
        quoteRecord.setInsurancePackage(orderInsurancePackageService.createInsurancePackage(model));
        quoteRecord.setPremium(model.getCommercialPremium());
        quoteRecord.setEffectiveDate(DateUtils.getDate(model.getCommercialEffectiveDate(), DateUtils.DATE_SHORTDATE_PATTERN));
        quoteRecord.setCompulsoryEffectiveDate(DateUtils.getDate(model.getCompulsoryEffectiveDate(), DateUtils.DATE_SHORTDATE_PATTERN));
        quoteRecord.setExpireDate(DateUtils.getDate(model.getCommercialExpireDate(), DateUtils.DATE_SHORTDATE_PATTERN));
        quoteRecord.setCompulsoryExpireDate(DateUtils.getDate(model.getCompulsoryExpireDate(), DateUtils.DATE_SHORTDATE_PATTERN));
        quoteRecord.setUpdateTime(new Date());
        String[] contains = new String[]{"compulsoryPremium", "autoTax", "thirdPartyPremium", "thirdPartyAmount",
            "damagePremium", "damageAmount", "theftPremium", "theftAmount", "enginePremium", "driverPremium", "driverAmount",
            "passengerPremium", "passengerAmount", "spontaneousLossPremium", "spontaneousLossAmount", "glassPremium", "scratchAmount",
            "scratchPremium", "unableFindThirdPartyPremium","designatedRepairShopPremium", "damageIop", "thirdPartyIop", "theftIop", "engineIop", "driverIop", "passengerIop", "spontaneousLossIop", "scratchIop"};
        BeanUtil.copyPropertiesContain(model, quoteRecord, contains);
        quoteRecord.setIopTotal(quoteRecord.sumIopItems());
        return quoteRecordRepository.save(quoteRecord);
    }


    private Insurance saveCommercialInsurance(OrderReverse model) {
        if(model.getCommercialPremium() <= 0){
            return null;
        }
        Insurance insurance = null;
        InsuranceCompany insuranceCompany = null;
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findOne(model.getPurchaseOrderId());
        QuoteRecord quoteRecord = quoteRecordRepository.findOne(purchaseOrder.getObjId());
        if(quoteRecord != null) {
            insurance = insuranceRepository.findFirstByQuoteRecordOrderByCreateTimeDesc(quoteRecord);
            insuranceCompany = quoteRecord.getInsuranceCompany();
        }
        if(insurance == null) {
            // 根据保单详情生成商业险保单
            insurance = getInsuranceByDetails(model, purchaseOrder, quoteRecord);
        } else {
            //已经存在的更新保单信息
            String[] insuranceContains = new String[]{"thirdPartyPremium",
                "thirdPartyAmount", "damagePremium", "damageAmount", "theftPremium", "enginePremium",
                "engineAmount", "driverPremium", "driverAmount", "passengerPremium", "passengerAmount","passengerCount", "spontaneousLossPremium",
                "glassPremium", "glassAmount", "scratchAmount", "scratchPremium", "unableFindThirdPartyPremium","damageIop", "thirdPartyIop",
                "theftIop", "engineIop", "driverIop", "passengerIop", "scratchIop","spontaneousLossIop", "discount","applicantName","applicantIdNo","designatedRepairShopPremium"};
            //super.createLog(tempInsurance,insuranceContains);
            BeanUtil.copyPropertiesContain(model, insurance, insuranceContains);
            insurance.setInsurancePackage(quoteRecord.getInsurancePackage());
            insurance.setPolicyNo(model.getCommercialPolicyNo());
            insurance.setEffectiveDate(
                DateUtils.getDate(model.getCommercialEffectiveDate(), DateUtils.DATE_SHORTDATE_PATTERN));
            insurance.setExpireDate(
                DateUtils.getDate(model.getCommercialExpireDate(), DateUtils.DATE_SHORTDATE_PATTERN));
            insurance.setPremium(model.getCommercialPremium());
            insurance.setInsuranceImage(getInsuranceImageUrl(model.getInsuranceImage()));
            insurance.setEffectiveHour(model.getCommercialEffectiveHour());
            insurance.setExpireHour(model.getCommercialExpireHour());
            insurance.setIopTotal(quoteRecord.getIopTotal());
            insurance.setApplicantIdentityType(model.getApplicantIdType());
            if(model.getTheftAmount()>0){
                insurance.setTheftAmount(model.getTheftAmount());
            }
            if(model.getSpontaneousLossAmount()>0){
                insurance.setSpontaneousLossAmount(model.getSpontaneousLossAmount());
            }
            insurance.setUpdateTime(Calendar.getInstance().getTime());
        }
        insurance.setInsuranceCompany(insuranceCompany);//保险公司
        if (insuranceCompany.equals(InsuranceCompany.Enum.ANSWERN_65000))
            insurance.setProportion(AnswernConstant.PROPORTION);
        Insurance.setInsuranceReferences(purchaseOrder, quoteRecord, purchaseOrder.getOperator(), insurance);
        return insuranceRepository.save(insurance);
    }

    private CompulsoryInsurance saveCompulsoryInsurance(OrderReverse model) {
        if(model.getCompulsoryPremium() <= 0){
            return null;
        }
        CompulsoryInsurance compulsoryInsurance = null;
        InsuranceCompany insuranceCompany = null;
        // 订单
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findOne(model.getPurchaseOrderId());
        QuoteRecord quoteRecord = quoteRecordRepository.findOne(purchaseOrder.getObjId());
        if(quoteRecord != null) {
            compulsoryInsurance = compulsoryInsuranceRepository.findFirstByQuoteRecordOrderByCreateTimeDesc(quoteRecord);
            insuranceCompany = quoteRecord.getInsuranceCompany();
        }
        if(compulsoryInsurance == null) {
            compulsoryInsurance = getCompulsoryInsuranceByDetails(model, purchaseOrder, quoteRecord);
        } else {
            //super.createLog(tempCompulsoryInsurance,new String[]{"compulsoryPolicyNo","compulsoryPremium","autoTax","effectiveDate","expireDate","discount"});
            compulsoryInsurance.setPolicyNo(model.getCompulsoryPolicyNo());
            compulsoryInsurance.setEffectiveDate(
                DateUtils.getDate(model.getCompulsoryEffectiveDate(), DateUtils.DATE_SHORTDATE_PATTERN));
            compulsoryInsurance.setExpireDate(
                DateUtils.getDate(model.getCompulsoryExpireDate(), DateUtils.DATE_SHORTDATE_PATTERN));
            compulsoryInsurance.setEffectiveHour(model.getCompulsoryEffectiveHour());
            compulsoryInsurance.setExpireHour(model.getCompulsoryExpireHour());
            compulsoryInsurance.setCompulsoryPremium(model.getCompulsoryPremium());
            compulsoryInsurance.setAutoTax(model.getAutoTax());
            compulsoryInsurance.setDiscount(model.getDiscountCI());
            compulsoryInsurance.setInsuranceImage(getInsuranceImageUrl(model.getCompulsoryInsuranceImage()));
            compulsoryInsurance.setStamp(getInsuranceImageUrl(model.getCompulsoryStampFile()));
            compulsoryInsurance.setUpdateTime(Calendar.getInstance().getTime());
            compulsoryInsurance.setApplicantName(model.getApplicantName());
            compulsoryInsurance.setApplicantIdNo(model.getApplicantIdNo());
            compulsoryInsurance.setInsurancePackage(quoteRecord.getInsurancePackage());
            compulsoryInsurance.setApplicantIdentityType(model.getApplicantIdType());
        }
        compulsoryInsurance.setInsuranceCompany(insuranceCompany);
        return compulsoryInsuranceRepository.save(compulsoryInsurance);
    }

    private void createInsurancePurchaseOrderRebate(PurchaseOrder purchaseOrder,OrderReverse model){
        InsurancePurchaseOrderRebateViewModel insurancePurchaseOrderRebateViewModel=model.getInsurancePurchaseOrderRebateViewModel();
        insurancePurchaseOrderRebateViewModel.setDownRebateChannel(RebateChannel.Enum.REBATE_CHANNEL_INSTITUTION);
        insurancePurchaseOrderRebateViewModel.setDownChannelId(model.getInstitution());
        insurancePurchaseOrderRebateViewModel.setCommercialPremium(model.getCommercialPremium());
        insurancePurchaseOrderRebateViewModel.setCompulsoryPremium(model.getCompulsoryPremium());
        insurancePurchaseOrderRebateViewModel.setPurchaseOrderId(purchaseOrder.getId());
        insurancePurchaseOrderRebateService.savePurchaseOrderRebate(insurancePurchaseOrderRebateViewModel);
    }

    /**
     * 根据保单详情生成交强险保单
     * @param model
     * @return
     */
    private CompulsoryInsurance getCompulsoryInsuranceByDetails(OrderReverse model, PurchaseOrder order, QuoteRecord quoteRecord) {
        User user = order.getApplicant();
        Auto auto = order.getAuto();
        InternalUser operator = orderCenterInternalUserManageService.getCurrentInternalUser();
        InsuranceCompany insuranceCompany = quoteRecord.getInsuranceCompany();
        InsurancePackage insurancePackage=quoteRecord.getInsurancePackage();
        CompulsoryInsurance compulsoryInsurance = new CompulsoryInsurance();
        compulsoryInsurance.setCompulsoryPremium(model.getCompulsoryPremium());//交强险
        compulsoryInsurance.setAutoTax(model.getAutoTax());//车船税
        compulsoryInsurance.setEffectiveDate(
            DateUtils.getDate(model.getCompulsoryEffectiveDate(), DateUtils.DATE_SHORTDATE_PATTERN));//有效日期
        compulsoryInsurance.setExpireDate(
            DateUtils.getDate(model.getCompulsoryExpireDate(), DateUtils.DATE_SHORTDATE_PATTERN));//失效日期
        compulsoryInsurance.setEffectiveHour(model.getCompulsoryEffectiveHour());
        compulsoryInsurance.setExpireHour(model.getCompulsoryExpireHour());
        compulsoryInsurance.setPolicyNo(model.getCompulsoryPolicyNo());//保单号
        compulsoryInsurance.setDiscount(model.getDiscountCI());//浮动系数
        compulsoryInsurance.setInsuredName(auto.getOwner());//被保险人名称
        compulsoryInsurance.setInsuredIdNo(auto.getIdentity());//被保险人证件号码
        compulsoryInsurance.setInsuredMobile(order.getDeliveryAddress() == null ?
            "" : order.getDeliveryAddress().getMobile());//被保险人手机
        compulsoryInsurance.setInsuredEmail(order.getDeliveryAddress() == null ?
            "" : order.getDeliveryAddress().getMobile() + OrderCenterConstants.DEFAULT_EMAIL_SUFFIX);//被保险人邮箱
        compulsoryInsurance.setApplicantName(model.getApplicantName());
        compulsoryInsurance.setApplicantIdNo(model.getApplicantIdNo());
        compulsoryInsurance.setApplicantMobile(order.getDeliveryAddress() == null ?
            "" : order.getDeliveryAddress().getMobile());//投保人手机
        compulsoryInsurance.setApplicantEmail(order.getDeliveryAddress() == null ?
            "" : order.getDeliveryAddress().getMobile() + OrderCenterConstants.DEFAULT_EMAIL_SUFFIX);//投保人邮箱
        compulsoryInsurance.setInsuranceImage(getInsuranceImageUrl(model.getCompulsoryInsuranceImage()));
        compulsoryInsurance.setStamp(getInsuranceImageUrl(model.getCompulsoryStampFile()));
        compulsoryInsurance.setApplicant(user);//用户
        compulsoryInsurance.setAuto(auto);//车辆
        compulsoryInsurance.setQuoteRecord(quoteRecord);//报价记录
        compulsoryInsurance.setInsurancePackage(insurancePackage);//保单套餐
        compulsoryInsurance.setInsuranceCompany(insuranceCompany);//保险公司

        compulsoryInsurance.setEffectiveHour(OrderCenterConstants.INSURANCE_EFFECTIVE_HOUR);//生效小时
        compulsoryInsurance.setExpireHour(OrderCenterConstants.INSURANCE_EXPIRE_HOUR);//失效小时
        compulsoryInsurance.setOperator(operator);//操作人
        compulsoryInsurance.setCreateTime(Calendar.getInstance().getTime());
        compulsoryInsurance.setUpdateTime(Calendar.getInstance().getTime());
        return compulsoryInsurance;
    }

    /**
     * 根据保单详情生成商业险保单
     * @param model
     * @return
     */
    private Insurance getInsuranceByDetails(OrderReverse model, PurchaseOrder order, QuoteRecord quoteRecord) {
        User user = order.getApplicant();
        Auto auto = order.getAuto();
        InternalUser operator = orderCenterInternalUserManageService.getCurrentInternalUser();
        Insurance insurance = new Insurance();
        String[] insuranceContains = new String[]{"thirdPartyPremium",
            "thirdPartyAmount", "damagePremium", "damageAmount", "theftPremium", "theftAmount", "enginePremium",
            "engineAmount", "driverPremium", "driverAmount", "passengerPremium", "passengerAmount","passengerCount", "spontaneousLossPremium",
            "spontaneousLossAmount", "glassPremium", "glassAmount", "scratchAmount", "scratchPremium","unableFindThirdPartyPremium", "damageIop", "thirdPartyIop",
            "theftIop", "engineIop", "driverIop", "passengerIop", "scratchIop","spontaneousLossIop", "insuredIdNo", "insuredName","discount","designatedRepairShopPremium"};
        BeanUtil.copyPropertiesContain(model, insurance, insuranceContains);
        insurance.setPremium(model.getCommercialPremium());//商业险总保费
        insurance.setInsuranceImage(getInsuranceImageUrl(model.getInsuranceImage()));
        insurance.setEffectiveDate(
            DateUtils.getDate(model.getCommercialEffectiveDate(), DateUtils.DATE_SHORTDATE_PATTERN));//有效日期
        insurance.setExpireDate(
            DateUtils.getDate(model.getCommercialExpireDate(), DateUtils.DATE_SHORTDATE_PATTERN));//失效日期
        insurance.setEffectiveHour(model.getCommercialEffectiveHour());
        insurance.setExpireHour(model.getCommercialExpireHour());
        insurance.setPolicyNo(model.getCommercialPolicyNo());//保单号
        insurance.setInsuredName(auto.getOwner());//被保险人名称
        insurance.setInsuredIdNo(auto.getIdentity());//被保险人证件号码
        insurance.setInsuredMobile(order.getDeliveryAddress() == null ?
            "" : order.getDeliveryAddress().getMobile());//被保险人手机
        insurance.setInsuredEmail(order.getDeliveryAddress() == null ?
            "" : order.getDeliveryAddress().getMobile() + OrderCenterConstants.DEFAULT_EMAIL_SUFFIX);//被保险人邮箱
        insurance.setApplicantName(model.getApplicantName());
        insurance.setApplicantIdNo(model.getApplicantIdNo());
        insurance.setApplicantMobile(order.getDeliveryAddress() == null ?
            "" : order.getDeliveryAddress().getMobile());//投保人手机
        insurance.setApplicantEmail(order.getDeliveryAddress() == null ?
            "" : order.getDeliveryAddress().getMobile() + OrderCenterConstants.DEFAULT_EMAIL_SUFFIX);//投保人邮箱
        insurance.setApplicant(user);//用户
        insurance.setAuto(auto);//车辆
        insurance.setQuoteRecord(quoteRecord);//报价记录
        insurance.setInsurancePackage(quoteRecord.getInsurancePackage());//保单套餐
        insurance.setEffectiveHour(OrderCenterConstants.INSURANCE_EFFECTIVE_HOUR);//生效小时
        insurance.setExpireHour(OrderCenterConstants.INSURANCE_EXPIRE_HOUR);//失效小时
        insurance.setOperator(operator);//操作人
        insurance.setCreateTime(Calendar.getInstance().getTime());
        insurance.setUpdateTime(Calendar.getInstance().getTime());
        insurance.setIopTotal(quoteRecord.getIopTotal());
        return insurance;
    }

    /**
     * 保费与订单对不上时需将订单更新为页面录入的
     * @param model
     */
    public void savePurchaseOrder(PurchaseOrder purchaseOrder,OrderReverse model){
        double sumPremium = model.getCommercialPremium()+model.getCompulsoryPremium()+model.getAutoTax();
        logger.debug("update purchase order payable amount, orderNo:{}, original payable amount:{}, current payable amount:{}",
            purchaseOrder.getOrderNo(), purchaseOrder.getPayableAmount(), sumPremium);
        DeliveryInfo deliveryInfo=createDeliveryInfo(purchaseOrder, model);
        purchaseOrder.appendDescription(",订单应付金额由" + purchaseOrder.getPayableAmount() + "变为" + sumPremium);
        purchaseOrder.setPayableAmount(sumPremium);
        purchaseOrder.setUpdateTime(new Date());
        purchaseOrder.setTrackingNo(model.getTrackingNo());
        purchaseOrder.setDeliveryInfo(deliveryInfo);
        purchaseOrderRepository.save(purchaseOrder);
    }

    public DeliveryInfo createDeliveryInfo(PurchaseOrder purchaseOrder, OrderReverse model) {
        DeliveryInfo deliveryInfo = purchaseOrder.getDeliveryInfo();
        if (deliveryInfo == null) {
            deliveryInfo = new DeliveryInfo();
            deliveryInfo.setCreateTime(Calendar.getInstance().getTime());
        }
        deliveryInfo.setExpressCompany(model.getExpressCompany());
        deliveryInfo.setTrackingNo(model.getTrackingNo());
        deliveryInfo.setUpdateTime(Calendar.getInstance().getTime());
        deliveryInfo.setOperator(orderCenterInternalUserManageService.getCurrentInternalUser());
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

    /**
     * 由订单管理连接进来根据id取值
     * @param id
     * @return
     */
    public OrderInsuranceViewModel getPaidPurchaseOrderById(Long id) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findOne(id);
        QuoteRecord quoteRecord = quoteRecordRepository.findOne(purchaseOrder.getObjId());
        Insurance insurance = insuranceRepository.findFirstByQuoteRecordOrderByCreateTimeDesc(quoteRecord);
        CompulsoryInsurance compulsoryInsurance = compulsoryInsuranceRepository.findFirstByQuoteRecordOrderByCreateTimeDesc(quoteRecord);
        OrderOperationInfo orderOperationInfo=orderOperationInfoService.getByPurchaseOrder(purchaseOrder);
        OrderInsuranceViewModel model = new OrderInsuranceViewModel();
        model.setIdentity(purchaseOrder.getAuto().getIdentity());
        model.setIdentityType(purchaseOrder.getAuto().getIdentityType());
        model.setLicensePlateNo(purchaseOrder.getAuto().getLicensePlateNo());
        model.setInsuranceCompany(quoteRecord.getInsuranceCompany().getId());
        model.setTrackingNo(purchaseOrder.getDeliveryInfo()!=null?purchaseOrder.getDeliveryInfo().getTrackingNo():null);
        model.setExpressCompany(purchaseOrder.getDeliveryInfo() != null ? purchaseOrder.getDeliveryInfo().getExpressCompany() : null);
        if(orderOperationInfo.getConfirmOrderDate()!=null){
            model.setConfirmOrderDate(DateUtils.getDateString(orderOperationInfo.getConfirmOrderDate(),DateUtils.DATE_LONGTIME24_PATTERN));
        }
        if(insurance != null || compulsoryInsurance != null) {
            if(insurance != null) {
                String[] insuranceContains = new String[]{"thirdPartyPremium",
                    "thirdPartyAmount", "damagePremium", "damageAmount", "theftPremium", "theftAmount", "enginePremium",
                    "engineAmount", "driverPremium", "driverAmount", "passengerPremium", "passengerAmount","passengerCount", "spontaneousLossPremium",
                    "spontaneousLossAmount", "glassPremium", "glassAmount", "scratchAmount", "scratchPremium", "insuredIdNo", "insuredName","unableFindThirdPartyPremium", "discount","applicantName","applicantIdType","applicantIdNo","designatedRepairShopPremium"};

                BeanUtil.copyPropertiesContain(insurance, model, insuranceContains);
                model.setCommercialPremium(insurance.getPremium());
                model.setGlassType(insurance.getInsurancePackage().getGlassType() == null ? 0 : insurance.getInsurancePackage().getGlassType().getId());
                model.setCommercialEffectiveDate(DateUtils.getDateString(insurance.getEffectiveDate(), DateUtils.DATE_SHORTDATE_PATTERN));
                model.setCommercialExpireDate(DateUtils.getDateString(insurance.getExpireDate(), DateUtils.DATE_SHORTDATE_PATTERN));
                model.setCommercialEffectiveHour(insurance.getEffectiveHour());
                model.setCommercialExpireHour(insurance.getExpireHour());
                model.setCommercialPolicyNo(insurance.getPolicyNo());
                model.setDamageIop(DoubleUtils.positiveDouble(insurance.getDamageIop()));
                model.setThirdPartyIop(DoubleUtils.positiveDouble(insurance.getThirdPartyIop()));
                model.setTheftIop(DoubleUtils.positiveDouble(insurance.getTheftIop()));
                model.setEngineIop(DoubleUtils.positiveDouble(insurance.getEngineIop()));
                model.setDriverIop(DoubleUtils.positiveDouble(insurance.getDriverIop()));
                model.setPassengerIop(DoubleUtils.positiveDouble(insurance.getPassengerIop()));
                model.setSpontaneousLossIop(DoubleUtils.positiveDouble(insurance.getSpontaneousLossIop()));
                model.setScratchIop(DoubleUtils.positiveDouble(insurance.getScratchIop()));
                model.setIop(insurance.getIopTotal());
                model.setInsuranceImage(getResourceAbsoluteUrl(insurance.getInsuranceImage()));
                model.setApplicantIdType(insurance.getApplicantIdentityType());
                model.setInsuredIdType(insurance.getInsuredIdentityType());
                if(!insurance.getInsurancePackage().isTheft()){
                    model.setTheftAmount(0.00);
                }
                if(!insurance.getInsurancePackage().isSpontaneousLoss()){
                    model.setSpontaneousLossAmount(0.00);
                }
            }
            if(compulsoryInsurance != null) {
                model.setCompulsoryPremium(compulsoryInsurance.getCompulsoryPremium());
                model.setAutoTax(compulsoryInsurance.getAutoTax());
                model.setDiscountCI(compulsoryInsurance.getDiscount());
                model.setCompulsoryEffectiveDate(DateUtils.getDateString(compulsoryInsurance.getEffectiveDate(), DateUtils.DATE_SHORTDATE_PATTERN));
                model.setCompulsoryExpireDate(DateUtils.getDateString(compulsoryInsurance.getExpireDate(), DateUtils.DATE_SHORTDATE_PATTERN));
                model.setCompulsoryEffectiveHour(compulsoryInsurance.getEffectiveHour());
                model.setCompulsoryExpireHour(compulsoryInsurance.getExpireHour());
                model.setCompulsoryPolicyNo(compulsoryInsurance.getPolicyNo());
                model.setApplicantName(compulsoryInsurance.getApplicantName());
                model.setApplicantIdNo(compulsoryInsurance.getApplicantIdNo());
                model.setInsuredName(compulsoryInsurance.getInsuredName());
                model.setInsuredIdNo(compulsoryInsurance.getInsuredIdNo());
                model.setCompulsoryInsuranceImage(getResourceAbsoluteUrl(compulsoryInsurance.getInsuranceImage()));
                model.setCompulsoryStampFile(getResourceAbsoluteUrl(compulsoryInsurance.getStamp()));
            }
        } else {
            String[] quoteRecordContains = new String[]{"thirdPartyPremium",
                "thirdPartyAmount", "damagePremium", "damageAmount", "theftPremium", "theftAmount", "enginePremium",
                "engineAmount", "driverPremium", "driverAmount", "passengerPremium", "passengerAmount","passengerCount", "spontaneousLossPremium",
                "spontaneousLossAmount", "glassPremium", "glassAmount", "scratchAmount","scratchPremium","unableFindThirdPartyPremium", "discount","compulsoryPremium","autoTax","designatedRepairShopPremium"};
            BeanUtil.copyPropertiesContain(quoteRecord, model, quoteRecordContains);
            model.setCommercialPremium(quoteRecord.getPremium());
            model.setCommercialEffectiveDate(DateUtils.getDateString(quoteRecord.getEffectiveDate(), DateUtils.DATE_SHORTDATE_PATTERN));
            model.setCommercialExpireDate(DateUtils.getDateString(quoteRecord.getExpireDate(), DateUtils.DATE_SHORTDATE_PATTERN));
            model.setDamageIop(DoubleUtils.positiveDouble(quoteRecord.getDamageIop()));
            model.setThirdPartyIop(DoubleUtils.positiveDouble(quoteRecord.getThirdPartyIop()));
            model.setTheftIop(DoubleUtils.positiveDouble(quoteRecord.getTheftIop()));
            model.setEngineIop(DoubleUtils.positiveDouble(quoteRecord.getEngineIop()));
            model.setDriverIop(DoubleUtils.positiveDouble(quoteRecord.getDriverIop()));
            model.setPassengerIop(DoubleUtils.positiveDouble(quoteRecord.getPassengerIop()));
            model.setSpontaneousLossIop(DoubleUtils.positiveDouble(quoteRecord.getSpontaneousLossIop()));
            model.setScratchIop(DoubleUtils.positiveDouble(quoteRecord.getScratchIop()));
        }
        InsurancePurchaseOrderRebate insurancePurchaseOrderRebate=insurancePurchaseOrderRebateRepository.findFirstByPurchaseOrder(purchaseOrder);
        if(insurancePurchaseOrderRebate!=null){
            model.setInstitution(insurancePurchaseOrderRebate.getDownChannelId());
            model.setInsurancePurchaseOrderRebateViewModel(InsurancePurchaseOrderRebateViewModel.createViewModel(insurancePurchaseOrderRebate));
        }
        return model;
    }

}
