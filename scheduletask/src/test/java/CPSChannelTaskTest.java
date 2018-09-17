import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.BusinessActivity;
import com.cheche365.cheche.core.model.CooperationMode;
import com.cheche365.cheche.core.repository.BusinessActivityRepository;
import com.cheche365.cheche.core.repository.InternalUserRepository;
import com.cheche365.cheche.core.repository.PartnerRepository;
import com.cheche365.cheche.scheduletask.app.config.ScheduleTaskConfig;
import com.cheche365.cheche.scheduletask.task.CPSChannelTask;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import java.util.Date;

/**
 * Created by xu.yelong on 2016/2/2.
 * 合作渠道报表发送测试
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
    classes = {ScheduleTaskConfig.class}
)
@TransactionConfiguration
public class CPSChannelTaskTest {
    @Autowired
    private CPSChannelTask task;

    @Autowired
    private BusinessActivityRepository businessActivityRepository;

    @Autowired
    private PartnerRepository partnerRepository;

    @Autowired
    private InternalUserRepository internalUserRepository;
    @Before
    public void before(){
        addNoEndData();
        addEndData();
    }

    @Test
    public void test(){
        task.process();
    }

    @After
    public void after(){
        resetTestData();
    }

    /**
     * 测试未结束的商务活动（活动开始日期和当前日期相差7天）
     */
    private void addNoEndData(){
        Date beginDate=DateUtils.getCustomDate(new Date(), -7, 0, 0, 0);
        Date endDate=DateUtils.getCustomDate(new Date(), +1, 0, 0, 0);
        String code="B100100100";
        addTestData(beginDate,endDate,code);
    }

    /**
     * 测试已结束的商务活动（活动开始日期和当前日期相差7天，结束日期在前7天内）
     */
    private void addEndData(){
        Date beginDate=DateUtils.getCustomDate(new Date(), -5, 0, 0, 0);
        Date endDate=DateUtils.getCustomDate(new Date(), -1, 0, 0, 0);
        String code="B100100101";
        addTestData(beginDate,endDate,code);
    }

    private BusinessActivity addTestData(Date beginDate,Date endDate,String code){
        BusinessActivity businessActivity=new BusinessActivity();
        businessActivity.setName("测试");
        businessActivity.setPartner(partnerRepository.findOne(new Long(35)));
        businessActivity.setCooperationMode(CooperationMode.Enum.CHANGE_QUANTITY);
        businessActivity.setRebate(200.00);
        businessActivity.setBudget(1000.00);
        businessActivity.setStartTime(beginDate);
        businessActivity.setEndTime(endDate);
        businessActivity.setLandingPage("http://www.baidu.com");
        businessActivity.setCreateTime(new Date());
        businessActivity.setUpdateTime(new Date());
        businessActivity.setOperator(internalUserRepository.findOne(new Long(1)));
        businessActivity.setRefreshTime(new Date());
        businessActivity.setLinkMan("张三");
        businessActivity.setMobile("18612332430");
        businessActivity.setEmail("xuyl@cheche365.com");
        businessActivity.setFrequency(1);
        businessActivity.setCode(code);
        businessActivity.setObjTable("marketing");
        businessActivity.setLandingPageType(1);
        businessActivityRepository.save(businessActivity);
        return businessActivity;
    }

    private void resetTestData(){
        BusinessActivity businessActivity=businessActivityRepository.findFirstByCode("B100100100");
        businessActivityRepository.delete(businessActivity);
        businessActivity=businessActivityRepository.findFirstByCode("B100100101");
        businessActivityRepository.delete(businessActivity);
    }
}
