package com.cheche365.cheche.operationcenter.service.partner;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.common.util.DoubleUtils;
import com.cheche365.cheche.core.constants.WebConstants;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.*;
import com.cheche365.cheche.core.util.BeanUtil;
import com.cheche365.cheche.manage.common.model.PublicQuery;
import com.cheche365.cheche.manage.common.service.BaseService;
import com.cheche365.cheche.manage.common.service.InternalUserManageService;
import com.cheche365.cheche.manage.common.util.AssertUtil;
import com.cheche365.cheche.manage.common.util.ExcelUtil;
import com.cheche365.cheche.manage.common.web.model.PageInfo;
import com.cheche365.cheche.manage.common.web.model.ResultModel;
import com.cheche365.cheche.operationcenter.constants.BusinessActivityConstants;
import com.cheche365.cheche.operationcenter.web.model.DataTablesPageViewModel;
import com.cheche365.cheche.operationcenter.web.model.partner.ActivityAreaViewModel;
import com.cheche365.cheche.operationcenter.web.model.partner.ActivityMonitorDataViewModel;
import com.cheche365.cheche.operationcenter.web.model.partner.BusinessActivityViewModel;
import com.cheche365.cheche.operationcenter.web.model.partner.CustomerFieldViewModel;
import com.cheche365.cheche.web.util.UrlUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.*;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by sunhuazhong on 2015/8/26.
 */
