package com.cheche365.cheche.pinganuk.flow

import com.cheche365.cheche.core.model.Auto
import groovy.util.logging.Slf4j

import static com.cheche365.cheche.common.util.AreaUtils.getProvinceCode
import static com.cheche365.cheche.common.util.ContactUtils.getAgeByIdentity
import static com.cheche365.cheche.common.util.ContactUtils.getBirthdayByIdentity
import static com.cheche365.cheche.common.util.ContactUtils.getGenderByIdentity
import static com.cheche365.cheche.common.util.ContactUtils.getRandomEmail
import static com.cheche365.cheche.common.util.ContactUtils.randomMobile
import static com.cheche365.cheche.common.util.FlowUtils.getEnvProperty
import static com.cheche365.cheche.core.model.GlassType.Enum.DOMESTIC_1
import static com.cheche365.cheche.core.model.GlassType.Enum.IMPORT_2
import static com.cheche365.cheche.parser.Constants._DAMAGE
import static com.cheche365.cheche.parser.Constants._DAMAGE_IOP
import static com.cheche365.cheche.parser.Constants._DATE_FORMAT3
import static com.cheche365.cheche.parser.Constants._DRIVER_AMOUNT
import static com.cheche365.cheche.parser.Constants._DRIVER_AMOUNT_LIST
import static com.cheche365.cheche.parser.Constants._DRIVER_IOP
import static com.cheche365.cheche.parser.Constants._ENGINE
import static com.cheche365.cheche.parser.Constants._ENGINE_IOP
import static com.cheche365.cheche.parser.Constants._GLASS
import static com.cheche365.cheche.parser.Constants._PASSENGER_AMOUNT
import static com.cheche365.cheche.parser.Constants._PASSENGER_AMOUNT_LIST
import static com.cheche365.cheche.parser.Constants._PASSENGER_IOP
import static com.cheche365.cheche.parser.Constants._SCRATCH_AMOUNT
import static com.cheche365.cheche.parser.Constants._SCRATCH_AMOUNT_LIST
import static com.cheche365.cheche.parser.Constants._SCRATCH_IOP
import static com.cheche365.cheche.parser.Constants._SPONTANEOUS_LOSS
import static com.cheche365.cheche.parser.Constants._THEFT
import static com.cheche365.cheche.parser.Constants._THEFT_IOP
import static com.cheche365.cheche.parser.Constants._THIRD_PARTY_AMOUNT
import static com.cheche365.cheche.parser.Constants._THIRD_PARTY_AMOUNT_LIST
import static com.cheche365.cheche.parser.Constants._THIRD_PARTY_IOP
import static com.cheche365.cheche.parser.Constants.get_SPONTANEOUS_LOSS_IOP
import static com.cheche365.cheche.parser.Constants.get_UNABLE_FIND_THIRDPARTY
import static com.cheche365.cheche.parser.util.BusinessUtils.adjustInsureAmount
import static com.cheche365.cheche.parser.util.BusinessUtils.getQuoteKindItemParams
import static com.cheche365.cheche.parser.util.BusinessUtils.isCommercialQuoted
import static com.cheche365.cheche.parser.util.BusinessUtils.isCompulsoryOrAutoTaxQuoted
import static com.cheche365.cheche.pinganuk.util.BusinessUtils.getAllBaseKindItems
import static com.cheche365.cheche.pinganuk.util.BusinessUtils.getCommercialInsurancePeriodTexts
import static com.cheche365.cheche.pinganuk.util.BusinessUtils.getCompulsoryInsurancePeriodTexts



/**
 * 请求生成器（RPG）和响应处理器（RH）
 */
@Slf4j
class Handlers {


    static final _AMOUNT_CONVERTER_BOOLEAN = { context, insurancePackage, kindItem, propName, iopPropName, extConfig ->
        insurancePackage[propName]
    }

    static final _AMOUNT_CONVERTER_INSURE_AMOUNT = { context, insurancePackage, kindItem, propName, iopPropName, extConfig ->
        insurancePackage[propName] ? kindItem.amount : 0
    }

    static final _GLASS_CONVERTER_FROM_AMOUNT = { context, insurancePackage, kindItem, propName, iopPropName, extConfig ->
        insurancePackage.glass ? insurancePackage.glassType : null
    }

