//zhengwei于2016/11/4重构时全部注释掉，目前系统没有用到
// package com.cheche365.cheche.wechat;
//
//import com.cheche365.cheche.wechat.model.WechatUserInfo;
//import com.cheche365.cheche.wechat.repository.WechatUserInfoRepository;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.util.StringUtils;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * Created by liqiang on 3/24/15.
// */
//
//@Component
//public class WechatUserUnionIdManager {
//
//    private Logger logger = LoggerFactory.getLogger(WechatUserUnionIdManager.class);
//
//    @Autowired
//    private MessageSender messageSender;
//
//    @Autowired
//    private WechatUserInfoRepository wechatUserInfoRepository;
//
//    public WechatUserInfo getWechatUserInfo(WechatUserInfo tempWechatUserInfo) {
//        if(logger.isDebugEnabled()) {
//            logger.debug("send get request info to get unionid.");
//        }
//
//        Map<String, Object> parameters = new HashMap<>();
//        parameters.put("openid", tempWechatUserInfo.getWechatUserChannel().getOpenId());
//        parameters.put("lang", WechatConstant.LANG);
//        WechatUserInfo wechatUserInfo = messageSender.getMessageForObject("/cgi-bin/user/info", parameters, WechatUserInfo.class);
//        if(wechatUserInfo != null && StringUtils.isEmpty(wechatUserInfo.getUnionid())) {
//            tempWechatUserInfo.setUnionid(wechatUserInfo.getUnionid());
//            wechatUserInfoRepository.save(tempWechatUserInfo);
//        }
//
//        return tempWechatUserInfo;
//    }
//}
