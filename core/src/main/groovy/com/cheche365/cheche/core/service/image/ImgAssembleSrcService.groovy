package com.cheche365.cheche.core.service.image

import com.cheche365.cheche.core.model.BusinessActivity
import com.cheche365.cheche.core.model.Channel
import org.apache.http.client.utils.URIBuilder

/**
 * Created by shanxf on 2017/4/12.
 * 处理首页所有图片后接src=，或者channel=
 */
class ImgAssembleSrcService {

    private final static String SRC="src"
    private final static String CPS ="cps"


    static void handleChannelType(object, BusinessActivity businessActivity,Channel channel){
        def objectSrc=generateSrc(businessActivity,channel)

        if(object instanceof List){
            object.each{
                if (it.url&&!it.url.contains(SRC)) {
                    it.url = generateUrl(it.url, objectSrc)
                }
            }
        }

    }

    static Map generateSrc(BusinessActivity businessActivity, Channel channel){
        def parameterMap=[:]
        if (businessActivity!=null){
            parameterMap.put(CPS,businessActivity.getCode())
        }else if(channel.isPartnerChannel()){
            parameterMap.put(SRC,channel.apiPartner?.code?.toLowerCase().toString())
        }
        parameterMap
    }

    static String generateUrl(original, Map parameterMap){
        URIBuilder resultUrl = new URIBuilder(original)
        if (parameterMap) {
            if (parameterMap.containsKey(CPS)) {
                resultUrl.addParameter(CPS, parameterMap.get(CPS))
            } else {
                resultUrl.addParameter(SRC, parameterMap.get(SRC))
            }
        }
        return resultUrl.toString()
    }
}
