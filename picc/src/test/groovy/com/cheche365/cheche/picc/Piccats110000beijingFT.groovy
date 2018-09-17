package com.cheche365.cheche.picc

import com.cheche365.cheche.core.model.InsuranceCompany
import com.cheche365.cheche.core.model.VehicleLicense
import com.cheche365.cheche.core.service.IThirdPartyAutoTypeService
import com.cheche365.cheche.picc.config.PiccAutoTypeServiceConfig
import com.cheche365.cheche.test.parser.AParserServiceFT
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Unroll

import static com.cheche365.cheche.common.Constants.get_DATE_FORMAT3
import static com.cheche365.cheche.test.parser.util.BusinessUtils.getAreaOfAuto
import static com.cheche365.cheche.test.parser.util.BusinessUtils.mergeTestDataParams
import static com.cheche365.cheche.test.util.ValidationUtils.verify

@ContextConfiguration(
    classes = [PiccAutoTypeServiceConfig]
)
@Slf4j
class Piccats110000beijingFT  extends AParserServiceFT{

    @Autowired(required = false)
    private IThirdPartyAutoTypeService service


    @Unroll
    'ID：#id 、DESC：#desc ，用车牌号：#licensePlateNo 、车架号：#vinNo 、引擎号：#engineNo 、车主：#owner 、身份证：#identity 、套餐选项：#packageOptions 、预期报价：#expectedAutoTypes 测试获取车型列表接口'() {

        def notSkipThisCase = notSkipThisCase(additionalParams: additionalParams, env: env, conf: mergedConf, logger: log)

        def autoTypes

        when: '构造车和申请人，然后调用车型服务API'

        if (notSkipThisCase) {
            def (_, mergedAdditionalParams) = mergeTestDataParams(mergedConf, id, testDataParamsName, packageOptions, additionalParams)
            VehicleLicense vl = new VehicleLicense(
                licensePlateNo: licensePlateNo,
                owner: owner,
                identity: identity,
                vinNo: vinNo,
                engineNo: engineNo,
                enrollDate: mergedAdditionalParams?.quoteRecord?.auto?.enrollDate ? _DATE_FORMAT3.parse(mergedAdditionalParams.quoteRecord.auto.enrollDate) : null
            )
            def area = getAreaOfAuto licensePlateNo
            autoTypes = service.getAutoTypes vl, [area: area, insuranceCompany: new InsuranceCompany(additionalParams.insuranceCompany)]

        } else {
            log.warn '由于不满足前置条件，本测试用例被跳过'
        }

        then: '检查结果'

        !notSkipThisCase || ignoreVerification ?: verify(expectedAutoTypes, autoTypes)

        where:

        [id, desc, licensePlateNo, vinNo, engineNo, owner, identity, packageOptions, expectedAutoTypes, additionalParams] << testData

    }

}
