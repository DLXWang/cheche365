package com.cheche365.cheche.unionpay.payment;

import com.cheche365.cheche.core.model.MoApplicationLog;
import com.cheche365.cheche.core.model.PurchaseOrder;
import com.cheche365.cheche.core.repository.PurchaseOrderRepository;
import com.cheche365.cheche.core.service.DoubleDBService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.cheche365.cheche.core.service.PaymentSerialNumberGenerator.getPurchaseNo;

/**
 * Created by wangfei on 2015/7/10.
 */
@Component
public class UnionPayProcessor {

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private DoubleDBService doubleDBService;

    @Transactional
    public void saveUnionPayLog(String orderId, String msg) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findFirstByOrderNo(getPurchaseNo(orderId));
        MoApplicationLog log = MoApplicationLog.applicationLogByPurchaseOrder(purchaseOrder);
        log.setLogMessage(msg);
        doubleDBService.saveApplicationLog(log);
    }

    public String getUnionPayMerId() {
        return IUnionPayHandler.UNION_PAY_MERCHANT_ID_MOBILE;
    }

    public static boolean isUnionPayRefundTrade(String txnType) {
        return StringUtils.isNotBlank(txnType)
            && IUnionPayHandler.UNION_PAY_TXN_TYPE_04.equals(txnType);
    }

    public static boolean isUnionPayRevokeTrade(String txnType) {
        return StringUtils.isNotBlank(txnType)
            && IUnionPayHandler.UNION_PAY_TXN_TYPE_31.equals(txnType);
    }
}
