package com.cheche365.cheche.rest.web.controller;


import com.cheche365.cheche.core.model.BusinessActivity;
import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.model.MessageType;
import com.cheche365.cheche.core.repository.MessageTypeRepository;
import com.cheche365.cheche.core.service.ModuleService;
import com.cheche365.cheche.core.service.SystemCountService;
import com.cheche365.cheche.core.service.WebPurchaseOrderService;
import com.cheche365.cheche.web.ContextResource;
import com.cheche365.cheche.web.counter.annotation.NonProduction;
import com.cheche365.cheche.web.response.RestResponseEnvelope;
import com.cheche365.cheche.web.util.ClientTypeUtil;
import com.cheche365.cheche.web.version.VersionedResource;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.cheche365.cheche.core.model.MessageType.Enum.A_HOME_TOP_IMAGE;

/**
 * add by zhaozhong
 * module资源resource,用于统一
 */
@RestController
@RequestMapping("/" + ContextResource.VERSION_NO + "/module")
@VersionedResource(from = "1.3")
public class ModuleResource extends ContextResource {

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private MessageTypeRepository messageTypeRepository;

    @Autowired
    private SystemCountService systemCountService;

    @Autowired
    private WebPurchaseOrderService webPurchaseOrderService;

    @RequestMapping(value = "messageType/keys", method = RequestMethod.GET)
    @VersionedResource(from = "1.5")
    @NonProduction
    public HttpEntity<RestResponseEnvelope> resourceKeys() {
        List<MessageType> messageTypeList = Lists.newArrayList(messageTypeRepository.findAll());
        List<MessageType> messageTypes = messageTypeList.stream()
            .filter(it -> it.getId() > 11L).collect(Collectors.toList());
        RestResponseEnvelope restResponseEnvelope = new RestResponseEnvelope<>(messageTypes);
        return new ResponseEntity(restResponseEnvelope, HttpStatus.OK);
    }

    @RequestMapping(value = "resource", method = RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope> resource(@RequestParam Map<String, String> payload, HttpServletRequest httpServletRequest) {

        String keys = String.valueOf(payload.get("keys"));
        BusinessActivity businessActivity = businessActivity();
        final String[] keyArray = keys.split(",");
        final Channel channel = ClientTypeUtil.getChannel(request);
        Map<String, Object> result = Maps.newHashMap();
        for (String keyName : keyArray) {
            if(MessageType.Enum.COUNT.getType().equals(keyName)){
                result.put(keyName, systemCountService.getSystemCountMap());
            }else {
                result.put(keyName, moduleService.homeMessages(keyName, businessActivity, channel));
            }
        }

        return getResponseEntity(result);
    }

    @RequestMapping(value = "basebanner", method = RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope> baseBanner(@RequestParam Map<String, String> payload, HttpServletRequest httpServletRequest) {
        String companyId = payload.get("companyId");
        final Channel channel = ClientTypeUtil.getChannel(httpServletRequest);

        Object result = moduleService.findBaseBanner(companyId, channel);
        if(null!=result && result instanceof Map){
            ((Map)result).put("marketingCode", payload.get("marketingCode"));
        }
        return getResponseEntity(result);

    }
    /**
     * 当用户登陆状态下，查询当前用户是否有一键续保的订单
     *  return{
     *      "orderNo":"订单号",
     *      "type" :""(引用自DisplayConstants)
     *  }
     *  or null
     */
    @RequestMapping(value = "/renewal",method = RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope> oneKeyRenewal(){

        Channel channel = getChannel();
        List<Map> map= webPurchaseOrderService.renewalOrder(currentUser(),channel);

        return new ResponseEntity<>(new RestResponseEnvelope(map), HttpStatus.OK);
    }
}
