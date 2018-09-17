package com.cheche365.cheche.scheduletask.service.task

import com.cheche365.cheche.core.repository.tide.TideContractRebateRepository
import com.cheche365.cheche.manage.common.constants.TideConstants
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import static com.cheche365.cheche.manage.common.constants.TideConstants.REBATESTATUS_MAP
/**
 * Created by yinJianBin on 2018/5/7.
 */
@Service
@Slf4j
class TideContractRebateStatusCheckService {

    @Autowired
    TideContractRebateRepository tideContractRebateRepository

    @Transactional
    def effectCheck() {
        def date = new Date()
        def list = tideContractRebateRepository.findAllByEffectiveDateLessThanAndStatus(date, TideConstants.STATUS_CREATE)
        def statusString = REBATESTATUS_MAP.get(TideConstants.STATUS_EFFECTIVE_ING)
        log.debug("点位:查询到状态要更新为($statusString)的点位数据${list.size()}条")
        list.each { rebate ->
            rebate.setStatus(TideConstants.STATUS_EFFECTIVE_ING)
            rebate.setDisable(false)
            tideContractRebateRepository.save(rebate)
        }

    }

    @Transactional
    def expireCheck() {
        def date = new Date()
        def list = tideContractRebateRepository.findAllByExpireDateLessThanAndStatus(date, TideConstants.STATUS_EFFECTIVE_ING)
        def statusString = REBATESTATUS_MAP.get(TideConstants.STATUS_EXPIRED)
        log.debug("点位:查询到状态要更新为($statusString)的点位数据${list.size()}条")
        list.each { rebate ->
            rebate.setStatus(TideConstants.STATUS_EXPIRED)
            rebate.setDisable(true)
            tideContractRebateRepository.save(rebate)
        }
    }

}
