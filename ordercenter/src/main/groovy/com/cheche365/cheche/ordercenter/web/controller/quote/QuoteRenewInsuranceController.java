package com.cheche365.cheche.ordercenter.web.controller.quote;

import com.cheche365.cheche.core.model.PurchaseOrder;
import com.cheche365.cheche.core.model.QuoteRecord;
import com.cheche365.cheche.core.model.SupplementInfo;
import com.cheche365.cheche.core.repository.PurchaseOrderRepository;
import com.cheche365.cheche.core.repository.QuoteRecordRepository;
import com.cheche365.cheche.core.util.CacheUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by wangshaobin on 2017/2/10.
 */
@RestController
@RequestMapping("/orderCenter/quote/renewInsurance")
public class QuoteRenewInsuranceController {

    @Autowired
    private QuoteRecordRepository quoteRecordRepository;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;


    @RequestMapping(value = "/getQuoteRecordByOrderId",method = RequestMethod.POST)
    public QuoteRecord getQuoteRecordByOrderId(@RequestParam(value = "orderId", required = true) Long orderId){
        return quoteRecordRepository.findByOrderId(orderId);
    }
    @RequestMapping(value = "/getPurchaseOrderById",method = RequestMethod.GET)
    public PurchaseOrder getPurchaseOrderById(@RequestParam(value = "orderId", required = true) Long orderId){
        PurchaseOrder order = purchaseOrderRepository.findOne(orderId);
        if(order.getAuto().getAutoType()!=null){
            SupplementInfo info = new SupplementInfo();
            info.setCode(order.getAuto().getAutoType().getCode());
            order.getAuto().getAutoType().setSupplementInfo(info);
        }
        return order;
    }
}
