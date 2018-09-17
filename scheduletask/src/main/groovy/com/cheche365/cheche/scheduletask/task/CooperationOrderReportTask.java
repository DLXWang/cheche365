package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.email.model.EmailInfo;
import com.cheche365.cheche.scheduletask.model.MessageInfo;
import com.cheche365.cheche.scheduletask.model.PurchaseOrderInfo;
import com.cheche365.cheche.scheduletask.service.task.CooperationOrderReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * Created by xu.yelong on 2016-03-25.
 */
@Service
public class CooperationOrderReportTask extends BaseTask{
    @Autowired
    private CooperationOrderReportService cooperationOrderReportService;

    private String emailconfigPath = "/emailconfig/cooperation_order_report.yml";

    @Override
    protected void doProcess() throws Exception {
        messageInfoList.add(getMessageInfo());
    }

    private MessageInfo getMessageInfo() throws IOException {
        EmailInfo emailInfo = assembleEmailInfo(emailconfigPath, null);
        Map<String,List<PurchaseOrderInfo>> sheetDataMaps=cooperationOrderReportService.getPurchaseOrderInfo();
        addAttachment(emailInfo, emailconfigPath,null, sheetDataMaps);
        return MessageInfo.createMessageInfo(emailInfo);
    }
}
