package com.cheche365.cheche.pingan.flow.step.m

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.ContactUtils.getBirthdayByIdentity
import static com.cheche365.cheche.common.util.ContactUtils.getGenderByIdentity
import static com.cheche365.cheche.common.util.ContactUtils.randomMobile
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static groovyx.net.http.ContentType.JSON

/**
 * 信息确认
 * @author wangxiaofei on 2016.8.24
 */
@Component
@Slf4j
class InfoConfirm implements IStep {

    private static final _API_PATH_INFO_CONFIRM = 'autox/do/api/info-confirm'

    @Override
    run(context) {
        def client = context.client
        def mobile = randomMobile
        def auto = context.auto
        def area = context.area
        def birthday = getBirthdayByIdentity(context.auto.identity).format('yyyy-MM-dd')
        def gender = getGenderByIdentity context.auto.identity, ['F', 'M']
        def args = [
            contentType       : JSON,
            path              : _API_PATH_INFO_CONFIRM,
            query              : [
                'address.storeExtractFlag' : '0',
                __xrc                      : context.__xrc,
                'register.idType'          : '01',
                'insured.idType'           : '01',
                'applicant.idType'         : '01',
                'policy.policyFormatType'  : '06',
                'insured.mobile'           : mobile,
                'applicant.mobile'         : mobile,
                'address.mobile'           : mobile,
                'register.mobile'          : mobile,
                'applicant.birthday'       : birthday,
                'insured.birthday'         : birthday,
                'register.birthday'        : birthday,
                'department.provinceName'  : area.id,
                'department.cityName'      : area.name,
                'department.townCityCode'  : area.name,
                'applicant.idNo'           : auto.identity,
                'register.idNo'            : auto.identity,
                'insured.idNo'             : auto.identity,
                'vehicle.engineNo'         : auto.engineNo,
                'flowId'                   : context.flowId,
                'vehicle.frameNo'          : auto.vinNo,
                'register.gender'          : gender,
                'insured.gender'           : gender,
                'applicant.gender'         : gender,
                'address.detail'           : '天畅园2号楼一层',
                'department.cityNameText'  : area.name,
                'insured.name'             : auto.owner,
                'applicant.name'           : auto.owner,
                'address.name'             : auto.owner,
                'register.name'            : auto.owner,
            ]
        ]

        def result = client.get args, { resp, json ->
            json
        }

        if ('C0000' == result.resultCode) {
            log.info '确认信息成功'
            getContinueFSRV result
        } else {
            log.error '确认信息是失败：{}', result
            getFatalErrorFSRV '确认信息是失败'
        }
    }
}
