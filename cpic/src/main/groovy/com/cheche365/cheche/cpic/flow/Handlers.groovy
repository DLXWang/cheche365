package com.cheche365.cheche.cpic.flow

import com.cheche365.cheche.core.model.Auto
import groovy.json.JsonBuilder
import groovy.util.logging.Slf4j

import java.util.regex.Matcher
import java.util.regex.Pattern

import static com.cheche365.cheche.common.Constants._DATETIME_FORMAT3
import static com.cheche365.cheche.common.Constants._DATE_FORMAT3
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopBreakFSRV
import static com.cheche365.cheche.cpic.flow.Constants._DATETIME_FORMAT_NEW
import static com.cheche365.cheche.cpic.flow.Constants._DATE_FORMAT
import static com.cheche365.cheche.parser.Constants._SUPPLEMENT_INFO_BRAND_CODE_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.util.BusinessUtils.getCompulsoryInsurancePeriodTexts
import static com.cheche365.cheche.parser.util.BusinessUtils.setCommercialInsurancePeriodTexts
import static com.cheche365.flow.core.util.FlowUtils.getNeedSupplementInfoFSRV
import static java.time.LocalDate.now as today
import static java.time.LocalDateTime.from as todate



/**
 * RPG & RH
 */
@Slf4j
class Handlers {

