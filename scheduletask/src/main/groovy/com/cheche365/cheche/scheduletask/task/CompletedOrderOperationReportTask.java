package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.email.model.EmailInfo;
import com.cheche365.cheche.scheduletask.constants.TaskConstants;
import com.cheche365.cheche.scheduletask.model.MessageInfo;
import com.cheche365.cheche.scheduletask.model.PurchaseOrderInfo;
import com.cheche365.cheche.scheduletask.service.task.CompletedOrderOperationReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * Created by liufei on 2016/1/13.
 * 运营部门出单中心成单统计
 */
@Service
public class CompletedOrderOperationReportTask extends BaseTask{

    @Autowired
    private CompletedOrderOperationReportService completedOrderOperationReportService;

    private String emailconfigPath = "/emailconfig/operation_completed_order_report.yml";

    @Override
    protected void doProcess() throws Exception {
        messageInfoList.add(getMessageInfo());
    }

    private MessageInfo getMessageInfo() throws IOException {
        //邮件参数
        // 邮件内容参数
        Map<String, Object> paramMap = new HashMap<>();
        int hour= Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        String yesterdayStr = DateUtils.getDateString(DateUtils.getCustomDate(new Date(), -1, 0, 0, 0), "yyyy年MM月dd日");
        String todayStr = DateUtils.getDateString(new Date(), "yyyy年MM月dd日");
        String timePeriod = yesterdayStr +"0时—"+todayStr+ (hour==0 ? "0":"16") + "时(40小时)";

        paramMap.put("timePeriod",timePeriod);//统计时间周期

        //邮件附件内容
        Map<String,List<PurchaseOrderInfo>> sheetDataMaps=completedOrderOperationReportService.getPurchaseOrderInfos();

        //装配邮件信息
        EmailInfo emailInfo=assembleEmailInfo(emailconfigPath,paramMap);
        addAttachment(emailInfo, emailconfigPath,paramMap, sheetDataMaps);

        return MessageInfo.createMessageInfo(emailInfo);
    }
}
