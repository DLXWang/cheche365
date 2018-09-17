package com.cheche365.cheche.ordercenter.web.controller.telMarketingCenter;

import com.cheche365.cheche.core.annotation.VisitorPermission;
import com.cheche365.cheche.core.model.InternalUser;
import com.cheche365.cheche.manage.common.model.TelMarketingCenter;
import com.cheche365.cheche.core.service.InternalUserService;
import com.cheche365.cheche.manage.common.service.TelMarketingCenterService;
import com.cheche365.cheche.ordercenter.service.telMarketingCenter.CustomerRedistributionService;
import com.cheche365.cheche.ordercenter.web.model.CustomerReAssignViewModel;
import com.cheche365.cheche.manage.common.web.model.DataTablePageViewModel;
import com.cheche365.cheche.manage.common.web.model.ResultModel;
import com.cheche365.cheche.ordercenter.web.model.telMarketingCenter.TelMarketingCenterRequestParams;
import com.cheche365.cheche.ordercenter.web.model.telMarketingCenter.TelMarketingCenterViewModel;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Created by xu.yelong on 2016-04-29.
 */
@RestController
@RequestMapping(value = "/orderCenter/telMarketingCenter/redistribution")
public class CustomerRedistributionController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private TelMarketingCenterService telMarketingCenterService;

    @Autowired
    private CustomerRedistributionService customerRedistributionService;

    @Autowired
    private InternalUserService internalUserService;

    @VisitorPermission("or0603")
    @RequestMapping(value = "/mobile", method = RequestMethod.GET)
    public CustomerReAssignViewModel findByMobile(@RequestParam(value = "phoneNo", required = true) String phoneNo) {
        List<TelMarketingCenter> telMarketingCenterList = telMarketingCenterService.findByMobile(phoneNo);
        if (CollectionUtils.isEmpty(telMarketingCenterList)) {
            return new CustomerReAssignViewModel(false, "手机号不存在");
        }
        return createViewModelByMobile(telMarketingCenterList);
    }

    @VisitorPermission("or0603")
    @RequestMapping(value = "/findByOperator", method = RequestMethod.GET)
    public CustomerReAssignViewModel findByOperator(@RequestParam(value = "operatorId", required = true) Long operatorId,
                                                    @RequestParam(value = "status", required = true) Long status) {
        int count = customerRedistributionService.getCountByAssigner(operatorId,status);
        if (status==null && count < 1) {
            return new CustomerReAssignViewModel(false, "所选跟进人没有可分配客户，请重新选择");
        }
        List<InternalUser> internalUserList = customerRedistributionService.getAssignersByOperatorId(operatorId);
        return createViewModelByOperator(operatorId, internalUserList, count);
    }

    @VisitorPermission("or0603")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public DataTablePageViewModel<TelMarketingCenterViewModel> getDataList(@RequestParam(value = "operatorId", required = true) Long operatorId,
                                                                           @RequestParam(value = "currentPage", required = true) Integer currentPage,
                                                                           @RequestParam(value = "pageSize", required = true) Integer pageSize,
                                                                           @RequestParam(value = "draw") Integer draw,
                                                                           @RequestParam(value = "status") Long status) {

        DataTablePageViewModel<TelMarketingCenterViewModel> dataView = customerRedistributionService.findDataByOperator(operatorId, currentPage, pageSize, draw,status);
        return dataView;
    }


    @VisitorPermission("or0603")
    @RequestMapping(value = "/reassignByMobile", method = RequestMethod.POST)
    public ResultModel reAssignByMobile(@RequestParam(value = "phoneNo", required = true) String phoneNo,
                                        @RequestParam(value = "newOperatorId", required = true) Long newOperatorId) {
        try {
            customerRedistributionService.redistributeByMobile(phoneNo, newOperatorId);
            return new ResultModel(true, "指定分配人成功！");
        } catch (Exception e) {
            return new ResultModel(false, "指定分配人失败！");
        }
    }

    @VisitorPermission("or0603")
    @RequestMapping(value = "/reassignByOperator", method = RequestMethod.POST)
    public ResultModel reAssignByOperator(@RequestParam(value = "oldOperatorId", required = true) Long oldOperatorId,
                                          @RequestParam(value = "newOperatorId", required = false) Long newOperatorId,
                                          @RequestParam(value = "distributionMethod", required = true) String distributionMethod,
                                          @RequestParam(value = "checkedIds") String[] checkedIds) {
        try {
            return customerRedistributionService.redistributeByOperator(oldOperatorId, newOperatorId, distributionMethod, checkedIds);
        } catch (Exception e) {
            logger.error("指定分配人失败！", e);
            return new ResultModel(false, "指定分配人失败！");
        }
    }

    @VisitorPermission("or060301")
    @RequestMapping(value = "/userAssignInfo", method = RequestMethod.GET)
    public Map<String, String> GetUserAssignInfo() {
        Map<String, String> resultMap = customerRedistributionService.getUserAssignInfo();
        return resultMap;
    }

    @VisitorPermission("or060301")
    @RequestMapping(value = "/param/list")
    public Map<String, String> paramSearch(TelMarketingCenterRequestParams params) throws Exception {
        logger.info("根据条件筛选数据,条件参数--> {}", params.toString());
        Map<String, String> resultMap = customerRedistributionService.paramSearch(params);
        return resultMap;
    }


    private CustomerReAssignViewModel createViewModelByMobile(List<TelMarketingCenter> telMarketingCenterList) {
        CustomerReAssignViewModel customerReAssignViewModel = new CustomerReAssignViewModel();
        StringBuffer oldOperatorNames = new StringBuffer();
        List<InternalUser> operatorList = customerRedistributionService.listAllEnableTelCommissioner();
        for (TelMarketingCenter telMarketingCenter : telMarketingCenterList) {
            if (telMarketingCenter.getOperator() != null) {
                oldOperatorNames.append(telMarketingCenter.getOperator().getName()).append(" ");
                operatorList.remove(telMarketingCenter.getOperator());
            }
        }
        customerReAssignViewModel.setOldOperatorName(oldOperatorNames.toString());
        customerReAssignViewModel.setNewOperatorList(operatorList);
        return customerReAssignViewModel;
    }

    public CustomerReAssignViewModel createViewModelByOperator(Long operatorId, List<InternalUser> internalUserList, Integer count) {
        CustomerReAssignViewModel customerReAssignViewModel = new CustomerReAssignViewModel();
        InternalUser internalUser = internalUserService.getInternalUserById(operatorId);
        customerReAssignViewModel.setOldOperatorName(internalUser.getName());
        customerReAssignViewModel.setOldOperatorId(operatorId);
        customerReAssignViewModel.setNewOperatorList(internalUserList);
        customerReAssignViewModel.setCount(count);
        return customerReAssignViewModel;
    }
}
