package com.cheche365.cheche.externalapi.api.sinosafe

import com.cheche365.cheche.core.util.MockUrlUtil
import com.cheche365.cheche.externalapi.ExternalAPI
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

import javax.ws.rs.core.MediaType

import com.cheche365.cheche.common.util.XmlUtils

/**
 * Created by zhengwei on 09/02/2018.
 */

@Service
abstract class SinosafeAPI extends ExternalAPI implements InitializingBean {

    @Autowired
    Environment env

    static final REQUEST_TEMPLATE =  [
        PACKET: [
            HEAD : [
                TRANSTYPE  : 'SNY',
                SYSCODE    : null,
                CONTENTTYPE: 'XML',
                VERIFYTYPE : '1'
            ],
            THIRD: [
                PRODNO       : '0000',
                PLANNO       : '0000'
            ]
        ]
    ]

    Map call(Map body) {

        Map request = REQUEST_TEMPLATE.clone()

        request.PACKET.HEAD.TRANSCODE = transCode()

        request.PACKET.THIRD.TRANSCODE = transCode()
        request.PACKET.THIRD.TRANSDATE = Calendar.instance.format('yyyy-MM-dd')
        request.PACKET.THIRD.TRANSTIME = Calendar.instance.format('HH:mm:ss')

        request.PACKET.BODY = body

        String responseInString = super.call(
            [
                    body: request
            ]
        )

        return XmlUtils.xmlToMap(responseInString)
    }

    abstract String transCode()

    @Override
    String method() {
        'POST'
    }

    @Override
    Class responseType() {
        String
    }

    @Override
    MediaType contentType() {
        MediaType.APPLICATION_XML_TYPE
    }

    @Override
    String host() {
        MockUrlUtil.findBaseUrl() ?: env.getProperty('sinosafe.api_base_url')
    }

    @Override
    void afterPropertiesSet() throws Exception {
        REQUEST_TEMPLATE.PACKET.HEAD.USER = env.getProperty('sinosafe.user')
        REQUEST_TEMPLATE.PACKET.HEAD.PASSWORD = env.getProperty('sinosafe.password')

        REQUEST_TEMPLATE.PACKET.THIRD.EXTENTERPCODE = env.getProperty('sinosafe.extenterpcode')
    }
}
