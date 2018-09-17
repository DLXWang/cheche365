package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.core.model.InternalUser;
import com.cheche365.cheche.core.model.LogType;
import com.cheche365.cheche.core.model.MoApplicationLog;
import com.cheche365.cheche.core.model.PurchaseOrder;
import com.cheche365.cheche.core.service.DoubleDBService;
import com.cheche365.cheche.core.service.IInternalUserService;
import com.cheche365.cheche.core.service.PurchaseOrderService;
import com.cheche365.cheche.web.service.order.ClientOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 重设过期订单状态任务
 * 超过8小时订单过期
 * 定时时间：每天凌晨0点
 * Created by liufei on 2016/1/14.
 */
@Service
public class ResetExpiredOrderStatusTask extends BaseTask{
    private Logger logger = LoggerFactory.getLogger(ResetExpiredOrderStatusTask.class);

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Autowired
    private ClientOrderService webPurchaseOrderService;

    @Autowired
    private DoubleDBService doubleDBService;

    @Autowired
    private IInternalUserService internalUserService;

    @Override
    protected void doProcess() throws Exception {
        // 查询过期订单
        Date expiredDate = getExpiredDate();

        // 处理所有过期订单
        List<PurchaseOrder> purchaseOrderList = purchaseOrderService.listExpiredOrder(new Date());

        if (purchaseOrderList != null && !purchaseOrderList.isEmpty()) {
            for(PurchaseOrder purchaseOrder:purchaseOrderList){
                // 取消订单
               try{
                    purchaseOrder = webPurchaseOrderService.cancel(purchaseOrder);
                    // 保存工作日志
                    saveApplicationLog(purchaseOrder);
                }catch(Exception e){
                    logger.error(" ResetExpiredOrderStatusTask error ,orderNo ->{} ", purchaseOrder.getOrderNo());
                    continue;
                }
            }
        }
    }

    public static Date getExpiredDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR_OF_DAY, -24);
        return calendar.getTime();
    }

    /**
     * 保存状态变更工作日志
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
        if(systemInternalUser != null) {
            applicationLog.setOpeartor(systemInternalUser.getId());//系统操作人
        }
        applicationLog.setCreateTime(Calendar.getInstance().getTime());//创建时间
        doubleDBService.saveApplicationLog(applicationLog);
    }

}
