package com.cheche365.cheche.chinalife.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.chinalife.util.BusinessUtils.getCarEnrollDate
import static com.cheche365.cheche.chinalife.util.BusinessUtils.getCarKindCode
import static com.cheche365.cheche.chinalife.util.BusinessUtils.getCarSeat
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.parser.util.BusinessUtils.getCommercialInsurancePeriodTexts
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC



/**
 * 获取车辆价格
 */
@Component
@Slf4j
class CarActualPrice implements IStep {
    private static final _URL_CAR_ACTUAL_PRICE = '/online/saleNewCar/carProposalcarActualPrice.do'

    @Override
    run(context) {
        RESTClient client = context.client

        def deptId = context.deptId
        def parentId = context.parentId
        def carEnrollDate = getCarEnrollDate context
        def vehicleInfo = context.vehicleInfo
        def defaultStartDateText = getCommercialInsurancePeriodTexts(context).first
        def carSeat = getCarSeat context
        def carKindCode = getCarKindCode context

        def args = [
            requestContentType: URLENC,
            contentType       : JSON,
            path              : _URL_CAR_ACTUAL_PRICE,
            body              : [
                'temporary.geProposalArea.deptID'                   : deptId,
                'temporary.geProposalArea.parentid'                 : parentId,
                'temporary.quoteMain.areaCode'                      : deptId,
                'temporary.quoteMain.geQuoteCars[0].enrollDate'     : carEnrollDate,
                'temporary.quoteMain.geQuoteCars[0].seatCount'      : carSeat,
                'temporary.quoteMain.geQuoteCars[0].vehicleStyle'   : carKindCode,
                'temporary.quoteMain.geQuoteCars[0].carKindCode'    : carKindCode,
                'temporary.quoteMain.geQuoteCars[0].purchasePrice'  : vehicleInfo.purchasePrice,
                'temporary.quoteMain.geQuoteCars[0].vehicleTonnage' : vehicleInfo.vehicleTonnage,

                'temporary.quoteMain.geQuoteRisks[0].id.riskCode'   : '0510',
                'temporary.quoteMain.geQuoteRisks[1].id.riskCode'   : '0507',
                'temporary.quoteMain.geQuoteRisks[0].startDate'     : defaultStartDateText,
                'temporary.quoteMain.geQuoteRisks[1].startDate'     : defaultStartDateText
            ]
        ]

        def result = client.post args, { resp, json ->
            json
        }
        log.info '获取车辆准确价格：{}', result

        def actualValue = result.temporary.quoteMain.geQuoteCars[0].actualValue
        if (actualValue) {
            context.carInfo.actualValue = actualValue
            getContinueFSRV result
        } else {
            getFatalErrorFSRV '获取车辆准确价格失败'
        }
    }

}
