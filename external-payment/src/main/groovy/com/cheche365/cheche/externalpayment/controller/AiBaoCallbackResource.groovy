package com.cheche365.cheche.externalpayment.controller

import com.cheche365.cheche.core.service.IConfigService
import com.cheche365.cheche.web.response.RestResponseEnvelope
import com.cheche365.cheche.web.util.Cryptos
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.http.HttpEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

import javax.servlet.http.HttpServletRequest

import static com.cheche365.cheche.common.util.FlowUtils.getEnvPropertyNew

/**
 * Created by wen on 2018/9/10.
 */
@Controller
@RequestMapping("/api/callback/aibao")
@Slf4j
class AiBaoCallbackResource {

    @Autowired
    Environment env
    @Autowired
    IConfigService configService

    @RequestMapping(value = "", method = RequestMethod.POST)
    HttpEntity<RestResponseEnvelope<Object>> paymentCallback(HttpServletRequest request){
        def param= request.getParameter('param')
        String key = getEnvPropertyNew([env: env, configService: configService, namespace: 'aibao'], 'encrykey', null, [])
        Cryptos.aesDecrypt(param,key)

    }

}
