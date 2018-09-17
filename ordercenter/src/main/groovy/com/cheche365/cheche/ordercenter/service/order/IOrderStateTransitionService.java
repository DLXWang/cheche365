package com.cheche365.cheche.ordercenter.service.order;

import com.cheche365.cheche.core.model.OrderOperationInfo;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Date;

/**
 * 该接口用于实现订单状态变更
 * PreAuthorize注解不能与Service注解一起使用
 * Created by sunhuazhong on 2015/5/8.
 */
public interface IOrderStateTransitionService {
    @PreAuthorize("hasAnyRole('PERMISSION_CUSTOMER', 'PERMISSION_ADMIN')")
    public String transitStatusForCustomer(OrderOperationInfo orderOperationInfo, Long currentStatusId, Date date, String reason, String confrimNo);

    @PreAuthorize("hasAnyRole('PERMISSION_INTERNAL', 'PERMISSION_ADMIN')")
    public String transitStatusForInternal(OrderOperationInfo orderOperationInfo, Long currentStatusId, Date date, String reason, String confrimNo);
}
