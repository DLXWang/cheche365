package com.cheche365.cheche.scheduletask.service.task;

import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.PurchaseOrderAmendRepository;
import com.cheche365.cheche.manage.common.service.TelMarketingCenterChannelFilterService;
import com.cheche365.cheche.manage.common.service.TelMarketingCenterService;
import com.cheche365.cheche.core.model.TelMarketingCenterRepeat;
import com.cheche365.cheche.core.model.TelMarketingCenterSource;
import com.cheche365.cheche.manage.common.model.TelMarketingCenterTaskType;
import com.cheche365.cheche.scheduletask.constants.TaskConstants;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by yinjianbin on 2016/9/15.
 */
@Service
public class TelMarketingCenterRefundDataImportService {

    Logger logger = LoggerFactory.getLogger(TelMarketingCenterRefundDataImportService.class);
    @Autowired
    private TelMarketingCenterService telMarketingCenterService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private PurchaseOrderAmendRepository purchaseOrderAmendRepository;
    @Autowired
    private TelMarketingCenterChannelFilterService telMarketingCenterChannelFilterService;


    /**
     * 导入申请退款数据
     */
    public void importRefundOrderData() {
        String preOrderIdStr = stringRedisTemplate.opsForValue().get(TaskConstants.REFUND_ORDER_ID_CACHE);
        if (StringUtils.isEmpty(preOrderIdStr)) {
            preOrderIdStr = "0";
        }
        logger.debug("schedule task starting--> import refund purchase order data,start from id --> [{}]", preOrderIdStr);
        List<Channel> excludeChannelList = telMarketingCenterChannelFilterService.findExcludeChannelsByTaskType(TelMarketingCenterTaskType.Enum.REFUND);
        List<PurchaseOrderAmend> purchaseOrderAmendList = purchaseOrderAmendRepository.findRefundOrderList(NumberUtils.toLong(preOrderIdStr), OrderTransmissionStatus.Enum.APPLY_FOR_REFUND, PaymentType.Enum.FULLREFUND_4, OrderStatus.Enum.REFUNDING_10, PurchaseOrderAmendStatus.Enum.CREATE,excludeChannelList);
        if (CollectionUtils.isEmpty(purchaseOrderAmendList)) {
            return;
        }
        logger.debug("订单id大于{}范围内的申请退款订单的数量为{}", preOrderIdStr, purchaseOrderAmendList.size());
        saveToTelMarketingCenter(purchaseOrderAmendList);
        preOrderIdStr = purchaseOrderAmendList.get(purchaseOrderAmendList.size() - 1).getId().toString();
        stringRedisTemplate.opsForValue().set(TaskConstants.REFUND_ORDER_ID_CACHE, preOrderIdStr);
    }


    public void saveToTelMarketingCenter(List<PurchaseOrderAmend> purchaseOrderAmendList) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + 15);
        Date triggerTime = calendar.getTime();

        for (PurchaseOrderAmend purchaseOrderAmend : purchaseOrderAmendList) {
            OrderOperationInfo orderOperationInfo = purchaseOrderAmend.getOrderOperationInfo();
            PurchaseOrder purchaseOrder = orderOperationInfo.getPurchaseOrder();
            User user = purchaseOrder.getApplicant();
            TelMarketingCenterSource source = TelMarketingCenterSource.Enum.ORDERS_REFUND;
            Channel sourceChannel = purchaseOrder.getSourceChannel() == null ? Channel.Enum.WAP_8 : purchaseOrder.getSourceChannel();
            try {
                telMarketingCenterService.save(null,user, user.getMobile(),
                        source, null, purchaseOrderAmend.getCreateTime(), purchaseOrderAmend.getId(),
                        TelMarketingCenterRepeat.Enum.PURCHASE_ORDER_AMEND, sourceChannel, triggerTime, null,null);
            } catch (Exception e) {
                logger.error("导入电销时保存出错,purchaseOrderNo-->[{}]", purchaseOrder.getOrderNo(), e);
            }
        }
    }


}
