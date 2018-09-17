package com.cheche365.cheche.rest.web.controller;

import com.cheche365.cheche.core.constants.WebConstants;
import com.cheche365.cheche.core.exception.BusinessException;
import com.cheche365.cheche.core.exception.TokenTimeOutException;
import com.cheche365.cheche.core.util.CacheUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhengwei on 5/17/16.
 * 处理spring不抛出的异常，比如request mapping出错，spring默认会redirect到error.html上，这种行为和目前异常处理机制不一致。
 * spring提供了两个属性关闭默认的行为，但在spring boot里不大灵，所以写了个默认处理这种异常的controller。
 * 详见: {@link https://github.com/spring-projects/spring-boot/issues/3980}
 */

@Controller
@RequestMapping("${error.path:/error}")
public class UncaughtExceptionController extends AbstractErrorController {

    @Value("${error.path:/error}")
    private String errorPath;


    Logger logger = LoggerFactory.getLogger(UncaughtExceptionController.class);

    public UncaughtExceptionController(ErrorAttributes errorAttributes) {
        super(errorAttributes);
    }

    @Override
    public String getErrorPath() {
        return this.errorPath;
    }

    @RequestMapping
    @ResponseBody
    public void error(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String,Object> errorMap = getErrorAttributes(request,true);
        int responseCode = (int)errorMap.get("status");

        Object isTokenOt=request.getAttribute(WebConstants.ORDER_CENTER_TOKEN);
        if(isTokenOt!=null&&(Boolean)isTokenOt){
            throw new TokenTimeOutException("token已过期,请重新获取token", null);
        }
        if(responseCode < 500){
            logger.debug("responseCode:{},stackTrace info:{}",responseCode, CacheUtil.doJacksonSerialize(errorMap));
            response.sendRedirect(request.getContextPath());
        } else {
            logger.error("responseCode:{},stackTrace info:{}",responseCode, CacheUtil.doJacksonSerialize(errorMap));
            throw new BusinessException(BusinessException.Code.INTERNAL_SERVICE_ERROR,"内部服务异常！");
        }
    }
}
