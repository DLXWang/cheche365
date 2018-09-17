package com.cheche365.cheche.rest.service.pingpp

import com.cheche365.cheche.core.model.LogType
import com.cheche365.cheche.core.model.MoApplicationLog
import com.cheche365.cheche.core.mongodb.repository.MoApplicationLogRepository
import com.cheche365.cheche.core.util.CacheUtil
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * @Author shanxf
 * @Date 2018/1/2  10:06
 */
@Component
@Slf4j
class PingWebhooksServiceFactory {

    @Autowired
    private List<PingWebhooksAbstract> list

    @Autowired
    private MoApplicationLogRepository moApplicationLogRepository

    PingWebhooksAbstract findService(Map event){

        PingWebhooksAbstract pingWebhooksInterface = list.find {it->it.webhooksType(String.valueOf(event.type))}

        if (!pingWebhooksInterface){
            log.info("event:{},temporary ignore",CacheUtil.doJacksonSerialize(event));
            MoApplicationLog moApplicationLog =new MoApplicationLog(
                createTime: Calendar.getInstance().getTime(),
                objTable: event.type,
                logMessage: CacheUtil.doJacksonSerialize(event),
                logType:  LogType.Enum.ORDER_RELATED_3
            )
            moApplicationLogRepository.save(moApplicationLog)

            return null
        }
        return pingWebhooksInterface
    }
}
