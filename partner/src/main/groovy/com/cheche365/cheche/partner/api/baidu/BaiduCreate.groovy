package com.cheche365.cheche.partner.api.baidu

import com.cheche365.cheche.common.util.DoubleUtils
import com.cheche365.cheche.core.constants.WebConstants
import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.ApiPartner
import com.cheche365.cheche.core.model.CompulsoryInsurance
import com.cheche365.cheche.core.model.Insurance
import com.cheche365.cheche.core.model.OrderStatus
import com.cheche365.cheche.core.model.PartnerOrder
import com.cheche365.cheche.core.model.PartnerUser
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.repository.PartnerOrderRepository
import com.cheche365.cheche.core.repository.PartnerUserRepository
import com.cheche365.cheche.core.service.PurchaseOrderService
import com.cheche365.cheche.core.util.CacheUtil
import com.cheche365.cheche.partner.handler.index.PartnerIndexParams
import com.cheche365.cheche.partner.model.BaiduBillDetail
import com.cheche365.cheche.partner.model.BaiduBillStatus
import com.cheche365.cheche.partner.model.BaiduBills
import com.cheche365.cheche.partner.model.BaiduInsuranceDetail
import com.cheche365.cheche.partner.model.BaiduPolicyCar
import com.cheche365.cheche.partner.model.BaiduPolicyUser
import com.cheche365.cheche.partner.utils.BaiduEncryptUtil
import com.cheche365.cheche.partner.utils.PartnerEncodeUtil
import com.cheche365.cheche.web.service.system.SystemUrlGenerator
import groovy.util.logging.Slf4j
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.time.DateUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import java.text.SimpleDateFormat

@Slf4j
@Service
class BaiduCreate extends BaiduApi {

    @Autowired
    private PurchaseOrderService purchaseOrderService

    @Autowired
    private PartnerOrderRepository partnerOrderRepository

    @Autowired
    private PartnerUserRepository partnerUserRepository

    @Autowired
    private SystemUrlGenerator systemUrlGenerator

    def prepareData(rawData) {
        PartnerOrder partnerOrder = getPartnerOrder(rawData)
        BaiduBills baiduBills = convertToBaiduBills(partnerOrder.purchaseOrder)
        log.debug("组装生成向百度同步订单信息报文,订单号为 {},报文内容为:\n{}", partnerOrder.purchaseOrder.getOrderNo(), baiduBills.toString())
        encryptBaiduBills(baiduBills)
        signBaiduBills(baiduBills)
        return baiduBills
    }

    @Override
    List supportOrderStatus() {
        return [OrderStatus.Enum.PENDING_PAYMENT_1, OrderStatus.Enum.INSURE_FAILURE_7, OrderStatus.Enum.FINISHED_5, OrderStatus.Enum.PAID_3, OrderStatus.Enum.REFUNDED_9, OrderStatus.Enum.CANCELED_6]
    }

    private void encryptBaiduBills(BaiduBills baiduBills) {
        baiduBills.setDetails(BaiduEncryptUtil.encrypt(baiduBills.getDetails()))
        baiduBills.setPolicy_holder(BaiduEncryptUtil.encrypt(baiduBills.getPolicy_holder()))
        baiduBills.setPolicy_users(BaiduEncryptUtil.encrypt(baiduBills.getPolicy_users()))
        baiduBills.setPolicy_car(BaiduEncryptUtil.encrypt(baiduBills.getPolicy_car()))
    }

    private void signBaiduBills(BaiduBills baiduBills) {
        String sign = "";
        SortedSet<String> keys = new TreeSet<String>(baiduBills.keySet());

        for (String key : keys) {
            if (baiduBills.getSignFields().contains(key)) {
                sign = (sign + baiduBills.getFirst(key).toString());
            }
        }
        baiduBills.setSign(BaiduEncryptUtil.sign(sign))
    }

