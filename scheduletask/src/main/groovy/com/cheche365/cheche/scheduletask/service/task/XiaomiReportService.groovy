package com.cheche365.cheche.scheduletask.service.task

import com.cheche365.cheche.common.util.DateUtils
import com.cheche365.cheche.core.repository.PurchaseOrderRepository
import com.cheche365.cheche.scheduletask.model.XiaomiReportInfo
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Created by chenxy on 2018/5/29.
 */
@Service
public class XiaomiReportService {
    Logger logger = LoggerFactory.getLogger(XiaomiReportService.class);
    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    public List<XiaomiReportInfo> getXiaomiInfoList(Date dateStart, Date dateEnd) {
        List<Object[]> result = purchaseOrderRepository.getXiaomiData(dateStart, dateEnd);
        List<XiaomiReportInfo> xiaomiInfoList = new ArrayList<>();
        try {
            for (Object[] obj : result) {
                XiaomiReportInfo info = new XiaomiReportInfo();
                info.setiPolicyNo(obj[0].toString());
                info.setCiPolicyNo(obj[1].toString());
                info.setAllPremium(obj[2].toString());
                info.setiPremium(obj[3].toString());
                info.setCiPremium(obj[4].toString());
                info.setAutoTax(obj[5].toString());
                info.setiEffectiveDate(obj[6] == null ? "" : DateUtils.getDateString((Date) obj[6], DateUtils.DATE_SHORTDATE_PATTERN));
                info.setiExpireDate(obj[7] == null ? "" : DateUtils.getDateString((Date) obj[7], DateUtils.DATE_SHORTDATE_PATTERN));
                info.setCiEffectiveDate(obj[8] == null ? "" : DateUtils.getDateString((Date) obj[8], DateUtils.DATE_SHORTDATE_PATTERN));
                info.setCiExpireDate(obj[9] == null ? "" : DateUtils.getDateString((Date) obj[9], DateUtils.DATE_SHORTDATE_PATTERN));
                info.setLicensePlateNo(obj[10].toString());
                info.setXiaomiId(obj[11].toString());

                info.setArea(obj[12]?.toString());
                info.setInsuredMobile(obj[13] == null ? "" : obj[13].toString());
                info.setInsuredName(obj[14]?.toString());
                info.setInsuredIdNo(obj[15]?.toString());
                info.setInsuredIdNo2(obj[15]?.toString());
                info.setApplicantName(obj[16]?.toString());
                info.setApplicantMobile(obj[17] == null ? "" : obj[17].toString());
                info.setApplicantIdNo(obj[18].toString());
                info.setPayTime(obj[19] == null ? "" : DateUtils.getDateString((Date) obj[19], DateUtils.DATE_LONGTIME24_PATTERN));
                info.setXiaomiId(obj[11] == null ? "" : obj[11].toString());
                info.setConfirmOrderDate(obj[21] ? DateUtils.getDateString((Date) obj[21], DateUtils.DATE_LONGTIME24_PATTERN) : '');
                info.setPaidAmount(obj[22]?.toString());
                info.setSubtraction(obj[23]?.toString());

                info.setInsureComp(obj[24]?.toString());
                info.setOwner(obj[25]?.toString());
                info.setOwnerIdNo(obj[26]?.toString());
                info.setOrderNo(obj[27]?.toString());
                info.setNull1("");
                info.setCiPoint("0");
                info.setiPoint("3");
                info.setNull2("");
                info.setPayChannel(obj[32]?.toString());
                info.setUserMobile(obj[33]?.toString());
                info.setNull3("");
                info.setInsuredIdNo2(obj[35]?.toString());

                info.setVinNo(obj[36]?.toString());
                info.setEngineNo(obj[37]?.toString());
                info.setEnrollDate(obj[38] ? DateUtils.getDateString((Date) obj[38], DateUtils.DATE_SHORTDATE_PATTERN) : '');
                info.setCarType(obj[39] == null ? "" : obj[39].toString());
                info.setAddress(obj[40]?.toString());
                info.setAddrName(obj[41]?.toString());
                info.setAddrMobile(obj[42] == null ? "" : obj[42].toString());
                info.setCreateTime(obj[43] ? DateUtils.getDateString((Date) obj[43], DateUtils.DATE_LONGTIME24_PATTERN) : '');
                xiaomiInfoList.add(info);
            }
        } catch (Exception e) {
            logger.error("小米装入view数据异常", e);
        }
        return xiaomiInfoList;
    }
}
