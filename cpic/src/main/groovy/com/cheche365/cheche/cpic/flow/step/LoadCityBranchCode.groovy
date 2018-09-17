package com.cheche365.cheche.cpic.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getLoopBreakFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopContinueFSRV
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC



/**
 * 初始化基本信息提交表单
 * Created by houjinxin on 2015/5/21.
 */
@Component
@Slf4j
class LoadCityBranchCode implements IStep {

    private static final _URL_PATH_LOADCITYBRANCHCODE = 'cpiccar/salesNew/businessCollect/loadCityBranchCode'

    @Override
    run(context) {
        RESTClient client = context.client
        def provinceCode = context.provinceCode
        def cityCode = context.cityCode
        def bodyContent = [
            provinceCode: provinceCode,
            cityCode    : cityCode,
            otherSource : '',
            customType  : ''
        ]
        def args = [
            requestContentType: URLENC,
            contentType       : JSON,
            path              : _URL_PATH_LOADCITYBRANCHCODE,
            body              : bodyContent
        ]

        def result = null
        try {
            result = client.post args, { resp, json ->
                json
            }
        } catch (ex) {
            log.warn '获取城市信息非预期异常：{}。稍后重试', ex.message
            getLoopContinueFSRV null, '无法报价'
        }


        if (result?.branchCode) {
            log.info "地区代码：provinceCode=${provinceCode},cityCode=${cityCode},branchCode=${result.branchCode},orgdeptCode=${result.orgdeptcode}"
            context.branchCode = result.branchCode
            context.orgdeptCode = result.orgdeptcode
            getLoopBreakFSRV result
        } else {
            getLoopContinueFSRV null, '获取branchCode和orgdeptCode失败'
        }
    }

}
