package com.cheche365.cheche.parser

import com.cheche365.cheche.core.model.AutoType
import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.GlassType
import com.cheche365.cheche.core.model.IdentityType
import com.cheche365.cheche.core.model.InsuranceCompany
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.service.IThirdPartyHandlerService
import com.cheche365.cheche.core.service.IThirdPartyHandlerService2
import com.cheche365.cheche.parser.service.ConThirdPartyHandlerService
import com.cheche365.cheche.parser.service.Referenced2ThirdPartyHandlerService
import com.cheche365.cheche.test.parser.AParserServiceFT
import groovy.util.logging.Slf4j
import groovyx.gpars.group.PGroup
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import spock.lang.Shared
import spock.lang.Unroll

import static com.cheche365.cheche.common.Constants._DATE_FORMAT3
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.ANSWERN_65000
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.CIC_45000
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.CPIC_25000
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.PICC_10000
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.PINGAN_20000
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.SINOSIG_15000
import static com.cheche365.cheche.parser.ArtificialPolicyConstants._GET_QUOTING_SERVICE
import static com.cheche365.cheche.parser.ArtificialPolicyConstants._POST_QUOTE_RECORD_FAILED_RULE
import static com.cheche365.cheche.parser.ArtificialPolicyConstants._THIRD_PARTY_HANDLER_RULE_MAPPINGS_1
import static com.cheche365.cheche.test.util.ValidationUtils.verify
import static org.apache.commons.lang3.SerializationUtils.clone

/**
 * 保险公司服务测试用例基类
 */

@Newify(ConfigSlurper)
@Slf4j
abstract class Referenced2ThirdPartyHandlerServiceFT extends AParserServiceFT {

    @Shared
    private successfulInsuringTestData

    @Shared
    private failedInsuringTestData

    @Autowired
    private Map<String, IThirdPartyHandlerService> services

    @Autowired
    @Qualifier('piccThirdPartyHandlerService')
    private IThirdPartyHandlerService piccService

    @Autowired
    @Qualifier('answernService')
    private IThirdPartyHandlerService answernService

    @Autowired
    @Qualifier('parserTaskPGroup')
    PGroup parserTaskPGroup

    private IThirdPartyHandlerService2 service

    // 是否启用报价后处理，默认值false，即禁用
    private boolean quotingPostProcessorEnabled
    // 是否启用核保后处理，默认值false，即禁用
    private boolean insuringPostProcessorEnabled


    @Override
    protected void doSetupSpec(clzBaseFtConf) {
        super.doSetupSpec clzBaseFtConf
        (successfulInsuringTestData, failedInsuringTestData) = createInsuringTestData(mergedConf, testData)
    }

    @Override
    protected void doSetup() {
        def companyServices = [
            (PICC_10000)   : piccService,
            (ANSWERN_65000): answernService
        ]
        IThirdPartyHandlerService2 referred = new ConThirdPartyHandlerService(companyServices, parserTaskPGroup)
        service = new Referenced2ThirdPartyHandlerService(
            referred,
            _POST_QUOTE_RECORD_FAILED_RULE,
            _THIRD_PARTY_HANDLER_RULE_MAPPINGS_1,
            _GET_QUOTING_SERVICE,
            services
        )
        quotingPostProcessorEnabled = Boolean.valueOf(env.getProperty('test.parser.quotingPostProcessorEnabled', Boolean.FALSE.toString()))
        insuringPostProcessorEnabled = Boolean.valueOf(env.getProperty('test.parser.insuringPostProcessorEnabled', Boolean.FALSE.toString()))
    }

    abstract protected getAreaProperties()


