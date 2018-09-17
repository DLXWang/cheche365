package com.cheche365.cheche.cpicuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import java.time.LocalDate

import static com.cheche365.cheche.common.Constants._DATETIME_FORMAT3
import static com.cheche365.cheche.common.Constants._DATE_FORMAT2
import static com.cheche365.cheche.common.Constants._DATE_FORMAT3
import static com.cheche365.cheche.common.Constants._DATE_FORMAT6
import static com.cheche365.cheche.common.util.ContactUtils._MALE
import static com.cheche365.cheche.common.util.ContactUtils.getGenderByIdentity
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopContinueFSRV
import static com.cheche365.cheche.cpicuk.util.BusinessUtils.getIDExpireDate
import static groovyx.net.http.ContentType.JSON



/**
 * Created by chukh on 2018/5/17.
 * 更新手机?   上传身份信息前步骤
 */
@Component
@Slf4j
class IDCardUpdatePhone implements IStep {

    private static final _API_PATH_ID_UPDATE = '/ecar/collect/IDCardUpdatePhone'

    @Override
    Object run(Object context) {
        def machineCode = context.samCode
        def holderVo = context.queryIdentityInformation.holderVo
        def identity = holderVo?.certiCode ?: (context.insurance ?: context.compulsoryInsurance ?: context.order)?.applicantIdNo ?: context.auto.identity
        def name = holderVo?.name ?: (context.insurance ?: context.compulsoryInsurance ?: context.order)?.applicantName ?: context.auto.owner
        def now = LocalDate.now()
        def personInfo = [
            address       : '北京市东城区大取灯胡同2号',
            //从身份查询来的birthDate格式：1990年01月01日，需要转换为1990-01-01
            birthDate     : holderVo?.birthDate ? _DATE_FORMAT3.format(_DATE_FORMAT6.parse(holderVo?.birthDate)) : _DATE_FORMAT3.format(_DATE_FORMAT2.parse(identity[6..13])),
            certiCode     : identity,
            customerType  : '2', //身份证
            certiEndDate  : holderVo?.certiEndDate ? getIDExpireDate(holderVo.certiEndDate) : _DATETIME_FORMAT3.format(now), // 有效日期
            certiStartDate: holderVo?.certiStartDate ? getIDExpireDate(holderVo.certiStartDate) : _DATETIME_FORMAT3.format(now.plusYears(10)), // 发证日期
            gender        : _MALE == getGenderByIdentity(identity) ? '1' : '2',
            issuer        : '北京市公安局东城分局',
            name          : name,
            nation        : '汉',
            telephone     : holderVo.telephone
        ]

        def newHolderVo = personInfo + [type: 1]
        def newInsureVo = personInfo + [type: 2]

        def updateArgsBody = [
            meta  : [:],
            redata: [
                type       : 1,
                machineCode: machineCode,
                quotationNo: context.quotationNo,
                holderVo   : newHolderVo,
                insureVo   : newInsureVo
            ]
        ]
        context.updateArgsBody = updateArgsBody
        RESTClient client = context.client
        def args = [
            path              : _API_PATH_ID_UPDATE,
            requestContentType: JSON,
            contentType       : JSON,
            body              : updateArgsBody
        ]
        log.debug '请求体：\n{}', args.body
        def result = client.post args, { resp, json ->
            json
        }
        log.debug '更新身份信息：{}', result
        def code = result.message.code
        def message = result.message.message
        if (code == 'success') {
            //更新保单中投保人手机，也是接收验证码的手机号
            context.holderTelephone = holderVo.telephone
            log.debug '更新身份信息成功', message
            getContinueFSRV null
        } else {
            log.error '更新失败，太平洋返回的提示信息为：{}', message
            getLoopContinueFSRV null, message
        }
    }
}
