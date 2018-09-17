package com.cheche365.cheche.externalapi.signature.botpy

import com.cheche365.cheche.core.util.URLUtils
import com.sun.jersey.api.client.ClientRequest
import net.sf.json.JSONObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.env.Environment

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import java.security.MessageDigest


class BotpySignature {

    static Logger logger = LoggerFactory.getLogger(BotpySignature.class);

    static String sign(headers, ClientRequest request) {


        String stringToSign=headers.timeStamp + headers.appKey + headers.accept +  request.URI.scheme+'://'+request.URI.host  + request.URI.path +buildQuery(request)+ bodyToMd5(request.entity as Map) + headers.appid
        logger.debug("待签名 : ${stringToSign}")
        HMACHA256(headers.appKey as String,stringToSign)
    }

    static buildQuery(request){
        def query=request.URI.query
        if(query){
            return URLUtils.splitQuery(query,false).keySet().toSorted()
        }
        ''
    }

    static bodyToMd5(Map dataMap){
        if(dataMap){
            return  MD5Encode(JSONObject.fromObject(dataMap).toString()).toLowerCase()
        }
        return ''
    }

    private static final MD5Encode(origin) {
        MessageDigest md = MessageDigest.getInstance('MD5')
        byte2Hex(md.digest(origin.getBytes('utf-8'))).toLowerCase()
    }


    /**
     * 将byte转为16进制
     */
    private static final byte2Hex(byte[] bytes) {
        StringBuffer stringBuffer = new StringBuffer()
        bytes.each {
            def temp = Integer.toHexString(it & 0xFF)
            if (temp.length() == 1) {
                stringBuffer.append("0")
            }
            stringBuffer.append(temp)
        }
        stringBuffer.toString()
    }

    private static final HMACHA256(secretKey, data) {
        Mac mac = Mac.getInstance("HmacSHA256")
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256")
        mac.init(secretKeySpec)
        byte2Hex(mac.doFinal(data.getBytes())).toLowerCase()
    }

}
