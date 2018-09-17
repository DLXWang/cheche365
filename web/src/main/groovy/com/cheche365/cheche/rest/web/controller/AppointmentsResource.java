package com.cheche365.cheche.rest.web.controller;

import com.cheche365.cheche.core.constants.WebConstants;
import com.cheche365.cheche.core.model.AppointmentInsurance;
import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.service.AppointmentInsuranceService;
import com.cheche365.cheche.core.util.CacheUtil;
import com.cheche365.cheche.web.ContextResource;
import com.cheche365.cheche.web.response.RestResponseEnvelope;
import com.cheche365.cheche.web.util.ClientTypeUtil;
import com.cheche365.cheche.web.version.VersionedResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by zhengwei on 2015/7/11.
 * 预约车险
 */

@RestController
@RequestMapping("/"+ContextResource.VERSION_NO+"/appointments")
@VersionedResource(from = "1.0")
public class AppointmentsResource extends ContextResource {

    private Logger logger = LoggerFactory.getLogger(AppointmentsResource.class);

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    private AppointmentInsuranceService appointmentInsuranceService;

    @RequestMapping(value="", method = RequestMethod.POST)
    public HttpEntity<RestResponseEnvelope> makeAppointments(@RequestBody AppointmentInsurance appointmentInsurance, HttpServletRequest request) {

        appointmentInsurance.setCreateTime(new Date());
        appointmentInsurance.setUser(this.currentUser());

        logger.debug("the current user id is "+this.currentUser().getId());

        final Channel channel = ClientTypeUtil.getChannel(request);
        AppointmentInsurance afterSave = this.appointmentInsuranceService.addAppointment(appointmentInsurance, channel);
        logger.debug("新增预约车险纪录，id {}, user: {}", afterSave.getId(), this.currentUser().getId());

        Map<String, Long> result = new HashMap<>();
        result.put("id", afterSave.getId());
        return this.getResponseEntity(result);
    }

    @RequestMapping(value="{appointmentId}", method = RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope> getAppointment(@PathVariable Long appointmentId){

        return this.getResponseEntity(this.appointmentInsuranceService.getAppointment(appointmentId, this.currentUser()));
    }


    @RequestMapping(value="", method= RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope> getAppointments( @RequestParam(value = "page", required = false) Integer page,
                                                             @RequestParam(value = "size", required = false) Integer size) {

        Pageable pageable = new PageRequest(toPageStart(page), toPageSize(size));

        return this.getResponseEntity(this.appointmentInsuranceService.getAppointmentPage(this.currentUser(), pageable));
    }

    /**
     * 目前在用，以后会统一用SystemVersionResource.getOrderCount
     * @return
     */
    @RequestMapping(value="/count", method = RequestMethod.GET)
    public int getAppointmentsCount(){

        Random rand = new Random();
        Long count = this.appointmentInsuranceService.getAppointmentsCount();
        int appion = 2015 + count.intValue() * 3 + rand.nextInt(3);
        boolean firstTime =  CacheUtil.getValueToObject(redisTemplate,WebConstants.PUT_VALUE_WITH) == null ;
        int valueTwo = firstTime ? appion :  (int) CacheUtil.getValueToObject(redisTemplate,WebConstants.PUT_VALUE_WITH);
        if(valueTwo < appion || firstTime){
            CacheUtil.putValue(redisTemplate, WebConstants.PUT_VALUE_WITH, appion);
        }
       return  Math.max(valueTwo,appion);
    }

}
