package com.cheche365.cheche.mock.service

import com.cheche365.cheche.core.service.IConfigService
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import groovy.util.logging.Log4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service

import javax.servlet.http.HttpSession

import static com.cheche365.cheche.common.util.FlowUtils.getEnvPropertyNew
import static com.cheche365.cheche.core.util.MockUrlUtil.findBaseUrl

/**
 * Created by liushijie on 2018/6/7.
 */
@Service
@Log4j
class MockCpicukService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate

    @Autowired
    Environment environment

    @Autowired
    IConfigService configService

    private def responseMessage = [:]

    private def messageFileNames = ['_ecar_auth_getCaptchaImage.json','_ecar_auth_queryFastLoginInfo.json','_ecar_j_spring_security_check.json','_ecar_paymentrecord_query.json','_ecar_view_portal_page_common_login.html.json']


    def getMockResponseMessage(){
        if(!responseMessage){
            messageFileNames.each {fileName->
                def message = MockCpicukService.getResourceAsStream("/mock/template/cpicuk/"+fileName).getText('utf-8')
                def messageKey = fileName.substring(0,fileName.lastIndexOf('.'))
                responseMessage.put(messageKey,new JsonSlurper().parseText(message) as Map)
            }
        }
        responseMessage
    }


    def mockDeCaptchaService(){
        new Thread(new Runnable() {
            @Override
            void run() {
                sleep(1000)
                stringRedisTemplate.opsForValue().get('mock-decaptcha-in-07')
                    .with {
                        new JsonSlurper().parseText(it).msg.tid
                    }
                    .with {tid->
                        def redisMessage =  ['msg-type': "decaptcha/recognize", msg: [text: 'tpij', tid: tid]]
                        stringRedisTemplate.convertAndSend('decaptcha-out', new JsonBuilder(redisMessage).toString())
                    }
            }
        }).start()
    }
}
