package com.cheche365.cheche.ordercenter.service.message;

import com.cheche365.cheche.core.model.InternalUser;
import com.cheche365.cheche.ordercenter.web.model.PurchaseOrderSummary;

import java.util.Map;

/**
 * Created by sunhuazhong on 2015/4/21.
 */
public interface IOrderService {

    /**
     * 获取新订单
     * @param keyword
     * @param pageNum
     * @return
     */
    public Map<String, Object> listNewOrder(String keyword, int pageNum, int pageSize);

    /**
     * 获取新订单基本信息，包括用户和车辆
     * @param id
     * @return
     */
    public PurchaseOrderSummary getNewOrderBasicInfo(Long id);

    /**
     * 获取当前用户
     * @return
     */
    public InternalUser getCurrentInternalUser();
}