    private BaiduBills convertToBaiduBills(PurchaseOrder purchaseOrder) {
        BaiduBills baiduBills = new BaiduBills()
        baiduBills.setPartner_id(purchaseOrderService.findQuoteRecord(purchaseOrder).getInsuranceCompany())
        baiduBills.setOrder_id(purchaseOrder.getOrderNo())

        PartnerUser partnerUser = partnerUserRepository.findFirstByPartnerAndUser(ApiPartner.Enum.BAIDU_PARTNER_2, purchaseOrder.getApplicant())
        if (null == partnerUser) {
            log.error("同步订单时异常,未找到百度账户信息,订单号为 {}", purchaseOrder.getOrderNo())
            throw new BusinessException(BusinessException.Code.OBJECT_NOT_EXIST, "同步订单时异常,未找到百度账户信息.")
        }
        baiduBills.setBduid(partnerUser.getPartnerId())

        List<BaiduBillDetail> baiduBillDetails = new ArrayList<>()
        String insuredIdNo = "", insuredName = ""
        CompulsoryInsurance compulsoryInsurance = this.purchaseOrderService.getCIBillByOrder(purchaseOrder.getOrderNo(), purchaseOrder.getApplicant())
        if (compulsoryInsurance != null) {
            insuredIdNo = compulsoryInsurance.getInsuredIdNo()
            insuredName = compulsoryInsurance.getInsuredName()
            BaiduBillDetail ciBillDetail = new BaiduBillDetail()
            ciBillDetail.setId(compulsoryInsurance.getId())
            //保单号为空时缺省值
            ciBillDetail.setPolicy_num(StringUtils.isBlank(compulsoryInsurance.getPolicyNo()) ? purchaseOrder.getOrderNo() : compulsoryInsurance.getPolicyNo())
            ciBillDetail.setProduct_code("JQX")
            ciBillDetail.setProduct_name("交强险")
            ciBillDetail.setAmount(1)
            Calendar calendar = Calendar.getInstance()
            //下单生效日期为空
            ciBillDetail.setStart_date(compulsoryInsurance.getEffectiveDate() == null ? DateUtils.truncate(calendar.getTime(), Calendar.DAY_OF_MONTH) : compulsoryInsurance.getEffectiveDate())
            calendar.add(Calendar.YEAR, 1)
            //下单失效日期为空
            ciBillDetail.setEnd_date(compulsoryInsurance.getExpireDate() == null ? DateUtils.truncate(calendar.getTime(), Calendar.DAY_OF_MONTH) : compulsoryInsurance.getExpireDate())
            ciBillDetail.setPrice_insurance(0.0)//承保金额，交强险为0
            ciBillDetail.setPrice_single(compulsoryInsurance.getCompulsoryPremium())//详情单价，交强险金额
            ciBillDetail.setPrice_tax(compulsoryInsurance.getAutoTax())//详情税费，交强险为车船税
            ciBillDetail.setPrice_discount(0.0)//优惠价格,统一算到商业险部分
            //交强险+车船税
            ciBillDetail.setPrice_total(DoubleUtils.displayDoubleValue(compulsoryInsurance.getCompulsoryPremium() + compulsoryInsurance.getAutoTax()))
            baiduBillDetails.add(ciBillDetail)
        }

        Insurance insurance = this.purchaseOrderService.getInsuranceBillsByOrder(purchaseOrder.getOrderNo(), purchaseOrder.getApplicant())
        if (insurance != null) {
            insuredIdNo = insurance.getInsuredIdNo()
            insuredName = insurance.getInsuredName()
            BaiduBillDetail insuranceBillDetail = new BaiduBillDetail()
            insuranceBillDetail.setId(insurance.getId())
            //保单号为空时缺省值
            insuranceBillDetail.setProduct_code("SYX")
            insuranceBillDetail.setPolicy_num(StringUtils.isBlank(insurance.getPolicyNo()) ? purchaseOrder.getOrderNo() : insurance.getPolicyNo())
            insuranceBillDetail.setProduct_name("商业险")
            insuranceBillDetail.setAmount(1)

            Calendar calendar = Calendar.getInstance()
            //下单生效日期为空
            Date effectiveDate = insurance.getEffectiveDate() == null ? DateUtils.truncate(calendar.getTime(), Calendar.DAY_OF_MONTH) : insurance.getEffectiveDate()
            insuranceBillDetail.setStart_date(effectiveDate)
            calendar.add(Calendar.YEAR, 1)
            //下单失效日期为空
            Date expireDate = insurance.getExpireDate() == null ? DateUtils.truncate(calendar.getTime(), Calendar.DAY_OF_MONTH) : insurance.getExpireDate()

            insuranceBillDetail.setEnd_date(expireDate)
            insuranceBillDetail.setPrice_insurance(Insurance.calculateTotalAmount(insurance))//承保金额，商业险为保费总额
            insuranceBillDetail.setPrice_single(insurance.getPremium())//详情单价，商业险单价
            insuranceBillDetail.setPrice_tax(0.0)//详情税费，商业险设置为0
            //优惠价格,统一算到商业险部分
            insuranceBillDetail.setPrice_discount(DoubleUtils.displayDoubleValue(purchaseOrder.getPayableAmount() - purchaseOrder.getPaidAmount()))
            insuranceBillDetail.setPrice_total(DoubleUtils.displayDoubleValue(insuranceBillDetail.getPrice_single() - insuranceBillDetail.getPrice_discount()))

            insuranceBillDetail.setLists(generateInsuranceDetails(insurance))
            baiduBillDetails.add(insuranceBillDetail)
        }
        baiduBills.setDetails(CacheUtil.doJacksonSerialize(baiduBillDetails))

        BaiduPolicyUser policyHolder = new BaiduPolicyUser()//其他非必填信息暂时不传
        policyHolder.setUsername(purchaseOrder.getAuto().getOwner())//投保人真实姓名
        policyHolder.setMobile(purchaseOrder.getApplicant().getMobile())//投保人手机号
        baiduBills.setPolicy_holder(CacheUtil.doJacksonSerialize(policyHolder))

        List<BaiduPolicyUser> policyUsers = new ArrayList<>()
        BaiduPolicyUser policyUser = new BaiduPolicyUser()
        policyUser.setUsername(insuredName)
        policyUser.setMobile(purchaseOrder.getApplicant().getMobile())//被保人手机号
        policyUser.setId_type(1)
        policyUser.setId_num(insuredIdNo)
        policyUsers.add(policyUser)
        baiduBills.setPolicy_users(CacheUtil.doJacksonSerialize(policyUsers))

        BaiduPolicyCar policyCar = new BaiduPolicyCar()
        policyCar.setCity(String.valueOf(purchaseOrder.getArea().getId()))
        policyCar.setPlate_num(purchaseOrder.getAuto().getLicensePlateNo())
        policyCar.setEngine_num(purchaseOrder.getAuto().getEngineNo())
        policyCar.setFrame_num(purchaseOrder.getAuto().getVinNo())
        baiduBills.setPolicy_car(CacheUtil.doJacksonSerialize(policyCar))

        baiduBills.setPrice_total(DoubleUtils.displayDoubleValue(purchaseOrder.getPayableAmount()))
        baiduBills.setPrice_discount(DoubleUtils.displayDoubleValue(purchaseOrder.getPayableAmount() - purchaseOrder.getPaidAmount()))
        baiduBills.setPrice_pay(DoubleUtils.displayDoubleValue(purchaseOrder.getPaidAmount()))
        baiduBills.setCallback_detail(generateCallbackDetail(purchaseOrder))
        baiduBills.setCallback_pay(generateCallbackPay(purchaseOrder))
        baiduBills.setStatus(BaiduBillStatus.convertToBaiduBillStatus(purchaseOrder.getStatus()))
        baiduBills.setCreate_time(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(purchaseOrder.getCreateTime()))
        return baiduBills
    }

