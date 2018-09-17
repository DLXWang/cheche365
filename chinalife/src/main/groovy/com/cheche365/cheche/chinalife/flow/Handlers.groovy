package com.cheche365.cheche.chinalife.flow

import com.cheche365.cheche.core.model.Auto
import groovy.util.logging.Slf4j

import static com.cheche365.cheche.chinalife.flow.Constants._UPDATE_CONTEXT_FIND_CAR_MODEL_INFO
import static com.cheche365.cheche.chinalife.util.BusinessUtils.createOldProposalQuoteItemParam
import static com.cheche365.cheche.chinalife.util.BusinessUtils.getAutoEngineNo
import static com.cheche365.cheche.chinalife.util.BusinessUtils.getAutoVinNo
import static com.cheche365.cheche.chinalife.util.BusinessUtils.getBirthdayById
import static com.cheche365.cheche.chinalife.util.BusinessUtils.getCarEnrollDate
import static com.cheche365.cheche.chinalife.util.BusinessUtils.getCarKindCode
import static com.cheche365.cheche.chinalife.util.BusinessUtils.getCarOwner
import static com.cheche365.cheche.chinalife.util.BusinessUtils.getCarSeat
import static com.cheche365.cheche.chinalife.util.BusinessUtils.getDefaultPublishDate
import static com.cheche365.cheche.chinalife.util.BusinessUtils.getOldCustomerFlag
import static com.cheche365.cheche.common.util.ContactUtils.getGenderByIdentity
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.parser.Constants._DATE_FORMAT3
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_ENGINE_NO_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_ENROLL_DATE_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_LICENSE_PLATE_NO_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_OWNER_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_VIN_NO_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._DATETIME_FORMAT3
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_AUTO_TYPE_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.util.BusinessUtils.getCommercialInsurancePeriodTexts
import static com.cheche365.cheche.parser.util.BusinessUtils.getCompulsoryInsurancePeriodTexts
import static com.cheche365.cheche.parser.util.BusinessUtils.getSelectedCarModelFSRV
import static com.cheche365.flow.core.util.FlowUtils.getProvideValuableHintsFSRV



/**
 * 请求生成器（RPG）和响应处理器（RH）
 */
@Slf4j
class Handlers {

    //获取车型 BASE
    static final _FIND_CAR_MODEL_INFO_BASE = { context, carBrandName, carBrandCode ->
        def deptId = context.deptId
        Auto auto = context.auto
        def publishDate = getDefaultPublishDate context
        def (defaultStartDateText, defaultEndDateText) = getCommercialInsurancePeriodTexts(context, _DATETIME_FORMAT3, false)
        def carEnrollDate = getCarEnrollDate context
        def carKindCode = getCarKindCode context
        def oldCustomerFlag = getOldCustomerFlag context
        def carOwner = getCarOwner context

        [
            'temporary.carVerify.newCarTaxFlag'                                            : 2,
            'temporary.quoteMain.geQuoteCars[0].brandName'                                 : carBrandName,
            'temporary.quoteMain.geQuoteCars[0].RBCode'                                    : carBrandCode,
            'temporary.proposalAreaCode'                                                   : deptId,
            'temporary.quoteMain.areaCode'                                                 : deptId,
            'temporary.quoteMain.geQuoteCars[0].carOwner'                                  : carOwner,
            'temporary.quoteMain.geQuoteCars[0].licenseNo'                                 : auto.licensePlateNo,
            'temporary.quoteMain.geQuoteCars[0].engineNo'                                  : getAutoEngineNo(context),
            'temporary.quoteMain.geQuoteCars[0].frameNo'                                   : getAutoVinNo(context),
            'temporary.quoteMain.geQuoteCars[0].publishDate'                               : publishDate,
            'temporary.quoteMain.geQuoteCars[0].enrollDate'                                : carEnrollDate,
            'temporary.quoteMain.geQuoteCars[0].vehicleStyle'                              : carKindCode,
            'temporary.quoteMain.geQuoteCars[0].carKindCode'                               : carKindCode,
            'temporary.quoteMain.geQuoteCars[0].unCarLicenceNoFlag'                        : '0',
            'temporary.quoteMain.geQuoteCars[0].newVehicleFlag'                            : '0',
            'temporary.quoteMain.geQuoteCars[0].carOwnerNature'                            : '7',
            'temporary.quoteMain.geQuoteCars[0].useNatureFlag'                             : '1',
            'temporary.quoteMain.geQuoteCars[0].ecdemicFlag'                               : '0',

            // 因为之前已经阻止了出租车报价（CheckLicensePlateNo），所以下面两个参数可以写死值
            'temporary.quoteMain.geQuoteCars[0].newCarTempTaxiFlag'                        : '0',
            // 无车辆查询选择了家庭自用，则赋值使用性质为“8A”
            'temporary.quoteMain.geQuoteCars[0].useNatureCode'                             : '8A',

            'temporary.quoteMain.geQuoteParties[0].partyFlag'                              : '1',
            'temporary.quoteMain.geQuoteParties[1].partyFlag'                              : '2',
            'temporary.quoteMain.geQuoteParties[2].partyFlag'                              : '3',
            'temporary.quoteMain.geQuoteParties[3].partyFlag'                              : '4',
            'temporary.quoteMain.geQuoteCars[0].obtainCarModelV2XFlag'                     : context.obtainCarModelV2XFlag, //如果是费改地区,第一次调用车型及纯风险保费信息查询接口
            'temporary.quoteMain.geQuoteParties[2].partyName'                              : carOwner,
            'temporary.isOldCustomer'                                                      : oldCustomerFlag,
            'temporary.quoteMain.geQuoteRisks[0].id.riskCode'                              : '0510',
            'temporary.quoteMain.geQuoteRisks[1].id.riskCode'                              : '0507',
            'temporary.quoteMain.geQuoteRisks[0].startDate'                                : defaultStartDateText,
            'temporary.quoteMain.geQuoteRisks[0].endDate'                                  : defaultEndDateText,
            'temporary.quoteMain.geQuoteRisks[1].startDate'                                : defaultStartDateText,
            'temporary.quoteMain.geClauseTypeVersion[0].geItemkindInfoList[25].mainKindStr': '215',
            'temporary.quoteMain.geClauseTypeVersion[0].riskCode'                          : '0511',
        ] + (context.transferFlag ? [
                'temporary.quoteMain.geQuoteCars[0].RBCode'   : '',
                'temporary.quoteMain.geQuoteCars[0].brandName': context.auto.autoType.code
        ] : [:])
    }

