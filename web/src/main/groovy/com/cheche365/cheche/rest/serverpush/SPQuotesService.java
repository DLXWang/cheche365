package com.cheche365.cheche.rest.serverpush;

import static com.cheche365.cheche.rest.serverpush.SPConstants.*;

import com.cheche365.cheche.core.constants.WebConstants;
import com.cheche365.cheche.core.util.URLUtils;
import org.atmosphere.config.service.Disconnect;
import org.atmosphere.config.service.Get;
import org.atmosphere.config.service.ManagedService;
import org.atmosphere.config.service.Ready;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.cpr.BroadcasterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * Websocket生命周期管理类，包括握手、断开连接。
 * 修改resource/META-INF/services/org.atmosphere.cpr.AtmosphereFramework 可以添加/删除Atmosphere提供的interceptor/filter
 * 
 * @author zhengwei
 */
@Component
@ManagedService(path = TOPIC_PATH)
public class SPQuotesService {

    private Logger logger = LoggerFactory.getLogger(SPQuotesService.class);

    @Get
    public void init(AtmosphereResource resource) {

        BroadcasterFactory bf = resource.getAtmosphereConfig().getBroadcasterFactory();
        Broadcaster broadcaster = bf.lookup(CROSS_JVM_BROADCASTER_ID, false);
        broadcaster.addAtmosphereResource(resource);

        String clientId = getClientId(resource.getRequest());
        resource.getRequest().setAttribute(KEY_CLIENT_ID, clientId);
        resource.addBroadcaster(broadcaster);

        resource.getResponse().setCharacterEncoding(UTF_8);
        logger.debug("websocket 初始化完毕，client id {}, uuid {} resource size {}", clientId, resource.uuid(), broadcaster.getAtmosphereResources().size());
    }

    @Ready
    public void onReady(final AtmosphereResource resource) {
    }


   @Disconnect
   public void onDisconnect(AtmosphereResourceEvent event) {
        if (event.isCancelled()) {
            logger.info("Browser " + event.getResource().uuid() + " unexpectedly disconnected");
        } else if (event.isClosedByClient()) {
            event.getResource().removeFromAllBroadcasters();
            logger.info("Browser " + event.getResource().uuid() + " closed the connection");
        }
   }

   private String getClientId(HttpServletRequest request){
       if(null != request.getQueryString()){
           if(request.getQueryString().startsWith("uuid")){
               return URLUtils.splitQuery(request.getQueryString()).get("uuid");
           } else if (request.getQueryString().startsWith(WebConstants.WECHAT_APP_HEADER)){
               return URLUtils.splitQuery(request.getQueryString()).get(WebConstants.WECHAT_APP_HEADER);
           }
       }

       return request.getSession(true).getId();

   }
}
