package com.cheche365.cheche.ordercenter.web.controller.telMarketingCenter;

import com.cheche365.cheche.common.excel.ExcelExportUtil;
import com.cheche365.cheche.common.excel.entity.params.ExcelExportEntity;
import com.cheche365.cheche.common.excel.entity.params.ExportParams;
import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.annotation.VisitorPermission;
import com.cheche365.cheche.core.model.QuotePhone;
import com.cheche365.cheche.manage.common.util.ResponseOutUtil;
import com.cheche365.cheche.manage.common.web.model.DataTablePageViewModel;
import com.cheche365.cheche.manage.common.web.model.PageViewModel;
import com.cheche365.cheche.manage.common.web.model.ResultModel;
import com.cheche365.cheche.ordercenter.service.telMarketingCenter.TelMarketingCenterManageService;
import com.cheche365.cheche.ordercenter.service.telMarketingCenter.TelMarketingCenterRepeatService;
import com.cheche365.cheche.ordercenter.web.model.auto.AutoViewModel;
import com.cheche365.cheche.ordercenter.web.model.telMarketingCenter.*;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;


/**
 * 电话营销中心
 * Created by lyh on 2015/11/5.
 */
@RestController
@RequestMapping("/orderCenter/telMarketingCenter")
public class TelMarketingCenterController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private TelMarketingCenterManageService telMarketingCenterManageService;

    @Autowired
    private TelMarketingCenterRepeatService telMarketingCenterRepeatService;

    /**
     * 获取优先和正常列表
     *
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    @VisitorPermission("or0601")
    public TelMarketingCenterListViewModel list(TelMarketingCenterRequestParams params) {
        logger.debug("获取优先和正常列表数据，条件:{}", params.toString());
        return telMarketingCenterManageService.getList(params);
    }

    /**
     * 换一批
     *
     * @return
     */
    @RequestMapping(value = "/newBatch", method = RequestMethod.GET)
    public ResultModel newBatch() {
        return telMarketingCenterManageService.newBatch();
    }


    /**
     * 获取优先列表
     *
     * @return
     */
    @RequestMapping(value = "/dataList/priority", method = RequestMethod.GET)
    public DataTablePageViewModel<TelMarketingCenterViewModel> dataListPriority(TelMarketingCenterRequestParams params) {
        logger.debug("获取意向用户优先列表数据，条件:{}", params.toString());
        DataTablePageViewModel<TelMarketingCenterViewModel> viewModel = telMarketingCenterManageService.getPriorityPage(params);
        return viewModel;
    }

    /**
     * 获取正常列表
     *
     * @return
     */
    @RequestMapping(value = "/dataList/normal", method = RequestMethod.GET)
    public DataTablePageViewModel<TelMarketingCenterViewModel> dataListNormal(TelMarketingCenterRequestParams params) {
        logger.debug("获取意向用户正常列表数据，条件:{}", params.toString());
        DataTablePageViewModel<TelMarketingCenterViewModel> normalPage = telMarketingCenterManageService.getNormalPage(params);
        return normalPage;
    }

    /**
     * 获取今天处理列表
     *
     * @return
     */
    @RequestMapping(value = "/dataList/today", method = RequestMethod.GET)
    public DataTablePageViewModel<TelMarketingCenterViewModel> dataListToday(TelMarketingCenterRequestParams params) {
        logger.debug("获取意向用户今天处理列表数据，条件:{}", params.toString());
        DataTablePageViewModel<TelMarketingCenterViewModel> viewModel = telMarketingCenterRepeatService.getTodayPage(params);
        return viewModel;
    }

    /**
     * 根据号码查看其信息
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @VisitorPermission("or060101,or060103,or060211")
    public TelMarketingCenterViewModel findOne(@PathVariable Long id, @RequestParam(value = "hisId", required = true) Long hisId) {
        logger.debug("查看详情信息，电销id：{}，历史id：{}", id, hisId);
        return telMarketingCenterManageService.findById(id, hisId);
    }


    /**
     * 处理结果保存
     *
     * @param viewModel
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResultModel add(TelMarketingCenterViewModel viewModel) {
        return telMarketingCenterManageService.save(viewModel);
    }

    /**
     * 报价操作
     *
     * @param id
     * @param mobile
     * @return
     */
    @RequestMapping(value = "/{id}/quote", method = RequestMethod.GET)
    public List<QuotePhone> quote(@PathVariable Long id, @RequestParam(value = "mobile", required = true) String mobile) {
        return telMarketingCenterManageService.quote(id, mobile);
    }

    /**
     * 发送短信
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/{id}/sendSMS", method = RequestMethod.GET)
    public ResultModel sendSMS(@PathVariable Long id) {
        return telMarketingCenterManageService.saveHistoryForAction(id, "短信", 2);
    }


    /**
     * 工作查看搜索
     *
     * @return
     */
    @RequestMapping(value = "/phones", method = RequestMethod.GET)
    @VisitorPermission("or060102,or060210")
    public PageViewModel<TelMarketingCenterHistoryViewModel> workSearchByPhone(TelMarketingCenterRequestParams params) {
        params.clearDataForMobile();
        logger.debug("根据工作查看搜索条件查询数据，条件:{}", params.toString());
        return telMarketingCenterManageService.getHistoryRecordsForMobile(params);
    }

    /**
     * 呼叫情况
     *
     * @return
     */
    @RequestMapping(value = "/history", method = RequestMethod.GET)
    @VisitorPermission("or060201,or060203,or060205,or060207,or060212,or060214")
    public PageViewModel<TelMarketingCenterHistoryViewModel> getCaRecords(TelMarketingCenterRequestParams params) {
        logger.debug("根据呼叫情况条件查询数据，条件:{}", params.toString());
        return telMarketingCenterManageService.getRecords(params);
    }


    /**
     * 整体情况
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/wholeSituation", method = RequestMethod.GET)
    @VisitorPermission("or060209")
    public TelMarketingCenterHistoryViewModel getWholeSituation(TelMarketingCenterRequestParams params) throws Exception {
        logger.debug("根据整体情况条件查询数据，条件:{}", params.toString());
        return telMarketingCenterManageService.getWholeSituation(params);
    }


    @RequestMapping(value = "/auto/{id}", method = RequestMethod.GET)
    public List<AutoViewModel> getAuto(@PathVariable Long id) {
        return telMarketingCenterManageService.getAutoInfo(id);
    }

    /**
     * 导出数据
     *
     * @return
     */
    @RequestMapping(value = "/exportWorkDetail", method = RequestMethod.GET)
    @VisitorPermission("or060214")
    public ResultModel exportWorkDetail(TelMarketingCenterRequestParams params, HttpServletResponse response) {
        logger.debug("根据条件查询要导出的数据，条件:{}", params.toString());
        List workDetailList = telMarketingCenterManageService.getExportExcelData(params);
        logger.debug("要导出的进库数据量为{}条", workDetailList.size());
        return exportWorkDetail(workDetailList, response);
    }


    @RequestMapping(value = "/getTelMarketingCenterRepeat", method = RequestMethod.GET)
    @VisitorPermission("or060101,or060103,or060211")
    public PageViewModel<TelMarketingCenterRepeatViewModel> getTelMarketingCenterRepeat(Long centerId, TelMarketingCenterRequestParams params) {
        return telMarketingCenterManageService.getTelMarketingCenterRepeat(centerId, params);
    }

    private ResultModel exportWorkDetail(List workDetailList, HttpServletResponse response) {
        ExportParams params = new ExportParams("进库数据量信息表");
        List<ExcelExportEntity> orderDeliveryInfo = new ArrayList<>();
        orderDeliveryInfo.add(new ExcelExportEntity("电话", "mobile", 20));
        orderDeliveryInfo.add(new ExcelExportEntity("来源时间", "sourceCreateTime", 35));
        orderDeliveryInfo.add(new ExcelExportEntity("渠道", "channel", 30));
        orderDeliveryInfo.add(new ExcelExportEntity("类型", "type", 30));
        orderDeliveryInfo.add(new ExcelExportEntity("车险到期日", "expireDate", 20));
        orderDeliveryInfo.add(new ExcelExportEntity("是否已处理", "isHandled", 20));
        orderDeliveryInfo.add(new ExcelExportEntity("当前跟进人", "operator", 20));
        String currentDate = DateUtils.getCurrentDateString(DateUtils.DATE_SHORTDATE_PATTERN);
        Workbook workbook = ExcelExportUtil.exportExcel(params, orderDeliveryInfo, workDetailList);
        return ResponseOutUtil.excelExport(workbook, response, currentDate + "导出进库数据量信息表.xls");
    }
}
