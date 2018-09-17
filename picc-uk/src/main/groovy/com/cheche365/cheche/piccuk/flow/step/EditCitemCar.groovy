package com.cheche365.cheche.piccuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.parser.util.BusinessUtils.getHtmlParser
import static groovyx.net.http.ContentType.TEXT
import static groovyx.net.http.ContentType.URLENC
import static com.cheche365.cheche.piccuk.util.BusinessUtils.getValueById

@Component
@Slf4j
class EditCitemCar implements IStep {

    private static final _API_PATH_EDIT_CITEMCAR = '/prpall/business/editCitemCar.do'

    @Override
    Object run(Object context) {

        RESTClient client = context.client
        def renewallMap = context.renewallMap
        def args = [
            requestContentType: URLENC,
            contentType       : TEXT,
            path              : _API_PATH_EDIT_CITEMCAR,
            query             : [
                editType   : 'RENEWAL',
                bizType    : 'PROPOSAL',
                bizNo      : context.bizNo,
                operateDate: renewallMap.get('prpCmain.operateDate'),
                riskCode   : renewallMap.riskCode,
                startDate  : renewallMap.ciStartDate,
                endDate    : renewallMap.ciEndDate,
                startHour  : renewallMap.ciStartHour,
                endHour    : renewallMap.ciEndHour,
            ]
        ]

        def renewalBaseInfo = client.get args, { resp, reader ->
            htmlParser.parse(reader).depthFirst().INPUT
        }

        if (renewalBaseInfo) {
            context.autoModel = getValueById(['prpCitemCar.modelCode'], renewalBaseInfo).getAt('prpCitemCar.modelCode').replaceAll(" ", "")
            context.additionalParameters.supplementInfo?.autoModel = context.additionalParameters.supplementInfo?.autoModel ?:context.autoModel
            log.debug '续保车型： {}', context.autoModel
            getContinueFSRV '获取到 续保车型: ' + context.autoModel
        } else {
            getFatalErrorFSRV '获取续保请求参数异常'
        }

    }

}
