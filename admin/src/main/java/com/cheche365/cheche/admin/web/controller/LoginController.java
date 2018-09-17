package com.cheche365.cheche.admin.web.controller;

import com.cheche365.cheche.manage.common.service.InternalUserManageService;
import com.cheche365.cheche.common.util.ServletUtils;
import com.cheche365.cheche.core.model.InternalUserLoginLog;
import com.cheche365.cheche.core.service.InternalUserLoginLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * 该控制器类用于登录管理系统
 * Created by sunhuazhong on 2015/4/28.
 */
@Controller
@RequestMapping(value = "/")
public class LoginController {

    @Autowired
    private InternalUserManageService internalUserManageService;

    @Autowired
    private InternalUserLoginLogService internalUserLoginLogService;
    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public ModelAndView toIndex() {
        return new ModelAndView("redirect:/home.jsp");
    }

    @RequestMapping(value = "/admin/success", method = RequestMethod.GET)
    public ModelAndView success(HttpServletRequest request) {
        internalUserLoginLogService.saveLog(ServletUtils.getIP(request),internalUserManageService.getCurrentInternalUser(), InternalUserLoginLog.PLATEFORM.ADMINISTER);
        return new ModelAndView("redirect:/views/index.jsp");
    }
}
