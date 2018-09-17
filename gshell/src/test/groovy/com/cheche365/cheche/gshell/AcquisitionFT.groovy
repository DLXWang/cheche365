package com.cheche365.cheche.gshell

import com.cheche365.cheche.core.service.IOCRService
import com.cheche365.cheche.gshell.app.config.GshellConfig
import com.cheche365.cheche.test.common.ALayeredTestDataFT
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Unroll



/**
 * 悟空信息采集测试
 * Created by wangxf on 2017/8/28
 */
@ContextConfiguration(classes = [GshellConfig])
class AcquisitionFT extends ALayeredTestDataFT {

    @Autowired
    private IOCRService<Map> service


    @Unroll
    'ID：#id 、DESC：#desc ，图像路径：#imgUrl 测试悟空采集接口'() {

        when: '调用悟空采集API'

            service.getInformation imgUrlTexts, additionalParams

        then: '检查结果'

            ignoreVerification

        where:

            [id, desc, imgUrlTexts, additionalParams] << testData

    }

}
