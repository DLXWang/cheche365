package com.cheche365.cheche.alipay.web.factory;

import com.alipay.api.internal.util.StringUtils;
import com.cheche365.cheche.alipay.common.AliPayException;
import com.cheche365.cheche.alipay.constants.AlipayServiceEventConstants;
import com.cheche365.cheche.alipay.constants.AlipayServiceNameConstants;
import com.cheche365.cheche.alipay.web.executor.ActionExecutor;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 业务动作分发器
 */
@Component
public class ActionExecutorFactory {

    @Resource(name = "defaultExecutor")
    private ActionExecutor defaultExecutor;
    @Resource(name = "followExecutor")
    private ActionExecutor followExecutor;
    @Resource(name = "unFollowExecutor")
    private ActionExecutor unFollowExecutor;
    @Resource(name = "verifyExecutor")
    private ActionExecutor verifyExecutor;

    /**
     * 根据业务参数获取业务执行器
     */
    public ActionExecutor getExecutor(final String service, final JSONObject bizContentJson) throws AliPayException {
        final String msgType = bizContentJson.getString("MsgType");
        // 1.获取消息类型
        if (StringUtils.isEmpty(msgType)) {
            throw new AliPayException("无法取得消息类型");
        }
        // 2.根据消息类型(msgType)进行执行器的分类转发
        // 2.1 事件类型
        if ("event".equals(msgType)) {
            return getEventExecutor(service, bizContentJson);
        } else {
            // 2.2 后续支付宝还会新增其他类型，因此默认返回ack应答
            return defaultExecutor;
        }
    }

    /**
     * 根据事件类型细化查找对应执行器
     */
    private ActionExecutor getEventExecutor(String service, JSONObject bizContentJson)
        throws AliPayException {
        // 1. 获取事件类型
        String eventType = bizContentJson.getString("EventType");
        if (StringUtils.isEmpty(eventType)) {
            throw new AliPayException("无法取得事件类型");
        }
        // 2.根据事件类型再次区分服务类型
        // 2.1 激活验证开发者模式
        if (AlipayServiceNameConstants.ALIPAY_CHECK_SERVICE.equals(service)
            && eventType.equals(AlipayServiceEventConstants.VERIFYGW_EVENT)) {
            return verifyExecutor;
            // 2.2 其他消息通知类
        } else if (AlipayServiceNameConstants.ALIPAY_PUBLIC_MESSAGE_NOTIFY.equals(service)) {
            return getMsgNotifyExecutor(eventType, bizContentJson);
            // 2.3 对于后续支付宝可能新增的类型，统一默认返回AKC响应
        } else {
            return defaultExecutor;
        }
    }

    /**
     * 根据事件类型(eventType)进行执行器的分类转发
     */
    private ActionExecutor getMsgNotifyExecutor(String eventType, JSONObject bizContentJson)
        throws AliPayException {
        if (eventType.equals(AlipayServiceEventConstants.FOLLOW_EVENT)) {
            // 服务窗关注事件
            return followExecutor;
        } else if (eventType.equals(AlipayServiceEventConstants.UNFOLLOW_EVENT)) {
            //  服务窗取消关注事件
            return unFollowExecutor;
            //根据actionParam进行执行器的转发
        } else if (eventType.equals(AlipayServiceEventConstants.CLICK_EVENT)) {
            // 点击事件
            return getClickEventExecutor(bizContentJson);
        } else {
            // 对于后续支付宝可能新增的类型，统一默认返回AKC响应
            return defaultExecutor;
        }

    }

    /**
     * 点击事件执行器
     */
    private ActionExecutor getClickEventExecutor(JSONObject bizContentJson) {
        String actionParam = bizContentJson.getString("ActionParam");
        if ("async_image_text".equals(actionParam)) {
            return defaultExecutor;
        } else {
            return null;
        }
    }


}
