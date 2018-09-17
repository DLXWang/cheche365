package com.cheche365.cheche.scheduletask.service.task

import com.cheche365.cheche.common.math.NumberUtils
import com.cheche365.cheche.common.util.DateUtils
import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.repository.PurchaseOrderRepository
import com.cheche365.cheche.scheduletask.model.DatebaoEmailInfo
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import java.lang.reflect.InvocationTargetException

/**
 * Created by yinJianBin on 2017/5/18.
 */
@Service
class DatebaoOrderReportService {

    def logger = LoggerFactory.getLogger(DatebaoOrderReportService.class)

    @Autowired
    PurchaseOrderRepository purchaseOrderRepository


    def getEmailDataList() {
        def yesterdayStart = DateUtils.getCustomDate(new Date(), -1, 00, 00, 00)
        def yesterdayEnd = DateUtils.getCustomDate(new Date(), -1, 23, 59, 59)
        def datebaoChannelList = Arrays.asList(Channel.Enum.PARTNER_JINGSUANSHI_58.getId(), Channel.Enum.ORDER_CENTER_JINGSUANSHI_59.getId())
        def objectsList = purchaseOrderRepository.findDatebaoOrders(yesterdayStart, yesterdayEnd, datebaoChannelList)

        def fieldNameList = Arrays.asList("parterUserId", "orderNo", "orderCreateTime", "orderStatus", "cityName", "insuranceCompanyName",
                "mobile", "nickName", "ownerMobile", "source", "registerChannel", "licensePlateNo", "vinNo", "engineNo", "enrollDate", "seats",
                "brandAndModel", "transferDate", "insranceEffectiveDate", "insranceExpireDate", "insrancePolicyNo", "compulsoryEffectiveDate",
                "compulsoryExpireDate", "compulsoryPolicyNo", "payableAmount", "paidAmount", "compulsoryPremium", "autoTax", "commecialPremium",
                "damagePremium", "damageAmount", "thirdPartyPremium", "thirdPartyAmount", "driverPremium", "driverAmount", "passengerPremium",
                "passengerAmount", "theftPremium", "theftAmount", "scratchPremium", "scratchAmount", "spontaneousLossPremium", "spontaneousLossAmount",
                "enginePremium", "engineAmount", "ownerName", "ownerIdentityType", "ownerIdentity", "applicantName", "applicantIdentityType",
                "applicantIdentity", "insuredName", "insuredIdentityType", "insuredIdentity", "paymentId", "paymentType", "amount", "status", "updateTime",
                "paymentChannel", "outTradeNo", "thirdpartyPaymentNo", "receiver", "receiverIdentity", "receiverMobile", "sendTime", "sendPeriod", "address")
        def emailInfoList = objectMapper(objectsList, fieldNameList)
//        List<DatebaoEmailInfo> emailInfoList = this.processData(objectsList)

        logger.info("大特保昨天的订单统计数据共[{}]条", emailInfoList.size())
        emailInfoList
    }


    def objectMapper(List<Object[]> objectsList, List<String> fieldNameList) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        def emailInfoList = new ArrayList<>(objectsList.size())
        MetaClass metaClass = DatebaoEmailInfo.metaClass
        for (int i = 0; i < objectsList.size(); i++) {
            def objects = objectsList.get(i)
            def model = new DatebaoEmailInfo()
            for (int j = 0; j < fieldNameList.size(); j++) {
                String name = fieldNameList.get(j)
                def value = objects[j]
                if (value != null) {
                    if (value in BigDecimal) {
                        value = NumberUtils.toFinancialDouble((String) objects[j])
                        value = value.equals(0d) ? "0" : (value as String) + " "
                    } else {
                        value = (String) objects[j]
                    }

                    metaClass.setAttribute(model, name, value)
                }
            }
            model.getOrderCreateTime() && model.setOrderCreateTime(model.getOrderCreateTime().substring(0, model.getOrderCreateTime().indexOf(".")))
            model.getUpdateTime() && model.setUpdateTime(model.getUpdateTime().substring(0, model.getUpdateTime().indexOf(".")))

            emailInfoList << model
        }

