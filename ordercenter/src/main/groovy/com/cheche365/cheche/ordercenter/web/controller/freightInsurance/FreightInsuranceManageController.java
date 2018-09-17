package com.cheche365.cheche.ordercenter.web.controller.freightInsurance;

import com.cheche365.cheche.core.annotation.VisitorPermission;
import com.cheche365.cheche.manage.common.web.model.DataTablePageViewModel;
import com.cheche365.cheche.ordercenter.service.freightInsurance.FreightInsuranceManageService;
import com.cheche365.cheche.ordercenter.web.model.freightInsurance.FreightInsuranceOrderRequestModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

/**
 * Created by yinJianBin on 2017/8/22.
 */
@RestController
@RequestMapping(value = "/orderCenter/freightInsurance")
public class FreightInsuranceManageController {

    @Autowired
    private FreightInsuranceManageService freightInsuranceManageService;

    @VisitorPermission("or1201")
    @RequestMapping(value = "/channel", method = RequestMethod.GET)
    public List<Map> channel() {
        List<Map> channels = freightInsuranceManageService.getChannels();
        return channels;
    }

    @VisitorPermission("or1201")
    @RequestMapping(value = "/category/{channel}", method = RequestMethod.GET)
    public List<Map<String, String>> goodsType(@PathVariable Long channel) {
        List<Map<String, String>> categorysMap = freightInsuranceManageService.getCategorysByChannel(channel);
        return categorysMap;
    }

    @VisitorPermission("or1201")
    @RequestMapping(value = "/order", method = RequestMethod.GET)
    public DataTablePageViewModel orderList(FreightInsuranceOrderRequestModel requestModel) throws JsonProcessingException {
        DataTablePageViewModel viewModel = freightInsuranceManageService.listOrder(requestModel);
        return viewModel;
    }

    @VisitorPermission("or1201")
    @RequestMapping(value = "/claim", method = RequestMethod.GET)
    public DataTablePageViewModel settlementList(FreightInsuranceOrderRequestModel requestModel) throws JsonProcessingException {
        DataTablePageViewModel viewModel = freightInsuranceManageService.listClaim(requestModel);
        return viewModel;
    }

    @VisitorPermission("or1201")
    @RequestMapping(value = "/order/{orderId}", method = RequestMethod.GET)
    public Map<String, String> freightOrderDetail(@PathVariable Long orderId) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Map<String, String> resultMap = freightInsuranceManageService.getOrderDetail(orderId);
        return resultMap;
    }

    @VisitorPermission("or1201")
    @RequestMapping(value = "/preCliam/{orderId}", method = RequestMethod.GET)
    public List<Map<String, String>> preClaimList(@PathVariable Long orderId) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        List<Map<String, String>> resultMap = freightInsuranceManageService.getPreClaimList(orderId);
        return resultMap;
    }


}
