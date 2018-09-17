package com.cheche365.cheche.core.service;

import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.InternalUserRepository;
import com.cheche365.cheche.core.repository.OrderCooperationInfoRepository;
import com.cheche365.cheche.core.repository.OrderCooperationStatusRepository;
import com.cheche365.cheche.core.repository.QuoteRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by wangfei on 2015/11/13.
 */
@Service
@Transactional
public class OrderCooperationInfoService {
    private Logger logger = LoggerFactory.getLogger(OrderCooperationInfoService.class);

    @Autowired
    private OrderCooperationStatusRepository orderCooperationStatusRepository;

    @Autowired
    private OrderCooperationInfoRepository orderCooperationInfoRepository;

    @Autowired
    private QuoteRecordRepository quoteRecordRepository;

    @Autowired
    private AreaContactInfoService areaContactInfoService;

    @Autowired
    private InstitutionService institutionService;

    @Autowired
    private IInternalUserService internalUserService;

    @Autowired
    private OrderProcessHistoryService orderProcessHistoryService;

    @Autowired
    private InternalUserRepository internalUserRepository;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public List<OrderCooperationStatus> getAllStatus() {
        List<OrderCooperationStatus> statusList = new ArrayList<>();
        Iterable<OrderCooperationStatus> statusIterable = orderCooperationStatusRepository.findAll(new Sort(Sort.Direction.ASC, "id"));
        Iterator<OrderCooperationStatus> statusIterator = statusIterable.iterator();
        while (statusIterator.hasNext()) {
            statusList.add(statusIterator.next());
        }
        return statusList;
    }

    public OrderCooperationInfo updateOrderCooperationStatus(OrderCooperationInfo orderCooperationInfo, OrderCooperationStatus newStatus) {
        orderCooperationInfo.setStatus(newStatus);
        orderCooperationInfo.setUpdateTime(new Date());
        return orderCooperationInfoRepository.save(orderCooperationInfo);
    }

    public OrderCooperationInfo getById(Long id) {
        return orderCooperationInfoRepository.findOne(id);
    }

    public OrderCooperationInfo getByPurchaseOrder(PurchaseOrder purchaseOrder) {
        return orderCooperationInfoRepository.findFirstByPurchaseOrder(purchaseOrder);
    }

    public OrderCooperationInfo saveOrderCooperationInfo(PurchaseOrder purchaseOrder) {
        if(logger.isDebugEnabled()) {
            logger.debug("generate order cooperation info by purchase order, orderNo:{}", purchaseOrder.getOrderNo());
        }
        if (purchaseOrder == null) {
            throw new RuntimeException("save order cooperation info, purchase order can not be null");
        }
        OrderCooperationInfo orderCooperationInfo = orderCooperationInfoRepository.findFirstByPurchaseOrder(purchaseOrder);
        if (orderCooperationInfo == null) {
            return orderCooperationInfoRepository.save(this.createOrderCooperationInfo(purchaseOrder));
        }
        return orderCooperationInfo;
    }

    /**
     * 验证该订单输入自助下单还是合作下单
     * true：合作下单，false：自助下单
     * @param purchaseOrder
     * @return
     */
    public boolean checkOrderMode(PurchaseOrder purchaseOrder) {
        if(logger.isDebugEnabled()) {
            logger.debug("check order center mode, orderNo:{}", purchaseOrder.getOrderNo());
        }
        QuoteRecord quoteRecord = quoteRecordRepository.findOne(purchaseOrder.getObjId());
        return "true".equals(getRedisCooperationFlag()) && institutionService.checkEnableByAreaAndInsuranceCompany(
            purchaseOrder.getArea(), quoteRecord.getInsuranceCompany());
    }

    public String getRedisCooperationFlag() {
        String value = "false";
        boolean notExist = stringRedisTemplate.opsForValue().setIfAbsent("support.order.cooperation.info.flag", value);
        if(!notExist) {
            value = stringRedisTemplate.opsForValue().get("support.order.cooperation.info.flag");
        }
        return value;
    }

    public OrderCooperationInfo createOrderCooperationInfo(PurchaseOrder purchaseOrder) {
        QuoteRecord quoteRecord = quoteRecordRepository.findOne(purchaseOrder.getObjId());
        OrderCooperationInfo orderCooperationInfo = new OrderCooperationInfo();
        orderCooperationInfo.setPurchaseOrder(purchaseOrder);
        orderCooperationInfo.setArea(purchaseOrder.getArea());//城市
        orderCooperationInfo.setInsuranceCompany(quoteRecord.getInsuranceCompany());//保险公司
        orderCooperationInfo.setAreaContactInfo(areaContactInfoService.findByArea(purchaseOrder.getArea()));//车车分站
        orderCooperationInfo.setAssigner(internalUserService.getRandomCustomer());//指定操作人
        orderCooperationInfo.setCreateTime(Calendar.getInstance().getTime());
        orderCooperationInfo.setUpdateTime(Calendar.getInstance().getTime());
        return orderCooperationInfo;
    }

    public OrderCooperationInfo updatePaySuccessOrderStatus(PurchaseOrder purchaseOrder) {
        if (null == purchaseOrder) return null;
        OrderCooperationInfo orderCooperationInfo = orderCooperationInfoRepository.findFirstByPurchaseOrder(purchaseOrder);
        if (null != orderCooperationInfo) {
            logger.info("need update order_cooperation_info status to {} when pay success!", OrderCooperationStatus.Enum.CREATED.getStatus());
            InternalUser operator = internalUserRepository.findFirstByName("system");
            orderProcessHistoryService.saveChangeStatusHistory(operator, purchaseOrder, null, OrderCooperationStatus.Enum.CREATED);
            return updateOrderCooperationStatus(orderCooperationInfo, OrderCooperationStatus.Enum.CREATED);
        }
        return null;
    }

    /**
     * 第三方合作未完全处理订单
     * @return
     */
    public List<OrderCooperationInfo> findCooperationOrderByOperateStatus(List statusList,List sourceList){
        return orderCooperationInfoRepository.findCooperationOrderByOperateStatus(statusList,sourceList);
    }
}