    static final _FIND_CAR_MODEL_INFO_BASE_TYPE2 = { context ->
        def deptId = context.deptId
        Auto auto = context.auto
        def publishDate = getDefaultPublishDate context
        def defaultStartDateText = getCommercialInsurancePeriodTexts(context, _DATETIME_FORMAT3, false).first
        def multiVehicleInfo = context.multiVehicleInfo
        def oldCustomerFlag = getOldCustomerFlag context
        def carOwner = getCarOwner context

        [
            'temporary.carVerify.newCarTaxFlag'                                            : 2,
            'temporary.proposalAreaCode'                                                   : deptId,
            'temporary.quoteMain.areaCode'                                                 : deptId,
            'temporary.quoteMain.geQuoteCars[0].carOwner'                                  : carOwner,
            'temporary.quoteMain.geQuoteCars[0].VINCode'                                   : auto.vinNo,
            'temporary.quoteMain.geQuoteCars[0].engineNo'                                  : auto.engineNo,
            'temporary.quoteMain.geQuoteCars[0].frameNo'                                   : auto.vinNo,
            'temporary.quoteMain.geQuoteCars[0].licenseNo'                                 : auto.licensePlateNo,
            'temporary.quoteMain.geQuoteCars[0].publishDate'                               : publishDate,
            'temporary.quoteMain.geQuoteCars[0].purchasePrice'                             : multiVehicleInfo.purchasePrice,
            'temporary.quoteMain.geQuoteCars[0].purchasePriceTax'                          : multiVehicleInfo.purchasePriceTax,
            'temporary.quoteMain.geQuoteCars[0].carKindCode'                               : multiVehicleInfo.vehicleClassCode,
            'temporary.quoteMain.geQuoteCars[0].exhaustScale'                              : multiVehicleInfo.engineDesc[0..-2],
            'temporary.quoteMain.geQuoteCars[0].brandName'                                 : multiVehicleInfo.brandName,
            'temporary.quoteMain.geQuoteCars[0].marketDate'                                : multiVehicleInfo.marketDate,
            'temporary.quoteMain.geQuoteCars[0].enrollDate'                                : _DATE_FORMAT3.format(auto.enrollDate ?: new java.util.Date()),
            'temporary.quoteMain.geQuoteCars[0].unCarLicenceNoFlag'                        : '0',
            'temporary.quoteMain.geQuoteCars[0].newVehicleFlag'                            : '0',
            'temporary.quoteMain.geQuoteCars[0].carOwnerNature'                            : '7',
            'temporary.quoteMain.geQuoteCars[0].useNatureFlag'                             : '1',
            'temporary.quoteMain.geQuoteCars[0].ecdemicFlag'                               : '0',
            'temporary.quoteMain.geQuoteCars[0].newCarTempTaxiFlag'                        : '0',
            'temporary.quoteMain.geQuoteCars[0].useNatureCode'                             : '8A',
            'temporary.quoteMain.geQuoteCars[0].obtainCarModelV2XFlag'                     : context.obtainCarModelV2XFlag,
            'temporary.quoteMain.geQuoteParties[0].partyFlag'                              : '1',
            'temporary.quoteMain.geQuoteParties[1].partyFlag'                              : '2',
            'temporary.quoteMain.geQuoteParties[2].partyFlag'                              : '3',
            'temporary.quoteMain.geQuoteParties[3].partyFlag'                              : '4',
            'temporary.quoteMain.geQuoteParties[2].partyName'                              : carOwner,
            'temporary.isOldCustomer'                                                      : oldCustomerFlag,
            'temporary.quoteMain.geQuoteRisks[0].id.riskCode'                              : '0510',
            'temporary.quoteMain.geQuoteRisks[1].id.riskCode'                              : '0507',
            'temporary.quoteMain.geQuoteRisks[0].startDate'                                : defaultStartDateText,
            'temporary.quoteMain.geQuoteRisks[1].startDate'                                : defaultStartDateText,
            'temporary.quoteMain.geClauseTypeVersion[0].geItemkindInfoList[25].mainKindStr': '215',
            'temporary.quoteMain.geClauseTypeVersion[0].riskCode'                          : '0511',
        ]
    }

    //获取车型 北京
    static final _FIND_CAR_MODEL_INFO_DEFAULT = { context ->
        def carBrandName = context.carBrandInfo?.brandName
        def carBrandCode = context.carBrandInfo?.RBCode
        _FIND_CAR_MODEL_INFO_BASE.call(context, carBrandName, carBrandCode)
    }

    //获取车型 广州深圳等手动输入的品牌型号
    static final _FIND_CAR_MODEL_INFO_WITH_AUTOTYPE = { context ->
        def carBrandName = context.carRenewalInfo?.brandName ?: context.auto?.autoType?.code
        def carBrandCode = context.carRenewalInfo?.RBCode ?: context.vehicleInfo?.rbCode ?: context.extendedAttributes?.autoModel
        _FIND_CAR_MODEL_INFO_BASE.call(context, carBrandName, carBrandCode)
    }

    //获取车型重庆手动输入的品牌型号,上送rbcode却有问题的
    static final _FIND_CAR_MODEL_INFO_WITHOUT_RBCODE = { context ->
        def carBrandName = context.auto?.autoType?.code
        _FIND_CAR_MODEL_INFO_BASE.call(context, carBrandName, '')
    }

    //获取车型 上海不需要品牌型号的
    static final _FIND_CAR_MODEL_INFO_WITHOUT_AUTOTYPE = { context ->
        _FIND_CAR_MODEL_INFO_BASE.call(context, '', '')
    }