    static final _AMOUNT_CONVERTER_FROM_AMOUNT_LIST = { context, insurancePackage, kindItem, propName, iopPropName, extConfig ->
        def expectedAmount = insurancePackage[propName]
        def amountList = kindItem?.amountList?.reverse()

        expectedAmount ? (adjustInsureAmount(expectedAmount, amountList) ?: 0) : 0
    }

    static final _AMOUNT_CONVERTER_UNSUPPORTED = { context, insurancePackage, kindItem, propName, iopPropName, extConfig ->
        false
    }

    /*
     * 内部的保额转换成外部的请求(内转外)
     */
    static final _I2O_PREMIUM_CONVERTER = { context, outerKindCode, kindItem, result ->
        if (result) {
            def dutyList = [
                dutyCode     : outerKindCode,
                //将不计免赔等的true转为0
                insuredAmount: (result && result != true) ? result : 0
            ]

            if ('CV01001' == outerKindCode) { // 车损
                dutyList.pureRiskPremium = context.pureRiskPremium01 ?: 0
            }
            if ('CV17005' == outerKindCode) { // 乘客
                dutyList.seats = context.selectedCarModel.seats - 1
            }
            if ('CV08000' == outerKindCode) { // 玻璃
                dutyList.seats = DOMESTIC_1 == result ? 0 : 1
                dutyList.insuredAmount = 0
            }

            dutyList
        }
    }

    /*
     * 获取报价后，将返回的报价转换成内部的结果（外转内）
     * 返回值有四项：保额，报价，iop的报价，其他的信息(比如玻璃险的类型，获取乘客险的座位数)
     */
    static final _O2I_PREMIUM_CONVERTER = { context, innerKindCode, kindItem, amountName, premiumName, isIop,
                                            iopPremiumName, extConfig ->
        def other
        if (_GLASS == innerKindCode) {
            other = (0 == kindItem?.seats) ? DOMESTIC_1 : (1 == kindItem?.seats) ? IMPORT_2 : null
        }
        if (_PASSENGER_AMOUNT == innerKindCode) {
            other = kindItem?.seats
        }

        [
            kindItem?.amount,
            isIop ? null : kindItem?.premium,
            isIop ? kindItem?.premium : null,
            other
        ]
    }

