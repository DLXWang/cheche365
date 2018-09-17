package com.cheche365.cheche.scheduletask.service.task;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.repository.AppointmentInsuranceRepository;
import com.cheche365.cheche.scheduletask.model.AttachmentData;
import com.cheche365.cheche.scheduletask.model.PurchaseOrderInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by guoweifu on 2015/12/4.
 */
@Service
public class CustomerAppointmentService {

    @Autowired
    private AppointmentInsuranceRepository appointmentInsuranceRepository;

    /**
     * 获取订单信息
     * @param endTime
     * @param startTime
     * @return
     */
    public List<PurchaseOrderInfo> getPurchaseOrderInfos(Date endTime, Date startTime) {
        // 领红包手机列表
        List<PurchaseOrderInfo> purchaseOrderInfos = new ArrayList<>();
        List dataList = appointmentInsuranceRepository.selectDistinctCustomers(startTime, endTime);
        for (int i = 0; i < dataList.size(); i++) {
            Object[] temp = (Object[]) dataList.get(i);
            PurchaseOrderInfo purchaseOrderInfo = new PurchaseOrderInfo();
            // mobile, contact, expire_before, license_plate_no, create_time
            purchaseOrderInfo.setMobile((String) temp[0]);
            purchaseOrderInfo.setLinkMan((String) temp[1]);
            purchaseOrderInfo.setExpireTime(DateUtils.getDateString((Date) temp[2], DateUtils.DATE_LONGTIME24_PATTERN));
            purchaseOrderInfo.setLicenseNo((String) temp[3]);
            purchaseOrderInfo.setSubmitTime(DateUtils.getDateString((Date) temp[4], DateUtils.DATE_LONGTIME24_PATTERN));
            purchaseOrderInfo.setChannel((String) temp[5]);
            purchaseOrderInfos.add(purchaseOrderInfo);
        }
        return purchaseOrderInfos;
    }
}
