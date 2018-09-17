package com.cheche365.cheche.scheduletask.task

import com.cheche365.cheche.core.message.InsuranceImportResultMessage
import com.cheche365.cheche.email.model.EmailInfo
import com.cheche365.cheche.scheduletask.model.MessageInfo
import com.cheche365.cheche.scheduletask.model.OfflineDataMatchResult
import com.cheche365.cheche.scheduletask.service.insurance.OfflineImportDataMatchService
import groovy.util.logging.Slf4j
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component

/**
 * 泛华上传的数据和保险公司上传的数据进行匹配,更新PurchaseOrder的关联
 * Created by yinJianBin on 2017/12/8.
 */
@Component
@Slf4j
class OfflineImportDataMatchTask extends BaseTask {

    def emailconfigPath = '/emailconfig/offline_import_result.yml'

    OfflineImportDataMatchService offlineImportDataMatchService
    StringRedisTemplate stringRedisTemplate

    OfflineImportDataMatchTask(OfflineImportDataMatchService offlineImportDataMatchService, StringRedisTemplate stringRedisTemplate) {
        this.offlineImportDataMatchService = offlineImportDataMatchService
        this.stringRedisTemplate = stringRedisTemplate
    }

    @Override
    protected void doProcess() throws Exception {
        def matchRunningFlag = InsuranceImportResultMessage.getMatchRunningFlag(stringRedisTemplate)
        if (!matchRunningFlag) {
            return
        }
        InsuranceImportResultMessage.resetMatchProcessFlag(stringRedisTemplate)
        log.debug('开始匹配线下导入数据')
        def dataList = offlineImportDataMatchService.matchData() as ArrayList<OfflineDataMatchResult>
        if (dataList) {
            EmailInfo emailInfo = assembleEmailInfo(this.emailconfigPath, null);
            addSimpleAttachment(emailInfo, this.emailconfigPath, null, dataList);
            def messageInfo = MessageInfo.createMessageInfo(emailInfo);
            messageInfoList << messageInfo
        }
    }
}
