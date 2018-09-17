package com.cheche365.cheche.piccuk.client

import com.cheche365.cheche.core.model.Address
import com.cheche365.cheche.core.model.AutoType
import com.cheche365.cheche.core.model.IdentityType
import com.cheche365.cheche.core.model.Insurance
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.model.User
import com.cheche365.cheche.core.service.IThirdPartyHandlerService
import com.cheche365.cheche.piccuk.client.app.config.PiccUKClientTestConfig
import com.cheche365.cheche.test.parser.AParserServiceFT
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Shared
import spock.lang.Unroll

import static com.cheche365.cheche.common.Constants._DATE_FORMAT3
import static com.cheche365.cheche.core.model.Channel.Enum.WAP_8
import static com.cheche365.cheche.core.model.GlassType.Enum.findById
import static com.cheche365.cheche.parser.Constants._SUPPLEMENT_INFO_SUPPORT_LIST_TEMPLATE
import static com.cheche365.cheche.test.util.ValidationUtils.verify
import static java.util.UUID.randomUUID as uuid
import static org.apache.commons.lang3.SerializationUtils.clone

/**
 * 车型列表服务测试类
 */
@ContextConfiguration(classes = PiccUKClientTestConfig)
@Slf4j
class Piccuk110000beijingFT extends AParserServiceFT {

    @Autowired(required = false)
    private IThirdPartyHandlerService service

    @Shared
    private successfulInsuringTestData

    @Shared
    private failedInsuringTestData

    // 是否启用报价后处理，默认值false，即禁用
    private boolean quotingPostProcessorEnabled
    // 是否启用核保后处理，默认值false，即禁用
    private boolean insuringPostProcessorEnabled

    protected static final getInsuranceCompanyProperties() {
        [id: 10000, code: 'CPIC', name: '人保UK']
    }

    protected static final getAreaProperties() {
        [id: 110000L, name: '北京']
    }

    @Override
    protected void doSetup() {
        quotingPostProcessorEnabled = Boolean.valueOf(env.getProperty('test.parser.quotingPostProcessorEnabled',
            Boolean.FALSE.toString()))
        insuringPostProcessorEnabled = Boolean.valueOf(env.getProperty('test.parser.insuringPostProcessorEnabled',
            Boolean.FALSE.toString()))
    }

    @Override
    protected void doSetupSpec(clzBaseFtConf) {
        super.doSetupSpec clzBaseFtConf
        (successfulInsuringTestData, failedInsuringTestData) = createInsuringTestData(mergedConf, testData)
    }


    @Unroll
    'ID：#id 、DESC：#desc ，用车牌号：#licensePlateNo 、车架号：#vinNo 、引擎号：#engineNo 、车主：#owner 、身份证：#identity 、套餐选项：#packageOptions 、预期报价：#expectedQuoteRecord 测试报价接口'() {

        def notSkipThisCase = notSkipThisCase(additionalParams: additionalParams, env: env, conf: mergedConf, logger: log)

        when: '构造车和申请人，然后调用车型服务API'

        if (notSkipThisCase) {
            def quoteRecord = createQuoteRecord licensePlateNo, vinNo, engineNo, owner, identity, packageOptions, additionalParams
            def supplementInfo = createSupplementInfo additionalParams,quoteRecord.auto
            service.quote quoteRecord, [supplementInfo: supplementInfo] + [supplementInfoSupportList: clone(_SUPPLEMENT_INFO_SUPPORT_LIST_TEMPLATE)]

            if (quotingPostProcessorEnabled) {
                additionalParams?.quotingPostProcess?.call(env, quoteRecord)
            }
        } else {
            log.warn '由于不满足前置条件，本测试用例被跳过'
        }


        then: '检查结果'

        !notSkipThisCase || ignoreVerification ?: verify(expectedQuoteRecord, quoteRecord)


        where:

        [ id, desc, licensePlateNo, vinNo, engineNo, owner, identity, packageOptions, expectedQuoteRecord, additionalParams ] << mergedTestData

    }

    @Unroll
    'ID：#id 、DESC：#desc ，用车牌号：#licensePlateNo 、车架号：#vinNo 、引擎号：#engineNo 、车主：#owner 、身份证：#identity 、套餐选项：#packageOptions 、预期报价：#expectedQuoteRecord 测试投保接口，且核保成功'() {

        def notSkipThisCase = notSkipThisCase(additionalParams: additionalParams, env: env, conf: mergedConf, logger:
            log)

        when: '构造车和申请人，然后调用车型服务API'

        if (notSkipThisCase) {
            def quoteRecord = createQuoteRecord licensePlateNo, vinNo, engineNo, owner, identity, packageOptions,
                additionalParams
            def insurance = new Insurance(quoteRecord: createQuoteRecord(licensePlateNo, vinNo, engineNo, owner,
                identity, packageOptions, additionalParams))

            def po = new PurchaseOrder(orderNo: uuid() as String)

            def supplementInfo = createSupplementInfo additionalParams, quoteRecord.auto
            service.insure po, insurance, null, [supplementInfo: supplementInfo] + [supplementInfoSupportList: clone
                (_SUPPLEMENT_INFO_SUPPORT_LIST_TEMPLATE)] + [persistentState: additionalParams.persistentState ?
                clone(additionalParams.persistentState) : [:]]

            if (insuringPostProcessorEnabled) {
                additionalParams?.insuringPostProcess?.call(env, insurance)
            }
        } else {
            log.warn '由于不满足前置条件，本测试用例被跳过'
        }


        then: '检查结果'

        !notSkipThisCase || ignoreVerification ?: verify(expectedQuoteRecord, insurance.quoteRecord)


        where:

        [id, desc, licensePlateNo, vinNo, engineNo, owner, identity, packageOptions, expectedQuoteRecord,
         additionalParams] << successfulInsuringTestData

    }

