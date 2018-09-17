package com.cheche365.cheche.scheduletask.task.chebaoyi

import com.cheche365.cheche.common.util.DateUtils
import com.cheche365.cheche.email.model.EmailInfo
import com.cheche365.cheche.scheduletask.model.ChebaoyiLevelOneQuoteDataReportModel
import com.cheche365.cheche.scheduletask.model.MessageInfo
import com.cheche365.cheche.scheduletask.service.task.ChebaoyiLevelOneQuoteReportService
import com.cheche365.cheche.scheduletask.task.BaseTask
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * 车保易指定账号报价情况导出
 * Created by yinJianBin on 2018/8/1.
 */
@Slf4j
@Component
class ChebaoyiLevelOneQuoteDataReportTask extends BaseTask {

    @Autowired
    ChebaoyiLevelOneQuoteReportService chebaoyiLevelOneQuoteReportService
    private String emailConfigPath = "/emailconfig/chebaoyi_level_one_quote_report.yml";
    Map jobDataMap

    @Override
    protected void doProcess() throws Exception {
        Date startTime = DateUtils.getCustomDate(new Date(), -1, 8, 0, 0)
        Date endTime = null
        def mobileList = ['15972671980', '13381128213', '13911983937', '13938915314','17610726687','17768668356','18851053656','17372796030','15210307310','13120356004']
        if (jobDataMap) {
            def startTimeStr = jobDataMap.get("startTimeStr");
            if (startTimeStr) {
                log.debug("获取到自定义执行时间参数,startTimeStr :{}", startTimeStr);
                startTime = DateUtils.getDate(startTimeStr as String, DateUtils.DATE_LONGTIME24_PATTERN);
            }
            def endTimeStr = jobDataMap.get("endTimeStr");
            if (endTimeStr) {
                log.debug("获取到自定义执行时间参数,endTimeStr :{}", endTimeStr);
                endTime = DateUtils.getDate(endTimeStr as String, DateUtils.DATE_LONGTIME24_PATTERN);
            }
            def mobileListStr = jobDataMap.get('mobileListStr')
            log.debug("获取到自定义执行时间参数,mobileListStr :{}", mobileListStr);
            if (mobileListStr) {
                mobileList = Arrays.asList(mobileListStr.split(','))
            }
        }
        if (!endTime) {
            endTime = DateUtils.getCustomDate(startTime, 1, 8, 0, 0)
        }

        messageInfoList.add(getMessageInfo(startTime, endTime, mobileList))
    }


    private MessageInfo getMessageInfo(Date startTime, Date endTime, def mobileList) throws IOException {
        List<ChebaoyiLevelOneQuoteDataReportModel> dataList = chebaoyiLevelOneQuoteReportService.getQuoteData(startTime, endTime, mobileList)
        EmailInfo emailInfo = assembleEmailInfo(this.emailConfigPath, null)
        addSimpleAttachment(emailInfo, this.emailConfigPath, null, dataList)
        return MessageInfo.createMessageInfo(emailInfo);
    }
}
