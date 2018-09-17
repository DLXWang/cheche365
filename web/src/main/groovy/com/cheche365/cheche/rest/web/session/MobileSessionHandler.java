package com.cheche365.cheche.rest.web.session;

import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.model.UUIDMapping;
import com.cheche365.cheche.core.model.User;
import com.cheche365.cheche.core.repository.UuidMappingRepository;
import com.cheche365.cheche.web.service.ChannelAgentService;
import com.cheche365.cheche.core.service.UserLoginInfoService;
import com.cheche365.cheche.core.util.CacheUtil;
import com.cheche365.cheche.core.util.IpUtil;
import com.cheche365.cheche.web.service.UuidMappingService;
import com.cheche365.cheche.web.util.ClientTypeUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.session.Session;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.util.Calendar;

/**
 * Created by zhengwei on 6/10/15.
 * 车车App(iOS Android) session工具类，实现保持App的登录状态，保证App只需要登录一次，session中始终有user，除非显示的logout.
 * 除了车车自己的App，也处理其他有类似需求的客户端，比如将M站嵌入第三方的App中（类似汽车之家）
 */

@Service(value = "MobileSessionHandler")
public class MobileSessionHandler {

    private Logger logger = LoggerFactory.getLogger(MobileSessionHandler.class);

    @Autowired
    private UuidMappingRepository uuidMappingRepository;

    @Autowired
    UserLoginInfoService userLoginInfoService;
    @Autowired
    private ChannelAgentService channelAgentService;
    @Autowired
    UuidMappingService uuidMappingService;


    public String
    get(HttpServletRequest request, Channel channel) {
        String uuid =  ClientTypeUtil.extractUUID(request);

        UUIDMapping mapping = uuidMappingRepository.findFirstByUuidAndClientType(uuid, channel.getName());
        if (null != mapping) {
            return mapping.getSessionId();
        } else {
            return null;
        }

    }

    @Transactional
    public void updateSessionId(HttpServletRequest request, Session session, Channel channel) {
        String uuid =  ClientTypeUtil.extractUUID(request);
        if(uuidMappingService.incorrectUuid(request,uuid)){
            logger.info("app update session fail channelId:{},uuid",channel.getId(),uuid);
            return;
        }
        UUIDMapping mapping = uuidMappingRepository.findFirstByUuidAndClientType(uuid, channel.getName());
        if (null != mapping) {
            mapping.setSessionId(session.getId());
            mapping.setUpdateDate(Calendar.getInstance().getTime());
            uuidMappingRepository.save(mapping);
            if (mapping.isLogged()) {
                CacheUtil.cacheUser(request.getSession(), mapping.getUser());
                ClientTypeUtil.cacheChannel(request, channel);
                channelAgentService.handleChannelAgentCache(request,mapping.getUser(),channel);
            }

        } else {
            logger.debug("there is no uuid mapping record when a new session created for app with uuid: {}, the user is not login yet. ", uuid);
            this.create(uuid, session.getId(), null, channel.getName());

        }

    }

    @Transactional
    public void doLogin(HttpServletRequest request, HttpSession session, User user,Channel channel) {
        String uuid =  ClientTypeUtil.extractUUID(request);
        if(uuidMappingService.incorrectUuid(request,uuid)){
            logger.info("app login uuid error channelId:{},mobile:{},uuid:{}",channel.getId(),user.getMobile(),uuid);
            return;
        }

        UUIDMapping mapping = uuidMappingRepository.findFirstByUuidAndClientType(uuid, channel.getName());
        if (null == mapping) {
            this.create(uuid, session.getId(), user, channel.getName());
        } else {
            this.updateLoginStatus(mapping, session, true, user);
        }
        userLoginInfoService.updateUserLoginInfo(user, IpUtil.getIP(request),ClientTypeUtil.getChannel(request));
    }

    @Transactional
    public void doLogout(HttpServletRequest request, Channel channel) {
        String uuid =  ClientTypeUtil.extractUUID(request);

        UUIDMapping mapping = uuidMappingRepository.findFirstByUuidAndClientType(uuid, channel.getName());
        if (null == mapping) {
            logger.error("fail to updateSessionId the uuid mapping record when app logout, there is no uuid mapping found mobile with uuid: {}, should never happen.", uuid);
            return;
        }
        this.updateLoginStatus(mapping, null, false, null);
    }

    private void create(String uuid, String sessionId, User user, String clientType) {

        UUIDMapping mapping = new UUIDMapping()
            .setSessionId(sessionId)
            .setUpdateDate(Calendar.getInstance().getTime())
            .setUuid(uuid).setUser(user)
            .setClientType(clientType)
            .setLogged(user != null)
            .setLoginDate(Calendar.getInstance().getTime());

        UUIDMapping afterSave = uuidMappingRepository.save(mapping);
        logger.debug("saved a uuid session mapping record, id: {} uuid: {} session id {}", afterSave.getId(), afterSave.getUuid(), afterSave.getSessionId());
    }

    private void updateLoginStatus(UUIDMapping uuidMapping, HttpSession session, boolean isLogin, User user) {
        if (isLogin) {
            uuidMapping.setLogged(true).setLoginDate(Calendar.getInstance().getTime()).setUpdateDate(Calendar.getInstance().getTime()).setSessionId(session.getId()).setUser(user);
            logger.debug("updateSessionId uuid mapping logged status as login");
        } else {
            uuidMapping.setLogged(false).setLogoutDate(Calendar.getInstance().getTime()).setUpdateDate(Calendar.getInstance().getTime());
            logger.debug("updateSessionId uuid mapping logged status as logout");
        }

        this.uuidMappingRepository.save(uuidMapping);
    }

}
