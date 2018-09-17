package com.cheche365.cheche.gshell.flow.step

import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV



/**
 * 上传身份证反面照片
 */
@Component
@Slf4j
class UploadSFBack extends AUploadSFNew {

    @Override
    protected getImageFile(context) {
        context.imageFile[-1]
    }

    @Override
    protected getFSRV(context, text) {
        if (text) {
            context.imgIdBackUrl = text + '&back'
            log.info '身份证照片背面上传成功'
            getContinueFSRV text
        } else {
            log.error '身份证照片背面上传失败'
            getFatalErrorFSRV '身份证背面上传失败，请重新上传'
        }
    }

}


