package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.common.util.DoubleUtils;
import com.cheche365.cheche.common.util.StringUtil;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.PurchaseOrderRepository;
import com.cheche365.cheche.core.service.*;
import com.cheche365.cheche.email.model.EmailInfo;
import com.cheche365.cheche.scheduletask.model.MessageInfo;
import com.cheche365.cheche.scheduletask.model.PurchaseOrderInfo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * Created by yellow on 2017/10/25.
 */
@Service
public class AnswernOrderReportByDayTask extends BaseTask {

    private String emailconfigPath = "/emailconfig/answern_finished_order_by_day_report.yml";

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private QuoteRecordService quoteRecordService;

    @Autowired
    private InsuranceService insuranceService;

    @Autowired
    private CompulsoryInsuranceService compulsoryInsuranceService;

    @Autowired
    private PurchaseOrderGiftService purchaseOrderGiftService;

    private Logger logger= LoggerFactory.getLogger(AnswernOrderReportByDayTask.class);

    @Override
    protected void doProcess() throws Exception {
        messageInfoList.add(getMessageInfo());
    }

    private MessageInfo getMessageInfo() throws IOException {
        List<PurchaseOrderInfo> purchaseOrderInfos = getData();
        logger.info("answer finished order report ,data size ->{} ",purchaseOrderInfos.size());
        EmailInfo emailInfo = assembleEmailInfo(emailconfigPath, null);
        addSimpleAttachment(emailInfo, emailconfigPath, null, purchaseOrderInfos);
        return MessageInfo.createMessageInfo(emailInfo);
    }

    private List<PurchaseOrderInfo> getData() {
        Date date = new Date();
        List<PurchaseOrderInfo> infoList = new ArrayList<>();
        Date startDate = DateUtils.getCustomDate(date, -1, 0, 0, 0);
        Date endDate = DateUtils.getCustomDate(date, -1, 23, 59, 59);
        List<PurchaseOrder> purchaseOrders = purchaseOrderRepository.findOrderByInsuranceCompanyAndStatus(OrderStatus.Enum.FINISHED_5, startDate, endDate, InsuranceCompany.Enum.ANSWERN_65000);
        for (PurchaseOrder purchaseOrder : purchaseOrders) {
            PurchaseOrderInfo info = new PurchaseOrderInfo();
            QuoteRecord quoteRecord = quoteRecordService.getById(purchaseOrder.getObjId());
            Insurance insurance = insuranceService.findByQuoteRecord(quoteRecord);
            CompulsoryInsurance compulsoryInsurance = compulsoryInsuranceService.findByQuoteRecord(quoteRecord);
            info.setChannel(purchaseOrder.getSourceChannel().getDescription());
            info.setOrderNo(purchaseOrder.getOrderNo());
            info.setEnrollDate(DateUtils.getDateString(quoteRecord.getEffectiveDate(), DateUtils.DATE_SHORTDATE2_PATTERN));
            info.setLicenseNo(purchaseOrder.getAuto().getLicensePlateNo());
            info.setMobile(purchaseOrder.getApplicant().getMobile());
            info.setOwner(quoteRecord.getAuto().getOwner());
            info.setInsuredName(insurance != null?insurance.getInsuredName() :compulsoryInsurance.getInsuredName());
            info.setApplicantName(insurance != null?insurance.getApplicantName():compulsoryInsurance.getApplicantName());
            info.setCommecialPremium(String.valueOf(insurance != null ? insurance.getPremium() : 0));
            info.setCompulsoryPremium(String.valueOf(compulsoryInsurance != null ? compulsoryInsurance.getCompulsoryPremium() : 0));
            info.setAutoTax(String.valueOf(compulsoryInsurance != null ? compulsoryInsurance.getAutoTax() : 0));
            info.setPremiumSum(String.valueOf((insurance != null ? insurance.getPremium() : 0) + (compulsoryInsurance != null ? compulsoryInsurance.getCompulsoryPremium() : 0) + (compulsoryInsurance != null ? compulsoryInsurance.getAutoTax() : 0)));
            List<Gift> gifts = purchaseOrderGiftService.findGiftByPurchaseOrder(purchaseOrder);
            if (CollectionUtils.isNotEmpty(gifts)) {
                for (Gift gift : gifts) {
                    int quantity = gift.getQuantity() == null ? 1 : gift.getQuantity();
                    if (gift.getGiftAmount() == null || gift.getGiftAmount() == 0.00) {
                        info.setGiftDetail(StringUtil.convertNull(info.getGiftDetail()) + gift.getGiftType().getName()
                            + "：" + (StringUtils.isEmpty(gift.getGiftDisplay()) ? "*" : gift.getGiftDisplay() + "元 * ")
                            + quantity + (gift.getUnit() == null ? "" : gift.getUnit()) + ";");
                    } else {
                        Double giftAmount = StringUtils.isEmpty(info.getActivityFavour()) ? 0.00 : Double.valueOf(info.getActivityFavour());
                        info.setActivityFavour(String.valueOf(giftAmount + (gift.getGiftAmount() * quantity)));
                    }
                }
            }
            info.setDamagePremium(insurance!= null ?(DoubleUtils.moreThanZero(insurance.getDamagePremium()) ? "是" : "否"):"");
            if(purchaseOrder.getDeliveryAddress() != null){
                info.setDeliveryAddress(StringUtil.convertNull(purchaseOrder.getDeliveryAddress().getProvinceName()) + StringUtil.convertNull(purchaseOrder.getDeliveryAddress().getCityName()) + StringUtil.convertNull(purchaseOrder.getDeliveryAddress().getStreet()));
                info.setLinkMan(StringUtil.convertNull(purchaseOrder.getDeliveryAddress().getName()));
                info.setDeliveryMobile(StringUtil.convertNull(purchaseOrder.getDeliveryAddress().getMobile()));
            }
            infoList.add(info);
        }
        return infoList;
    }
}