    static final _SUBMIT_VEHICLE_DETAIL_INFO_1 = { context, complusoryInsuranceFlag ->
        Auto auto = context.auto
        def mobile = context.mobile
        def vehicleInfo = context.vehicleInfo
        def currentDate = today()
        def commercialStartDate = currentDate.plusDays(1).toString() + ' 00:00:00'
        def commercialEndDate = currentDate.plusYears(1).plusDays(1).toString() + ' 00:00:00'
        def applicationDate = currentDate
        def issueDate = currentDate
        def startDate = currentDate.plusDays(1)
        def registerDate = vehicleInfo?.registerDate ?: currentDate.minusDays(1)
        def orderId = context.orderNo as long
        def orderNo = context.orderNo
        def opportunityId = context.opportunityId
        // TODO：字串都改成map
        def bodyContentStr = """\
            {
                "tempSave": "0",
                "VehicleInfo": {
                    "driveArea": ${vehicleInfo.driveArea},
                    "plateNo": "${auto.licensePlateNo}",
                    "registerDate": "${registerDate}",
                    "carVIN": "${auto.vinNo}",
                    "engineNo": "${auto.engineNo}",
                    "vehicleBrand": "${vehicleInfo?.vehicleBrand ?: ""}",
                    "vehicleModel": "${context.noCarInfo ? vehicleInfo.vehicleModel : vehicleInfo?.makerModel ?: ""}",
                    "makerModel": "${context.noCarInfo ? (vehicleInfo.vehicleModel ?: "") : vehicleInfo?.makerModel}",
                    "moldName": "${context.noCarInfo ? (vehicleInfo?.moldName ?: "") : ""}",
                    "engineCapacity": "${vehicleInfo?.engineCapacity ?: ""}",
                    "moldCharacterCode": "${vehicleInfo?.moldCharacterCode ?: ""}",
                    "carryingCapacity": "${context.noCarInfo ? "" : vehicleInfo.tonnage ?: ""}",
                    "producingArea": "${vehicleInfo.producingArea ?: ""}",
                    "glassManufacturer": "${vehicleInfo.glassManufacturer ?: ""}",
                    "familyCode": "${vehicleInfo.familyCode}",
                    "rVehicleFamily": "${context.noCarInfo ? vehicleInfo.rVehicleFamily : vehicleInfo?.familyName ?: ""}",
                    "riskflagCode": "${vehicleInfo.riskflagCode}",
                    "jyRiskFlagCode": "${vehicleInfo.jyRiskFlagCode}",
                    "EmptyWeight": "${vehicleInfo?.EmptyWeight ?: ""}",
                    "seatCount": "${context.noCarInfo ? vehicleInfo.seatCount : vehicleInfo?.seat ?: ""}",
                    "transferVehicleFlag": "0",
                    "transferDate": "",
                    "complusoryInsuranceFlag": "${complusoryInsuranceFlag}",
                    "VehicleVariety1": "01",
                    "VehicleVariety2": "01",
                    "loanVehicleFlag": "0",
                    "plateColor": "1",
                    "purchasePrice": "${vehicleInfo.price ?: vehicleInfo.purchasePrice}",
                    "marketDate": "${vehicleInfo.marketYear}",
                    "abs": "${vehicleInfo.abs}",
                    "yearPattern": "${vehicleInfo.yearPattern ?:""}",
                    "fullWeightMax": "${vehicleInfo.fullWeightMax}",
                    "fullWeightMin": "${vehicleInfo.fullWeightMin}",
                    "seatMax": "",
                    "seatMin": "",
                    "groupName": "${vehicleInfo.groupName}",
                    "vehicleClassCode": "${vehicleInfo.vehicleClassCode}",
                    "engineDesc": "${vehicleInfo.engineDesc}",
                    "vehicleFgwCode": "${vehicleInfo.vehicleFgwCode}",
                    "vehicleAlias": "${vehicleInfo.vehicleAlias}",
                    "insuranceCode": "${vehicleInfo.insuranceCode}",
                    "batholith": "",
                    "bodyType": "${vehicleInfo.bodyType ?:""}",
                    "vehicleClassName": "${vehicleInfo.vehicleClassName}",
                    "groupCode": "${vehicleInfo.groupCode}",
                    "vehicleFgwName": "${vehicleInfo.vehicleFgwName}",
                    "factoryName": "${vehicleInfo.factoryName}",
                    "antiTheft": "${vehicleInfo.antiTheft}",
                    "power": "${vehicleInfo.vehiclePower}",
                    "searchCode": "",
                    "tbHcarType": "${vehicleInfo.tbHcarType}",
                    "insuranceClass": "${vehicleInfo.insuranceClass}",
                    "bakString1": "",
                    "bakString2": "",
                    "bakString3": "",
                    "bakString4": "",
                    "bakString5": ""
                },
                "PolicyBaseInfo": {
                    "provinceCode": "${context.provinceCode}",
                    "cityCode": "${context.cityCode}",
                    "branchCode": "${context.branchCode}",
                    "orgdeptCode": "${context.orgdeptCode}",
                    "commecialStartDate": "${commercialStartDate}",
                    "commecialEndDate": "${commercialEndDate}",
                    "applicationDate": "${applicationDate}",
                    "issueDate": "${issueDate}",
                    "otherSource": ""
                },
                "Opportunity": {
                    "licenseOwner": "${auto.owner}",
                    "mobile": "${mobile}",
                    "opportunityId": "${opportunityId}"
                },
                "CoverageInfo": [
                    {
                        "coverageCode": "DamageLossCoverage",
                        "coverageName": "specialFlag",
                        "coverageAmount": "${vehicleInfo.price?vehicleInfo.price : vehicleInfo?.kindPrice}"
                    },
                    {
                        "coverageCode": "ThirdPartyLiabilityCoverage",
                        "coverageName": "机动车第三者责任保险",
                        "coverageAmount": "500000.00"
                    }
                ],
                "OrderInfo": {
                    "orderId": "${orderId}",
                    "orderNo": "${orderNo}"
                },
                "priceMap": {
                    "registerDate": "${registerDate}",
                    "startDate": "${startDate}",
                    "price": "${vehicleInfo?.price ?: ""}",
                    "taxPrice": "${vehicleInfo?.taxPrice ?: ""}",
                    "kindPrice": "${vehicleInfo?.kindPrice ?: ""}",
                    "taxKindPrice": "${vehicleInfo?.taxKindPrice ?: ""}"
                },
                "carMark": "${context.noCarInfo ? (vehicleInfo.carMark ?:"" ): ""}",
                "confirmPlateNo": "",
                "random": "${context.baseInfoResult.random}",
                "marketDate": "${vehicleInfo.marketYear}",
                "insurancePageStatus": "${context.orderOpeartion ? "" : context.insurancePageStatus ?: ""}",
                "vehicleCode": "${vehicleInfo.rVehicleFamily}",
                "userId": "",
                "ocrUsedFlag": "",
                "plateNoForUpdate": "",
                "licenseOwnerForUpdate": "",
                "tbRandom": "${context.initVehicleBaseInfo.tbRandom}"
            }
        """
        Pattern p = Pattern.compile("\\s{2,}|\t|\r|\n");
        Matcher m = p.matcher(bodyContentStr);
        m.replaceAll("");
    }

