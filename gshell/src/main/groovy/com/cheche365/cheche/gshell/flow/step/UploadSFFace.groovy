package com.cheche365.cheche.gshell.flow.step

import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV



/**
 * 上传身份证正面照片
 */
@Component
@Slf4j
class UploadSFFace extends AUploadSFNew {

    @Override
    protected getImageFile(context) {
        context.imageFile[0]
    }

    @Override
    protected getFSRV(context, text) {
        if (text) {
            context.imgIdFaceUrl = text + '&face'
            log.info '身份证正面照片上传成功'
            getContinueFSRV text
        } else {
            log.error '身份证正面照片上传失败'
            getFatalErrorFSRV '身份证正面上传失败，请重新上传'
        }
    }

}