    @Unroll
    'ID：#id 、DESC：#desc ，用车牌号：#licensePlateNo 、车架号：#vinNo 、引擎号：#engineNo 、车主：#owner 、身份证：#identity 、套餐选项：#packageOptions 、预期报价：#expectedQuoteRecord 同时测试报价、投保接口，且核保成功'() {

        def notSkipThisCase = notSkipThisCase(additionalParams: additionalParams, env: env, conf: mergedConf, logger:
            log)

        when: '构造车和申请人，然后调用车型服务API'

        if (notSkipThisCase) {
            def quoteRecord = createQuoteRecord licensePlateNo, vinNo, engineNo, owner, identity, packageOptions,
                additionalParams
            def supplementInfo = createSupplementInfo additionalParams, quoteRecord.auto
            def addParams = [supplementInfo: supplementInfo] + [supplementInfoSupportList: clone
                (_SUPPLEMENT_INFO_SUPPORT_LIST_TEMPLATE)] + [persistentState: additionalParams.persistentState ?
                clone(additionalParams.persistentState) : [:]]
            service.quote quoteRecord, addParams

            // 核保
            def po = new PurchaseOrder(orderNo: uuid() as String)
            po.deliveryAddress = new Address()
            po.applicant = new User()
            def insurance = new Insurance(quoteRecord: quoteRecord)
            service.insure po, insurance, null, addParams

            if (insuringPostProcessorEnabled) {
                additionalParams?.insuringPostProcess?.call(env, insurance)
            }
        } else {
            log.warn '由于不满足前置条件，本测试用例被跳过'
        }


        then: '检查结果'

        !notSkipThisCase || ignoreVerification ?: verify(expectedQuoteRecord, insurance.quoteRecord)


        where:

        [id, desc, licensePlateNo, vinNo, engineNo, owner, identity, packageOptions, expectedQuoteRecord,
         additionalParams] << successfulInsuringTestData

    }


    protected QuoteRecord createQuoteRecord(licensePlateNo, vinNo, engineNo, owner, identity, mergedPackageOptions,
                                            mergedAdditionalParams) {

        def modelBuilder = new ObjectGraphBuilder().with {
            classNameResolver = [name: 'reflection', root: 'com.cheche365.cheche.core.model']
            identifierResolver = 'refId'
            it
        }

        modelBuilder.quoteRecord {
            channel             WAP_8
            // 投保人
            applicant           name            : 'agent1',
                mobile          : mergedAdditionalParams?.quoteRecord?.applicant?.mobile

            // 车辆
            auto                vinNo           : vinNo,
                licensePlateNo  : licensePlateNo,
                engineNo        : engineNo,
                owner           : owner,
                identity        : identity,
                identityType    : new IdentityType(id: 1, description: '身份证', name: '身份证'),
                enrollDate      : mergedAdditionalParams?.quoteRecord?.auto?.enrollDate ? _DATE_FORMAT3.parse(mergedAdditionalParams.quoteRecord.auto.enrollDate) : null,
                autoType        : mergedAdditionalParams?.quoteRecord?.auto?.autoType || //向后兼容
                    mergedAdditionalParams?.quoteRecord?.extendedAttributes ?
                    new AutoType(code: mergedAdditionalParams.quoteRecord.auto.autoType.code)
                    : null,
                insuredIdNo     : mergedAdditionalParams?.quoteRecord?.auto?.insuredIdNo ?: null

            // 地域
            area                areaProperties

            // 保险公司
            insuranceCompany    insuranceCompanyProperties

            // 套餐
            insurancePackage    mergedPackageOptions.with { options ->
                if (options.glassType && options.glassType instanceof Number) {
                    /**
                     * 之所以要判断it.glassType是否是Number，是因为同一套测试数据被报价和投保测试用例同时使用，
                     * 由于投保用例执行到这里之后已经把glassType设置为GlassType的实例了，
                     * 所以，当投保接口也走到这里时it.glassType就不是个Number了。
                     */
                    options.glassType = findById(options.glassType as Long)
                }

                [
                    ['thirdPartyAmount',    'thirdPartyIop'],
                    ['driverAmount',        'driverIop'],
                    ['passengerAmount',     'passengerIop'],
                    ['scratchAmount',       'scratchIop'],
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

    protected static createSupplementInfo(mergedAdditionalParams, auto) {
        def supplementInfo = mergedAdditionalParams?.quoteRecord?.auto?.autoType?.supplementInfo
        [
            autoModel          : supplementInfo?.autoModel,
            commercialStartDate: supplementInfo?.commercialStartDate ? _DATE_FORMAT3.parse(supplementInfo.commercialStartDate) : null,
            compulsoryStartDate: supplementInfo?.compulsoryStartDate ? _DATE_FORMAT3.parse(supplementInfo.compulsoryStartDate) : null,
            transferDate       : supplementInfo?.transferDate ? _DATE_FORMAT3.parse(supplementInfo.transferDate) : null,
            transferFlag       : supplementInfo?.transferFlag ?: false,
            verificationCode   : supplementInfo?.verificationCode ?: null,
            verificationMobile : supplementInfo?.verificationMobile ?: null,
            idCard: [
                nation: supplementInfo?.idCard?.nation,
                issueDate: supplementInfo?.idCard?.issueDate,
                expirationDate: supplementInfo?.idCard?.expirationDate,
                issuingAuthority: supplementInfo?.idCard?.issuingAuthority,
                address: supplementInfo?.idCard?.address,
            ],
            enrollDate         : auto?.enrollDate,
            seats              : auto?.autoType?.seats,
            auto               : auto
        ]
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

}