    //获取基础套餐
    static final _BASE_PREMIUM_DEFAULT = { context ->
        def deptId = context.deptId
        def parentId = context.parentId
        Auto auto = context.auto
        def vehicleInfo = context.vehicleInfo
        def carEnrollDate = getCarEnrollDate context
        def publishDate = getDefaultPublishDate context
        def (startDateText, endDateText) = getCommercialInsurancePeriodTexts(context)
        def carInfo = context.carInfo
        def carSeat = getCarSeat context
        def oldCustomerFlag = getOldCustomerFlag context
        def carOwner = getCarOwner context
        def cityCodeParamsMapping = context.carVerify

        [
            'temporary.geProposalArea.deptID'                       : deptId,
            'temporary.geProposalArea.parentid'                     : parentId,
            'temporary.quoteMain.areaCode'                          : deptId,
            'temporary.isOldCustomer'                               : oldCustomerFlag,
            'temporary.carVerify.newCarAloneCarShipTaxCalculateFlag': '1',
            'temporary.quoteMain.geQuoteCars[0].RBCode'             : vehicleInfo.RBCode,
            'temporary.quoteMain.geQuoteCars[0].platmodelname'      : vehicleInfo.platmodelname,
            'temporary.quoteMain.geQuoteCars[0].purchasePriceTax'   : vehicleInfo.prichasePriceTax,
            'temporary.quoteMain.geQuoteCars[0].unCarLicenceNoFlag' : '0',
            'temporary.quoteMain.geQuoteCars[0].useNatureFlag'      : '1',
            'temporary.quoteMain.geQuoteCars[0].vehicleTonnage'     : vehicleInfo.vehicleTonnage,
            'temporary.quoteMain.geQuoteCars[0].wholeWeight'        : vehicleInfo.wholeWeight,
            'temporary.quoteMain.geQuoteCars[0].carOwner'           : carOwner,
            'temporary.quoteMain.geQuoteCars[0].engineNo'           : getAutoEngineNo(context),
            'temporary.quoteMain.geQuoteCars[0].frameNo'            : getAutoVinNo(context),
            'temporary.quoteMain.geQuoteCars[0].licenseNo'          : auto.licensePlateNo,
            'temporary.quoteMain.geQuoteCars[0].actualValue'        : carInfo.actualValue,
            'temporary.quoteMain.geQuoteCars[0].brandName'          : vehicleInfo.brandName,
            'temporary.quoteMain.geQuoteCars[0].enrollDate'         : carEnrollDate,
            'temporary.quoteMain.geQuoteCars[0].seatCount'          : carSeat,
            'temporary.quoteMain.geQuoteCars[0].modeCode'           : vehicleInfo.modeCode,
            'temporary.quoteMain.geQuoteCars[0].publishDate'        : publishDate,
            'temporary.quoteMain.geQuoteCars[0].exhaustScale'       : vehicleInfo.exhaustScale,
            'temporary.quoteMain.geQuoteCars[0].purchasePrice'      : vehicleInfo.purchasePrice,
            'temporary.quoteMain.geQuoteCars[0].userYear'           : carInfo.userYear,
            'temporary.quoteMain.geQuoteCars[0].useNatureCode'      : '8A',
            'temporary.quoteMain.geQuoteCars[0].ecdemicFlag'        : '0',
            'temporary.quoteMain.geQuoteParties[0].partyFlag'       : '1',
            'temporary.quoteMain.geQuoteParties[1].partyFlag'       : '2',
            'temporary.quoteMain.geQuoteRisks[0].id.riskCode'       : '0510',
            'temporary.quoteMain.geQuoteRisks[1].id.riskCode'       : '0507',
            'temporary.quoteMain.geQuoteRisks[0].startDate'         : startDateText,
            'temporary.quoteMain.geQuoteRisks[0].endDate'           : endDateText,
            'temporary.quoteMain.geQuoteRisks[1].startDate'         : startDateText,
            'temporary.quoteMain.geQuoteRisks[1].endDate'           : endDateText,
            'temporary.quoteMain.geQuoteParties[1].birthday'        : getBirthdayById(auto.identity),
            'temporary.quoteMain.geQuoteParties[1].partyName'       : carOwner,
            'temporary.quoteMain.geQuoteParties[2].partyFlag'       : '3',
            'temporary.quoteMain.geQuoteParties[2].partyName'       : carOwner,
            'temporary.quoteMain.geQuoteParties[3].partyFlag'       : '4',
            'temporary.quoteMain.geQuoteParties[1].gender'          : '1',
            'temporary.carVerify.UInewCarLimitBEFlagMessage'        : cityCodeParamsMapping.UInewCarLimitBEFlagMessage,
            'temporary.carVerify.UInewCarLimitBEFlag'               : cityCodeParamsMapping.UInewCarLimitBEFlag,
        ] + (context.clauseTypeParams ?: [:]) + (context.nonRenewalCaptcha ? [
            'temporary.quoteMain.busChangeCheckCodeFlag': '1',
            'temporary.quoteMain.busRenewalFlag'        : '1',
            'temporary.quoteMain.demandNo'              : context.bsDemandNo,
            'temporary.quoteMain.busCheckCode'          : context.captchaText
        ] : [:])
    }

