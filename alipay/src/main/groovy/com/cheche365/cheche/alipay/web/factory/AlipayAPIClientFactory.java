/**
 * AlipayAPIClientFactory
 */
package com.cheche365.cheche.alipay.web.factory;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.cheche365.cheche.alipay.constants.AlipayServiceEnvConstants;


/**
 * API调用客户端工厂
 * @author zhaozhong
 */
public class AlipayAPIClientFactory {

    /** API调用客户端 */
    private static AlipayClient alipayClient;


    public static AlipayClient getAlipayClient(){
        if(null == alipayClient){
            alipayClient = new DefaultAlipayClient(AlipayServiceEnvConstants.ALIPAY_GATEWAY, AlipayServiceEnvConstants.APP_ID, AlipayServiceEnvConstants.PRIVATE_KEY, "json", AlipayServiceEnvConstants.CHARSET,AlipayServiceEnvConstants.ALIPAY_PUBLIC_KEY);
        }
        return alipayClient;
    }
}
