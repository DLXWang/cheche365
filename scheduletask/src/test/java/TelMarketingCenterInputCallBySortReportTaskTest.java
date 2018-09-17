import com.cheche365.cheche.scheduletask.app.config.ScheduleTaskConfig;
import com.cheche365.cheche.scheduletask.task.TelMarketingCenterInputCallBySortReportTask;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;


/**
 * Created by Luly on 2016/12/22.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
    classes = {ScheduleTaskConfig.class}
)
@TransactionConfiguration
public class TelMarketingCenterInputCallBySortReportTaskTest {
    @Autowired
    private TelMarketingCenterInputCallBySortReportTask telMarketingCenterInputCallBySortReportTask;

    @Test
    public void test(){
        telMarketingCenterInputCallBySortReportTask.process();
    }
}
