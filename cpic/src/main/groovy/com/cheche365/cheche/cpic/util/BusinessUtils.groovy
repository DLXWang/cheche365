package com.cheche365.cheche.cpic.util

import com.cheche365.cheche.core.model.Auto
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.model.User
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient

import java.awt.image.BufferedImage
import java.sql.Date

import static com.cheche365.cheche.common.util.FlowUtils.generateRequestParameters
import static com.cheche365.cheche.core.model.GlassType.Enum.DOMESTIC_1
import static com.cheche365.cheche.core.model.GlassType.Enum.IMPORT_2
import static com.cheche365.cheche.core.model.GlassType.Enum.findById
import static com.cheche365.cheche.cpic.flow.InsuranceMapping._FORCE_INSURANCE
import static com.cheche365.cheche.parser.Constants._FLOW_PARTICIPANT_MESSAGE_2
import static com.cheche365.cheche.parser.util.BusinessUtils.adjustInsureAmount
import static com.cheche365.flow.business.flow.util.BusinessUtils.getVehicleOption
import static groovyx.net.http.ContentType.JSON
import static java.awt.image.BufferedImage.TYPE_BYTE_BINARY
import static java.time.LocalDate.now as today
import static javax.imageio.ImageIO.read as readImage
import static javax.imageio.ImageIO.write as writeImage



/**
 * 业务对象相关的工具
 */
@Slf4j
class BusinessUtils {

    // 发动机号最大长度
    private static final _ENGINE_NO_MAX_LENGTH = 16

    static final _PREPROCESS_CAPTCHA_IMAGE = { imagePath ->
        def image = readImage imagePath as File
        def h = image.height
        def w = image.width
        def bgColor = image.getRGB 0, 0

        /**
         * 图片处理思路：
         * 1、将图片中的斜线等噪音去掉：
         * 我们基于的理论是，通常情况下斜线都不大可能在一列中同时出现在3行上（斜线恰巧为一条竖线的可能性太低了）
         * 2、然后再二值化
         * 具体步骤：
         * 1、按列收集各个点的rgb以及该点是否与坐标(0, 0)的颜色一致，不一致记为1，否则记为0
         * 2、按列分析，按rgb分组，得到各种rgb及出现次数的映射关系，最后保留那些出现次数超过3的rgb，构成一个rgbList
         * 3、二值化：处理整张图片，如果像素点的rgb在前面的rgbList中就置为黑色，否则置为白色
         */
        def binaryImage = [0..<h, 0..<w].combinations().with { coordinates ->
            coordinates.collect { y, x ->
                image.getRGB(x, y).with { rgb ->
                    [x, rgb, rgb != bgColor ? 1 : 0]
                }
            }.groupBy { x, _1, _2 ->
                x
            }.collectMany { x, colRgbCountTuples ->
                colRgbCountTuples.collect { _, rgb, count ->
                    [rgb, count]
                }.groupBy { rgb, _2 ->
                    rgb
                }.collectEntries { rgb, rgbCountTuples ->
                    [(rgb): rgbCountTuples.collect { _, count -> count }.sum()]
                }.findAll { _, count ->
                    count > 3
                }.keySet()
            }.groupBy(Closure.IDENTITY).collect { rgb, _ ->
                rgb
            }.with { colors ->
                new BufferedImage(w, h, TYPE_BYTE_BINARY).with { newImage ->
                    coordinates.each { y, x ->
                        def rgb = image.getRGB(x, y) in colors ? 0x000000 : 0xFFFFFF
                        newImage.setRGB x, y, rgb
                    }
                    newImage
                }
            }
        }

        (imagePath - '.png' + '-p.png').with { newImagePath ->
            writeImage binaryImage, 'png', newImagePath as File
            newImagePath
        }
    }

