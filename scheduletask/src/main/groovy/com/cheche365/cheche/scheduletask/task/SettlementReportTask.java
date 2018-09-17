package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.email.model.EmailInfo;
import com.cheche365.cheche.scheduletask.constants.TaskConstants;
import com.cheche365.cheche.scheduletask.model.MessageInfo;
import com.cheche365.cheche.scheduletask.service.task.SettlementReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

/**
 * 结算报表任务
 * 结算对象：订单、金额
 * 结算时间：每天0点
 * 结算周期：昨天00：00：00---23：59：59
 * 收件人：秦琪+张磊+待补
 * 标题： 2015-4-17日结算通知
 * 正文：
 * 累计保单总金额（含线上、线下，优惠后金额）         累计到账总金额（线上，优惠后金额）            累计保单数量（支付成功，含订单完成）
 * 昨日累计保单总金额（含线上、线下，优惠后金额）      昨日累计到账总金额（线上，优惠后金额）        昨日累计保单数量（支付成功，含订单完成）
 * Created by sunhuazhong on 2015/5/11.
 */
@Service
public class SettlementReportTask extends BaseTask {

    @Autowired
    private SettlementReportService settlementReportService;

    private String emailconfigPath = "/emailconfig/settlement_report_email.yml";

    /**
     * 执行任务详细内容
     *
     * @return
     */
    @Override
    public void doProcess() throws Exception {
        //添加消息
        messageInfoList.add(getMessageInfo());
    }

    /**
     * 装配任务消息
     * @return
     */
    private MessageInfo getMessageInfo() throws IOException {

        //邮件内容参数
        Map<String, Object> paramMap = settlementReportService.getContentParam();

        //装配邮件信息
        EmailInfo emailInfo = assembleEmailInfo(emailconfigPath, paramMap);

        return MessageInfo.createMessageInfo(emailInfo);
    }
}
