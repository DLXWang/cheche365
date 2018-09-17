package com.cheche365.cheche.scheduletask.task

import com.cheche365.cheche.common.util.DateUtils
import com.cheche365.cheche.email.model.EmailInfo
import com.cheche365.cheche.scheduletask.model.ChebaoyiWithdrawalInfoModel
import com.cheche365.cheche.scheduletask.model.MessageInfo
import com.cheche365.cheche.scheduletask.service.task.ChebaoyiWithdrawalReportService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * 车保易提现信息财务报表
 * Created by yinjainbin on 2018/4/3.
 */
@Service
@Slf4j
class ChebaoyiWithdrawalReportTask extends BaseTask {
    private final String emailConfigPath = "/emailconfig/chebaoyi_withdrawal_report.yml";
    Map jobDataMap

    @Autowired
    ChebaoyiWithdrawalReportService chebaoyiWithdrawalReportService;

    @Override
    protected void doProcess() throws Exception {
        messageInfoList.add(getMessageInfo());
    }

    private MessageInfo getMessageInfo() throws IOException {
        def paramMap = [:]
        def startTime = DateUtils.getCustomDate(new Date(), -1, 15, 35, 0)
        def endTime = DateUtils.getCustomDate(new Date(), 0, 15, 34, 59)

        String startTimeStr = jobDataMap?.get('startTimeStr')
        String endTimeStr = jobDataMap?.get('endTimeStr')
        if (startTimeStr && startTimeStr) {
            log.debug("获取到动态配置的参数,startTimeStr:{},endTimeStr:{}", startTimeStr, endTimeStr)
            startTime = DateUtils.getDate(startTimeStr, DateUtils.DATE_LONGTIME24_PATTERN)
            endTime = DateUtils.getDate(endTimeStr, DateUtils.DATE_LONGTIME24_PATTERN)
            paramMap.put("currentDate", DateUtils.getDateString(endTime, DateUtils.DATE_SHORTDATE_PATTERN));
            log.debug("配置时间范围参数获取车保易提现报表,startTime:{},endTime:{}", startTime, endTime)
        } else {
            paramMap = null
        }
        //邮件附件内容
        Map<String, List<ChebaoyiWithdrawalInfoModel>> sheetDataMaps = chebaoyiWithdrawalReportService.getWithdrawalInfo(startTime, endTime);
        //装配邮件信息
        EmailInfo emailInfo = assembleEmailInfo(emailConfigPath, null);
        addAttachment(emailInfo, emailConfigPath, paramMap, sheetDataMaps);
        return MessageInfo.createMessageInfo(emailInfo);
    }

}
