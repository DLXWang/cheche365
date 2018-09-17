package com.cheche365.cheche.scheduletask.task

import com.cheche365.cheche.scheduletask.model.DatebaoEmailInfo
import com.cheche365.cheche.scheduletask.model.MessageInfo
import com.cheche365.cheche.scheduletask.service.task.DatebaoOrderReportService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * 晶算师订单报表
 * 每天早上 8 点发送，前一天订单数据
 * 接收人 lixin@cheche365.com
 * Created by yinJianBin on 2017/5/18.
 */
@Service("datebaoOrderReportTask")
class DatebaoOrderReportTask extends BaseTask {

    Logger logger = LoggerFactory.getLogger(DatebaoOrderReportTask.class)

    @Autowired
    private DatebaoOrderReportService datebaoOrderReportService

    def emailconfigPath = "/emailconfig/datebao_order_report.yml"


    @Override
    protected void doProcess() throws Exception {
        messageInfoList.add(getMessageInfo());
    }

    def getMessageInfo() {
        //获取数据list
        List<DatebaoEmailInfo> dataList = datebaoOrderReportService.getEmailDataList();

        def emailInfo = assembleEmailInfo(this.emailconfigPath, null);
        addSimpleAttachment(emailInfo, this.emailconfigPath, null, dataList);

        MessageInfo.createMessageInfo(emailInfo);
    }
}
