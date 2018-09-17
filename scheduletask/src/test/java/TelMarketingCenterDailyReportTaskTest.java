import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.scheduletask.app.config.ScheduleTaskConfig;
import com.cheche365.cheche.scheduletask.task.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by wangshaobin on 2016/7/14.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
    classes = {ScheduleTaskConfig.class}
)
@TransactionConfiguration
public class TelMarketingCenterDailyReportTaskTest {
    /*@Autowired
    private TelMarketingCenterCallRecordReportTask dataSourceTask;*/

    /*@Autowired
    private TelMarketingCenterHistoryRepository telMarketingCenterHistoryRepository;*/

    /*@Autowired
    private TelMarketingCenterRepository telMarketingCenterRepository;*/

    /*@Autowired
    private UserRepository userRepository;*/

    /*@Autowired
    private TelMarketingCenterGenerateOrderReportTask issueTask;*/

    /*@Autowired
    private TelMarketingCenterLastYearUnpayOrderDataImportTask telMarketingCenterLastYearUnpayOrderDataImportTask;*/

    /*@Autowired
    private TelMarketingCenterPushDataTask telMarketingCenterPushDataTask;*/

    /*@Autowired
    private TelMarketingCenterExpireTimeDataImportTask telMarketingCenterExpireTimeDataImportTask;

    @Autowired
    private TelMarketingCenterRegisterNoOperationDataImportTask telMarketingCenterRegisterNoOperationDataImportTask;

    @Autowired
    private TelMarketingCenterQuotePhotoDataImportTask telMarketingCenterQuotePhotoDataImportTask;

    @Autowired
    private TelMarketingCenterAppointmentInsuranceDataImportTask telMarketingCenterAppointmentInsuranceDataImportTask;

    @Autowired
    private TelMarketingCenterMarketingDataImportTask telMarketingCenterMarketingDataImportTask;

    @Autowired
    private TelMarketingCenterUnPayOrderDataImportTask telMarketingCenterUnPayOrderDataImportTask;*/

    /*@Autowired
    private AnswernUltimoInsuranceReportTask answernUltimoCompleteOrderReportTask;*/

    /*@Autowired
    private DatebaoDataSyncReportTask datebaoDataSyncReportTask;*/

    @Autowired
    private FanhuaOrderBillReportTask fanhuaOrderBillReportTask;

    @Autowired
    private FanhuaOrderGiftReportTask fanhuaOrderGiftReportTask;

    @Autowired
    private TelMarketingCenterMonthGenerateOrderReportTask telMarketingCenterMonthGenerateOrderReportTask;

//    @Autowired
//    private ImportDataToMongoFromMysqlTask importDataToMongoFromMysqlTask;

    @Autowired
    private TelMarketingCenterQuoteDataImportTask telMarketingCenterQuoteDataImportTask;

    @Autowired
    private BaiduDataSyncEmailTask baiduDataSyncEmailTask;

    @Autowired
    private WeicheQuoteReportTask weicheQuoteReportTask;

    @Autowired
    private TelMarketingCenterToAQuoteDataImportTask telMarketingCenterToAQuoteDataImportTask;

    @Autowired
    private DailyInsuranceOrderActivityReportTask dailyInsuranceOrderActivityReportTask;

    @Test
    public void testMongo(){
//        importDataToMongoFromMysqlTask.process();
        /*telMarketingCenterQuoteDataImportTask.process();
        baiduDataSyncEmailTask.process();
        weicheQuoteReportTask.process();
        telMarketingCenterToAQuoteDataImportTask.process();
        dailyInsuranceOrderActivityReportTask.process();*/
    }

    @Test
    public void testTelMarketingCenterTask(){
        telMarketingCenterMonthGenerateOrderReportTask.process();
        fanhuaOrderGiftReportTask.process();
        fanhuaOrderBillReportTask.process();
        /*datebaoDataSyncReportTask.process();*/
        /*answernUltimoCompleteOrderReportTask.process();*/
        /*telMarketingCenterExpireTimeDataImportTask.process();
        System.out.println("车险到期日 定时任务执行成功");
        telMarketingCenterRegisterNoOperationDataImportTask.process();
        System.out.println("注册无行为 定时任务执行成功");
        telMarketingCenterQuotePhotoDataImportTask.process();
        System.out.println("拍照报价 定时任务执行成功");
        telMarketingCenterAppointmentInsuranceDataImportTask.process();
        System.out.println("预约 定时任务执行成功");
        telMarketingCenterMarketingDataImportTask.process();
        System.out.println("活动 定时任务执行成功");
        telMarketingCenterUnPayOrderDataImportTask.process();
        System.out.println("未支付订单用户 定时任务执行成功");*/
    }

