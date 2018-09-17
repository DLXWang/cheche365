package com.cheche365.cheche.scheduletask.service.task;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.*;
import com.cheche365.cheche.manage.common.service.TelMarketingCenterChannelFilterService;
import com.cheche365.cheche.manage.common.service.TelMarketingCenterService;
import com.cheche365.cheche.core.model.TelMarketingCenterRepeat;
import com.cheche365.cheche.core.model.TelMarketingCenterSource;
import com.cheche365.cheche.manage.common.model.TelMarketingCenterTaskType;
import com.cheche365.cheche.scheduletask.constants.TaskConstants;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by wangshaobin on 2016/12/21.
 */
@Service
public class TelMarketingCenterLastYearUnpayOrderDataImportService {
    Logger logger = LoggerFactory.getLogger(TelMarketingCenterLastYearUnpayOrderDataImportService.class);

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;
    @Autowired
    private TelMarketingCenterService telMarketingCenterService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private TelMarketingCenterChannelFilterService taskExcludeChannelSettingService;
    /**
     * 上年未成单订单数据
     * **/
    @Transactional
    public void importLastYearUnpayOrderData(){
        Date currentTime = DateUtils.getCurrentDate(DateUtils.DATE_LONGTIME24_PATTERN);
        Date startTime = null;
        Date endTime = DateUtils.getAroundYearsDay(currentTime, -1);
        String previousTimeStr = stringRedisTemplate.opsForValue().get(TaskConstants.LASY_YEAR_UNPAY_ORDER_CACHE);
        logger.debug("从redis中获取的上次上年未成单订单定时任务执行时间为{}", previousTimeStr);
        if(!StringUtils.isEmpty(previousTimeStr)) {
            Date previousTime = DateUtils.getDate(previousTimeStr,DateUtils.DATE_LONGTIME24_PATTERN);
            startTime = DateUtils.getAroundYearsDay(previousTime, -1);
        }
        int startIndex = 0;int pageSize = TaskConstants.PAGE_SIZE;
        List<Channel> excludeChannels= taskExcludeChannelSettingService.findExcludeChannelsByTaskType(TelMarketingCenterTaskType.Enum.LAST_YEAR_UNPAY_ORDER);
        List<OrderStatus> includeStatus = Arrays.asList(OrderStatus.Enum.CANCELED_6,OrderStatus.Enum.INSURE_FAILURE_7,
            OrderStatus.Enum.REFUNDED_9);
        List<PurchaseOrder> orders = purchaseOrderRepository.findLastYearUnOrderOrder(startTime, endTime, startIndex, pageSize, excludeChannels, includeStatus);
        logger.debug("上年未成单订单，订单id大于{}范围内的未成单订单的数量为{}", startIndex, getListSize(orders));
        while(!CollectionUtils.isEmpty(orders)) {
            saveTelMarketingCenterForPurchaseOrder(orders);
            if(orders.size() < TaskConstants.PAGE_SIZE) {
                break;
            }
            startIndex += orders.size();
            orders = purchaseOrderRepository.findLastYearUnOrderOrder(startTime, endTime, startIndex, pageSize, excludeChannels, includeStatus);
            logger.debug("上年未成单订单，订单id大于{}范围内的未成单订单的数量为{}", startIndex, getListSize(orders));
        }
        setRedisPreviousTime(currentTime);
    }


    private void saveTelMarketingCenterForPurchaseOrder(List<PurchaseOrder> purchaseOrderList) {
        for (PurchaseOrder purchaseOrder : purchaseOrderList) {
            User user = purchaseOrder.getApplicant();
            TelMarketingCenterSource source = TelMarketingCenterSource.Enum.ORDERS_LAST_YEAR_UNPAY;
            Channel sourceChannel = purchaseOrder.getSourceChannel() == null? Channel.Enum.WAP_8 : purchaseOrder.getSourceChannel();
            /**新需求，上年未成单订单中，sourceCreateTime设置为定时任务执行时间，定时任务的执行时间为每天凌晨三点\
             * 1）需求又变了，来源创建时间改成订单的创建时间 20150815
             *      由于查询时，只是针对子表中的source_create_time进行比较的，因此，暂时通过脚本修改子表的source_create_time字段，主表的暂时不动；
             *      不过程序现均修改为订单的创建时间
             * **/
            telMarketingCenterService.save(user, user.getMobile(),
                source, null, purchaseOrder.getCreateTime(), purchaseOrder.getId(),
                TelMarketingCenterRepeat.Enum.PURCHASE_ORDER, sourceChannel);
        }
        logger.debug("将订单id大于{}的{}条上年未成单订单成功保存至电销表", purchaseOrderList.get(0).getId(), getListSize(purchaseOrderList));
    }

    private int getListSize(List list){
        return CollectionUtils.isEmpty(list) ? 0 : list.size();
    }

    private void setRedisPreviousTime(Date executeTime){
        stringRedisTemplate.opsForValue().set(TaskConstants.LASY_YEAR_UNPAY_ORDER_CACHE,DateUtils.getDateString(executeTime, DateUtils.DATE_LONGTIME24_PATTERN));
    }
}
