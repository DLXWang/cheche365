package com.cheche365.cheche.chinalife.flow.step

import com.cheche365.cheche.common.flow.IStep
import com.cheche365.cheche.core.model.Auto
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.chinalife.util.BusinessUtils.getAutoEngineNo
import static com.cheche365.cheche.chinalife.util.BusinessUtils.getAutoTaxPremium
import static com.cheche365.cheche.chinalife.util.BusinessUtils.getAutoVinNo
import static com.cheche365.cheche.chinalife.util.BusinessUtils.getCarEnrollDate
import static com.cheche365.cheche.chinalife.util.BusinessUtils.getCarKindCode
import static com.cheche365.cheche.chinalife.util.BusinessUtils.getCarOwner
import static com.cheche365.cheche.chinalife.util.BusinessUtils.getCarSeat
import static com.cheche365.cheche.chinalife.util.BusinessUtils.getDefaultPublishDate
import static com.cheche365.cheche.chinalife.util.BusinessUtils.getOldCustomerFlag
import static com.cheche365.cheche.chinalife.util.BusinessUtils.populateQuoteRecordBZ
import static com.cheche365.cheche.common.util.DateUtils.getLocalDate
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.parser.Constants._DATETIME_FORMAT3
import static com.cheche365.cheche.parser.Constants._DATETIME_FORMAT4
import static com.cheche365.cheche.parser.Constants._DATE_FORMAT3
import static com.cheche365.cheche.parser.util.BusinessUtils.getCompulsorySupplementInfoPeriodTexts
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC



/**
 * 车船税
 */
@Component
@Slf4j
class GetCarAutoTax implements IStep{
    private static final _URL_CAR_Tax = '/online/saleNewCar/carProposalgetCarShipTaxInfo.do'

    @Override
    run(Object context) {
        if(!context.needGetAutoTax) {
            if (context.compulsoryEnabled) {
                def quoteRecord = populateQuoteRecordBZ(context, context.compulsoryPremium, context.autoTaxPremium)
                log.info '合并交强险报价之后的QuoteRecord：{}', quoteRecord
            }
            return getContinueFSRV(null)
        }

        RESTClient client = context.client

        def args = getRequestParameters(context)

        def quote = client.post args, { resp, json ->
            json
        }

        def autoTaxPremium = getAutoTaxPremium(quote.temporary.quoteMain.geQuoteCars?.get(0)?.geQuoteCarTax)
        log.info '车船税报价:{}', autoTaxPremium
        def quoteRecord = populateQuoteRecordBZ(context, context.compulsoryPremium, autoTaxPremium)
        log.info '合并交强险报价之后的QuoteRecord：{}', quoteRecord

        getContinueFSRV quote
    }

