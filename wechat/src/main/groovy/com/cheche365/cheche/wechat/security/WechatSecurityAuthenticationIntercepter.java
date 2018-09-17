package com.cheche365.cheche.wechat.security;

import com.cheche365.cheche.core.constants.WebConstants;
import com.cheche365.cheche.core.exception.BusinessException;
import com.cheche365.cheche.core.model.User;
import com.cheche365.cheche.web.util.ClientTypeUtil;
import com.cheche365.cheche.core.model.WechatUserChannel;
import com.cheche365.cheche.core.repository.WechatUserChannelRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 将用户注入到Spring Security中
 * Created by sunhuazhong on 2015/3/3.
 */
public class WechatSecurityAuthenticationIntercepter extends HandlerInterceptorAdapter {

    private Logger logger = LoggerFactory.getLogger(WechatSecurityAuthenticationIntercepter.class);

    @Autowired
    private WechatUserChannelRepository wechatUserChannelRepository;

    @Override
    public void afterCompletion(
        HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
        throws Exception {
        if(ex != null) {
            if(logger.isDebugEnabled()) {
                logger.debug("request throw exception.");
            }

            return;
        }

        // 只拦截以下两类生成认证，其他的放行
        String requestURI = request.getRequestURI();
        if(!"/web/proposal".equals(requestURI) && !"/web/order".equals(requestURI)) {
            if(logger.isDebugEnabled()) {
                logger.debug("only intercepter uri:/web/proposal and /web/order.");
            }

            return;
        }

        // 获取请求Session
        HttpSession session = request.getSession(true);

        // 获取微信用户Json对象
        String wechatUserJson = (String) session.getAttribute(WebConstants.SESSION_KEY_USER);
        if(StringUtils.isBlank(wechatUserJson)) {
            if(logger.isDebugEnabled()) {
                logger.debug("the user in session is empty.");
            }

            return;
        }

        if(logger.isDebugEnabled()) {
            logger.debug("register wechat user into spring security is starting...");
        }

        // 反序列化微信用户
        User wechatUser = doJacksonDeserialize(wechatUserJson);


//        // 根据用户得到微信用户
        WechatUserChannel userChannel = wechatUserChannelRepository.findByUserChannel(wechatUser, ClientTypeUtil.getChannel(request));

        if(SecurityContextHolder.getContext() == null
            || SecurityContextHolder.getContext().getAuthentication() == null
            || (null!=userChannel && !userChannel.getOpenId().equals(SecurityContextHolder.getContext().getAuthentication().getPrincipal()))) {
            // 用户名
            String username = userChannel.getOpenId();
            // 密码
            String password = "";
            // 权限
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

            UserDetails user = new org.springframework.security.core.userdetails.User(username, password, authorities);

            //根据userDetails构建新的Authentication
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), user.getAuthorities());

            //设置authentication中details
            usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetails(request));

            //存放usernamePasswordAuthenticationToken到SecurityContextHolder
            SecurityContext context = new SecurityContextImpl();
            context.setAuthentication(usernamePasswordAuthenticationToken);
            SecurityContextHolder.setContext(context);

            //在session中存放security context,方便同一个session中控制用户的其他操作
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
        }
    }

    private User doJacksonDeserialize(String json){
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, User.class);
        } catch (IOException e) {
            logger.error("Fail to deserialize the json with Jackson: " + json);
            throw new BusinessException(BusinessException.Code.INTERNAL_SERVICE_ERROR, "Errors when deserialize json");
        }
    }
}
