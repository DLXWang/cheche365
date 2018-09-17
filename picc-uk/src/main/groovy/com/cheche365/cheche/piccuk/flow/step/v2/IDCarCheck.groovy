package com.cheche365.cheche.piccuk.flow.step.v2

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import java.time.LocalDate

import static com.cheche365.cheche.common.Constants._DATETIME_FORMAT3
import static com.cheche365.cheche.common.Constants.get_DATE_FORMAT3
import static com.cheche365.cheche.common.util.ContactUtils.getBirthdayByIdentity
import static com.cheche365.cheche.common.util.ContactUtils.getGenderByIdentity
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.flow.core.util.FlowUtils.getKnownReasonErrorFSRV
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC



/**
 * 身份采集后发送手机验证码
 */
@Component
@Slf4j
class IDCarCheck implements IStep {

    private static final _API_ID_CAR_CHECK = '/prpall/idcard/idCarChekc.do'

    @Override
    Object run(Object context) {
        RESTClient client = context.client
        def applicant = context.order.applicant
        def userMobile = context.extendedAttributes?.verificationMobile ?: applicant.mobile   // 用户手机
        def (bsProposalNo, bzProposalNo) = context.proposalNos.first()
        def order = context.order
        def auto = context.auto
        def insurance = context.insurance
        def compulsoryInsurance = context.compulsoryInsurance

        def applicantId = (insurance ?: compulsoryInsurance ?: order).applicantIdNo ?: auto.identity // 投保人身份证
        def applicantName = (insurance ?: compulsoryInsurance ?: order).applicantName ?: auto.owner // 投保人姓名
        def now = LocalDate.now()
        //TODO:整理 参数
        def args = [
            requestContentType: URLENC,
            contentType       : JSON,
            path              : _API_ID_CAR_CHECK,
            body              : [
                proposalNoBI                        : bsProposalNo,  //商业险单号
                proposalNoCI                        : bzProposalNo,
                'isNetProp'                         : '1',
                'NETPPROP_SWITCH'                   : '1',
                'checkStatus'                       : '1',
                'agentCode'                         : context.baseInfo?.agentCode,
                'idCardCheckInfos[0].mobile'        : userMobile,
                'idCardCheckInfos[0].id'            : '19874907',
                'idCardCheckInfos[0].businessNature': '3',
                'idCardCheckInfos[0].agentcode'     : context.baseInfo?.agentCode,
                'idCardCheckInfos[0].insuredcode'   : '1100100005551042',
                'idCardCheckInfos[0].samCode'       : '', //context.samCode
                'idCardCheckInfos[0].samType'       : '',
                'idCardCheckInfos[0].validFlag'     : '1',
                'idCardCheckInfos[0].flag'          : '1',
                'idCardCheckInfos[0].checkCodeDate' : '',
                'idCardCheckInfos[0].checkStatus'   : '1',
                'idCardCheckInfos[0].insuredFlag'   : '11',
                'idCardCheckInfos[0].name'          : applicantName,
                'sex_Des[0]'                        : getGenderByIdentity(applicantId, ['男性', '女性']),
                'idCardCheckInfos[0].sex'           : getGenderByIdentity(applicantId, ['1', '2']),
                'nationDes[0]'                      : '汉族',
                'idCardCheckInfos[0].nation'        : '01',
                'idCardCheckInfos[0].birthday'      : _DATE_FORMAT3.format(getBirthdayByIdentity(applicantId)),
                'idCardCheckInfos[0].address'       : context.area.name,
                'idCardCheckInfos[0].idcardType'    : '01',
                'idCardCheckInfos[0].idcardCode'    : applicantId,
                'idCardCheckInfos[0].issure'        : '签发机关',
                'idCardCheckInfos[0].validStartDate': _DATETIME_FORMAT3.format(now), // 发证日期
                'idCardCheckInfos[0].validEndDate'  : _DATETIME_FORMAT3.format(now.plusYears(10)), // 有效日期
                'hidden_index_idCardCheckInfo'      : '1',
                'hidden_index_idInfoOffLine'        : '0'
            ]
        ]

        log.debug 'args {}', args
        def result = client.post args, { resp, json ->
            json
        }


        if (0 == result.totalRecords && result.msg.contains('告知短信已发送')) {
            context.proposalStatus = '身份采集验证码发送成功'
            log.debug '身份采集验证码发送成功 ： {}', result.msg
            getContinueFSRV result
        } else {
            log.debug '身份采集验证码发送失败：{}', result.msg
            context.proposalStatus = '身份采集验证码发送失败'
            getKnownReasonErrorFSRV '身份采集验证码发送失败'
        }
    }
}