    private getRequestParameters(context) {
        def deptId = context.deptId
        def parentId = context.parentId
        Auto auto = context.auto
        def carInfo = context.carInfo
        def vehicleInfo = context.vehicleInfo
        def carEnrollDate = getCarEnrollDate context
        def publishDate = getDefaultPublishDate context
        def (startDateText, endDateText) = getCompulsorySupplementInfoPeriodTexts(context, _DATETIME_FORMAT3, false)
        def carSeat = getCarSeat context
        def comCode = context.comCode
        def (lastPolicyEndDate, year)  = getAutoTaxDateParams(context.dateInfo?.bzStartDateText)
        def carKindCode = getCarKindCode context
        def oldCustomerFlag = getOldCustomerFlag context
        def carOwner = getCarOwner context

        [
            requestContentType: URLENC,
            contentType       : JSON,
            path              : _URL_CAR_Tax,
            body              : [
                'temporary.geProposalArea.deptID'                                   : deptId,
                'temporary.geProposalArea.parentid'                                 : parentId,
                'temporary.quoteMain.areaCode'                                      : deptId,
                'temporary.carVerify.newCarCalculateFlag'                           : '0',
                'temporary.isOldCustomer'                                           : oldCustomerFlag,
                'temporary.quoteMain.geQuoteCars[0].licenseNo'                      : auto.licensePlateNo,
                'temporary.quoteMain.geQuoteCars[0].engineNo'                       : getAutoEngineNo(context),
                'temporary.quoteMain.geQuoteCars[0].frameNo'                        : getAutoVinNo(context),
                'temporary.quoteMain.geQuoteCars[0].carOwner'                       : carOwner,
                'temporary.quoteMain.geQuoteCars[0].enrollDate'                     : carEnrollDate,
                'temporary.quoteMain.geQuoteCars[0].publishDate'                    : publishDate,
                'temporary.quoteMain.geQuoteCars[0].searchSequenceNo'               : carInfo.searchSequenceNo,
                'temporary.quoteMain.geQuoteCars[0].actualValue'                    : vehicleInfo.actualValue,
                'temporary.quoteMain.geQuoteCars[0].brandName'                      : vehicleInfo.brandName,
                'temporary.quoteMain.geQuoteCars[0].exhaustScale'                   : vehicleInfo.exhaustScale,
                'temporary.quoteMain.geQuoteCars[0].importFlag'                     : vehicleInfo.importFlag,
                'temporary.quoteMain.geQuoteCars[0].modeCode'                       : vehicleInfo.RBCode,
                'temporary.quoteMain.geQuoteCars[0].purchasePrice'                  : vehicleInfo.purchasePrice,
                'temporary.quoteMain.geQuoteCars[0].seatCount'                      : carSeat,
                'temporary.quoteMain.geQuoteCars[0].userYear'                       : carInfo.userYear,
                'temporary.quoteMain.geQuoteCars[0].runMiles'                       : '28000',
                'temporary.quoteMain.geQuoteCars[0].useNatureCode'                  : '8A',
                'temporary.quoteMain.geQuoteCars[0].ecdemicFlag'                    : '0',
                'temporary.quoteMain.geQuoteCars[0].newCarTempTaxiFlag'             : '0',
                'temporary.quoteMain.geQuoteCars[0].newVehicleFlag'                 : '0',
                'temporary.quoteMain.geQuoteCars[0].carKindCode'                    : carKindCode,
                'temporary.quoteMain.geQuoteCars[0].geQuoteCarTax.lastPolicyEndDate': lastPolicyEndDate,
                'temporary.quoteMain.geQuoteCars[0].geQuoteCarTax.payTaxFeeYear'    : year,

                'temporary.quoteMain.geQuoteParties[1].partyFlag'                   : '2',
                'temporary.quoteMain.geQuoteRisks[0].id.riskCode'                   : '0510',
                'temporary.quoteMain.geQuoteRisks[1].id.riskCode'                   : '0507',
                'temporary.quoteMain.geQuoteRisks[0].startDate'                     : startDateText,
                'temporary.quoteMain.geQuoteRisks[0].endDate'                       : endDateText,
                'temporary.quoteMain.geQuoteRisks[1].startDate'                     : startDateText,
                'temporary.quoteMain.geQuoteRisks[1].endDate'                       : endDateText,

                'temporary.quoteMain.geQuoteCars[0].tonCount'                       : '0',
                'temporary.quoteMain.geQuoteCars[0].transferFlag'                   : '0',
                'temporary.quoteMain.geQuoteCars[0].transmissionType'               : '',
                'temporary.quoteMain.geQuoteCars[0].unCarLicenceNoFlag'             : '0',
                'temporary.quoteMain.geQuoteCars[0].fuelCode'                       : vehicleInfo.fuelCode,
                'temporary.quoteMain.geQuoteParties[0].partyFlag'                   : '1',
                'temporary.quoteMain.geQuoteParties[1].partyName'                   : carOwner,
                'temporary.quoteMain.geQuoteParties[2].partyName'                   : carOwner,
                'temporary.geProposalArea.tryrunflag'                               : '1',
                'temporary.quoteMain.comCodes'                                      : comCode
            ]
        ]
    }

    //根据所传的交强险开始日期减一天得到上份交强险到期日期,交强险开始日期减一年得到上份交强险的年份.
    private getAutoTaxDateParams(bzStartDate) {
        def startDate = getLocalDate(_DATE_FORMAT3.parse(bzStartDate))
        def lastPolicyEndDate = _DATETIME_FORMAT3.format startDate.minusDays(1)
        def year = _DATETIME_FORMAT4.format startDate.minusYears(1)

        new Tuple2(lastPolicyEndDate, year)
    }

}
