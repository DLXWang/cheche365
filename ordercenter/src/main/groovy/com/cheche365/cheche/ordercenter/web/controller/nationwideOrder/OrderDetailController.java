package com.cheche365.cheche.ordercenter.web.controller.nationwideOrder;

import com.cheche365.cheche.common.util.DoubleUtils;
import com.cheche365.cheche.core.exception.handler.LackOfSupplementInfoHandler;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.service.*;
import com.cheche365.cheche.core.util.BeanUtil;
import com.cheche365.cheche.manage.common.exception.FieldValidtorException;
import com.cheche365.cheche.ordercenter.constants.OrderCooperationInfoConstants;
import com.cheche365.cheche.ordercenter.service.nationwideOrder.InstitutionQuoteRecordService;
import com.cheche365.cheche.ordercenter.service.nationwideOrder.OrderCooperationInfoManageService;
import com.cheche365.cheche.ordercenter.web.model.nationwideOrder.InstitutionQuoteRecordViewModel;
import com.cheche365.cheche.ordercenter.web.model.nationwideOrder.OrderCooperationInfoViewModel;
import com.cheche365.cheche.ordercenter.web.model.order.OrderInsuranceViewModel;
import com.cheche365.cheche.core.model.WechatUserInfo;
import com.cheche365.cheche.core.repository.WechatUserInfoRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 订单详情
 * Created by sunhuazhong on 2015/11/17.
 */