    private static final _SUBMIT_VEHICLE_DETAIL_INFO_2 = { context, questionAnswer = '' ->
        Auto auto = context.auto
        def mobile = context.mobile
        def vehicleInfo = context.vehicleInfo

        def currentDate = today()
        def commercialStartDate = _DATETIME_FORMAT3.format currentDate.plusDays(1)
        def commercialEndDate = _DATETIME_FORMAT3.format currentDate.plusYears(1).plusDays(1)
        def applicationDate = currentDate
        def issueDate = currentDate
        def startDate = currentDate.plusDays(1)
        def enrollDate = vehicleInfo?.registerDate ?: currentDate.minusDays(1)
        if (context.auto.enrollDate) {
            def dateFormat = _DATE_FORMAT
            enrollDate = dateFormat.format(context.auto.enrollDate)
        }
        def orderId = context.orderNo as long
        def orderNo = context.orderNo
        def opportunityId = context.opportunityId
        def transferDate = context.extendedAttributes?.transferDate

        def bodyContentStr = """\
            {
                "tempSave": "0",
                "VehicleInfo": {
                    "driveArea": ${vehicleInfo.driveArea ?:"2"},
                    "plateNo": "${auto.licensePlateNo}",
                    "registerDate": "${enrollDate}",
                    "carVIN": "${auto.vinNo ?: vehicleInfo.newCarVIN}",
                    "engineNo": "${auto.engineNo ?: vehicleInfo.newEngineNo}",
                    "vehicleBrand": "${vehicleInfo.vehicleBrand}",
                    "vehicleModel": "${vehicleInfo.vehicleModel}",
                    "makerModel": "${vehicleInfo.vehicleModel}",
                    "moldName": "${vehicleInfo.moldName ?: ""}",
                    "engineCapacity": "${vehicleInfo.engineCapacity}",
                    "moldCharacterCode": "${vehicleInfo.moldCharacterCode}",
                    "carryingCapacity": "",
                    "producingArea": "${vehicleInfo.producingArea}",
                    "glassManufacturer": "",
                    "familyCode": "${vehicleInfo.familyCode ?: ""}",
                    "rVehicleFamily": "${vehicleInfo.rVehicleFamily}",
                    "riskflagCode": "${vehicleInfo.riskflagCode ?: ""}",
                    "jyRiskFlagCode": "${vehicleInfo.jyRiskFlagCode ?: ""}",
                    "EmptyWeight": "${vehicleInfo.EmptyWeight ?: ""}",
                    "newVehicleFlag": "0",
                    "seatCount": "${!context.defaultSeatCount ? vehicleInfo.seatCount : context.defaultSeatCount}",
                    "showIsTelSaleFlag": "0",
                    "transferVehicleFlag": "${transferDate ? 1 : 0}",
                    "transferDate": "${transferDate ? _DATE_FORMAT3.format(transferDate) : ''}",
                    "VehicleVariety1": "01",
                    "VehicleVariety2": "01",
                    "loanVehicleFlag": "0",
                    "plateColor": "1",
                    "purchasePrice": "${vehicleInfo.price ?: vehicleInfo.purchasePrice}",
                    "marketDate": "${vehicleInfo.marketYear ?: ""}",
                    "abs": "${vehicleInfo.abs ?: ""}",
                    "yearPattern": "${vehicleInfo.yearPattern ?:""}",
                    "fullWeightMax": "${vehicleInfo.fullWeightMax ?: ""}",
                    "fullWeightMin": "${vehicleInfo.fullWeightMin ?: ""}",
                    "seatMax": "${vehicleInfo.seatMax ?:""}",
                    "seatMin": "${vehicleInfo.seatMin ?:""}",
                    "groupName": "${vehicleInfo.groupName ?: ""}",
                    "vehicleClassCode": "${vehicleInfo.vehicleClassCode ?: ""}",
                    "engineDesc": "${vehicleInfo.engineDesc ?: ""}",
                    "vehicleFgwCode": "${vehicleInfo.vehicleFgwCode ?: ""}",
                    "vehicleAlias": "${vehicleInfo.vehicleAlias ?: ""}",
                    "insuranceCode": "${vehicleInfo.insuranceCode ?: ""}",
                    "batholith": "",
                    "bodyType": "${vehicleInfo.bodyType ?:""}",
                    "vehicleClassName": "${vehicleInfo.vehicleClassName ?:""}",
                    "groupCode": "${vehicleInfo.groupCode ?:""}",
                    "vehicleFgwName": "${vehicleInfo.vehicleFgwName ?:""}",
                    "factoryName": "${vehicleInfo.factoryName ?:""}",
                    "dumpTrailerFlag": "${vehicleInfo.dumpTrailerFlag ?:""}",
                    "antiTheft": "${vehicleInfo.antiTheft ?:""}",
                    "power": "${vehicleInfo.vehiclePower ?:""}",
                    "searchCode": "",
                    "tbHcarType": "${vehicleInfo.tbHcarType ?:""}",
                    "insuranceClass": "${vehicleInfo.insuranceClass ?:""}",
                    "bakString1": "",
                    "bakString2": "",
                    "bakString3": "",
                    "bakString4": "",
                    "bakString5": "",
                    "industryVehicleCode": "${vehicleInfo.industryVehicleCode ?:""}",
                    "brandCode": "${vehicleInfo.brandCode ?:""}",
                    "industryVehicleName": "${vehicleInfo.industryVehicleName ?:""}"
                },
                "PolicyBaseInfo": {
                    "provinceCode": "${context.provinceCode}",
                    "cityCode": "${context.cityCode}",
                    "branchCode": "${context.branchCode}",
                    "orgdeptCode": "${context.orgdeptCode}",
                    "commecialStartDate": "${commercialStartDate}",
                    "commecialEndDate": "${commercialEndDate}",
                    "applicationDate": "${applicationDate}",
                    "issueDate": "${issueDate}",
                    "otherSource": "",
                    "comprenhensivePrdCode": "${vehicleInfo.comprenhensivePrdCode ?:""}"
                },
                "Opportunity": {
                    "licenseOwner": "${auto.owner}",
                    "mobile": "${mobile}",
                    "opportunityId": "${opportunityId}"
                },
                "CoverageInfo": [
                    {
                        "coverageCode": "DamageLossCoverage",
                        "coverageName": "机动车损失保险",
                        "coverageAmount": "${vehicleInfo.price?vehicleInfo.price : vehicleInfo?.kindPrice}"
                    },
                    {
                        "coverageCode": "ThirdPartyLiabilityCoverage",
                        "coverageName": "机动车第三者责任保险",
                        "coverageAmount": "500000.00"
                    }
                ],
                "OrderInfo": {
                    "orderId": "${orderId}",
                    "orderNo": "${orderNo}"
                },
                "priceMap": {
                    "registerDate": "${enrollDate}",
                    "startDate": "${startDate}",
                    "price": "${vehicleInfo.price ?: vehicleInfo.purchasePrice}",
                    "taxPrice": "${vehicleInfo.taxPrice}",
                    "kindPrice": "${vehicleInfo.taxKindPrice ?:""}",
                    "taxKindPrice": "${vehicleInfo.taxKindPrice ?:""}"
                },
                "carMark": "",
                "confirmPlateNo": "",
                "random": "${vehicleInfo.random ?:context.baseInfoResult?.random}",
                "marketDate": "${vehicleInfo.marketYear}",
                "insurancePageStatus": "${vehicleInfo.insurancePageStatus ?:context.insurancePageStatus}",
                "vehicleCode": "${vehicleInfo.rVehicleFamily}",
                "userId": "",
                "ocrUsedFlag": "",
                "plateNoForUpdate": "",
                "licenseOwnerForUpdate": "",
                "questionAnswer": "${context.validationCodeFlag ? (context.questionAnswer ?:travelTaxInfo.calcTaxAnswer) : ""}",
                "tbRandom": "${context.initVehicleBaseInfo.tbRandom}"
            }
        """
        Pattern p = Pattern.compile("\\s{2,}|\t|\r|\n");
        Matcher m = p.matcher(bodyContentStr);
        m.replaceAll("");
    }

