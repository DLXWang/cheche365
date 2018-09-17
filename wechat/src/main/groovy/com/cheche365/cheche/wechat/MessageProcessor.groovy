package com.cheche365.cheche.wechat;

import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.wechat.message.InMessage;
import com.cheche365.cheche.wechat.message.TransferSupportResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.cheche365.cheche.core.WechatConstant.CHEBAOYI_UNIQUE_ID
import static com.cheche365.cheche.core.model.Channel.Enum.PARTNER_CHEBAOYI_67;
import static com.cheche365.cheche.core.model.Channel.Enum.WE_CHAT_3;

/**
 * Created by liqiang on 3/24/15.
 */
@Component
public class MessageProcessor implements IMessageProcessor{

    @Autowired
    private UserManager userManager;

    @Autowired
    private MessageSender messageSender;

    private static String briefIntroduction = "巴菲特说：“没人喜欢买车险，但几乎所有人都爱开车。” 过去，您支付的每一笔车险保费都有15%进了代理人腰包，算一算这些年花了多少冤枉钱。 \n" +
        "车车--作为一家站在车主利益角度出发的移动互联网公司，还原车险真价格，帮您发现最好最便宜的车险！";

    private static String supportWelcome = "您好，我是车车客服，有什么可以帮您的？";

    @Override
    public String process(InMessage inMessage) {
        Channel channel = CHEBAOYI_UNIQUE_ID == inMessage.getToUserName() ? PARTNER_CHEBAOYI_67 : WE_CHAT_3
        if (inMessage.isEvent()){ //收到推送事件消息
            switch (EventType.valueOf(inMessage.getEvent())) {
                case 'subscribe':
                    userManager.subscribe(inMessage, channel);
                    break;
                case 'unsubscribe'://用户撤销关注
                    userManager.unsubscribe(inMessage.getFromUserName());
                    break;
                case 'SCAN'://已关注用户扫描二维码
                    userManager.scanQRCode(inMessage.getFromUserName(),inMessage.getEventKey(), inMessage.getTicket());
                    break;
                case 'VIEW'://用户点击链接菜单
                    break;
                case 'CLICK'://用户点击消息菜单
                    if ("V0001_CHECHE_BRIEF_INTRODUCTION".equals(inMessage.getEventKey())) {
                        messageSender.sendTextMessage(inMessage.getFromUserName(),briefIntroduction, channel);
                    }else if ("V0002_CHECHE_SUPPORT_WELCOME".equals(inMessage.getEventKey())){
                        messageSender.sendTextMessage(inMessage.getFromUserName(),supportWelcome, channel);
                    }
                    break;
                case 'LOCATION'://用户上传位置信息
                    break;
                case 'kf_create_session':
                    break;
                case 'kf_close_session':
                    break;
                case 'kf_switch_session':
                    break;
                default:
                    //should never enter this case
                    break;
            }
        }else{//用户发送消息给公众号
            if (inMessage.isText()){
                String content = inMessage.getContent();
                if (StringUtils.isNoneBlank(content)){
                    if (content.startsWith("代理")) {
                        String mobile = content.substring("代理".length());
                        userManager.bindAgentAccount(mobile, inMessage.getFromUserName(), channel);
                        return "";
                    }
                }
            }

            //转发消息到微信客服
            TransferSupportResponse transferSupportResponse = new TransferSupportResponse();
            transferSupportResponse.setCreateTime(System.currentTimeMillis());
            transferSupportResponse.setToUserName(inMessage.getFromUserName());
            transferSupportResponse.setFromUserName(inMessage.getToUserName());
            transferSupportResponse.setMsgType("transfer_customer_service");
            return transferSupportResponse.toXmlString();
        }

        return "";
    }

}
