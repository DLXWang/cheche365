package com.cheche365.cheche.operationcenter.web.controller.quoteOffline;

import com.cheche365.cheche.common.excel.ExcelExportUtil;
import com.cheche365.cheche.common.excel.entity.params.ExcelExportEntity;
import com.cheche365.cheche.common.excel.entity.params.ExportParams;
import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.annotation.VisitorPermission;
import com.cheche365.cheche.core.model.QuoteFlowConfig;
import com.cheche365.cheche.core.service.ResourceService;
import com.cheche365.cheche.manage.common.exception.FileUploadException;
import com.cheche365.cheche.manage.common.model.QuoteFlowConfigOperateLog;
import com.cheche365.cheche.manage.common.service.BaseService;
import com.cheche365.cheche.manage.common.service.QuoteFlowConfigService;
import com.cheche365.cheche.manage.common.util.ExcelUtil;
import com.cheche365.cheche.manage.common.util.ResponseOutUtil;
import com.cheche365.cheche.manage.common.web.model.*;
import com.cheche365.cheche.operationcenter.web.model.DataTablesPageViewModel;
import com.cheche365.cheche.operationcenter.web.model.quoteFlowConfig.QuoteFlowConfigOperateLogViewData;
import com.cheche365.cheche.operationcenter.web.model.quoteFlowConfig.QuoteFlowConfigViewData;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.*;

/**
 * Created by chenxiangyin on 2017/7/7.
 */
