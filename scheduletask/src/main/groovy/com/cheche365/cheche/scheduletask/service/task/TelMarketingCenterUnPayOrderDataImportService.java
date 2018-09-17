package com.cheche365.cheche.scheduletask.service.task;

import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.PurchaseOrderRepository;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by wangshaobin on 2016/12/21.
 */
@Service
public class TelMarketingCenterUnPayOrderDataImportService {
    Logger logger = LoggerFactory.getLogger(TelMarketingCenterUnPayOrderDataImportService.class);
    @Autowired
    private TelMarketingCenterService telMarketingCenterService;
    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private TelMarketingCenterChannelFilterService taskExcludeChannelSettingService;

    /**
     * 导入未支付订单数据.
     */
    public void importUnPayOrderData() {
        logger.debug("import unpay purchase order data");
        String previousOrderIdStr = getPreviousOrderIdStr();
        Date maxCreateTime = getDate();
        Pageable pageable = new PageRequest(TaskConstants.PAGE_NUMBER, TaskConstants.PAGE_SIZE);
        List<Channel> excludeChannelList=taskExcludeChannelSettingService.findExcludeChannelsByTaskType(TelMarketingCenterTaskType.Enum.UNPAY_ORDER);
        Page<PurchaseOrder> purchaseOrderPage = purchaseOrderRepository.findUnPayOrderList(
            Long.valueOf(previousOrderIdStr), Arrays.asList(OrderStatus.Enum.PENDING_PAYMENT_1), OrderType.Enum.INSURANCE,
            maxCreateTime, excludeChannelList, pageable);
        List<PurchaseOrder> purchaseOrderList = purchaseOrderPage.getContent();
        logger.debug("订单id大于{}范围内的未支付订单的数量为{}", previousOrderIdStr, purchaseOrderList.size());
        if (CollectionUtils.isEmpty(purchaseOrderList)) {
            return;
        }
        while(purchaseOrderList.size() > 0) {
            saveTelMarketingCenterForPurchaseOrder(purchaseOrderList);
            previousOrderIdStr = purchaseOrderList.get(purchaseOrderList.size() - 1).getId().toString();
            stringRedisTemplate.opsForValue().set(TaskConstants.QUOTE_UNPAY_ORDER_ID_CACHE, previousOrderIdStr);
            if(purchaseOrderList.size() < TaskConstants.PAGE_SIZE) {
                break;
            }
            purchaseOrderPage = purchaseOrderRepository.findUnPayOrderList(
                Long.getLong(previousOrderIdStr), Arrays.asList(OrderStatus.Enum.PENDING_PAYMENT_1), OrderType.Enum.INSURANCE,
                maxCreateTime, excludeChannelList, pageable);
            purchaseOrderList = purchaseOrderPage.getContent();
            logger.debug("订单id大于{}范围内的未支付订单的数量为{}", previousOrderIdStr, purchaseOrderList.size());
        }
    }

    private String getPreviousOrderIdStr() {
        String previousOrderIdStr = stringRedisTemplate.opsForValue().get(TaskConstants.QUOTE_UNPAY_ORDER_ID_CACHE);
        if(StringUtils.isEmpty(previousOrderIdStr)) {
            previousOrderIdStr = "1";
        }
        return previousOrderIdStr;
    }

    private Date getDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -30);
        return calendar.getTime();
    }

    private void saveTelMarketingCenterForPurchaseOrder(List<PurchaseOrder> purchaseOrderList) {
        for (PurchaseOrder purchaseOrder : purchaseOrderList) {
            try {
                User user = purchaseOrder.getApplicant();
                TelMarketingCenterSource source = TelMarketingCenterSource.Enum.ORDERS_UNPAY;
                Channel sourceChannel = purchaseOrder.getSourceChannel() == null? Channel.Enum.WAP_8 : purchaseOrder.getSourceChannel();
                telMarketingCenterService.save(user, user.getMobile(),
                    source, null, purchaseOrder.getCreateTime(), purchaseOrder.getId(),
                    TelMarketingCenterRepeat.Enum.PURCHASE_ORDER, sourceChannel);
            } catch (Exception ex) {
                logger.error("import unpay purchase order data has an error, id:{}", purchaseOrder.getId(), ex);
            }
        }
    }
}
