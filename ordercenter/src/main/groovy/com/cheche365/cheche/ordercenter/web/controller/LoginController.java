package com.cheche365.cheche.ordercenter.web.controller;

import com.cheche365.cheche.common.util.ServletUtils;
import com.cheche365.cheche.core.model.InternalUserLoginLog;
import com.cheche365.cheche.core.service.InternalUserLoginLogService;
import com.cheche365.cheche.ordercenter.annotation.LoginIn;
import com.cheche365.cheche.ordercenter.service.user.IInternalUserManageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * 该控制器类用于登录错误和权限错误处理
 * Created by sunhuazhong on 2015/4/28.
 */
@Controller
@RequestMapping(value = "/")
public class LoginController {

    private Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private IInternalUserManageService internalUserManageService;

    @Autowired
    private InternalUserLoginLogService internalUserLoginLogService;

    @RequestMapping(value = "/orderCenter", method = RequestMethod.GET)
    public ModelAndView toIndex() {
        return new ModelAndView("redirect:/home.jsp");
    }

    @RequestMapping(value = "/orderCenter/success", method = RequestMethod.GET)
    @LoginIn
    public ModelAndView success(HttpServletRequest request) {
        internalUserLoginLogService.saveLog(ServletUtils.getIP(request),internalUserManageService.getCurrentInternalUser(), InternalUserLoginLog.PLATEFORM.ORDER_CENTER);
        return new ModelAndView("redirect:/page/layout.html");
    }
}
