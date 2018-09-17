package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.email.model.EmailInfo;
import com.cheche365.cheche.scheduletask.model.MessageInfo;
import com.cheche365.cheche.scheduletask.model.PurchaseOrderInfo;
import com.cheche365.cheche.scheduletask.service.task.AnswernUltimoInsuranceReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 *  安心上月成单订单情况
 * 	  1) 保险公司为安心
 * 	  2) 确认时间在上月内
 * 	  3) 出单状态为3、4、5
 * 	  4) 实物礼品默认为茶叶，根据收货地址中的括号截取里面填写的实物信息【注意：括号有可能是中文或者英文】
 * 	  5) 备注信息太长的话，需要截取展示
 *  Created by wangshaobin on 2017/4/5.
 */
@Service
public class AnswernUltimoInsuranceReportTask extends BaseTask{

    @Autowired
    private AnswernUltimoInsuranceReportService answernUltimoCompleteOrderReportService;

    private String emailconfigPath = "/emailconfig/answern_ultimo_insurance_report_email.yml";

    @Override
    protected void doProcess() throws Exception {
        messageInfoList.add(getMessageInfo());
    }

    private MessageInfo getMessageInfo() throws IOException {
        Map<String,List<PurchaseOrderInfo>> sheetDataMaps =answernUltimoCompleteOrderReportService.getAnswernUltimoCompleteOrderData();
        EmailInfo emailInfo=assembleEmailInfo(emailconfigPath, null);
        addAttachment(emailInfo, emailconfigPath, null, sheetDataMaps);
        return MessageInfo.createMessageInfo(emailInfo);
    }
}
