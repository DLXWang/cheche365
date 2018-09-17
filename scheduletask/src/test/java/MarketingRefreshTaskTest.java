import com.cheche365.cheche.core.model.MarketingRule;
import com.cheche365.cheche.scheduletask.app.config.ScheduleTaskConfig;
import com.cheche365.cheche.scheduletask.task.MarketingRefreshTask;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;


/**
 * Created by xu.yelong on 2016/8/24.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
    classes = {ScheduleTaskConfig.class}
)
@TransactionConfiguration
public class MarketingRefreshTaskTest {
    @Autowired
    private MarketingRefreshTask marketingRefreshTask;

    @Test
    public void test(){
        marketingRefreshTask.process();
    }
}
