package com.cheche365.cheche.bihu.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.bihu.Constants._INSURANCE_COMPANY_MAPPING
import static com.cheche365.cheche.bihu.util.BusinessUtils.getCurrentInsuranceCompany
import static com.cheche365.cheche.bihu.util.BusinessUtils.sendAndReceive
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV

/**
 * 获取核保信息
 */
@Component
@Slf4j
class GetSubmitInfo implements IStep {

    private static final _API_PATH_GET_SUBMIT_INFO = '/api/CarInsurance/GetSubmitInfo'

    @Override
    run(context) {

        def auto = context.auto
        def queryBody = [
            LicenseNo     : auto.licensePlateNo,
            SubmitGroup   : _INSURANCE_COMPANY_MAPPING[getCurrentInsuranceCompany(context)],
            RenewalCarType: 0
        ]

        def result = sendAndReceive context, _API_PATH_GET_SUBMIT_INFO, queryBody, this.class.name

        if (1 == result.BusinessStatus) {
            def bsNo = result.Item?.BizNo
            def bzNo = result.Item?.ForceNo
            log.info '获取核保成功，bsNo:{},bzNo:{}', bsNo, bzNo

            getContinueFSRV result
        } else {
            log.error "壁虎请求核保异常状态码：{}，详细信息：{}", result.BusinessStatus, result.Item.QuoteResult ?: result.StatusMessage
            getFatalErrorFSRV result.StatusMessage
        }
    }

}