    String generateCallbackDetail(PurchaseOrder purchaseOrder) {
        StringBuilder detailUrl = new StringBuilder(WebConstants.getDomainURL())
            .append("/partner/")
            .append(purchaseOrder.sourceChannel.apiPartner.code)
            .append("/callback/order/")
            .append(purchaseOrder.getOrderNo());

        String paramStr = PartnerEncodeUtil.encodeQueryString(generateQueryString(purchaseOrder));
        return StringUtils.isBlank(paramStr) ? detailUrl.toString() : detailUrl.append("?").append(paramStr).toString();
    }

    String generateCallbackPay(PurchaseOrder purchaseOrder) {
        if (purchaseOrder.getStatus() == null || !OrderStatus.Enum.PENDING_PAYMENT_1 == purchaseOrder.getStatus()) {
            return StringUtils.EMPTY;
        }

        String url = systemUrlGenerator.toPaymentUrlOriginal(purchaseOrder.getOrderNo());
        String paramStr = PartnerEncodeUtil.encodeQueryString(generateQueryString(purchaseOrder))
        String callbackUrl= StringUtils.isBlank(paramStr) ? url : url + "&" + paramStr
        return callbackUrl
    }

    String generateQueryString(PurchaseOrder purchaseOrder) {
        // example : bduid=PevZO9dVnOa6b&mobile=Zsw6G&companyId=25000

        PartnerOrder partnerOrder = partnerOrderRepository.findFirstByPurchaseOrderId(purchaseOrder.getId())
        if (partnerOrder == null || partnerOrder.getPartnerUser() == null) {
            return StringUtils.EMPTY
        }

        PartnerUser partnerUser = partnerOrder.getPartnerUser()
        StringBuilder queryStr = new StringBuilder(PartnerIndexParams.BD_MAP_UID).append("=")
            .append(partnerUser.getPartnerId())
            .append("&").append('mobile').append("=")
            .append(BaiduEncryptUtil.encrypt(partnerUser.getUser().getMobile()))
            .append("&").append('companyId').append("=")
            .append(purchaseOrderService.findQuoteRecord(purchaseOrder).getInsuranceCompany().getId())
            .append("&").append('source').append("=bdmap")
        return queryStr.toString()
    }

