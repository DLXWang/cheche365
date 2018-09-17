package com.cheche365.cheche.piccuk.flow.step.v3

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component
import sun.misc.BASE64Encoder

import static com.cheche365.cheche.common.util.CollectionUtils.mergeMaps
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.parser.Constants._SUPPLEMENT_INFO_IMAGE_UPLOAD_TEMPLATE_INSURING
import static com.cheche365.cheche.parser.util.BusinessUtils.getHtmlParser
import static com.cheche365.cheche.parser.util.BusinessUtils.getSupplementInfoFSRV
import static com.cheche365.flow.core.util.FlowUtils.getKnownReasonErrorFSRV
import static groovyx.net.http.ContentType.TEXT
import static groovyx.net.http.ContentType.URLENC



/**
 * 发起影像上传
 */
@Component
@Slf4j
class GoToFileUpload implements IStep {

    private static final _URL_PATH_GO_TO_FILE_UPLOAD = '/prpall/upload/fileUpload.do'

    @Override
    run(context) {
        // 首先确保得到集成层传输的图片
        def files = getFiles context
        if (!files) {
            log.debug '没有发现图片，初始化影像平台失败'
            return getSupplementInfoFSRV(
                [
                    mergeMaps(_SUPPLEMENT_INFO_IMAGE_UPLOAD_TEMPLATE_INSURING, [meta: [orderNo: context.order.orderNo]])
                ])
        } else {
            context.batchImages = getImagesBase64 context
        }

        RESTClient client = context.client
        def (bsProposalNo, bzProposalNo) = context.proposalNos.first()
        def args = [
            requestContentType: URLENC,
            contentType       : TEXT,
            path              : _URL_PATH_GO_TO_FILE_UPLOAD,
            query             : [
                opreateType : 'add',
                businessNo  : bsProposalNo ?: '',
                businessNoCI: bzProposalNo ?: '',
                businessType: 'prpol',
                bizType     : 'PROPOSAL'
            ]
        ]

        def result = client.get args, { resp, html ->
            def response = htmlParser.parse(html)
            response
        }

        processResult result.depthFirst(), context
    }


    private processResult(resultNodeList, context) {
        def sunECMUrl = resultNodeList.INPUT.find {
            'url' == it.@name
        }?.@value
        def opreateType = resultNodeList.INPUT.find {
            'opreateType' == it.@name
        }?.@value //add
        def businessNo = resultNodeList.INPUT.find {
            'businessNo' == it.@name
        }?.@value
        def applyNo = resultNodeList.INPUT.find {
            'applyNo' == it.@name
        }?.@value
        context.sunECMUrl = sunECMUrl
        context.sunECMOpreateType = opreateType
        context.sunECMBusinessNo = businessNo
        context.sunECMApplyNo = applyNo
        if (sunECMUrl) {
            context.sunECMXml = sunECMUrl.substring(63)
            def customerIDPatten = "customerID%3E(.*)%3C%2FcustomerID"
            def m = sunECMUrl =~ /$customerIDPatten/
            if (m.find()) {
                context.ECMCustomerID = m[0][1]
            }
            getContinueFSRV '进行影像上传'
        } else {
            getKnownReasonErrorFSRV('获取人保影像上传系统的url失败')
        }
    }

    private static getImagesBase64(context) {
        context.pageUrlList = []
        context.additionalParameters.supplementInfo.images.collect { imgUrl ->
            def filename = imgUrl.split('/')[-1]
            context.pageUrlList << filename
            [
                name       : filename,
                imageBase64: new BASE64Encoder().encode(new URL(imgUrl).bytes)
            ]
        }
    }

    /**
     * 获取文件
     * @param context
     * @return
     */
    private static getFiles(context) {
        def images = context.additionalParameters?.supplementInfo?.images
        images.collect { imgUrl ->
            new URL(imgUrl).withInputStream { is ->
                File.createTempFile('piccuk-upload-image', '.jpg').with { tmpFile ->
                    tmpFile.withOutputStream { os ->
                        os << is
                    }
                    tmpFile
                }
            }
        }
    }

}
