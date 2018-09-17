package com.cheche365.cheche.scheduletask.task

import com.cheche365.cheche.scheduletask.model.MessageInfo
import com.cheche365.cheche.scheduletask.model.TuhuEmailInfo
import com.cheche365.cheche.scheduletask.service.task.TuhuOrderReportService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service

/**
 * 途虎成单数据   #10647
 * Created by zhangtc on 2018/1/23.
 */
@Service
class TuhuOrderReportTask extends BaseTask {

    @Autowired
    TuhuOrderReportService tuhuOrderReportService
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private String emailconfigPath = "/emailconfig/tuhu_order_report.yml";

    @Override
    protected void doProcess() throws Exception {
        messageInfoList.add(getMessageInfo());
    }

    private MessageInfo getMessageInfo() throws IOException {
        List<TuhuEmailInfo> dataList = tuhuOrderReportService.getEmailDataList();

        def emailInfo = assembleEmailInfo(this.emailconfigPath, null);
        addSimpleAttachment(emailInfo, this.emailconfigPath, null, dataList);

        MessageInfo.createMessageInfo(emailInfo);
    }

}
