package com.cheche365.cheche.pingan.flow.step.m

import com.cheche365.cheche.core.model.Auto
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.generateRequestParameters
import static com.cheche365.flow.core.util.FlowUtils.getNeedSupplementInfoFSRV
import static com.cheche365.cheche.parser.Constants._DATE_FORMAT3
import static com.cheche365.cheche.parser.Constants._SUPPLEMENT_INFO_CAR_SEAT_COUNT_TEMPLATE_QUOTING



/**
 * 转保保存车辆信息
 * Created by wangxin on 2015/11/4.
 */
@Component
@Slf4j
class QuoteSaveQuoteInfo extends ASaveQuoteInfo {
    @Override
    protected generateParams(context) {
        Auto auto = context.auto
        context.registerDate = _DATE_FORMAT3.format(auto.enrollDate ?: new Date())
        log.info '初登日期：{}', context.registerDate
        generateRequestParameters(context, this)
    }

    @Override
    protected getVehicleSeatSupplementInfoFSRV(context) {
        if (!context.carInfo.seat && !context.auto.autoType.seats) {
            log.warn '车辆座位数为零，推补充信息'
            getNeedSupplementInfoFSRV { [_SUPPLEMENT_INFO_CAR_SEAT_COUNT_TEMPLATE_QUOTING] }
        }
    }
}
