package com.cheche365.cheche.cpicuk

import com.cheche365.cheche.cpicuk.app.config.CpicUKMinTestConfig
import com.cheche365.cheche.cpicuk.service.CpicUKCheckAccountService
import com.cheche365.cheche.test.parser.AThirdPartyHandlerServiceFT
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Unroll


@ContextConfiguration(classes = [CpicUKMinTestConfig])
abstract class ACpicFT extends AThirdPartyHandlerServiceFT {

    @Override
    protected final getInsuranceCompanyProperties() {
        [id: 25000, code: 'CPIC', name: '太平洋保险']
    }

    @Autowired
    private CpicUKCheckAccountService cpicUKCheckLoginService

    @Unroll
    '测试登录接口'() {

        def payInfo

        when: '构造支付信息PayInfo并且发起支付申请'

       def aa =  cpicUKCheckLoginService.checkLoginStatus()
        println aa
        then: '检查结果'
        true
    }


}
