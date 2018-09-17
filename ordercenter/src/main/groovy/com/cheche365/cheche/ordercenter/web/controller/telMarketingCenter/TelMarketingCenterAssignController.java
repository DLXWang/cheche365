package com.cheche365.cheche.ordercenter.web.controller.telMarketingCenter;

import com.cheche365.cheche.ordercenter.service.telMarketingCenter.CustomerRedistributionService;
import com.cheche365.cheche.ordercenter.service.telMarketingCenter.TelMarketingCenterAssignBatchService;
import com.cheche365.cheche.ordercenter.web.model.telMarketingCenter.TelMarketingCenterRequestParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Created by yinJianBin on 2017/3/29.
 */
@RestController
@RequestMapping("/orderCenter/telMarketingCenter/assign")
public class TelMarketingCenterAssignController {

    private Logger logger = LoggerFactory.getLogger(TelMarketingCenterAssignController.class);

    @Autowired
    private TelMarketingCenterAssignBatchService telMarketingCenterAssignBatchService;

    @Autowired
    private CustomerRedistributionService customerRedistributionService;


    @RequestMapping(value = "/average", method = RequestMethod.GET)
    public Map<String, String> assignByAverage(TelMarketingCenterRequestParams param) {
        telMarketingCenterAssignBatchService.averageAssign(param, param.getOperatorIds());
        Map<String, String> userAssignInfo = customerRedistributionService.getUserAssignInfo();
        userAssignInfo.put("result", "success");
        return userAssignInfo;
    }

    @RequestMapping(value = "/custom", method = RequestMethod.GET)
    public Map<String, String> assignByCuntom(TelMarketingCenterRequestParams param) {
        telMarketingCenterAssignBatchService.customAssign(param);
        Map<String, String> userAssignInfo = customerRedistributionService.getUserAssignInfo();
        userAssignInfo.put("result", "success");
        return userAssignInfo;
    }

}