    static final _KIND_ITEM_CONVERTERS_CONFIG = [
        ['CV01001', _DAMAGE, _AMOUNT_CONVERTER_INSURE_AMOUNT, null, _O2I_PREMIUM_CONVERTER, null], //机动车损失保险
        ['CV05002', _THIRD_PARTY_AMOUNT, _AMOUNT_CONVERTER_FROM_AMOUNT_LIST, _THIRD_PARTY_AMOUNT_LIST, _O2I_PREMIUM_CONVERTER, null],                                                                   //机动车第三者责任保险
        ['CV09003', _THEFT, _AMOUNT_CONVERTER_INSURE_AMOUNT, null, _O2I_PREMIUM_CONVERTER, null], //机动车盗抢保险
        ['CV13004', _DRIVER_AMOUNT, _AMOUNT_CONVERTER_FROM_AMOUNT_LIST, _DRIVER_AMOUNT_LIST, _O2I_PREMIUM_CONVERTER, null],//机动车车上人员责任保险（司机）
        ['CV17005', _PASSENGER_AMOUNT, _AMOUNT_CONVERTER_FROM_AMOUNT_LIST, _PASSENGER_AMOUNT_LIST, _O2I_PREMIUM_CONVERTER, null],             //机动车车上人员责任保险（乘客）
        ['CV08000', _GLASS, _GLASS_CONVERTER_FROM_AMOUNT, null, _O2I_PREMIUM_CONVERTER, null], //玻璃单独破碎险
        ['CV17000', _SCRATCH_AMOUNT, _AMOUNT_CONVERTER_FROM_AMOUNT_LIST, _SCRATCH_AMOUNT_LIST, _O2I_PREMIUM_CONVERTER, null],                                       //车身划痕损失险
        ['CV18000', _SPONTANEOUS_LOSS, _AMOUNT_CONVERTER_INSURE_AMOUNT, null, _O2I_PREMIUM_CONVERTER, null], //自燃损失险
        ['CV27027', _DAMAGE_IOP, _AMOUNT_CONVERTER_BOOLEAN, null, _O2I_PREMIUM_CONVERTER, null], //不计免赔特约条款（车损险）
        ['CV31028', _THIRD_PARTY_IOP, _AMOUNT_CONVERTER_BOOLEAN, null, _O2I_PREMIUM_CONVERTER, null], //不计免赔特约条款（三者险）
        ['CV36041', _ENGINE, _AMOUNT_CONVERTER_INSURE_AMOUNT, null, _O2I_PREMIUM_CONVERTER, null], //涉水发动机损坏险
        ['CV41048', _THEFT_IOP, _AMOUNT_CONVERTER_BOOLEAN, null, _O2I_PREMIUM_CONVERTER, null], //不计免赔特约条款（盗抢险）
        ['CV44049', _DRIVER_IOP, _AMOUNT_CONVERTER_BOOLEAN, null, _O2I_PREMIUM_CONVERTER, null], //不计免赔特约条款（车上人员司机）
        ['CV56075', _SCRATCH_IOP, _AMOUNT_CONVERTER_BOOLEAN, null, _O2I_PREMIUM_CONVERTER, null], //不计免赔特约条款（车身划痕险）
        ['CV58077', _SPONTANEOUS_LOSS_IOP, _AMOUNT_CONVERTER_BOOLEAN, null, _O2I_PREMIUM_CONVERTER, null], // 不计免赔特约条款(自燃损失险)
        ['CV60079', _ENGINE_IOP, _AMOUNT_CONVERTER_BOOLEAN, null, _O2I_PREMIUM_CONVERTER, null], //不计免赔特约条款（涉水发动机损坏险）
        ['CV44080', _PASSENGER_IOP, _AMOUNT_CONVERTER_BOOLEAN, null, _O2I_PREMIUM_CONVERTER, null], //不计免赔特约条款（车上人员乘客）
        ['CV49063', _UNABLE_FIND_THIRDPARTY, _AMOUNT_CONVERTER_BOOLEAN, null, _O2I_PREMIUM_CONVERTER, null], //车损险无法找到第三方
    ]

