package com.cheche365.cheche.core.service;

import com.cheche365.cheche.core.model.BusinessActivity;
import com.cheche365.cheche.core.model.OrderSourceType;
import com.cheche365.cheche.core.model.PurchaseOrder;
import com.cheche365.cheche.core.repository.BusinessActivityRepository;
import com.cheche365.cheche.core.repository.PurchaseOrderRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by mahong on 2015/8/18.
 */
@Service
@Transactional
public class CPSChannelService {
    private Logger logger = LoggerFactory.getLogger(CPSChannelService.class);

    @Autowired
    private BusinessActivityRepository activityRepository;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    public BusinessActivity getCpsActivityByOrderNo(String orderNo) {
        if (StringUtils.isBlank(orderNo))
            return null;

        PurchaseOrder order = purchaseOrderRepository.findFirstByOrderNo(orderNo);
        if (null == order)
            return null;

        if (null != order.getOrderSourceType()
            && OrderSourceType.Enum.CPS_CHANNEL_1.getId().equals(order.getOrderSourceType().getId())) {
            return activityRepository.findOne(Long.parseLong(order.getOrderSourceId()));
        }

        return null;
    }
}
