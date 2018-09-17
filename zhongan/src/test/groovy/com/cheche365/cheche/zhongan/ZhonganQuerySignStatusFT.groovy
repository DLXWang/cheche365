package com.cheche365.cheche.zhongan

import com.cheche365.cheche.test.parser.AParserServiceFT
import com.cheche365.cheche.zhongan.app.config.ZhonganConfig
import com.cheche365.cheche.zhongan.app.config.ZhonganNonProductionConfig
import com.cheche365.cheche.zhongan.service.ZhonganQuerySignService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Unroll

@ContextConfiguration(classes = [ZhonganConfig, ZhonganNonProductionConfig])
class ZhonganQuerySignStatusFT  extends AParserServiceFT{


    @Autowired
    private ZhonganQuerySignService service


    @Unroll
    'ID：#id，insureFlowCode : #insureFlowCode 测试签名接口'() {

        when: '测试签名接口'

        def flag  = service.isSigned(insureFlowCode)

        then :
        flag

        where:

        [id, insureFlowCode] << testData

    }


}
