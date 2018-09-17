package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.email.model.EmailInfo;
import com.cheche365.cheche.scheduletask.model.MessageInfo;
import com.cheche365.cheche.scheduletask.model.PurchaseOrderInfo;
import com.cheche365.cheche.scheduletask.service.task.InternalOrderReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * 客服中心-出单中心内勤报表
 * 0 0 16,18 * * ?
 * Created by liufei on 2016/1/14.
 */
@Service
public class InternalOrderReportTask extends BaseTask{

    @Autowired
    private InternalOrderReportService internalOrderReportService;

    private String emailconfigPath = "/emailconfig/internal_order_report.yml";

    @Override
    protected void doProcess() throws Exception {
        messageInfoList.add(getMessageInfo());
    }

    private MessageInfo getMessageInfo() throws IOException {
        //邮件附件内容
        List<PurchaseOrderInfo> attachmentDataList=internalOrderReportService.getPurchaseOrderInfos();

        //装配邮件信息
        EmailInfo emailInfo=assembleEmailInfo(emailconfigPath, null);
        addSimpleAttachment(emailInfo,emailconfigPath,null,attachmentDataList);

        return MessageInfo.createMessageInfo(emailInfo);
    }
}
