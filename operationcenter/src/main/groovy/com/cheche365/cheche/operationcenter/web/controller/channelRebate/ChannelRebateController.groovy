package com.cheche365.cheche.operationcenter.web.controller.channelRebate

import com.cheche365.cheche.common.excel.ExcelExportUtil
import com.cheche365.cheche.common.excel.entity.params.ExcelExportEntity
import com.cheche365.cheche.common.excel.entity.params.ExportParams
import com.cheche365.cheche.common.util.DateUtils
import com.cheche365.cheche.core.model.*
import com.cheche365.cheche.core.service.ResourceService
import com.cheche365.cheche.manage.common.exception.FileUploadException
import com.cheche365.cheche.manage.common.service.InternalUserManageService
import com.cheche365.cheche.manage.common.util.ExcelUtil
import com.cheche365.cheche.manage.common.util.ResponseOutUtil
import com.cheche365.cheche.manage.common.web.model.DataTablePageViewModel
import com.cheche365.cheche.operationcenter.exception.OperationCenterException
import com.cheche365.cheche.operationcenter.service.channelRebate.ChannelRebateManageService
import com.cheche365.cheche.operationcenter.service.resource.AreaResource
import com.cheche365.cheche.operationcenter.web.model.area.AreaViewData
import com.cheche365.cheche.operationcenter.web.model.channelRebate.ChannelRebateHistoryViewData
import com.cheche365.cheche.operationcenter.web.model.channelRebate.ChannelRebateViewModel
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

import javax.servlet.http.HttpServletResponse

/**
 * Created by yinJianBin on 2017/6/14.
 */
@RestController
@RequestMapping("/operationcenter/channelRebate")
class ChannelRebateController {

    private Logger logger = LoggerFactory.getLogger(this.getClass())

    private static final String TEMPLATE_FILE_NAME = "运营中心渠道配置批量导入模板.xlsx"

    @Autowired
    private ChannelRebateManageService channelRebateManageService

    @Autowired
    private InternalUserManageService internalUserManageService

    @Autowired
    private ResourceService resourceService

    @Autowired
    AreaResource areaResource


    @RequestMapping(value = "", method = RequestMethod.GET)
    DataTablePageViewModel<ChannelRebateViewModel> list(ChannelRebateViewModel channelRebateViewModel) {
        Page<ChannelRebate> page = channelRebateManageService.findPage(channelRebateViewModel)
        InternalUser operator = internalUserManageService.getCurrentInternalUser()

        DataTablePageViewModel<ChannelRebateViewModel> dataTablePageViewModel = new DataTablePageViewModel<>()
        List<ChannelRebateViewModel> viewModelList = new ArrayList<>(channelRebateViewModel.getPageSize())
        page.getContent().each {
            channelRebate ->
                viewModelList.add(ChannelRebateViewModel.buildViewData(channelRebate, operator))
        }

        dataTablePageViewModel.setAaData(viewModelList)
        dataTablePageViewModel.setiTotalRecords(page.getTotalElements())
        dataTablePageViewModel.setiTotalDisplayRecords(page.getTotalElements())

        return dataTablePageViewModel
    }


    @RequestMapping(value = "/add", method = RequestMethod.POST)
    //add or update
    ResultModel add(@RequestBody List<ChannelRebateViewModel> formList) {
        try {
            channelRebateManageService.add(formList)
        } catch (OperationCenterException e) {
            return new ResultModel(false, e.getMessage())
        }
        return new ResultModel()
    }

    @RequestMapping(value = "/description/getDescription", method = RequestMethod.GET)
    Map<String, String> getDescription(Long channelRebateId) {
        ChannelRebate channelRebate = channelRebateManageService.findById(channelRebateId)
        Map<String, String> map = new HashMap<>()
        map.put("description", channelRebate.getDescription())
        return map
    }

    @RequestMapping(value = "/description/save", method = RequestMethod.POST)
    ResultModel saveDescription(Long channelRebateId, String description) {
        ChannelRebate channelRebate = channelRebateManageService.findById(channelRebateId)
        channelRebate.setDescription(description)
        channelRebateManageService.save(channelRebate)
        return new ResultModel()
    }