    private static final _MAPPING_DOUBLE_TYPE = [
        'damagePremium'              : 'DamageLossCoverage',
        'thirdPartyPremium'          : 'ThirdPartyLiabilityCoverage',
        'theftPremium'               : 'TheftCoverage',
        'driverPremium'              : 'InCarDriverLiabilityCoverage',
        'passengerPremium'           : 'InCarPassengerLiabilityCoverage',
        'glassPremium'               : 'GlassBrokenCoverage',
        'scratchPremium'             : 'CarBodyPaintCoverage',
        'spontaneousLossPremium'     : 'SelfIgniteCoverage',
        'spontaneousLossIop'         : 'SelfIgniteExemptDeductibleSpecialClause',
        'unableFindThirdPartyPremium': 'DamageLossCannotFindThirdSpecialCoverage',
        'enginePremium'              : 'PaddleDamageCoverage',
        'damageIop'                  : 'DamageLossExemptDeductibleSpecialClause',
        'thirdPartyIop'              : 'ThirdPartyLiabilityExemptDeductibleSpecialClause',
        'theftIop'                   : 'TheftCoverageExemptDeductibleSpecialClause',
        'passengerIop'               : 'InCarPassengerLiabilityExemptDeductibleSpecialClause',
        'driverIop'                  : 'InCarDriverLiabilityExemptDeductibleSpecialClause',
        'scratchIop'                 : 'CarBodyPaintExemptDeductibleSpecialClause',
        'engineIop'                  : 'PaddleDamageExemptDeductibleSpecialClause',
        'premium'                    : 'total'
    ]

    private static final _CHECHE_AMOUNT_CONVERTER = { coverageName, convertedCustCoverage ->
        def coverageAmount = (convertedCustCoverage[coverageName] ?: 0) as double
        [(coverageName): coverageAmount]
    }

    private static final _CHECHE_BOOLEAN_CONVERTER = { coverageName, convertedCustCoverage ->
        [(coverageName): convertedCustCoverage[coverageName] as boolean]
    }

    private static final _CHECHE_GLASS_CONVERTER = { coverageName, convertedCustCoverage ->
        'glass' in convertedCustCoverage.keySet() ? [glass: true, glassType: findById(convertedCustCoverage.glassType as Long)] : [glass: false]
    }

    static final _CHECHE_COVERAGES_V3 = [
        DamageLossCoverage                                  : ['damage', _CHECHE_AMOUNT_CONVERTER],
        DamageLossExemptDeductibleSpecialClause             : ['damageIop', _CHECHE_BOOLEAN_CONVERTER],
        CarBodyPaintCoverage                                : ['scratchAmount', _CHECHE_AMOUNT_CONVERTER],
        PaddleDamageCoverage                                : ['engine', _CHECHE_BOOLEAN_CONVERTER],
        SelfIgniteCoverage                                  : ['spontaneousLoss', _CHECHE_BOOLEAN_CONVERTER],
        SelfIgniteExemptDeductibleSpecialClause             : ['spontaneousLossIop', _CHECHE_BOOLEAN_CONVERTER],
        DamageLossCannotFindThirdSpecialCoverage            : ['unableFindThirdParty', _CHECHE_BOOLEAN_CONVERTER],
        GlassBrokenCoverage                                 : ['glass', _CHECHE_GLASS_CONVERTER],
        ThirdPartyLiabilityCoverage                         : ['thirdPartyAmount', _CHECHE_AMOUNT_CONVERTER],
        ThirdPartyLiabilityExemptDeductibleSpecialClause    : ['thirdPartyIop', _CHECHE_BOOLEAN_CONVERTER],
        InCarDriverLiabilityCoverage                        : ['driverAmount', _CHECHE_AMOUNT_CONVERTER],
        InCarPassengerLiabilityCoverage                     : ['passengerAmount', _CHECHE_AMOUNT_CONVERTER],
        TheftCoverage                                       : ['theft', _CHECHE_AMOUNT_CONVERTER],
        TheftCoverageExemptDeductibleSpecialClause          : ['theftIop', _CHECHE_BOOLEAN_CONVERTER],
        CarBodyPaintExemptDeductibleSpecialClause           : ['scratchIop', _CHECHE_BOOLEAN_CONVERTER],
        PaddleDamageExemptDeductibleSpecialClause           : ['engineIop', _CHECHE_BOOLEAN_CONVERTER],
        InCarDriverLiabilityExemptDeductibleSpecialClause   : ['driverIop', _CHECHE_BOOLEAN_CONVERTER],
        InCarPassengerLiabilityExemptDeductibleSpecialClause: ['passengerIop', _CHECHE_BOOLEAN_CONVERTER]
    ]

