package com.cheche365.cheche.cpicuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.CollectionUtils.mergeMaps
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.parser.Constants._SUPPLEMENT_INFO_IMAGE_UPLOAD_TEMPLATE_INSURING
import static com.cheche365.cheche.parser.util.BusinessUtils.getSupplementInfoFSRV
import static com.cheche365.flow.core.util.FlowUtils.getKnownReasonErrorFSRV
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.Method.POST
import static org.apache.http.entity.mime.MultipartEntityBuilder.create as createMultiEntityBuilder



/**
 * 上传影像
 */
@Component
@Slf4j
class UploadImage implements IStep {

    private static final _API_PATH_UPLOAD_IMAGE = '/ecar/fileUpload/uploadMore'

    @Override
    run(context) {
        RESTClient client = context.client
        // 首先得到集成层给了多少个图片
        def files = getFiles context
        if (files) {
            def result = client.request(POST) { req ->
                uri.path = _API_PATH_UPLOAD_IMAGE
                uri.addQueryParam('quotationNo', context.quotationNo)
                uri.addQueryParam('type', '8')  //待数据库脚本维护后，做转换  目前均为其他
                requestContentType = 'image/jpeg'
                contentType = JSON
                req.entity = getFinalEntity(files)
                response.success = { resp, json ->
                    json
                }

                response.failure = { resp ->

                }
            }
            if (result?.message?.code == 'success') {
                log.info '太平洋上传图片成功'
                getContinueFSRV '太平洋上传图片成功'

            } else {
                log.warn '太平洋上传影像失败，原因：{}', result?.message?.message
                getKnownReasonErrorFSRV '太平洋上传影像失败'
            }
        } else {
            log.warn '太平洋上传影像失败，原因： 没有获取到图片'
            return getSupplementInfoFSRV(
                [
                    mergeMaps(_SUPPLEMENT_INFO_IMAGE_UPLOAD_TEMPLATE_INSURING, [meta: [orderNo: context.order.orderNo]])
                ])
        }

    }
    /**
     * 组装
     * @param files
     * @return
     */
    private static getFinalEntity(files) {
        def entityBuilder = createMultiEntityBuilder()
        //能走到这一步说明补充信息里肯定是有东西的
        files.each { file ->
            entityBuilder.addBinaryBody('files', file)
        }
        entityBuilder.build()
    }

    /**
     * 获取文件
     * @param context
     * @return
     */
    private static getFiles(context) {
        def images = context.additionalParameters?.supplementInfo?.images
        images.collect { imgUrl ->
            log.info '影像url：{}', imgUrl
            new URL(imgUrl).withInputStream { is ->
                File.createTempFile('cpicuk-upload-image', '.jpg').with { tmpFile ->
                    tmpFile.withOutputStream { os ->
                        os << is
                    }
                    tmpFile
                }
            }
        }
    }

}
