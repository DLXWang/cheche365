package com.cheche365.cheche.scheduletask.task

import com.cheche365.cheche.common.util.DateUtils
import com.cheche365.cheche.email.model.EmailInfo
import com.cheche365.cheche.scheduletask.model.DongchediMarketingReportModel
import com.cheche365.cheche.scheduletask.model.MessageInfo
import com.cheche365.cheche.scheduletask.service.task.DongchediMarketingReportService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * 懂车帝意外险活动报表 #10698
 * Created by zhangtc on 2018/1/29.
 */
@Service
class DongchediMarketingReportTask extends BaseTask {

    Logger logger = LoggerFactory.getLogger(DongchediMarketingReportTask.class)

    private String emailconfigPath = "/emailconfig/dongchedi_marketing_report.yml"

    @Autowired
    DongchediMarketingReportService dongchediMarketingReportService

    @Override
    protected void doProcess() throws Exception {
        messageInfoList.add(getMessageInfo())
    }

    private MessageInfo getMessageInfo() throws IOException {


        List<DongchediMarketingReportModel> emailDataList = dongchediMarketingReportService.getList()

        EmailInfo emailInfo = assembleEmailInfo(this.emailconfigPath, new HashMap() {

            {
                put("currentDateTime", DateUtils.getDateString(new Date(), "yyyy年MM月dd日HH时mm分ss秒"));
            }
        })
        addSimpleAttachment(emailInfo, this.emailconfigPath, new HashMap() {

            {
                put("currentDateTime", DateUtils.getDateString(new Date(), "yyyy年MM月dd日HH时mm分ss秒"));
            }
        }, emailDataList)
        return MessageInfo.createMessageInfo(emailInfo)
    }

    @Override
    protected void sendOnOff() {
        send = true;
        if (dataSize == 0) {
            send = false;
            logger.info("邮件数据为空，将不发送此邮件");
        }
    }
}
