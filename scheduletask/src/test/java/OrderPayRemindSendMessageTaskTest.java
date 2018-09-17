import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.*;
import com.cheche365.cheche.scheduletask.app.config.ScheduleTaskConfig;
import com.cheche365.cheche.scheduletask.task.OrderPayRemindSendMessageTask;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by xu.yelong on 2016/1/29.
 * 未支付订单短信提醒测试
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
    classes = {ScheduleTaskConfig.class}
)
@TransactionConfiguration
public class OrderPayRemindSendMessageTaskTest {
    private static final String ORDER_PAYMENT_REMIND_KEY = "schedules:task:payment:remind:order:id";

    @Autowired
    private OrderPayRemindSendMessageTask task;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private AutoRepository autoRepository;

    @Autowired
    private StringRedisTemplate redisTemplate;


    @Before
    public void before(){

    }

    /**
     * 支付宝支付订单
     */
    @Test
    public void testAliPayOrder(){
        PurchaseOrder purchaseOrder=addTestData(Channel.Enum.ALIPAY_21);
        purchaseOrderRepository.save(purchaseOrder);
        task.process();
    }

    /**
     * 非支付宝支付订单
     */
    @Test
    public void tests() {
        PurchaseOrder purchaseOrder=addTestData(Channel.Enum.ORDER_CENTER_11);
        purchaseOrderRepository.save(purchaseOrder);
        task.process();
    }


    @After
    public void after(){
        resetTestData();
    }



    /**
     * 添加测试用例数据
     * @param channel
     * @return
     */
    private PurchaseOrder addTestData(Channel channel){
        Date currentTime=new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentTime);
        calendar.add(Calendar.MINUTE, -30);

        User user=userRepository.findById(new Long(1));
        user.setMobile("186122232430");
        userRepository.save(user);

        Address address=addressRepository.findOne(new Long(1));
        Auto auto=autoRepository.findOne(new Long(1));

        PurchaseOrder purchaseOrder=new PurchaseOrder();
        purchaseOrder.setPayableAmount(10000.00);
        purchaseOrder.setPaidAmount(10000.00);
        purchaseOrder.setCreateTime(calendar.getTime());
        purchaseOrder.setObjId(new Long(1000));
        purchaseOrder.setApplicant(user);
        purchaseOrder.setSourceChannel(channel);
        purchaseOrder.setDeliveryAddress(address);
        purchaseOrder.setStatus(OrderStatus.Enum.PENDING_PAYMENT_1);
        purchaseOrder.setType(OrderType.Enum.INSURANCE);
        purchaseOrder.setAuto(auto);
        purchaseOrder.setOrderNo("T20160201888880");
        purchaseOrder.setArea(Area.Enum.BJ);
        purchaseOrder.setAudit(1);
        return purchaseOrder;
    }


    /**
     * 清楚测试用例数据
     * @return
     */
    private void resetTestData(){
        PurchaseOrder purchaseOrder=purchaseOrderRepository.findFirstByOrderNo("T20160201888880");
        purchaseOrderRepository.delete(purchaseOrder);
      //  clearCache(purchaseOrder.getId());
    }



    /**
     * 清除已缓存的订单ID
     */
    private void clearCache(){
        redisTemplate.delete(ORDER_PAYMENT_REMIND_KEY);
    }

}
