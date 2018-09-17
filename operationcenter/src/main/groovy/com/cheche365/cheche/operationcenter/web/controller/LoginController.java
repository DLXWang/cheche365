package com.cheche365.cheche.operationcenter.web.controller;

import com.cheche365.cheche.common.util.ServletUtils;
import com.cheche365.cheche.core.model.InternalUserLoginLog;
import com.cheche365.cheche.core.service.InternalUserLoginLogService;
import com.cheche365.cheche.manage.common.service.InternalUserManageService;
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
    private InternalUserManageService internalUserManageService;

    @Autowired
    private InternalUserLoginLogService internalUserLoginLogService;

    @RequestMapping(value = "/operationcenter", method = RequestMethod.GET)
    public ModelAndView toIndex() {
        return new ModelAndView("redirect:/home.jsp");
    }

    @RequestMapping(value = "/operationcenter/success", method = RequestMethod.GET)
    public ModelAndView success(HttpServletRequest request) {
        internalUserLoginLogService.saveLog(ServletUtils.getIP(request),internalUserManageService.getCurrentInternalUser(), InternalUserLoginLog.PLATEFORM.OPERATION_CENTER);
        return new ModelAndView("redirect:/views/index.jsp");
    }
}
