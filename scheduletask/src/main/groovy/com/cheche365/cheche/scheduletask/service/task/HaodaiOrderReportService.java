package com.cheche365.cheche.scheduletask.service.task;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.*;
import com.cheche365.cheche.core.service.CompulsoryInsuranceService;
import com.cheche365.cheche.core.service.InsuranceService;
import com.cheche365.cheche.scheduletask.model.HaodaiOrderReportModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by chenxiangyin on 2017/6/2.
 */
@Service
public class HaodaiOrderReportService {
    Logger logger = LoggerFactory.getLogger(DailyInsuranceBasicReportService.class);
    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;
    @Autowired
    private PartnerUserRepository partnerUserRepository;
    @Autowired
    private InsuranceService insuranceService;
    @Autowired
    private CompulsoryInsuranceService compulsoryInsuranceService;
    /**
     * 发送打回邮件
     * 根据打回状态，发送给客服或者内勤
     */
    public List<HaodaiOrderReportModel> getEmailContent() {
        List<HaodaiOrderReportModel> content = new ArrayList<>();
        List<PurchaseOrder> purchaseOrderList = purchaseOrderRepository.findOrderByHaoDaiExcel();
        for(PurchaseOrder purchaseOrder:purchaseOrderList){
            HaodaiOrderReportModel model = new HaodaiOrderReportModel();
            PartnerUser partnerUser=partnerUserRepository.findFirstByPartnerAndUser(ApiPartner.Enum.HAODAI_PARTNER_20, purchaseOrder.getApplicant());
            Insurance insurance = insuranceService.findByQuoteRecordId(purchaseOrder.getObjId());
            CompulsoryInsurance compulsoryInsurance = compulsoryInsuranceService.findByQuoteRecordId(purchaseOrder.getObjId());
            model.setUid(partnerUser == null ? "" : partnerUser.getPartnerId());
            model.setUpdateDate(purchaseOrder.getUpdateTime() != null ? DateUtils.getDateString(purchaseOrder.getUpdateTime(), DateUtils.DATE_SHORTDATE_PATTERN) : "");
            model.setCity(purchaseOrder.getArea().getName());
            if(purchaseOrder.getAuto() != null){
                model.setPlateNo(purchaseOrder.getAuto().getLicensePlateNo());
            }
            model.setOrderNo(purchaseOrder.getOrderNo());
            model.setOrderStatus(purchaseOrder.getStatus().getStatus());
            model.setMobile(purchaseOrder.getApplicant().getMobile());
            model.setApplicantName(purchaseOrder.getAuto().getOwner());
            if(insurance != null){//优先商业否则交强
                model.setCommercial(insurance.getPremium().toString());
                model.setInsuranceCompany(insurance.getInsuranceCompany().getName());
                model.setCommercialNo(insurance.getPolicyNo());
            }
            if(compulsoryInsurance != null){
                model.setInsuranceCompany(compulsoryInsurance.getInsuranceCompany().getName());
                model.setCompulsory(compulsoryInsurance.getCompulsoryPremium() != null?compulsoryInsurance.getCompulsoryPremium().toString():"");
                model.setAutoTax(compulsoryInsurance.getAutoTax() != null?compulsoryInsurance.getAutoTax().toString():"");
                model.setCompulsoryNo(compulsoryInsurance != null?compulsoryInsurance.getPolicyNo():"");
            }
            //添加收件人的电话
            model.setRecipientMobile(purchaseOrder.getDeliveryAddress().getMobile()!= null?purchaseOrder.getDeliveryAddress().getMobile():"");
            content.add(model);
        }
        return content;
    }
}
