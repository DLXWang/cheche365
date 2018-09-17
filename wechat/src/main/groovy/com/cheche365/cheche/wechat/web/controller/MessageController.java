package com.cheche365.cheche.wechat.web.controller;

import com.cheche365.cheche.common.util.HashUtils;
import com.cheche365.cheche.wechat.IMessageProcessor;
import com.cheche365.cheche.core.WechatConstant;
import com.cheche365.cheche.wechat.message.InMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 接收微信发来的验证信息，返回正确的验证码
 * Created by liqiang on 3/20/15.
 */

@Controller
public class MessageController {

    private Logger logger = LoggerFactory.getLogger(MessageController.class);

    private static final String DUPLICATED_MESSAGE = "duplicated message";
    private static final String RECEIVED_MESSAGE = "received message";

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IMessageProcessor messageProcessor;

    @RequestMapping(value = "/web/wechat", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> get(@RequestParam String signature, @RequestParam String timestamp, @RequestParam String nonce, @RequestParam String echostr, HttpEntity<String> httpEntity){
        httpEntity.getHeaders();

        if (logger.isDebugEnabled()){
            logger.debug("headers: " + httpEntity.getHeaders());
        }

        List<String> params = new ArrayList<>();
        params.add(WechatConstant.TOKEN);
        params.add(nonce);
        params.add(timestamp);

        Collections.sort(params);

        StringBuilder stringBuilder = new StringBuilder();
        params.forEach(e -> stringBuilder.append(e));
        String tempString = HashUtils.sha1(stringBuilder.toString());
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("calculated signature [%s]", tempString));
        }

        String body;
        if (tempString.equals(signature)){
            body = echostr;
        }else{
            body = "signature doesn't match!";
        }

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.TEXT_PLAIN);
        ResponseEntity<String> responseEntity = new ResponseEntity<String>(body,responseHeaders,HttpStatus.OK);
        return  responseEntity;
    }

    @RequestMapping(value = "/web/wechat", method = RequestMethod.POST)
    @ResponseBody
    public String post(@RequestBody String requestBody){
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Received push message: [%s]", requestBody));
        }
        InMessage inMessage = InMessage.parseMessage(requestBody);
        Long messageID = inMessage.getMsgId();
        String key;
        if (messageID != null) {
            key = "wechat:msgid:" + messageID;
        }else{
            String fromUserName = inMessage.getFromUserName();
            Long createTime = inMessage.getCreateTime();
            key = "wechat:user:" + fromUserName + ":createTime:" + createTime;
        }

        String content = RECEIVED_MESSAGE;
        boolean notExists = redisTemplate.opsForValue().setIfAbsent(key ,"received");
        if (!notExists) {
            return "";
        }else {
            redisTemplate.expire(key, 5, TimeUnit.MINUTES);
        }

        return messageProcessor.process(inMessage);

    }

}
