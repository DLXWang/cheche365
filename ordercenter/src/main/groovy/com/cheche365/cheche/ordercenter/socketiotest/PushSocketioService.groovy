package com.cheche365.cheche.ordercenter.socketiotest

import com.cheche365.cheche.common.util.DateUtils
import com.cheche365.cheche.core.model.InternalUser
import com.cheche365.cheche.core.repository.InternalUserRepository
import com.cheche365.cheche.core.repository.TelMarketingCenterRepeatRepository
import com.cheche365.cheche.manage.common.model.TelMarketingCenter
import com.cheche365.cheche.manage.common.repository.TelMarketingCenterRepository
import com.corundumstudio.socketio.*
import com.corundumstudio.socketio.listener.ConnectListener
import com.corundumstudio.socketio.listener.DataListener
import com.corundumstudio.socketio.listener.DisconnectListener
import org.apache.commons.lang.StringUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service
/**
 * 通过socketio推送修改指定跟进人数量
 * Created by zhangpengcheng on 2018/6/4.
 */
@Service
public class PushSocketioService {
    @Autowired
    private TelMarketingCenterRepository telMarketingCenterRepository;

    @Autowired
    private InternalUserRepository internalUserRepository;

    @Autowired
    private TelMarketingCenterRepeatRepository telMarketingCenterRepeatRepository;

    @Autowired
    private Environment env;

    private org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());


    private static SocketIOServer server;
    private static HashMap<UUID, String> user_client_cache = new HashMap<UUID, String>();
    //客户端暂存
    private static HashMap<String, SocketIOClient> client_cache = new HashMap<String, SocketIOClient>();
    //String[] Domainlist = WebConstants.getDomainURL().split("//");
    //String[] Hostdomain = Domainlist[1].split(":");
    /*
     * 添加客户端
     */

    public void startSocketio() {
        Configuration config = new Configuration();
        config.setHostname("0.0.0.0");
        config.setPort(9092);
        config.setAllowCustomRequests(true);
        config.setAuthorizationListener(new AuthorizationListener() {
            @Override
            boolean isAuthorized(HandshakeData data) {
                return true;
            }
        });
        //config.setTransports(Transport.WEBSOCKET);
        config.setOrigin(null);
        config.setUpgradeTimeout(18000);
        SocketIOServer server = new SocketIOServer(config);

        server.addEventListener("regId", ChatObject.class,
            new DataListener<ChatObject>() {
                //实现dataListener的回调
                @Override
                public void onData(SocketIOClient client, ChatObject data,
                                   AckRequest ackSender) throws Exception {
                    Date currentTime = DateUtils.getCurrentDate(DateUtils.DATE_LONGTIME24_PATTERN);
                    Date startTime = DateUtils.getCustomDate(currentTime, 0, 0, 0, 0);
                    String userid = data.getUserName();
                    if (userid != null && StringUtils.isNotEmpty(userid)) {
                        client_cache.remove(userid);
                        //增加新的客户端
                        client_cache.put(userid, client);
                        user_client_cache.put(client.getSessionId(), data.getUserName());
                        //连接时将当前消息数量传上去
                        ChatObject content = new ChatObject();
                        Integer tmc = telMarketingCenterRepository.findNeedPushedData(startTime, currentTime, userid);
                        //int Messagelength = tmc.size();
                        content.setMessage(tmc.toString());
                        content.setUserName(userid);
                        client.sendEvent("push", content);
                    }
                }

            });

        ConnectListener connect = new ConnectListener() {

            @Override
            public void onConnect(SocketIOClient client) {
                ChatObject content = new ChatObject("abc", "a");
                client.sendEvent("linkevent", content);
                logger.info("socketio user" + client.getSessionId() + "login");
            }
        };

        server.addDisconnectListener(new DisconnectListener() {

            @Override
            public void onDisconnect(SocketIOClient client) {

                //根据客户端sessionID获取用户与client缓存中的信息
                String userid = user_client_cache.get(client.getSessionId());
                if (userid != null) {
                    if (client_cache.get(userid).getSessionId().equals(client.getSessionId())) {
                        //如果当前缓存中的client就是断开的client
                        //清除当前信息
                        client_cache.remove(userid);
                        logger.info("socketio user" + client.getSessionId() + "log out");

                    }
                    //清除关系缓存中的信息
                    user_client_cache.remove(client.getSessionId());
                }

            }
        });
        server.addConnectListener(connect);

        //启动服务
        server.start();
        Thread.sleep(Integer.MAX_VALUE);
        server.stop();

    }

    /*
     * 触发时的消息推送
     *
     */

    public void pushArr() {
        Date currentTime = DateUtils.getCurrentDate(DateUtils.DATE_LONGTIME24_PATTERN);
        Date startTime = DateUtils.getCustomDate(currentTime, 0, 0, 0, 0);
        //根据新传入的单子进行判断到底应该发给谁
        //TelMarketingCenterRepeat ChangedOne = (TelMarketingCenterRepeat) ((Message) payload).getPayload();
        String phoneNum = telMarketingCenterRepeatRepository.getCertainNeedRandom();
        InternalUser a = internalUserRepository.getEmailWithPhone(phoneNum);

        //发送消息数量
        if (a != null) {
            Integer tmcl = telMarketingCenterRepository.findNeedPushedData(startTime, currentTime, a.getEmail());
            ChatObject content = new ChatObject();
            content.setMessage(tmcl.toString());
            content.setUserName(a.getEmail());
            if (client_cache.get(a.getEmail()) != null) {
                SocketIOClient client = client_cache.get(a.getEmail());
                client.sendEvent("push", content);
            }
        }
    }

    /*
     * 启动服务
     */

    public void startServer() {
        if (server == null) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        startSocketio();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    /*
     * 停止服务
     */

    public void stopSocketio() {
        if (server != null) {
            server.stop();
            server = null;
        }
    }
}