    //重庆 获取基础套餐
    static final _BASE_PREMIUM_TYPE2 = { context ->
        def deptId = context.deptId
        def parentId = context.parentId
        Auto auto = context.auto
        def vehicleInfo = context.vehicleInfo
        def carEnrollDate = getCarEnrollDate context
        def publishDate = getDefaultPublishDate context
        def (startDateText, endDateText) = getCommercialInsurancePeriodTexts(context)
        def carInfo = context.carInfo
        def carSeat = getCarSeat context
        def clauseType = context.clauseType
        def carKindCode = getCarKindCode context
        def birthday = getBirthdayById auto.identity
        def cityCodeParamsMapping = context.carVerify
        def oldCustomerFlag = getOldCustomerFlag context
        def carOwner = getCarOwner context

        [
            'temporary.geProposalArea.deptID'                                          : deptId,
            'temporary.geProposalArea.parentid'                                        : parentId,
            'temporary.quoteMain.areaCode'                                             : deptId,
            'temporary.isOldCustomer'                                                  : oldCustomerFlag,
            'temporary.carVerify.newCarAloneCarShipTaxCalculateFlag'                   : '1',
            'temporary.pageFreeComboKindItem.mainKinds[0].id.kindCode'                 : 'A',
            'temporary.pageFreeComboKindItem.mainKinds[0].id.riskCode'                 : '0510',

            'temporary.quoteMain.geQuoteCars[0].carOwner'                              : carOwner,
            'temporary.quoteMain.geQuoteCars[0].engineNo'                              : getAutoEngineNo(context),
            'temporary.quoteMain.geQuoteCars[0].frameNo'                               : getAutoVinNo(context),
            'temporary.quoteMain.geQuoteCars[0].licenseNo'                             : auto.licensePlateNo,
            'temporary.quoteMain.geQuoteCars[0].actualValue'                           : carInfo.actualValue,
            'temporary.quoteMain.geQuoteCars[0].brandName'                             : vehicleInfo.brandName,
            'temporary.quoteMain.geQuoteCars[0].enrollDate'                            : carEnrollDate,
            'temporary.quoteMain.geQuoteCars[0].seatCount'                             : carSeat,
            'temporary.quoteMain.geQuoteCars[0].modeCode'                              : vehicleInfo.modeCode,
            'temporary.quoteMain.geQuoteCars[0].publishDate'                           : publishDate,
            'temporary.quoteMain.geQuoteCars[0].exhaustScale'                          : vehicleInfo.exhaustScale,
            'temporary.quoteMain.geQuoteCars[0].purchasePrice'                         : vehicleInfo.purchasePrice,
            'temporary.quoteMain.geQuoteCars[0].searchSequenceNo'                      : carInfo.searchSequenceNo,
            'temporary.quoteMain.geQuoteCars[0].userYear'                              : carInfo.userYear,
            'temporary.quoteMain.geQuoteCars[0].useNatureCode'                         : '8A',
            'temporary.quoteMain.geQuoteCars[0].ecdemicFlag'                           : '0',
            'temporary.quoteMain.geQuoteCars[0].runMiles'                              : '28000',
            'temporary.quoteMain.geQuoteParties[0].partyFlag'                          : '1',
            'temporary.quoteMain.geQuoteParties[1].partyFlag'                          : '2',
            'temporary.quoteMain.geQuoteRisks[0].id.riskCode'                          : '0510',
            'temporary.quoteMain.geQuoteRisks[1].id.riskCode'                          : '0507',
            'temporary.quoteMain.geQuoteRisks[0].startDate'                            : startDateText,
            'temporary.quoteMain.geQuoteRisks[0].endDate'                              : endDateText,
            'temporary.quoteMain.geQuoteRisks[1].startDate'                            : startDateText,
            'temporary.quoteMain.geQuoteRisks[1].endDate'                              : endDateText,
            'temporary.carVerify.UInewCarLimitBEFlagMessage'                           : cityCodeParamsMapping.UInewCarLimitBEFlagMessage,
            'temporary.carVerify.UInewCarLimitBEFlag'                                  : cityCodeParamsMapping.UInewCarLimitBEFlag,

            'temporary.quoteMain.geClauseTypeVersion[0].geItemkindInfoList[0].kindCode': '001',
            'temporary.carVerify.insuredEqualsOwner'                                   : '0',
            'temporary.quoteMain.geClauseTypeVersion[0].clauseType'                    : clauseType,
            'temporary.quoteMain.geQuoteCars[0].clauseType'                            : clauseType,
            'temporary.quoteMain.geQuoteCars[0].RBCode'                                : vehicleInfo.RBCode,
            'temporary.quoteMain.geQuoteCars[0].carKindCode'                           : carKindCode,
            'temporary.quoteMain.geQuoteCars[0].platmodelname'                         : vehicleInfo.platmodelname,
            'temporary.quoteMain.geQuoteCars[0].vehicleTonnage'                        : vehicleInfo.vehicleTonnage,
            'temporary.quoteMain.geQuoteCars[0].wholeWeight'                           : vehicleInfo.wholeWeight,
            'temporary.quoteMain.geQuoteParties[1].birthday'                           : birthday,
            'temporary.quoteMain.geQuoteCars[0].runAreaCode'                           : '04',
            'temporary.quoteMain.geQuoteCars[0].unCarLicenceNoFlag'                    : '0'
        ] + (context.nonRenewalCaptcha ? [
            'temporary.quoteMain.busChangeCheckCodeFlag': '1',
            'temporary.quoteMain.busRenewalFlag'        : '1',
            'temporary.quoteMain.demandNo'              : context.bsDemandNo,
            'temporary.quoteMain.busCheckCode'          : context.captchaText
        ] : [:])
    }

