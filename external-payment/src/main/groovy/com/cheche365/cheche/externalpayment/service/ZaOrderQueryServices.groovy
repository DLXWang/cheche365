package com.cheche365.cheche.externalpayment.service

import com.cheche365.cheche.core.model.CompulsoryInsurance
import com.cheche365.cheche.core.model.Insurance
import com.cheche365.cheche.core.model.OrderStatus
import com.cheche365.cheche.core.model.Payment
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.repository.CompulsoryInsuranceRepository
import com.cheche365.cheche.core.repository.InsuranceRepository
import com.cheche365.cheche.core.repository.PaymentRepository
import com.cheche365.cheche.core.repository.PurchaseOrderRepository
import com.cheche365.cheche.core.util.MockUrlUtil
import com.cheche365.cheche.zhongan.service.ZhonganService
import com.google.common.collect.Maps

import static com.cheche365.cheche.externalpayment.constants.ZaCashierConstant.*
import com.cheche365.cheche.zhongan.util.BusinessUtils
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Created by Administrator on 2017/12/19.
 */
@Service
@Slf4j
class ZaOrderQueryServices {
    @Autowired
    CompulsoryInsuranceRepository ciRepo;
    @Autowired
    InsuranceRepository iRepo;
    @Autowired
    PurchaseOrderRepository poRepo
    @Autowired
    PaymentRepository payRepo
    @Autowired
    ZhonganService zhonganService;
    @Autowired
    ZaCallBackService zaCallBackService;


    def query(PurchaseOrder order){

        log.info("定时任务-->调用众安承保服务开始，orderNo:${order.orderNo}")
        Insurance insurance = iRepo.findByQuoteRecordId(order.getObjId());
        CompulsoryInsurance compulsoryInsurance = ciRepo.findByQuoteRecordId(order.getObjId());
        Payment payment=payRepo.findFirstByPurchaseOrder(order)
        zaCallBackService.order(order, insurance,compulsoryInsurance, payment,null);
        log.info("定时任务-->调用众安承保服务完毕")
    }


}
