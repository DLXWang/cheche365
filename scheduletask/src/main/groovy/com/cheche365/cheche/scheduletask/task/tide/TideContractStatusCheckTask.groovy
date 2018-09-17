package com.cheche365.cheche.scheduletask.task.tide

import com.cheche365.cheche.scheduletask.service.task.TideContractRebateStatusCheckService
import com.cheche365.cheche.scheduletask.service.task.TideContractStatusCheckService
import com.cheche365.cheche.scheduletask.task.BaseTask
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * 合约以及点位生效定时任务
 * Created by yinJianBin on 2018/5/6.
 */
@Component
class TideContractStatusCheckTask extends BaseTask {

    @Autowired
    TideContractStatusCheckService tideContractStatusCheckService
    @Autowired
    TideContractRebateStatusCheckService tideContractRebateStatusCheckService


    void doProcess() {
        checkContractRebateStatus()
    }

    def checkContractRebateStatus() {
        tideContractRebateStatusCheckService.effectCheck()
        tideContractRebateStatusCheckService.expireCheck()
    }
}
