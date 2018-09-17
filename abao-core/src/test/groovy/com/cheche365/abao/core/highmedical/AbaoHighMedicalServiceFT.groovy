package com.cheche365.abao.core.highmedical

import com.cheche365.abao.core.app.config.AbaoCoreConfig
import com.cheche365.abao.core.app.config.AbaoCoreTestConfig
import com.cheche365.abao.core.highmedical.model.QuoteObject
import com.cheche365.cheche.core.app.config.CoreConfig
import com.cheche365.cheche.core.model.InsuranceCompany
import com.cheche365.cheche.core.model.abao.InsuranceField
import com.cheche365.cheche.core.model.abao.InsurancePerson
import com.cheche365.cheche.core.model.abao.InsuranceProduct
import com.cheche365.cheche.core.model.abao.InsuranceQuoteField
import com.cheche365.cheche.core.service.spi.IHandlerService
import com.cheche365.cheche.test.common.ALayeredTestDataFT
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.test.context.ContextConfiguration
import spock.lang.Unroll

import static com.cheche365.cheche.common.Constants._DATE_FORMAT3

/**
 * 阿宝报价测试用例
 */

@ContextConfiguration(classes = [AbaoCoreConfig, AbaoCoreTestConfig, CoreConfig])
class AbaoHighMedicalServiceFT extends ALayeredTestDataFT {

    @Autowired
    protected Environment env

    @Autowired
    private IHandlerService service


    @Unroll
    'ID：#id 、DESC：#desc ，用生日：#birthday 、社保：#socialSecurity、套餐选项：#packageOptions 、预期保费：#expectedPremium 测试高端医疗报价接口'() {

        def quoteObject, quoteResult

        when: '阿宝高端医疗报价'

        quoteObject = createQuoteObject birthday, socialSecurity, packageOptions
        quoteResult = service.quote quoteObject

        then: '检查结果'
        expectedPremium == quoteResult.insuranceQuotes[0]?.premium

        where:
        [id, desc, birthday, socialSecurity, packageOptions, expectedPremium] << testData

    }

    private createQuoteObject(birthday, socialSecurity, packageOptions) {
        new QuoteObject().with {
            insuranceProduct = new InsuranceProduct(insuranceCompany: insuranceCompany)
            insuranceQuoteFields = packageOptions.collect { fieldCode, amount ->
                new InsuranceQuoteField(
                    amount: amount,
                    insuranceField: new InsuranceField(code: fieldCode)
                )
            }
            insurancePerson = new InsurancePerson(
                birthday: _DATE_FORMAT3.parse(birthday),
                socialSecurity: socialSecurity)

            it
        }
    }

    protected getInsuranceCompany() {
        new InsuranceCompany(id: -1L)
    }


    @Override
    protected getTestDataFilterConfig() {
        [
            appName: 'abao'
        ]
    }

    @Override
    protected getTestDataParamNamesMergeConfig() {
    }

}