    static final _SUBMIT_VEHICLE_DETAIL_INFO_RPG_DEFAULT = { context ->
        def stepDefault = _SUBMIT_VEHICLE_DETAIL_INFO_1
        def step2 = _SUBMIT_VEHICLE_DETAIL_INFO_2
        def stepId = context.submitVehicleDetailInfoStepFactor
        stepId != 2 ? stepDefault(context, stepId) : step2(context)
    }

    static final _CALC_PREMIUM_NEW = { context ->
        def bsEndDateText = context.dateInfo?.bsEndDateText
        if (bsEndDateText) {
            bsEndDateText = _DATETIME_FORMAT_NEW.format(todate(_DATETIME_FORMAT_NEW.parse(bsEndDateText)).plusDays(1))
        }
        context.planNo = 1
        def bodyParams = [
            calcTaxAnswer           : context.validationCodeFlag ? (context.questionAnswer ?: context.travelTaxInfo.calcTaxAnswer) : context.bsCaptchaText,
            random                  : context.baseInfoResult.random,
            orderNo                 : context.orderNo,
            planNo                  : context.planNo,
            CoverageInfo            : context.coverageInfo,
            InsuredIdentifyType     : '022001',
            InsuredIdentifyNo       : context.auto.identity,
            commecialEndDateAnswer  : bsEndDateText,
            commecialStartDateAnswer: context.dateInfo?.bsStartDateText,
            VehicleInfo             : [
                glassManufacturer: context.glassManufacturer,
                driveArea        : context.vehicleInfo.driveArea,
                isDoublePolicy   : '',
                moldName         : context.vehicleInfo.moldName ?: ''
            ],
            PolicyBaseInfo          : [
                cusotmerLoyalty: context.preRules.cusotmerLoyalty
            ],
            renewalFlag             : context.renewable ? 'renewal' : 'non_renewal'
        ]

        (context.glassManufacturer ?
            bodyParams + [glassManufacturer: context.glassManufacturer]
            : bodyParams) + [tbRandom: context.initVehicleBaseInfo.tbRandom]
    }

