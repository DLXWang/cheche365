package com.cheche365.cheche.wechat;

import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.wechat.message.json.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * Created by liqiang on 3/27/15.
 */
@Component
public class MenuManager {

    @Autowired
    private MessageSender messageSender;

    public Result createMenu(String menu) {
        String path = "cgi-bin/menu/create";
        return messageSender.postMessage(path, new HashMap<>(), menu, Result.class, Channel.Enum.WE_CHAT_3);
    }

}
