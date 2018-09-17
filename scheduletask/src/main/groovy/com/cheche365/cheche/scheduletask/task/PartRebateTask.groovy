package com.cheche365.cheche.scheduletask.task

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.email.model.EmailInfo;
import com.cheche365.cheche.scheduletask.model.MessageInfo
import com.cheche365.cheche.scheduletask.model.PartRebateInfo;
import com.cheche365.cheche.scheduletask.model.PurchaseOrderInfo
import com.cheche365.cheche.scheduletask.service.task.PartRebateService
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * 部分退款——对账记录
 * Created by zhangpengcheng on 2018/5/7.
 */
@Service
public class PartRebateTask extends BaseTask {

    Logger logger = Logger.getLogger(PartRebateTask.class);

    private String emailconfigPath = "/emailconfig/part_rebate_email.yml";

    @Autowired
    private PartRebateService partRebateService;


    @Override
    protected void doProcess() throws Exception {
        messageInfoList.add(getMessageInfo());
    }

    private MessageInfo getMessageInfo() throws IOException {
        //邮件参数
        Map<String, Object> paramMap = new HashMap<>();
        int hour= Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        String yesterdayStr = DateUtils.getDateString(DateUtils.getCustomDate(new Date(), -1, 0, 0, 0), "yyyy年MM月dd日");
        String todayStr = DateUtils.getDateString(new Date(), "yyyy年MM月dd日");
        String timePeriod = yesterdayStr +"0时00分—"+todayStr+ "0时00分(24小时)";
        paramMap.put("timePeriod",timePeriod);
        //邮件附件内容
        Map<String,List<PartRebateInfo>> sheetDataMaps = partRebateService.getPartRebate();
        //装配邮件信息
        EmailInfo emailInfo=assembleEmailInfo(emailconfigPath,paramMap);
        addAttachment(emailInfo, emailconfigPath,paramMap, sheetDataMaps);
        return MessageInfo.createMessageInfo(emailInfo);
    }
}
