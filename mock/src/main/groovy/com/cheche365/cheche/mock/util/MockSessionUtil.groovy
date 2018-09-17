package com.cheche365.cheche.mock.util

import com.cheche365.cheche.core.constants.WebConstants
import com.cheche365.cheche.core.service.spi.IHTTPContext

import javax.servlet.http.HttpSession

class MockSessionUtil {

    static void addMockUrl(HttpSession session, IHTTPContext httpContext){
        session.setAttribute("mock_base_url", WebConstants.getDomainURL(false))
        httpContext.copySession()
    }

    static void removeMockUrl(HttpSession session, IHTTPContext httpContext){
        session.removeAttribute("mock_base_url")
        httpContext.removeSession(session.id)
    }

    static void removeMockUrl(String sessionId, IHTTPContext httpContext){
        httpContext.removeSession(sessionId)
    }

}
