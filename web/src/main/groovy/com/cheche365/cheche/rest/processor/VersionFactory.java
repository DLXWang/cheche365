package com.cheche365.cheche.rest.processor;

import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by zhengwei on 7/22/15.
 */

@Service
public class VersionFactory {


    protected boolean after1_0(HttpServletRequest request) {
        String uri = request.getRequestURI().toString();
        if ((uri.startsWith("/v") && !uri.startsWith("/v1/") && !uri.startsWith("/v1.0/"))||uri.startsWith("/partner/")||uri.startsWith("/api/public/")) {
            return true;
        }
        return false;
    }
}
