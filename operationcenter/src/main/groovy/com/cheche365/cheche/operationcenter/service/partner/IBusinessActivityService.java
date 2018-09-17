package com.cheche365.cheche.operationcenter.service.partner;

import com.cheche365.cheche.manage.common.model.PublicQuery;
import com.cheche365.cheche.operationcenter.web.model.DataTablesPageViewModel;
import com.cheche365.cheche.manage.common.web.model.ResultModel;
import com.cheche365.cheche.operationcenter.web.model.partner.BusinessActivityViewModel;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.util.Map;

/**
 * Created by sunhuazhong on 2015/8/26.
 */
public interface IBusinessActivityService {
    /**
     * 新建商务活动
     * @param viewModel
     */
    boolean add(BusinessActivityViewModel viewModel);

    /**
     * 查询商务活动详情
     * @param id
     * @return
     */
    BusinessActivityViewModel findById(Long id);

    /**
     * 修改商务活动
     * @param activityId
     * @param viewData
     * @return
     */
    boolean update(Long activityId, BusinessActivityViewModel viewData);

    /**
     * 删除商务活动
     * @param id
     * @return
     */
    boolean delete(Long id);

    /**
     * 查询商务活动
     * @param showFlag 显示标记，1-不显示监控数据；2-显示最新监控数据；3-按天显示所有监控数据；4-按小时显示所有监控数据
     * @return
     */
    DataTablesPageViewModel<BusinessActivityViewModel> search(PublicQuery query, Integer showFlag);

    /**
     * 更新商务活动监控数据
     * @param activityId
     * @return
     */
    BusinessActivityViewModel refreshMonitorData(Long activityId);

    /**
     * 导出查询到的商务活动列表

     * @return
     */
    HSSFWorkbook createExportExcel(PublicQuery query);

    /**
     * 导出商务活动时段监控数据
     * @param activityId
     * @return
     */
    HSSFWorkbook createHourExportExcel(Long activityId);

    /**
     * 更新商务活动监控数据
     * @param activityId
     * @param areaId 全国：1；未知来源：-1；城市：城市id
     * @return
     */
    BusinessActivityViewModel getCityMonitorData(Long activityId, Long areaId);

    /**
     * 验证商务活动编号唯一性以及商务活动的开始时间是否在推广活动的时间范围内
     * @param code
     * @param marketingId
     * @param startTime
     * @return
     */
    ResultModel checkBusinessActivityData(String code, Long marketingId, String startTime);

    /**
     * 获取商务活动URL
     * 包括M站首页或购买页URL
     * @param urlType
     * @return
     */
    String getLandingPage(String urlType);

    /**
     * 获取Marketing活动URL
     * @param marketingId
     * @return
     */
    Map<String, String> getMarketingData(Long marketingId);
}
