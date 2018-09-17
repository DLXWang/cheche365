package com.cheche365.cheche.ordercenter.service.order;

import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.InternalUserRepository;
import com.cheche365.cheche.core.repository.OrderOperationInfoRepository;
import com.cheche365.cheche.core.repository.PurchaseOrderRepository;
import com.cheche365.cheche.core.util.CacheUtil;
import com.cheche365.cheche.ordercenter.service.resource.InternalUserResource;
import com.cheche365.cheche.ordercenter.service.user.OrderCenterInternalUserManageService;
import com.cheche365.cheche.manage.common.web.model.DataTablePageViewModel;
import com.cheche365.cheche.manage.common.web.model.ResultModel;
import com.cheche365.cheche.ordercenter.web.model.order.OrderOperationInfoViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by wangfei on 2015/6/15.
 */
@Service
public class OrderRedistributionService {

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private InternalUserResource internalUserResource;

    @Autowired
    private InternalUserRepository internalUserRepository;

    @Autowired
    private OrderOperationInfoRepository orderOperationInfoRepository;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private OrderCenterInternalUserManageService orderCenterInternalUserManageService;

    private static final String CACHE_KEY = "schedules.task.ordercenter.redistribution";

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public PurchaseOrder findByOrderNo(String orderNo) {
        return purchaseOrderRepository.findFirstByOrderNo(orderNo);
    }

    public Long getOrderCountByAssigner(Long operatorId) {
        InternalUser internalUser = internalUserRepository.findOne(operatorId);
        if (internalUser == null)
            throw new RuntimeException("redistribute order by operator, internalUser do not exists");
        Long num = orderOperationInfoRepository.countByAssigner(internalUser);
        return num;
      //  List<OrderOperationInfo> assignerList = orderOperationInfoRepository.findByAssigner(internalUser);
//        if (assignerList != null && assignerList.size() > 0) {
//            return assignerList.size();
//        } else {
//            List<PurchaseOrder> orderList = purchaseOrderRepository.findByOperatorAndType(internalUser, OrderType.Enum.INSURANCE);
//            if (orderList != null && orderList.size() > 0) {
//                return orderList.size();
//            }
//        }
    }

    public List<InternalUser> listAllEnableCustomerExceptOne(InternalUser internalUser) {
        return internalUserResource.listAllEnableCustomerExceptOne(internalUser);
    }

    public List<InternalUser> listAllEnableCustomer() {
        return internalUserResource.listAllEnableCustomer();
    }

    public List<InternalUser> getAssignersByOperatorId(Long operatorId) {
        InternalUser internalUser = internalUserRepository.findOne(operatorId);
        List<InternalUser> internalUserList = internalUserResource.listAllEnableCustomerExceptOne(internalUser);
        return internalUserList;
    }

    @Transactional(rollbackFor = Exception.class)
    public PurchaseOrder redistributeByOrder(String orderNo, Long newOperatorId) {
        PurchaseOrder purchaseOrder = this.findByOrderNo(orderNo);
        if (purchaseOrder == null)
            throw new RuntimeException("redistribute purchaseOrder by orderNo, purchaseOrder do not exists");

        InternalUser internalUser = internalUserRepository.findOne(newOperatorId);
        if (internalUser == null)
            throw new RuntimeException("redistribute purchaseOrder by orderNo, internalUser do not exists");

        OrderOperationInfo orderOperationInfo = orderOperationInfoRepository.findFirstByPurchaseOrder(purchaseOrder);

        purchaseOrder.setOperator(internalUser);
        purchaseOrder.setUpdateTime(new Date());
        purchaseOrderRepository.save(purchaseOrder);

        if (orderOperationInfo != null) {
            if(orderOperationInfo.getAssigner().getId().equals(orderOperationInfo.getOwner().getId())) {
                orderOperationInfo.setOwner(internalUser);
            }
            orderOperationInfo.setAssigner(internalUser);
            orderOperationInfo.setUpdateTime(new Date());
            orderOperationInfoRepository.save(orderOperationInfo);
        }
        return purchaseOrder;
    }

    @Transactional(rollbackFor = Exception.class)
    public ResultModel redistributeByOperator(Long oldOperatorId, Long newOperatorId, String distributionType, String[] checkedIds) {
        InternalUser oldInternalUser = internalUserRepository.findOne(oldOperatorId);
        InternalUser currentUser = orderCenterInternalUserManageService.getCurrentInternalUser();
        if (oldInternalUser == null )
            throw new RuntimeException("redistribute purchaseOrder by operator, old internalUser do not exists");
        if ("0".equals(distributionType)) {
            InternalUser newOperator = internalUserRepository.findOne(newOperatorId);
            //  String[] checkedIdArr = checkedIds.split(",");
            List<OrderOperationInfo> orderOperationInfoList = orderOperationInfoRepository.findByIds(Arrays.asList(checkedIds));
            for (OrderOperationInfo orderOperationInfo : orderOperationInfoList) {
                orderOperationInfo.setAssigner(newOperator);
                orderOperationInfo.setOperator(currentUser);
            }
            orderOperationInfoRepository.save(orderOperationInfoList);
            logger.debug("save customer redistribution successful ,oldOperatorId:{} , newOperatorId:{} ,distributionType:{} , checkedIds:{}", oldOperatorId, newOperatorId, distributionType, checkedIds);
            return new ResultModel(true, "指定分配人成功！已生效！");
        }else{
            logger.debug("save customer redistribution operate to redis ,oldOperatorId:{} ,newOperatorId:{} ,distributionType:{}", oldOperatorId, newOperatorId, distributionType);
            Map map = new HashMap();
            map.put("oldOperatorId", oldOperatorId);
            map.put("newOperatorId", newOperatorId);
            map.put("distributionType", distributionType);
            map.put("operator", currentUser.getId());
            stringRedisTemplate.opsForList().leftPush(CACHE_KEY, CacheUtil.doJacksonSerialize(map));
            return new ResultModel(true, "指定分配人成功！30分钟内指派生效，请勿重复指派！");
        }
    }

    public DataTablePageViewModel<OrderOperationInfoViewModel> findOperationInfoByOperator(Long operatorId, int pageNo, int pageSize, Integer draw) {
        int startIndex = (pageNo - 1) * pageSize;
        List<OrderOperationInfo> orderOperationInfoList = orderOperationInfoRepository.findByAssigner(operatorId, startIndex, pageSize);
        List<OrderOperationInfoViewModel> orderOperationInfoViewModelList = new ArrayList<>();
        orderOperationInfoList.forEach(opi -> orderOperationInfoViewModelList.add(OrderOperationInfoViewModel.createViewModel(opi)));
        InternalUser currentAssigner = internalUserRepository.findOne(operatorId);
        long totalElements = orderOperationInfoRepository.countByAssigner(currentAssigner);
        return new DataTablePageViewModel(totalElements, totalElements, draw, orderOperationInfoViewModelList);
    }
}
