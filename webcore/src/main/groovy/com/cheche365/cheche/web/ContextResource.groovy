package com.cheche365.cheche.web

import com.cheche365.cheche.core.constants.WebConstants
import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.BusinessActivity
import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.InternalUser
import com.cheche365.cheche.core.model.User
import com.cheche365.cheche.core.model.agent.ChannelAgent
import com.cheche365.cheche.core.repository.agent.ChannelAgentRepository
import com.cheche365.cheche.web.model.UserCallbackInfo
import com.cheche365.cheche.web.response.RestResponseEnvelope
import com.cheche365.cheche.web.service.UuidMappingService
import com.cheche365.cheche.web.util.ClientTypeUtil
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession

import static com.cheche365.cheche.web.service.http.SessionUtils.USER_RELATED
import static com.cheche365.cheche.web.service.http.SessionUtils.get

/**
 * Created by zhengwei on 4/6/15.
 */
@Slf4j
class ContextResource extends WebConstants {


    @Autowired(required = false)
    public HttpSession session;

    @Autowired(required = false)
    public HttpServletRequest request;

    @Autowired(required = false)
    private UuidMappingService uuidMappingService

    @Autowired
    public ChannelAgentRepository channelAgentRepository

    User currentUser() {
        checkLogin()
        return this.safeGetCurrentUser()
    }

    ChannelAgent currentChannelAgent() {
        checkAgentLogin()
        safeGetCurrentAgent()
    }

    /**
     * 提供给支持userCallback登录的接口（即系统链接进入需要调用的接口）获取登录对象
     * @param objId 能够唯一查询的对象Id
     * @return
     */
    User currentUserWithCallback(objId) {
        UserCallbackInfo userCallbackInfo = safeGetCurrentUserCallback()
        userCallbackInfo && userCallbackInfo.objId == objId ? userCallbackInfo.user : currentUser()
    }

    User safeGetCurrentUser() {
        if (!session.getAttribute(SESSION_KEY_USER) && ClientTypeUtil.isMobileClientType(request)) {
            updateMobileSessionUser()
        }
        get(session, USER_RELATED)
    }

    ChannelAgent safeGetCurrentAgent() {
        if (!session.getAttribute(SESSION_KEY_CHANNEL_AGENT) && ClientTypeUtil.isMobileClientType(request)) {
            updateMobileSessionUser()
        }
        ChannelAgent channelAgent = get(session, SESSION_KEY_CHANNEL_AGENT)
        log.info("find channelAgent:{},current channel:{}",channelAgent?.id,channel.id)
        Channel.findAgentChannel(getChannel().parent) == channelAgent?.channel ? channelAgent : null
    }

    UserCallbackInfo safeGetCurrentUserCallback() {
        get(session, SESSION_KEY_USER_CALLBACK)
    }

    User safeGetCurrentUserWithCallback() {
        safeGetCurrentUser() ?: safeGetCurrentUserCallback()?.user
    }

    InternalUser internalUser() {
        get(session, SESSION_KEY_INTERNAL_USER)
    }

    BusinessActivity businessActivity() {
        get(session, SESSION_KEY_CPS_CHANNEL)
    }


    Channel getChannel() {
        return ClientTypeUtil.getChannel(this.request);
    }

    private void checkLogin() {
        if (USER_RELATED.every { !session.getAttribute(it) }) {
            if (ClientTypeUtil.isMobileClientType(request)) {
                updateMobileSessionUser()
                if (!get(session, USER_RELATED)) {
                    throw new BusinessException(BusinessException.Code.NONE_VALID_SESSION, "无用户信息，请登录。");
                }
            } else {
                throw new BusinessException(BusinessException.Code.NONE_VALID_SESSION, "无用户信息，请登录。");
            }
        }
        if (getChannel().isLevelAgent() && !safeGetCurrentAgent()) {
            throw new BusinessException(BusinessException.Code.NONE_VALID_SESSION, "无用户信息，请登录。")
        }
    }

    private void updateMobileSessionUser() {
        String uuid = ClientTypeUtil.extractUUID(request)
        Channel channel = ClientTypeUtil.getMobileClientTypeByRequest(request)
        uuidMappingService.handlerAppSessionExpire(request, uuid, channel)
    }

    private void checkAgentLogin() {
        log.info("checkAgentLogin session ca :{},ca class:{},ca boolean:{}",session.getAttribute(SESSION_KEY_CHANNEL_AGENT),session.getAttribute(SESSION_KEY_CHANNEL_AGENT)?.class, !session.getAttribute(SESSION_KEY_CHANNEL_AGENT))
        if (!session.getAttribute(SESSION_KEY_CHANNEL_AGENT)) {
            throw new BusinessException(BusinessException.Code.NONE_VALID_SESSION, "无用户信息，请登录。")
        }
    }

    String apiVersion() {
        ClientTypeUtil.getVersion(request)
    }

    boolean simplifiedQuoteSupported() {
        apiVersion() >= 'v1.6'
    }

    static HttpEntity<RestResponseEnvelope> getResponseEntity(Object object = null) {
        return new ResponseEntity(new RestResponseEnvelope(object ?: [:]), HttpStatus.OK);
    }


    static int toPageStart(Integer page) {
        return (page == null || page <= 0) ? 0 : page;

    }

    static int toPageSize(Integer size) {
        return (size == null || size <= 0) ? PAGE_SIZE : size;
    }

    ChannelAgent getCurrentChannelAgent() {
        Channel channel = Channel.findAgentChannel(getChannel());
        return channelAgentRepository.findByUserAndChannel(currentUser(), channel?.parent);
    }
}
