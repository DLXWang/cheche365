package com.cheche365.cheche.baoxian.flow.step

import com.cheche365.cheche.baoxian.flow.step.v2.ABaoXianCommonStep
import groovy.transform.InheritConstructors
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV

/**
 * 影像上传，影像大小限制 1M 以内
 * @author taicw
 */
@Component
@Slf4j
@InheritConstructors(
    constructorAnnotations = true,
    parameterAnnotations = true
)
class UploadImage extends ABaoXianCommonStep {

    private static final _API_PATH_UPLOAD_IMAGE = '/uploadImage'


    @Override
    run(context) {
        def images = context.additionalParameters?.supplementInfo?.images

        def params = [
            taskId    : context.taskId,
            imageInfos: images?.collect { imageUrl ->
                [
                    imageMode: imageUrl.tokenize('.').last(),
                    imageUrl : imageUrl
                ]
            }
        ]

        log.info "上传图片步骤参数：${params}"

        def result = send context,prefix + _API_PATH_UPLOAD_IMAGE, params

        log.info "上传图片步骤响应：${result}"

        if ('0' == result.code || '00' == result.respCode){
            log.info '影像上传成功'
            getContinueFSRV '影像上传成功'
        } else {
            log.error '影像上传失败'
            getFatalErrorFSRV '影像上传失败'
        }
    }
}