@Service(value = "businessActivityService")
@Transactional
public class BusinessActivityService extends BaseService implements IBusinessActivityService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private BusinessActivityRepository businessActivityRepository;

    @Autowired
    private ActivityAreaRepository activityAreaRepository;

    @Autowired
    private AreaRepository areaRepository;

    @Autowired
    private ActivityMonitorDataRepository activityMonitorDataRepository;

    @Autowired
    private PartnerRepository partnerRepository;

    @Autowired
    private CooperationModeRepository cooperationModeRepository;

    @Autowired
    private CustomerFieldRepository customerFieldRepository;

    @Autowired
    private MonitorDataTypeRepository monitorDataTypeRepository;

    @Autowired
    private ArithmeticOperatorRepository arithmeticOperatorRepository;

    @Autowired
    private PaymentChannelRepository paymentChannelRepository;

    @Autowired
    private MarketingRepository marketingRepository;

    @Autowired
    private InternalUserManageService internalUserManageService;


    @Override
    public boolean add(BusinessActivityViewModel viewModel) {
        try {
            //保存商务活动
            BusinessActivity businessActivity = businessActivityRepository.save(this.createBusinessActivity(viewModel));
            // 保存商务活动落地页
            String landingPage = viewModel.getLandingPage();
            landingPage=landingPage.replace(WebConstants.getSchemaURL(true),"").replace(WebConstants.getSchemaURL(false),"");
            landingPage=landingPage.substring(landingPage.indexOf("/"),landingPage.length()).replace(BusinessActivityConstants.LANDING_PAGE_KEYWORD, businessActivity.getCode());;
            businessActivity.setLandingPage(businessActivity.assembleCpsUrl(landingPage));
            businessActivity.setOriginalUrl(landingPage);
            businessActivityRepository.save(businessActivity);

            //保存城市
            if (StringUtils.isNotEmpty(viewModel.getCity())) {
                activityAreaRepository.save(this.createActivityArea(businessActivity, viewModel.getCity()));
            }

            //保存自定义字段
            if (viewModel.getCustomerField() != null && viewModel.getCustomerField().size() > 0) {
                customerFieldRepository.save(this.createCustomerField(businessActivity, viewModel.getCustomerField()));
            }

            return true;
        } catch (Exception e) {
            logger.error("add business activity has error", e);
        }
        return false;
    }

    @Override
    public BusinessActivityViewModel findById(Long id) {
        try {
            BusinessActivity businessActivity = businessActivityRepository.findOne(id);
            return this.createViewData(businessActivity, 3);
        } catch (Exception e) {
            logger.error("find business activity by id has error", e);
        }
        return null;
    }

    @Override
    public boolean update(Long activityId, BusinessActivityViewModel viewData) {
        try {
            //保存商务活动
            viewData.setId(activityId);
            businessActivityRepository.save(this.createBusinessActivityForUpdate(viewData));
            return true;
        } catch (Exception e) {
            logger.error("update business activity has error", e);
        }
        return false;
    }

    @Override
    public boolean delete(Long id) {
        return false;
    }

    @Override
    public DataTablesPageViewModel<BusinessActivityViewModel> search(PublicQuery query, Integer showFlag) {
        try {
            Page<BusinessActivity> page = getBusinessActivityPage(query);
            List<BusinessActivityViewModel> pageViewDataList = new ArrayList<>();
            for (BusinessActivity businessActivity : page.getContent()) {
                BusinessActivityViewModel viewData = createViewData(businessActivity, showFlag);
                pageViewDataList.add(viewData);
            }
            PageInfo pageInfo = createPageInfo(page);
            return new DataTablesPageViewModel<>(pageInfo.getTotalElements(), pageInfo.getTotalElements(), query.getDraw(), pageViewDataList);
        } catch (Exception e) {
            logger.error("find business activity info by page has error", e);
        }
        return null;
    }

    public Page<BusinessActivity> getBusinessActivityPage(PublicQuery query) {
        try {
            return this.findBySpecAndPaginate(query,
                    buildPageable(query.getCurrentPage(), query.getPageSize(), Sort.Direction.DESC, SORT_ID));
        } catch (Exception e) {
            logger.error("find business activity page by page has error", e);
        }
        return null;
    }


    /**
     * 组建商务活动对象，返回到前端显示
     *
     * @param businessActivity
     * @param showFlag         显示标记，1-不显示监控数据；2-显示最新监控数据，不显示自定义字段；3-按天显示所有监控数据；4-按小时显示所有监控数据
     * @return
     */
    public BusinessActivityViewModel createViewData(BusinessActivity businessActivity, Integer showFlag) {
        // 活动支持的城市
        List<ActivityArea> activityAreaList = activityAreaRepository.findByBusinessActivity(businessActivity);
        // 最新数据更新时间
        ActivityMonitorData lastActivityMonitorData = activityMonitorDataRepository
                .findFirstByBusinessActivityOrderByMonitorTimeDesc(businessActivity);
        BusinessActivityViewModel viewModel = new BusinessActivityViewModel();
        // 获取商务活动基本信息
        getBasicData(businessActivity, activityAreaList, lastActivityMonitorData, viewModel);
        // 最新数据更新时间以及监控数据,商务活动列表导出Excel使用
        if (showFlag == 2) {
            setActivityArea(activityAreaList, viewModel);
            getLastMonitorData(businessActivity, lastActivityMonitorData, viewModel);
        }
        // 最新数据更新时间以及按天的监控数据，区分城市,商务活动详情按天获取监控数据使用
        else if (showFlag == 3) {
            // 获取全国监控数据，并汇总数据，按天计算
            setActivityArea(activityAreaList, viewModel);
            getCountryMonitorData(businessActivity, viewModel, 1, new Long(0));
        }
        // 最新数据更新时间以及按小时的监控数据,商务活动详情按小时导出Excel使用
        else if (showFlag == 4) {
            // 获取全国监控数据，并汇总数据，按小时计算
            getCountryMonitorData(businessActivity, viewModel, 2, new Long(0));
        }

        return viewModel;
    }

    private void setActivityArea(List<ActivityArea> activityAreaList, BusinessActivityViewModel viewModel) {
        // 活动支持的城市
        List<ActivityAreaViewModel> activityAreaViewModelList = new ArrayList<>();
        for (ActivityArea activityArea : activityAreaList) {
            ActivityAreaViewModel activityAreaViewModel = new ActivityAreaViewModel();
            activityAreaViewModel.setId(activityArea.getId());
            activityAreaViewModel.setBusinessActivity(activityArea.getBusinessActivity().getId());
            activityAreaViewModel.setArea(activityArea.getArea().getId());
            activityAreaViewModel.setBusinessActivityName(activityArea.getBusinessActivity().getName());
            activityAreaViewModel.setAreaName(activityArea.getArea().getName());
            activityAreaViewModelList.add(activityAreaViewModel);
        }
        viewModel.setActivityArea(activityAreaViewModelList);
    }

    /**
     * 获取全国监控数据，按天计
     *
     * @param businessActivity
     * @param viewModel
     * @param timeFlag         1-按天计算；2-按小时计算
     * @param areaId           0:全国；-1：未知来源
     */
    private void getCountryMonitorData(BusinessActivity businessActivity, BusinessActivityViewModel viewModel,
                                       int timeFlag, Long areaId) {
        List<ActivityMonitorDataViewModel> countryMonitorDataList = new ArrayList<>();
        // 获取指定城市的第一条监控数据，根据该数据的监控时间按天或小时进行统计
        ActivityMonitorData firstActivityMonitorData = getFirstActivityMonitorData(businessActivity, areaId);
        if (firstActivityMonitorData != null) {
            Date startTime = firstActivityMonitorData.getMonitorTime();
            Date currentTime = DateUtils.getCurrentDate(DateUtils.DATE_LONGTIME24_PATTERN);
            while (startTime.before(currentTime)) {
                ActivityMonitorDataViewModel dayMonitorData = new ActivityMonitorDataViewModel();
                Date startMonitorTime = null;//监控时间的开始时间
                Date endMonitorTime = null;//监控时间的结束时间
                if (timeFlag == 1) {
                    dayMonitorData.setMonitorTime(DateUtils.getDateString(startTime, "yyyyMMdd"));//监控时间
                    startMonitorTime = DateUtils.getDayStartTime(startTime);
                    endMonitorTime = DateUtils.getDayEndTime(startTime);
                } else {
                    dayMonitorData.setMonitorTime(DateUtils.getDateString(startTime, "yyyyMMdd HH:mm"));//监控时间
                    startMonitorTime = DateUtils.getHourStartTime(startTime);
                    endMonitorTime = DateUtils.getHourEndTime(startTime);
                }

                // 指定之间范围内容的监控数据
                List<ActivityMonitorData> dataList = getCityMonitorData(businessActivity, areaId, startMonitorTime, endMonitorTime);
                if (!CollectionUtils.isEmpty(dataList)) {
                    for (ActivityMonitorData data : dataList) {
                        // 构建监控数据对象
                        setMonitorData(dayMonitorData, data);
                    }
                }
                countryMonitorDataList.add(dayMonitorData);
                startTime = addTime(startTime, timeFlag);
            }
        }
        viewModel.setMonitorDataList(countryMonitorDataList);
    }

    private List<ActivityMonitorData> getCityMonitorData(BusinessActivity businessActivity, Long areaId,
                                                         Date startMonitorTime, Date endMonitorTime) {
        List<ActivityMonitorData> dataList = null;
        // 全国第一条监控数据
        if (areaId == 0) {
            dataList = activityMonitorDataRepository
                    .findByBusinessActivityAndMonitorTimeGreaterThanEqualAndMonitorTimeLessThanEqualOrderByMonitorTime(
                            businessActivity, startMonitorTime, endMonitorTime);
        }
        // 未知来源第一条监控数据
        else if (areaId == -1) {
            dataList = activityMonitorDataRepository.listUnknownSourceMonitorData(businessActivity.getId(), startMonitorTime, endMonitorTime);
        }
        // 指定城市第一条监控数据
        else {
            Area area = areaRepository.findOne(areaId);
            dataList = activityMonitorDataRepository
                    .findByBusinessActivityAndAreaAndMonitorTimeGreaterThanEqualAndMonitorTimeLessThanEqualOrderByMonitorTime(
                            businessActivity, area, startMonitorTime, endMonitorTime);
        }
        return dataList;
    }

    private ActivityMonitorData getFirstActivityMonitorData(BusinessActivity businessActivity, Long areaId) {
        ActivityMonitorData firstActivityMonitorData = null;
        // 全国第一条监控数据
        if (areaId == 0) {
            firstActivityMonitorData = activityMonitorDataRepository
                    .findFirstByBusinessActivityOrderByMonitorTime(businessActivity);
        }
        // 未知来源第一条监控数据
        else if (areaId == -1) {
            firstActivityMonitorData = activityMonitorDataRepository.getFirstUnknownSourceMonitorData(businessActivity.getId());
        }
        // 指定城市第一条监控数据
        else {
            Area area = areaRepository.findOne(areaId);
            firstActivityMonitorData = activityMonitorDataRepository
                    .findFirstByBusinessActivityAndAreaOrderByMonitorTime(businessActivity, area);
        }
        return firstActivityMonitorData;
    }

    private void setMonitorData(ActivityMonitorDataViewModel monitorData, ActivityMonitorData data) {
        monitorData.setPv(getValue(monitorData.getPv()) + getValue(data.getPv()));
        monitorData.setUv(getValue(monitorData.getUv()) + getValue(data.getUv()));
        monitorData.setRegister(getValue(monitorData.getRegister()) + getValue(data.getRegister()));
        monitorData.setQuote(getValue(monitorData.getQuote()) + getValue(data.getQuote()));
        monitorData.setSubmitCount(getValue(monitorData.getSubmitCount()) + getValue(data.getSubmitCount()));
        monitorData.setSubmitAmount(getValue(monitorData.getSubmitAmount()) + getValue(data.getSubmitAmount()));
        monitorData.setPaymentCount(getValue(monitorData.getPaymentCount()) + getValue(data.getPaymentCount()));
        monitorData.setPaymentAmount(getValue(monitorData.getPaymentAmount()) + getValue(data.getPaymentAmount()));
        monitorData.setNoAutoTaxAmount(getValue(monitorData.getNoAutoTaxAmount()) + getValue(data.getNoAutoTaxAmount()));
        monitorData.setSpecialMonitor(getValue(monitorData.getSpecialMonitor()) + getValue(data.getSpecialMonitor()));
    }

    private Integer getValue(Integer integerValue) {
        return integerValue == null ? new Integer(0) : integerValue;
    }

    private Double getValue(Double doubleValue) {
        return doubleValue == null ? new Double(0.0) : doubleValue;
    }

    private Date addTime(Date startTime, int timeFlag) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startTime);
        if (timeFlag == 1) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
        } else {
            calendar.add(Calendar.HOUR_OF_DAY, 1);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
        }
        return calendar.getTime();
    }

    /**
     * 组建商务活动对象
     *
     * @param viewModel
     * @return
     */
    public BusinessActivity createBusinessActivity(BusinessActivityViewModel viewModel) {
        BusinessActivity activity = new BusinessActivity();
        activity.setName(viewModel.getName());//商务活动名称
        if (viewModel.getPartner() != null) {
            Partner partner = partnerRepository.findOne(viewModel.getPartner());
            activity.setPartner(partner);//合作商
        }
        CooperationMode cooperationMode = cooperationModeRepository.findOne(viewModel.getCooperationMode());
        activity.setCooperationMode(cooperationMode);//合作方式
        activity.setRebate(viewModel.getRebate());//佣金
        activity.setBudget(viewModel.getBudget());//预算
        activity.setStartTime(DateUtils.getDate(viewModel.getStartTime(), "yyyy-MM-dd HH:mm"));//活动开始时间
        activity.setEndTime(DateUtils.getDate(viewModel.getEndTime(), "yyyy-MM-dd HH:mm"));//活动结束时间
        activity.setLandingPage(viewModel.getLandingPage());//落地页
        activity.setComment(viewModel.getComment());//备注
        activity.setCreateTime(Calendar.getInstance().getTime());//创建时间
        activity.setUpdateTime(Calendar.getInstance().getTime());//修改时间
        activity.setOperator(internalUserManageService.getCurrentInternalUser());//操作人
        activity.setLinkMan(viewModel.getLinkMan());//联系人
        activity.setMobile(viewModel.getMobile());//联系方式
        activity.setFrequency(viewModel.getFrequency());//邮件报表发送频率
        activity.setEmail(viewModel.getEmail());//报表接收邮箱
        activity.setRefreshFlag(viewModel.isRefreshFlag());
        if (viewModel.getCode() != null) {
            activity.setCode(viewModel.getCode().toLowerCase());//活动编号
        }

        if (viewModel.getLandingPageType() != null && (viewModel.getLandingPageType() == 3 || viewModel.getLandingPageType() == 4)) {
            activity.setObjId(viewModel.getObjId());//M端活动页，PC端活动页
            activity.setObjTable("marketing");//推广活动，marketing表
        }

        activity.setLandingPageType(viewModel.getLandingPageType());//落地页类型

        if (viewModel.getLandingPageType() != null) {
            String[] contains = null;
            switch (viewModel.getLandingPageType()) {
                case 1://m站首页
                    contains = new String[]{"topBrand", "myCenter", "topCarousel", "activityEntry", "ourCustomer",
                            "bottomCarousel", "bottomInfo", "bottomDownload", "enable", "footer", "btn", "app", "display"};
                    activity.setPaymentChannels(createPaymentChannels(viewModel));//活动支持的支付方式
                    BeanUtil.copyPropertiesContain(viewModel, activity, contains);
                    break;
                case 2://m站购买页
                    contains = new String[]{"enable", "footer", "btn", "app", "display"};
                    activity.setPaymentChannels(createPaymentChannels(viewModel));//活动支持的支付方式
                    BeanUtil.copyPropertiesContain(viewModel, activity, contains);
                    break;
            }
        }
        return activity;
    }

    private List<PaymentChannel> createPaymentChannels(BusinessActivityViewModel viewModel) {
        List<PaymentChannel> paymentChannelList = new ArrayList<>();
        if (viewModel.getPaymentChannels() != null) {
            String[] paymentChannelIds = viewModel.getPaymentChannels().split(",");
            for (String paymentChannelId : paymentChannelIds) {
                PaymentChannel paymentChannel = paymentChannelRepository.findOne(Long.parseLong(paymentChannelId));
                AssertUtil.notNull(paymentChannel, "illegal paymentChannel id, can not find PaymentChannel by id -> " + paymentChannelId);
                paymentChannelList.add(paymentChannel);
            }
        }
        return paymentChannelList;
    }

    /**
     * 组建商务活动对象 用于更新
     *
     * @param viewModel
     * @return
     */
    private BusinessActivity createBusinessActivityForUpdate(BusinessActivityViewModel viewModel) {
        BusinessActivity activity = businessActivityRepository.findOne(viewModel.getId());
        activity.setRebate(viewModel.getRebate());//佣金
        activity.setBudget(viewModel.getBudget());//预算
        activity.setStartTime(DateUtils.getDate(viewModel.getStartTime(), "yyyy-MM-dd HH:mm"));//活动开始时间
        activity.setEndTime(DateUtils.getDate(viewModel.getEndTime(), "yyyy-MM-dd HH:mm"));//活动结束时间
        activity.setLinkMan(viewModel.getLinkMan());//联系人
        activity.setMobile(viewModel.getMobile());//联系方式
        activity.setFrequency(viewModel.getFrequency());//邮件报表发送频率
        activity.setEmail(viewModel.getEmail());//报表接收邮箱
        activity.setComment(viewModel.getComment());//备注
        activity.setUpdateTime(Calendar.getInstance().getTime());//修改时间
        return activity;
    }

    /**
     * 组建活动城市对象
     *
     * @param businessActivity
     * @param city
     * @return
     */
    private List<ActivityArea> createActivityArea(BusinessActivity businessActivity, String city) {
        List<ActivityArea> activityAreas = new ArrayList<>();
        String[] areas = city.split(",");
        for (String areaId : areas) {
            ActivityArea activityArea = new ActivityArea();
            activityArea.setBusinessActivity(businessActivity);
            activityArea.setArea(areaRepository.findOne(Long.valueOf(areaId)));
            activityAreas.add(activityArea);
        }

        return activityAreas;
    }

    /**
     * 组建自定义字段对象
     *
     * @param businessActivity
     * @param customerFieldViewModels
     * @return
     */
    private List<CustomerField> createCustomerField(BusinessActivity businessActivity, List<CustomerFieldViewModel> customerFieldViewModels) {
        List<CustomerField> customerFields = new ArrayList<>();
        customerFieldViewModels.forEach(customerFieldViewModel -> {
            if (StringUtils.isNotEmpty(customerFieldViewModel.getName())) {
                CustomerField customerField = new CustomerField();
                customerField.setBusinessActivity(businessActivity);
                customerField.setName(customerFieldViewModel.getName());
                customerField.setFirstField(monitorDataTypeRepository.findOne(customerFieldViewModel.getFirstField()));
                customerField.setOperator(arithmeticOperatorRepository.findOne(customerFieldViewModel.getOperator()));
                customerField.setSecondField(monitorDataTypeRepository.findOne(customerFieldViewModel.getSecondField()));
                customerFields.add(customerField);
            }
        });

        return customerFields;
    }

    /**
     * 获取商务活动最新的监控数据
     *
     * @param businessActivity
     * @param lastActivityMonitorData
     * @param viewModel
     */
    private void getLastMonitorData(BusinessActivity businessActivity,
                                    ActivityMonitorData lastActivityMonitorData, BusinessActivityViewModel viewModel) {
        // 截止到最新更新时间的所有监控数据汇总
        if (lastActivityMonitorData != null) {
            List<ActivityMonitorData> activityMonitorDataList = activityMonitorDataRepository
                    .findByBusinessActivityAndMonitorTimeLessThanEqual(businessActivity, lastActivityMonitorData.getMonitorTime());
            ActivityMonitorDataViewModel monitorDataViewModel = viewModel.getLastMonitorData();
            for (ActivityMonitorData activityMonitorData : activityMonitorDataList) {
                monitorDataViewModel.setPv(monitorDataViewModel.getPv()
                        + getValue(activityMonitorData.getPv()));
                monitorDataViewModel.setUv(monitorDataViewModel.getUv()
                        + getValue(activityMonitorData.getUv()));
                monitorDataViewModel.setRegister(monitorDataViewModel.getRegister()
                        + getValue(activityMonitorData.getRegister()));
                monitorDataViewModel.setQuote(monitorDataViewModel.getQuote()
                        + getValue(activityMonitorData.getQuote()));
                monitorDataViewModel.setSubmitCount(monitorDataViewModel.getSubmitCount()
                        + getValue(activityMonitorData.getSubmitCount()));
                monitorDataViewModel.setSubmitAmount(monitorDataViewModel.getSubmitAmount()
                        + DoubleUtils.displayDoubleValue(activityMonitorData.getSubmitAmount()));
                monitorDataViewModel.setPaymentCount(monitorDataViewModel.getPaymentCount()
                        + getValue(activityMonitorData.getPaymentCount()));
                monitorDataViewModel.setPaymentAmount(monitorDataViewModel.getPaymentAmount()
                        + DoubleUtils.displayDoubleValue(activityMonitorData.getPaymentAmount()));
                monitorDataViewModel.setNoAutoTaxAmount(monitorDataViewModel.getNoAutoTaxAmount()
                        + DoubleUtils.displayDoubleValue(activityMonitorData.getNoAutoTaxAmount()));
            }
        }
    }

    /**
     * 获取商务活动基本信息
     *
     * @param businessActivity
     * @param activityAreaList
     * @param lastActivityMonitorData
     * @param viewModel
     */
    private void getBasicData(BusinessActivity businessActivity, List<ActivityArea> activityAreaList,
                              ActivityMonitorData lastActivityMonitorData, BusinessActivityViewModel viewModel) {
        viewModel.setId(businessActivity.getId());
        viewModel.setName(businessActivity.getName());//商务活动名称
        viewModel.setCode(businessActivity.getCode());//商务活动编号
        viewModel.setPartner(businessActivity.getPartner()==null?null:businessActivity.getPartner().getId());//合作商
        viewModel.setPartnerName(businessActivity.getPartner()==null?null:businessActivity.getPartner().getName());//合作商名称
        viewModel.setCooperationMode(businessActivity.getCooperationMode().getId());//合作方式
        viewModel.setCooperationModeName(businessActivity.getCooperationMode().getName());//合作方式名称
        viewModel.setBudget(businessActivity.getBudget());//预算
        viewModel.setStatus(getStatus(businessActivity));//状态
        viewModel.setLandingPage(UrlUtil.toFullUrl(businessActivity.getLandingPage()));//落地页
        viewModel.setLandingPageType(businessActivity.getLandingPageType());//落地页类型
        viewModel.setComment(businessActivity.getComment());//备注
        viewModel.setLinkMan(businessActivity.getLinkMan());//联系人
        viewModel.setMobile(businessActivity.getMobile());//联系人手机号

        viewModel.setStartTime(DateUtils.getDateString(
                businessActivity.getStartTime(), "yyyy-MM-dd HH:mm"));//活动开始时间，精确到分钟
        viewModel.setEndTime(DateUtils.getDateString(
                businessActivity.getEndTime(), "yyyy-MM-dd HH:mm"));//活动结束时间，精确到分钟
        viewModel.setCreateTime(DateUtils.getDateString(
                businessActivity.getCreateTime(), DateUtils.DATE_LONGTIME24_PATTERN));//创建时间
        viewModel.setUpdateTime(DateUtils.getDateString(
                businessActivity.getUpdateTime(), DateUtils.DATE_LONGTIME24_PATTERN));//修改时间
        viewModel.setRefreshTime(businessActivity.getRefreshTime() == null ?
                "" : DateUtils.getDateString(businessActivity.getRefreshTime(), DateUtils.DATE_LONGTIME24_PATTERN));//数据更新时间
        viewModel.setRefreshFlag(businessActivity.isRefreshFlag());//是否可刷新标记
        viewModel.setOperator(businessActivity.getOperator() == null ? "" : businessActivity.getOperator().getName());//操作人

        List<String> cityList = new ArrayList<>();
        for (ActivityArea activityArea : activityAreaList) {
            cityList.add(activityArea.getArea().getName());
        }
        viewModel.setCity(String.join(",", cityList));
        if (lastActivityMonitorData != null) {
            viewModel.getLastMonitorData().setMonitorTime(DateUtils.getDateString(
                    lastActivityMonitorData.getMonitorTime(), DateUtils.DATE_LONGTIME24_PATTERN));
        }

        viewModel.setEmail(businessActivity.getEmail());//联系人邮箱
        viewModel.setFrequency(businessActivity.getFrequency());//发送频率，1-每周；2-每月；3-不发送
        // 购买页参数
        viewModel.setRebate(businessActivity.getRebate());//佣金
        viewModel.setEnable(businessActivity.isEnable());//是否使用优惠券,true-使用，false-不使用
        viewModel.setDisplay(businessActivity.isDisplay());//是否显示"回到首页"、"我的"等按钮 true-显示  false-不显示
        viewModel.setHome(businessActivity.isHome());//是否可以回到首页，默认为true，true-可以，false-不可以
        viewModel.setFooter(businessActivity.isFooter());//是否显示底部公司标识，默认为false，true-显示，false-不显示
        viewModel.setMine(businessActivity.isMine());//是否可以显示我的，默认为true，true-显示，false-不显示
        viewModel.setBtn(businessActivity.isBtn());//成功提交订单后是否显示按钮，默认为true，true-显示，false-不显示
        viewModel.setApp(businessActivity.isApp());//成功提交订单后是否显示公司微信二维码，默认为false，true-显示，false-不显示

        //m站首页配置项
        String[] contains = new String[]{"topBrand", "myCenter", "topCarousel", "activityEntry", "ourCustomer",
                "bottomCarousel", "bottomInfo", "bottomDownload"};
        BeanUtil.copyPropertiesContain(businessActivity, viewModel, contains);

        // 关联推广活动
        if (businessActivity.getObjId() != null
                && businessActivity.getLandingPageType() != null
                && (businessActivity.getLandingPageType() == 3 || businessActivity.getLandingPageType() == 4)) {
            Marketing marketing = marketingRepository.findOne(Long.parseLong(businessActivity.getObjId()));
            viewModel.setObjId(marketing.getId() + "");
            viewModel.setMarketingName(marketing.getName());
            viewModel.setBeginDate(DateUtils.getDateString(marketing.getBeginDate(), "yyyy-MM-dd HH:mm"));
            viewModel.setEndDate(DateUtils.getDateString(marketing.getEndDate(), "yyyy-MM-dd HH:mm"));
        }

        // 自定义字段
        setCustomerField(businessActivity, viewModel);
    }

    /**
     * 获取该活动的状态，未开始，进行中，已结束
     *
     * @param businessActivity
     * @return
     */
    private String getStatus(BusinessActivity businessActivity) {
        Date currentTime = DateUtils.getCurrentDate(DateUtils.DATE_LONGTIME24_PATTERN);
        if (currentTime.before(businessActivity.getStartTime())) {
            return BusinessActivityConstants.ACTIVITY_STATUS_NOT_START;
        } else if (currentTime.after(businessActivity.getEndTime())) {
            return BusinessActivityConstants.ACTIVITY_STATUS_FINISHED;
        } else {
            return BusinessActivityConstants.ACTIVITY_STATUS_RUNNING;
        }
    }


    /**
     * 分页查询
     *
     * @param pageable 分页信息
     * @return Page<Partner>
     */
    private Page<BusinessActivity> findBySpecAndPaginate(PublicQuery publicQuery, Pageable pageable) throws Exception {
        return businessActivityRepository.findAll(new Specification<BusinessActivity>() {
            @Override
            public Predicate toPredicate(Root<BusinessActivity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                CriteriaQuery<BusinessActivity> criteriaQuery = cb.createQuery(BusinessActivity.class);

                //条件构造
                List<Predicate> predicateList = new ArrayList<>();
                if (StringUtils.isNotBlank(publicQuery.getKeyword())) {
                    // 合作商名称
                    if (publicQuery.getKeyType() == 1) {
                        Path<String> partnerNamePath = root.get("partner").get("name");
                        predicateList.add(cb.like(partnerNamePath, publicQuery.getKeyword() + "%"));
                    }
                    // 商务活动名称
                    else if (publicQuery.getKeyType() == 2) {
                        Path<String> activityNamePath = root.get("name");
                        predicateList.add(cb.like(activityNamePath, publicQuery.getKeyword() + "%"));
                    }
                }
                predicateList.add(cb.notEqual(root.get("cooperationMode").get("id"), CooperationMode.Enum.MARKETING.getId()));
                Predicate[] predicates = new Predicate[predicateList.size()];
                predicates = predicateList.toArray(predicates);
                return criteriaQuery.where(predicates).getRestriction();
            }
        }, pageable);
    }

    @Override
    public BusinessActivityViewModel refreshMonitorData(Long activityId) {
        BusinessActivity businessActivity = businessActivityRepository.findOne(activityId);

        BusinessActivityViewModel viewModel = new BusinessActivityViewModel();
        // 最新监控数据更新时间
        viewModel.setRefreshTime(DateUtils.getDateString(businessActivity.getRefreshTime(), DateUtils.DATE_LONGTIME24_PATTERN));
        viewModel.setRefreshFlag(businessActivity.isRefreshFlag());//是否可刷新标记

        // 活动状态为进行中时获取最新的监控数据，按天计算，全国
        getCountryMonitorData(businessActivity, viewModel, 1, new Long(0));
        // 自定义字段
        setCustomerField(businessActivity, viewModel);
        return viewModel;
    }

    private void setCustomerField(BusinessActivity businessActivity, BusinessActivityViewModel viewModel) {
        List<CustomerField> customerFieldList = customerFieldRepository.findByBusinessActivityOrderById(businessActivity);
        if (!CollectionUtils.isEmpty(customerFieldList)) {
            List<CustomerFieldViewModel> customerFieldViewModelList = new ArrayList<>();
            customerFieldList.forEach(customerField -> {
                CustomerFieldViewModel customerFieldViewModel = new CustomerFieldViewModel();
                customerFieldViewModel.setId(customerField.getId());
                customerFieldViewModel.setName(customerField.getName());
                customerFieldViewModelList.add(customerFieldViewModel);
            });
            viewModel.setCustomerField(customerFieldViewModelList);
        }
    }

    @Override
    public HSSFWorkbook createExportExcel(PublicQuery query) {
        List<BusinessActivityViewModel> dataList = this.getAllResult(query, 2);

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("商务活动查询结果");

        HSSFFont font = ExcelUtil.createFont(workbook, "宋体", (short) 12);
        HSSFCellStyle cellStyle = ExcelUtil.createCellStyle(workbook, font);

        HSSFFont fontTitle = ExcelUtil.createFont(workbook, "宋体", (short) 14);
        HSSFCellStyle cellStyleTitle = ExcelUtil.createCellStyle(workbook, fontTitle);

        ExcelUtil.createStrCellValues(sheet, 0, this.supplyDayExcelTitle(sheet), cellStyleTitle);

        Integer index = 1;
        for (BusinessActivityViewModel data : dataList) {
            ExcelUtil.createStrCellValues(sheet, index, this.supplyDayExcelContent(data), cellStyle);
            index++;
        }

        return workbook;
    }

    /**
     * 获取导出结果集
     *
     * @param showFlag 显示标记，1-不显示监控数据；2-显示最新监控数据；3-按天显示所有监控数据；4-按小时显示所有监控数据
     * @return
     */
    private List<BusinessActivityViewModel> getAllResult(PublicQuery query, Integer showFlag) {
        List<BusinessActivityViewModel> dataList = new ArrayList<>();
        DataTablesPageViewModel<BusinessActivityViewModel> model = this.search(query, showFlag);
        dataList.addAll(model.getAaData());

        Integer totalPage = query.getPageSize();
        for (Integer index = 2; index <= totalPage; index++) {
            query.setCurrentPage(index);
            dataList.addAll(this.search(query, showFlag).getAaData());
        }

        return dataList;
    }

    private String[] supplyDayExcelContent(BusinessActivityViewModel data) {
        DecimalFormat format = new DecimalFormat("#.##");
        return new String[]{
                "HD" + data.getId() + "",//ID
                data.getName(),//商务活动名字
                data.getPartnerName(),//合作商
                data.getCooperationModeName(),//合作方式
                format.format(DoubleUtils.doubleValue(data.getRebate())),//佣金
                data.getCity() + "",//城市
                format.format(DoubleUtils.doubleValue(data.getBudget())),//预算
                data.getStartTime(),//活动开始时间
                data.getEndTime(),//活动结束时间
                data.getLandingPage(),//落地页
                data.getRefreshTime(),//数据更新时间
                data.getComment() == null ? "" : data.getComment(),//备注
                data.getLastMonitorData().getPv() == null ? "0" : data.getLastMonitorData().getPv() + "",//PV
                data.getLastMonitorData().getUv() == null ? "0" : data.getLastMonitorData().getUv() + "",//UV
                data.getLastMonitorData().getRegister() == null ? "0" : data.getLastMonitorData().getRegister() + "",//注册
                data.getLastMonitorData().getQuote() == null ? "0" : data.getLastMonitorData().getQuote() + "",//试算
                data.getLastMonitorData().getSubmitCount() == null ? "0" : data.getLastMonitorData().getSubmitCount() + "",//提交订单数
                format.format(DoubleUtils.doubleValue(data.getLastMonitorData().getSubmitAmount())),//提交订单总额
                data.getLastMonitorData().getPaymentCount() == null ? "0" : data.getLastMonitorData().getPaymentCount() + "",//支付订单数
                format.format(DoubleUtils.doubleValue(data.getLastMonitorData().getPaymentAmount())),//支付订单总额
                format.format(DoubleUtils.doubleValue(data.getLastMonitorData().getNoAutoTaxAmount()))//不包含车船税总额
        };
    }

    private String[] supplyDayExcelTitle(HSSFSheet sheet) {
        sheet.setColumnWidth(0, 18 * 120);//ID
        sheet.setColumnWidth(1, 18 * 300);//商务活动名字
        sheet.setColumnWidth(2, 18 * 300);//合作商
        sheet.setColumnWidth(3, 18 * 180);//合作方式
        sheet.setColumnWidth(4, 18 * 250);//佣金
        sheet.setColumnWidth(5, 18 * 250);//城市
        sheet.setColumnWidth(6, 18 * 250);//预算
        sheet.setColumnWidth(7, 18 * 350);//活动开始时间
        sheet.setColumnWidth(8, 18 * 350);//活动结束时间
        sheet.setColumnWidth(9, 18 * 400);//落地页
        sheet.setColumnWidth(10, 18 * 350);//数据更新时间
        sheet.setColumnWidth(11, 18 * 400);//备注
        sheet.setColumnWidth(12, 18 * 250);//PV
        sheet.setColumnWidth(13, 18 * 250);//UV
        sheet.setColumnWidth(14, 18 * 250);//注册
        sheet.setColumnWidth(15, 18 * 250);//试算
        sheet.setColumnWidth(16, 18 * 250);//提交订单数
        sheet.setColumnWidth(17, 18 * 250);//提交订单总额
        sheet.setColumnWidth(18, 18 * 250);//支付订单数
        sheet.setColumnWidth(19, 18 * 250);//支付订单总额
        sheet.setColumnWidth(20, 18 * 350);//不包含车船税总额
        return new String[]{
                "ID", "商务活动名字", "合作商", "合作方式", "佣金",
                "城市", "预算", "活动开始时间", "活动结束时间", "落地页", "数据更新时间", "备注",
                "PV", "UV", "注册", "试算", "提交订单数", "提交订单总额", "支付订单数", "支付订单总额", "不包含车船税总额"
        };
    }

    @Override
    public HSSFWorkbook createHourExportExcel(Long activityId) {
        BusinessActivity businessActivity = businessActivityRepository.findOne(activityId);
        BusinessActivityViewModel viewModel = createViewData(businessActivity, 4);

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("监控数据时段结果");

        HSSFFont font = ExcelUtil.createFont(workbook, "宋体", (short) 12);
        HSSFCellStyle cellStyle = ExcelUtil.createCellStyle(workbook, font);

        HSSFFont fontTitle = ExcelUtil.createFont(workbook, "宋体", (short) 14);
        HSSFCellStyle cellStyleTitle = ExcelUtil.createCellStyle(workbook, fontTitle);

        // 商务活动基本信息
        ExcelUtil.createStrCellValues(sheet, 0, supplyHourBasicExcelTitle(sheet), cellStyleTitle);
        ExcelUtil.createStrCellValues(sheet, 1, supplyHourBasicExcelContent(viewModel), cellStyle);

        // 商务活动监控数据信息
        List<CustomerField> customerFieldList = customerFieldRepository.findByBusinessActivityOrderById(businessActivity);
        ExcelUtil.createStrCellValues(sheet, 2, supplyHourListExcelTitle(sheet, customerFieldList), cellStyleTitle);
        Integer index = 3;
        List<ActivityMonitorDataViewModel> monitorDataList = viewModel.getMonitorDataList();
        if (!CollectionUtils.isEmpty(monitorDataList)) {
            for (ActivityMonitorDataViewModel data : monitorDataList) {
                ExcelUtil.createStrCellValues(sheet, index, supplyHourListExcelContent(data, customerFieldList), cellStyle);
                index++;
            }
        }

        return workbook;
    }

    private String[] supplyHourListExcelContent(ActivityMonitorDataViewModel data, List<CustomerField> customerFieldList) {
        String monitorTime = data.getMonitorTime();
        DecimalFormat format = new DecimalFormat("#.##");
        String[] times = monitorTime.split(" ");
        List<String> valueList = new ArrayList<>();
        valueList.add(times[0]);//日期
        valueList.add(times[1]);//时间
        valueList.add(data.getPv() == null ? "0" : data.getPv() + "");//PV
        valueList.add(data.getUv() == null ? "0" : data.getUv() + "");//UV
        valueList.add(data.getRegister() == null ? "0" : data.getRegister() + "");//注册
        valueList.add(data.getQuote() == null ? "0" : data.getQuote() + "");//试算
        valueList.add(data.getSubmitCount() == null ? "0" : data.getSubmitCount() + "");//提交订单数
        valueList.add(format.format(DoubleUtils.doubleValue(data.getSubmitAmount())));//提交订单总额
        valueList.add(data.getPaymentCount() == null ? "0" : data.getPaymentCount() + "");//支付订单数
        valueList.add(format.format(DoubleUtils.doubleValue(data.getPaymentAmount())));//支付订单总额
        valueList.add(format.format(DoubleUtils.doubleValue(data.getNoAutoTaxAmount())));//不包含车船税总额
        valueList.add(data.getSpecialMonitor() == null ? "0" : data.getSpecialMonitor() + "");//特殊监控
        if (!CollectionUtils.isEmpty(customerFieldList)) {
            for (int i = 0; i < customerFieldList.size(); i++) {
                try {
                    Method method = data.getClass().getMethod("getCustomerField" + (i + 1));
                    valueList.add(format.format(DoubleUtils.doubleValue((Double) method.invoke(data))));//自定义字段
                } catch (Exception ex) {
                    logger.error("reflect to get customer field value error", ex);
                }
            }
        }
        String[] values = new String[valueList.size()];
        return valueList.toArray(values);
    }

    private String[] supplyHourListExcelTitle(HSSFSheet sheet, List<CustomerField> customerFieldList) {
        sheet.setColumnWidth(0, 18 * 180);//日期
        sheet.setColumnWidth(1, 18 * 180);//时间
        sheet.setColumnWidth(2, 18 * 250);//PV
        sheet.setColumnWidth(3, 18 * 250);//UV
        sheet.setColumnWidth(4, 18 * 250);//注册
        sheet.setColumnWidth(5, 18 * 250);//试算
        sheet.setColumnWidth(6, 18 * 250);//提交订单数
        sheet.setColumnWidth(7, 18 * 250);//提交订单总额
        sheet.setColumnWidth(8, 18 * 250);//支付订单数
        sheet.setColumnWidth(9, 18 * 250);//支付订单总额
        sheet.setColumnWidth(10, 18 * 350);//支付订单总额
        sheet.setColumnWidth(11, 18 * 250);//特殊监控
        if (!CollectionUtils.isEmpty(customerFieldList)) {
            for (int i = 0; i < customerFieldList.size(); i++) {
                sheet.setColumnWidth(12 + i, 18 * 250);//自定义字段
            }
        }

        List<String> titleList = new ArrayList<>();
        titleList.add("日期");
        titleList.add("时间");
        titleList.add("PV");
        titleList.add("UV");
        titleList.add("注册");
        titleList.add("试算");
        titleList.add("提交订单数");
        titleList.add("提交订单总额");
        titleList.add("支付订单数");
        titleList.add("支付订单总额");
        titleList.add("不包含车船税总额");
        titleList.add("特殊监控");
        if (!CollectionUtils.isEmpty(customerFieldList)) {
            customerFieldList.forEach(customerField -> {
                titleList.add(customerField.getName());
            });
        }
        String[] titles = new String[titleList.size()];
        return titleList.toArray(titles);
    }

    private String[] supplyHourBasicExcelContent(BusinessActivityViewModel data) {
        DecimalFormat format = new DecimalFormat("#.##");
        return new String[]{
                data.getId() + "",                   //ID
                data.getName(),                      //商务活动名字
                data.getPartnerName(),               //合作商
                data.getCooperationModeName(),       //合作方式
                format.format(DoubleUtils.displayDoubleValue(data.getRebate())),//佣金
                data.getCity() + "",                 //城市
                format.format(DoubleUtils.doubleValue(data.getBudget())),//预算
                data.getStartTime(),                 //活动开始时间
                data.getEndTime(),                   //活动结束时间
                data.getLandingPage(),               //落地页
                data.getComment(),                   //备注
                data.getRefreshTime()                //数据更新时间
        };
    }

    private String[] supplyHourBasicExcelTitle(HSSFSheet sheet) {
        sheet.setColumnWidth(0, 18 * 20);//ID
        sheet.setColumnWidth(1, 18 * 600);//商务活动名字
        sheet.setColumnWidth(2, 18 * 400);//合作商
        sheet.setColumnWidth(3, 18 * 180);//合作方式
        sheet.setColumnWidth(4, 18 * 250);//佣金
        sheet.setColumnWidth(5, 18 * 250);//城市
        sheet.setColumnWidth(6, 18 * 250);//预算
        sheet.setColumnWidth(7, 18 * 450);//活动开始时间
        sheet.setColumnWidth(8, 18 * 450);//活动结束时间
        sheet.setColumnWidth(9, 18 * 200);//落地页
        sheet.setColumnWidth(10, 18 * 400);//备注
        sheet.setColumnWidth(11, 18 * 400);//数据更新时间
        return new String[]{
                "ID", "商务活动名字", "合作商", "合作方式", "佣金",
                "城市", "预算", "活动开始", "活动结束", "落地页", "备注", "数据更新时间"
        };
    }

    @Override
    public BusinessActivityViewModel getCityMonitorData(Long activityId, Long areaId) {
        BusinessActivity businessActivity = businessActivityRepository.findOne(activityId);
        BusinessActivityViewModel viewModel = new BusinessActivityViewModel();

        // 获取指定城市的按天计算的最新的监控数据
        getCountryMonitorData(businessActivity, viewModel, 1, areaId);
        // 自定义字段
        setCustomerField(businessActivity, viewModel);
        return viewModel;
    }

    @Override
    public ResultModel checkBusinessActivityData(String code, Long marketingId, String startTimeStr) {
        ResultModel resultModel = new ResultModel();
        // 验证商务活动编号的唯一性
        if (code != null && !"".equals(code)) {
            Integer businessActivityCount = businessActivityRepository.countByCode(code.toLowerCase());
            if (businessActivityCount != null && businessActivityCount > 0) {
                resultModel.setPass(false);
                resultModel.setMessage("该商务活动编号已使用，请重新填写！");
                return resultModel;
            }
        }
        // 验证商务活动的开始时间在推广活动的开始时间范围内
        if (marketingId != null) {
            Marketing marketing = marketingRepository.findOne(marketingId);
            Date startTime = DateUtils.getDate(startTimeStr, "yyyy-MM-dd HH:mm");
            if (startTime.before(marketing.getBeginDate()) || startTime.after(marketing.getEndDate())) {
                resultModel.setPass(false);
                resultModel.setMessage("开始时间必须在推广活动时间范围内！");
                return resultModel;
            }
        }
        return resultModel;
    }

    @Override
    public String getLandingPage(String urlType) {
        StringBuffer landingPage = new StringBuffer();
        switch (urlType) {
            case BusinessActivityConstants.LANDING_PAGE_M_HOME:
                landingPage.append(WebConstants.getDomainURL());
                landingPage.append("/m/channel/");
                landingPage.append(BusinessActivityConstants.LANDING_PAGE_KEYWORD);
                break;
            case BusinessActivityConstants.LANDING_PAGE_M_PAYMENT:
                landingPage.append(WebConstants.getDomainURL());
                landingPage.append("/m/channel/");
                landingPage.append(BusinessActivityConstants.LANDING_PAGE_KEYWORD);
                landingPage.append("#base");
                break;
            case BusinessActivityConstants.LANDING_PAGE_WEB_HOME:
                landingPage.append(WebConstants.getDomainURL());
                landingPage.append("/web/channel/");
                landingPage.append(BusinessActivityConstants.LANDING_PAGE_KEYWORD);
                break;
        }
        return landingPage.toString();
    }

    @Override
    public Map<String, String> getMarketingData(Long marketingId) {
        Marketing marketing = marketingRepository.findOne(marketingId);
        StringBuffer landingPage = new StringBuffer();
        landingPage.append(WebConstants.getDomainURL());
        landingPage.append("/marketing/m/");
        landingPage.append(marketing.getCode());
        landingPage.append("/index.html?cps=");
        landingPage.append(BusinessActivityConstants.LANDING_PAGE_KEYWORD);
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("landingPage", landingPage.toString());
        dataMap.put("startTime", DateUtils.getDateString(marketing.getBeginDate(), "yyyy-MM-dd HH:mm"));
        dataMap.put("endTime", DateUtils.getDateString(marketing.getEndDate(), "yyyy-MM-dd HH:mm"));
        return dataMap;
    }

}
