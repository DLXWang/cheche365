import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.AddressRepository;
import com.cheche365.cheche.core.repository.AutoRepository;
import com.cheche365.cheche.core.repository.PurchaseOrderRepository;
import com.cheche365.cheche.core.repository.UserRepository;
import com.cheche365.cheche.scheduletask.app.config.ScheduleTaskConfig;
import com.cheche365.cheche.scheduletask.task.CompletedOrderOperationReportTask;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by xu.yelong on 2016/1/29.
 * 运营部门成单统计邮件发送测试
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
    classes = {ScheduleTaskConfig.class}
)
@TransactionConfiguration
public class CompletedOrderOperationReportTaskTest {
    @Autowired
    private CompletedOrderOperationReportTask task;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private AutoRepository autoRepository;

    @Before
    public void before(){
        create24HourOrder();
        create40HourOrder();
    }

    /**
     * 此定时任务内有时间判断，24小时成单统计只会在0点发送，所以只能测试40小时订单
     */
    @Test
    public void test(){
        task.process();
    }

    @After
    public void after(){
        reset24HourOrder();
        reset40HourOrder();
    }

    private void create40HourOrder(){
        PurchaseOrder purchaseOrder=createOrder();
        Date currentTime=new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentTime);
        calendar.set(Calendar.HOUR_OF_DAY, 1);
        purchaseOrder.setCreateTime(calendar.getTime());
        purchaseOrder.setOrderNo("T20160201888880");
        purchaseOrderRepository.save(purchaseOrder);
    }

    private void create24HourOrder(){
        PurchaseOrder purchaseOrder=createOrder();
        Date currentTime=new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentTime);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        purchaseOrder.setCreateTime(calendar.getTime());
        purchaseOrder.setOrderNo("T20160201888881");
        purchaseOrderRepository.save(purchaseOrder);
    }


    private PurchaseOrder createOrder(){
        User user=userRepository.findById(new Long(1));
        Address address=addressRepository.findOne(new Long(1));
        Auto auto=autoRepository.findOne(new Long(1));

        PurchaseOrder purchaseOrder=new PurchaseOrder();
        purchaseOrder.setPayableAmount(10000.00);
        purchaseOrder.setPaidAmount(10000.00);
        purchaseOrder.setObjId(new Long(1000));
        purchaseOrder.setApplicant(user);

        purchaseOrder.setSourceChannel(Channel.Enum.ALIPAY_21);
        purchaseOrder.setChannel(PaymentChannel.Enum.ALIPAY_1);
        purchaseOrder.setDeliveryAddress(address);
        purchaseOrder.setStatus(OrderStatus.Enum.FINISHED_5);
        purchaseOrder.setType(OrderType.Enum.INSURANCE);
        purchaseOrder.setAuto(auto);
        purchaseOrder.setArea(Area.Enum.BJ);
        purchaseOrder.setAudit(1);
        return purchaseOrder;
    }

    private void reset24HourOrder(){
        PurchaseOrder purchaseOrder=purchaseOrderRepository.findFirstByOrderNo("T20160201888881");
        purchaseOrderRepository.delete(purchaseOrder);
    }

    private void reset40HourOrder(){
        PurchaseOrder purchaseOrder=purchaseOrderRepository.findFirstByOrderNo("T20160201888880");
        purchaseOrderRepository.delete(purchaseOrder);
    }
}
