package com.cheche365.cheche.gshell.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import org.apache.http.entity.mime.content.FileBody
import org.springframework.stereotype.Component

import static groovyx.net.http.ContentType.TEXT
import static groovyx.net.http.Method.POST
import static org.apache.http.entity.mime.HttpMultipartMode.BROWSER_COMPATIBLE
import static org.apache.http.entity.mime.MultipartEntityBuilder.create as createMultiEntityBuilder



/**
 * 上传身份证照片
 */
@Component
@Slf4j
abstract class AUploadSFNew implements IStep {

    private static final _API_PATH_UPLOAD_SF_NEW = '/uploadP/uploadSFNew'

    @Override
    run(context) {
        def client = context.client
        def imageFile = getImageFile(context)

        def text = client.request(POST) { req ->
            uri.path = _API_PATH_UPLOAD_SF_NEW
            contentType = TEXT
            req.entity = createMultiEntityBuilder().setMode(BROWSER_COMPATIBLE).addPart('file', new FileBody(imageFile)).addTextBody('FileName', imageFile.name).addTextBody('Upload', 'Submit Query').build()

            response.success = { resp, text ->
                text.readLine()
            }

            response.failure = { resp ->

            }
        }
        getFSRV(context, text)
    }

    protected abstract getFSRV(context, text)

    protected abstract getImageFile(context)
}
