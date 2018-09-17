package com.cheche365.cheche.ccint

import com.cheche365.cheche.ccint.app.config.CcintConfig
import com.cheche365.cheche.ccint.app.config.CcintNonProductionConfig
import com.cheche365.cheche.ccint.app.config.CcintTestConfig
import com.cheche365.cheche.core.model.VehicleLicense
import com.cheche365.cheche.core.service.IOCRService
import com.cheche365.cheche.test.common.ALayeredTestDataFT
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer
import org.springframework.core.env.Environment
import org.springframework.test.context.ContextConfiguration
import spock.lang.Unroll

import static com.cheche365.cheche.test.util.ValidationUtils.verify

/**
 * 合合行驶证信息识别测试
 * Created by liheng on 2017/3/17 017.
 */
@ContextConfiguration(
    classes = [CcintConfig, CcintNonProductionConfig, CcintTestConfig],
    initializers = ConfigFileApplicationContextInitializer
)
class VehicleLicenseFT extends ALayeredTestDataFT {

    @Autowired
    protected Environment env

    @Autowired
    private IOCRService<VehicleLicense> service


    @Unroll
    'ID：#id 、DESC：#desc ，图像路径：#imgUrl 、预期IBI：#expectedInsuranceBasicInfo、附加参数：#additionalParams 测试合合行驶证识别接口'() {

        def insuranceInfo

        when: '调用行驶证识别API'

            insuranceInfo = service.getInformation imgUrl, additionalParams

        then: '检查结果'

            ignoreVerification ?: verify(expectedInsuranceBasicInfo, insuranceInfo)

        where:

            [id, desc, imgUrl, expectedInsuranceBasicInfo, additionalParams] << testData

    }

    @Override
    protected getTestDataFilterConfig() {
        [
            appName: 'ccint'
        ]
    }

    @Override
    protected getTestDataParamNamesMergeConfig() {
    }

}
