package com.cheche365.cheche.rest.web.session;

import com.cheche365.cheche.core.constants.WebConstants;
import com.cheche365.cheche.web.util.ClientTypeUtil;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class ClaimAppSessionHandler {

    public boolean isClaimApp(HttpServletRequest request) {
        return ClientTypeUtil.headerContains(request, WebConstants.CLAIM_APP_HEADER);
    }

    public String get(HttpServletRequest request) {
        return request.getHeader(WebConstants.CLAIM_APP_HEADER);
    }

}
