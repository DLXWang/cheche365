package com.cheche365.cheche.scheduletask.service.task;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.InsuranceCompany;
import com.cheche365.cheche.core.model.OrderStatus;
import com.cheche365.cheche.core.repository.InsuranceRepository;
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

import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.ANSWERN_65000;

/**
 * Created by wangshaobin on 2017/3/2.
 */
@Service
public class TelMarketingCenterPerformanceReportService {
    Logger logger = LoggerFactory.getLogger(TelMarketingCenterPerformanceReportService.class);

    private static final String TEL_MARKETING_PERFORMANCE = "telMarketingcenter";
    private static final String UN_TEL_MARKETING_PERFORMANCE = "unTelMarketingcenter";

    @Autowired
    private InsuranceRepository insuranceRepository;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public Map<String,List<PurchaseOrderInfo>> getPerformanceReport(){
        Date startTime = null;
        Date endTime = DateUtils.getDate(DateUtils.getCustomDate(new Date(),-1,23,59,59),DateUtils.DATE_LONGTIME24_PATTERN);
        String previousTimeStr = stringRedisTemplate.opsForValue().get(TaskConstants.PERFORMANCE_ORDER);
        logger.debug("从redis中获取电销/非电销业绩订单定时任务上次执行时间为{}", previousTimeStr);
        if(StringUtils.isEmpty(previousTimeStr))
            startTime = DateUtils.getDate(DateUtils.getCustomDate(new Date(),-1,0,0,0),DateUtils.DATE_LONGTIME24_PATTERN);
        else //如果定时任务第一次执行，redis中没有上次执行时间，默认取昨天零点
            startTime = DateUtils.getDate(previousTimeStr,DateUtils.DATE_LONGTIME24_PATTERN);
        List<OrderStatus> statuses = Arrays.asList(OrderStatus.Enum.PAID_3,OrderStatus.Enum.DELIVERED_4,OrderStatus.Enum.FINISHED_5);
        List<InsuranceCompany> companies = Arrays.asList(ANSWERN_65000);
        //获取商业险
        List<Object[]> insurances = insuranceRepository.findPerformanceOrder(startTime, endTime, statuses,companies);
        logger.debug("从时间点{}开始，商业险的订单数量为：{}",DateUtils.getDateString(startTime, DateUtils.DATE_LONGTIME24_PATTERN), insurances.size());
        if(CollectionUtils.isEmpty(insurances)){
            stringRedisTemplate.opsForValue().set(TaskConstants.PERFORMANCE_ORDER, DateUtils.getDateString(new Date(),DateUtils.DATE_LONGTIME24_START_PATTERN));
            logger.debug("从时间点{}开始，没有符合要求的保单", DateUtils.getDateString(startTime, DateUtils.DATE_LONGTIME24_PATTERN));
            return null;
        }
        Map<String,List<PurchaseOrderInfo>> map = getPerformanceReportExcelSheetInfo(insurances);
        stringRedisTemplate.opsForValue().set(TaskConstants.PERFORMANCE_ORDER, DateUtils.getDateString(new Date(),DateUtils.DATE_LONGTIME24_START_PATTERN));
        return map;
    }

    private Map<String,List<PurchaseOrderInfo>> getPerformanceReportExcelSheetInfo(List<Object[]> insuranceList){
        Map<String,List<PurchaseOrderInfo>> purchaseOrderInfoListMap = new HashMap<>();
        List<PurchaseOrderInfo> telMarketingCenterList = new ArrayList<PurchaseOrderInfo>();
        List<PurchaseOrderInfo> unTelMarketingCenterList = new ArrayList<PurchaseOrderInfo>();
        /**
         * object数据的数据各列依此为：银行、银行卡、持卡人、是否是电销业绩、订单号、车牌号、手机号
         * **/
        for(Object[] objects : insuranceList){
            String orderNo = String.valueOf(objects[4]);
            PurchaseOrderInfo info = new PurchaseOrderInfo();
            info.setBank(defaultNullStr(objects[0]));
            info.setBankNo(defaultNullStr(objects[1]));
            info.setLinkMan(defaultNullStr(objects[2]));
            info.setOrderNo(orderNo);
            info.setLicenseNo(defaultNullStr(objects[5]));
            info.setLinkPhone(defaultNullStr(objects[6]));
            boolean isTelCenter = 1 == Integer.valueOf(String.valueOf(objects[3]));
            if(isTelCenter)
                telMarketingCenterList.add(info);
            else
                unTelMarketingCenterList.add(info);
        }
        purchaseOrderInfoListMap.put(TEL_MARKETING_PERFORMANCE, telMarketingCenterList);
        purchaseOrderInfoListMap.put(UN_TEL_MARKETING_PERFORMANCE, unTelMarketingCenterList);
        logger.debug("最终被发送的邮件中电销业绩订单数为：{}，非电销业绩订单数为：{}", telMarketingCenterList.size(), unTelMarketingCenterList.size());
        return purchaseOrderInfoListMap;
    }

    private String defaultNullStr(Object obj){
        return obj == null?"" : obj.toString();
    }
}
