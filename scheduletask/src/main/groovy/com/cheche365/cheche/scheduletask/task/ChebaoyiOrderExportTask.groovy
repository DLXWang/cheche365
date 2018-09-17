package com.cheche365.cheche.scheduletask.task

import com.cheche365.cheche.common.util.DateUtils
import com.cheche365.cheche.email.model.EmailInfo
import com.cheche365.cheche.scheduletask.model.ChebaoyiOrderExportInfo
import com.cheche365.cheche.scheduletask.model.MessageInfo
import com.cheche365.cheche.scheduletask.service.task.ChebaoyiOrderExportService
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


/**
 * a端订单导出
 * Created by zhangpengcheng on 2018/6/25.
 */
@Service
public class ChebaoyiOrderExportTask extends BaseTask{

    Logger logger = Logger.getLogger(ChebaoyiOrderExportTask.class);

    private String emailconfigPath = "/emailconfig/chebaoyi_order_export_email.yml";

    @Autowired
    private ChebaoyiOrderExportService chebaoyiOrderExportService;

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
        Map<String,List<ChebaoyiOrderExportInfo>> sheetDataMaps =chebaoyiOrderExportService.getChebaoyiOrderExport();
        //装配邮件信息
        EmailInfo emailInfo=assembleEmailInfo(emailconfigPath,paramMap);
        addAttachment(emailInfo, emailconfigPath,paramMap, sheetDataMaps);
        return MessageInfo.createMessageInfo(emailInfo);

    }
}
