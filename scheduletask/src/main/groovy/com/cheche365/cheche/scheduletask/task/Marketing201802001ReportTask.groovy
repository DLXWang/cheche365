package com.cheche365.cheche.scheduletask.task

import com.cheche365.cheche.common.util.DateUtils
import com.cheche365.cheche.core.repository.MarketingRepository
import com.cheche365.cheche.core.repository.MarketingSuccessRepository
import com.cheche365.cheche.email.model.EmailInfo
import com.cheche365.cheche.scheduletask.model.Marketing201802001ReportModel
import com.cheche365.cheche.scheduletask.model.MessageInfo
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * 2018年2月抽奖活动 中奖人员导出
 * Created by zhangtc on 2018/1/4.
 */
@Service
public class Marketing201802001ReportTask extends BaseTask {

    private static final String MARKETING_CODE = "201802001";

    private static final String CACHE_PREV_ID = "schedulestask.marketing201802001.report.prev.id";

    private String emailconfigPath = "/emailconfig/marketing_201802001_report.yml";

    @Autowired
    private MarketingRepository marketingRepository;

    @Autowired
    private MarketingSuccessRepository marketingSuccessRepository;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void doProcess() throws Exception {
        messageInfoList.add(getMessageInfo());
    }


    private MessageInfo getMessageInfo() throws IOException {

        List marketingSuccessList = marketingSuccessRepository.findMarketing201802001()
        List<Marketing201802001ReportModel> emailDataList = new ArrayList<>()

        marketingSuccessList.collect() { map ->
            Marketing201802001ReportModel model = new Marketing201802001ReportModel();
            model.setLicensePlateNo(String.valueOf(map.licensePlateNo));
            model.setMobile(String.valueOf(map.mobile));
            model.setGood(detailCode(String.valueOf(map.detail)));
            model.setCreateTime(String.valueOf(map.createTime));
            model.setStatus(statusCode(String.valueOf(map.status)));
            emailDataList.add(model);
        };
        EmailInfo emailInfo = assembleEmailInfo(this.emailconfigPath, new HashMap() {

            {
                put("currentDateTime", DateUtils.getDateString(new Date(), "yyyy年MM月dd日HH时mm分ss秒"));
            }
        });
        addSimpleAttachment(emailInfo, this.emailconfigPath, new HashMap() {

            {
                put("currentDateTime", DateUtils.getDateString(new Date(), "yyyy年MM月dd日HH时mm分ss秒"));
            }
        }, emailDataList);
        return MessageInfo.createMessageInfo(emailInfo);
    }

    @Override
    protected void sendOnOff() {
        send = true;
        if (dataSize == 0) {
            send = false;
            logger.info("邮件数据为空，将不发送此邮件");
        }
    }

    static String detailCode(String detail) {
        switch (detail) {
            case '1':
                '出行意外险'
                break;
            case '2':
                '5元话费'
                break;
            case '3':
                '爱奇艺黄金会员'
                break;
            default:
                '未中奖'
        }
    }

    static String statusCode(String status) {
        return status == '1' ? '已领取' : '未领取'
    }
}
