package com.cheche365.cheche.huanong.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j

import static com.cheche365.cheche.common.util.CollectionUtils.mergeMaps
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.huanong.flow.Constants._SUCCESS
import static com.cheche365.cheche.huanong.util.BusinessUtils.createRequestParams
import static com.cheche365.cheche.huanong.util.BusinessUtils.sendParamsAndReceive
import static com.cheche365.cheche.parser.Constants._SUPPLEMENT_INFO_IMAGE_UPLOAD_TEMPLATE_INSURING
import static com.cheche365.cheche.parser.util.BusinessUtils.getSupplementInfoFSRV



/**
 * 图像上传
 * Created by LIU GUO on 2018/8/13.
 */
@Slf4j
class ImageUpload implements IStep {

    private static final _TRAN_CODE = 'UploadImage'//华农上传图片

    @Override
    run(context) {
        //如果需要上传影像但是没有传递影像，推补充信息
        if (!context.additionalParameters.supplementInfo.images) {
            return getSupplementInfoFSRV(
                [
                    mergeMaps(_SUPPLEMENT_INFO_IMAGE_UPLOAD_TEMPLATE_INSURING, [meta: [orderNo: context.order.orderNo]])
                ])
        }

        //首先得到集成层给了多少个图片
        def images = getImagesBase64 context.additionalParameters.supplementInfo.images
        //能走到这一步说明补充信息里肯定是有东西的
        def params = getRequestParams context, images

        def result = sendParamsAndReceive context, params, '影像上传', log

        if (result.head.responseCode == _SUCCESS) {
            log.info '华农上传图片成功'
            getContinueFSRV '华农上传图片成功'
        } else {
            log.warn '华农上传影像失败，原因：{}', result.error
            getSupplementInfoFSRV(
                [
                    mergeMaps(_SUPPLEMENT_INFO_IMAGE_UPLOAD_TEMPLATE_INSURING, [meta: [orderNo: context.order.orderNo]])
                ])
        }

    }

    private static getRequestParams(context, images) {
        def imageBeanList = images.collect {
            [
                fileName: it.imageName,
                image   : it.imageBytes,
                remark  : '',
            ]
        }
        def requestParam = [
            contractNo  : context.imageUploadId, //业务号
            nodeBeanList: [
                [
                    id           : 'CG022',//其他资料
                    imageBeanList: imageBeanList,
                ]
            ]

        ]
        createRequestParams context, _TRAN_CODE, requestParam

    }

    private static getImagesBase64(images) {
        images.collect { imgUrl ->
            log.info '影像url：{}', imgUrl
            def url = new URL(imgUrl)
            [
                imageBytes: url.bytes,
                imageName : new File(url.file).name,
            ]
        }
    }

}
