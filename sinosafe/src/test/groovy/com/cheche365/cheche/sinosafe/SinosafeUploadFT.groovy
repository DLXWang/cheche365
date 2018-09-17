package com.cheche365.cheche.sinosafe

import com.cheche365.cheche.core.service.IThirdPartyUploadingService
import com.cheche365.cheche.sinosafe.app.config.SinosafeConfig
import com.cheche365.cheche.sinosafe.app.config.SinosafeNonProductionConfig
import com.cheche365.cheche.test.parser.AParserServiceFT
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Unroll



/**
 * 上传文件服务测试
 */
@Slf4j
@ContextConfiguration(classes = [SinosafeConfig, SinosafeNonProductionConfig])
class SinosafeUploadFT extends AParserServiceFT {

    @Autowired
    private IThirdPartyUploadingService service

    @Unroll
    '上传文件 测试上传接口'() {

        when: '发起上传申请'
            def list = ['http://www.cn486.com/upload/2010/10/08/101008094025552.jpg']

            def additionalParameters = [CAL_APP_NO: 'Q100701030120180020887_Q100701030220180021051']
            service.upload list, additionalParameters

        then: '检查结果'
            true
    }
}
