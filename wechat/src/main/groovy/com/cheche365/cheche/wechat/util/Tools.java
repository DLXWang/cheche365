/**
 * 微信公众平台开发模式(JAVA) SDK
 * (c) 2012-2013 ____′↘夏悸 <wmails@126.cn>, MIT Licensed
 * http://www.jeasyuicn.com/wechat
 */
package com.cheche365.cheche.wechat.util;

import com.cheche365.cheche.common.util.HashUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Tools {

    public static final String inputStream2String(InputStream in) throws UnsupportedEncodingException, IOException{
        if(in == null)
            return "";
        
        StringBuffer out = new StringBuffer();
        byte[] b = new byte[4096];
        for (int n; (n = in.read(b)) != -1;) {
            out.append(new String(b, 0, n, "UTF-8"));
        }
        return out.toString();
    }
    
    public static final boolean checkSignature(String token,String signature,String timestamp,String nonce){
        List<String> params = new ArrayList<String>();
        params.add(token);
        params.add(timestamp);
        params.add(nonce);
        Collections.sort(params);
        String temp = params.get(0)+params.get(1)+params.get(2);
        return HashUtils.sha1(temp).equals(signature);
    }


}
