package com.cheche365.cheche.partner.web.controller;

import com.cheche365.cheche.core.exception.BusinessException;
import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.model.noAuto.NoAutoUser;
import com.cheche365.cheche.core.service.NoAutoUserService;
import com.cheche365.cheche.web.ContextResource;
import com.cheche365.cheche.web.response.RestResponseEnvelope;
import com.cheche365.cheche.web.util.ClientTypeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * Created by shanxf on 2017/6/12.
 * 非车险用户相关入口
 */
@Controller
@RequestMapping("/"+ContextResource.VERSION_NO+"/partner/")
public class PartnerUserResource extends ContextResource{


    private Logger logger= LoggerFactory.getLogger(PartnerUserResource.class);

    @Autowired
    NoAutoUserService noAutoUserService;

    @RequestMapping(value = "user", method = RequestMethod.POST)
    public HttpEntity<RestResponseEnvelope<NoAutoUser>> createUser(@RequestBody Map map) {
        if(!ClientTypeUtil.isNoAutoUser(request)){
            throw  new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID,"不是非车险用户");
        }
        NoAutoUser noAutoUser= new NoAutoUser();
        if (map!=null){
            String uid =map.get("uid").toString();
            String mobile =map.get("mobile").toString();
            Channel channel = ClientTypeUtil.getChannel(request);
            logger.info("login cheche user uid:{} ,mobile:{}",uid,mobile);
            noAutoUser = noAutoUserService.createOrUpdate(uid, channel, mobile);
        }

        return new ResponseEntity<>(new RestResponseEnvelope(noAutoUser), HttpStatus.OK);
    }

    @RequestMapping(value = "/user",method = RequestMethod.GET)
    public  HttpEntity<RestResponseEnvelope> findByUid(@RequestParam(value = "uid",required = true) String uid){
        Channel channel = ClientTypeUtil.getChannel(request);
        logger.info("login cheche user uid:{}",uid);
        NoAutoUser noAutoUser = noAutoUserService.find(uid,channel);

        return  new ResponseEntity<>(new RestResponseEnvelope(noAutoUser),HttpStatus.OK);
    }
}
