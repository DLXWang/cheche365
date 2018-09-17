package com.cheche365.cheche.operationcenter.web.controller.partner;

import com.cheche365.cheche.core.annotation.VisitorPermission;
import com.cheche365.cheche.manage.common.exception.FieldValidtorException;
import com.cheche365.cheche.manage.common.model.PublicQuery;
import com.cheche365.cheche.operationcenter.service.partner.IBusinessActivityService;
import com.cheche365.cheche.operationcenter.web.model.DataTablesPageViewModel;
import com.cheche365.cheche.manage.common.web.model.ResultModel;
import com.cheche365.cheche.operationcenter.web.model.partner.BusinessActivityViewModel;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.OutputStream;
import java.util.Map;

/**
 * Created by sunhuazhong on 2015/8/26.
 */
@RestController
@RequestMapping("/operationcenter/activities")
public class BusinessActivityController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IBusinessActivityService businessActivityService;

    /**
     * 新增商务活动
     * @param model
     * @param result
     * @return
     */
    @RequestMapping(value = "",method = RequestMethod.POST)
    @VisitorPermission("op010202")
    public boolean add(@Valid BusinessActivityViewModel model, BindingResult result) {
        if(logger.isDebugEnabled()) {
            logger.debug("add new business activity start");
        }

        if (result.hasErrors())
            throw new RuntimeException("some required info has been missed");

        return businessActivityService.add(model);
    }

    /**
     * 修改商务活动
     * @param activityId
     * @param model
     * @param result
     * @return
     */
    @RequestMapping(value = "/{activityId}",method = RequestMethod.PUT)
    @VisitorPermission("op010206")
    public boolean update(@PathVariable Long activityId, @Valid BusinessActivityViewModel model, BindingResult result) {
        if(logger.isDebugEnabled()) {
            logger.debug("update business activity start");
        }

        if (result.hasErrors())
            throw new RuntimeException("some required info has been missed");

        return businessActivityService.update(activityId, model);
    }

    /**
     * 删除商务活动
     * @param activityId
     */
    @RequestMapping(value = "/{activityId}",method = RequestMethod.DELETE)
    public boolean delete(@PathVariable Long activityId) {
        if(logger.isDebugEnabled()) {
            logger.debug("delete business activity start");
        }

        return businessActivityService.delete(activityId);
    }

    /**
     * 根据条件查询商务活动
     * @return
     */
    @RequestMapping(value = "",method = RequestMethod.GET)
    @VisitorPermission("op0102")
    public DataTablesPageViewModel<BusinessActivityViewModel> search(PublicQuery query) {
        return businessActivityService.search(query, 1);
    }

    /**
     * 获取商务活动详情
     * @param activityId
     * @return
     */
    @RequestMapping(value = "/{activityId}",method = RequestMethod.GET)
    @VisitorPermission("op010203")
    public BusinessActivityViewModel findOne(@PathVariable Long activityId) {
        if(logger.isDebugEnabled()) {
            logger.debug("get business activity detail,id:" + activityId);
        }

        if(activityId == null || activityId < 1){
            throw new FieldValidtorException("find business activity detail, id can not be null or less than 1");
        }

        return businessActivityService.findById(activityId);
    }

    /**
     * 更新商务活动监控数据
     * @param activityId
     * @return
     */
    @RequestMapping(value = "/{activityId}/update",method = RequestMethod.GET)
    @VisitorPermission("op010204")
    public BusinessActivityViewModel refreshData(@PathVariable Long activityId) {
        if(logger.isDebugEnabled()) {
            logger.debug("refresh business activity monitor data,id:" + activityId);
        }

        if(activityId == null || activityId < 1){
            throw new FieldValidtorException("refresh business activity monitor data, id can not be null or less than 1");
        }

        return businessActivityService.refreshMonitorData(activityId);
    }

    /**
     * 导出查询出的商务活动列表
     * @param response
     */
    @RequestMapping(value = "/export",method = RequestMethod.GET)
    @VisitorPermission("op010201")
    public void export(PublicQuery query, HttpServletResponse response) {
        if(logger.isDebugEnabled()) {
            logger.debug("export business activity list data");
        }

        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setCharacterEncoding("utf-8");
        response.setHeader("X-Frame-Options", "SAMEORIGIN");

        OutputStream out = null;
        try {
            response.setHeader("Content-Disposition",
                "attachment; filename=" + new String(("商务活动查询结果.xls").getBytes(), "iso-8859-1"));
            HSSFWorkbook workbook = businessActivityService.createExportExcel(query);
            out = response.getOutputStream();
            workbook.write(out);
            out.flush();
            out.close();
        } catch (Exception ex) {
            logger.error("export business activity result has error", ex);
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (Exception ex) {
                logger.error("export business activity result, close OutputStream has error", ex);
            }
        }
    }

    /**
     * 导出商务活动时段监控数据
     * @param activityId
     * @param response
     */
    @RequestMapping(value = "/{activityId}/export",method = RequestMethod.GET)
    @VisitorPermission("op010205")
    public void export(@PathVariable Long activityId, HttpServletResponse response) {
        if(logger.isDebugEnabled()) {
            logger.debug("export business activity hour monitor data,id:" + activityId);
        }

        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setCharacterEncoding("utf-8");
        response.setHeader("X-Frame-Options", "SAMEORIGIN");

        OutputStream out = null;
        try {
            response.setHeader("Content-Disposition",
                "attachment; filename=" + new String(("监控数据时段结果.xls").getBytes(), "iso-8859-1"));
            HSSFWorkbook workbook = businessActivityService.createHourExportExcel(activityId);
            out = response.getOutputStream();
            workbook.write(out);
            out.flush();
            out.close();
        } catch (Exception ex) {
            logger.error("export business activity hour monitor data excel has error", ex);
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (Exception ex) {
                logger.error("export business activity hour monitor data result, close OutputStream has error", ex);
            }
        }
    }

    /**
     * 获取指定地区的商务活动监控数据
     * @param activityId
     * @param areaId 全国：1；未知来源：-1；城市：城市id
     * @return
     */
    @RequestMapping(value = "/{activityId}/{areaId}/data",method = RequestMethod.GET)
    public BusinessActivityViewModel getCityMonitorData(@PathVariable Long activityId, @PathVariable Long areaId) {
        if(logger.isDebugEnabled()) {
            logger.debug("get business activity monitor data of city, activity id:" + activityId + ",area id:" + areaId);
        }

        if(activityId == null || activityId < 1){
            throw new FieldValidtorException("get business activity monitor data of city, id can not be null or less than 1");
        }

        return businessActivityService.getCityMonitorData(activityId, areaId);
    }

    /**
     * 验证商务活动编号唯一性以及商务活动的开始结束时间是否在推广活动的时间范围内
     * @param code
     * @param marketingId
     * @param startTime
     * @return
     */
    @RequestMapping(value = "/check",method = RequestMethod.GET)
    public ResultModel checkBusinessActivityData(@RequestParam(value = "code",required = false) String code,
                                             @RequestParam(value = "marketingId",required = false) Long marketingId,
                                             @RequestParam(value = "startTime",required = false) String startTime) {
        if(logger.isDebugEnabled()) {
            logger.debug("check business activity code and startTime");
        }

        return businessActivityService.checkBusinessActivityData(code, marketingId, startTime);
    }

    /**
     * 获取商务活动URL
     * 包括M站首页或购买页URL
     * @param urlType
     * @return
     */
    @RequestMapping(value = "/landingPage",method = RequestMethod.GET)
    public String getLandingPage(@RequestParam(value = "urlType",required = true) String urlType) {
        if(logger.isDebugEnabled()) {
            logger.debug("get landing page url, url type:" + urlType);
        }

        if(StringUtils.isEmpty(urlType)){
            throw new FieldValidtorException("get landing page url, url type is empty");
        }

        return businessActivityService.getLandingPage(urlType);
    }

    /**
     * 获取商务活动URL和开始日期和结束日期
     * @param marketingId
     * @return
     */
    @RequestMapping(value = "/marketing",method = RequestMethod.GET)
    public Map<String, String> getMarketingData(@RequestParam(value = "marketingId",required = true) Long marketingId) {
        if(logger.isDebugEnabled()) {
            logger.debug("get landing page url and begin date and end date of marketing, marketing id:" + marketingId);
        }

        if(marketingId == null || marketingId < 1){
            throw new FieldValidtorException("get landing page url and begin date and end date of marketing, marketing id is empty");
        }

        return businessActivityService.getMarketingData(marketingId);
    }
}