    // 获取续保套餐BASE
    static final _RENEWAL_PREMIUM_DEFAULT = { context ->
        def deptId = context.deptId
        def parentId = context.parentId
        Auto auto = context.auto
        def vehicleInfo = context.vehicleInfo
        def carEnrollDate = getCarEnrollDate context
        def (startDateText, endDateText) = getCommercialInsurancePeriodTexts(context)
        def carInfo = context.carInfo
        def carSeat = getCarSeat context
        def carKindCode = getCarKindCode context
        def carOwner = getCarOwner context
        def cityCodeParamsMapping = context.carVerify
        def searchSequenceNo = context.carInfo?.searchSequenceNo
        def oldCustomerFlag = getOldCustomerFlag context
        def quoteItemParam = createOldProposalQuoteItemParam context.geQuoteItemkinds

        [
            'temporary.geProposalArea.parentid'                     : parentId,
            'temporary.quoteMain.areaCode'                          : deptId,
            'temporary.carVerify.newCarAloneCarShipTaxCalculateFlag': '1',
            'temporary.quoteMain.geQuoteCars[0].carOwner'           : carOwner,
            'temporary.quoteMain.geQuoteCars[0].engineNo'           : getAutoEngineNo(context),
            'temporary.quoteMain.geQuoteCars[0].frameNo'            : getAutoVinNo(context),
            'temporary.quoteMain.geQuoteCars[0].licenseNo'          : auto.licensePlateNo,
            'temporary.quoteMain.geQuoteCars[0].actualValue'        : carInfo.actualValue,
            'temporary.quoteMain.geQuoteCars[0].brandName'          : vehicleInfo.brandName,
            'temporary.quoteMain.geQuoteCars[0].enrollDate'         : carEnrollDate,
            'temporary.quoteMain.geQuoteCars[0].seatCount'          : carSeat,
            'temporary.quoteMain.geQuoteCars[0].exhaustScale'       : vehicleInfo.exhaustScale,
            'temporary.quoteMain.geQuoteCars[0].purchasePrice'      : vehicleInfo.purchasePrice,
            'temporary.quoteMain.geQuoteCars[0].userYear'           : carInfo.userYear,
            'temporary.quoteMain.geQuoteCars[0].useNatureCode'      : '8A',
            'temporary.quoteMain.geQuoteCars[0].ecdemicFlag'        : '0',
            'temporary.quoteMain.geQuoteCars[0].runMiles'           : '28000',
            'temporary.quoteMain.geQuoteCars[0].publishDate'        : carEnrollDate,
            'temporary.quoteMain.geQuoteCars[0].searchSequenceNo'   : searchSequenceNo,
            'temporary.quoteMain.geQuoteParties[0].partyFlag'       : '1',
            'temporary.quoteMain.geQuoteParties[1].partyFlag'       : '2',
            'temporary.quoteMain.geQuoteParties[1].partyName'       : auto.owner,
            'temporary.quoteMain.geQuoteRisks[0].id.riskCode'       : '0510',
            'temporary.quoteMain.geQuoteRisks[1].id.riskCode'       : '0507',
            'temporary.quoteMain.geQuoteRisks[0].startDate'         : startDateText,
            'temporary.quoteMain.geQuoteRisks[0].endDate'           : endDateText,
            'temporary.quoteMain.geQuoteRisks[1].startDate'         : startDateText,
            'temporary.quoteMain.geQuoteRisks[1].endDate'           : endDateText,
            'temporary.quoteMain.geQuoteCars[0].carKindCode'        : carKindCode,
            'temporary.quoteMain.geQuoteCars[0].vehicleTonnage'     : vehicleInfo.vehicleTonnage,
            'temporary.quoteMain.riskCode'                          : context.riskCode ?: '0501',
            'temporary.isOldCustomer'                               : oldCustomerFlag,
            'temporary.carVerify.UInewCarDeductKindsFlag'           : cityCodeParamsMapping.UInewCarDeductKindsFlag,
            'temporary.carVerify.newCarTaxFlag'                     : cityCodeParamsMapping.newCarTaxFlag,//焦作需要
            'temporary.oldOriginalRiskCode'                         : context.oldOriginalRiskCode//焦作需要
        ] + quoteItemParam
    }

    // 获取续保套餐 济南/郑州
    static final _RENEWAL_PREMIUM_TYPE2 = { context ->
        def vehicleInfo = context.vehicleInfo
        def birthday = getBirthdayById context.auto.identity

        _RENEWAL_PREMIUM_DEFAULT.call(context) + [
            'temporary.quoteMain.geClauseTypeVersion[0].clauseType': context.clauseType,
            'temporary.quoteMain.geQuoteCars[0].modeCode'          : vehicleInfo.modeCode,
            'temporary.quoteMain.geQuoteCars[0].platmodelname'     : vehicleInfo.platmodelname,
            'temporary.quoteMain.geQuoteCars[0].RBCode'            : vehicleInfo.RBCode,
            'temporary.quoteMain.geQuoteCars[0].unCarLicenceNoFlag': '0',
            'temporary.quoteMain.geQuoteCars[0].wholeWeight'       : vehicleInfo.wholeWeight,
            'temporary.quoteMain.geQuoteParties[1].birthday'       : birthday
        ] + _ITEM_KIND_INFO_LIST_PARAMS
    }

    //计算套餐 重庆
    static final _CUSTOM_PREMIUM_TYPE2 = { context ->
        def deptId = context.deptId
        def parentId = context.parentId
        Auto auto = context.auto
        def vehicleInfo = context.vehicleInfo
        def carEnrollDate = getCarEnrollDate context
        def defaultPublishDate = getDefaultPublishDate context
        def (startDateText, endDateText) = getCommercialInsurancePeriodTexts(context)
        def carInfo = context.carInfo
        def carSeat = getCarSeat context
        def clauseTypeParams = context.clauseTypeParams
        def carKindCode = getCarKindCode context
        def birthday = getBirthdayById auto.identity
        def oldCustomerFlag = getOldCustomerFlag context
        def carOwner = getCarOwner context
        def gender = getGenderByIdentity(context.auto.identity)
        def pageFreeComboKindItem = context.pageFreeComboKindItem
        def clauseType = context.clauseType

        [
            'temporary.geProposalArea.deptID'                         : deptId,
            'temporary.geProposalArea.parentid'                       : parentId,
            'temporary.quoteMain.areaCode'                            : deptId,
            'temporary.isOldCustomer'                                 : oldCustomerFlag,
            'temporary.carVerify.newCarAloneCarShipTaxCalculateFlag'  : '1',

            'temporary.quoteMain.geQuoteCars[0].carOwner'             : carOwner,
            'temporary.quoteMain.geQuoteCars[0].engineNo'             : getAutoEngineNo(context),
            'temporary.quoteMain.geQuoteCars[0].frameNo'              : getAutoVinNo(context),
            'temporary.quoteMain.geQuoteCars[0].licenseNo'            : auto.licensePlateNo,
            'temporary.quoteMain.geQuoteCars[0].actualValue'          : carInfo.actualValue,
            'temporary.quoteMain.geQuoteCars[0].brandName'            : vehicleInfo.brandName,
            'temporary.quoteMain.geQuoteCars[0].enrollDate'           : carEnrollDate,
            'temporary.quoteMain.geQuoteCars[0].seatCount'            : carSeat,
            'temporary.quoteMain.geQuoteCars[0].modeCode'             : vehicleInfo.modeCode,
            'temporary.quoteMain.geQuoteCars[0].publishDate'          : defaultPublishDate,
            'temporary.quoteMain.geQuoteCars[0].exhaustScale'         : vehicleInfo.exhaustScale,
            'temporary.quoteMain.geQuoteCars[0].purchasePrice'        : vehicleInfo.purchasePrice,
            'temporary.quoteMain.geQuoteCars[0].searchSequenceNo'     : carInfo.searchSequenceNo,
            'temporary.quoteMain.geQuoteCars[0].userYear'             : carInfo.userYear,
            'temporary.quoteMain.geQuoteCars[0].useNatureCode'        : '8A',
            'temporary.quoteMain.geQuoteCars[0].ecdemicFlag'          : '0',
            'temporary.quoteMain.geQuoteParties[1].partyFlag'         : '2',
            'temporary.quoteMain.geQuoteRisks[0].id.riskCode'         : '0510',
            'temporary.quoteMain.geQuoteRisks[1].id.riskCode'         : '0507',
            'temporary.quoteMain.geQuoteRisks[0].startDate'           : startDateText,
            'temporary.quoteMain.geQuoteRisks[0].endDate'             : endDateText,
            'temporary.quoteMain.geQuoteRisks[1].startDate'           : startDateText,
            'temporary.quoteMain.geQuoteRisks[1].endDate'             : endDateText,

            'temporary.carVerify.insuredEqualsOwner'                  : '0',
            'temporary.quoteMain.geQuoteCars[0].unCarLicenceNoFlag'   : '0',
            'temporary.quoteMain.geQuoteCars[0].RBCode'               : vehicleInfo.RBCode,
            'temporary.quoteMain.geQuoteCars[0].carKindCode'          : carKindCode,
            'temporary.quoteMain.geQuoteCars[0].platmodelname'        : vehicleInfo.platmodelname,
            'temporary.quoteMain.geQuoteCars[0].vehicleTonnage'       : vehicleInfo.vehicleTonnage,
            'temporary.quoteMain.geQuoteCars[0].wholeWeight'          : vehicleInfo.wholeWeight,
            'temporary.quoteMain.geQuoteParties[1].birthday'          : birthday,
            'temporary.quoteMain.geClauseTypeVersion[0].riskCode'     : '0511',
            'temporary.quoteMain.geQuoteParties[1].gender'            : gender,

            'temporary.quoteMain.geClauseTypeVersion[0].clauseType'   : clauseType,
            'temporary.quoteMain.geQuoteCars[0].clauseType'           : clauseType,

            'temporary.resultCode'                                    : '3',
            'temporary.resultType'                                    : '0',
            'temporary.quoteMain.geQuoteCars[0].obtainCarModelV2XFlag': context.obtainCarModelV2XFlag
        ] + clauseTypeParams + pageFreeComboKindItem  + (context.nonRenewalCaptcha ? [
            'temporary.quoteMain.busChangeCheckCodeFlag': '1',
            'temporary.quoteMain.busRenewalFlag'        : '1',
            'temporary.quoteMain.demandNo'              : context.bsDemandNo,
            'temporary.quoteMain.busCheckCode'          : context.captchaText
        ] : [:])
    }

