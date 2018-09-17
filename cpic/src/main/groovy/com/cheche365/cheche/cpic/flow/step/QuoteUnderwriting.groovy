package com.cheche365.cheche.cpic.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getContinueWithIgnorableErrorFSRV
import static com.cheche365.cheche.parser.Constants._ERROR_MESSAGE_COMMERCIAL_PERIOD_CHECK_FAILURE
import static com.cheche365.cheche.parser.util.BusinessUtils.disableCommercial
import static groovyx.net.http.ContentType.JSON



/**
 * 商业险核保
 * Created by houjinxin on 2015/5/21.
 */
@Component
@Slf4j
class QuoteUnderwriting implements IStep {

    private static final _URL_PATH_QUOTE_UNDERWRITING = 'cpiccar/salesNew/quotation/quoteUnderwriting'

    @Override
    run(context) {
        RESTClient client = context.client
        def vehicleInfo = context.vehicleInfo
        def premiumInfo = context.premiumInfo
        def bodyParams = [
            calcAnswer          : '',
            random              : vehicleInfo.random ?: context.baseInfoResult.random,
            orderNo             : context.orderNo,
            CoverageInfo        : context.coverageInfo,
            PolicyRiskRates     : [
                ProfitmaxRate               : premiumInfo.policyRiskRates.ProfitmaxRate,
                DriveMileageRate            : premiumInfo.policyRiskRates.DriveMileageRate,
                NamedDriverRate             : premiumInfo.policyRiskRates.NamedDriverRate,
                DiscuteMinRate              : premiumInfo.policyRiskRates.DiscuteMinRate,
                VehicleCountRate            : premiumInfo.policyRiskRates.VehicleCountRate,
                ChannelRate                 : premiumInfo.policyRiskRates.ChannelRate,
                UnderwritingRate            : premiumInfo.policyRiskRates.UnderwritingRate,
                LastchangeableFeeRate       : premiumInfo.policyRiskRates.LastchangeableFeeRate,
                IsMatchFeeRule              : premiumInfo.policyRiskRates.IsMatchFeeRule,
                OriginalChangeableFeeRate   : premiumInfo.policyRiskRates.OriginalChangeableFeeRate,
                IsendorsefloatingRate       : premiumInfo.policyRiskRates.IsendorsefloatingRate,
                ProfitminRate               : premiumInfo.policyRiskRates.ProfitminRate,
                TrafficTransgressRate       : premiumInfo.policyRiskRates.TrafficTransgressRate,
                NonClaimDiscountRate        : premiumInfo.policyRiskRates.NonClaimDiscountRate,
                SpecialVehicleRate          : premiumInfo.policyRiskRates.SpecialVehicleRate,
                DiscuteMaxRate              : premiumInfo.policyRiskRates.DiscuteMaxRate,
                CustomLoyaltyRate           : premiumInfo.policyRiskRates.CustomLoyaltyRate,
                DriveAreaRate               : premiumInfo.policyRiskRates.DriveAreaRate,
                DriverAgeRate               : premiumInfo.policyRiskRates.DriverAgeRate,
                LastpagechangeablefeeRate   : premiumInfo.policyRiskRates.LastpagechangeablefeeRate,
                LastlocalPureriskPremium    : premiumInfo.policyRiskRates.LastlocalPureriskPremium,
                MultipleCoveragesRate       : premiumInfo.policyRiskRates.MultipleCoveragesRat
            ],
            Opportunity         : [
                mobile  : context.mobile
            ],
            sendSms             : '1',
            InsuredIdentifyType : '022001',
            InsuredIdentifyNo   : context.auto.identity,
            VehicleInfo         : [
                glassManufacturer   : context.glassManufacturer,
                driveArea           : vehicleInfo.driveArea ?: '2'
            ],
            PolicyBaseInfo      : [
                commecialPremuim        : premiumInfo.total,
                planCode                : context.planNo,
                TBsearchSeqNo           : premiumInfo.TBsearchSeqNo,
                TBsearchSeqNoExpireTime : premiumInfo.TBsearchSeqNoExpireTime,
                cusotmerLoyalty         : '00'
            ],
            planNoUuid          : context.renewable ? premiumInfo.uuid4 : premiumInfo.uuid1,
            planNoUuid1         : premiumInfo.uuid1,
            planNoUuid2         : '',
            planNoUuid3         : '',
            planNoUuid4         : '',
            tbRandom            : context.initVehicleBaseInfo.tbRandom
        ]

        def args = [
            requestContentType: JSON,
            contentType       : JSON,
            path              : _URL_PATH_QUOTE_UNDERWRITING,
            body              : bodyParams
        ]

        def result = client.post args, { resp, json ->
            json
        }
        if (result.orderNo) {
            context.orderNo = result.orderNo
            log.info '该套餐成功通过核保，订单号：{}',result.orderNo
            getContinueFSRV result
        } else {
            def msg = result.totalBlockDescrtion ?: result.webBlockDescrtion
            def realMsg = result.totalBlockDescrtion ?: msg?.split(':')[1].trim()
            log.info "来自太平洋官网的错误信息：${realMsg}"
            disableCommercial context, _ERROR_MESSAGE_COMMERCIAL_PERIOD_CHECK_FAILURE
            getContinueWithIgnorableErrorFSRV null, _ERROR_MESSAGE_COMMERCIAL_PERIOD_CHECK_FAILURE
        }
    }

}
