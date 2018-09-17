package com.cheche365.cheche.misc.controller

import com.cheche365.cheche.core.repository.AgentRepository
import com.cheche365.cheche.core.repository.FanhuaBochengRepository
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller

/**
 * -Pargs="-poexp -id fanhuabocheng -df D:/保险公司/数据/20170206/fanhuabocheng/data.json -n 500 -rf D:/保险公司/数据/20170206/fanhuabocheng/rf.csv "
 */
@Controller
@Slf4j
class FanhuaBochengDataExporter extends PoFakeDataExporter {

    static final _ID = 'fanhuabocheng'

    @Autowired
    private FanhuaBochengRepository fanhuaBochengRepo

    @Autowired
    private AgentRepository agentRepo


    @Override
    getOrderList() {
        fanhuaBochengRepo.findAllValidData()
    }

    @Override
    getAgentList() {
        agentRepo.findDisabledAgents()
    }

    @Override
    getOrderInfo() {
        def c = {  orderInfo ->
            [
                randomTime(orderInfo.orderDate),
                orderInfo.licensePlateNo,
                orderInfo.customer,
                orderInfo.premium,
                orderInfo.compulsoryPremium,
                null,
                orderInfo.icShortName,
                orderInfo.channel,
                null,
                null,
                orderInfo.insuranceNo,
                orderInfo.compulsoryInsuranceNo,
                orderInfo.agent
            ]
        }
    }

    @Override
    checkId(id) {
        _ID == id
    }

}
