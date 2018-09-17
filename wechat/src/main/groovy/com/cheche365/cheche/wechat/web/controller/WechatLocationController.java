package com.cheche365.cheche.wechat.web.controller;

import com.cheche365.cheche.externalapi.api.location.LocationAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * Created by chenqc on 2016/11/14.
 */
@Controller
public class WechatLocationController {

    @Autowired
    private LocationAPI locationAPI;

    @RequestMapping(value = "/wechat/city", method = RequestMethod.POST)
    @ResponseBody
    public String getCity(@RequestBody Map<String, String> location) {

        return locationAPI.call(location.get("location"));
    }
}