    @Test
    public void testFindHistoryByPeriodTime(){
        /*Map<String, String> timePoints = getTimePoints();
        List<TelMarketingCenterHistory> histories = telMarketingCenterHistoryRepository.findHistoryByPeriodTime(timePoints.get("yesterdayTimePoint"),timePoints.get("todayTimePoint"));
        System.out.println("histories的长度："+histories.size());
        if(!CollectionUtils.isEmpty(histories)){
            for(int i=0;i<histories.size();i++){
                TelMarketingCenterHistory history = histories.get(i);
                TelMarketingCenter center = history.getTelMarketingCenter();
                String createTime = center.getCreateTime().toString();
                String operatorName = history.getOperator().getName();
                String mobile = center.getMobile();
                String source = center.getSource().getName();
                System.out.println("首次拨打日期:" + createTime + ",operatorName:" + operatorName + ",mobile:" + mobile + ",source:" + source);
            }
        }*/
    }

    @Test
    public void testTelMarketingIssue(){
        /*Date firstDate = DateUtils.getDate("2016-08-11 18:04:25",DateUtils.DATE_LONGTIME24_PATTERN);
        Date secondDate = DateUtils.getDate("2016-08-12 17:26:34",DateUtils.DATE_LONGTIME24_PATTERN);
        String str = "2016-08-11 18:04:25";
        *//*boolean flag = DateUtils.compareDate(firstDate, secondDate);*//*
        boolean flag = str.substring(0,10).equals(DateUtils.getDateString(secondDate,DateUtils.DATE_SHORTDATE_PATTERN));
        System.out.println("str:" +str.substring(0,10) +"secondDate:" +secondDate +"flag:" + flag);*/
        /*telMarketingCenterPushDataTask.process();*/
        /*telMarketingCenterLastYearUnpayOrderDataImportTask.process();*/
        /*dataSourceTask.process();*/
        /*issueTask.process();*/
    }

    @Test
    public void testTelMarketingDataSource(){
        /*//当天的时间点
        String todayTimePoint = DateUtils.getCurrentDateString("yyyy-MM-dd 18:00:00");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -3);
        //前一天的时间点
        String yesterdayTimePoint = new SimpleDateFormat("yyyy-MM-dd 18:00:00").format(cal.getTime());
        //从tel_marketing_center_history表中获取首次拨打日期、联系人、联系结果、备注
        List<Object[]> histories = telMarketingCenterHistoryRepository.findHistoryDataByCreateTimeBetween(yesterdayTimePoint,todayTimePoint);
        System.out.println("histories size:"+histories.size());
        for(Object[] objs:histories){
            System.out.println("tel_marketing_center:"+ objs[0]+" operator:" + objs[1] +" deal_result:"+ objs[2] +" COMMENT:"+objs[3]);
        }

        System.out.println("开始查询center信息");
        List<Long> centerIds = new ArrayList<>();
        centerIds.add(57732L);
        centerIds.add(57786L);
        centerIds.add(57799L);
        List<Object[]> centers = telMarketingCenterRepository.findUserSourceByIds(centerIds);
        System.out.println("centers size:"+centerIds.size());
        for(Object[] objs:centers){
            System.out.println("id:"+ objs[0]+" user:" + objs[1] +" mobile:"+ objs[2] +" name:"+objs[3]);
        }

        System.out.println("开始查询渠道信息");
        List<Long> userIds = new ArrayList<>();
        userIds.add(1110L);
        userIds.add(1214L);
        userIds.add(1263L);
        List<Object[]> userChannels = userRepository.findChannelByUserIds(userIds);
        System.out.println("userChannels size:"+userChannels.size());
        for(Object[] objs:userChannels){
            System.out.println("id:"+ objs[0]+" description:" + objs[1]);
        }
        task.process();*/
    }

    private Map<String, String> getTimePoints(){
        Map<String, String> timePoints = new HashMap<String, String>();
        //当天的时间点
        String todayTimePoint = DateUtils.getCurrentDateString("yyyy-MM-dd 18:00:00");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -3);
        //前一天的时间点
        String yesterdayTimePoint = new SimpleDateFormat("yyyy-MM-dd 18:00:00").format(cal.getTime());
        //从tel_marketing_center_history表中获取首次拨打日期、联系人、联系结果、备注
        timePoints.put("todayTimePoint",todayTimePoint);
        timePoints.put("yesterdayTimePoint",yesterdayTimePoint);
        return timePoints;
    }
}
