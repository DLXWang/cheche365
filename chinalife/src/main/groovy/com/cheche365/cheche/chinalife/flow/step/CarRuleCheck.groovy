package com.cheche365.cheche.chinalife.flow.step

import com.cheche365.cheche.common.flow.IStep
import com.cheche365.cheche.core.model.Auto
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.chinalife.util.BusinessUtils.getAutoEngineNo
import static com.cheche365.cheche.chinalife.util.BusinessUtils.getAutoVinNo
import static com.cheche365.cheche.chinalife.util.BusinessUtils.getCarEnrollDate
import static com.cheche365.cheche.chinalife.util.BusinessUtils.getCarKindCode
import static com.cheche365.cheche.chinalife.util.BusinessUtils.getCarOwner
import static com.cheche365.cheche.chinalife.util.BusinessUtils.getCarSeat
import static com.cheche365.cheche.chinalife.util.BusinessUtils.getDefaultPublishDate
import static com.cheche365.cheche.chinalife.util.BusinessUtils.getOldCustomerFlag
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.parser.util.BusinessUtils.getCommercialInsurancePeriodTexts
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC



/**
 * Created by suyq on 2015/9/25.
 * 自动核保
 */
@Component
@Slf4j
class CarRuleCheck implements IStep{
    private static final _CAR_RULES_CHECK = '/online/saleNewCar/carProposalcarRulesCheckForCalculateAndInsure.do'

    @Override
    run(Object context) {
        RESTClient client = context.client

        def deptId = context.deptId
        def parentId = context.parentId
        Auto auto = context.auto
        def vehicleInfo = context.vehicleInfo
        def carEnrollDate = getCarEnrollDate context
        def publishDate = getDefaultPublishDate context
        def (startDateText, endDateText) = getCommercialInsurancePeriodTexts(context)
        def carInfo = context.carInfo
        def carSeat = getCarSeat context
        def carKindCode = getCarKindCode context
        def oldCustomerFlag = getOldCustomerFlag context
        def carOwner = getCarOwner context

        def args = [
            requestContentType: URLENC,
            contentType       : JSON,
            path              : _CAR_RULES_CHECK,
            body              : [
                'temporary.geProposalArea.deptID'                         : deptId,
                'temporary.geProposalArea.parentid'                       : parentId,
                'temporary.quoteMain.areaCode'                            : deptId,
                'temporary.isOldCustomer'                                 : oldCustomerFlag,
                'temporary.carVerify.newCarAloneCarShipTaxCalculateFlag'  : '1',
                'temporary.pageFreeComboKindItem.mainKinds[0].id.kindCode': 'A',
                'temporary.pageFreeComboKindItem.mainKinds[0].id.riskCode': '0510',

                'temporary.quoteMain.geQuoteCars[0].carOwner'             : carOwner,
                'temporary.quoteMain.geQuoteCars[0].engineNo'             : getAutoEngineNo(context),
                'temporary.quoteMain.geQuoteCars[0].frameNo'              : getAutoVinNo(context),
                'temporary.quoteMain.geQuoteCars[0].licenseNo'            : auto.licensePlateNo,
                'temporary.quoteMain.geQuoteCars[0].actualValue'          : carInfo.actualValue,
                'temporary.quoteMain.geQuoteCars[0].brandName'            : vehicleInfo.brandName,
                'temporary.quoteMain.geQuoteCars[0].enrollDate'           : carEnrollDate,
                'temporary.quoteMain.geQuoteCars[0].seatCount'            : carSeat,
                'temporary.quoteMain.geQuoteCars[0].modeCode'             : vehicleInfo.modeCode,
                'temporary.quoteMain.geQuoteCars[0].publishDate'          : publishDate,
                'temporary.quoteMain.geQuoteCars[0].exhaustScale'         : vehicleInfo.exhaustScale,
                'temporary.quoteMain.geQuoteCars[0].purchasePrice'        : vehicleInfo.purchasePrice,
                'temporary.quoteMain.geQuoteCars[0].importFlag'           : vehicleInfo.importFlag,
                'temporary.quoteMain.geQuoteCars[0].searchSequenceNo'     : carInfo.searchSequenceNo,
                'temporary.quoteMain.geQuoteCars[0].carBlackFlag'         : carInfo.carBlackFlag,
                'temporary.quoteMain.geQuoteCars[0].vehicleStyle'         : carKindCode,
                'temporary.quoteMain.geQuoteCars[0].useNatureCode'        : '8A',
                'temporary.quoteMain.geQuoteCars[0].ecdemicFlag'          : '0',
                'temporary.quoteMain.geQuoteCars[0].vehicleTonnage'       : '0.0',
                'temporary.quoteMain.geQuoteCars[0].runMiles'             : '0',
                'temporary.quoteMain.geQuoteCars[0].limitLoad'            : '0',
                'temporary.quoteMain.geQuoteCars[0].newCarTempTaxiFlag'   : '0',
                'temporary.quoteMain.geQuoteCars[0].newVehicleFlag'       : '0',
                'temporary.quoteMain.geQuoteCars[0].runAreaCode'          : '04',
                'temporary.quoteMain.geQuoteCars[0].unCarLicenceNoFlag'   : '0',
                'temporary.quoteMain.geQuoteCars[0].useNatureFlag'        : '1',
                'temporary.quoteMain.geQuoteParties[1].partyFlag'         : '2',
                'temporary.quoteMain.geQuoteRisks[0].id.riskCode'         : '0510',
                'temporary.quoteMain.geQuoteRisks[1].id.riskCode'         : '0507',

                'temporary.quoteMain.geQuoteRisks[0].startDate'           : startDateText,
                'temporary.quoteMain.geQuoteRisks[0].endDate'             : endDateText,
                'temporary.quoteMain.geQuoteRisks[1].startDate'           : startDateText,
                'temporary.quoteMain.geQuoteRisks[1].endDate'             : endDateText,

                'temporary.quoteMain.geQuoteCars[0].GTFlag'               : '0',  // 呼和浩特
                'temporary.quoteMain.geQuoteCars[0].obtainCarModelV2XFlag': context.obtainCarModelV2XFlag,
            ]
        ]

        def result = client.post args, { resp, json ->
            json
        }

        if ('1' == result?.temporary?.resultType) {
            context.carInfo = result.temporary.quoteMain.geQuoteCars[0]
            log.info '车辆信息{}', context.carInfo
            log.debug '自动核保通过'
            getContinueFSRV result
        } else {
            getFatalErrorFSRV result?.temporary?.resultMessage
        }
    }

}
