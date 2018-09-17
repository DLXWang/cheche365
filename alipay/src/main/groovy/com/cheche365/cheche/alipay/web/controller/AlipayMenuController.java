package com.cheche365.cheche.alipay.web.controller;

import com.cheche365.cheche.alipay.web.AlipayMenuManager;
import com.cheche365.cheche.alipay.web.AlipayOAuthManager;
import com.cheche365.cheche.core.constants.WebConstants;
import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.model.User;
import com.cheche365.cheche.core.util.CacheUtil;
import com.cheche365.cheche.web.util.ClientTypeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Controller
@RequestMapping("/web/alipay/channels/")
public class AlipayMenuController {

    private static Logger logger = LoggerFactory.getLogger(AlipayMenuController.class);
    private final Set<String> states = new HashSet<>(Arrays.asList("home", "base", "reserve", "claim", "mine", "bind"));
    @Autowired
    private AlipayOAuthManager alipayOAuthManager;
    @Autowired
    private AlipayMenuManager menuManager;

    @RequestMapping("menu/dispatch")
    public String dispatch(@RequestParam String state, HttpServletRequest request, HttpSession session) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("received proposal request, state [%s]", state));
        }
        Channel channel= Channel.Enum.ALIPAY_21;
        User user = alipayOAuthManager.authenticate(request, channel);
        if (logger.isDebugEnabled()) {
            logger.debug("put user[" + user.getId() + "] into session [" + session.getId() + "]");
        }
        CacheUtil.cacheUser(session, user);
        ClientTypeUtil.cacheChannel(request, channel);

        StringBuilder url = new StringBuilder()
            .append("redirect:"+WebConstants.getDomainURL())
            .append("/m/index.html");
        if (states.contains(state)) {
            url.append("#").append(state);
        }
        return url.toString();
    }

    @RequestMapping(value = "menu/redirect/{state}", method = RequestMethod.GET)
    @ResponseBody
    public String redirect(@PathVariable("state") String state, HttpServletRequest request, HttpServletResponse response) {
        if ("downloadApp".equals(state)) {
            try {
                response.sendRedirect(WebConstants.getDomainURL() + "/web/h5/share.html");
            } catch (IOException e) {
                logger.error("重定向到downloadApp时出错", e);
            }
        }
        return "";
    }


    @RequestMapping(value = "menu/create", method = RequestMethod.POST)
    @ResponseBody
    public String createMenuWithName() {
        return menuManager.create();
    }

    @RequestMapping(value = "menu/update", method = RequestMethod.POST)
    @ResponseBody
    public String updateMenu() {
        return menuManager.update();
    }


    @RequestMapping(value = "menu/delete", method = RequestMethod.POST)
    @ResponseBody
    public String deleteMenu() {
        return menuManager.delete();
    }

    @RequestMapping(value = "menu/query", method = RequestMethod.GET)
    @ResponseBody
    public String queryMenu() {
        String menu = menuManager.query();
        return menu;

    }

}
