package com.cheche365.cheche.rest.web.controller;

import com.cheche365.cheche.core.exception.BusinessException;
import com.cheche365.cheche.core.model.InternalUser;
import com.cheche365.cheche.core.model.User;
import com.cheche365.cheche.web.service.ChannelAgentService;
import com.cheche365.cheche.core.service.IInternalUserService;
import com.cheche365.cheche.core.service.UserService;
import com.cheche365.cheche.core.util.CacheUtil;
import com.cheche365.cheche.web.ContextResource;
import com.cheche365.cheche.web.response.RestResponseEnvelope;
import com.cheche365.cheche.web.service.http.SessionUtils;
import com.cheche365.cheche.web.service.system.ChannelAgentInviteURL;
import com.cheche365.cheche.web.util.ClientTypeUtil;
import com.cheche365.cheche.web.version.VersionedResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

/**
 * Created by zhengwei on 12/10/15.
 *
 * 与车车用户无关的User相关服务，比如出单中心的需求。API的URL和 {@link UsersResource}一致，只是为了方便管理代码。
 */

@RestController
@RequestMapping("/" + ContextResource.VERSION_NO + "/users")
@VersionedResource(from = "1.0")
public class UsersThirdPartyResource extends ContextResource {

    private static Logger logger = LoggerFactory.getLogger(UsersThirdPartyResource.class);
    @Autowired
    public UserService userService;

    @Autowired
    private IInternalUserService internalUserService;

    @Autowired
    private ChannelAgentService channelAgentService;



    @RequestMapping(value = "/login/internal", method = RequestMethod.POST)
    public HttpEntity<RestResponseEnvelope> internalLogin(HttpServletRequest request, @RequestParam(required = true) String email, @RequestParam(required = true) String password, @RequestParam(required = true) Long userId) {
        request.getSession().invalidate();
        InternalUser internalUser = internalUserService.findByEmailAndPassword(email, password);
        if (internalUser == null) {
            throw new BusinessException(BusinessException.Code.OBJECT_NOT_EXIST, "用户" + email + ":校验错误");
        }
        User user = userService.getUserById(userId);
        if (user == null) {
            throw new BusinessException(BusinessException.Code.OBJECT_NOT_EXIST, "用户:" + userId + " 不存在");
        }
        if (!Boolean.TRUE.equals(user.isBound())) {
            throw new BusinessException(BusinessException.Code.NONE_VALID_SESSION, "用户:" + userId + " 未绑定手机号");
        }
        session.setAttribute(SESSION_KEY_INTERNAL_USER, CacheUtil.doJacksonSerialize(internalUser));
        session.setAttribute(SESSION_KEY_IMPERSONATION_USER, CacheUtil.doJacksonSerialize(user));
        ClientTypeUtil.cacheChannel(request);
        channelAgentService.handleChannelAgentCache(request,user,getChannel().getParent());
        HashMap hashMap = new HashMap(){{
            put("result","success");
            put("token",request.getSession().getId());
        }};
        return getResponseEntity(hashMap);
    }

    /**
     * 当前登录客服用户信息
     */
    @RequestMapping(value = "/login/internal", method = RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope> internalLoginInfo() {
        InternalUser internalUser = internalUser();
        return getResponseEntity(internalUser);
    }

    /**
     * 当前模拟登陆用户信息
     */
    @RequestMapping(value = "/login/impersonation", method = RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope> impersonationLogin() {
        return getResponseEntity(SessionUtils.get(session, SESSION_KEY_IMPERSONATION_USER));
    }

}