    static populateQuoteRecord(context) {
        context.flowParticipant?.sendMessage _FLOW_PARTICIPANT_MESSAGE_2
        User applicant = context.applicant
        Auto auto = context.auto
        def insuranceCompany = context.insuranceCompany
        def insurancePackage = context.accurateInsurancePackage
        def today = today()
        def createTime = Date.valueOf today
        def quoteFieldStatus = context.quoteFieldStatus
        quoteFieldStatus = quoteFieldStatus.size() != 0 ? quoteFieldStatus : null

        new QuoteRecord(
            applicant: applicant,
            auto: auto,
            insuranceCompany: insuranceCompany,
            insurancePackage: insurancePackage,
            createTime: createTime,
            quoteFieldStatus: quoteFieldStatus
        ).with { quoteRecord ->
            //保费赋值
            _MAPPING_DOUBLE_TYPE.each { entry ->
                quoteRecord[entry.key] = (context.premiumInfo[entry.value] ?: 0.0) as double
            }

            //保额赋值
            context.amount.each { entry ->
                if (!entry || !entry.value) {
                    entry.value = 0
                }

                quoteRecord[entry.key] = entry.value as double
            }
            //车座位数设置
            passengerCount = context.baseInfoResult?.VehicleInfo?.seatCount ?: context.vehicleInfo.seatCount
            //不计免赔总额计算
            iopTotal = sumIopItems()
            quoteRecord
        }
    }

    static loopAnswer(context, path, step) {
        try {
            context.validationCodeFlag = true
            RESTClient client = context.client
            def args = [
                requestContentType: JSON,
                contentType       : JSON,
                path              : path,
                body              : generateRequestParameters(context, step)
            ]
            client.post args, { resp, json ->
                json
            }
        } finally {
            context.validationCodeFlag = false
        }
    }

    static generateCustInsurancePackage(context, checheCoverages, custCoverage) {
        def semiInsurancePackageProps = convertPropertiesName custCoverage, checheCoverages
        if ('glass' in semiInsurancePackageProps.keySet()) {
            semiInsurancePackageProps << ['glassType': (context.allCoverageInfo.GlassBrokenCoverage.coverageAmount as long) + 1]
        }
        def insurancePackageProps = checheCoverages.collectEntries { _, properties ->
            if (properties instanceof Map) {
                properties.collectEntries { _0, value ->
                    def (ipPropName, extractor) = value
                    extractor ipPropName, semiInsurancePackageProps
                }
            } else if (properties instanceof List) {
                def (ipPropName, extractor) = properties
                extractor ipPropName, semiInsurancePackageProps
            }
        }
        def modelBuilder = new ObjectGraphBuilder().with {
            classNameResolver = [name: 'reflection', root: 'com.cheche365.cheche.core.model']
            identifierResolver = 'refId'
            it
        }

        modelBuilder.insurancePackage(insurancePackageProps + [compulsory: true, autoTax: true])
    }

    private static convertPropertiesName(custCoverage, checheCoverages) {
        def custCoverageNames = custCoverage.coverageCode
        def groupedRiderSpecialClauseCoverage = custCoverage.groupBy { coverage ->
            'PaddleDamageExemptDeductibleSpecialClause' in custCoverageNames ? 'others' :
                coverage.coverageCode == 'RiderExemptDeductibleSpecialClause' ? 'RiderExemptDeductibleSpecialClause' :
                    coverage.coverageCode == 'InCarPersonLiabilityExemptDeductibleSpecialClause' ?
                        'InCarPersonLiabilityExemptDeductibleSpecialClause' :
                        'others'
        }
        groupedRiderSpecialClauseCoverage.collectEntries { group, values ->
            def kindCodeIpPropNameMappings = checheCoverages[group]
            if (kindCodeIpPropNameMappings) {
                kindCodeIpPropNameMappings.findResults { kindCode, ipPropNameAndExtractor ->
                    kindCode in custCoverageNames ? [(ipPropNameAndExtractor[0]): true] : null
                }.sum() ?: [:]
            } else {
                values.collectEntries {
                    [(checheCoverages[it.coverageCode]?.getAt(0)): it.coverageAmount ?: true]
                }
            }
        }
    }

