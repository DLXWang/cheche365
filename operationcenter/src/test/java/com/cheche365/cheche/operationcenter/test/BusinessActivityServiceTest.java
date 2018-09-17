package com.cheche365.cheche.operationcenter.test;

import com.cheche365.cheche.core.app.config.CoreConfig;
import com.cheche365.cheche.core.model.BusinessActivity;
import com.cheche365.cheche.operationcenter.app.config.OperationCenterConfig;
import com.cheche365.cheche.manage.common.model.PublicQuery;
import com.cheche365.cheche.operationcenter.service.partner.BusinessActivityService;
import com.cheche365.cheche.manage.common.web.model.PageInfo;
import com.cheche365.cheche.operationcenter.web.model.partner.ActivityAreaViewModel;
import com.cheche365.cheche.operationcenter.web.model.partner.ActivityMonitorDataViewModel;
import com.cheche365.cheche.operationcenter.web.model.partner.BusinessActivityViewModel;
import com.cheche365.cheche.operationcenter.web.model.partner.CustomerFieldViewModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.ArrayList;
import java.util.List;


/**
 * 商务活动Service测试类
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
    classes = {CoreConfig.class, OperationCenterConfig.class}
)
@EnableAutoConfiguration
@EnableWebMvc
@WebAppConfiguration
@EnableSpringDataWebSupport
@TransactionConfiguration
public class BusinessActivityServiceTest {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private BusinessActivityService businessActivityService;

    @Test
    public void add() {
        BusinessActivityViewModel viewModel = new BusinessActivityViewModel();
        viewModel.setName("新用户活动");//商务活动名称
        viewModel.setPartner(new Long(1));//合作商
        viewModel.setCooperationMode(new Long(1));//合作方式
        viewModel.setRebate(null);//佣金
        viewModel.setBudget(new Double(500000));//预算
        viewModel.setStartTime("2015-08-26 15:17:50");//活动开始时间，精确到分钟
        viewModel.setEndTime("2015-08-31 15:17:50");//活动结束时间，精确到分钟
        viewModel.setLandingPage("www.baidu.com");//落地页
        viewModel.setComment("新用户活动");//备注
        //活动支持的城市
        List<ActivityAreaViewModel> activityAreaList = new ArrayList<>();
        ActivityAreaViewModel area1 = new ActivityAreaViewModel();
        area1.setArea(new Long(110000));
        activityAreaList.add(area1);
        ActivityAreaViewModel area2 = new ActivityAreaViewModel();
        area2.setArea(new Long(310000));
        activityAreaList.add(area2);
        viewModel.setActivityArea(activityAreaList);
        //自定义字段
        List<CustomerFieldViewModel> customerFieldList = new ArrayList<>();
        CustomerFieldViewModel customerField = new CustomerFieldViewModel();
        customerField.setName("自定义字段1");
        customerField.setFirstField(new Long(1));
        customerField.setSecondField(new Long(2));
        customerField.setOperator(new Long(1));
        customerFieldList.add(customerField);
        viewModel.setCustomerField(customerFieldList);
        businessActivityService.add(viewModel);
    }

    @Test
    public void update() {
        BusinessActivityViewModel viewModel = new BusinessActivityViewModel();
        viewModel.setId(new Long(4));
        viewModel.setRebate(null);//佣金
        viewModel.setBudget(new Double(100000));//预算
        viewModel.setStartTime("2015-07-26 15:17:50");//活动开始时间，精确到分钟
        viewModel.setEndTime("2015-09-31 15:17:50");//活动结束时间，精确到分钟
        viewModel.setComment("新用户活动update");//备注
        businessActivityService.update(viewModel.getId(), viewModel);
    }

    @Test
    public void testSearch() {
        try {
            PublicQuery query = new PublicQuery();
            query.setCurrentPage(1);
            query.setPageSize(20);
            query.setKeyword("");
            query.setKeyType(1);
            Integer showFlag = 1;
            Page<BusinessActivity> page = businessActivityService.getBusinessActivityPage(query);
            PageInfo pageInfo = businessActivityService.createPageInfo(page);
            logger.debug("商务活动元素数：" + pageInfo.getTotalElements());
            logger.debug("商务活动总页数：" + pageInfo.getTotalPage());
            List<BusinessActivityViewModel> modelList = new ArrayList<>();
            for (BusinessActivity businessActivity : page.getContent()) {
                modelList.add(businessActivityService.createViewData(businessActivity, showFlag));
            }
            if (!CollectionUtils.isEmpty(modelList)) {
                for (BusinessActivityViewModel viewModel : modelList) {
                    logger.debug("商务活动名称：" + viewModel.getName());
                    logger.debug("商务活动合作商名称：" + viewModel.getPartnerName());
                    logger.debug("商务活动合作方式名称：" + viewModel.getCooperationModeName());
                    logger.debug("商务活动开始时间：" + viewModel.getStartTime());
                    logger.debug("商务活动结束时间：" + viewModel.getEndTime());
                    logger.debug("商务活动状态：" + viewModel.getStatus());
                }
            }
        } catch (Exception e) {
            logger.error("PageInfo has error", e);
        }
    }

    @Test
    public void detail() {
        Long activityId = new Long(1);
        BusinessActivityViewModel viewModel = businessActivityService.findById(activityId);
        logger.debug("商务活动名称：" + viewModel.getName());
        logger.debug("商务活动合作商名称：" + viewModel.getPartnerName());
        logger.debug("商务活动合作方式名称：" + viewModel.getCooperationModeName());
        logger.debug("商务活动开始时间：" + viewModel.getStartTime());
        logger.debug("商务活动结束时间：" + viewModel.getEndTime());
        logger.debug("商务活动状态：" + viewModel.getStatus());
        logger.debug("商务活动支持城市：" + viewModel.getCity());
        logger.debug("数据更新时间：" + viewModel.getRefreshTime());
        List<ActivityMonitorDataViewModel> monitorDataList = viewModel.getMonitorDataList();
        if (!CollectionUtils.isEmpty(monitorDataList)) {
            for (ActivityMonitorDataViewModel dataViewModel : monitorDataList) {
                logger.debug("------------------------------------------------------");
                logger.debug("监控时间：" + dataViewModel.getMonitorTime());
                logger.debug("PV：" + dataViewModel.getPv());
                logger.debug("UV：" + dataViewModel.getUv());
                logger.debug("注册：" + dataViewModel.getRegister());
                logger.debug("试算：" + dataViewModel.getQuote());
                logger.debug("提交订单数：" + dataViewModel.getSubmitCount());
                logger.debug("提交订单总额：" + dataViewModel.getSubmitAmount());
                logger.debug("支付订单数：" + dataViewModel.getPaymentCount());
                logger.debug("支付订单总额：" + dataViewModel.getPaymentAmount());
                logger.debug("特殊监控：" + dataViewModel.getSpecialMonitor());
                logger.debug("自定义字段1：" + dataViewModel.getCustomerField1());
                logger.debug("自定义字段2：" + dataViewModel.getCustomerField2());
                logger.debug("自定义字段3：" + dataViewModel.getCustomerField3());
                logger.debug("自定义字段4：" + dataViewModel.getCustomerField4());
                logger.debug("自定义字段5：" + dataViewModel.getCustomerField5());
            }
        }
    }

    @Test
    public void refreshData() {
        Long activityId = new Long(1);
        BusinessActivityViewModel viewModel = businessActivityService.refreshMonitorData(activityId);
        logger.debug("数据更新时间：" + viewModel.getRefreshTime());
        List<ActivityMonitorDataViewModel> monitorDataList = viewModel.getMonitorDataList();
        if (!CollectionUtils.isEmpty(monitorDataList)) {
            for (ActivityMonitorDataViewModel dataViewModel : monitorDataList) {
                logger.debug("------------------------------------------------------");
                logger.debug("监控时间：" + dataViewModel.getMonitorTime());
                logger.debug("PV：" + dataViewModel.getPv());
                logger.debug("UV：" + dataViewModel.getUv());
                logger.debug("注册：" + dataViewModel.getRegister());
                logger.debug("试算：" + dataViewModel.getQuote());
                logger.debug("提交订单数：" + dataViewModel.getSubmitCount());
                logger.debug("提交订单总额：" + dataViewModel.getSubmitAmount());
                logger.debug("支付订单数：" + dataViewModel.getPaymentCount());
                logger.debug("支付订单总额：" + dataViewModel.getPaymentAmount());
                logger.debug("特殊监控：" + dataViewModel.getSpecialMonitor());
                logger.debug("自定义字段1：" + dataViewModel.getCustomerField1());
                logger.debug("自定义字段2：" + dataViewModel.getCustomerField2());
                logger.debug("自定义字段3：" + dataViewModel.getCustomerField3());
                logger.debug("自定义字段4：" + dataViewModel.getCustomerField4());
                logger.debug("自定义字段5：" + dataViewModel.getCustomerField5());
            }
        }
    }

    @Test
    public void export() {
        PublicQuery query = new PublicQuery();
        query.setCurrentPage(1);
        query.setPageSize(20);
        query.setKeyword("");
        query.setKeyType(1);
        businessActivityService.createExportExcel(query);
    }

    @Test
    public void exportData() {
        Long activityId = new Long(1);
        businessActivityService.createHourExportExcel(activityId);
    }

    @Test
    public void getCityMonitorData() {
        Long activityId = new Long(1);
        Long areaId = new Long(0);
        BusinessActivityViewModel viewModel = businessActivityService.getCityMonitorData(activityId, areaId);
        logger.debug("数据更新时间：" + viewModel.getRefreshTime());
        List<ActivityMonitorDataViewModel> monitorDataList = viewModel.getMonitorDataList();
        if (!CollectionUtils.isEmpty(monitorDataList)) {
            for (ActivityMonitorDataViewModel dataViewModel : monitorDataList) {
                logger.debug("------------------------------------------------------");
                logger.debug("监控时间：" + dataViewModel.getMonitorTime());
                logger.debug("PV：" + dataViewModel.getPv());
                logger.debug("UV：" + dataViewModel.getUv());
                logger.debug("注册：" + dataViewModel.getRegister());
                logger.debug("试算：" + dataViewModel.getQuote());
                logger.debug("提交订单数：" + dataViewModel.getSubmitCount());
                logger.debug("提交订单总额：" + dataViewModel.getSubmitAmount());
                logger.debug("支付订单数：" + dataViewModel.getPaymentCount());
                logger.debug("支付订单总额：" + dataViewModel.getPaymentAmount());
                logger.debug("特殊监控：" + dataViewModel.getSpecialMonitor());
                logger.debug("自定义字段1：" + dataViewModel.getCustomerField1());
                logger.debug("自定义字段2：" + dataViewModel.getCustomerField2());
                logger.debug("自定义字段3：" + dataViewModel.getCustomerField3());
                logger.debug("自定义字段4：" + dataViewModel.getCustomerField4());
                logger.debug("自定义字段5：" + dataViewModel.getCustomerField5());
            }
        }
    }
}
