package com.cheche365.cheche.cpic.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopContinueFSRV
import static groovyx.net.http.ContentType.BINARY
import static org.apache.commons.codec.binary.Base64.encodeBase64String
import static org.apache.commons.io.IOUtils.toByteArray

/**
 * 识别交管验证码加上确认车辆基本信息 目前南京用
 * Created by wangxiafei on 2016-08-16.
 */
@Slf4j
class GetNewCaptcha implements IStep {

    private static final _URL_PATH_GENERATE_CAPTCHA = '/cpiccar/salesNew/businessCollect/generateCaptchaP09'

    @Override
    run(context) {
        def client = context.client
        def auto = context.auto
        def bodyContent = [
            plateNo   : auto.licensePlateNo,
            carVin    : auto.vinNo,
            branchCode: context.branchCode,
            random    : context.baseInfoResult?.random ?: ''
        ]

        def args = [
            contentType: BINARY,
            path       : _URL_PATH_GENERATE_CAPTCHA,
            query      : bodyContent
        ]

        def result = client.get args, { resp, is ->
            encodeBase64String toByteArray(is)
        }

        if (result) {
            log.info '成功获得交管验证码'
            context.imageBase64 = result
            getContinueFSRV result
        } else {
            getLoopContinueFSRV result, '没有获取验证码'
        }
    }
}
