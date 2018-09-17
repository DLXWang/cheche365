package com.cheche365.cheche.scheduletask.service.task;

import com.cheche365.cheche.common.math.NumberUtils;
import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.common.util.StringUtil;
import com.cheche365.cheche.core.model.Gift;
import com.cheche365.cheche.core.model.GiftType;
import com.cheche365.cheche.core.repository.GiftRepository;
import com.cheche365.cheche.core.util.BeanUtil;
import com.cheche365.cheche.scheduletask.model.DailyInsuranceOfferReport;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by yinJianBin on 2017/3/14.
 */
public class BaseDailyInsuranceOfferReportService {


    @Autowired
    private GiftRepository giftRepository;

    /**
     * 将数据库返回的数据组装为报表实体
     *
     * @param dailyInsuranceOfferInfos
     * @param dateAndType
     * @return
     */
    public List<DailyInsuranceOfferReport> convertDetailList(List<Object[]> dailyInsuranceOfferInfos, String dateAndType) {
        List<DailyInsuranceOfferReport> emaildataList = new ArrayList<>();
        DailyInsuranceOfferReport dailyInsuranceOfferReport;
        for (int i = 1; i <= dailyInsuranceOfferInfos.size(); i++) {
            Object[] objects = dailyInsuranceOfferInfos.get(i - 1);
            dailyInsuranceOfferReport = new DailyInsuranceOfferReport();
            dailyInsuranceOfferReport.setId(i + "");
            dailyInsuranceOfferReport.setReportType(dateAndType);
            dailyInsuranceOfferReport.setOrderNo(objects[0] == null ? "" : objects[0] + "");
            dailyInsuranceOfferReport.setStartTime(objects[1] == null ? " " : DateUtils.getDateString((Date) objects[1], DateUtils.DATE_SHORTDATE_PATTERN));
            dailyInsuranceOfferReport.setLicensePlateNo(objects[2] == null ? " " : objects[2] + "");
            dailyInsuranceOfferReport.setMobile(objects[3] == null ? " " : objects[3] + "");
            dailyInsuranceOfferReport.setUserName(objects[4] == null ? " " : objects[4] + "");
            dailyInsuranceOfferReport.setBankName(objects[5] == null ? " " : objects[5] + "");
            dailyInsuranceOfferReport.setUserCardNo(objects[6] == null ? " " : objects[6] + "");
            dailyInsuranceOfferReport.setRewardDays(objects[7] == null ? " " : objects[7] + "");
            dailyInsuranceOfferReport.setRefundPremium(objects[8] == null ? " " : objects[8] + "");
            dailyInsuranceOfferReport.setPremium(objects[9] == null ? " " : objects[9] + "");
            dailyInsuranceOfferReport.setCompulsoryPremium(objects[10] == null ? " " : objects[10] + "");
            dailyInsuranceOfferReport.setAutoTax(objects[11] == null ? " " : objects[11] + "");
            dailyInsuranceOfferReport.setTotalPremium(objects[12] == null ? " " : objects[12] + "");
            dailyInsuranceOfferReport.setCreateTime(objects[13] == null ? " " : DateUtils.getDateString((Date) objects[13], DateUtils.DATE_LONGTIME24_PATTERN));
            dailyInsuranceOfferReport.setStatus(" ");

            String orderId = StringUtil.defaultNullStr(objects[14]);
            if (StringUtils.isNotEmpty(orderId)) {
                List<Gift> gifts = giftRepository.findMaterialGiftByOrder(NumberUtils.toLong(orderId));
                this.setGiftInfo(gifts, dailyInsuranceOfferReport);
            }

            emaildataList.add(dailyInsuranceOfferReport);
        }

        return emaildataList;
    }

    private void setGiftInfo(List<Gift> giftList, DailyInsuranceOfferReport dailyInsuranceOfferReport) {
        List<String> giftDetailsList = new ArrayList<>();
        for (Gift gift : giftList) {
            if (gift.getGiftType().getId().equals(GiftType.Enum.JINGDONG_CARD_30.getId())) {
                dailyInsuranceOfferReport.setJdCard(gift.getGiftDisplay() + " ");
            } else {
                int quantity = gift.getQuantity() == null ? 1 : gift.getQuantity();
                if (gift.getGiftAmount() == null) {
                    giftDetailsList.add(
                            StringUtil.defaultNullStr(gift.getGiftDisplay())
                                    + (BeanUtil.equalsID(gift.getGiftType(), GiftType.Enum.INSURE_GIVE_GIFT_PACK_29) ? gift.getGiftContent() : gift.getGiftType().getName())
                                    + "*" + quantity + (gift.getUnit() == null ? "" : gift.getUnit()));
                } else {
                    giftDetailsList.add(gift.getGiftType().getName() + "：" + gift.getGiftDisplay() + " * " + quantity + (gift.getUnit() == null ? "" : gift.getUnit()));
                }
            }
        }
        String giftString = String.join("、", giftDetailsList);
        dailyInsuranceOfferReport.setGift(giftString);
    }

}
