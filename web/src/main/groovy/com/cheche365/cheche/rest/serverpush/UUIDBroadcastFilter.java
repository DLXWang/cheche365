package com.cheche365.cheche.rest.serverpush;

import net.sf.json.JSONObject;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.PerRequestBroadcastFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.cheche365.cheche.rest.serverpush.SPConstants.KEY_CLIENT_ID;
import static com.cheche365.cheche.rest.serverpush.SPConstants.KEY_MESSAGE_ID;

/**
 * Created by liqiang on 6/5/15.
 */
public class UUIDBroadcastFilter implements PerRequestBroadcastFilter{


    @Override
    public BroadcastAction filter(String broadcasterId, AtmosphereResource resource, Object originalMessage, Object message) {

        JSONObject object = JSONObject.fromObject(message);
        String messageId = (String) object.get(KEY_MESSAGE_ID);

        Object clientId = resource.getRequest().getAttribute(KEY_CLIENT_ID);

        return null!=clientId && clientId.equals(messageId) ?
            new BroadcastAction(BroadcastAction.ACTION.CONTINUE, message) :
            new BroadcastAction(BroadcastAction.ACTION.ABORT,originalMessage);
    }

    @Override
    public BroadcastAction filter(String broadcasterId, Object originalMessage, Object message) {
        return new BroadcastAction(message);
    }
}