    @Unroll
    'ID：#id 、DESC：#desc ，用车牌号：#licensePlateNo 、车架号：#vinNo 、引擎号：#engineNo 、车主：#owner 、身份证：#identity 、套餐选项：#packageOptions 、预期报价：#expectedQuoteRecord 测试报价接口'() {

        def notSkipThisCase = notSkipThisCase(additionalParams: additionalParams, env: env, conf: mergedConf, logger: log)

        when: '构造车和申请人，然后调用车型服务API'

        def quoteRecord
        if (notSkipThisCase) {
            quoteRecord = createQuoteRecord licensePlateNo, vinNo, engineNo, owner, identity, packageOptions, additionalParams
            quoteRecord.insuranceCompany = PICC_10000
            quoteRecord.channel = Channel.Enum.WAP_8
            def supplementInfo = createSupplementInfo additionalParams, quoteRecord.auto, quoteRecord.insuranceCompany
            Map<InsuranceCompany, Map> result = service.quotes quoteRecord, [
                supplementInfo: supplementInfo,
//                flowState: [ hasThrowLackOfSupplementInfo: true ],
                quoteCompanies : [ PINGAN_20000, CIC_45000, CPIC_25000, SINOSIG_15000, PICC_10000, ANSWERN_65000]
            ]

            result.each { k, v ->
                if (v.code == 0) {
                    log.info "保险公司为： {}， 返回结果为：【总保费： {}， debugInfo： {}， addParams： {}】", k.code, v.data.quoteRecord.getTotalPremium(), v.metaInfo.debugInfo, v.data.additionalParameters
                    log.info "保险公司为： {}， 报价类型为：{} ", k.code, v.data.quoteRecord.type.name
                } else {
                    log.info "保险公司为： {}， 返回结果为：【失败状态：{}, 原始错误类型：{}， 参考公司：{}】", k.code, v.code, v.metaInfo.realCode, v.metaInfo.referencedInsuranceCompany
                }
            }

            if (quotingPostProcessorEnabled) {
                additionalParams?.quotingPostProcess?.call(env, quoteRecord)
            }
        } else {
            log.warn '由于不满足前置条件，本测试用例被跳过'
        }


        then: '检查结果'

        !notSkipThisCase || ignoreVerification ?: verify(expectedQuoteRecord, quoteRecord)


        where:

        [id, desc, licensePlateNo, vinNo, engineNo, owner, identity, packageOptions, expectedQuoteRecord, additionalParams] << mergedTestData

    }

    @Override
    protected final getTestDataParamsName() {
        "${testDataName}Params"
    }

    private static createInsuringTestData(conf, filteredTestData) {
        def failedInsuringTestDataAssemblers = conf.failedInsuringTestDataAssemblers
        if (failedInsuringTestDataAssemblers) {
            // 失败的数据片ID列表
            def failedSliceIDs = failedInsuringTestDataAssemblers.keySet()

            def groupedTestData = clone(filteredTestData).groupBy { slice ->
                !(slice[0] in failedSliceIDs)
            }

            /** 成功的投保测试数据 **/
            def successfulInsuringTestData = groupedTestData[true]

            /** 失败的投保测试数据 **/
            def failedInsuringTestData = groupedTestData[false].collect { slice ->
                def assembler = failedInsuringTestDataAssemblers[slice[0]]
                assembler slice
            }

            [successfulInsuringTestData, failedInsuringTestData]
        } else {
            [clone(filteredTestData), null]
        }
    }

    private QuoteRecord createQuoteRecord(licensePlateNo, vinNo, engineNo, owner, identity, mergedPackageOptions, mergedAdditionalParams) {

        def modelBuilder = new ObjectGraphBuilder().with {
            classNameResolver = [name: 'reflection', root: 'com.cheche365.cheche.core.model']
            identifierResolver = 'refId'
            it
        }

        modelBuilder.quoteRecord {
            // 投保人
            applicant name: 'agent1',
                mobile: mergedAdditionalParams?.quoteRecord?.applicant?.mobile

            // 车辆
            auto vinNo: vinNo,
                licensePlateNo: licensePlateNo,
                engineNo: engineNo,
                owner: owner,
                identity: identity,
                identityType: new IdentityType(id: 1, description: '身份证', name: '身份证'),
                enrollDate: mergedAdditionalParams?.quoteRecord?.auto?.enrollDate ? _DATE_FORMAT3.parse(mergedAdditionalParams.quoteRecord.auto.enrollDate) : null,
                autoType: mergedAdditionalParams?.quoteRecord?.auto?.autoType || //向后兼容
                    mergedAdditionalParams?.quoteRecord?.extendedAttributes ?
                    new AutoType(code: mergedAdditionalParams.quoteRecord.auto.autoType.code)
                    : null,
                insuredIdNo: mergedAdditionalParams?.quoteRecord?.auto?.insuredIdNo ?: null

            // 地域
            area areaProperties

            // 保险公司 并发时有多个保险公司暂不指定
//            insuranceCompany    mergedAdditionalParams?.quoteRecord?.insuranceCompany

            // 套餐
            insurancePackage mergedPackageOptions.with { options ->
                if (options.glassType && options.glassType instanceof Number) {
                    /**
                     * 之所以要判断it.glassType是否是Number，是因为同一套测试数据被报价和投保测试用例同时使用，
                     * 由于投保用例执行到这里之后已经把glassType设置为GlassType的实例了，
                     * 所以，当投保接口也走到这里时it.glassType就不是个Number了。
                     */
                    options.glassType = GlassType.Enum.findById(options.glassType as Long)
                }

                [
                    ['thirdPartyAmount', 'thirdPartyIop'],
                    ['driverAmount', 'driverIop'],
                    ['passengerAmount', 'passengerIop'],
                    ['scratchAmount', 'scratchIop'],
                ].each { amount, iop ->
                    options[amount] = options[amount] ?: 0.0
                    options[iop] = null == options[iop] ? options[amount] as boolean : options[iop]
                }

                options
            }

        }.with {
            if (auto.autoType) {
                auto.autoType.seats = mergedAdditionalParams?.quoteRecord?.auto?.autoType?.seats ?: 5
            }
            it
        }
    }

