package com.cheche365.cheche.scheduletask.service.insurance

import com.cheche365.cheche.common.util.DateUtils
import com.cheche365.cheche.core.repository.PurchaseOrderRepository
import com.cheche365.cheche.core.util.BeanUtil
import com.cheche365.cheche.manage.common.model.OfflineInsuranceCompanyImportData
import com.cheche365.cheche.manage.common.repository.OfflineInsuranceCompanyImportDataRepository
import com.cheche365.cheche.scheduletask.model.OfflineDataMatchResult
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service

/**
 * 泛华上传的数据和保险公司上传的数据进行匹配,更新PurchaseOrder的关联
 * Created by yinJianBin on 2017/12/8.
 */
@Service
@Slf4j
class OfflineImportDataMatchService {
    static final int PAGESIZE = 200

    OfflineInsuranceCompanyImportDataRepository repository
    PurchaseOrderRepository purchaseOrderRepository
    OfflineDataMatchHandler offlineDataMatchHandler

    OfflineImportDataMatchService(OfflineInsuranceCompanyImportDataRepository repository, PurchaseOrderRepository purchaseOrderRepository, OfflineDataMatchHandler offlineDataMatchHandler) {
        this.repository = repository
        this.purchaseOrderRepository = purchaseOrderRepository
        this.offlineDataMatchHandler = offlineDataMatchHandler
    }

    def matchData() {
        def resultInfoList = []
        List<OfflineInsuranceCompanyImportData> dataList = repository.findNotMatchedData(PAGESIZE)
        Integer batchNum = 1
        while (dataList.size() > 0) {
            log.info('线下数据匹配,当前批次({}),查询到可以匹配的数据({})条', batchNum++, dataList.size())
            dataList.groupBy { it.policyNo }.collect { policyNo, List<OfflineInsuranceCompanyImportData> models ->
                def results = offlineDataMatchHandler.matchByPolicyNo(policyNo, models)
                if (results) results.each {
                    OfflineDataMatchResult emailInfo = new OfflineDataMatchResult()
                    emailInfo.issueTime = DateUtils.getDateString(it.issueTime, DateUtils.DATE_SHORTDATE_PATTERN)
                    emailInfo.balanceTime = DateUtils.getDateString(it.balanceTime, DateUtils.DATE_SHORTDATE_PATTERN)
                    BeanUtil.copyPropertiesIgnore(it, emailInfo, "issueTime", "balanceTime")
                    resultInfoList << emailInfo
                }
                resultInfoList
            }
            if (dataList.size() < PAGESIZE) break
            dataList = repository.findNotMatchedData(PAGESIZE)
        }
        resultInfoList
    }


}
