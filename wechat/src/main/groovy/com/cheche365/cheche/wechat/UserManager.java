package com.cheche365.cheche.wechat;

import com.cheche365.cheche.core.WechatConstant;
import com.cheche365.cheche.core.constants.WebConstants;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.AgentRepository;
import com.cheche365.cheche.core.repository.QRCodeChannelRepository;
import com.cheche365.cheche.core.repository.UserRepository;
import com.cheche365.cheche.core.util.QRCodeChannelUtil;
import com.cheche365.cheche.wechat.message.InMessage;
import com.cheche365.cheche.wechat.message.json.CustomerMessage;
import com.cheche365.cheche.core.model.WechatQRCode;
import com.cheche365.cheche.core.model.WechatUserChannel;
import com.cheche365.cheche.core.model.WechatUserInfo;
import com.cheche365.cheche.core.repository.WechatQRCodeRepository;
import com.cheche365.cheche.core.repository.WechatUserChannelRepository;
import com.cheche365.cheche.core.repository.WechatUserInfoRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liqiang on 3/24/15.
 */

@Component
public class UserManager {

    public static final String QRSCENE_PREFIX = "qrscene_";
    private static String welcomeMessage = "车车——发现最好最便宜的车险\n" +
        "•官方授权——多家保险公司官方承保，10分钟出单\n" +
        "•理赔无忧——千家4S店提供尊享VIP一站式理赔服务\n" +
        "•全网最低——比保险公司官网直销价还便宜";

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private WechatUserInfoRepository wechatUserInfoRepository;

    @Autowired
    private WechatUserChannelRepository wechatUserChannelRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AgentRepository agentRepository;

    @Autowired
    private WechatQRCodeRepository wechatQRCodeRepository;

    @Autowired
    private QRCodeChannelRepository qrCodeChannelRepository;

    @Autowired
    private WelcomeMessageHolder welcomeMessageHolder;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private WechatUserHandler userHandler;

    private Logger logger = LoggerFactory.getLogger(UserManager.class);

    public void subscribe(InMessage inMessage, Channel channel) {
        String openID = inMessage.getFromUserName();
        String eventKey = StringUtils.isNoneBlank(inMessage.getEventKey()) ? inMessage.getEventKey() : null;
        String ticket = StringUtils.isNoneBlank(inMessage.getEventKey()) ? inMessage.getTicket() : null;

        logger.debug("eventKey is {},ticket is {},openID is {}", eventKey, ticket, openID);

        WechatQRCode qrCode = null;
        if (StringUtils.isNotBlank(ticket)) {
            qrCode = wechatQRCodeRepository.findFirstByTicket(ticket);
        }

        WechatUserChannel userChannel = this.userHandler.publicAccountSaveUserInfo(openID, null, channel);
        userChannel.setQrcode(qrCode == null ? null : qrCode.getId());
        wechatUserChannelRepository.save(userChannel);
        sendWelcomeMessage(openID, qrCode, channel);

        saveSubscribeData(eventKey, userChannel);
    }

    private void sendWelcomeMessage(String openID, WechatQRCode qrCode, Channel channel) {
//
//        List<CustomerMessage.Article> articles = welcomeMessageHolder.getWelcomeMessage(getScene(qrCode));
//
//        messageSender.sendNewsMessage(openID, articles, channel);

        String content ="Hi，你来了~\n让我猜猜你是车险快到期了吗?\n来来来，点这里<a href=\"{baseUrl}\">车险报价</a>，带你体验互联网时代最棒的车险服务，拒绝中间商，直击最底价！\n同时你将享受10秒精准报价、1分钟在线投保、24小时保单送达服务\n更贴心的服务，请拨4000150999，客服小妹一直在等你 。".replace("{baseUrl}", WebConstants.getDomainURL() + "/m/index.html#base");

        messageSender.sendTextMessage(openID, content, channel);

    }

    private String getScene(WechatQRCode qrCode) {
        if (qrCode == null) {
            return null;
        }
        return StringUtils.isBlank(qrCode.getSceneStr()) ? String.valueOf(qrCode.getSceneId()) : qrCode.getSceneStr().trim();
    }

    public void unsubscribe(String openID) {
        //mark user as unsubscribed
        WechatUserChannel wechatUserChannel = wechatUserChannelRepository.findFirstByOpenId(openID);
        WechatUserInfo userInfo = wechatUserChannel == null ? null : wechatUserChannel.getWechatUserInfo();
        if (userInfo != null) {
            saveUnSubscribeData(wechatUserChannel);
            wechatUserChannel.setUnsubscribed(true);
            wechatUserChannel.setQrcode(null);
            wechatUserChannel.setQrcodeChannel(null);
            wechatUserInfoRepository.save(userInfo);
            wechatUserChannelRepository.save(wechatUserChannel);
        } else {
            //it should never happen
            logger.warn(String.format("wechat user[%s] unsubscribed, but couldn't find corresponding cheche account", openID));
        }
    }

    public <T> T getUserInfo(String openID, Class<T> resultClass, Channel channel) {
        return getUserInfo(openID, null, "/cgi-bin/user/info", resultClass, channel);
    }

    public <T> T getUserInfo(String openID, String access_token, String url, Class<T> resultClass, Channel channel) {
        Map<String, Object> parameters = new HashMap<>();
        if (access_token != null) {
            parameters.put("access_token", access_token);
        }
        parameters.put("openid", openID);
        parameters.put("lang", "zh_CN");
        T result = messageSender.getMessageForObject(url, parameters, resultClass, access_token == null, WechatConstant.getAppId(channel));
        return result;

    }

