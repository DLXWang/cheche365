package com.cheche365.cheche.alipay.web.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.cheche365.cheche.alipay.constants.AlipayServiceEnvConstants;
import com.cheche365.cheche.alipay.util.RequestUtil;
import com.cheche365.cheche.alipay.web.factory.ActionExecutorFactory;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@Controller
@RequestMapping("/web/alipay/channels/")
public class AlipayGatewayController {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private ActionExecutorFactory actionExecutorFactory;
    /* 消息redis key */
    private final static String ALIPAY_KEY = "alipay:channels";

    private final static Logger logger = LoggerFactory.getLogger(AlipayGatewayController.class);

    /**
     * 支付宝服务窗网关
     * @param request
     * @param response
     */
    @RequestMapping(value = "gateway", method = RequestMethod.POST)
    public void gateway(HttpServletRequest request, HttpServletResponse response) {
        String responseMsg = "";
        try {
            Map<String, String> params = RequestUtil.getRequestParams(request);
            logger.info("支付宝请求串:" + params.toString());
            verifySign(params);
            JSONObject bizContent = RequestUtil.getBizContent(params);
            if(verifyRepeatMessage(bizContent)) {
                final String service = params.get("service");
                responseMsg = actionExecutorFactory.getExecutor(service, bizContent).execute(bizContent);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                responseMsg = AlipaySignature.encryptAndSign(responseMsg,
                    AlipayServiceEnvConstants.ALIPAY_PUBLIC_KEY,
                    AlipayServiceEnvConstants.PRIVATE_KEY,
                    AlipayServiceEnvConstants.CHARSET,
                    false, true);
                response.setContentType("text/xml;charset=GBK");
                PrintWriter printWriter = response.getWriter();
                printWriter.print(responseMsg);
                response.flushBuffer();
            } catch (AlipayApiException | IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void verifySign(Map<String, String> params) throws AlipayApiException {
        if (!AlipaySignature.rsaCheckV2(params, AlipayServiceEnvConstants.ALIPAY_PUBLIC_KEY,
            AlipayServiceEnvConstants.SIGN_CHARSET)) {
            logger.info("verify sign fail; params=" + params.toString());
            throw new AlipayApiException("verify sign fail.");
        }
    }

    /**
     * 判断消息是否已经处理，相同的消息MsgId相同，放入Redis判断重复
     * @param bizContent
     * @return
     */
    private boolean verifyRepeatMessage(JSONObject bizContent) {
        if(bizContent.get("MsgId") == null) {
            return true;
        }
        boolean access = false;
        String msgId = bizContent.getString("MsgId");
        String hashKey = "msgid:" + msgId;
        if(redisTemplate.opsForHash().get(ALIPAY_KEY, hashKey) == null) {
            redisTemplate.opsForHash().increment(ALIPAY_KEY, hashKey, 1);
            access = true;
        }else {
            redisTemplate.opsForHash().delete(ALIPAY_KEY, hashKey);
        }
        return access;
    }
}
