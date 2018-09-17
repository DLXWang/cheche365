package com.cheche365.cheche.scheduletask.service.task;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.repository.MarketingSuccessRepository;
import com.cheche365.cheche.scheduletask.model.PurchaseOrderInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by guoweifu on 2016/1/14.
 */
@Service
public class MarketingMobileService {

    @Autowired
    private MarketingSuccessRepository marketingSuccessRepository;

    public List<PurchaseOrderInfo> getPurchaseOrderInfos(Date startTime,Date nowTime) {
        // 领红包手机列表
        List<PurchaseOrderInfo> purchaseOrderInfoList = new ArrayList<>();
        List mobileList = marketingSuccessRepository.selectDistinctMobiles(startTime, nowTime);
        for (int i = 0; i < mobileList.size(); i++) {
            Object[] temp = (Object[]) mobileList.get(i);
            PurchaseOrderInfo purchaseOrderInfo = new PurchaseOrderInfo();
            purchaseOrderInfo.setMobile((String)temp[0]);
            purchaseOrderInfo.setMarketingName((String) temp[1]);
            purchaseOrderInfo.setTime(DateUtils.getDateString((Date) temp[2], DateUtils.DATE_LONGTIME24_PATTERN));
            purchaseOrderInfoList.add(purchaseOrderInfo);
        }
        return purchaseOrderInfoList;
    }
}
