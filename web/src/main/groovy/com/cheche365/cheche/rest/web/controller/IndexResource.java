package com.cheche365.cheche.rest.web.controller;


import com.cheche365.cheche.core.constants.Device;
import com.cheche365.cheche.core.constants.WebConstants;
import com.cheche365.cheche.core.model.BusinessActivity;
import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.repository.BusinessActivityRepository;
import com.cheche365.cheche.web.ContextResource;
import com.cheche365.cheche.web.util.ClientTypeUtil;
import com.cheche365.cheche.web.util.UrlUtil;
import com.cheche365.cheche.web.util.UserDeviceUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * add by chenxz
 * 判断用户设备状态controller
 */
@RestController
public class IndexResource extends ContextResource {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public static final String DEFAULT_PATH = WebConstants.WEB_ROOT_PATH;

    public static final String ACTIVITY_PARAMETER = "activity=";
    public static final String ACTIVITY_M_SUFFIX = "-m";

    @Autowired
    private BusinessActivityRepository activityRepository;

    /**
     * 判断用户设备并跳转到不同页面,pc,mobile,tablet
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView index(HttpServletRequest request, HttpServletResponse response) {
        String indexPage = DEFAULT_PATH;
        Device device = UserDeviceUtil.getDeviceType(request);
        String queryStr = request.getQueryString();

        if (!StringUtils.isBlank(queryStr) && queryStr.contains(ACTIVITY_PARAMETER)) {
            BusinessActivity businessActivity = getBusinessActivityByQueryStr(queryStr, device);
            if (businessActivity != null) {
                indexPage = businessActivity.getLandingPage();
            }
        } else {
            if (device.equals(Device.COMPUTER)) {
                indexPage = DEFAULT_PATH;
            }
        }
        ClientTypeUtil.cacheChannel(request);
        return new ModelAndView("redirect:" + UrlUtil.toFullUrl(request, indexPage));
    }

    @RequestMapping(value = "/m", method = RequestMethod.GET)
    public ModelAndView index(){
        return new ModelAndView("redirect:" + WebConstants.M_ROOT_PATH);
    }

    private BusinessActivity getBusinessActivityByQueryStr(String queryStr, Device device) {
        if (StringUtils.isBlank(queryStr) || !queryStr.contains(ACTIVITY_PARAMETER)) {
            return null;
        }

        String[] parameters = queryStr.split("&");
        for (String parameter : parameters) {
            if (parameter.startsWith(ACTIVITY_PARAMETER)) {
                String activityCodePrefix = parameter.substring(parameter.indexOf(ACTIVITY_PARAMETER) + ACTIVITY_PARAMETER.length());
                String activityCode;
                if (device.equals(Device.MOBILE)) {
                    activityCode = activityCodePrefix + ACTIVITY_M_SUFFIX;
                } else {
                    return null;
                }
                return activityRepository.findFirstByCode(activityCode.toLowerCase());
            }
        }

        return null;
    }

}
