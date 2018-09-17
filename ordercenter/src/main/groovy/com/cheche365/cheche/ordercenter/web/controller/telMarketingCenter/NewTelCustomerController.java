package com.cheche365.cheche.ordercenter.web.controller.telMarketingCenter;

import com.cheche365.cheche.core.annotation.VisitorPermission;
import com.cheche365.cheche.ordercenter.service.telMarketingCenter.NewTelCustomerService;
import com.cheche365.cheche.manage.common.web.model.ResultModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by xu.yelong on 2016-05-03.
 */
@RestController
@RequestMapping("/orderCenter/telMarketingCenter/inputData")
public class NewTelCustomerController {
    Logger logger= LoggerFactory.getLogger(this.getClass());
    @Autowired
    private NewTelCustomerService newTelCustomerService;

    @RequestMapping(value="/save",method = RequestMethod.POST)
    @VisitorPermission("or0604")
    public ResultModel save(@RequestParam(value = "operatorId", required = false) Long operatorId,
                            @RequestParam(value = "channelId") Long channelId,
                            @RequestParam(value = "mobile") String mobile,
                            @RequestParam(value = "comment", required = false) String comment){
        try{
            logger.debug("新建客户，手机号:{}，渠道id:{}，跟进人id:{}，备注:{}", mobile, channelId, operatorId, comment);
            newTelCustomerService.save(operatorId,channelId,mobile,comment);
            return new ResultModel(true,"保存成功");
        }catch(Exception e){
            return new ResultModel(false,"保存失败");
        }
    }


    @RequestMapping(value="/check",method = RequestMethod.GET)
    @VisitorPermission("or0604")
    public ResultModel checkOldOperators(@RequestParam(value = "mobile") String mobile){
        String operators=newTelCustomerService.check(mobile);
        return new ResultModel(true,operators);
    }

}