        emailInfoList
    }

    def processData(objectsList) {
        List<DatebaoEmailInfo> emailInfoList = new ArrayList<>(objectsList.size())
        objectsList.each {
            objects ->
                DatebaoEmailInfo datebaoEmailInfo = new DatebaoEmailInfo()

                objects[0] && datebaoEmailInfo.setParterUserId(objects[0] + "")
                objects[1] && datebaoEmailInfo.setOrderNo(objects[1] + "")
                objects[2] && datebaoEmailInfo.setOrderCreateTime(DateUtils.getDateString(objects[2], DateUtils.DATE_LONGTIME24_PATTERN) + "")
                objects[3] && datebaoEmailInfo.setOrderStatus(objects[3] + "")
                objects[4] && datebaoEmailInfo.setCityName(objects[4] + "")
                objects[5] && datebaoEmailInfo.setInsuranceCompanyName(objects[5] + "")
                objects[6] && datebaoEmailInfo.setMobile(objects[6] + "")
                objects[7] && datebaoEmailInfo.setNickName(objects[7] + "")
                objects[8] && datebaoEmailInfo.setOwnerMobile(objects[8] + "")
                objects[9] && datebaoEmailInfo.setSource(objects[9] + "")
                objects[10] && datebaoEmailInfo.setRegisterChannel(objects[10] + "")
                objects[11] && datebaoEmailInfo.setLicensePlateNo(objects[11] + "")
                objects[12] && datebaoEmailInfo.setVinNo(objects[12] + "")
                objects[13] && datebaoEmailInfo.setEngineNo(objects[13] + "")
                objects[14] && datebaoEmailInfo.setEnrollDate(DateUtils.getDateString(objects[14], DateUtils.DATE_SHORTDATE_PATTERN) + "")
                objects[15] && datebaoEmailInfo.setSeats(objects[15] + "")
                objects[16] && datebaoEmailInfo.setBrandAndModel(objects[16] + "")
                objects[17] && datebaoEmailInfo.setTransferDate(DateUtils.getDateString(objects[17], DateUtils.DATE_SHORTDATE_PATTERN))
                objects[18] && datebaoEmailInfo.setInsranceEffectiveDate(DateUtils.getDateString(objects[18], DateUtils.DATE_SHORTDATE_PATTERN) + "")
                objects[19] && datebaoEmailInfo.setInsranceExpireDate(DateUtils.getDateString(objects[19], DateUtils.DATE_SHORTDATE_PATTERN) + "")
                objects[20] && datebaoEmailInfo.setInsrancePolicyNo(objects[20] + "")
                objects[21] && datebaoEmailInfo.setCompulsoryEffectiveDate(DateUtils.getDateString(objects[21], DateUtils.DATE_SHORTDATE_PATTERN) + "")
                objects[22] && datebaoEmailInfo.setCompulsoryExpireDate(DateUtils.getDateString(objects[22], DateUtils.DATE_SHORTDATE_PATTERN) + "")
                objects[23] && datebaoEmailInfo.setCompulsoryPolicyNo(objects[23] + "")
                objects[24] && datebaoEmailInfo.setPayableAmount(objects[24] + "")
                objects[25] && datebaoEmailInfo.setPaidAmount(objects[25] + "")
                objects[26] && datebaoEmailInfo.setCompulsoryPremium(objects[26] + "")
                objects[27] && datebaoEmailInfo.setAutoTax(objects[27] + "")
                objects[28] && datebaoEmailInfo.setCommecialPremium(objects[28] + "")
                objects[29] && datebaoEmailInfo.setDamagePremium(objects[29] + "")
                objects[30] && datebaoEmailInfo.setDamageAmount(objects[30] + "")
                objects[31] && datebaoEmailInfo.setThirdPartyPremium(objects[31] + "")
                objects[32] && datebaoEmailInfo.setThirdPartyAmount(objects[32] + "")
                objects[33] && datebaoEmailInfo.setDriverPremium(objects[33] + "")
                objects[34] && datebaoEmailInfo.setDriverAmount(objects[34] + "")
                objects[35] && datebaoEmailInfo.setPassengerPremium(objects[35] + "")
                objects[36] && datebaoEmailInfo.setPassengerAmount(objects[36] + "")
                objects[37] && datebaoEmailInfo.setTheftPremium(objects[37] + "")
                objects[38] && datebaoEmailInfo.setTheftAmount(objects[38] + "")
                objects[39] && datebaoEmailInfo.setScratchPremium(objects[39] + "")
                objects[40] && datebaoEmailInfo.setScratchAmount(objects[40] + "")
                objects[41] && datebaoEmailInfo.setSpontaneousLossPremium(objects[41] + "")
                objects[42] && datebaoEmailInfo.setSpontaneousLossAmount(objects[42] + "")
                objects[43] && datebaoEmailInfo.setEnginePremium(objects[43] + "")
                objects[44] && datebaoEmailInfo.setEngineAmount(objects[44] + "")
                objects[45] && datebaoEmailInfo.setOwnerName(objects[45] + "")
                objects[46] && datebaoEmailInfo.setOwnerIdentityType(objects[46] + "")
                objects[47] && datebaoEmailInfo.setOwnerIdentity(objects[47] + "")
                objects[48] && datebaoEmailInfo.setApplicantName(objects[48] + "")
                objects[49] && datebaoEmailInfo.setApplicantIdentityType(objects[49] + "")
                objects[50] && datebaoEmailInfo.setApplicantIdentity(objects[50] + "")
                objects[51] && datebaoEmailInfo.setInsuredName(objects[51] + "")
                objects[52] && datebaoEmailInfo.setInsuredIdentityType(objects[52] + "")
                objects[53] && datebaoEmailInfo.setInsuredIdentity(objects[53] + "")
                objects[54] && datebaoEmailInfo.setPaymentId(objects[54] + "")
                objects[55] && datebaoEmailInfo.setPaymentType(objects[55] + "")
                objects[56] && datebaoEmailInfo.setAmount(objects[56] + "")
                objects[57] && datebaoEmailInfo.setStatus(objects[57] + "")
                objects[58] && datebaoEmailInfo.setUpdateTime(DateUtils.getDateString(objects[58], DateUtils.DATE_LONGTIME24_PATTERN) + "")
                objects[59] && datebaoEmailInfo.setPaymentChannel(objects[59] + "")
                objects[60] && datebaoEmailInfo.setOutTradeNo(objects[60] + "")
                objects[61] && datebaoEmailInfo.setThirdpartyPaymentNo(objects[61] + "")
                objects[62] && datebaoEmailInfo.setReceiver(objects[62] + "")
                objects[63] && datebaoEmailInfo.setReceiverIdentity(objects[63] + "")
                objects[64] && datebaoEmailInfo.setReceiverMobile(objects[64] + "")
                objects[65] && datebaoEmailInfo.setSendTime(objects[65] + "")
                objects[66] && datebaoEmailInfo.setSendPeriod(objects[66] + "")
                objects[67] && datebaoEmailInfo.setAddress(objects[67] + "")

                emailInfoList << datebaoEmailInfo
        }

        emailInfoList
    }

}