    /**
     * 车型列表option构造闭包
     */
    static final _CPIC_GET_VEHICLE_OPTION = { context, vehicle ->
        def vehicleOptionInfo = [
            brand          : vehicle.vehicleBrand,
            family         : vehicle.rVehicleFamily,
            gearbox        : vehicle.gearBoxType,
            exhaustScale   : vehicle.engineDesc,
            model          : vehicle.moldName,
            productionDate : vehicle.marketYear,
            seats          : vehicle.seatCount,
            newPrice       : vehicle.price,
        ]
        getVehicleOption vehicle.moldCharacterCode, vehicleOptionInfo
    }

    private static final _CHANGE_AMOUNT_EXTRACTOR = { it.value as double }

    private static toggleIopInsurance(insurancePackage, iopPropName, description) {
        def originIop = insurancePackage[iopPropName]
        insurancePackage[iopPropName] = !originIop
    }

    private static createCoverageInfoList(insurancePackage, propName, insurance, coverageAmount, context) {
        def coverageInfoList = []
        if (insurancePackage[propName]) {
            def (description, rulePropName, iopPropName, coverageKind) = insurance[0]
            def (rulePropIopName, coverageIopKind) = insurance[1]
            coverageInfoList += [
                coverageCode  : rulePropName,
                coverageName  : description,
                coverageAmount: coverageAmount,
                coverageKind  : coverageKind
            ]
            def specifyIopFilterFlagForNewCity = iopPropName in ['scratchIop', 'engineIop'] && !insurance[0][5] && !(insurance[1][0] in context.preRules?.PlanRelationShip?.keySet())
            if (iopPropName && insurancePackage[iopPropName] && !specifyIopFilterFlagForNewCity) {
                coverageInfoList += [
                    coverageCode  : rulePropIopName,
                    coverageName  : description + '不计免赔',
                    coverageAmount: '',
                    coverageKind  : coverageIopKind
                ]
            }
        }
        coverageInfoList
    }

    static removeDuplicateCoverage(coverageInfoList) {
        coverageInfoList.groupBy { item ->
            item.coverageCode
        }.collect { key, item ->
            item[0]
        }
    }

    /**
     * 将业务对象规则化
     * @param quoteRecord
     */
    static void normalizeBusinessObjects(QuoteRecord quoteRecord) {
        quoteRecord.auto.engineNo = quoteRecord.auto.engineNo?.with { engineNo ->
            engineNo ? engineNo[0..(Math.min(engineNo.size(), _ENGINE_NO_MAX_LENGTH) - 1)] : engineNo
        }
    }

    private static checkAttachIopNotAllowed (context) {
        def insuranceCoverage = context.preRules.PlanRelationShip['RiderExemptDeductibleSpecialClause']
        (!insuranceCoverage || (insuranceCoverage?.priceListDTO?.size() == 1 && insuranceCoverage?.priceListDTO[0].price == '不投'))
    }

