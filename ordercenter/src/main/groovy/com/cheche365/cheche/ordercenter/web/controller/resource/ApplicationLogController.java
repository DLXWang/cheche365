package com.cheche365.cheche.ordercenter.web.controller.resource;

import com.cheche365.cheche.core.model.MoApplicationLog;
import com.cheche365.cheche.ordercenter.service.resource.ApplicationLogService;
import com.cheche365.cheche.ordercenter.web.model.ApplicationLogViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * Created by xuyelong on 2016/01/26.
 */
@RestController
@RequestMapping(value = "/orderCenter/applicationLog")
public class ApplicationLogController {
    private Logger logger = LoggerFactory.getLogger(ApplicationLogController.class);

    @Autowired
    private ApplicationLogService applicationLogService;

    @RequestMapping(value = "/{objTable}/{objId}")
    public List<ApplicationLogViewModel> getLog(@PathVariable String objTable,@PathVariable String objId) {
        if (logger.isDebugEnabled()) {
            logger.debug("get log for applicationLog objId -> {}", objId);
        }
        return applicationLogService.createViewModelList(applicationLogService.getLog(objId,objTable));
    }

    @RequestMapping(value = "",method = RequestMethod.POST)
    public ApplicationLogViewModel saveOrderHistory(@RequestBody MoApplicationLog applicationLog) {
        if (logger.isDebugEnabled()) {
            logger.debug("save new applicationLog for objId -> {}", applicationLog.getObjId());
        }
        return applicationLogService.createViewModel(applicationLogService.saveLog(applicationLog));
    }

}
