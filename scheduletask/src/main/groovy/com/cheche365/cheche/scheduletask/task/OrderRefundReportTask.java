package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.email.model.EmailInfo;
import com.cheche365.cheche.scheduletask.model.MessageInfo;
import com.cheche365.cheche.scheduletask.model.PurchaseOrderInfo;
import com.cheche365.cheche.scheduletask.service.task.OrderRefundReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * Created by xu.yelong on 2016/11/16.
 * 退款订单统计报表
 */
@Service
public class OrderRefundReportTask extends BaseTask {
    @Autowired
    private OrderRefundReportService orderRefundReportService;

    private String emailConfigPath = "/emailconfig/refund_order_report.yml";

    @Override
    protected void doProcess() throws Exception {
        messageInfoList.add(getMessageInfo());
    }

    private MessageInfo getMessageInfo() throws IOException {
        Map<String, Object> paramMap = new HashMap<>();
        Date date=DateUtils.getCustomDate(new Date(),0,12,00,00);
        String timePeriod =DateUtils.getDateString(date,DateUtils.DATE_SHORTDATE_PATTERN);
        paramMap.put("timePeriod",timePeriod);//统计时间周期
        Map<String,List<PurchaseOrderInfo>> sheetDataMaps=orderRefundReportService.getRefundOrders();
        EmailInfo emailInfo=assembleEmailInfo(emailConfigPath,paramMap);
        addAttachment(emailInfo, emailConfigPath,paramMap, sheetDataMaps);
        return MessageInfo.createMessageInfo(emailInfo);
    }
}