    static final _CALC_TRAVEL_TAX_V3_BASE = { payTaxStatus = 0, context ->
        def travelTaxInfo = context.travelTaxInfo
        travelTaxInfo.calcTaxAnswer = travelTaxInfo.calcTaxAnswer ?:""
        def month = today().getMonthValue() as String
        def year = today().getYear()
        def taxTerm = "$year${month.length() == 1 ? "0$month": "$month"}"
        def taxVehicleType = travelTaxInfo.taxVehicleType
        def certificatesType = "${travelTaxInfo.certificatesType ?:"022001"}"
        def cardNum = context.auto.identity
        def (bzStartDateText, bzEndDateText) = getCompulsoryInsurancePeriodTexts(context)

        if (context.travelTaxInfo.payTaxStatus == '1') {
            payTaxStatus = '0'
            if (context.travelTaxInfo.taxType == "C" ||
                context.travelTaxInfo.taxType == '4'){
                payTaxStatus = '1'
            } else if (context.travelTaxInfo.taxType == '2' ||
                context.travelTaxInfo.taxType == 'E' ||
                context.travelTaxInfo.taxType == 'M'){
                payTaxStatus = '2'
            }
        } else {
            payTaxStatus = '4'
        }
        def bodyContent = [
            random        : travelTaxInfo.random ?: context.baseInfoResult.random,
            orderNo       : context.orderNo,
            calcTaxAnswer : context.validationCodeFlag ? (context.questionAnswer ?: travelTaxInfo.calcTaxAnswer) : context.bzCaptchaText,
            VehicleInfo   : [
                purchaseinvoicesDate: '',
                moldName            : context.vehicleInfo.moldName ?: ''
            ],
            ComplusoryInfo: [
                complusoryStartDate: bzStartDateText + ' 00:00:00',
                complusoryEndDate  : bzEndDateText + ' 24:00:00'
            ],
            VehicleTax    : [
                payTaxStatus          : payTaxStatus,
                taxTerm               : taxTerm,
                taxpayerName          : context.auto.owner,
                taxVehicleType        : taxVehicleType ?: 'K01',
                fuelType              : travelTaxInfo.fuelType ?: '0',
                certificatesType      : certificatesType,
                cardNum               : cardNum,
                taxStartDate          : bzStartDateText,
                taxEndDate            : bzEndDateText,
                deductionDueType      : travelTaxInfo.deductionDueType ?: '',
                deductionDueCode      : travelTaxInfo.deductionDueCode ?: '',
                deductionDueProportion: travelTaxInfo.deductionDueProportion ?: '',
                derateNo              : travelTaxInfo.derateNo ?: '',
                sex                   : travelTaxInfo.sex ?: '',
                registryNumber        : context.registryNumber ?: ''
            ],
            telsal        : '',
            tbRandom      : context.initVehicleBaseInfo.tbRandom
        ]
        new JsonBuilder(bodyContent).toString()
    }

