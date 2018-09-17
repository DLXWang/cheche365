package com.cheche365.cheche.core.util

import com.cheche365.cheche.core.context.ApplicationContextHolder
import com.cheche365.cheche.core.service.spi.IHTTPContext
import groovy.util.logging.Slf4j

/**
 * Created by zhengwei on 30/11/2017.
 */

@Slf4j
class MockUrlUtil {

    public static final String MOCK_BASE_URL = 'mock_base_url'
    public static final String MOCK_SERVER_KEYPAIR = 'mock_server_keypair'

    static findMockSessionAttribute(String key){
        if(!RuntimeUtil.isProductionEnv()){
            try{
                findHTTPContext().currentSession().getAttribute(key)
            } catch (Exception e){
                log.warn('跨线程获取mockUrl 却没有传sessionId，无法获取当前session')
            }
        }
    }

    static findMockSessionAttribute(String sessionId, String key){
        if(!RuntimeUtil.isProductionEnv()){
            (findHTTPContext()?.currentSession(sessionId) as Map)?.get(key)
        }
    }

    private static findMockAttribute(Map additionalParameters, String key){
        additionalParameters?.get(key) ?: findMockSessionAttribute(key) ?:
            (additionalParameters?.client_identifier ? findMockSessionAttribute(additionalParameters.client_identifier as String, key) : null)
    }

    static String findBaseUrl(Map additionalParameters = [:]){
        findMockAttribute(additionalParameters, MOCK_BASE_URL)
    }

    static String findPublicKey(Map additionalParameters = [:]){
        findMockAttribute(additionalParameters, MOCK_SERVER_KEYPAIR)?.publicKey
    }
    static String findPrivateKey(Map additionalParameters = [:]){
        findMockAttribute(additionalParameters, MOCK_SERVER_KEYPAIR)?.privateKey
    }

    //用于跨进程，放入到additionalParameters里
    static Map additionalParameters(){
        if (!RuntimeUtil.isProductionEnv() && findBaseUrl()){
            return  [
                (MOCK_BASE_URL) : findBaseUrl(),
                (MOCK_SERVER_KEYPAIR) : [
                    publicKey : findPublicKey(),
                    privateKey : findPrivateKey()
                ]
            ]
        }
        [:]
    }



    static findHTTPContext(){
        try{
            return ApplicationContextHolder.getApplicationContext().getBean(IHTTPContext.class);
        } catch (Exception e){
            //do nothing
        }

    }
}
