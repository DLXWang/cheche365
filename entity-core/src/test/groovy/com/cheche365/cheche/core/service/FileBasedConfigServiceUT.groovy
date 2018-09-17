package com.cheche365.cheche.core.service

import com.cheche365.cheche.core.app.config.CoreServiceMinTestConfig
import com.cheche365.cheche.test.common.ALayeredTestDataFT
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Unroll



/**
 * 基于文件的配置服务单元测试
 * Created by Huabin on 2018/5/24.
 */
@ContextConfiguration(classes = CoreServiceMinTestConfig)
@Slf4j
class FileBasedConfigServiceUT extends ALayeredTestDataFT {

    @Autowired
    private IConfigService configService


    @Override
    protected boolean isNeedCloneData() {
        false
    }

    @Unroll
    'ID：#id 、DESC：#desc ，测试基于文件的配置服务getProperty'() {

        when: '创建服务实例，并获取指定属性'

            def propValue
            (loopTimes ?: 10).times {
                propValue = configService.getProperty propName
                log.info '属性 {} 的值为：{}', propName, propValue
                Thread.sleep interval ?: 1000L
            }

        then: '检查结果'

            expectedPropValue == propValue

        where:
            [ id, desc, propName, expectedPropValue, loopTimes, interval ] << testData
    }

    @Unroll
    'ID：#id 、DESC：#desc ，测试基于文件的配置服务getAllProperties'() {

        when: '创建服务实例，并获取指定属性'

            def props
            (loopTimes ?: 10).times {
                props = configService.getAllProperties namespace
                log.info '命名空间 {} 的所有属性为：{}', namespace, props
                Thread.sleep interval ?: 1000L
            }

        then: '检查结果'

            expectedPropValue == props

        where:
            [ id, desc, namespace, expectedPropValue, loopTimes, interval ] << testData
    }

}
