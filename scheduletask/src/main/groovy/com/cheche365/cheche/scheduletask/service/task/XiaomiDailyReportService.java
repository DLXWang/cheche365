package com.cheche365.cheche.scheduletask.service.task;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.repository.PurchaseOrderRepository;
import com.cheche365.cheche.core.repository.TelMarketingCenterRepeatRepository;
import com.cheche365.cheche.manage.common.model.TelMarketingCenterStatus;
import com.cheche365.cheche.manage.common.repository.TelMarketingCenterRepository;
import com.cheche365.cheche.scheduletask.model.XiaomiDailyReportInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by chenxy on 2018/6/25.
 */
@Service
public class XiaomiDailyReportService {
    Logger logger = LoggerFactory.getLogger(XiaomiDailyReportService.class);
    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;
    @Autowired
    private TelMarketingCenterRepository telMarketingCenterRepository;
    @Autowired
    private TelMarketingCenterRepeatRepository telMarketingCenterRepeatRepository;

    public List<XiaomiDailyReportInfo> getXiaomiInfoList(){
        List<XiaomiDailyReportInfo> infoList = new ArrayList<>();
        try{
            XiaomiDailyReportInfo info = new XiaomiDailyReportInfo();
            Date dateStart = DateUtils.getCustomDate(new Date(), -1, 0, 0, 0);
            Date dateEnd = DateUtils.getCustomDate(new Date(), -1, 23, 59, 59);
            info.setMarketingNum(telMarketingCenterRepository.countXiaomiAll(dateStart, dateEnd).toString());
            info.setDialNum(telMarketingCenterRepeatRepository.countXiaomiCall(dateStart, dateEnd).toString());
            info.setConnectNum(telMarketingCenterRepository.countXiaomiByStatus(dateStart, dateEnd, Arrays.asList(
                TelMarketingCenterStatus.Enum.UNTREATED,
                TelMarketingCenterStatus.Enum.VACANT_NUMBER,
                TelMarketingCenterStatus.Enum.NO_ANSWER,
                TelMarketingCenterStatus.Enum.CANNOT_CONNECT)).toString());
            info.setOrderNum(purchaseOrderRepository.countXiaomiOrderNum(dateStart,dateEnd).toString());
            info.setPaid(purchaseOrderRepository.getXiaomiPaid(dateStart,dateEnd).toString());
            infoList.add(info);
        }catch(Exception e){
            logger.error(e.getMessage());
        }
        return infoList;
    }
}
