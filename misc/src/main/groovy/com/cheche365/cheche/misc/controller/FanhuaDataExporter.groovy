package com.cheche365.cheche.misc.controller

import com.cheche365.cheche.core.repository.AgentRepository
import com.cheche365.cheche.core.repository.FanhuaAutoRepository
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller

/**
 * -Pargs="-poexp -id fanhua -df D:/保险公司/数据/20170206/fanhua/data.json -n 500 -rf D:/保险公司/数据/20170206/fanhua/rf.csv "
 */
@Controller
@Slf4j
class FanhuaDataExporter extends PoFakeDataExporter {

    static final _ID = 'fanhua'

    @Autowired
    private FanhuaAutoRepository fanhuaAutoRepo

    @Autowired
    private AgentRepository agentRepo

    @Override
    getOrderList() {
        fanhuaAutoRepo.findAllValidData()
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
                orderInfo.customerId,
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
