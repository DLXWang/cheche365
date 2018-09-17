package com.cheche365.cheche.scheduletask.task

import com.cheche365.cheche.core.message.InsuranceImportResultMessage
import com.cheche365.cheche.email.model.EmailInfo
import com.cheche365.cheche.manage.common.model.OfflineOrderImportHistory
import com.cheche365.cheche.manage.common.repository.OfflineOrderImportHistoryRepository
import com.cheche365.cheche.manage.common.service.offlinedata.OfflineOrderDataConvertHandler
import com.cheche365.cheche.scheduletask.listener.RedisNotifyEmailListener
import com.cheche365.cheche.scheduletask.model.MessageInfo
import com.cheche365.cheche.scheduletask.service.insurance.InsuranceImportResultService
import groovy.util.logging.Slf4j
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component

/**
 * Created by yinJianBin on 2017/9/22.
 */
@Component
@Slf4j
class InsuranceImportResultCallbackTask extends BaseTask {

    Logger logger = LoggerFactory.getLogger(RedisNotifyEmailListener.class);
    protected static final String INSURANCE_IMPORT_EMAIL_CONFIG_PATH = "/emailconfig/insurance_import_result_email.yml";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    InsuranceImportResultService insuranceImportResultService
    @Autowired
    OfflineOrderImportHistoryRepository offlineOrderImportHistoryRepository
    @Autowired
    StringRedisTemplate stringRedisTemplate
    @Autowired
    OfflineOrderDataConvertHandler offlineOrderDataConvertHandler

    @Override
    protected void doProcess() throws Exception {
        OfflineOrderImportHistory history
        String historyId = InsuranceImportResultMessage.getRunningFlag(stringRedisTemplate);
        if (StringUtils.isBlank(historyId)) {
            return
        }
        history = offlineOrderImportHistoryRepository.findOne(historyId as Long)
        if (history == null) {
            log.warn('线下数据导入数据库和redis状态不同步!')
            InsuranceImportResultMessage.resetProcessFlag(stringRedisTemplate)//删除redis中此任务正在运行的锁
            return
        }
        Map paramMap = [:]
        paramMap.put("result", "导入未完成!")
        paramMap.put("errorMessage", "ERROR!")
        paramMap.put("fileName", history.fileName)
        try {
            log.debug("线下数据导入定时任务获取到数据开始运行,offlineOrderImportHistory id = ({})", history.id)
            insuranceImportResultService.processData(history)
            paramMap.put("result", "导入完成!")
            paramMap.put("errorMessage", "")
        } catch (Exception e) {
            def currentTimeMillis = System.currentTimeMillis()
            paramMap.put("result", "导入发生异常!")
            paramMap.put("errorMessage", "异常原因: " + e.getMessage() + ",exception time:" + currentTimeMillis)
            insuranceImportResultService.updateHistory(history, true, history.getComment() + "; 导入发生异常!", 0)
            log.debug('线下数据导入发生异常,exception time:{}', currentTimeMillis, e)
        } finally {
            //如果成功,设置flag,让线下数据匹配的定时任务开始运行
            InsuranceImportResultMessage.setMatchRunningFlag(stringRedisTemplate, "true")
            InsuranceImportResultMessage.resetProcessFlag(stringRedisTemplate)//删除redis中此任务正在运行的锁
            log.info('线下数据导入定时任务执行完成,导入文件historyId:({})', historyId)
            paramMap.put("successInfo", offlineOrderDataConvertHandler.getResourceAbsoluteUrl(history.successPath))
            paramMap.put("failedInfo", offlineOrderDataConvertHandler.getResourceAbsoluteUrl(history.failedPath))
            EmailInfo emailInfo = assembleEmailInfo(INSURANCE_IMPORT_EMAIL_CONFIG_PATH, paramMap);
            addSimpleAttachment(emailInfo, INSURANCE_IMPORT_EMAIL_CONFIG_PATH, paramMap, []);
            def messageInfo = MessageInfo.createMessageInfo(emailInfo)
            messageInfoList.add(messageInfo);
        }

    }

}
