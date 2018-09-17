package com.cheche365.cheche.developer.controller;

import com.cheche365.cheche.core.constants.WebConstants;
import com.cheche365.cheche.web.counter.annotation.NonProduction;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import static com.cheche365.cheche.core.util.RuntimeUtil.isDevEnv;

/**
 * @Author shanxf
 * @Date 2018/4/23  9:58
 */
@Controller
public class SwaggerController {

    @Autowired
    ApplicationContext appContext;


    @RequestMapping(value = "/internal/developer/swagger/api-docs/{channel}", method = RequestMethod.GET)
    @NonProduction
    protected ModelAndView toAPIDoc(@PathVariable(name = "channel") String channel) {
        return new ModelAndView("redirect:"+getDomainURL()+"/internal/developer/swagger/api-docs/"+channel+"/api-docs");
    }

    @RequestMapping(value = "/internal/developer/swagger/{channel}/index",method = RequestMethod.GET)
    @NonProduction
    public ModelAndView toIndex(@PathVariable(name = "channel") String channel){

        if(StringUtils.isBlank(channel)){
            channel = "c";
        }
        return new ModelAndView("redirect:"+getDomainURL()+"/internal/developer/swagger/index.html?channel="+channel);
    }

    private String getDomainURL() {
        String domainURL = WebConstants.getDomainURL();
        if(isDevEnv()){
            domainURL = "";
        }
        return domainURL;
    }


}
