package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.email.model.EmailInfo;
import com.cheche365.cheche.scheduletask.model.MessageInfo;
import com.cheche365.cheche.scheduletask.model.PurchaseOrderInfo;
import com.cheche365.cheche.scheduletask.service.task.RefundFaildOrderReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * 退款失败的订单报表
 * 定时时间：一天一次 每天8点发送
 * Created by yinJianBin on 2017/2/20.
 */
@Service
public class RefundFaildOrderReportTask extends BaseTask {


    Logger logger = LoggerFactory.getLogger(RefundFaildOrderReportTask.class);

    @Autowired
    private RefundFaildOrderReportService refundFaildOrderReportService;

    private String emailconfigPath = "/emailconfig/refund_faild_order_report.yml";


    @Override
    public void doProcess() throws Exception {
        messageInfoList.add(getMessageInfo());
    }

    public MessageInfo getMessageInfo() throws IOException {
        //获取数据list
        List<PurchaseOrderInfo> dataList = refundFaildOrderReportService.getEmailInfoList();

        EmailInfo emailInfo = assembleEmailInfo(this.emailconfigPath, null);
        addSimpleAttachment(emailInfo, this.emailconfigPath, null, dataList);
        return MessageInfo.createMessageInfo(emailInfo);
    }

}
