package com.cheche365.cheche.rest.web.controller;

import com.cheche365.cheche.core.model.Auto;
import com.cheche365.cheche.core.model.agent.Customer;
import com.cheche365.cheche.core.model.agent.CustomerAuto;
import com.cheche365.cheche.core.repository.Page;
import com.cheche365.cheche.core.service.agent.CustomerService;
import com.cheche365.cheche.web.ContextResource;
import com.cheche365.cheche.web.response.RestResponseEnvelope;
import com.cheche365.cheche.web.version.VersionedResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Created by mahong on 16/06/2017.
 * 代理人系统-客户管理相关
 */
@RestController
@RequestMapping("/" + ContextResource.VERSION_NO + "/customers")
@VersionedResource(from = "1.5")
public class CustomerResource extends ContextResource {

    @Autowired
    private CustomerService customerService;

    @RequestMapping(value = "", method = RequestMethod.POST)
    public HttpEntity<RestResponseEnvelope> createIfNotExists(@RequestBody Customer customer) {
        return getResponseEntity(customerService.createIfNotExists(currentUser(), customer));
    }

    @RequestMapping(value = "{customerId}/auto", method = RequestMethod.POST)
    public HttpEntity<RestResponseEnvelope> createCustomerAuto(@PathVariable Long customerId, @RequestBody Auto auto) {
        CustomerAuto customerAuto = customerService.createCustomerAuto(currentUser(), customerId, auto);
        return getResponseEntity(customerAuto.getAuto());
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope> getCustomers(@RequestParam(value = "page", required = false) Integer page,
                                                         @RequestParam(value = "size", required = false) Integer size) {

        Pageable pageable = new PageRequest(toPageStart(page), toPageSize(size));
        Page<Object> customerAutos = customerService.findCustomerAutos(currentUser(), pageable);
        return getResponseEntity(customerAutos);
    }

    @RequestMapping(value = "{customerId}/bills", method = RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope> getCustomerDetail(@PathVariable Long customerId) {
        Map result = customerService.findBillsByCustomer(currentUser(), customerId);
        return getResponseEntity(result);
    }
}