@RestController
@RequestMapping("/operationcenter/quoteFlowConfig")
public class QuoteFlowConfigController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final String TEMPLATE_FILE_NAME = "运营中心报价配置批量导入模板V2.xlsx";
    @Autowired
    private BaseService baseService;
    @Autowired
    private QuoteFlowConfigService quoteFlowConfigService;
    @Autowired
    private ResourceService resourceService;
    @Autowired
    @RequestMapping(value="/quoteSourceList",method = RequestMethod.GET)
    public List<QuoteFlowConfig.ConfigValue> quoteSourceList(){
        return Arrays.asList(QuoteFlowConfig.ConfigValue.WEB_PARSER,QuoteFlowConfig.ConfigValue.API,QuoteFlowConfig.ConfigValue.FANHUA);
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    @VisitorPermission("op0901")//权限的地方
    public DataTablesPageViewModel<QuoteFlowConfigViewData> configList(QuoteFlowConfigSearchQuery query) {
        Page<QuoteFlowConfig> page = quoteFlowConfigService.getConfigListByPage(query);
        PageInfo pageInfo = baseService.createPageInfo(page);
        List<QuoteFlowConfigViewData> dataList = new ArrayList<>();
        page.getContent().forEach(config -> {
            dataList.add(QuoteFlowConfigViewData.createViewModel(config));
        });
        return new DataTablesPageViewModel<QuoteFlowConfigViewData>(pageInfo.getTotalElements(),pageInfo.getTotalElements(),query.getDraw(),dataList);
    }

    @RequestMapping(value = "/logOperateList",method = RequestMethod.GET)
    public DataTablesPageViewModel<QuoteFlowConfigOperateLogViewData>  logOperateList(QuoteFlowConfigSearchQuery query) {

        Page<QuoteFlowConfigOperateLog> page = quoteFlowConfigService.getLogListByPage(query);
        PageInfo pageInfo = baseService.createPageInfo(page);
        List<QuoteFlowConfigOperateLogViewData> modelList = new ArrayList<>();
        page.getContent().forEach(log -> modelList.add(QuoteFlowConfigOperateLogViewData.createViewData(log)));
        return new DataTablesPageViewModel<QuoteFlowConfigOperateLogViewData>(pageInfo.getTotalElements(),pageInfo.getTotalElements(),query.getDraw(),modelList);
    }

    @RequestMapping(value = "/editQuoteOffline",method = RequestMethod.POST)
    public ResultModel editQuoteOffline(@Valid QuoteFlowConfigQuery query, BindingResult bindingResult) {
        quoteFlowConfigService.edit(query);
        return new ResultModel(true,"修改成功");
    }
    @RequestMapping(value = "/add",method = RequestMethod.POST)
    public ResultModel add(@Valid QuoteFlowConfigQuery query){
        quoteFlowConfigService.add(query);
        return new ResultModel(true,"添加成功");
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ResultModel upload(@RequestParam(value = "codeFile", required = false) MultipartFile file, HttpServletResponse response) {
        logger.debug("import quote flow config Excel start");
        Workbook book;
        try {
            book = ExcelUtil.upload(file);
            if(book==null){
                throw new FileUploadException("文件转换错误 !");
            }
            Sheet sheet = book.getSheetAt(0);
            List<List<String>> dataList = new ArrayList<>();
            int coloumNum = ExcelUtil.getColumnCount(sheet);
            if(sheet.getLastRowNum() < 1){//第一行是模板
                throw new FileUploadException("空表不需要传 !");
            }
            for (int i = 1; i <= sheet.getLastRowNum(); i++){
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                List<String> rowList = new ArrayList<>();
                for (int c = 0; c < 7; c++) {
                    Cell cell = row.getCell(c);
                    rowList.add(ExcelUtil.getCellValue(cell));
                }
                dataList.add(rowList);
            }
            try {
                List<QuoteFlowConfigExcelModel> returnModelList = quoteFlowConfigService.importData(dataList);
                if(returnModelList.size() != 0){
                    export(returnModelList, response);
                }else{
                    ResponseOutUtil.outPrint(response, "success");
                }
            } catch (Exception e) {
                ResponseOutUtil.outPrint(response, e.getMessage());
                logger.error("上传保单失败", e);
            }
        } catch (IOException e) {
            logger.error("导入excel数据失败", e);
        }
        return null;
    }

    private ResultModel export(List<QuoteFlowConfigExcelModel> excelModelList, HttpServletResponse response) {
        ExportParams params = new ExportParams("运营中心渠道配置批量导入模板错误");
        List<ExcelExportEntity> rebateErrorInfo = new ArrayList<>();
        rebateErrorInfo.add(new ExcelExportEntity("城市名称", "cityName", 10));
        rebateErrorInfo.add(new ExcelExportEntity("保险公司", "insuranceComp", 10));
        rebateErrorInfo.add(new ExcelExportEntity("类型", "type", 20));
        rebateErrorInfo.add(new ExcelExportEntity("渠道", "channel", 20));
        rebateErrorInfo.add(new ExcelExportEntity("渠道名", "channelName", 10));
        rebateErrorInfo.add(new ExcelExportEntity("状态", "status", 10));
        rebateErrorInfo.add(new ExcelExportEntity("报价方式", "quoteWay", 10));
        rebateErrorInfo.add(new ExcelExportEntity("错误提示", "excelErr", 10));

        List dataList = new ArrayList<>();
        for (QuoteFlowConfigExcelModel viewModel : excelModelList) {
            Map configMap = new HashMap<String, Object>();
            configMap.put("cityName", viewModel.getCityName());
            configMap.put("insuranceComp", viewModel.getInsuranceComp());
            configMap.put("type", viewModel.getType());
            configMap.put("channel", viewModel.getChannel());
            configMap.put("channelName", viewModel.getChannelName());
            configMap.put("status", viewModel.getStatus());
            configMap.put("quoteWay", viewModel.getQuoteWay());
            configMap.put("excelErr", viewModel.getExcelErr());
            dataList.add(configMap);
        }
        String currentDate = DateUtils.getCurrentDateString(DateUtils.DATE_SHORTDATE_PATTERN);
        Workbook workbook = ExcelExportUtil.exportExcel(params, rebateErrorInfo, dataList);
        return ResponseOutUtil.excelExport(workbook, response,  "运营中心报价配置批量导入模板" + currentDate +".xls");
    }

    @RequestMapping(value = "/template/url", method = RequestMethod.GET)
    public ResultModel getTemplateUrl() {
        String templatePath = resourceService.getResourceAbsolutePath(resourceService.getProperties().getTemplatePath());
        String url = resourceService.absoluteUrl(templatePath, TEMPLATE_FILE_NAME);
        return new ResultModel(true, url);
    }

}
