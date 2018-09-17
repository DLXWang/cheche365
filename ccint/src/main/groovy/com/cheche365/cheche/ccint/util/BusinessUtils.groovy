package com.cheche365.cheche.ccint.util

import com.cheche365.cheche.core.model.MoApplicationLog
import com.cheche365.cheche.core.model.mongo.MongoUser
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import org.apache.http.entity.mime.content.FileBody

import static com.cheche365.cheche.common.util.FlowUtils.getEnvProperty
import static com.cheche365.cheche.core.model.LogType.Enum.CCINT_34
import static groovyx.net.http.Method.POST
import static org.apache.http.entity.mime.HttpMultipartMode.BROWSER_COMPATIBLE
import static org.apache.http.entity.mime.MultipartEntityBuilder.create as createMultiEntityBuilder

/**
 * 工具集
 * Created by Huabin on 2016/6/24.
 */
@Slf4j
class BusinessUtils {

    /**
     * 与合合通信，path须带user和password
     * @param context
     * @param path
     * @param imageFile
     * @return
     */
    static sendAndReceive(context, path, imageFile) {
        context.client.request(POST) { req ->
            uri.path = path
            uri.query = [
                user: getEnvProperty(context, 'ccint.user'),
                password: getEnvProperty(context, 'ccint.password')
                // 请求参数中可以指定encoding，默认为utf8
            ]
            req.entity = createMultiEntityBuilder().setMode(BROWSER_COMPATIBLE).addPart(imageFile.name, new FileBody(imageFile)).build()

            response.success = { resp, reader ->
                new JsonSlurper().parse reader
            }

            response.failure = { resp ->
                log.error '调用合合行驶证识别服务失败'
            }
        }
    }

    static saveApplicationLog(context, logMessage, objId) {
        MoApplicationLog log=new MoApplicationLog(
                createTime: new Date(),
                instanceNo: context.imageFile.name,
                logLevel: 0,
                logMessage: logMessage,
                logType: CCINT_34,
                objId: objId,
                objTable: CCINT_34.name,
                user: context.additionalParameters?.user?.with{
                    MongoUser.toMongoUser(it)
                }
            )
        context.dbService.saveApplicationLog(log)
    }

}