    //计算交强险 默认
    static final _BZ_PREMIUM_DEFAULT = { context ->
        def deptId = context.deptId
        def parentId = context.parentId
        Auto auto = context.auto
        def carInfo = context.carInfo
        def vehicleInfo = context.vehicleInfo
        def carEnrollDate = getCarEnrollDate context
        def publishDate = getDefaultPublishDate context
        def (startDateText, endDateText) = getCompulsoryInsurancePeriodTexts(context, _DATETIME_FORMAT3, false)
        def carSeat = getCarSeat context
        def oldCustomerFlag = getOldCustomerFlag context
        def carOwner = getCarOwner context

        [
            'temporary.geProposalArea.deptID'                      : deptId,
            'temporary.geProposalArea.parentid'                    : parentId,
            'temporary.quoteMain.areaCode'                         : deptId,
            'temporary.carVerify.newCarCalculateFlag'              : '0',
            'temporary.isOldCustomer'                              : oldCustomerFlag,
            'temporary.quoteMain.geQuoteCars[0].licenseNo'         : auto.licensePlateNo,
            'temporary.quoteMain.geQuoteCars[0].engineNo'          : getAutoEngineNo(context),
            'temporary.quoteMain.geQuoteCars[0].frameNo'           : getAutoVinNo(context),
            'temporary.quoteMain.geQuoteCars[0].carOwner'          : carOwner,
            'temporary.quoteMain.geQuoteCars[0].enrollDate'        : carEnrollDate,
            'temporary.quoteMain.geQuoteCars[0].publishDate'       : publishDate,
            'temporary.quoteMain.geQuoteCars[0].searchSequenceNo'  : carInfo.searchSequenceNo,
            'temporary.quoteMain.geQuoteCars[0].actualValue'       : vehicleInfo.actualValue,
            'temporary.quoteMain.geQuoteCars[0].brandName'         : vehicleInfo.brandName,
            'temporary.quoteMain.geQuoteCars[0].exhaustScale'      : vehicleInfo.exhaustScale,
            'temporary.quoteMain.geQuoteCars[0].importFlag'        : vehicleInfo.importFlag,
            'temporary.quoteMain.geQuoteCars[0].modeCode'          : vehicleInfo.RBCode,
            'temporary.quoteMain.geQuoteCars[0].purchasePrice'     : vehicleInfo.purchasePrice,
            'temporary.quoteMain.geQuoteCars[0].seatCount'         : carSeat,
            'temporary.quoteMain.geQuoteCars[0].userYear'          : carInfo.userYear,
            'temporary.quoteMain.geQuoteCars[0].runMiles'          : '28000',
            'temporary.quoteMain.geQuoteCars[0].useNatureCode'     : '8A',
            'temporary.quoteMain.geQuoteCars[0].ecdemicFlag'       : '0',
            'temporary.quoteMain.geQuoteParties[1].partyFlag'      : '2',
            'temporary.quoteMain.geQuoteRisks[0].id.riskCode'      : '0510',
            'temporary.quoteMain.geQuoteRisks[1].id.riskCode'      : '0507',
            'temporary.quoteMain.geQuoteRisks[0].startDate'        : startDateText,
            'temporary.quoteMain.geQuoteRisks[0].endDate'          : endDateText,
            'temporary.quoteMain.geQuoteRisks[1].startDate'        : startDateText,
            'temporary.quoteMain.geQuoteRisks[1].endDate'          : endDateText,

            'temporary.geProposalArea.tryrunflag'                  : '1',
            'temporary.quoteMain.geQuoteCars[0].tonCount'          : '0',
            'temporary.quoteMain.geQuoteCars[0].transferFlag'      : '0',
            'temporary.quoteMain.geQuoteCars[0].transmissionType'  : '',
            'temporary.quoteMain.geQuoteCars[0].unCarLicenceNoFlag': '0',
            'temporary.quoteMain.geQuoteParties[0].partyFlag'      : '1',
            'temporary.quoteMain.geQuoteParties[1].partyName'      : carOwner,
            'temporary.quoteMain.geQuoteParties[2].partyName'      : carOwner
        ] + (context.nonRenewalCaptcha ? [
            'temporary.quoteMain.busChangeCheckCodeFlag': '1',
            'temporary.quoteMain.busCheckCode'          : context.captchaText,
            'temporary.quoteMain.busRenewalFlag'        : '1',
            'temporary.busChangeCheckCodeFlag'          : '1',
            'temporary.quoteMain.bzDemandNo'            : context.bzDemandNo
        ] : [:])
    }
    //计算交强险 重庆，南宁
    static final _BZ_PREMIUM_TYPE2 = { context ->
        def deptId = context.deptId
        def parentId = context.parentId
        Auto auto = context.auto
        def carInfo = context.carInfo
        def vehicleInfo = context.vehicleInfo
        def carEnrollDate = getCarEnrollDate context
        def publishDate = getDefaultPublishDate context
        def (startDateText, endDateText) = getCompulsoryInsurancePeriodTexts(context)
        def carSeat = getCarSeat context
        def birthday = getBirthdayById auto.identity
        def oldCustomerFlag = getOldCustomerFlag context
        def carOwner = getCarOwner context

        [
            'temporary.geProposalArea.deptID'                                          : deptId,
            'temporary.geProposalArea.parentid'                                        : parentId,
            'temporary.quoteMain.areaCode'                                             : deptId,
            'temporary.carVerify.newCarCalculateFlag'                                  : '0',
            'temporary.isOldCustomer'                                                  : oldCustomerFlag,
            'temporary.quoteMain.geQuoteCars[0].licenseNo'                             : auto.licensePlateNo,
            'temporary.quoteMain.geQuoteCars[0].engineNo'                              : getAutoEngineNo(context),
            'temporary.quoteMain.geQuoteCars[0].frameNo'                               : getAutoVinNo(context),
            'temporary.quoteMain.geQuoteCars[0].carOwner'                              : carOwner,
            'temporary.quoteMain.geQuoteCars[0].enrollDate'                            : carEnrollDate,
            'temporary.quoteMain.geQuoteCars[0].publishDate'                           : publishDate,
            'temporary.quoteMain.geQuoteCars[0].searchSequenceNo'                      : carInfo.searchSequenceNo,
            'temporary.quoteMain.geQuoteCars[0].actualValue'                           : vehicleInfo.actualValue,
            'temporary.quoteMain.geQuoteCars[0].brandName'                             : vehicleInfo.brandName,
            'temporary.quoteMain.geQuoteCars[0].exhaustScale'                          : vehicleInfo.exhaustScale,
            'temporary.quoteMain.geQuoteCars[0].importFlag'                            : vehicleInfo.importFlag,
            'temporary.quoteMain.geQuoteCars[0].modeCode'                              : vehicleInfo.modeCode,
            'temporary.quoteMain.geQuoteCars[0].purchasePrice'                         : vehicleInfo.purchasePrice,
            'temporary.quoteMain.geQuoteCars[0].seatCount'                             : carSeat,
            'temporary.quoteMain.geQuoteCars[0].userYear'                              : carInfo.userYear,
            'temporary.quoteMain.geQuoteCars[0].runMiles'                              : '28000',
            'temporary.quoteMain.geQuoteCars[0].useNatureCode'                         : '8A',
            'temporary.quoteMain.geQuoteCars[0].ecdemicFlag'                           : '0',
            'temporary.quoteMain.geQuoteParties[1].partyFlag'                          : '2',
            'temporary.quoteMain.geQuoteRisks[0].id.riskCode'                          : '0510',
            'temporary.quoteMain.geQuoteRisks[1].id.riskCode'                          : '0507',
            'temporary.quoteMain.geQuoteRisks[0].startDate'                            : startDateText,
            'temporary.quoteMain.geQuoteRisks[0].endDate'                              : endDateText,
            'temporary.quoteMain.geQuoteRisks[1].startDate'                            : startDateText,
            'temporary.quoteMain.geQuoteRisks[1].endDate'                              : endDateText,

            'temporary.geProposalArea.tryrunflag'                                      : '1',
            'temporary.quoteMain.geQuoteCars[0].tonCount'                              : '0',
            'temporary.quoteMain.geQuoteCars[0].transferFlag'                          : '0',
            'temporary.quoteMain.geQuoteCars[0].transmissionType'                      : '',
            'temporary.quoteMain.geQuoteCars[0].unCarLicenceNoFlag'                    : '0',
            'temporary.quoteMain.geQuoteParties[0].partyFlag'                          : '1',
            'temporary.quoteMain.geQuoteParties[1].partyName'                          : carOwner,
            'temporary.quoteMain.geQuoteParties[2].partyName'                          : carOwner,

            'temporary.quoteMain.geQuoteCars[0].RBCode'                                : vehicleInfo.RBCode,
            'temporary.quoteMain.geQuoteCars[0].platmodelname'                         : vehicleInfo.platmodelname,
            'temporary.quoteMain.geQuoteCars[0].wholeWeight'                           : vehicleInfo.wholeWeight,
            'temporary.quoteMain.geClauseTypeVersion[0].clauseType'                    : context.clauseType,
            'temporary.pageFreeComboKindItem.mainKinds[0].id.kindCode'                 : 'A',
            'temporary.pageFreeComboKindItem.mainKinds[1].id.kindCode'                 : 'B',
            'temporary.pageFreeComboKindItem.mainKinds[2].id.kindCode'                 : 'G',
            'temporary.pageFreeComboKindItem.mainKinds[3].id.kindCode'                 : 'D11',
            'temporary.pageFreeComboKindItem.mainKinds[4].id.kindCode'                 : 'D12',
            'temporary.quoteMain.geClauseTypeVersion[0].geItemkindInfoList[0].kindCode': '001',
            'temporary.quoteMain.geClauseTypeVersion[0].geItemkindInfoList[1].kindCode': '002',
            'temporary.quoteMain.geClauseTypeVersion[0].geItemkindInfoList[2].kindCode': '003',
            'temporary.quoteMain.geClauseTypeVersion[0].geItemkindInfoList[3].kindCode': '006',
            'temporary.quoteMain.geQuoteItemkinds[0].valuerange'                       : '',
            'temporary.quoteMain.geQuoteItemkinds[1].valuerange'                       : '',
            'temporary.quoteMain.geQuoteItemkinds[2].valuerange'                       : '',
            'temporary.quoteMain.geQuoteItemkinds[3].kindCode'                         : 'D12',
            'temporary.quoteMain.geQuoteItemkinds[3].kindName'                         : '车上人员责任险（乘客）',
            'temporary.quoteMain.geQuoteItemkinds[3].unitAmount'                       : '10000',
            'temporary.quoteMain.geQuoteParties[1].birthday'                           : birthday,
            //以默认起保时间交强险报价失败，需修改时间需要的参数
            'temporary.quoteMain.busRealationFlag'                                     : '1',

            'temporary.quoteMain.geQuoteCars[0].fuelType'                              : '0', //南宁需要
        ] + (context.nonRenewalCaptcha ? [
            'temporary.quoteMain.busChangeCheckCodeFlag': '1',
            'temporary.quoteMain.busCheckCode'          : context.captchaText,
            'temporary.quoteMain.busRenewalFlag'        : '1',
            'temporary.busChangeCheckCodeFlag'          : '1',
            'temporary.quoteMain.bzDemandNo'            : context.bzDemandNo
        ] : [:])
    }

