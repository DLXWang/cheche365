package com.cheche365.cheche.cpic.flow.step

import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

/**
 * 新步骤，目前只有重庆使用  TODO 和ASubmitVehicleDetailInfo 应该可以抽成一个类，他们只有一个url区别
 * 提交详细信息，原请求加上第一步返回的opportunityId, orderNo
 * Created by xushao on 2015/7/21.
 */
@Component
@Slf4j
class SubmitVehicleDetailInfo extends ASubmitVehicleDetailInfo {

    private static final _API_PATH_SUBMITVEHICLEDETAILINFO = 'cpiccar/salesNew/businessCollect/submitVehicleDetailInfo'

    @Override
    protected int getStepFactor() {
        log.info 'v3流程 查询车型'
        2
    }

    @Override
    protected getApiPath() {
        _API_PATH_SUBMITVEHICLEDETAILINFO
    }

}
