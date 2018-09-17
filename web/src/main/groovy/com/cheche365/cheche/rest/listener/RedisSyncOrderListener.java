package com.cheche365.cheche.rest.listener;

import com.cheche365.cheche.core.message.PartnerOrderMessage;
import com.cheche365.cheche.core.model.PurchaseOrder;
import com.cheche365.cheche.core.util.CacheUtil;
import com.cheche365.cheche.partner.service.order.PartnerOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mahong on 2016/2/22.
 * 监听需要同步的订单
 */
@Component
public class RedisSyncOrderListener implements MessageListener {

    private Logger logger = LoggerFactory.getLogger(RedisSyncOrderListener.class);

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private PartnerOrderService partnerOrderService;

    @Override
    public void onMessage(Message message, byte[] pattern) {

        String purchaseOrderJson = String.valueOf(message.toString());
        PurchaseOrder purchaseOrder;
        Map orderPaymentMap=new HashMap();

        logger.debug("purchaseOrderJson" + purchaseOrderJson);
        purchaseOrder = CacheUtil.doJacksonDeserialize(purchaseOrderJson, PurchaseOrder.class);
        if (purchaseOrder.getId()!= null) {
            orderPaymentMap.put("order", purchaseOrder);
        } else {
            orderPaymentMap = CacheUtil.doJacksonDeserialize(purchaseOrderJson, Map.class);
            purchaseOrder = CacheUtil.doJacksonDeserialize(CacheUtil.doJacksonSerialize(orderPaymentMap.get("order")), PurchaseOrder.class);
        }

        if (null == purchaseOrder) {
            logger.error("synchronize order error, can not deserialize purchaseOrderJson ,purchaseOrderJson is {}", purchaseOrderJson);
            return;
        }
        logger.debug("over  order id "+purchaseOrder.getId());
        try {
            if (redisTemplate.opsForSet().remove(PartnerOrderMessage.QUEUE_SET, purchaseOrder.getOrderNo()) > 0) {
                logger.debug("synchronize order which publish in redis,purchaseOrderJson is {}", purchaseOrderJson);
                logger.debug("begin sync order, remove order from redis,order no is {}", purchaseOrder.getOrderNo());
                partnerOrderService.syncPurchaseOrder(orderPaymentMap);
            }
        } catch (Exception e) {
            logger.debug("处理监听到的待同步订单过程中出现异常"+e.getMessage());
            logger.error("处理监听到的待同步订单过程中出现异常", e);
        }
    }
}
