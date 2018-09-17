package com.cheche365.cheche.rest.web.controller;


import com.cheche365.cheche.core.exception.BusinessException;
import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.model.User;
import com.cheche365.cheche.core.model.agent.ChannelAgent;
import com.cheche365.cheche.core.model.agent.ProfessionApprove;
import com.cheche365.cheche.core.repository.ProfessionApproveRepository;
import com.cheche365.cheche.core.service.UserService;
import com.cheche365.cheche.core.service.UserSessionService;
import com.cheche365.cheche.core.service.agent.ApproveService;
import com.cheche365.cheche.core.util.CacheUtil;
import com.cheche365.cheche.rest.processor.login.LoginFactory;
import com.cheche365.cheche.rest.processor.login.LoginInfo;
import com.cheche365.cheche.rest.processor.login.RegisterInfo;
import com.cheche365.cheche.rest.processor.login.UserInfo;
import com.cheche365.cheche.web.ContextResource;
import com.cheche365.cheche.web.counter.annotation.CountApiInvoke;
import com.cheche365.cheche.web.response.RestResponseEnvelope;
import com.cheche365.cheche.web.service.ChannelAgentService;
import com.cheche365.cheche.web.service.UserCallbackService;
import com.cheche365.cheche.web.service.UuidMappingService;
import com.cheche365.cheche.web.service.shareInfo.ChannelAgentShareHandler;
import com.cheche365.cheche.web.util.ClientTypeUtil;
import com.cheche365.cheche.web.version.VersionedResource;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static com.cheche365.cheche.core.exception.BusinessException.Code.OPERATION_NOT_ALLOWED;
import static com.cheche365.cheche.core.model.agent.ApproveStatus.Enum.NOT_APPROVE_1;

@RestController
@RequestMapping("/" + ContextResource.VERSION_NO + "/agent")
@VersionedResource(from = "1.6")
public class ChannelAgentResource extends ContextResource {

    private Logger logger = LoggerFactory.getLogger(ChannelAgentResource.class);

    @Autowired
    private LoginFactory userLoginFactory;

    @Autowired
    private ChannelAgentService channelAgentService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserCallbackService userCallbackService;

    @Autowired
    private ChannelAgentShareHandler channelAgentShareHandler;
    @Autowired
    private UserSessionService userSessionService;
    @Autowired
    UuidMappingService uuidMappingService;
    @Autowired
    ProfessionApproveRepository professionApproveRepository;
    @Autowired
    ApproveService approveService;

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public HttpEntity<RestResponseEnvelope> register(@RequestBody RegisterInfo registerInfo){

        preCheck(request);
        if (Channel.toChannel(registerInfo.getChannelId()) != null) {
            ClientTypeUtil.cacheChannel(request, Channel.toChannel(registerInfo.getChannelId()));
        }
        checkChannel();
        ChannelAgent channelAgent = userLoginFactory.getUserLoginProcessor().register(registerInfo);

        CacheUtil.cacheChannelAgent(request.getSession(), channelAgent);
        CacheUtil.cacheUser(request.getSession(), channelAgent.getUser());

        return getResponseEntity(responseAgentInfo(channelAgent));
    }

