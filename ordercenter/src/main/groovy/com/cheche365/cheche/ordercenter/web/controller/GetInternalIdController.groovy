package com.cheche365.cheche.ordercenter.web.controller

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.cheche365.cheche.core.constants.WebConstants
import com.cheche365.cheche.core.model.InternalUser
import com.cheche365.cheche.manage.common.service.InternalUserManageService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
/**
 * 获取前端页面登录人邮箱
 * Created by zhangpengcheng on 2018/6/4.
 */
@RestController
@RequestMapping("/internalId")
public class GetInternalIdController {
    @Autowired
    private InternalUserManageService internalUserManageService;


    @RequestMapping(value = "/user",method = RequestMethod.GET)
    public JSONObject getUser() {
        InternalUser self = internalUserManageService.getCurrentInternalUser();
        JSONObject object=new JSONObject();
        object.put("email",self.getEmail());

        String[] Domainlist = WebConstants.getDomainURL().split(":");
        String domain =Domainlist[0]+":"+Domainlist[1];
        object.put("domain",domain);
        return  object;
    }
}
