package com.cheche365.cheche.rest.v1_3.web.controller;

import com.cheche365.cheche.core.message.RedisPublisher;
import com.cheche365.cheche.core.service.sms.ConditionTriggerHandler;
import com.cheche365.cheche.core.service.sms.ConditionTriggerUtil;
import com.cheche365.cheche.web.ContextResource;
import com.cheche365.cheche.web.response.RestResponseEnvelope;
import com.cheche365.cheche.web.util.ClientTypeUtil;
import com.cheche365.cheche.web.service.security.throttle.SmsValidationUtils;
import com.cheche365.cheche.web.version.VersionedResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import static com.cheche365.cheche.core.message.IPFilterMessage.CODE_SMS;

/**
 * Created by dongruiren on 2015/12/3.
 */
@RestController
@RequestMapping("/" + ContextResource.VERSION_NO + "/users")
@VersionedResource(from = "1.0" )
public class UsersResource_1_3 extends ContextResource{
    public Logger logger = LoggerFactory.getLogger(UsersResource_1_3.class);

    @Autowired
    protected ConditionTriggerHandler conditionTriggerHandler;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedisPublisher redisPublisher;

    @RequestMapping(value = "/sendValidationCodeForNewUser", method = RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope> sendValidationCodeForNewUser(@RequestParam String mobile, HttpServletRequest request) {
        SmsValidationUtils.validateRequest(CODE_SMS, request, redisTemplate, redisPublisher);
        //030000038_001：【车车】您的验证码是：${label1}，有效期为20分钟。您正在申请注册车车账号，如非本人操作请忽略本短信。
        return this.getResponseEntity(ConditionTriggerUtil.sendValidateCodeMessage(conditionTriggerHandler, mobile, ClientTypeUtil.getChannel(request)));
    }

    //发送短信验证码
    @RequestMapping(value = "/sendValidationCodeForLogin", method = RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope> sendValidationCodeForLogin(@RequestParam String mobile, HttpServletRequest request) {
        SmsValidationUtils.validateRequest(CODE_SMS, request, redisTemplate, redisPublisher);
        //030000038_001：【车车】您的验证码是：${label1}，有效期为20分钟。您正在申请注册车车账号，如非本人操作请忽略本短信。
        return this.getResponseEntity(ConditionTriggerUtil.sendValidateCodeMessage(conditionTriggerHandler, mobile, ClientTypeUtil.getChannel(request)));
    }

    @RequestMapping(value = "/sendValidationCodeForFindPassword", method = RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope> sendValidationCodeForFindPassword(@RequestParam String mobile, HttpServletRequest request) {
        SmsValidationUtils.validateRequest(CODE_SMS, request, redisTemplate, redisPublisher);
        //030000038_002：【车车】您的验证码是${label1}，有效期为20分钟。您正在申请找回密码，如非本人操作请忽略本短信。
        return this.getResponseEntity(ConditionTriggerUtil.sendValidateCodeMessage(conditionTriggerHandler, mobile, ClientTypeUtil.getChannel(request)));
    }

    @RequestMapping(value = "/sendValidationCodeForBindMobile", method = RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope> sendValidationCodeForBindMobile(@RequestParam String mobile, HttpServletRequest request) {
        SmsValidationUtils.validateRequest(CODE_SMS, request, redisTemplate, redisPublisher);
        //030000038_004: 【车车】您的验证码是：${label1}，有效期为20分钟。您正在修改车车绑定的手机号，如非本人操作请忽略本短信。
        logger.debug("will generate validation code for mobile: " + mobile);
        return this.getResponseEntity(ConditionTriggerUtil.sendValidateCodeMessage(conditionTriggerHandler, mobile, ClientTypeUtil.getChannel(request)));
    }


}
