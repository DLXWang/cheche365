package com.cheche365.cheche.mock.controller

import com.cheche365.cheche.core.service.IConfigService
import com.cheche365.cheche.web.ContextResource
import com.cheche365.cheche.web.counter.annotation.NonProduction
import com.cheche365.cheche.web.service.http.SpringHTTPContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

import static com.cheche365.cheche.common.util.FlowUtils.getEnvPropertyNew
import static com.cheche365.cheche.core.util.MockUrlUtil.findBaseUrl

@RestController
@RequestMapping("/mock/url")
class MockUrlResource extends ContextResource{


    @Autowired
    Environment env

    @Autowired
    IConfigService configService

    @Autowired
    SpringHTTPContext httpContext


    @NonProduction
    @RequestMapping(value = "/{namespace}", method = RequestMethod.POST)
    def addMockUrl(@PathVariable(value = "namespace")String namespace){
        session.setAttribute("mock_base_url", getDomainURL(false))
        httpContext.copySession()
        getCurrentBaseUrl(namespace)

    }

    @NonProduction
    @RequestMapping(value = "/{namespace}", method = RequestMethod.DELETE)
    def removeMockUrl(@PathVariable(value = "namespace")String namespace){
        session.removeAttribute("mock_base_url")
        httpContext.removeSession(session.id)
        getCurrentBaseUrl(namespace)
    }

    @NonProduction
    @RequestMapping(value = "/{namespace}", method = RequestMethod.GET)
    Map getCurrentBaseUrl(@PathVariable(value = "namespace")String namespace){
        def currentBaseUrl = findBaseUrl() ?: getEnvPropertyNew([env: env, configService: configService, namespace: namespace], 'base_url', null, [])
        [
            sessionId:session.id,
            currentBaseURL:currentBaseUrl,
        ]
    }

}
