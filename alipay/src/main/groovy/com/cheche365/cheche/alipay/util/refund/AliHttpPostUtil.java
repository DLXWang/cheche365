package com.cheche365.cheche.alipay.util.refund;


import org.apache.commons.httpclient.NameValuePair;

import java.util.Map;

/**
 * Created by Administrator on 2016/10/27 0027.
 */
public class AliHttpPostUtil {

    public static String doPost(Map<String,String> map,String url) throws Exception{
        HttpProtocolHandler httpProtocolHandler = HttpProtocolHandler.getInstance();
        HttpRequest request = new HttpRequest(HttpResultType.BYTES);
        request.setCharset("utf-8");
        request.setParameters(generatNameValuePair(map));
        request.setUrl(url + "_input_charset=" + HttpProtocolHandler.DEFAULT_CHARSET);
        HttpResponse response = httpProtocolHandler.execute(request,"","");
        if (response == null) {
            return null;
        }
        String strResult = response.getStringResult();

        return strResult;
    }


    /**
     * MAP类型数组转换成NameValuePair类型
     * @param properties  MAP类型数组
     * @return NameValuePair类型数组
     */
    private static NameValuePair[] generatNameValuePair(Map<String, String> properties) {
        NameValuePair[] nameValuePair = new NameValuePair[properties.size()];
        int i = 0;
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            nameValuePair[i++] = new NameValuePair(entry.getKey(), entry.getValue());
        }

        return nameValuePair;
    }


}
