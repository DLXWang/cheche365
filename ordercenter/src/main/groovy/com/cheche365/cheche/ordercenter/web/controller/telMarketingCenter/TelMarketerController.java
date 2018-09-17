package com.cheche365.cheche.ordercenter.web.controller.telMarketingCenter;

import com.cheche365.cheche.core.annotation.VisitorPermission;
import com.cheche365.cheche.core.model.ResultModel;
import com.cheche365.cheche.core.model.TelMarketingCenterRepeat;
import com.cheche365.cheche.ordercenter.exception.OrderCenterException;
import com.cheche365.cheche.ordercenter.model.TelMarketerViewModel;
import com.cheche365.cheche.manage.common.web.model.DataTablePageViewModel;
import com.cheche365.cheche.ordercenter.service.telMarketingCenter.TelMarketerService;
import com.cheche365.cheche.ordercenter.service.telMarketingCenter.TelMarketingCenterManageService;
import com.cheche365.cheche.ordercenter.third.clink.CallStatus;
import com.cheche365.cheche.ordercenter.third.clink.ClinkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 电销人员管理
 * Created by chenxiangyin on 2017/9/27.
 */
@RestController
@RequestMapping("/orderCenter/telMarketingCenter/telMarketer")
public class TelMarketerController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private ClinkService clinkService;
    @Autowired
    private TelMarketerService service;
    @Autowired
    private TelMarketingCenterManageService telMarketingCenterManageService;
    @RequestMapping(value = "", method = RequestMethod.GET)
    @VisitorPermission("or0606")
    public DataTablePageViewModel<TelMarketerViewModel>  getTelMarketerList(@RequestParam(value = "currentPage", required = true) Integer currentPage,
                                                                           @RequestParam(value = "pageSize", required = true) Integer pageSize,
                                                                           @RequestParam(value = "draw", required = true) Integer draw) {
        return service.telMarketerList(draw,currentPage,pageSize);
    }

    @RequestMapping(value = "/call", method = RequestMethod.GET)
    @VisitorPermission("or060104")
    public ResultModel call(@RequestParam(value = "customerNumber", required = true) String customerNumber) {
        TelMarketingCenterRepeat repeat = telMarketingCenterManageService.getRepeatByMobile(customerNumber);
        String tag = "";
        if(repeat != null){
            tag = "来源名称" + repeat.getSource().getDescription() + "来源id" + repeat.getSourceId();
        }
        CallStatus status = clinkService.call(customerNumber,tag);
        if(status == null){
            return new ResultModel(false,"");
        }
        if(status.getRes().equals(0)){
            return new ResultModel(true,"成功");
        }
        return new ResultModel(false,CallStatus.STATUS_MAP.get(status.getRes()).toString());
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    //@VisitorPermission("or060101,or060103,or060211")
    public ResultModel call(@RequestBody List<TelMarketerViewModel> formList){
        try {
            service.edit(formList);
        } catch (OrderCenterException e) {
            return new ResultModel(false, e.getMessage());
        }
        return new ResultModel();
    }
}
