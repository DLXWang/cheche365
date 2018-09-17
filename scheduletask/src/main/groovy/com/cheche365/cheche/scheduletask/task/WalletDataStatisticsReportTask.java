package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.email.model.EmailInfo;
import com.cheche365.cheche.scheduletask.model.MessageInfo;
import com.cheche365.cheche.scheduletask.model.WalletDataInfo;
import com.cheche365.cheche.scheduletask.service.task.WalletDataStatisticsReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * Created by wangshaobin on 2017/7/28.
 */
@Service
public class WalletDataStatisticsReportTask extends BaseTask {

    private String emailconfigPath = "/emailconfig/wallet_data_statistics_report_email.yml";

    @Autowired
    private WalletDataStatisticsReportService walletDataStatisticsReportService;

    @Override
    protected void doProcess() throws Exception {
        messageInfoList.add(getMessageInfo());
    }

    private MessageInfo getMessageInfo() throws IOException {
        //邮件附件内容
        Map<String,List<WalletDataInfo>> sheetDataMaps = walletDataStatisticsReportService.getWalletExcelInfo();
        //装配邮件信息
        EmailInfo emailInfo=assembleEmailInfo(emailconfigPath,null);
        addAttachment(emailInfo, emailconfigPath,null, sheetDataMaps);
        return MessageInfo.createMessageInfo(emailInfo);
    }
}