    private static _ITEM_KIND_INFO_LIST_PARAMS = [
        'temporary.quoteMain.geClauseTypeVersion[0].geItemkindInfoList[0].kindCode' : '001',
        'temporary.quoteMain.geClauseTypeVersion[0].geItemkindInfoList[1].kindCode' : '002',
        'temporary.quoteMain.geClauseTypeVersion[0].geItemkindInfoList[2].kindCode' : '003',
        'temporary.quoteMain.geClauseTypeVersion[0].geItemkindInfoList[3].kindCode' : '006',
        'temporary.quoteMain.geClauseTypeVersion[0].geItemkindInfoList[4].kindCode' : '007',
        'temporary.quoteMain.geClauseTypeVersion[0].geItemkindInfoList[5].kindCode' : '201',
        'temporary.quoteMain.geClauseTypeVersion[0].geItemkindInfoList[6].kindCode' : '202',
        'temporary.quoteMain.geClauseTypeVersion[0].geItemkindInfoList[7].kindCode' : '203',
        'temporary.quoteMain.geClauseTypeVersion[0].geItemkindInfoList[8].kindCode' : '205',
        'temporary.quoteMain.geClauseTypeVersion[0].geItemkindInfoList[9].kindCode' : '206',
        'temporary.quoteMain.geClauseTypeVersion[0].geItemkindInfoList[10].kindCode': '207',
        'temporary.quoteMain.geClauseTypeVersion[0].geItemkindInfoList[11].kindCode': '210',
        'temporary.quoteMain.geClauseTypeVersion[0].geItemkindInfoList[12].kindCode': '211',
        'temporary.quoteMain.geClauseTypeVersion[0].geItemkindInfoList[13].kindCode': '301',
        'temporary.quoteMain.geClauseTypeVersion[0].geItemkindInfoList[14].kindCode': '208',
        'temporary.quoteMain.geClauseTypeVersion[0].geItemkindInfoList[15].kindCode': '215',
        'temporary.quoteMain.geClauseTypeVersion[0].geItemkindInfoList[16].kindCode': '302',
        'temporary.quoteMain.geClauseTypeVersion[0].geItemkindInfoList[17].kindCode': '303',
        'temporary.quoteMain.geClauseTypeVersion[0].geItemkindInfoList[18].kindCode': '305',
        'temporary.quoteMain.geClauseTypeVersion[0].geItemkindInfoList[19].kindCode': '306',
        'temporary.quoteMain.geClauseTypeVersion[0].geItemkindInfoList[20].kindCode': '307',
        'temporary.quoteMain.geClauseTypeVersion[0].geItemkindInfoList[21].kindCode': '308',
        'temporary.quoteMain.geClauseTypeVersion[0].geItemkindInfoList[22].kindCode': '309',
        'temporary.quoteMain.geClauseTypeVersion[0].geItemkindInfoList[23].kindCode': '310',
        'temporary.quoteMain.geClauseTypeVersion[0].geItemkindInfoList[24].kindCode': '311',
        'temporary.quoteMain.geClauseTypeVersion[0].geItemkindInfoList[25].kindCode': '315'
    ]

