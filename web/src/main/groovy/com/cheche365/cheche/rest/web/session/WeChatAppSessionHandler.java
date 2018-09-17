package com.cheche365.cheche.rest.web.session;

import com.cheche365.cheche.core.constants.WebConstants;
import com.cheche365.cheche.web.util.ClientTypeUtil;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by zhengwei on 10/19/16.
 * 处理微信小程序session，由于小程序不能传cookie，所以单独定义了header来传session id
 */

@Service
public class WeChatAppSessionHandler {

    public boolean isWechatApp(HttpServletRequest request) {
        return ClientTypeUtil.headerContains(request, WebConstants.WECHAT_APP_HEADER);
    }

    public String get(HttpServletRequest request) {
        return request.getHeader(WebConstants.WECHAT_APP_HEADER);
    }
}