@RestController
@RequestMapping("/orderCenter/nationwide")
public class OrderDetailController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private OrderCooperationInfoManageService orderCooperationInfoManageService;

    @Autowired
    private InstitutionQuoteRecordService institutionQuoteRecordService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Autowired
    private PurchaseOrderGiftService purchaseOrderGiftService;

    @Autowired
    private QuoteRecordService quoteRecordService;

    @Autowired
    private InsuranceService insuranceService;

    @Autowired
    private CompulsoryInsuranceService compulsoryInsuranceService;

    @Autowired
    private WechatUserInfoRepository wechatUserInfoRepository;

    @Autowired
    private QuoteSupplementInfoService quoteSupplementInfoService;

    /**
     * get order detail
     * @param id
     * @return
     */
    @RequestMapping(value = "/detail/{id}",method = RequestMethod.GET)
    public OrderCooperationInfoViewModel detail(@PathVariable Long id) {
        logger.debug("find order detail by order cooperation info id:{}", id);

        if(id == null || id < 1){
            throw new FieldValidtorException("find order detail, id can not be null or less than 1");
        }

        OrderCooperationInfo orderCooperationInfo = orderCooperationInfoManageService.findById(id);
        return createDetailViewModel(orderCooperationInfo);
    }

    private OrderCooperationInfoViewModel createDetailViewModel(OrderCooperationInfo orderCooperationInfo) {
        OrderCooperationInfoViewModel viewModel = OrderCooperationInfoViewModel.createDetailViewModel(orderCooperationInfo);
        PurchaseOrder purchaseOrder = orderCooperationInfo.getPurchaseOrder();
        // 微信用户信息
        setWechatUserInfo(purchaseOrder, viewModel);
        // 被保险人信息和险种信息
        setInsuredAndInsuranceData(purchaseOrder, viewModel);
        // 送单地址
        viewModel.setAddress(addressService.getAddress(purchaseOrder));
        // 用户来源
        viewModel.setSource(purchaseOrderService.getUserSource(purchaseOrder));
        // 优惠券信息
        viewModel.setGiftDetails(purchaseOrderGiftService.getGiftDetail(purchaseOrder));
        // 出单机构报价信息
        setQuoteRecord(orderCooperationInfo, viewModel);
        // 支付状态和出单状态信息
        setPaymentStatus(orderCooperationInfo, viewModel);
        //车辆补充信息
        viewModel.setSupplementInfos(LackOfSupplementInfoHandler.toOrderCenterFormat(quoteSupplementInfoService.getSupplementInfosByPurchaseOrder(purchaseOrder)));
        return viewModel;
    }

    protected void setPaymentStatus(OrderCooperationInfo orderCooperationInfo, OrderCooperationInfoViewModel viewModel) {
        PaymentStatus paymentStatus = orderCooperationInfoManageService.getPaymentStatus(orderCooperationInfo.getPurchaseOrder());
        PurchaseOrder purchaseOrder = orderCooperationInfo.getPurchaseOrder();
        OrderStatus orderStatus = purchaseOrder.getStatus();
        if(paymentStatus == null) {
            viewModel.setPaymentStatus(OrderCooperationInfoConstants.NO_PAYMENT_STATUS);
            viewModel.setCooperationStatus(null);
        } else if(paymentStatus.getId().equals(PaymentStatus.Enum.PAYMENTSUCCESS_2.getId())) {
            viewModel.setPaymentStatus(OrderCooperationInfoConstants.PAID_STATUS);
            viewModel.setCooperationStatus(orderCooperationInfo.getStatus());
        } else {
            if(orderStatus.getId().equals(OrderStatus.Enum.CANCELED_6.getId())
                || orderStatus.getId().equals(OrderStatus.Enum.EXPIRED_8.getId())) {
                viewModel.setPaymentStatus(OrderCooperationInfoConstants.GIVEUP_PAYMENT_STATUS);
                viewModel.setCooperationStatus(null);
            } else {
                viewModel.setPaymentStatus(OrderCooperationInfoConstants.NO_PAYMENT_STATUS);
                viewModel.setCooperationStatus(null);
            }
        }
    }
    public void setQuoteRecord(OrderCooperationInfo orderCooperationInfo, OrderCooperationInfoViewModel viewModel) {
        PurchaseOrder purchaseOrder = orderCooperationInfo.getPurchaseOrder();
        InstitutionQuoteRecord institutionQuoteRecord = institutionQuoteRecordService.getByPurchaseOrder(purchaseOrder);
        if(institutionQuoteRecord != null) {
            InstitutionQuoteRecordViewModel institutionQuoteRecordViewModel = new InstitutionQuoteRecordViewModel();
            String[] properties = new String[]{
                "id", "commercialRebate", "compulsoryRebate", "commercialPolicyNo", "compulsoryPolicyNo",
                "commercialPremium", "compulsoryPremium", "autoTax",
                "thirdPartyPremium", "thirdPartyAmount", "theftPremium", "theftAmount", "damagePremium", "damageAmount",
                "driverPremium", "driverAmount", "passengerPremium", "passengerAmount", "passengerCount", "enginePremium",
                "glassPremium", "scratchPremium", "scratchAmount", "spontaneousLossPremium", "spontaneousLossAmount"
            };
            BeanUtil.copyPropertiesContain(institutionQuoteRecord, institutionQuoteRecordViewModel, properties);
            institutionQuoteRecordViewModel.setDamageIop(DoubleUtils.positiveDouble(institutionQuoteRecord.getDamageIop()));
            institutionQuoteRecordViewModel.setThirdPartyIop(DoubleUtils.positiveDouble(institutionQuoteRecord.getThirdPartyIop()));
            institutionQuoteRecordViewModel.setTheftIop(DoubleUtils.positiveDouble(institutionQuoteRecord.getTheftIop()));
            institutionQuoteRecordViewModel.setEngineIop(DoubleUtils.positiveDouble(institutionQuoteRecord.getEngineIop()));
            institutionQuoteRecordViewModel.setDriverIop(DoubleUtils.positiveDouble(institutionQuoteRecord.getDriverIop()));
            institutionQuoteRecordViewModel.setPassengerIop(DoubleUtils.positiveDouble(institutionQuoteRecord.getPassengerIop()));
            institutionQuoteRecordViewModel.setScratchIop(DoubleUtils.positiveDouble(institutionQuoteRecord.getScratchIop()));
            viewModel.setQuoteRecord(institutionQuoteRecordViewModel);
        }
    }

    private void setInsuredAndInsuranceData(PurchaseOrder purchaseOrder, OrderCooperationInfoViewModel viewModel) {
        Auto auto = purchaseOrder.getAuto();
        viewModel.setInsuredName(auto.getOwner());
        viewModel.setInsuredIdentity(auto.getIdentity());
        viewModel.setInsuredIdentityType(auto.getIdentityType().getName());
        if(purchaseOrder.getObjId() != null) {
            QuoteRecord quoteRecord = quoteRecordService.getById(purchaseOrder.getObjId());
            CompulsoryInsurance compulsoryInsurance = compulsoryInsuranceService.findByQuoteRecord(quoteRecord);
            Insurance insurance = insuranceService.findByQuoteRecord(quoteRecord);
            OrderInsuranceViewModel orderInsuranceViewModel = new OrderInsuranceViewModel();
            if(compulsoryInsurance != null) {
                // 被保险人信息
                viewModel.setInsuredName(StringUtils.isBlank(compulsoryInsurance.getInsuredName())?
                        auto.getOwner() : compulsoryInsurance.getInsuredName());
                viewModel.setInsuredIdentity(StringUtils.isBlank(compulsoryInsurance.getInsuredIdNo())?
                        auto.getIdentity() : compulsoryInsurance.getInsuredIdNo());
                viewModel.setInsuredIdentityType(purchaseOrder.getInsuredIdentityType().getName());
                viewModel.setApplicantName(StringUtils.isEmpty(compulsoryInsurance.getApplicantName())?auto.getOwner():compulsoryInsurance.getApplicantName());
                viewModel.setApplicantIdNo(StringUtils.isEmpty(compulsoryInsurance.getApplicantIdNo())?auto.getIdentity():compulsoryInsurance.getApplicantIdNo());
                viewModel.setApplicantIdentityType(purchaseOrder.getApplicantIdentityType().getName());
                // 交强险险种信息
                orderInsuranceViewModel.setCompulsoryPolicyNo(compulsoryInsurance.getPolicyNo());
                orderInsuranceViewModel.setCompulsoryPremium(compulsoryInsurance.getCompulsoryPremium());
                orderInsuranceViewModel.setAutoTax(compulsoryInsurance.getAutoTax());
            }
            if (insurance != null) {
                // 被保险人信息
                viewModel.setInsuredName(StringUtils.isBlank(insurance.getInsuredName()) ?
                    auto.getOwner() : insurance.getInsuredName());
                viewModel.setInsuredIdentity(StringUtils.isBlank(insurance.getInsuredIdNo()) ?
                    auto.getIdentity() : insurance.getInsuredIdNo());
                viewModel.setInsuredIdentityType(purchaseOrder.getInsuredIdentityType().getName());
                viewModel.setApplicantName(StringUtils.isEmpty(insurance.getApplicantName())?auto.getOwner():insurance.getApplicantName());
                viewModel.setApplicantIdNo(StringUtils.isEmpty(insurance.getApplicantIdNo())?auto.getIdentity():insurance.getApplicantIdNo());
                viewModel.setApplicantIdentityType(purchaseOrder.getApplicantIdentityType().getName());
                // 商业险险种信息
                String[] properties = new String[]{
                    "thirdPartyPremium", "thirdPartyAmount", "scratchPremium", "scratchAmount", "damagePremium", "damageAmount",
                    "theftPremium", "theftAmount", "driverPremium", "driverAmount", "passengerPremium", "passengerAmount",
                    "enginePremium", "glassPremium", "spontaneousLossPremium", "spontaneousLossAmount"
                };
                BeanUtil.copyPropertiesContain(insurance, orderInsuranceViewModel, properties);
                orderInsuranceViewModel.setCommercialPolicyNo(insurance.getPolicyNo());
                orderInsuranceViewModel.setCommercialPremium(insurance.getPremium());
                orderInsuranceViewModel.setGlassTypeName(insurance.getInsurancePackage().getGlassType() == null ? "" :
                    insurance.getInsurancePackage().getGlassType().getName());
                orderInsuranceViewModel.setIop(insurance.getIopTotal());
            }
            viewModel.setOrderInsurance(orderInsuranceViewModel);
        }
    }

    private void setWechatUserInfo(PurchaseOrder purchaseOrder, OrderCooperationInfoViewModel viewModel) {
        WechatUserInfo wechatUserInfo = wechatUserInfoRepository.findFirstByUser(purchaseOrder.getApplicant());
        if (wechatUserInfo != null && StringUtils.isNotBlank(wechatUserInfo.getNickname())) {
            viewModel.setNickName(wechatUserInfo.getNickname());
        } else {
            viewModel.setNickName("");
        }
    }
}