    static final _FIND_CAR_MODEL_INFO_RH_BASE = { route, result, context ->
        if (result.list) {
            getSelectedCarModelFSRV context, result.list, result, [updateContext: _UPDATE_CONTEXT_FIND_CAR_MODEL_INFO.curry(log)]
        } else if ('0' == result.resultType && route) {
            getContinueFSRV true
        } else {
            def hints = [
                _VALUABLE_HINT_LICENSE_PLATE_NO_TEMPLATE_QUOTING.with {
                    it.originalValue = context.auto.licensePlateNo
                    it
                },
                _VALUABLE_HINT_VIN_NO_TEMPLATE_QUOTING.with {
                    it.originalValue = context.auto.vinNo
                    it
                },
                _VALUABLE_HINT_ENGINE_NO_TEMPLATE_QUOTING.with {
                    it.originalValue = context.auto.engineNo
                    it
                },
                _VALUABLE_HINT_ENROLL_DATE_TEMPLATE_QUOTING.with {
                    it.originalValue = context.auto.enrollDate
                    it
                },
                _VALUABLE_HINT_OWNER_TEMPLATE_QUOTING.with {
                    it.originalValue = context.auto.owner
                    it
                },
                _VALUABLE_HINT_AUTO_TYPE_TEMPLATE_QUOTING.with {
                    it.originalValue = context.auto?.autoType?.code
                    it
                }
            ]
            getProvideValuableHintsFSRV { hints }
        }

    }

    static final _FIND_CAR_MODEL_INFO_RH_320100 = _FIND_CAR_MODEL_INFO_RH_BASE.curry(true)

    static final _FIND_CAR_MODEL_INFO_RH_DEFAULT = _FIND_CAR_MODEL_INFO_RH_BASE.curry(false)

}
