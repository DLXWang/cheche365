package com.cheche365.cheche.bihu.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.bihu.util.BusinessUtils.sendAndReceive
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.parser.Constants._DATE_FORMAT3
import static com.cheche365.cheche.parser.util.BusinessUtils.getCommercialInsurancePeriodTexts

/**
 * 获取车辆折扣价格
 */
@Component
@Slf4j
class GetDepreciationPrice implements IStep {

    private static final _API_PATH_GET_DEPRECIATION_PRICE = '/api/CarInsurance/GetDepreciationPrice'

    @Override
    run(context) {
        def auto = context.auto
        def queryBody = [
            BizStartDate : getCommercialInsurancePeriodTexts(context).first,
            RegisterDate : _DATE_FORMAT3.format(auto.enrollDate),
            PurchasePrice: '', // 购置价格
            CarType      : '0',
        ]

        def result = sendAndReceive context, _API_PATH_GET_DEPRECIATION_PRICE, queryBody, this.class.name

        if (1 == result.BusinessStatus) {
            getContinueFSRV result
        } else {
            log.error "壁虎获取车辆折扣价格异常状态码：{}，详细信息：{}", result.BusinessStatus, result.Item.QuoteResult ?: result.StatusMessage
            getFatalErrorFSRV result.StatusMessage
        }
    }

}
