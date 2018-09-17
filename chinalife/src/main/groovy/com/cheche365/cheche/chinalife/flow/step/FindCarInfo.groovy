package com.cheche365.cheche.chinalife.flow.step

import com.cheche365.cheche.common.flow.IStep
import com.cheche365.cheche.core.model.Auto
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.chinalife.util.BusinessUtils.getAutoEngineNo
import static com.cheche365.cheche.chinalife.util.BusinessUtils.getAutoVinNo
import static com.cheche365.cheche.chinalife.util.BusinessUtils.getCarOwner
import static com.cheche365.cheche.common.util.FlowUtils.getLoopBreakFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopContinueFSRV
import static com.cheche365.flow.core.util.FlowUtils.getProvideValuableHintsFSRV
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_ENGINE_NO_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_LICENSE_PLATE_NO_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_OWNER_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_VIN_NO_TEMPLATE_QUOTING
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC


/**
 * 获取车辆品牌
 */
@Component
@Slf4j
class FindCarInfo implements IStep {
    private static final _URL_FIND_CAR_INFO = '/online/saleNewCar/carProposalfindCarInfo.do'

    @Override
    run(context) {
        RESTClient client = context.client

        def deptId = context.deptId
        def parentId = context.parentId
        Auto auto = context.auto
        def carOwner = getCarOwner context

        def args = [
            requestContentType: URLENC,
            contentType       : JSON,
            path              : _URL_FIND_CAR_INFO,
            body              : [
                'temporary.geProposalArea.deptID'             : deptId,
                'temporary.geProposalArea.parentid'           : parentId,
                'temporary.quoteMain.areaCode'                : deptId,
                'temporary.quoteMain.geQuoteCars[0].licenseNo': auto.licensePlateNo,
                'temporary.quoteMain.geQuoteCars[0].engineNo' : getAutoEngineNo(context),
                'temporary.quoteMain.geQuoteCars[0].frameNo'  : getAutoVinNo(context),
                'temporary.quoteMain.geQuoteCars[0].carOwner' : carOwner,
                // 下面两个参数北京用不到但是也没事儿
                'temporary.quoteMain.geQuoteCars[0].checkCode': context.captchaText,
                'temporary.quoteMain.geQuoteCars[0].checkNo'  : context.checkNo
            ]
        ]

        def result = client.post args, { resp, json ->
            json
        }

        if (result.resultType == '1') {
            log.info '自动获取车辆品牌结果：{}', result.list
            def carBrandInfo = result.list[0] // TODO：result.list如果数量超过1则应该推送补充信息
            context.carBrandInfo = carBrandInfo
            context.obtainCarModelV2XFlag = '0'
            getLoopBreakFSRV result
        } else if (result.resultType == '3' && checkResultMessage(result.resultMessage)) {
            log.error '新验证码错误：{}', result.resultMessage
            getLoopContinueFSRV result, '录入的校验码有误'
        } else {
            log.error '无法获得车型，通常是车辆/人员信息有误导致的：{}', result
            def hints = [
                _VALUABLE_HINT_LICENSE_PLATE_NO_TEMPLATE_QUOTING.with {
                    it.originalValue = context.auto.licensePlateNo
                    it
                },
                _VALUABLE_HINT_VIN_NO_TEMPLATE_QUOTING.with {
                    it.originalValue = context.auto.vinNo
                    it
                },
                _VALUABLE_HINT_ENGINE_NO_TEMPLATE_QUOTING.with {
                    it.originalValue = context.auto.engineNo
                    it
                },
                _VALUABLE_HINT_OWNER_TEMPLATE_QUOTING.with {
                    it.originalValue = context.auto.owner
                    it
                }
            ]
            getProvideValuableHintsFSRV { hints }
        }
    }

    private checkResultMessage(message) {
        def m = message =~/.*校验码.*/
        m.find()
    }

}