    private List<BaiduInsuranceDetail> generateInsuranceDetails(Insurance insurance) {
        List<BaiduInsuranceDetail> insuranceDetails = new ArrayList<>()
        //1.机动车第三者责任保险
        Double thirdPartyPremium = insurance.getThirdPartyPremium()//保费
        if (thirdPartyPremium != null && thirdPartyPremium.compareTo(0.0) > 0) {
            BaiduInsuranceDetail thirdPartyDetail = new BaiduInsuranceDetail()
            thirdPartyDetail.setCode("thirdParty")//编码(必填)
            thirdPartyDetail.setName("机动车第三者责任保险")//名称(必填)
            thirdPartyDetail.setInsurance(insurance.getThirdPartyAmount())//承保金额，默认0，精确到小数点后两位(必填)
            thirdPartyDetail.setPrice(thirdPartyPremium)//单价，默认0，精确到小数点后两位(必填)
            insuranceDetails.add(thirdPartyDetail)
        }
        //2.机动车损失险
        Double damagePremium = insurance.getDamagePremium()//保费
        if (damagePremium != null && damagePremium.compareTo(0.0) > 0) {
            BaiduInsuranceDetail damageDetail = new BaiduInsuranceDetail()
            damageDetail.setCode("damage")//编码(必填)
            damageDetail.setName("机动车损失险")//名称(必填)
            damageDetail.setInsurance(insurance.getDamageAmount())//承保金额，默认0，精确到小数点后两位(必填)
            damageDetail.setPrice(damagePremium)//单价，默认0，精确到小数点后两位(必填)
            insuranceDetails.add(damageDetail)
        }
        //3.玻璃单独破碎险
        Double glassPremium = insurance.getGlassPremium()
        if (glassPremium != null && glassPremium.compareTo(0.0) > 0) {
            BaiduInsuranceDetail glassDetail = new BaiduInsuranceDetail()
            glassDetail.setCode("glass")//编码(必填)
            glassDetail.setName("玻璃单独破碎险")//名称(必填)
            glassDetail.setInsurance(0.0)//承保金额，默认0，精确到小数点后两位(必填)
            glassDetail.setPrice(glassPremium)//单价，默认0，精确到小数点后两位(必填)
            insuranceDetails.add(glassDetail)
        }
        //4.车身划痕损失险
        Double scratchPremium = insurance.getScratchPremium()
        if (scratchPremium != null && scratchPremium.compareTo(0.0) > 0) {
            BaiduInsuranceDetail scratchDetail = new BaiduInsuranceDetail()
            scratchDetail.setCode("scratch")//编码(必填)
            scratchDetail.setName("车身划痕损失险")//名称(必填)
            scratchDetail.setInsurance(insurance.getScratchAmount())//承保金额，默认0，精确到小数点后两位(必填)
            scratchDetail.setPrice(scratchPremium)//单价，默认0，精确到小数点后两位(必填)
            insuranceDetails.add(scratchDetail)
        }
        //5.车上人员责任险(司机)
        Double driverPremium = insurance.getDriverPremium()
        if (driverPremium != null && driverPremium.compareTo(0.0) > 0) {
            BaiduInsuranceDetail driverDetail = new BaiduInsuranceDetail()
            driverDetail.setCode("driver")//编码(必填)
            driverDetail.setName("车上人员责任险(司机)")//名称(必填)
            driverDetail.setInsurance(insurance.getDriverAmount())//承保金额，默认0，精确到小数点后两位(必填)
            driverDetail.setPrice(driverPremium)//单价，默认0，精确到小数点后两位(必填)
            insuranceDetails.add(driverDetail)
        }
        //6.车上人员责任险(乘客)
        Double passengerPremium = insurance.getPassengerPremium()
        if (passengerPremium != null && passengerPremium.compareTo(0.0) > 0) {
            BaiduInsuranceDetail passengerDetail = new BaiduInsuranceDetail()
            passengerDetail.setCode("passenger")//编码(必填)
            passengerDetail.setName("车上人员责任险(乘客)")//名称(必填)
            passengerDetail.setInsurance(insurance.getPassengerAmount())//承保金额，默认0，精确到小数点后两位(必填)
            passengerDetail.setPrice(passengerPremium)//单价，默认0，精确到小数点后两位(必填)
            insuranceDetails.add(passengerDetail)
        }
        //7.机动车盗抢险
        Double theftPremium = insurance.getTheftPremium()
        if (theftPremium != null && theftPremium.compareTo(0.0) > 0) {
            BaiduInsuranceDetail theftDetail = new BaiduInsuranceDetail()
            theftDetail.setCode("theft")//编码(必填)
            theftDetail.setName("机动车盗抢险")//名称(必填)
            theftDetail.setInsurance(insurance.getTheftAmount())//承保金额，默认0，精确到小数点后两位(必填)
            theftDetail.setPrice(theftPremium)//单价，默认0，精确到小数点后两位(必填)
            insuranceDetails.add(theftDetail)
        }
        //8.自燃损失险
        Double spontaneousLossPremium = insurance.getSpontaneousLossPremium()
        if (spontaneousLossPremium != null && spontaneousLossPremium.compareTo(0.0) > 0) {
            BaiduInsuranceDetail spontaneousLossDetail = new BaiduInsuranceDetail()
            spontaneousLossDetail.setCode("spontaneousLoss")//编码(必填)
            spontaneousLossDetail.setName("自燃损失险")//名称(必填)
            spontaneousLossDetail.setInsurance(insurance.getSpontaneousLossAmount())//承保金额，默认0，精确到小数点后两位(必填)
            spontaneousLossDetail.setPrice(spontaneousLossPremium)//单价，默认0，精确到小数点后两位(必填)
            insuranceDetails.add(spontaneousLossDetail)
        }
        //9.发动机特别损失险
        Double enginePremium = insurance.getEnginePremium()
        if (enginePremium != null && enginePremium.compareTo(0.0) > 0) {
            BaiduInsuranceDetail engineDetail = new BaiduInsuranceDetail()
            engineDetail.setCode("engine")//编码(必填)
            engineDetail.setName("发动机特别损失险(涉水险)")//名称(必填)
            engineDetail.setInsurance(0.0)//承保金额，默认0，精确到小数点后两位(必填)
            engineDetail.setPrice(enginePremium)//单价，默认0，精确到小数点后两位(必填)
            insuranceDetails.add(engineDetail)
        }
        //12.不计免赔合计
        if (insurance.getIopTotal() > 0) {
            BaiduInsuranceDetail iopTotalDetail = new BaiduInsuranceDetail()
            iopTotalDetail.setCode("iopTotal")//编码(必填)
            iopTotalDetail.setName("不计免赔险")//名称(必填)
            iopTotalDetail.setInsurance(0.0)//承保金额，默认0，精确到小数点后两位(必填)
            iopTotalDetail.setPrice(DoubleUtils.displayDoubleValue(insurance.getIopTotal()))//单价，默认0，精确到小数点后两位(必填)
            insuranceDetails.add(iopTotalDetail)
        }
        return insuranceDetails
    }
}
