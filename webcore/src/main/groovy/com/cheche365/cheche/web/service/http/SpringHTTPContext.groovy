package com.cheche365.cheche.web.service.http

import com.cheche365.cheche.core.service.spi.IHTTPContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import javax.servlet.http.HttpSession

/**
 * Created by zhengwei on 21/11/2017.
 */
@Service
class SpringHTTPContext implements IHTTPContext {

    @Autowired(required = false)
    public HttpSession session;

    private static final SESSIONS = [:]

    @Override
    HttpSession currentSession() {
        return session
    }

    @Override
    Map currentSession(Object id) {
        SESSIONS.get(id)
    }

    @Override
    void copySession(){
        SESSIONS.put(session.id, dumpToMap())
    }

    @Override
    void removeSession(Object id) {
        SESSIONS.remove(id)
    }

    @Override
    int sessionSize() {
        SESSIONS.size()
    }

    def dumpToMap() {
        def attrs = [:]
        session.attributeNames?.each { attrName->
            attrs.put(attrName , session.getAttribute(attrName))
        }
        return attrs
    }


    def clearSession(){
        SESSIONS.clear()
    }
}
