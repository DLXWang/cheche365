package com.cheche365.cheche.baoxian.pay

import com.cheche365.cheche.baoxian.app.config.BaoXianConfig
import com.cheche365.cheche.baoxian.model.PayInfo
import com.cheche365.cheche.core.app.config.CoreConfig
import com.cheche365.cheche.core.model.Area
import com.cheche365.cheche.core.model.InsuranceCompany
import com.cheche365.cheche.core.service.IPayUrlService
import com.cheche365.cheche.test.parser.AParserServiceFT
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Unroll



/**
 * Created by wangxin on 2017/7/14.
 */
@Slf4j
@ContextConfiguration(classes = [BaoXianConfig, CoreConfig])
class BaoxianPayServiceFT extends AParserServiceFT {

    @Autowired
    IPayUrlService<Void, PayInfo> service

    @Unroll
    '利用PayInfo发起支付申请 测试支付接口'() {

        def payInfo

        when: '构造支付信息PayInfo并且发起支付申请'

        payInfo = new PayInfo([
            taskId              : env.getProperty('test.baoxian.refund.taskId'),
            area                : new Area([
                id: 371600L
            ]),
            insuranceCompany    : new InsuranceCompany([id: 45000L]),
            additionalParameters: [
                auto: [:]
            ]
        ]
        )
        service.pay(payInfo)

        then: '检查结果'
        true
    }
}
