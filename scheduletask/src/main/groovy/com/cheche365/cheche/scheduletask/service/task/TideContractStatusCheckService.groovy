package com.cheche365.cheche.scheduletask.service.task

import com.cheche365.cheche.core.model.InternalUser
import com.cheche365.cheche.core.repository.tide.TideContractRepository
import com.cheche365.cheche.manage.common.constants.TideConstants
import com.cheche365.cheche.manage.common.service.TideLogAspectService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import static com.cheche365.cheche.manage.common.constants.TideConstants.STATUS_EFFECTIVE_ING
import static com.cheche365.cheche.manage.common.constants.TideConstants.STATUS_MAP

/**
 * Created by yinJianBin on 2018/5/7.
 */
@Service
@Slf4j
class TideContractStatusCheckService {

    @Autowired
    TideContractRepository tideContractRepository
    @Autowired
    TideLogAspectService tideLogAspectService


    @Transactional
    def effectCheck() {
        def date = new Date()
        def list = tideContractRepository.findAllByEffectiveDateLessThanAndStatus(date, TideConstants.STATUS_CREATE)
        def statusString = STATUS_MAP.get(STATUS_EFFECTIVE_ING)
        log.debug("合约:查询到状态要更新为($statusString)的合约数据${list.size()}条")
        list.each { contract ->
            contract.setStatus(STATUS_EFFECTIVE_ING)
            tideContractRepository.save(contract)
            tideLogAspectService.saveLog("定时任务更新合约状态为 $statusString", '', 'contract_effect', contract.id, InternalUser.ENUM.SYSTEM.id)
        }
    }

    @Transactional
    def expireCheck() {
        def date = new Date()
        def list = tideContractRepository.findAllByExpireDateLessThanAndStatus(date, STATUS_EFFECTIVE_ING)
        def statusString = STATUS_MAP.get(TideConstants.STATUS_EXPIRED)
        log.debug("合约:查询到状态要更新为($statusString)的合约数据${list.size()}条")
        list.each { contract ->
            contract.setStatus(TideConstants.STATUS_EXPIRED)
            contract.setDisable(true)
            tideContractRepository.save(contract)
            tideLogAspectService.saveLog("定时任务更新合约状态为 $statusString", '', 'contract_effect', contract.id, InternalUser.ENUM.SYSTEM.id)
        }

    }

}
