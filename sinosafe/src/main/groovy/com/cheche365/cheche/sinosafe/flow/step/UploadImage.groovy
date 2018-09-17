package com.cheche365.cheche.sinosafe.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import sun.misc.BASE64Encoder

import static com.cheche365.cheche.common.util.CollectionUtils.mergeMaps
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.parser.Constants._SUPPLEMENT_INFO_IMAGE_UPLOAD_TEMPLATE_INSURING
import static com.cheche365.cheche.parser.util.BusinessUtils.getSupplementInfoFSRV
import static com.cheche365.cheche.sinosafe.util.BusinessUtils.createRequestParams
import static com.cheche365.cheche.sinosafe.util.BusinessUtils.sendAndReceive2Map



/**
 * 影像上传接口
 */
@Slf4j
class UploadImage implements IStep {

    private static final _TRAN_CODE = 100035

    @Override
    run(context) {
        if (!context.isUpdateImages && !context.uploadingImages) {
            return getContinueFSRV('无需上传影像')
        }

        def images = context.additionalParameters?.supplementInfo?.images ?: context.uploadingImages
        if (!images) {
            return getSupplementInfoFSRV(
                [mergeMaps(_SUPPLEMENT_INFO_IMAGE_UPLOAD_TEMPLATE_INSURING, [meta: [orderNo: context.order?.orderNo, errorMessage: context.updateImagesErrorMessage]])])
        }

        def result = sendAndReceive2Map(context, getRequestParams(context, images), log)
        def head = result.PACKET.HEAD
        if ('C00000000' == head.RESPONSECODE) {
            getContinueFSRV '影像上传成功'
        } else {
            getFatalErrorFSRV head.ERRORMESSAGE
        }
    }

    private static getRequestParams(context, images) {
        def body = [
            BASE: [
                CAL_APP_NO: context.CAL_APP_NO, // 报价单号
                IMG_LIST  : [
                    IMG_DATA: getImageData(images)
                ]
            ]
        ]
        createRequestParams context, _TRAN_CODE, body
    }

    private static getImageData(images) {
        log.info '影像urls：{}', images
        images.collect { imgUrl ->
            [IMG_INFO: new BASE64Encoder().encode(new URL(imgUrl).bytes)]
        }
    }

}
