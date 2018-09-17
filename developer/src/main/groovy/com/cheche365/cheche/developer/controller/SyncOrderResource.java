package com.cheche365.cheche.developer.controller;

import com.cheche365.cheche.developer.service.SyncOrderService;
import com.cheche365.cheche.developer.vo.SyncOrder;
import com.cheche365.cheche.web.counter.annotation.NonProduction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import static com.cheche365.cheche.signature.Parameters.AUTHORIZATION_HEADER;

/**
 * @Author shanxf
 * @Date 2018/4/17  17:33
 */
@RestController
@RequestMapping(value = "/internal/developer/third")
public class SyncOrderResource {

    private static final Integer SUCCESS_CODE = 0 ;

    @Autowired
    private SyncOrderService syncOrderService;

    @RequestMapping(value = "/sync/order", method = RequestMethod.POST)
    @NonProduction
    public String processSync(HttpServletRequest request, @RequestBody SyncOrder syncOrder) {

        syncOrderService.saveLog(syncOrder, request.getHeader(AUTHORIZATION_HEADER));

        //咱们现有的响应机制无法返回code=0，默认为200
        return  "{\"code\":0,\"message\":\"同步成功\"}";
    }

    @RequestMapping(value = "/sync/agent", method = RequestMethod.POST)
    @NonProduction
    public String syncAgent(HttpServletRequest request) {
        return "{\"code\":0,\"message\":\"同步成功\"}";
    }
}