    public void bindAgentAccount(String mobile, String openID, Channel channel) {
        Agent agent = agentRepository.findFirstByMobile(mobile);
        if (agent == null) {
            logger.warn("can't find agent with mobile: " + mobile);
            messageSender.sendTextMessage(openID, "无法找到匹配的手机.", channel);
            return;
        }

        WechatUserChannel wechatUserChannel = wechatUserChannelRepository.findFirstByOpenId(openID);
        WechatUserInfo wechatUserInfo = wechatUserChannel == null ? null : wechatUserChannel.getWechatUserInfo();
        if (wechatUserInfo == null) {
            logger.warn("can't find wechat user with open id: " + openID);
            return;
        }

        User user = wechatUserInfo.getUser();
        if (user == null) {
            user = new User();
            wechatUserInfo.setUser(user);
            userRepository.save(user);
        }
        cleanupExistBinding(agent, user);

        agent.setUser(user);
        agentRepository.save(agent);
        user.setUserType(UserType.Enum.Agent);
        userRepository.save(user);
        wechatUserInfoRepository.save(wechatUserInfo);

        messageSender.sendTextMessage(openID, "恭喜您绑定成功，您提交的信息审核通过，请仔细阅读 <a href='"+ WebConstants.getDomainURL()+"/license/agent-agreement.html'>《车车代理人服务协议》</a> ", channel);

    }

    private void cleanupExistBinding(Agent agent, User user) {

        if (null != agent.getUser()) {
            agent.getUser().setUserType(UserType.Enum.Customer);
            this.userRepository.save(agent.getUser());
            logger.debug("change a agent to customer, agent id: {}, user id {}", agent.getId(), agent.getUser().getId());
        }

        List<Agent> existedAgents = this.agentRepository.findByUser(user);
        if (!CollectionUtils.isEmpty(existedAgents)) {
            existedAgents.forEach(existedAgent -> {
                existedAgent.setUser(null);
                this.agentRepository.save(existedAgent);
                logger.debug("cleanup a agent related user, agent id {}", existedAgent.getId());
            });
        }
    }

    public void scanQRCode(String openID, String eventKey, String ticket) {
        //nothing to do for now, user has subscribed our public account
        saveScanData(eventKey);
    }

    /**
     * 用户取消关注时，进行取消关注后的事件推送，保存指定二维码关注数
     *
     * @param wechatUserChannel 微信用户
     */
    private void saveUnSubscribeData(WechatUserChannel wechatUserChannel) {
        logger.debug("用户取消关注微信公众号, openID：{}, qrcode:{}, qrcode channel:{}",
            wechatUserChannel.getOpenId(), wechatUserChannel.getQrcode(), wechatUserChannel.getQrcodeChannel());
        if (wechatUserChannel.getQrcodeChannel() != null) {
            QRCodeChannel qrCodeChannel = qrCodeChannelRepository.findOne(wechatUserChannel.getQrcodeChannel());
            if (qrCodeChannel != null) {
                String key = QRCodeChannelUtil.getCurrentKey();
                String hashKey = QRCodeChannelUtil.getCurrentHashKeyForSubscribe(qrCodeChannel.getId());
                stringRedisTemplate.opsForHash().increment(key, hashKey, -1);
            }
        }
    }

    /**
     * 用户未关注时，进行关注后的事件推送，保存指定二维码关注数
     *
     * @param eventKey 事件KEY值，qrscene_为前缀，后面为二维码的参数值
     */
    private void saveSubscribeData(String eventKey, WechatUserChannel wechatUserChannel) {
        logger.debug("用户关注微信公众号, eventKey：{}", eventKey);
        if (wechatUserChannel != null && wechatUserChannel.getQrcode() != null) {
            QRCodeChannel qrCodeChannel = qrCodeChannelRepository.findFirstByWechatQRCodeAndDisable(wechatUserChannel.getQrcode(), false);
            String key = QRCodeChannelUtil.getCurrentKey();
            String hashKey = QRCodeChannelUtil.getCurrentHashKeyForSubscribe(qrCodeChannel.getId());
            stringRedisTemplate.opsForHash().increment(key, hashKey, 1);
            // 将用户关注的渠道保存到用户信息中
            wechatUserChannel.setQrcodeChannel(qrCodeChannel.getId());
            wechatUserChannelRepository.save(wechatUserChannel);
        }
    }

    /**
     * 用户已关注时的事件推送，保存指定二维码扫描数
     *
     * @param eventKey 事件KEY值，是一个32位无符号整数，即创建二维码时的二维码scene_id
     */
    private void saveScanData(String eventKey) {
        if (logger.isDebugEnabled()) {
            logger.debug("用户扫描二维码, eventKey：{}", eventKey);
        }
        if (StringUtils.isNoneBlank(eventKey)) {
            String sceneId = eventKey;
            WechatQRCode wechatQRCode = wechatQRCodeRepository.findFirstBySceneId(Long.valueOf(sceneId));
            QRCodeChannel qrCodeChannel = qrCodeChannelRepository.findFirstByWechatQRCodeAndDisable(wechatQRCode.getId(), false);
            String key = QRCodeChannelUtil.getCurrentKey();
            String hashKey = QRCodeChannelUtil.getCurrentHashKeyForScan(qrCodeChannel.getId());
            stringRedisTemplate.opsForHash().increment(key, hashKey, 1);
        }
    }

}
