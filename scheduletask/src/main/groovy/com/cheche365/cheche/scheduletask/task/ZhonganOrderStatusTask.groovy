package com.cheche365.cheche.scheduletask.task

import com.cheche365.cheche.core.model.InsuranceCompany
import com.cheche365.cheche.core.model.InternalUser
import com.cheche365.cheche.core.model.LogType
import com.cheche365.cheche.core.model.MoApplicationLog
import com.cheche365.cheche.core.model.OrderStatus
import com.cheche365.cheche.core.model.OrderSubStatus
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.repository.InsuranceCompanyRepository
import com.cheche365.cheche.core.repository.PurchaseOrderRepository
import com.cheche365.cheche.core.service.DoubleDBService
import com.cheche365.cheche.core.service.IInternalUserService
import com.cheche365.cheche.externalpayment.service.ZaOrderQueryServices
import com.cheche365.cheche.web.service.order.ClientOrderService
import org.apache.commons.lang.time.DateUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import java.text.SimpleDateFormat

/**
 *  众安已支付承保失败订单状态查询
 *  频率：每天
 * Created by zhangtc on 2017/12/26.
 */
@Service
class ZhonganOrderStatusTask extends BaseTask {

    static final SimpleDateFormat sdf = new SimpleDateFormat('yyyy-MM-dd')
    static int[] QueryDay = [-1, -4, -7, -11, -16]
    static int expiredDay = -17

    Logger logger = LoggerFactory.getLogger(WeicheQuoteReportTask.class)

    @Autowired
    ZaOrderQueryServices zaOrderQueryServices
    @Autowired
    PurchaseOrderRepository purchaseOrderRepository
    @Autowired
    InsuranceCompanyRepository insuranceCompanyRepository
    @Autowired
    private ClientOrderService webPurchaseOrderService
    @Autowired
    private DoubleDBService doubleDBService
    @Autowired
    private IInternalUserService internalUserService

    @Override
    protected void doProcess() throws Exception {
        def ic = insuranceCompanyRepository.findByName('保骉车险')
        Date now = new Date()
        for (int day : QueryDay) {
            query(sdf.format(DateUtils.addDays(now, day)), ic)
        }
        cancel(sdf.format(DateUtils.addDays(now, expiredDay)), ic)
    }

    def query(String day, InsuranceCompany ic) {
        List<PurchaseOrder> list = purchaseOrderRepository.findByStatusAndSubStatusAndInsuranceCompany(OrderStatus.Enum.PAID_3, OrderSubStatus.Enum.FAILED_1, ic, day + ' 00:00:00', day + ' 23:59:59')
        logger.info("定时任务-->众安承保失败状态查询->日期：{}->数据量:{}", day, list.size())
        for (PurchaseOrder purchaseOrder : list) {
            zaOrderQueryServices.query(purchaseOrder)
        }
    }

    def cancel(String day, InsuranceCompany ic) {
        List<PurchaseOrder> list = purchaseOrderRepository.findByStatusAndSubStatusAndInsuranceCompany(OrderStatus.Enum.PAID_3, OrderSubStatus.Enum.FAILED_1, ic, day + ' 00:00:00', day + ' 23:59:59')
        for (PurchaseOrder purchaseOrder : list) {
            // 取消订单
            try {
                purchaseOrder = webPurchaseOrderService.cancel(purchaseOrder)
                // 保存工作日志
                saveApplicationLog(purchaseOrder)
            } catch (Exception ignored) {
                logger.error(" ResetExpiredOrderStatusTask error ,orderNo ->{} ", purchaseOrder.getOrderNo())
            }
        }
    }

    /**
     * 保存状态变更工作日志
     * copy from ResetExpiredOrderStatusTask
     * @param purchaseOrder
     */
    private void saveApplicationLog(PurchaseOrder purchaseOrder) {
        MoApplicationLog applicationLog = new MoApplicationLog();
        applicationLog.setLogLevel(2);//日志的级别 1debug  2info 3warn 4error
        applicationLog.setLogMessage("订单过期，状态变更为已取消");//日志信息
        applicationLog.setLogType(LogType.Enum.RESET_EXPIRED_ORDER_STATUS_9);//重设过期订单状态
        applicationLog.setObjId(purchaseOrder.getId() + "");//对象id
        applicationLog.setObjTable("purchase_order");//对象表名
        InternalUser systemInternalUser = internalUserService.getSystemInternalUser();
        if (systemInternalUser != null) {
            applicationLog.setOpeartor(systemInternalUser.getId());//系统操作人
        }
        applicationLog.setCreateTime(Calendar.getInstance().getTime());//创建时间
        doubleDBService.saveApplicationLog(applicationLog);
    }
}
