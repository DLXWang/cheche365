import com.cheche365.cheche.scheduletask.app.config.ScheduleTaskConfig;
import com.cheche365.cheche.scheduletask.task.BaiduDataSyncEmailTask;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

/**
 * Created by wangshaobin on 2017/1/6.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
    classes = {ScheduleTaskConfig.class}
)
@TransactionConfiguration
public class BaiduDataSyncEmailTaskTest {
    @Autowired
    private BaiduDataSyncEmailTask baiduDataSyncEmailTask;

    @Test
    public void testBaiduDataSyncEmailTask(){
        Long start = System.currentTimeMillis();
        baiduDataSyncEmailTask.process();
        Long end = System.currentTimeMillis();
        System.out.println("百度数据同步邮件 定时任务执行成功");
        System.out.println("定时任务耗费时间：" + (end - start)/60000);
    }
}