    static final _QUERY_AND_QUOTE_RPG_BASE = { config, context ->
        context.kindItemConvertersConfig = config

        def voucher = context.voucher,
            c01BaseInfo = voucher?.c01BaseInfo,
            c51BaseInfo = voucher?.c51BaseInfo,
            ownerDriver = voucher?.ownerDriver,
            vehicleTarget = voucher?.vehicleTarget,
            vehicleTaxInfo = voucher?.vehicleTaxInfo,
            selectedCarModel = context.selectedCarModel
        def fuelType = vehicleTaxInfo?.fuelType
        def baseInfo = context.baseInfo
        def departmentCode = baseInfo.departmentCode
        def rateFactorList = context.rateFactorList
        def vehicleData = context.vehicleDataList?.first()
        Auto auto = context.auto
        def identity = auto.identity

        def allBaseKindItems = getAllBaseKindItems context, config
        def c01DutyList = getQuoteKindItemParams context, allBaseKindItems, config, _I2O_PREMIUM_CONVERTER
        def (bsBeginTime, bsEndTime) = getCommercialInsurancePeriodTexts(context)
        def (bzBeginTime, bzEndTime) = getCompulsoryInsurancePeriodTexts(context)

        [
            mainQuotationNo   : context.mainQuotationNo,
            saleInfo          : baseInfo.subMap(['channelSourceDetailCode',
                                                 'departmentCode',
                                                 'businessSourceCode',
                                                 'businessSourceDetailCode',
                                                 'channelSourceCode',
                                                 'channelSourceDetailCode']) + [
                employeeInfoList: [[
                                       employeeCode: context?.baseInfo?.saleAgentCode,
                                   ]]
            ],
            aplylicantInfoList: [[personnelType: '1']],
            quotationList     : [
                [
                    applyQueryResult: [
                        circInfoDTO        : [
                            claimRecordList    : context.claimRecordList?.collect { claimRecord ->
                                claimRecord.subMap(['claimAmount'])
                            },
                            thirdVehicleInfoDTO: [
                                busilastyearenddate: context.busilastyearenddate,
                                nonclaimAdjust     : context.nonClaimAdjust // 非索赔调整
                            ]
                        ],
                        coveragePremiumList: [
                            [
                                dutyCode       : '01',
                                pureRiskPremium: context.pureRiskPremium01
                            ]
                        ]
                    ],
                    processType     : context.processType ?: '',
                    c51CircInfoDTO  : context.c51CircInfoDTO,
                    voucher         : [
                        c01DutyList             : c01DutyList,    //商业险套餐
                        thirdCarBusinessInfoDTO : [
                            cardNo: 0
                        ],
                        baseInfo                : [
                            departmentCode: departmentCode,
                            rateClassFlag : '14'
                        ],
                        c01BaseInfo             : (c01BaseInfo ? c01BaseInfo.subMap([
                            'shortTimeCoefficient',
                            'disputedSettleModeCode'
                        ]) : [
                            shortTimeCoefficient  : 1,
                            disputedSettleModeCode: 1
                        ]) + [
                            renewalTypeCode         : context.renewable ? '1' : '0', // 续保状态 TODO 官网还有专门步骤抓取续保状态
                            insuranceBeginTime      : bsBeginTime,
                            insuranceEndTime        : bsEndTime,
                            planCode                : isCommercialQuoted(context.accurateInsurancePackage) ? 'PL0100003' : '', // 是否投保商业险
                            brokerCode              : baseInfo.brokerCode,//影响保存报价单
                            departmentCode          : departmentCode,
                            rateClassFlag           : '14',
                            isReportElectronRelation: 'Y', // 核保需要
                            isaccommodation         : 'N', // 核保需要
                            isCalculateWithoutCirc  : 'N',
                            lastPolicyNo            : context.c01LastPolicyNo ?: '',
                            agentName               : baseInfo.userName,
                            agentCode               : context?.baseInfo?.agentCode,
                            agentAgreementNo        : baseInfo.authorization.conferNo,
                            supplementAgreementNo   : baseInfo.authorization.subConferNo
                        ],
                        c51BaseInfo             : (c51BaseInfo ? c51BaseInfo.subMap([
                            'shortTimeCoefficient',
                            'renewalTypeCode'
                        ]) : [
                            shortTimeCoefficient: 1,
                            renewalTypeCode     : '0'
                        ]) + [
                            agentCode               : context?.baseInfo?.agentCode,
                            insuranceBeginTime      : bzBeginTime,
                            insuranceEndTime        : bzEndTime,
                            planCode                : isCompulsoryOrAutoTaxQuoted(context.accurateInsurancePackage) ? 'PL0100C51' : '', // 是否投保交强和车船
                            brokerCode              : baseInfo.brokerCode,
                            departmentCode          : departmentCode,
                            rateClassFlag           : '14',
                            isReportElectronRelation: 'Y', // 核保需要
                            disputedSettleModeCode  : c01BaseInfo?.disputedSettleModeCode ?: 1,
                            lastPolicyNo            : c51BaseInfo?.policyNo ?: '',
                            agentName               : baseInfo.userName,
                            isCalculateWithoutCirc  : 'N',
                            formatType              : '04',//商业保单形式
                            agentAgreementNo        : baseInfo.authorization.conferNo,
                            supplementAgreementNo   : baseInfo.authorization.subConferNo
                        ],
                        c01DisplayRateFactorList: rateFactorList.collect { factor ->
                            factor.subMap(['factorCode', 'ratingTableNo'])
                        },
                        c51DisplayRateFactorList: [
                            [factorCode: 'F54'],
                            [factorCode: 'F55'],
                            [factorCode: 'F999']
                        ],
                        c01ExtendInfo           : [
                            ownerVehicleTypeCode      : voucher?.c01ExtendInfo?.ownerVehicleTypeCode ?: vehicleData?.ownerVehicleTypeCode ?: 'K33',
                            offerLastPolicyFlag       : 'N', // 核保需要
                            expectationUnderwriteLimit: '2',
                            samCode                   : getEnvProperty(context, 'pinganuk.samCode'),
                            commercialClaimRecord     : context.c01CommercialClaimRecord ?: '',
                            crossCommissionRate       : context.c01CrossCommissionRate ?: ''
                        ],
                        c51ExtendInfo           : [
                            ownerVehicleTypeCode      : voucher?.c51ExtendInfo?.ownerVehicleTypeCode ?: vehicleData?.ownerVehicleTypeCode ?: 'K33',
                            expectationUnderwriteLimit: '2'
                        ],
                        // 交强险
                        c51DutyList             : [
                            [
                                dutyCode     : 'CV39047', // 财产损失
                                insuredAmount: 2000
                            ],
                            [
                                dutyCode     : 'CV38046', // 医疗费用
                                insuredAmount: 10000
                            ],
                            [
                                dutyCode     : 'CV37045', // 死亡伤残
                                insuredAmount: 110000
                            ]
                        ],
                        insurantInfo            : [
                            personnelName: voucher?.insurantInfo?.personnelName ?: auto.owner,
                            personnelFlag: '1',// 1 表示客户类型为：个人客户
                        ],
                        ownerDriver             : (ownerDriver ? ownerDriver.subMap([
                            'certificateTypeCode',
                            'certificateTypeNo',
                            'personnelName',
                            'birthday',
                            'sexCode',
                            'age'
                        ]) : [
                            certificateTypeCode: '01',
                            certificateTypeNo  : identity,
                            personnelName      : auto.owner,
                            birthday           : _DATE_FORMAT3.format(getBirthdayByIdentity(identity)),
                            sexCode            : getGenderByIdentity(identity, ['F', 'M']),
                            age                : getAgeByIdentity(identity)
                        ]) + [
                            personnelFlag: '1',// 1 表示客户类型为：个人客户
                        ],
                        saleInfo                : baseInfo.subMap([
                            'businessSourceCode',
                            'businessSourceDetailCode',
                            'channelSourceCode',
                            'channelSourceDetailCode',
                            'saleAgentCode',
                            'departmentCode',
                            'saleGroupCode'
                        ]),

                        vehicleTarget           : selectedCarModel.subMap([
                            'autoModelCode',
                            'autoModelName',
                            'circAutoModelCode'
                        ]) + (vehicleTarget ? vehicleTarget.subMap([
                            'engineNo',
                            'firstRegisterDate',
                            'licenceTypeCode',
                            'modifyAutoModelName',
                            'ownershipAttributeCode',
                            'usageAttributeCode',
                            'vehicleFrameNo',
                            'vehicleLicenceCode',
                            'vehicleSeats',
                            'vehicleTypeCode',
                            'certificationDate',
                            'wholeWeight'
                        ]) : (vehicleData?.subMap([
                            'firstRegisterDate',
                            'licenceTypeCode',
                            'vehicleFrameNo',
                            'vehicleLicenceCode',
                            'vehicleSeats',
                            'certificationDate'
                        ]) + [
                            engineNo              : vehicleData?.engineno,
                            modifyAutoModelName   : selectedCarModel.autoModelName,
                            ownershipAttributeCode: '03',
                            usageAttributeCode    : '02',
                            vehicleTypeCode       : selectedCarModel.vehicleTypeNew
                        ])) + [
                            purchasePriceDefaultValue: selectedCarModel.purchasePrice, // 与玻璃报价有关，应为汽车价格
                            vehicleLossInsuredValue  : context.theftAmount,
                            changeOwnerFlag          : '0',
                            isMiniVehicle            : 'N', // 核保需要
                            isAbnormalCar            : '0' // 核保需要
                        ],
                        vehicleTaxInfo          : [
                            fuelType: fuelType && '0' != fuelType ? fuelType : 'A', // 燃料类型
                            taxType : vehicleTaxInfo?.taxType ?: 0 // 纳税类型
                        ],
                        extendInfo              : [
                            commercialClaimRecord: context.commercialClaimRecord // 商业索偿纪录
                        ]
                    ]
                ]
            ]
        ]
    }

