import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.*;
import com.cheche365.cheche.scheduletask.app.config.ScheduleTaskConfig;
import com.cheche365.cheche.scheduletask.task.CompletedOrderFinanceReportTask;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by xu.yelong on 2016/2/1.
 * 财务部成单统计报告测试
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
    classes = {ScheduleTaskConfig.class}
)
@TransactionConfiguration
public class CompletedOrderFinanceReportTaskTest {
    @Autowired
    CompletedOrderFinanceReportTask task;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private AutoRepository autoRepository;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private InternalUserRepository internalUserRepository;

    @Before
    public void before(){
        createInAWeekOrder();
        createOutAweekOrder();
    }


    /**
     * 发送一周内成单报表
     */
    @Test
    public void testInWeekOrder(){
        redisTemplate.opsForValue().setIfAbsent("first.completed.order", 1);
        task.process();
    }

    /**
     * 发送所有成单报表
     */
    @Test
    public void testAllOrder(){
        redisTemplate.delete("first.completed.order");
        task.process();
    }

    @After
    public void after(){
        resetTestData();
    }

    private void createInAWeekOrder(){
        PurchaseOrder purchaseOrder=createOrder();
        Date currentTime=new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentTime);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        purchaseOrder.setCreateTime(new Date());
        purchaseOrder.setOrderNo("T20160201888880");
        purchaseOrderRepository.save(purchaseOrder);
        createPayment(purchaseOrder, "P10010010000",calendar.getTime());
    }

    private void createOutAweekOrder(){
        PurchaseOrder purchaseOrder=createOrder();
        Date currentTime=new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentTime);
        calendar.add(Calendar.DAY_OF_MONTH, -8);
        purchaseOrder.setCreateTime(calendar.getTime());
        purchaseOrder.setOrderNo("T20160201888881");
        purchaseOrderRepository.save(purchaseOrder);
        createPayment(purchaseOrder, "P10010010001",calendar.getTime());
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

    private void createPayment(PurchaseOrder purchaseOrder,String paymentNo,Date updateTime){
        Payment payment=new Payment();
        payment.setUser(userRepository.findById(new Long(1)));
        payment.setPurchaseOrder(purchaseOrder);
        payment.setAmount(10000.00);
        payment.setChannel(PaymentChannel.Enum.ALIPAY_1);
        payment.setCreateTime(new Date());
        payment.setUpdateTime(updateTime);
        payment.setClientType(Channel.Enum.ALIPAY_21);
        payment.setOperator(internalUserRepository.findOne(new Long(1)));
        payment.setStatus(PaymentStatus.Enum.PAYMENTSUCCESS_2);
        payment.setThirdpartyPaymentNo(paymentNo);
        paymentRepository.save(payment);
    }

    /**
     * 重置测试数据
     */
    private void resetTestData(){
        PurchaseOrder purchaseOrder=purchaseOrderRepository.findFirstByOrderNo("T20160201888881");
        Payment payment=paymentRepository.findFirstByPurchaseOrder(purchaseOrder);
        paymentRepository.delete(payment);
        purchaseOrderRepository.delete(purchaseOrder);
        purchaseOrder=purchaseOrderRepository.findFirstByOrderNo("T20160201888880");
        payment=paymentRepository.findFirstByPurchaseOrder(purchaseOrder);
        paymentRepository.delete(payment);
        purchaseOrderRepository.delete(purchaseOrder);

    }
}