    @RequestMapping(value = "/inviteCode", method = RequestMethod.POST)
    public HttpEntity<RestResponseEnvelope> verifyAgentInviteCode(@RequestBody Map<String, String> param, HttpServletRequest request){
        checkChannel();
        channelAgentService.verifyAgentInviteCode(param.get("inviteCode"), getChannel());
        Map<String, Object> result = Maps.newHashMap();
        result.put("result", "success");
        return getResponseEntity(result);
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope> login(@RequestParam(value = "mobile") String mobile,
                                                  @RequestParam(value = "validationCode", required = false) String validationCode, HttpServletRequest request) {
        preCheck(request);
        checkChannel();
        ChannelAgent channelAgent = userLoginFactory.getUserLoginProcessor().login(new LoginInfo(mobile, validationCode));

        CacheUtil.cacheChannelAgent(request.getSession(), channelAgent);
        CacheUtil.cacheUser(request.getSession(), channelAgent.getUser());

        return getResponseEntity(responseAgentInfo(channelAgent));
    }

    @RequestMapping(value = "/hasLogin", method = RequestMethod.GET)
    @CountApiInvoke(value = "pvuv")
    public HttpEntity<RestResponseEnvelope> hasLogin() {
        CacheUtil.cacheSmsFlag(session);
        checkChannel();
        Map<String, Object> result = Maps.newHashMap();
        ChannelAgent channelAgent = userCallbackService.hasLoginByChannelAgent(userLoginFactory.getUserLoginProcessor().hasLogin(), safeGetCurrentUserCallback(), request);
        if (channelAgent != null) {
            result = responseAgentInfo(channelAgent);
            result.put("result",true);
        } else {
            result.put("result", false);
        }
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(new RestResponseEnvelope(result));
    }

    @RequestMapping(value = "/info", method = RequestMethod.PUT)
    public HttpEntity<RestResponseEnvelope> modify(@RequestBody RegisterInfo registerInfo) {
        checkChannel();
        ChannelAgent channelAgent = registerInfo.getChannelAgentInfo();
        User user = registerInfo.getUserInfo();
        User existedUser = userService.getUserById(currentUser().getId());
        ProfessionApprove professionApprove = professionApproveRepository.findByChannelAgent(currentChannelAgent());

        if (professionApprove != null && !professionApprove.getApproveStatus().equals(NOT_APPROVE_1)) {
            logger.info("代理人资格认证处于：{}阶段，核心信息无法更改",professionApprove.getApproveStatus().getDescription());
            registerInfo.setName(null);
            registerInfo.setEthnic(null);
            registerInfo.setIdentity(null);
        }
        existedUser = userService.modifyUser(existedUser, user);
        channelAgentService.modifyQRCode(existedUser);

        ChannelAgent existedChannelAgent = channelAgentRepository.findOne(currentChannelAgent().getId());
        existedChannelAgent = channelAgentService.modifyChannelAgent(existedChannelAgent, channelAgent);
        CacheUtil.cacheUser(session, existedUser);
        CacheUtil.cacheChannelAgent(session, existedChannelAgent);

        return getResponseEntity(responseAgentInfo(existedChannelAgent));
    }

    @RequestMapping(value = "detail", method = RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope> detail(){

        return getResponseEntity(getApproveStatus());
    }

    @RequestMapping(value = "approve",method = RequestMethod.POST)
    public HttpEntity<RestResponseEnvelope> approve(@RequestBody RegisterInfo registerInfo){

        preApproveCheck(registerInfo);
        ChannelAgent channelAgent = approveService.approve(currentChannelAgent(), registerInfo.getName(), registerInfo.getIdentity(), registerInfo.getEthnic());
        CacheUtil.cacheUser(session, channelAgent.getUser());
        CacheUtil.cacheChannelAgent(session, channelAgent);
        return getResponseEntity(getApproveStatus());
    }

    private void preApproveCheck(RegisterInfo registerInfo) {
        if (StringUtils.isBlank(registerInfo.getName()) ||
            StringUtils.isBlank(registerInfo.getIdentity()) ||
            registerInfo.getEthnic() == null) {
            throw new BusinessException(OPERATION_NOT_ALLOWED, "认证信息不全，请补全");
        }
    }

    private void checkChannel() throws BusinessException {
        if (!getChannel().isLevelAgent()) {
            throw new BusinessException(OPERATION_NOT_ALLOWED, "非代理人渠道无权调用此接口");
        }
    }

    private Map responseAgentInfo(ChannelAgent channelAgent){

        channelAgent.setShareInfo(channelAgentShareHandler.shareInfo(channelAgent));
        userSessionService.cacheUserSession(channelAgent,request.getSession());
        Map<String, Object> result = Maps.newHashMap();
        result.put("agent", channelAgent);
        return result;
    }

    private void preCheck(HttpServletRequest request){
        String uuid = ClientTypeUtil.extractUUID(request);
        if(uuidMappingService.incorrectUuid(request,uuid)){
            throw new BusinessException(BusinessException.Code.INTERNAL_SERVICE_ERROR,"请求失败");
        }
    }
    private UserInfo getApproveStatus() {
        UserInfo userInfo = new UserInfo();
        userInfo.setApproveStatus(approveService.caApproveStatus(currentChannelAgent()));
        userInfo.setNeedCompletionUser(currentChannelAgent().needCompletionUser());
        userInfo.setUser(currentUser());
        userInfo.setAgent(currentChannelAgent());
        return userInfo;
    }
}
