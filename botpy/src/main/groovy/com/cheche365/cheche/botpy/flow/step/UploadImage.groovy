package com.cheche365.cheche.botpy.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import sun.misc.BASE64Encoder

import static com.cheche365.cheche.botpy.util.BusinessUtils.sendParamsAndReceive
import static com.cheche365.cheche.common.util.CollectionUtils.mergeMaps
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.parser.Constants._SUPPLEMENT_INFO_IMAGE_UPLOAD_TEMPLATE_INSURING
import static com.cheche365.cheche.parser.util.BusinessUtils.getSupplementInfoFSRV
import static groovyx.net.http.Method.PUT



/**
 * 上传影像
 */
@Slf4j
class UploadImage implements IStep {

    private static final _API_PATH_CHECK_RENEWAL = "/proposals/"

    @Override
    run(context) {

        //如果需要上传影像但是没有传递影像，推补充信息
        if (!context.additionalParameters.supplementInfo.images) {

            return getSupplementInfoFSRV(
                [
                    mergeMaps(_SUPPLEMENT_INFO_IMAGE_UPLOAD_TEMPLATE_INSURING, [meta: [orderNo: context.order.orderNo]])
                ])
        }

        def proposal_id = context.proposal_id

        //首先得到集成层给了多少个图片
        def images = getImagesBase64 context

        def num = 0
        //能走到这一步说明补充信息里肯定是有东西的
        while (num < images.size()) {
            def param = getRequestParams 'O1~O10', images[num], '其他'
            num++
            def result = sendParamsAndReceive context, _API_PATH_CHECK_RENEWAL + proposal_id + '/photos', param, PUT, log
            if (result.error) {
                log.warn '金斗云上传影像失败，原因：{}', result.error
                return getSupplementInfoFSRV(
                    [
                        mergeMaps(_SUPPLEMENT_INFO_IMAGE_UPLOAD_TEMPLATE_INSURING, [meta: [orderNo: context.order.orderNo]])
                    ])
            }
        }

        log.info '金斗云上传图片成功'
        getContinueFSRV '金斗云上传图片成功'
    }

    private static getRequestParams(imageType, images, name) {

        [
            code          : imageType, //证件类型 身份证正面
            content_base64: images,
            name          : name, //证件名称 被保人身份证正面照
            content_type  : 'image/png'
        ]
    }

    private static getImagesBase64(context) {
        context.additionalParameters.supplementInfo.images.collect { imgUrl ->
            log.info '影像url：{}', imgUrl
            new BASE64Encoder().encode(new URL(imgUrl).bytes)
        }
    }


}
