package com.cheche365.cheche.externalapi.api.huanong

import com.cheche365.cheche.core.service.IConfigService
import com.cheche365.cheche.externalapi.ExternalAPI
import com.sinosoft.RSAUtils
import groovy.json.JsonOutput
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

import static com.cheche365.cheche.common.util.FlowUtils.getEnvPropertyNew
import static com.cheche365.cheche.core.util.MockUrlUtil.findBaseUrl

/**
 * Created by wen on 2018/8/6.
 */
@Service
abstract class HuanongAPI extends ExternalAPI {

    @Autowired
    Environment env

    @Autowired
    IConfigService configService

    String clientIdentifier

    def call(Map requestParam){
        super.call(
            [
                body : sign(requestParam)
            ]
        )
    }


    def sign(Map requestParam){
        RSAUtils.encryptByPublicKey(buildEncrypt(requestParam),publicKey())
    }

    def buildEncrypt(Map requestParam){
        def header = getHead()
        if(requestParam.token){
            header.head += [token : requestParam.token]
            requestParam.remove('token')
        }

        JsonOutput.toJson( header + requestParam)
    }

    def getHead(){
        [
            head : [
                interfaceCode : 'ZTYQ',
                transCode : transCode(),
                transType : 'Req'
            ]
        ]
    }

    @Override
    String host(){
        findBaseUrl([client_identifier:clientIdentifier]) ?: envPropertyNew('base_url')
    }

    @Override
    String method(){
        "POST"
    }

    def envPropertyNew(String property,def prefixes=null){
        getEnvPropertyNew([env: env, configService: configService, namespace: 'huanong'], property, null, prefixes)
    }

    String publicKey(){
        envPropertyNew('publicKey')
    }

    abstract String transCode()

}
