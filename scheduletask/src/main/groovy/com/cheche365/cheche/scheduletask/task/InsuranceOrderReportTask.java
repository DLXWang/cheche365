package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.email.model.EmailInfo;
import com.cheche365.cheche.scheduletask.constants.TaskConstants;
import com.cheche365.cheche.scheduletask.model.MessageInfo;
import com.cheche365.cheche.scheduletask.model.PurchaseOrderInfo;
import com.cheche365.cheche.scheduletask.service.task.InsuranceOrderReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * 出单中心出单报表
 * Created by liufei on 2016/1/13.
 */
@Service
public class InsuranceOrderReportTask extends BaseTask{

    @Autowired
    private InsuranceOrderReportService insuranceOrderReportService;

    private String emailconfigPath = "/emailconfig/insurance_order_report.yml";

    @Override
    protected void doProcess() throws Exception {
        messageInfoList.add(getMessageInfo());
    }

    private MessageInfo getMessageInfo() throws IOException {

        //邮件附件内容
        List<PurchaseOrderInfo> attachmentDataList=insuranceOrderReportService.getPurchaseOrderInfos();

        //装配邮件信息
        EmailInfo emailInfo=assembleEmailInfo(this.emailconfigPath,null);
        addSimpleAttachment(emailInfo, this.emailconfigPath, null, attachmentDataList);

        return MessageInfo.createMessageInfo(emailInfo);
    }
}
