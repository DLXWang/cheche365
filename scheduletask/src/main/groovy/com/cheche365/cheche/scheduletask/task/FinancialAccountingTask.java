package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.email.model.EmailInfo;
import com.cheche365.cheche.scheduletask.model.MessageInfo;
import com.cheche365.cheche.scheduletask.model.PurchaseOrderInfo;
import com.cheche365.cheche.scheduletask.service.task.FinancialAccountingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.List;

/**
 * 财务台帐订单统计
 * 一天执行一次，每天7点执行
 * Created by sunhuazhong on 2016/5/30.
 */
@Service
public class FinancialAccountingTask extends BaseTask{
    private Logger logger = LoggerFactory.getLogger(FinancialAccountingTask.class);
    @Autowired
    private FinancialAccountingService financialAccountingService;

    private String emailconfigPath = "/emailconfig/financial_accounting_report.yml";

    @Override
    protected void doProcess() throws Exception {
        messageInfoList.add(getMessageInfo());
    }

    private MessageInfo getMessageInfo() throws IOException {
        //邮件附件内容
        List<PurchaseOrderInfo> attachmentDataList=financialAccountingService.getPurchaseOrderInfos();
//        if(CollectionUtils.isEmpty(attachmentDataList)) {
//            logger.debug("执行完成查询财务台帐定时任务，没有查询到订单数据");
//            return null;
//        }
        //装配邮件信息
        EmailInfo emailInfo=assembleEmailInfo(this.emailconfigPath,null);
        addSimpleAttachment(emailInfo, this.emailconfigPath, null, attachmentDataList);
        return MessageInfo.createMessageInfo(emailInfo);
    }
}
