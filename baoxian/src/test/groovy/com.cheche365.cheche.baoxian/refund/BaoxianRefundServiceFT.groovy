package com.cheche365.cheche.baoxian.refund

import com.cheche365.cheche.baoxian.app.config.BaoXianConfig
import com.cheche365.cheche.baoxian.model.RefundInfo
import com.cheche365.cheche.core.app.config.CoreConfig
import com.cheche365.cheche.core.model.Area
import com.cheche365.cheche.core.model.InsuranceCompany
import com.cheche365.cheche.core.service.IRefundService
import com.cheche365.cheche.test.parser.AParserServiceFT
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Unroll


/**
 * Created by wangxin on 2017/4/5.
 */
@Slf4j
@ContextConfiguration(classes = [BaoXianConfig, CoreConfig])
class BaoxianRefundServiceFT extends AParserServiceFT {

    @Autowired
    IRefundService<Void, RefundInfo> service

    @Unroll
    '利用taskId和providerId发起退款申请 测试退款接口'() {

        def refundInfo

        when: '构造退款信息RefundInfo并且发起退款申请'

        refundInfo = new RefundInfo([
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
        service.refund(refundInfo)

        then: '检查结果'
        true
    }
}
