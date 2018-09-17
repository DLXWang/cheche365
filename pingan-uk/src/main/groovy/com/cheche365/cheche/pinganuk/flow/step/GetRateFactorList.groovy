package com.cheche365.cheche.pinganuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.Constants._DATETIME_FORMAT2
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static groovyx.net.http.ContentType.JSON
import static java.time.LocalDate.now as today

/**
 * 获取商业险税率因子
 * Created by wangxiaofei on 2016.8.29
 */
@Component
@Slf4j
class GetRateFactorList implements IStep {

    private static final _API_PATH_QUICK_SEARCH_VOUCHER = '/icore_pnbs/do/app/quotation/getRateFactorList'

    @Override
    run(context) {

        RESTClient client = context.client
        def baseInfo = context.baseInfo
        def defaultStartDateText = _DATETIME_FORMAT2.format today().plusDays(1).atTime(0, 0, 0)

        def args = [
            requestContentType: JSON,
            contentType       : JSON,
            path              : _API_PATH_QUICK_SEARCH_VOUCHER,
            body              : [
                departmentCode          : baseInfo.departmentCode,
                businessSourceCode      : baseInfo.businessSourceCode,
                businessSourceDetailCode: baseInfo.businessSourceDetailCode,
                channelSourceCode       : baseInfo.channelSourceCode,
                channelSourceDetailCode : baseInfo.channelSourceDetailCode,
                usageAttributeCode      : '02',             // 使用性质
                ownershipAttributeCode  : '03',             // 行驶证车主性质
                insuranceBeginTime      : defaultStartDateText,
                planCode                : 'C01',
                bidFlag                 : '0'
            ]
        ]

        def rateFactorList = client.post args, { resp, json ->
            json
        }

        if (rateFactorList) {
            context.rateFactorList = rateFactorList

            log.info '商业险税率因子，{}', rateFactorList
            getContinueFSRV { rateFactorList }
        } else {
            getFatalErrorFSRV '获取商业险税率因子失败'
        }
    }

}
