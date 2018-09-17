package com.cheche365.cheche.picc.flow.step

import com.cheche365.cheche.common.flow.IStep
import com.cheche365.cheche.common.util.DateUtils
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.DateUtils.getLocalDate
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.parser.Constants._DATE_FORMAT5
import static com.cheche365.cheche.parser.Constants._DATETIME_FORMAT1
import static groovyx.net.http.ContentType.JSON

/**
 * 获取续保信息
 */
@Component
@Slf4j
class GetRenewalInfo implements IStep {

    private static final _API_PATH_GET_RENEWAL_INFO = '/ecar/renewal/getRenewalInfo'


    @Override
    run(context) {
        RESTClient client = context.client

        def args = [
            contentType : JSON,
            path        : _API_PATH_GET_RENEWAL_INFO,
            query       : [
                uniqueID        : context.uniqueID
            ]
        ]

        def renewalInfo = client.get args, { resp, json ->
            json
        }

        log.debug '续保信息：{}', renewalInfo
        context.renewalInfo = renewalInfo
        // 获取交强险起止日期
        def bzStartDate =  getLocalDate(_DATE_FORMAT5.parse(renewalInfo.startDateCI))
        context.bzStartDateText = _DATETIME_FORMAT1.format bzStartDate
        context.bzEndDateText = _DATETIME_FORMAT1.format bzStartDate.plusYears(1).minusDays(1)

        getContinueFSRV renewalInfo
    }

}
