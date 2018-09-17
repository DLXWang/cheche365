package com.cheche365.cheche.rest.web.controller;

import com.cheche365.cheche.common.util.ContactUtils;
import com.cheche365.cheche.core.exception.BusinessException;
import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.model.Gender;
import com.cheche365.cheche.core.model.User;
import com.cheche365.cheche.core.service.UserService;
import com.cheche365.cheche.core.util.CacheUtil;
import com.cheche365.cheche.rest.processor.login.LoginFactory;
import com.cheche365.cheche.rest.processor.login.LoginInfo;
import com.cheche365.cheche.rest.web.session.MobileSessionHandler;
import com.cheche365.cheche.sms.client.service.ValidatingService;
import com.cheche365.cheche.web.ContextResource;
import com.cheche365.cheche.web.counter.annotation.CountApiInvoke;
import com.cheche365.cheche.web.counter.annotation.PrometheusMetrics;
import com.cheche365.cheche.web.response.RestResponseEnvelope;
import com.cheche365.cheche.web.response.ResultObj;
import com.cheche365.cheche.web.service.UserCallbackService;
import com.cheche365.cheche.web.util.ClientTypeUtil;
import com.cheche365.cheche.web.version.VersionedResource;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static com.cheche365.cheche.core.constants.SmsConstants._SMS_PRODUCT_CHECHE;
import static com.cheche365.cheche.core.constants.SmsConstants._SMS_PRODUCT_KEY;

/**
 * Created by zhengwei on 3/20/15.
 * 本类只处理登录／注册相关的API，其他User相关API参考{@link UsersThirdPartyResource} 和 {@link UsersAutoResource}
 */

@RestController
@RequestMapping("/" + ContextResource.VERSION_NO + "/users")
@VersionedResource(from = "1.0")
public class UsersResource extends ContextResource {

    @Autowired
    private MobileSessionHandler mobileSessionHandler;

    @Autowired
    private LoginFactory userLoginFactory;
    @Autowired
    private ValidatingService validateService;
    @Autowired
    private UserService userService;
    @Autowired
    UserCallbackService userCallbackService;


    @PrometheusMetrics(name="loginTotal")
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public Object login(@RequestParam(value = "mobile") String mobile,
                        @RequestParam(value = "validationCode", required = false) String validationCode, HttpServletRequest request) {
        Map<String, Object> result = Maps.newHashMap();
        User user;
        try {
            user = userLoginFactory.getUserLoginProcessor().login(new LoginInfo(mobile, validationCode));
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException(BusinessException.Code.OPERATION_NOT_ALLOWED, "该手机号已经绑定");
        }

        CacheUtil.cacheUser(request.getSession(), user);

        result.put("user", user);
        result.put("userKeyInClient", session.getId());
        return this.getResponseEntity(result);
    }

    @RequestMapping(value = "/bind", method = RequestMethod.PUT)
    public HttpEntity<RestResponseEnvelope<String>> bindValidation(@RequestBody Map<String, String> param) {
        User user = userLoginFactory.getUserLoginProcessor().bind(new LoginInfo(param.get("mobile"), param.get("validationCode")));
        CacheUtil.cacheUser(request.getSession(), user);
        RestResponseEnvelope<String> bindResult = new RestResponseEnvelope<>("{result: \"success\"}");
        return new ResponseEntity<>(bindResult, HttpStatus.OK);
    }

    @RequestMapping(value = "/updateMobile", method = RequestMethod.PUT)
    public HttpEntity<RestResponseEnvelope> updateMobile(@RequestBody Map<String, String> param) {
        String mobile = param.get("mobile");
        String validationCode = param.get("validationCode");
        if (StringUtils.isBlank(mobile)) {
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "待绑定的手机号为空.");
        }
        if (StringUtils.isBlank(validationCode)) {
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "验证码为空.");
        }

        Map<String, String> additionalParam = new HashMap<>();
        additionalParam.put(_SMS_PRODUCT_KEY, _SMS_PRODUCT_CHECHE);
        validateService.validate(mobile, validationCode, additionalParam);
        User afterUpdate = userService.boundMobile(this.currentUser(), mobile);

        CacheUtil.cacheUser(request.getSession(), afterUpdate);
        return new ResponseEntity<>(new RestResponseEnvelope<>("{result: \"success\"}"), HttpStatus.OK);
    }


    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope> logout(HttpServletRequest request) {
        Channel mobileClientType = ClientTypeUtil.getMobileClientTypeByRequest(request);
        if (mobileClientType != null) {
            this.mobileSessionHandler.doLogout(request, mobileClientType);
        }
        session.invalidate();
        return this.getResponseEntity(null);
    }

    @RequestMapping(value = "/getBoundMobile", method = RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope> getBoundMobile() {
        User user = this.currentUser();
        ResultObj result = new ResultObj();
        result.setObj(user);
        if (new Boolean(true).equals(user.isBound())) {
            result.setResult(true);
            result.setMemo("已绑定手机号:" + user.getMobile());
        } else {
            result.setResult(false);
            result.setMemo("未绑定手机号");
        }
        return this.getResponseEntity(result);
    }

    @RequestMapping(value = "/hasLogin", method = RequestMethod.GET)
    @CountApiInvoke(value = "pvuv")
    public HttpEntity<RestResponseEnvelope> hasLogin(HttpServletRequest request) {
        CacheUtil.cacheSmsFlag(session);
        User user =  userCallbackService.hasLoginByUser(userLoginFactory.getUserLoginProcessor().hasLogin(), safeGetCurrentUserCallback(), request);
        Channel channel = ClientTypeUtil.getChannel(request);
        ResultObj result = new ResultObj();
        result.setObj(user);
        if (null != user && (Boolean.TRUE.equals(user.isBound()) || Channel.Enum.PARTNER_NCI_25.equals(channel))) {
            result.setResult(true);
            result.setMemo("已登录");
        } else {
            result.setResult(false);
            result.setMemo("未登录");
        }

        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(new RestResponseEnvelope(result));
    }

    @RequestMapping(value = "", method = RequestMethod.PUT)
    public HttpEntity<RestResponseEnvelope> modifyUser(@RequestBody User user) {
        User existedUser;
        if (currentUser().getId() == null || (existedUser = userService.getUserById(currentUser().getId())) == null) {
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "无权限修改用户信息");
        }
        if(user.getIdentity() != null && user.getGender() == null){
            Gender gender = new Gender();
            gender.setId(((Integer)ContactUtils.getGenderByIdentity(user.getIdentity())).longValue());
            user.setGender(gender);
        }
        if(StringUtils.isBlank(user.getEmail())){
            user.setEmail("baodan@cheche365.com"); //设置默认邮箱
        }
        existedUser = userService.modifyUser(existedUser, user);
        CacheUtil.cacheUser(session, existedUser);
        return this.getResponseEntity(existedUser);
    }

}
