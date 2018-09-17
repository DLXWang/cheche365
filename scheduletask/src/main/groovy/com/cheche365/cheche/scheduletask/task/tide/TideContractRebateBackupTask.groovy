package com.cheche365.cheche.scheduletask.task.tide

import com.cheche365.cheche.core.repository.tide.TideRebateRecordRepository
import com.cheche365.cheche.scheduletask.task.BaseTask
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * 合约点位每天备份
 * Created by yinJianBin on 2018/5/6.
 */
@Service
@Slf4j
class TideContractRebateBackupTask extends BaseTask {

    @Autowired
    TideRebateRecordRepository tideRebateRecordRepository


    void doProcess() {
        def size = tideRebateRecordRepository.recordRebate()
        log.info "点位备份数据{}条", size
    }
}