    @RequestMapping(value = "/batchAdd", method = RequestMethod.POST)
    ResultModel batchAdd(@RequestBody List<ChannelRebateViewModel> formList) {
        try {
            channelRebateManageService.batchAdd(formList.get(0))
        } catch (OperationCenterException e) {
            return new ResultModel(false, e.getMessage())
        }
        return new ResultModel()
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    ResultModel delete(@PathVariable Long id) {
        channelRebateManageService.delete(id)
        return new ResultModel()
    }

    /**
     * @return
     */
    @RequestMapping(value = "/insuranceCompanys", method = RequestMethod.GET)
    List<InsuranceCompany> getInsuranceCompanys() {
        List<InsuranceCompany> insuranceCompanyList = channelRebateManageService.getCompanyList();
        return insuranceCompanyList;
    }


    @RequestMapping(value = "/history", method = RequestMethod.GET)
    DataTablePageViewModel<ChannelRebateHistoryViewData> getHistory(ChannelRebateHistoryViewData data) {
        Page<ChannelRebateHistory> page = channelRebateManageService.findHistoryPage(data)
        InternalUser operator = internalUserManageService.getCurrentInternalUser()

        DataTablePageViewModel<ChannelRebateHistoryViewData> dataTablePageViewModel = new DataTablePageViewModel<>()
        List<ChannelRebateHistoryViewData> viewModelList = new ArrayList<>()
        page.getContent().each { channelRebateHistory ->
            viewModelList.add(ChannelRebateHistoryViewData.buildViewData(channelRebateHistory, operator))
        }
        dataTablePageViewModel.setAaData(viewModelList)
        dataTablePageViewModel.setiTotalRecords(page.getTotalElements())
        dataTablePageViewModel.setiTotalDisplayRecords(page.getTotalElements())

        return dataTablePageViewModel
    }

    /**
     * @return
     */
    @RequestMapping(value = "/status", method = RequestMethod.GET)
    Map<Integer, String> getChannelRebateStatusMap() {
        Map<Integer, String> statusMapping = ChannelRebate.Enum.STATUS_MAPPING
        statusMapping.remove(ChannelRebate.Enum.EXPIRED_2)
        return statusMapping
    }

    @RequestMapping(value = "/channels", method = RequestMethod.GET)
    List<Channel> channels(ChannelRebateViewModel formData) {
        return channelRebateManageService.getChannels(formData) - Channel.disables() - Channel.nonAuto();
    }

    @RequestMapping(value = "/channelsNoOrdercenter", method = RequestMethod.GET)
    List<Channel> channelsNoOrdercenter(ChannelRebateViewModel formData) {
        return channelRebateManageService.getChannels(formData) - (Channel.orderCenterChannels() - Channel.Enum.ORDER_CENTER_11) - Channel.disables() - Channel.nonAuto();
    }

    /**
     * 获取所有的省和直辖市
     *
     * @return
     */
    @RequestMapping(value = "/provinces", method = RequestMethod.GET)
    List<AreaViewData> getProvinces() {
        return areaResource.createAreaViewDataList(areaResource.getprovincesAndDirectCitys());
    }

    /**
     * 获取省下面的市
     *
     * @return
     */
    @RequestMapping(value = "/{province}/cities", method = RequestMethod.GET)
    List<AreaViewData> getCityAreaListByProvinceId(@PathVariable String province) {
        return areaResource.createAreaViewDataList(channelRebateManageService.getCityAreaListByProvinceId(province));
    }


    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    def upload(
            @RequestParam(value = "codeFile", required = false) MultipartFile file, HttpServletResponse response) {
        logger.debug("import channel rebate Excel start")
        Workbook book
        try {
            book = ExcelUtil.upload(file)
            if (book == null) {
                throw new FileUploadException("文件转换错误 !")
            }
        } catch (IOException e) {
            logger.error("导入excel数据失败", e)
            return
        }
        Sheet sheet = book.getSheetAt(0)
        if (sheet.getLastRowNum() < 1) {//第一行是模板
            logger.error("空表不需要传")
            return
        }
        List<List<String>> dataList = new ArrayList<>()
        int columnNum = ExcelUtil.getColumnCount(sheet)
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i)
            if (row == null) {
                continue
            }
            List<String> rowList = new ArrayList<>()
            for (int c = 0; c < columnNum; c++) {
                Cell cell = row.getCell(c)
                rowList.add((cell == null) ? "" : ExcelUtil.getCellValue(cell))
            }
            dataList.add(rowList)
        }
        try {
            List<ChannelRebateViewModel> returnModelList = channelRebateManageService.importDatas(dataList)
            if (returnModelList.size() != 0) {
                export(returnModelList, response)
            } else {
                ResponseOutUtil.outPrint(response, "success")
            }
        } catch (Exception e) {
            ResponseOutUtil.outPrint(response, e.getMessage())
            logger.error("上传保单失败", e)
        }
    }

    static def export(List<ChannelRebateViewModel> returnModelList, HttpServletResponse response) {
        ExportParams params = new ExportParams("运营中心渠道配置批量导入模板错误")
        List<ExcelExportEntity> rebateErrorInfo = new ArrayList<>()
        rebateErrorInfo.add(new ExcelExportEntity("客户类型", "clientType", 10))
        rebateErrorInfo.add(new ExcelExportEntity("渠道类型", "channelType", 10))
        rebateErrorInfo.add(new ExcelExportEntity("渠道名称", "channelName", 20))
        rebateErrorInfo.add(new ExcelExportEntity("城市名称", "areaName", 20))
        rebateErrorInfo.add(new ExcelExportEntity("保险公司", "insuranceCompanyName", 10))
        rebateErrorInfo.add(new ExcelExportEntity("生效时间", "effectiveDate", 10))
        rebateErrorInfo.add(new ExcelExportEntity("单商业险", "onlyCommercialRebate", 10))
        rebateErrorInfo.add(new ExcelExportEntity("单交强险", "onlyCompulsoryRebate", 10))
        rebateErrorInfo.add(new ExcelExportEntity("当前商业险", "commercialRebate", 10))
        rebateErrorInfo.add(new ExcelExportEntity("当前交强险", "compulsoryRebate", 10))
        rebateErrorInfo.add(new ExcelExportEntity("预生效时间", "readyEffectiveDate", 10))
        rebateErrorInfo.add(new ExcelExportEntity("预生效单商业险", "onlyReadyCommercialRebate", 10))
        rebateErrorInfo.add(new ExcelExportEntity("预生效单交强险", "onlyReadyCompulsoryRebate", 10))
        rebateErrorInfo.add(new ExcelExportEntity("预生效商业险", "readyCommercialRebate", 10))
        rebateErrorInfo.add(new ExcelExportEntity("预生效交强险", "readyCompulsoryRebate", 10))
        rebateErrorInfo.add(new ExcelExportEntity("出险政策", "description", 10))
        rebateErrorInfo.add(new ExcelExportEntity("错误提示", "excelErr", 10))

        List dataList = new ArrayList<>()
        for (ChannelRebateViewModel viewModel : returnModelList) {
            Map rebateMap = new HashMap<String, Object>()
            def insetArr = ["clientType", "channelType", "channelName", "areaName", "insuranceCompanyName", "effectiveDateStr",
                            "onlyCommercialRebate", "onlyCompulsoryRebate", "commercialRebate", "compulsoryRebate", "readyEffectiveDateStr",
                            "onlyReadyCommercialRebate", "onlyReadyCompulsoryRebate", "readyCommercialRebate", "readyCompulsoryRebate", "description", "excelErr"]
            for (int i = 0; i < insetArr.size(); i++) {
                rebateMap.put(insetArr[i], viewModel."${insetArr[i]}")
            }
            dataList.add(rebateMap)
        }
        String currentDate = DateUtils.getCurrentDateString(DateUtils.DATE_SHORTDATE_PATTERN)
        Workbook workbook = ExcelExportUtil.exportExcel(params, rebateErrorInfo, dataList)
        return ResponseOutUtil.excelExport(workbook, response, "运营中心渠道配置批量导入模板错误" + currentDate + ".xls")
    }

    @RequestMapping(value = "/template/url", method = RequestMethod.GET)
    ResultModel getTemplateUrl() {
        String templatePath = resourceService.getResourceAbsolutePath(resourceService.getProperties().getTemplatePath())
        String url = resourceService.absoluteUrl(templatePath, TEMPLATE_FILE_NAME)
        return new ResultModel(true, url)
    }
}
