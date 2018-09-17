package com.cheche365.cheche.ordercenter.web.controller.insurance;

import com.cheche365.cheche.core.annotation.VisitorPermission;
import com.cheche365.cheche.core.model.ResultModel;
import com.cheche365.cheche.manage.common.model.OfflineOrderImportHistory;
import com.cheche365.cheche.manage.common.web.model.DataTablePageViewModel;
import com.cheche365.cheche.ordercenter.service.insurance.ImportDataHistoryService;
import com.cheche365.cheche.ordercenter.web.model.insurance.ImportDataHistoryModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by chenxiangyin on 2017/12/18.
 */
@RestController
@RequestMapping(value = "/orderCenter/offlineOrderImportHistory")
public class ImportDataHistoryController {
    @Autowired
    private ImportDataHistoryService service;

    @RequestMapping(value = "", method = RequestMethod.GET)
    @VisitorPermission("or10011")
    public DataTablePageViewModel<ImportDataHistoryModel> getOrders(ImportDataHistoryModel query)  {
        Page<OfflineOrderImportHistory> page = service.getHistorysByPage(query);
        List<OfflineOrderImportHistory> histories = page.getContent();
        List<ImportDataHistoryModel> list = new ArrayList<>();
        histories.forEach(history->{
            list.add(service.createViewModel(history));
        });
        return new DataTablePageViewModel<>(page.getTotalElements(), page.getTotalElements(), query.getDraw(), list);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    //add or update
    public ResultModel add(@RequestBody ImportDataHistoryModel form) {
        try {
            service.add(form);
        } catch (Exception e) {
            return new ResultModel(false, "失败");
        }
        return new ResultModel(true,"成功");
    }
}