    private static createSupplementInfo(mergedAdditionalParams, auto, insuranceCompany) {
        def supplementInfo = mergedAdditionalParams?.quoteRecord?.auto?.autoType?.supplementInfo
        [
            autoModel          : supplementInfo?.autoModel,
            commercialStartDate: supplementInfo?.commercialStartDate ? _DATE_FORMAT3.parse(supplementInfo.commercialStartDate) : null,
            compulsoryStartDate: supplementInfo?.compulsoryStartDate ? _DATE_FORMAT3.parse(supplementInfo.compulsoryStartDate) : null,
            transferDate       : supplementInfo?.transferDate ? _DATE_FORMAT3.parse(supplementInfo.transferDate) : null,
            transferFlag       : supplementInfo?.transferFlag ?: false,
            verificationCode   : supplementInfo?.verificationCode ?: null,
            verificationMobile : supplementInfo?.verificationMobile ?: null,
            enrollDate         : auto?.enrollDate,
            code               : auto?.autoType?.code,
            seats              : auto?.autoType?.seats,
//            selectedAutoModel  : [
//                options: [
//                    [
//                        value : "LKAACI0031",
//                        meta:[
//                            vehicleOptionInfo:
//                            [
//                                brand:"雷克萨斯",
//                                family:"雷克萨斯ES系",
//                                gearbox:"手自一体",
//                                exhaustScale:"2.0L",
//                                model:"雷克萨斯LEXUS ES200轿车",
//                                productionDate:"2015款 ES200精英型",
//                                seats:5,
//                                newPrice:270000
//                            ]
//                        ]
//
//                    ],
//                    [
//                        value : "LKAACI0032",
//                        meta:[
//                            vehicleOptionInfo:
//                                [
//                                    brand:"雷克萨斯",
//                                    family:"雷克萨斯ES系",
//                                    gearbox:"手自一体",
//                                    exhaustScale:"2.0L",
//                                    model:"雷克萨斯LEXUS ES200轿车",
//                                    productionDate:"2015款 ES200舒适型",
//                                    seats:5,
//                                    newPrice:290000
//                                ]
//                        ]
//                    ],
//                    [
//                        value: "LKAACI0040",
//                        meta:[
//                            vehicleOptionInfo:
//                                [
//                                    brand:"雷克萨斯",
//                                    family:"雷克萨斯ES系",
//                                    gearbox:"手自一体",
//                                    exhaustScale:"2.0L",
//                                    model:"雷克萨斯LEXUS ES200轿车",
//                                    productionDate:"2017款 30周年纪念版",
//                                    seats:5,
//                                    newPrice:312000
//                                ]
//                        ]
//                    ],
//                    [
//                        value: "LKAACI0039",
//                        meta:[
//                            vehicleOptionInfo:
//                                [
//                                    brand:"雷克萨斯",
//                                    family:"雷克萨斯ES系",
//                                    gearbox:"手自一体",
//                                    exhaustScale:"2.0L",
//                                    model:"雷克萨斯LEXUS ES200轿车",
//                                    productionDate:"2016款 ES200特别限量版",
//                                    seats:5,
//                                    newPrice:306000
//                                ]
//                        ]
//                    ]
//                ],
//                selected: "LKAACI0039",
//                company: insuranceCompany
//            ]


        ]
    }

}
