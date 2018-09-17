package com.cheche365.cheche.alipay.web.controller;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


@Controller("alipayProposalController")
@RequestMapping("/web/alipay/channels/")
public class AlipayProposalController {

    private static final String STATE_PROPOSAL = "1"; //车险报价
    private static final String STATE_ORDER = "2"; //我的订单
    private static final String STATE_BUDGET = "3"; //预算车险
    private static final String STATE_BIND = "4"; //用户绑定
    private static final String STATE_COUPON = "5"; //我的优惠卷
    private static Logger logger = LoggerFactory.getLogger(AlipayProposalController.class);

    @Autowired
    private AlipayOAuthManager alipayOAuthManager;

    @RequestMapping("proposal")
    public String proposal(@RequestParam String state, HttpServletRequest request, HttpSession session) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("received proposal request, state [%s]", state));
        }
        User user = alipayOAuthManager.authenticate(request);
        if (logger.isDebugEnabled()) {
            //logger.debug("put user[" + user.getId() + "] into session [" + session.getId() +"]");
        }

        CacheUtil.cacheUser(session, user);
        ClientTypeUtil.cacheChannel(request, Channel.Enum.ALIPAY_21);

        String redirectURL = "redirect:" + WebConstants.getDomainURL() + "/app/app.html";

        switch (state) {
            case STATE_PROPOSAL:
                return redirectURL + "#buy";
            case STATE_ORDER:
                return redirectURL + "#order_result";
            case STATE_BUDGET:
                //change to m site url for testing
                return "redirect:" + WebConstants.getDomainURL() + "/m/";
            case STATE_BIND:
                return redirectURL + "#validation";
            case STATE_COUPON:
                return redirectURL + "#coupon";
            default:
                return redirectURL + "#buy";
        }

    }

}
