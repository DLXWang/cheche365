package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.email.model.EmailInfo;
import com.cheche365.cheche.scheduletask.model.MessageInfo;
import com.cheche365.cheche.scheduletask.model.PurchaseOrderInfo;
import com.cheche365.cheche.scheduletask.service.task.CompletedOrderFinanceReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * 结算系统任务
 * 结算对象：订单、金额
 * 结算时间：每周五晚上6点发送
 * 结算周期：一周
 * 收件人：梁总
 * 标题： 成交订单统计表
 * 正文：
 * 渠道（代理、个人、大客户等，能具体到哪一个代理人和大客户最好）、姓名、车牌号、优惠前金额、优惠后金额、支付日期
 * Created by sunhuazhong on 2015/7/13.
 */
@Service
public class CompletedOrderFinanceReportTask extends BaseTask {

    @Autowired
    private CompletedOrderFinanceReportService completedOrderFinanceReportService;

    private String emailconfigPath = "/emailconfig/finance_completed_order_report.yml";
    /**
     * 执行任务详细内容
     *
     */
    @Override
    public void doProcess() throws IOException {
        //添加消息
        messageInfoList.add(getMessageInfo());
    }

    /**
     * 装配任务消息
     * @return
     */
    private MessageInfo getMessageInfo() throws IOException {
        // 邮件附件数据
        List<PurchaseOrderInfo> attachmentDataList = completedOrderFinanceReportService.getPurchaseOrderInfos();
        //装配邮件信息
        EmailInfo emailInfo = assembleEmailInfo(emailconfigPath, null);
        addSimpleAttachment(emailInfo,emailconfigPath,null,attachmentDataList);

        return MessageInfo.createMessageInfo(emailInfo);
    }
    
}
