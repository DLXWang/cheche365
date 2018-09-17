package com.cheche365.cheche.parser

import com.cheche365.cheche.core.model.InsurancePackage
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.test.common.ALayeredTestDataFT
import spock.lang.Unroll



/**
 * 人工规则的第三方保险服务测试用例
 * @author sufengchen
 */
class ArtificialThirdPartyHandlerServiceFT extends ALayeredTestDataFT {

    @Unroll
    'ID：#id 、DESC：#desc ，测试人工报价的保额'() {

        when: '调用测试开始'

            def qr = new QuoteRecord(
                insurancePackage: new InsurancePackage(packageOptions)
            )

            ArtificialPolicyConstants._QUOTE_RECORD_FAILED_RULE(0, qr, [:], null)


        then: '检查结果'

            isValid expected, qr, packageOptions


        where:

            [id, desc, packageOptions, expected] << testData

    }

    boolean isValid(expected, actual, packageOptions) {
        !ArtificialPolicyConstants._INSURANCE_PACKAGE_KIND_NAME.any { configItem ->
            def (packagePropName, qrAmountPropName) = configItem
            packageOptions[packagePropName] ? expected[qrAmountPropName] != actual[qrAmountPropName] : false
        }
    }

}