    private static final _AMOUNT_CONVERTER = { propName, insurance, insuranceCoverages, context, insurancePackage, attachIopNotAllowed ->
        def quoteFieldStatus = []
        def (description, rulePropName, iopPropName, _kindCode, isAttach) = insurance[0]
        def insuranceCoverage = context.preRules.PlanRelationShip[rulePropName]

        if (insurancePackage[propName] || insuranceCoverage?.priceListDTO?.get(0)?.price != '不投') {
            def amount = adjustInsureAmount insurancePackage[propName],
                insuranceCoverage?.priceListDTO?.findAll { it.value != 'null' },
                _CHANGE_AMOUNT_EXTRACTOR, _CHANGE_AMOUNT_EXTRACTOR
            if (!amount) {
                if (insurancePackage[propName]) {
                    insurancePackage[propName] = 0
                    if (iopPropName && insurancePackage[iopPropName]) {
                        toggleIopInsurance insurancePackage, iopPropName, description
                    }
                }
            } else if (insurancePackage[propName] != amount) {
                if (!(insurancePackage[propName] as boolean)) {
                    insurancePackage[iopPropName] = true
                }
                insurancePackage[propName] = amount
            }
        }

        // 如果是该险种是 附加险&附加险不计免赔不投投保&套餐选择了不计免赔
        if (iopPropName && insurancePackage[iopPropName] && attachIopNotAllowed && isAttach) {
            toggleIopInsurance insurancePackage, iopPropName, description
        }


        [quoteFieldStatus, createCoverageInfoList(insurancePackage, propName, insurance, insurancePackage[propName], context), [(propName) : insurancePackage[propName]]]
    }

    private static final _BOOLEAN_CONVERTER = { amountConverter, propName, insurance, insuranceCoverages, context, insurancePackage, attachIopNotAllowed ->
        def quoteFieldStatus = []
        def (description, rulePropName, iopPropName, _kindCode, isAttach) = insurance[0]
        def coverageAmount = 0

        def insuranceCoverage = context.preRules.PlanRelationShip[rulePropName]
        if (!insurancePackage[propName]) {
            def flag = insuranceCoverage?.priceListDTO?.find {
                it.price == '不投'
            }
            if (!flag && insuranceCoverage) {
                insurancePackage[propName] = true
                toggleIopInsurance insurancePackage, iopPropName, description
            }
        } else {
            if (!insuranceCoverage || (insuranceCoverage?.priceListDTO?.size() == 1 && insuranceCoverage?.priceListDTO[0].price == '不投')) {
                insurancePackage[propName] = false //propName 应该是主险，insurance[2]是不计免赔
                if (iopPropName && insurancePackage[iopPropName]) {
                    toggleIopInsurance insurancePackage, iopPropName, description
                }
            } else {
                if (insuranceCoverage) {
                    coverageAmount = insuranceCoverage?.priceListDTO?.size() == 1 ? insuranceCoverage?.priceListDTO[0].value : insuranceCoverage?.priceListDTO[1].value
                }
            }
        }

        // 如果是该险种是 附加险&附加险不计免赔不投投保&套餐选择了不计免赔
        if (iopPropName && insurancePackage[iopPropName] && attachIopNotAllowed && isAttach) {
            toggleIopInsurance insurancePackage, iopPropName, description
        }

        coverageAmount = amountConverter coverageAmount
        [quoteFieldStatus, createCoverageInfoList(insurancePackage, propName, insurance, coverageAmount, context), [(propName + 'Amount') : coverageAmount]]
    }

    private static final _BOOLEAN_CONVERTER_V3 = _BOOLEAN_CONVERTER.curry {
        it
    }

    private static final _GLASS_CONVERTER = { propName, insurance, insuranceCoverages, context, insurancePackage, attachIopNotAllowed ->
        def quoteFieldStatus = []
        def glassManufacturer = null
        if (insurancePackage.glass) {
            def glassOption = context.preRules.PlanRelationShip.GlassBrokenCoverage?.priceListDTO
            if (glassOption) {
                glassManufacturer = insurancePackage.glassType == DOMESTIC_1 ? '0' : '1'
                //太平洋玻璃类型  0 国产 1进口
                def existed = glassOption.any { option ->
                    option.value == glassManufacturer
                }
                if (!existed) {
                    insurancePackage.glassType = glassManufacturer == '0' ? IMPORT_2 : DOMESTIC_1
                    glassManufacturer = glassManufacturer == '0' ? '1' : '0'
                }
            } else {
                insurancePackage.glass = false
                insurancePackage.glassType = null
            }
        }
        context.glassManufacturer = glassManufacturer
        [quoteFieldStatus, createCoverageInfoList(insurancePackage, propName, insurance, context.glassManufacturer, context), [:]]
    }

