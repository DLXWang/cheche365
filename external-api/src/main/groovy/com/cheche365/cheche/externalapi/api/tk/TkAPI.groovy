package com.cheche365.cheche.externalapi.api.tk

import com.cheche365.cheche.core.model.Payment
import com.cheche365.cheche.core.service.IConfigService
import com.cheche365.cheche.core.service.QuoteRecordCacheService
import com.cheche365.cheche.externalapi.ExternalAPI
import com.cheche365.cheche.externalapi.model.TkProposal
import net.sf.json.JSONObject
import org.apache.commons.io.IOUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service
import org.springframework.util.DigestUtils

import javax.ws.rs.core.MediaType

import static com.cheche365.cheche.common.util.DateUtils.DATE_LONGTIME24_PATTERN
import static com.cheche365.cheche.common.util.FlowUtils.getEnvPropertyNew
import static com.cheche365.cheche.core.util.MockUrlUtil.findBaseUrl

/**
 * Created by wen on 2018/6/25.
 */
@Service
class TkAPI extends ExternalAPI{

    @Autowired
    Environment env
    @Autowired
    IConfigService configService
    @Autowired
    QuoteRecordCacheService cacheService

    Map call(Payment payment,Map params){
        super.call(
            [
                body : reqBuild(payment,params)
            ])
    }

    Map reqBuild(Payment payment, Map params){
        TkProposal proposal=new TkProposal(payment.purchaseOrder,cacheService)

        def originBody = originBody(payment,params)

        [
            head : [
                proposalFormToken : proposal.getToken(),
                proposalFormId :  proposal.getFormId(),
                version : version(),
                function : function(),
                transTime : Calendar.instance.format(DATE_LONGTIME24_PATTERN),
                reqMsgId : UUID.randomUUID().toString(),
                channelId : channelId(),
                sign_type : "md5",
                sign : sign(originBody)
            ],
            apply_content : originBody
        ]
    }

    def originBody(Payment payment,Map params){}

    def function(){}

    def sign(originBody){
        DigestUtils.md5DigestAsHex(IOUtils.toByteArray(IOUtils.toInputStream(key()+JSONObject.fromObject(originBody).toString(), "UTF-8")))
    }

    @Override
    String host(){
        findBaseUrl() ?: getEnvPropertyNew([env: env, configService: configService, namespace: 'taikang'], 'base_url', null, [])
    }

    @Override
    String method(){
        'POST'
    }

    String key(){
        getEnvPropertyNew([env: env, configService: configService, namespace: 'taikang'], 'channelKey', null, [])
    }

    String version(){
        getEnvPropertyNew([env: env, configService: configService, namespace: 'taikang'], 'version', null, [])
    }
    String channelId(){
        getEnvPropertyNew([env: env, configService: configService, namespace: 'taikang'], 'channelId', null, [])
    }

    @Override
    MediaType contentType(){
        MediaType.valueOf('application/json;charset=UTF-8')
    }

}
