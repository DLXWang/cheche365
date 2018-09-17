package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.email.model.EmailInfo;
import com.cheche365.cheche.scheduletask.model.MessageInfo;
import com.cheche365.cheche.scheduletask.model.PurchaseOrderInfo;
import com.cheche365.cheche.scheduletask.service.task.BaobiaoFinancialAccountsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class BaobiaoFinancialAccountsTask extends BaseTask{

    @Autowired
    BaobiaoFinancialAccountsService baobiaoFinancialAccountsService;

    private String emailconfigPath = "/emailconfig/baobiao_financial_accounts_email.yml";

    @Override
    protected void doProcess() throws Exception {
        messageInfoList.add(getMessageInfo());
    }

    private MessageInfo getMessageInfo() throws IOException {
        Map<String,List<PurchaseOrderInfo>> sheetDataMaps =baobiaoFinancialAccountsService.getAnswernUltimoCompleteOrderData();
        EmailInfo emailInfo=assembleEmailInfo(emailconfigPath, null);
        addAttachment(emailInfo, emailconfigPath, null, sheetDataMaps);
        return MessageInfo.createMessageInfo(emailInfo);
    }
}
