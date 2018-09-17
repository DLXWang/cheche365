package com.cheche365.cheche.ordercenter.service.newyearpack;

import com.cheche365.cheche.core.constants.WebConstants;
import com.cheche365.cheche.core.model.OrderProcessHistory;
import com.cheche365.cheche.core.model.OrderProcessType;
import com.cheche365.cheche.core.model.OrderStatus;
import com.cheche365.cheche.core.model.PurchaseOrder;
import com.cheche365.cheche.core.repository.OrderProcessHistoryRepository;
import com.cheche365.cheche.core.repository.PurchaseOrderRepository;
import com.cheche365.cheche.ordercenter.constants.NewYearPackStatusEum;
import com.cheche365.cheche.ordercenter.service.user.OrderCenterInternalUserManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;

/**
 * Created by yang on 2015/12/31.
 */
@Service
public class NewYearPackManagerService {


    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;


    @Autowired
    private OrderProcessHistoryRepository orderProcessHistoryRepository;

    @Autowired
    private OrderCenterInternalUserManageService orderCenterInternalUserManageService;

    public String getNewYearRulePath() {
        return WebConstants.getDomainURL() + "/marketing/m/201601001/index.html?r=sh";
    }

    public String getCodeRemainQuantity() {

        return new StringBuffer().append("0").append(",").append("0").append(";")
            .append("0").append(";").append("0").toString();
    }

    @Transactional
    public void cancel(Long purchaseOrderId) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findOne(purchaseOrderId);
        purchaseOrder.setStatus(OrderStatus.Enum.CANCELED_6);
        purchaseOrder.setUpdateTime(Calendar.getInstance().getTime());
        purchaseOrder.setOperator(orderCenterInternalUserManageService.getCurrentInternalUser());
        purchaseOrderRepository.save(purchaseOrder);

        // 保存取消订单日志
        OrderProcessHistory history = new OrderProcessHistory();
        history.setPurchaseOrder(purchaseOrder);
        history.setComment(NewYearPackStatusEum.CANCEL_STATUS.getContent());
        history.setCreateTime(Calendar.getInstance().getTime());
        history.setOperator(orderCenterInternalUserManageService.getCurrentInternalUser());
        history.setOrderProcessType(OrderProcessType.Enum.INDEPENDENCE);
        orderProcessHistoryRepository.save(history);
    }

}
