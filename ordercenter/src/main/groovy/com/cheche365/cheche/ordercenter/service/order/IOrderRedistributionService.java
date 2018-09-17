package com.cheche365.cheche.ordercenter.service.order;

import com.cheche365.cheche.core.model.PurchaseOrder;

import java.util.Map;

/**
 * Created by wangfei on 2015/6/15.
 */
public interface IOrderRedistributionService {

    PurchaseOrder findByOrderNo(String orderNo);

    Map<String, Object> getAssignersByOrder(PurchaseOrder purchaseOrder);

    Map<String, Object> getAssignersByOperatorId(Long operatorId);

    Map<String, Object> redistributeByOrder(String orderNo, Long newOperatorId);

    Map<String, Object> redistributeByOperator(Long oldOperatorId, Long newOperatorId, String distributionMethod);

    Integer getCountByOperator(Long operatorId);
}
