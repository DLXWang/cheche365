package com.cheche365.cheche.core.constants
/**
 * Author:   shanxf
 * Date:     2018/8/6 16:31
 */
class RebateCalculateConstants {

    static enum CalculateType{
        totalRebate,detainRebate,actualRebate
    }

    final static HashMap REBATE_CALCULATE = [
        commercialRebate    : [
            insuranceType     : 'commercialRebate',
            parentDetainRebate: {
                channelAgentRebate -> channelAgentRebate?.parentDetainCommercialRebate

            }
        ],
        compulsoryRebate    : [
            insuranceType     : 'compulsoryRebate',
            parentDetainRebate: {
                channelAgentRebate -> channelAgentRebate?.parentDetainCompulsoryRebate

            }
        ],
        onlyCommercialRebate: [
            insuranceType     : 'onlyCommercialRebate',
            parentDetainRebate: {
                channelAgentRebate -> channelAgentRebate?.onlyCommercialRebate

            }
        ],
        onlyCompulsoryRebate: [
            insuranceType     : 'onlyCompulsoryRebate',
            parentDetainRebate: {
                channelAgentRebate -> channelAgentRebate?.onlyCompulsoryRebate

            }
        ]
    ]

}
