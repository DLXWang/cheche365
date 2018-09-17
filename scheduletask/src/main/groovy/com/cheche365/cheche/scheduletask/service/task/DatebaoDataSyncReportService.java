package com.cheche365.cheche.scheduletask.service.task;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.common.util.StringUtil;
import com.cheche365.cheche.core.model.ApiPartner;
import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.model.OrderStatus;
import com.cheche365.cheche.core.repository.PurchaseOrderRepository;
import com.cheche365.cheche.scheduletask.constants.TaskConstants;
import com.cheche365.cheche.scheduletask.model.PurchaseOrderInfo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by wangshaobin on 2017/4/26.
 */
@Service
public class DatebaoDataSyncReportService {
    Logger logger = LoggerFactory.getLogger(DatebaoDataSyncReportService.class);

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    public Map<String,List<PurchaseOrderInfo>> getSyncDatebaoData(){
        Date startTime = null;
        Date now = DateUtils.getCurrentDate(DateUtils.DATE_LONGTIME24_PATTERN);
        Date endTime = DateUtils.getDate(DateUtils.getCustomDate(now,-1,23,59,59),DateUtils.DATE_LONGTIME24_PATTERN);
        String previousTimeStr = stringRedisTemplate.opsForValue().get(TaskConstants.DATEBAO_DATA_SYNC_TIME);
        logger.debug("从redis中获取大特保数据同步邮件的定时任务上次执行时间为{}", previousTimeStr);
        if(!StringUtils.isEmpty(previousTimeStr))
            startTime = DateUtils.getDate(previousTimeStr,DateUtils.DATE_LONGTIME24_PATTERN);
        else //如果定时任务第一次执行，redis中没有上次执行时间，默认取昨天零点
            startTime = DateUtils.getDate(DateUtils.getCustomDate(endTime,-1,0,0,0), DateUtils.DATE_LONGTIME24_PATTERN);
        List<Object[]> purchaseOrderInfo = purchaseOrderRepository.findByUpdateTimeAndChannelAndPartner(startTime, endTime, Arrays.asList(OrderStatus.Enum.PAID_3, OrderStatus.Enum.DELIVERED_4, OrderStatus.Enum.FINISHED_5), Arrays.asList(Channel.Enum.PARTNER_JINGSUANSHI_58, Channel.Enum.ORDER_CENTER_JINGSUANSHI_59), ApiPartner.Enum.DATEBAO_PARTNER_22);
        logger.debug("获取时间点大于{}的大特保数据为{}条", DateUtils.getDateString(startTime, DateUtils.DATE_LONGTIME24_PATTERN), purchaseOrderInfo.size());
        //拼装各个数据到excel
        Map<String,List<PurchaseOrderInfo>> map = getDatebaoDataExcelSheetInfo(purchaseOrderInfo);
        //设置本次定时任务的执行时间到Redis缓存中
        stringRedisTemplate.opsForValue().set(TaskConstants.DATEBAO_DATA_SYNC_TIME, DateUtils.getDateString(now, DateUtils.DATE_LONGTIME24_PATTERN));
        return map;
    }

    private Map<String,List<PurchaseOrderInfo>> getDatebaoDataExcelSheetInfo(List<Object[]> purchaseOrderInfo){
        Map<String,List<PurchaseOrderInfo>> purchaseOrderInfoListMap = new HashMap<>();
        List<PurchaseOrderInfo> purchaseOrderfoList = new ArrayList<PurchaseOrderInfo>();
        if(CollectionUtils.isNotEmpty(purchaseOrderInfo)){
            for (Object[] obj : purchaseOrderInfo){
                PurchaseOrderInfo info = new PurchaseOrderInfo();
                info.setInsuranceCompany(StringUtil.defaultNullStr(obj[0]));
                info.setInsuredName(StringUtil.defaultNullStr(obj[1]));
                info.setAccount(StringUtil.defaultNullStr(obj[2]));
                info.setOrderNo(StringUtil.defaultNullStr(obj[3]));
                info.setOrderStatus(StringUtil.defaultNullStr(obj[4]));
                info.setLicenseNo(StringUtil.defaultNullStr(obj[5]));
                info.setLinkPhone(StringUtil.defaultNullStr(obj[6]));
                info.setCommecialPremium(StringUtil.defaultNullStr(obj[7]));
                info.setCompulsoryPremium(StringUtil.defaultNullStr(obj[8]));
                info.setSubmitTime(StringUtil.formatTimeToString(obj[9]));
                info.setVinNo(StringUtil.defaultNullStr(obj[10]));
                info.setEngineNo(StringUtil.defaultNullStr(obj[11]));
                info.setEnrollDate(StringUtil.defaultNullStr(obj[12]));
                info.setExpireTime(StringUtil.defaultNullStr(obj[13]));
                purchaseOrderfoList.add(info);
            }
        }
        purchaseOrderInfoListMap.put("datebaoData", purchaseOrderfoList);
        return purchaseOrderInfoListMap;
    }
}
