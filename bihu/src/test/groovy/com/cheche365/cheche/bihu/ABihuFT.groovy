package com.cheche365.cheche.bihu

import com.cheche365.cheche.bihu.app.config.BihuTestConfig
import com.cheche365.cheche.bihu.model.QuoteObject
import com.cheche365.cheche.parser.service.AFunctionalGeneralService
import com.cheche365.cheche.test.parser.AThirdPartyHandlerServiceFT
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer
import org.springframework.test.context.ContextConfiguration
import spock.lang.Unroll

import static com.cheche365.cheche.test.util.ValidationUtils.verify

@ContextConfiguration(
    classes = BihuTestConfig,
    initializers = ConfigFileApplicationContextInitializer
)
@Slf4j
abstract class ABihuFT extends AThirdPartyHandlerServiceFT {

    @Autowired
    private AFunctionalGeneralService functionalService

    @Override
    protected final getInsuranceCompanyProperties() {
        [id: 100000, code: 'CPIC', name: '壁虎车险']
    }

    @Unroll
    'ID：#id 、DESC：#desc ，用车牌号：#licensePlateNo 、车架号：#vinNo 、引擎号：#engineNo 、车主：#owner 、身份证：#identity 、套餐选项：#packageOptions 、预期报价：#expectedQuoteRecord 测试报价函数式接口'() {

        def notSkipThisCase = notSkipThisCase(additionalParams: additionalParams, env: env, conf: mergedConf, logger: log)

        when: '构造车和申请人，然后调用车型服务API'

        if (notSkipThisCase) {
            def quoteRecord = createQuoteRecord licensePlateNo, vinNo, engineNo, owner, identity, packageOptions, additionalParams
            def supplementInfo = createSupplementInfo additionalParams, quoteRecord.auto
            def params = [insuranceCompanyCodes: ['PICC', 'CPIC', 'PINGAN'], supplementInfo: supplementInfo]
            functionalService.quote new QuoteObject(quoteRecord, params)

            if (isQuotingPostProcessorEnabled()) {
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

}
