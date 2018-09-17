import com.cheche365.cheche.scheduletask.app.config.ScheduleTaskConfig;
import com.cheche365.cheche.scheduletask.task.DailyInsuranceStatusRefreshTask;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;


/**
 * Created by Luly on 2016/8/24.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
    classes = {ScheduleTaskConfig.class}
)
@TransactionConfiguration
public class DailyInsuranceStatusRefreshTaskTest {
    @Autowired
    private DailyInsuranceStatusRefreshTask dailyInsuranceStatusRefreshTask;

    @Test
    public void test(){
        dailyInsuranceStatusRefreshTask.process();
    }
}
