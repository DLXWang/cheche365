package com.cheche365.cheche.alipay.web.executor;

import com.cheche365.cheche.alipay.common.AliPayException;
import com.cheche365.cheche.alipay.constants.AlipayServiceEnvConstants;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;

/**
 * 开通服务窗开发者功能处理器
 */
@Component("verifyExecutor")
public class InAlipayVerifyExecutor implements ActionExecutor {

    /** 
     */
    @Override
    public String execute(JSONObject bizContent) throws AliPayException {
        //固定响应格式，必须按此格式返回
        StringBuilder builder = new StringBuilder();
        builder.append("<success>").append(Boolean.TRUE.toString()).append("</success>");
        builder.append("<biz_content>").append(AlipayServiceEnvConstants.PUBLIC_KEY)
            .append("</biz_content>");
        return builder.toString();
    }
}