    //处理组合险种问题，车损不投，无法投保 玻璃，涉水，自燃，划痕（附加险）
    private static final _GROUP_DAMAGE_CONVERTER = { propName, insurance, insuranceCoverages, context, insurancePackage, attachIopNotAllowed ->
        def quoteFieldStatus = []
        if (!insurancePackage[propName]) {
            insurance[2].collect { item ->
                def (description, _rulePropName, iopPropName, _coverageKind) = insuranceCoverages[item][0]
                if (insurancePackage[item]) {
                    if (item == 'scratchAmount') {
                        insurancePackage[item] = 0
                    } else {
                        insurancePackage[item] = false
                    }
                    if (iopPropName && insurancePackage[iopPropName]) {
                        toggleIopInsurance insurancePackage, iopPropName, description
                    }
                }
            }
        }
        [quoteFieldStatus, [], [:]]
    }

    static final _KIND_CODE_CONVERTERS_V3 = [
        ['DamageLossCoverage',              _BOOLEAN_CONVERTER_V3,         'damage'],
        ['GroupDamageCoverage',             _GROUP_DAMAGE_CONVERTER,       'damage'],
        ['PaddleDamageCoverage',            _BOOLEAN_CONVERTER_V3,         'engine'],
        ['TheftCoverage',                   _BOOLEAN_CONVERTER_V3,         'theft'],
        ['SelfIgniteCoverage',              _BOOLEAN_CONVERTER_V3,         'spontaneousLoss'],
        ['DamageLossCannotFindThirdSpecialCoverage', _BOOLEAN_CONVERTER_V3,'unableFindThirdParty'],
        ['ThirdPartyLiabilityCoverage',     _AMOUNT_CONVERTER,             'thirdPartyAmount'],
        ['InCarDriverLiabilityCoverage',    _AMOUNT_CONVERTER,             'driverAmount'],
        ['InCarPassengerLiabilityCoverage', _AMOUNT_CONVERTER,             'passengerAmount'],
        ['CarBodyPaintCoverage',            _AMOUNT_CONVERTER,             'scratchAmount'],
        ['GlassBrokenCoverage',             _GLASS_CONVERTER,              'glass']
    ]


    private static getKindCodeConverters(codeConverter, insuranceCoverages) {
        codeConverter.collect { kindCode, converter, propName ->
            [ kindCode, converter.curry(propName, insuranceCoverages[propName], insuranceCoverages)]
        }
    }

    static getChangedKindItems(codeConverter, context, insuranceCoverages) {
        def attachIopNotAllowed = checkAttachIopNotAllowed(context)
        def result = getKindCodeConverters(codeConverter, insuranceCoverages).collect { insurancePackageConverter ->
            def (_, converter) = insurancePackageConverter
            def (quoteFieldStatus, coverageInfo, amount) = converter(context, context.accurateInsurancePackage, attachIopNotAllowed)
            [quoteFieldStatus: quoteFieldStatus, coverageInfo: coverageInfo, amount: amount]
        }

        def coverageInfoList = removeDuplicateCoverage(result.coverageInfo).findAll {
            it
        }.flatten()

        [
            result.quoteFieldStatus.sum(),
            coverageInfoList,
            result.amount.sum([:])
        ]
    }

    static addForcedInsurance(context) {
        def filledInsuranceNames = context.insurancePackage.properties.findAll {
            it.value
        }.keySet()
        _FORCE_INSURANCE.findAll {
            !(it.key in filledInsuranceNames)
        }.each { name, values ->
            if (context.area.id in values[0]) {
                context.accurateInsurancePackage[name] = values[1]
                context.accurateInsurancePackage[values[2]] = context.iopEnabled
            }
        }
    }

}