    // 报价
    static final _QUERY_AND_QUOTE_RPG = _QUERY_AND_QUOTE_RPG_BASE.curry _KIND_ITEM_CONVERTERS_CONFIG

    // 核保
    static final _APPLY_POLICY_RPG = { context ->
        // 车主、被保险人、投保人信息
        Auto auto = context.auto
        def autoId = auto.identity
        def order = context.order
        def applicantId = order.applicantIdNo ?: autoId,
            applicantName = order.applicantName ?: auto.owner,
            applicantSex = getGenderByIdentity(applicantId, ['F', 'M']),
            applicantBirthday = _DATE_FORMAT3.format(getBirthdayByIdentity(applicantId)),
            applicantMobile = context.extendedAttributes?.verificationMobile ?: order.applicant?.mobile
        def insuredId = order.insuredIdNo ?: autoId,
            insuredName = order.insuredName ?: auto.owner
        def deliveryAddress = order.deliveryAddress,
            province = deliveryAddress?.province ?: getProvinceCode(context.area.id),
            city = deliveryAddress?.city ?: order.area?.id,
            county = deliveryAddress?.district ?: order.area?.id ?: deliveryAddress?.city,
            street = deliveryAddress?.street ?: '北京市东城区美术馆后街大取灯胡同2号院', //需要传入地址，不传会造成核保不通过
            postcode = '100000'    //北京

        def circInfoDTO = context.circInfoDTO,
            applyQueryCode = circInfoDTO.thirdVehicleInfoDTO.applyQueryCode ?: context.voucher?.c51DutyList?.applyQueryCode,
            stdVehicleInfoDTO = circInfoDTO.stdVehicleInfoDTO
        def autoTypeQueryCode = context.circVehicleTypeInfo?.autoTypeQueryCode
        def baseInfo = context.baseInfo
        def voucher = context.voucher,
            vehicleTarget = voucher.vehicleTarget,
            c01ExtendInfo = voucher.c01ExtendInfo,
            c01BaseInfo = voucher.c01BaseInfo,
            c51BaseInfo = voucher.c51BaseInfo

        [
            mainQuotationNo   : context.mainQuotationNo,
            isHideCommission  : 'N', //该值等于Y时，表示showCommission，对结果无影响
            saleInfo          : baseInfo.subMap([
                'departmentCode',
                'dealerCode',
                'businessSourceCode',
                'businessSourceDetailCode',
                'channelSourceCode',
                'channelSourceDetailCode',
                'saleGroupCode'
            ]) + [
                partnerWorknetName: baseInfo.userName,
                developFlg        : 'N',
                agentInfoList     : [
                    [
                        agentCode: baseInfo.agentCode,
                    ]
                ],
                employeeInfoList  : [
                    [
                        employeeCode: baseInfo.saleAgentCode,
                        employeeName: baseInfo.saleAgentName
                    ]
                ],
                partnerInfoList   : [
                    [
                        partnerType: baseInfo.partnerType,
                        partnerCode: baseInfo.partnerWorkNetCode
                    ]
                ]
            ],
            // 投保人相关信息
            aplylicantInfoList: [
                [
                    sexCode             : applicantSex,
                    birthday            : applicantBirthday,
                    certificateType     : '01',
                    certificateNo       : applicantId,
                    name                : applicantName,
                    personnelType       : '1', //voucher.applicantInfo.personnelFlag,
                    mobileTelephone     : applicantMobile,
                    postcode            : postcode,
                    email               : randomEmail,
                    nationality         : '156', // 国家（中国）
                    address             : street,
                    clientNo            : voucher.ownerDriver.clientNo, // ?: voucher.insurantInfo.clientNo,
                    nation              : '汉',                         //采集身份信息，写死
                    issuer              : '北京市东城区美术馆后街',
                    certificateAddress  : '北京市东城区美术馆后街大取灯胡同2号院',
                    certificateIssueDate: '2018-05-05',
                    certificateValidDate: '2028-05-05'
                ]
            ],
            quotationList     : [
                [
                    c01CircInfoDTO: [
                        stdVehicleInfoDTO  : stdVehicleInfoDTO.subMap([
                            'brandCode',
                            'brandName',
                            'carName',
                            'companyCode',
                            'companyName',
                            'confModel',
                            'familyCode',
                            'familyName',
                            'importCode',
                            'importName',
                            'modelCode',
                            'noticeType',
                            'typeCode',
                            'vehicleClass',
                            'vehicleMangline'
                        ]),
                        thirdVehicleInfoDTO: [
                            applyQueryCode   : applyQueryCode,
                            engineNo         : auto.engineNo,
                            vehicleFrameNo   : auto.vinNo,
                            autoTypeQueryCode: autoTypeQueryCode
                        ],
                    ],

                    c51CircInfoDTO: context.c51CircInfoDTO,

                    voucher       : [
                        quotationNo             : voucher.quotationNo,
                        ownerDriver             : voucher.ownerDriver.subMap([
                            'certificateTypeCode',
                            'sexCode',
                            'personnelName',
                            'certificateTypeNo',
                            'birthday',
                            'age',
                            'personnelFlag'
                        ]),
                        applicantInfo           : [
                            certificateTypeCode : '01',
                            certificateTypeNo   : applicantId,
                            personnelName       : applicantName,
                            birthday            : applicantBirthday,
                            sexCode             : applicantSex,
                            age                 : getAgeByIdentity(applicantId),
                            postCode            : postcode,
                            communicationAddress: street,
                            country             : '01',
                            province            : province,
                            city                : city,
                            county              : county,
                        ],
                        insurantInfo            : [
                            certificateTypeCode : '01',
                            certificateTypeNo   : insuredId,
                            personnelName       : insuredName,
                            postCode            : postcode,
                            country             : '01',
                            province            : province,
                            city                : city,
                            county              : county,
                            email               : randomEmail,
                            communicationAddress: street,
                            linkmodeNum         : randomMobile,
                            mobileTelephone     : randomMobile,             // 需要真实的手机号码，接受验证码
                            personnelFlag       : '1',//voucher?.insurantInfo?.personnelFlag,
                            sexCode             : applicantSex,
                            birthday            : applicantBirthday,
                            nation              : '汉',                         //采集身份信息，写死
                            issuer              : '北京市东城区美术馆后街',
                            certificateAddress  : '北京市东城区美术馆后街大取灯胡同2号院',
                            certificateIssueDate: '2018-05-05',
                            certificateValidDate: '2028-05-05'
                        ],

                        c01BaseInfo             : c01BaseInfo.subMap([
                            'insuranceBeginTime',
                            'insuranceEndTime',
                            'shortTimeCoefficient',
                            'planCode',
                            'departmentCode',
                            'totalAgreePremium',
                            'totalDiscountCommercial',
                            'totalActualPremium',
                            'totalStandardPremium',
                            'isRound',
                            'quoteSerialNo',
                            'quotationNo',
                            'brokerCode',
                            'rateClassFlag',
                            'isCalculateWithoutCirc',
                            'disputedSettleModeCode',
                            'renewalTypeCode',
                        ]) + [
                            isReportElectronRelation  : 'Y', // 是否上报电子联系单
                            isaccommodation           : 'N',
                            lastPolicyNo              : c01BaseInfo.policyNo,
                            commissionChargeProportion: c01BaseInfo.commissionChargeProportion,
                            agentCode                 : baseInfo.agentCode,
                            agentName                 : baseInfo.userName,
                            formatType                : '04',//商业保单形式
                            agentAgreementNo          : baseInfo.authorization.conferNo,
                            supplementAgreementNo     : baseInfo.authorization.subConferNo
                        ],
                        c51BaseInfo             : c51BaseInfo.subMap([
                            'insuranceBeginTime',
                            'insuranceEndTime',
                            'shortTimeCoefficient',
                            'departmentCode',
                            'planCode',
                            'totalAgreePremium',
                            'totalActualPremium',
                            'totalDiscountCommercial',
                            'totalStandardPremium',
                            'quotationNo',
                            'quoteSerialNo',
                            'renewalTypeCode',
                            'brokerCode',
                            'isCalculateWithoutCirc'
                        ]) + [
                            lastPolicyNo          : c51BaseInfo.policyNo,
                            disputedSettleModeCode: c01BaseInfo?.disputedSettleModeCode ?: 1,
                            agentCode             : baseInfo.agentCode,
                            agentName             : baseInfo.userName,
                            formatType            : '04',//商业保单形式
                            agentAgreementNo      : baseInfo.authorization.conferNo,
                            supplementAgreementNo : baseInfo.authorization.subConferNo
                        ],
                        c01ExtendInfo           : c01ExtendInfo.subMap([
                            'md5Result',
                            'ownerVehicleTypeCode',
                            'checkVehicleSituationCode'
                        ]) + [
                            offerLastPolicyFlag       : c01ExtendInfo.offerPoliceCertificateFlag,
                            expectationUnderwriteLimit: '2', // 与保存underwrite_info信息失败有关
                            samCode                   : getEnvProperty(context, 'pinganuk.samCode')
                        ],
                        c51ExtendInfo           : voucher.c51ExtendInfo.subMap([
                            'md5Result',
                            'ownerVehicleTypeCode',
                            'checkVehicleSituationCode'
                        ]) + [
                            expectationUnderwriteLimit: '2', // 与保存underwrite_info信息失败有关
                        ],
                        saleInfo                : baseInfo.subMap([
                            'departmentCode',
                            'businessSourceCode',
                            'businessSourceDetailCode',
                            'saleAgentCode',
                            'channelSourceCode',
                            'channelSourceDetailCode',
                            'saleGroupCode'
                        ]),

                        c01DisplayRateFactorList: voucher.c01DisplayRateFactorList.collect { factor ->
                            factor.subMap([
                                'factorValueName',
                                'factorRatioCOM',
                                'factorValue',
                                'factorCode',
                                'ratingTableNo',
                                'factorRatioVHL',
                                'factorRatioBGD',
                                'factorRatioDRIVERPSG',
                                'factorRatioOTHERS',
                                'factorRatioTHI'
                            ])
                        },
                        c51DisplayRateFactorList: voucher.c51DisplayRateFactorList.collect { factor ->
                            factor.subMap(['factorRatioCOM', 'factorValue', 'factorCode', 'ratingTableNo'])
                        },
                        c01DutyList             : voucher.c01DutyList.collect { c01KindItem ->
                            c01KindItem.subMap([
                                'dutyCode',
                                'insuredAmount',
                                'seats',
                                'premiumRate',
                                'basePremium',
                                'totalStandardPremium',
                                'totalAgreePremium',
                                'totalActualPremium',
                                'isSpecialGlass',
                                'insuredAmountDefaultValue',
                                'pureRiskPremium',
                                'riskPremium'
                            ])
                        },
                        c51DutyList             : voucher.c51DutyList.collect { c51KindItem ->
                            c51KindItem.subMap([
                                'dutyCode',
                                'insuredAmount',
                                'basePremium',
                                'totalStandardPremium',
                                'totalAgreePremium',
                                'totalActualPremium',
                            ])
                        },
                        vehicleTarget           : vehicleTarget.subMap([
                            'autoModelCode',
                            'modifyAutoModelName',
                            'exhaustCapability',
                            'engineNo',
                            'firstRegisterDate',
                            'purchasePriceDefaultValue',
                            'runRegionCode',
                            'vehicleLossInsuredValue',
                            'vehicleSeats',
                            'vehicleTypeCode',
                            'vehicleLicenceCode',
                            'vehicleFrameNo',
                            'licenceTypeCode',
                            'usageAttributeCode',
                            'ownershipAttributeCode',
                            'autoModelName',
                            'certificationDate',
                            'isCommercialVehicle',
                            'circAutoModelCode',
                            'isAbnormalCar',
                            'isMiniVehicle',
                            'wholeWeight',
                            'vehicleTonnages',
                            'vehicleLicenceValidDeadline',
                            'loanVehicleFlag',
                            'changeOwnerFlag',
                        ]) + [
                            ownerVehicleTypeCode: voucher.extendInfo.ownerVehicleTypeCode,
                            fleetMark           : vehicleTarget.fleetFlag,
                            price               : vehicleTarget.purchasePriceDefaultValue,
                        ],
                        vehicleTaxInfo          : voucher.vehicleTaxInfo.subMap([
                            'fuelType',
                            'totalTaxMoney',
                            'currentYearPay',
                            'taxType',
                            'isCommissionTax',
                            'taxPeriodEndDate',
                            'taxPeriodBeginDate',
                            'circTaxType',
                        ])
                    ]
                ]
            ]
        ]
    }

    static final _VERIFY_LOGIN_CAPTCHA_RH_370100 = { loginResult, context ->
        def m = loginResult.content =~ /.*CAS_SSO_COOKIE=(.*)/
        context.loginResult = [
            CAS_SSO_COOKIE: m[0][1],
        ]
    }

    static final _VERIFY_LOGIN_CAPTCHA_RH_DEFAULT = { loginResult, context ->
        def m = loginResult.content =~ /.*CAS_SSO_COOKIE=(.*)&.*PASESSION=(.*)/
        context.loginResult = [
            CAS_SSO_COOKIE: m[0][1],
            PASESSION     : m[0][2]
        ]
    }
}
