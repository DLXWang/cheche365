package com.cheche365.cheche.operationcenter.web.controller.dataStatistics;


import com.cheche365.cheche.core.annotation.VisitorPermission;
import com.cheche365.cheche.core.service.ResourceService;
import com.cheche365.cheche.manage.common.exception.FileUploadException;
import com.cheche365.cheche.manage.common.model.ActivityMonitorUrl;
import com.cheche365.cheche.manage.common.service.BaseService;
import com.cheche365.cheche.manage.common.util.ResponseOutUtil;
import com.cheche365.cheche.manage.common.web.model.PageInfo;
import com.cheche365.cheche.manage.common.web.model.ResultModel;
import com.cheche365.cheche.operationcenter.model.ActivityMonitorDataQuery;
import com.cheche365.cheche.operationcenter.service.dataStatistics.ActivityMonitorDataService;
import com.cheche365.cheche.operationcenter.web.model.DataTablesPageViewModel;
import com.cheche365.cheche.operationcenter.web.model.partner.ActivityMonitorDataViewModel;
import com.cheche365.cheche.operationcenter.web.model.partner.ActivityMonitorUrlViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenx on 2017/06/07.
 */
@RestController
@RequestMapping("/operationcenter/dataStatistics")
public class ActivityMonitorDataController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final String SUCCESS = "success";
    private static final String TEMPLATE_FILE_NAME = "模版.xls";

    @Autowired
    private ActivityMonitorDataService activityMonitorDataService;
    @Autowired
    private BaseService baseService;
    @Autowired
    private ResourceService resourceService;

    @RequestMapping(value = "/dataSearch/export", method = RequestMethod.GET)
    @VisitorPermission("op0701")
    public ResultModel exportAll(ActivityMonitorDataQuery query, HttpServletResponse response) {
        query.setCurrentPage(1);
        query.setPageSize(99999);
        Page<Object[]> insurancePolicyPage = activityMonitorDataService.findMonitorDataBySpecAndPaginate(query);
        logger.debug("按条件需导出数据数量[{}]条", insurancePolicyPage.getTotalElements());
        return activityMonitorDataService.export(insurancePolicyPage.getContent(), response,query.getGroupByDay());
    }

    @RequestMapping(value = "/dataSearch/dataList", method = RequestMethod.GET)
    @VisitorPermission("op0701")
    public DataTablesPageViewModel<ActivityMonitorDataViewModel> activityMonitorDataList(ActivityMonitorDataQuery query) {
        //Page<ActivityMonitorUrl> page = dataSearchService.findMonitorDataBySpecAndPaginate(query);
        Page<Object[]> page = activityMonitorDataService.findMonitorDataBySpecAndPaginate(query);
        List<ActivityMonitorDataViewModel> modelList = createDataViewModelList(page.getContent());
        PageInfo pageInfo = baseService.createPageInfo(page);
        return new DataTablesPageViewModel<>(pageInfo.getTotalElements(), pageInfo.getTotalElements(), query.getDraw(), modelList);
    }

    @RequestMapping(value = "/createUrl/urlList", method = RequestMethod.GET)
    @VisitorPermission("op0702")
    public DataTablesPageViewModel<ActivityMonitorUrlViewModel> urlList(ActivityMonitorDataQuery query) {
        Page<ActivityMonitorUrl> page = activityMonitorDataService.getUrlByPage(query);
        List<ActivityMonitorUrlViewModel> modelList = new ArrayList<>();
        page.getContent().forEach(activityMonitorUrl -> modelList.add(ActivityMonitorUrlViewModel.createViewModel(activityMonitorUrl)));
        PageInfo pageInfo = baseService.createPageInfo(page);
        return new DataTablesPageViewModel<>(pageInfo.getTotalElements(), pageInfo.getTotalElements(), query.getDraw(), modelList);
    }

    @RequestMapping(value = "/createUrl/del/{id}", method = RequestMethod.PUT)
    @VisitorPermission("op070201")
    public ResultModel del(@PathVariable Long id) {
        try {
            activityMonitorDataService.delUrlData(id);
            return new ResultModel(true, "保存成功！");
        } catch (Exception e) {
            logger.error("delete url data has error", e);
            return new ResultModel(false, "保存失败");
        }
    }

    @RequestMapping(value = "/createUrl/add", method = RequestMethod.POST)
    @VisitorPermission("op0702")
    public ResultModel add(@Valid ActivityMonitorDataQuery query,BindingResult result) {
        try {
            activityMonitorDataService.addUrlData(query);
            return new ResultModel(true, "保存成功！");
        } catch (Exception e) {
            logger.error("add url data has error", e);
            return new ResultModel(false, "保存失败");
        }
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @VisitorPermission("op0702")
    public void upload(@RequestParam(value = "codeFile") MultipartFile file, HttpServletResponse response) throws Exception {
        try {
            activityMonitorDataService.uploadFile(file);
            ResponseOutUtil.outPrint(response, SUCCESS);
        } catch (FileUploadException e) {
            ResponseOutUtil.outPrint(response, e.getMessage());
            logger.error("批量生成链接文件上传失败", e);
        }
    }

    @RequestMapping(value = "/template/url", method = RequestMethod.GET)
    @VisitorPermission("op0702")
    public ResultModel getTemplateUrl() {
        String templatePath = resourceService.getResourceAbsolutePath(resourceService.getProperties().getTemplatePath());
        String url = resourceService.absoluteUrl(templatePath, TEMPLATE_FILE_NAME);
        return new ResultModel(true, url);
    }


    public List<ActivityMonitorDataViewModel> createDataViewModelList(List<Object[]> objectList) {
        List<ActivityMonitorDataViewModel> activityMonitorDataViewModels = new ArrayList<>();
        for (Object[] obj : objectList) {
            ActivityMonitorDataViewModel activityMonitorDataViewModel = new ActivityMonitorDataViewModel();
            ActivityMonitorUrlViewModel activityMonitorUrlViewModel = new ActivityMonitorUrlViewModel();
            activityMonitorUrlViewModel.setId(Long.parseLong(obj[0].toString()));
            activityMonitorUrlViewModel.setScope(obj[1].toString());
            activityMonitorUrlViewModel.setSource(obj[2].toString());
            activityMonitorUrlViewModel.setPlan(obj[3].toString());
            activityMonitorUrlViewModel.setUnit(obj[4].toString());
            activityMonitorUrlViewModel.setKeyword(obj[5].toString());
            activityMonitorUrlViewModel.setUrl(obj[6].toString());
            activityMonitorDataViewModel.setUrl(activityMonitorUrlViewModel);
            activityMonitorDataViewModel.setPv(Integer.parseInt(obj[7].toString()));
            activityMonitorDataViewModel.setUv(Integer.parseInt(obj[8].toString()));
            activityMonitorDataViewModel.setRegister(Integer.parseInt(obj[9].toString()));
            activityMonitorDataViewModel.setPaymentCount(Integer.parseInt(obj[10].toString()));
            activityMonitorDataViewModel.setPaymentAmount(Double.parseDouble(obj[11].toString()));
            activityMonitorDataViewModel.setSubmitCount(Integer.parseInt(obj[12].toString()));
            activityMonitorDataViewModel.setTelCount(Integer.parseInt(obj[13].toString()));
            activityMonitorDataViewModels.add(activityMonitorDataViewModel);
//            activityMonitorDataViewModel.setSubmitCount(Integer.parseInt(obj[12].toString()));
//            if (obj[13] != null) {
//                activityMonitorDataViewModel.setTelCount(Integer.parseInt(obj[13].toString()));
//            }
        }
        return activityMonitorDataViewModels;
    }
}
