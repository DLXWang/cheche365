package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.email.model.EmailInfo;
import com.cheche365.cheche.scheduletask.model.MessageInfo;
import com.cheche365.cheche.scheduletask.service.task.QuestionableOrderReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by xu.yelong on 2016/12/15.
 */
@Service
public class QuestionableOrderReportTask extends BaseTask {
    @Autowired
    private QuestionableOrderReportService questionableOrderReportService;

    private String emailconfigPath = "/emailconfig/questionable_order_email.yml";

    @Override
    protected void doProcess() throws Exception {
        //状态同步异常订单报表
        orderStatusExceptionReport();
    }

    /**
     * 订单状态异常统计
     */
    private void orderStatusExceptionReport() throws Exception {
        Map<String, Object> paramMap = questionableOrderReportService.queryQestionableOrder();
        EmailInfo emailInfo = assembleEmailInfo(emailconfigPath, paramMap);
        messageInfoList.add(MessageInfo.createMessageInfo(emailInfo));
    }
}
