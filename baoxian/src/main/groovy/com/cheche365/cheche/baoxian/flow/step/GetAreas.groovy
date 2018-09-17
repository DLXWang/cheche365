package com.cheche365.cheche.baoxian.flow.step

import com.cheche365.cheche.baoxian.flow.step.v2.ABaoXianCommonStep
import groovy.transform.InheritConstructors
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.AreaUtils.getProvinceCode
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.parser.Constants._FLOW_PARTICIPANT_MESSAGE_1



/**
 * 获取开通车险投保的地市列表，验证此次投保的地区是否包含其中
 * Created by wangxin on 2017/2/9.
 */
@Component
@Slf4j
@InheritConstructors(
    constructorAnnotations = true,
    parameterAnnotations = true
)
class GetAreas extends ABaoXianCommonStep {

    private static final _API_PATH_GET_AREAS = ''

    @Override
    run(context) {
        context.flowParticipant?.sendMessage _FLOW_PARTICIPANT_MESSAGE_1

        def area = context.area
        //查询开通投保的地区列表只能通过省级编码查询，查询全国的参数传null
        def params = [agreementProvCode: getProvinceCode(area.id)]

        log.info '获取投保支持地区API：{}', prefix

        def result = send context, prefix + _API_PATH_GET_AREAS, params

        log.debug '获取投保支持地区结果：{}', result

        def cities = result.agreementAreas?.first()?.citys
        if (('0' == result.code || '00' == result.respCode) && cities) {
            def city = cities.find { c ->
                area.id as String == c.city
            }

            if (city) {
                log.info '查询到投保地区：{}', area.name
                getContinueFSRV city
            } else {
                log.error '{}地区暂不支持投保', area.name
                getFatalErrorFSRV '该地区不支持投保'
            }
        } else {
            log.error '查询投保地区失败：{}', result.msg
            getFatalErrorFSRV result.msg
        }
    }

}
