package com.cheche365.cheche.ordercenter.web.controller.telMarketingCenter;

import com.cheche365.cheche.core.annotation.VisitorPermission;
import com.cheche365.cheche.manage.common.model.TelMarketingCenterAssignBatch;
import com.cheche365.cheche.manage.common.service.BaseService;
import com.cheche365.cheche.ordercenter.service.telMarketingCenter.TelMarketingCenterAssignBatchService;
import com.cheche365.cheche.manage.common.web.model.DataTablePageViewModel;
import com.cheche365.cheche.manage.common.web.model.PageInfo;
import com.cheche365.cheche.ordercenter.web.model.telMarketingCenter.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by luly on 2017/03/25.
 */
@RestController
@RequestMapping("/orderCenter/telMarketingCenter/assign")
public class TelMarketingCenterAssignBatchController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private TelMarketingCenterAssignBatchService telMarketingCenterAssignBatchService;
    @Autowired
    private BaseService baseService;

    /**
     * 数据分配历史列表
     * @return
     */
    @RequestMapping(value = "/dataAssignHistory", method = RequestMethod.GET)
    @VisitorPermission("or060301")
    public DataTablePageViewModel getDataAssignHistory(TelMarketingCenterRequestParams params) {
        Page<TelMarketingCenterAssignBatch> page = telMarketingCenterAssignBatchService.getTelMarketingCenterAssignBatchByPage(params);
        List<TelMarketingCenterAssignBatch> infoList = telMarketingCenterAssignBatchService.handleDataList(page.getContent());
        List<TelMarketingCenterAssignBatchViewModel> dataList = new ArrayList<>();
        infoList.forEach(telMarketingCenterAssignBatch -> dataList.add(TelMarketingCenterAssignBatchViewModel.createViewData(telMarketingCenterAssignBatch)));
        PageInfo pageInfo = baseService.createPageInfo(page);
        return new DataTablePageViewModel<>(pageInfo.getTotalElements(),pageInfo.getTotalElements(),params.getDraw(),dataList);
    }


    @RequestMapping(value = "/dataAssignDetail", method = RequestMethod.GET)
    @VisitorPermission("or060301")
    public DataTablePageViewModel getDataAssignDetail(@RequestParam(value = "id", required = true) Long batchId,
                                                      @RequestParam(value = "currentPage", required = true) Integer currentPage,
                                                      @RequestParam(value = "pageSize", required = true) Integer pageSize,
                                                      @RequestParam(value = "draw") Integer draw) {
        int startIndex = (currentPage - 1) * pageSize;
        List<Map<String,String>> dataList = telMarketingCenterAssignBatchService.findDataByBatchId(batchId, startIndex, pageSize);
        long totalElements = telMarketingCenterAssignBatchService.countByBatchId(batchId);
        return new DataTablePageViewModel<>(totalElements,totalElements,draw,dataList);
    }

    /*@RequestMapping(value = "/getCountInfo", method = RequestMethod.GET)
    @VisitorPermission("or060301")
    public Map<String,Integer> getCountInfo(@RequestParam(value = "startTime", required = true) String startTime,
                                            @RequestParam(value = "endTime", required = true) String endTime) {
        return telMarketingCenterAssignBatchService.findDataByTime(
            DateUtils.getDayStartTime(DateUtils.getDate(startTime, DateUtils.DATE_SHORTDATE_PATTERN)),
            DateUtils.getDayEndTime(DateUtils.getDate(endTime, DateUtils.DATE_SHORTDATE_PATTERN)));
    }*/
}