    static final _CALC_TRAVEL_TAX_V3_RPG_DEFAULT = _CALC_TRAVEL_TAX_V3_BASE


    /**********************************************************************************************/

    private static final _TAX_VEHICLE_TYPE_MAP_310000 = [
        { it <= 1.0 } : 'K01',
        { it <= 1.6 } : 'K02',
        { it <= 2.0 } : 'K03',
        { it <= 2.5 } : 'K04',
        { it <= 3.0 } : 'K05',
        { it <= 4.0 } : 'K06',
        { it > 4.0 }  : 'K07'
    ]

    private static final _INIT_TRAVEL_TAX_V3_PRE_ACTION_310000 = { resp, context ->
        def engineCapacity = context.vehicleInfo.engineCapacity ?: context.vehicleDetailInfo?.VehicleInfo?.engineCapacity
        context.travelTaxInfo.taxVehicleType = _TAX_VEHICLE_TYPE_MAP_310000.findResult { entry ->
            def checker = entry.key
            checker((engineCapacity ?: 0) as double) ? entry.value : null
        } ?: 'K01'
    }

    private static final _INIT_TRAVEL_TAX_V3_RH_BASE = { preAction = { _resp, _context -> }, resp, context ->
        if (resp.orderNo) {
            preAction(resp, context)
            def commercialStartDate = resp.PolicyBaseInfo?.commecialStartDate
            if (commercialStartDate) {
                setCommercialInsurancePeriodTexts context, commercialStartDate - ' 00:00:00'
            }
            log.info '初始化交强险报价表单成功'
            getLoopBreakFSRV resp
        } else {
            log.error '初始化交强险报价表单失败，请稍候重试'
            getFatalErrorFSRV '初始化交强险报价表单失败，请稍候重试'
        }
    }

    static final _INIT_TRAVEL_TAX_V3_RH_DEFAULT = _INIT_TRAVEL_TAX_V3_RH_BASE
    static final _INIT_TRAVEL_TAX_V3_RH_310000 = _INIT_TRAVEL_TAX_V3_RH_BASE.curry _INIT_TRAVEL_TAX_V3_PRE_ACTION_310000

    static final _INIT_VEHICLE_DETAIL_INFO_V3_RH_DEFAULT = { resp, context ->
        getLoopBreakFSRV true
    }

    static final _INIT_VEHICLE_DETAIL_INFO_V3_RH_NO_MODEL_CODE = { resp, context ->
        if (!context.vehicleInfo?.vehicleModel) {
            // 如果没有得到初始化车辆信息，需要通过品牌型号查询车辆信息，没有填写品牌型号信息需要补充填写
            if (!context.auto.autoType?.code) {
                log.info '需补充品牌型号'
                getNeedSupplementInfoFSRV { [_SUPPLEMENT_INFO_BRAND_CODE_TEMPLATE_QUOTING ] }
            } else {
                getLoopBreakFSRV true
            }
        } else {
            getLoopBreakFSRV false
        }
    }
}
