package com.cheche365.cheche.ordercenter.web.controller.user;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.annotation.VisitorPermission;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.*;
import com.cheche365.cheche.core.service.*;
import com.cheche365.cheche.core.util.BeanUtil;
import com.cheche365.cheche.manage.common.exception.FieldValidtorException;
import com.cheche365.cheche.manage.common.model.TelMarketingCenter;
import com.cheche365.cheche.manage.common.repository.TelMarketingCenterRepository;
import com.cheche365.cheche.manage.common.service.BaseService;
import com.cheche365.cheche.manage.common.web.model.DataTablePageViewModel;
import com.cheche365.cheche.manage.common.web.model.PageInfo;
import com.cheche365.cheche.ordercenter.service.user.OuterUserService;
import com.cheche365.cheche.ordercenter.web.model.InsuranceCompanyData;
import com.cheche365.cheche.ordercenter.web.model.auto.AutoViewModel;
import com.cheche365.cheche.ordercenter.web.model.nationwideOrder.OrderProcessHistoryViewModel;
import com.cheche365.cheche.ordercenter.web.model.order.OrderOperationInfoViewModel;
import com.cheche365.cheche.ordercenter.web.model.user.UserViewModel;
import com.cheche365.cheche.core.model.WechatUserChannel;
import com.cheche365.cheche.core.repository.WechatUserChannelRepository;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.CIC_45000;

/**
 * Created by wangshaobin on 2017/3/30.
 */
@RestController
@RequestMapping("/orderCenter/outerUser")
public class OuterUserController {

    @Autowired
    private OuterUserService outerUserService;
    @Autowired
    private BaseService baseService;
    @Autowired
    private PurchaseOrderService purchaseOrderService;
    @Autowired
    private QuoteRecordRepository quoteRecordRepository;
    @Autowired
    private ResourceService resourceService;
    @Autowired
    private PurchaseOrderGiftService purchaseOrderGiftService;
    @Autowired
    private PurchaseOrderAmendService purchaseOrderAmendService;
    @Autowired
    private AutoRepository autoRepository;
    @Autowired
    private TelMarketingCenterRepository telMarketingCenterRepository;
    @Autowired
    private WechatUserChannelRepository wechatUserChannelRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserAutoRepository userAutoRepository;
    @Autowired
    private InsuranceRepository insuranceRepository;
    @Autowired
    private CompulsoryInsuranceRepository compulsoryInsuranceRepository;
    @Autowired
    QuoteFlowConfigRepository quoteFlowConfigRepository;
    @Autowired
    private QuoteConfigService quoteConfigService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @VisitorPermission("or0403")
    public DataTablePageViewModel findAll(@RequestParam(value = "currentPage", required = true) Integer currentPage,
                                          @RequestParam(value = "pageSize", required = true) Integer pageSize,
                                          @RequestParam(value = "keyword", required = false) String keyword,
                                          @RequestParam(value = "keyType", required = false) Integer keyType,
                                          @RequestParam(value = "draw", required = false) Integer draw) throws UnsupportedEncodingException {
        if (currentPage == null || currentPage < 1) {
            throw new FieldValidtorException("list user info, currentPage can not be null or less than 1");
        }

        if (pageSize == null || pageSize < 1) {
            throw new FieldValidtorException("list user info, pageSize can not be null or less than 1");
        }
        return outerUserService.findAllUserInfos(
            currentPage, pageSize, keyword, keyType, draw);
    }

    /**
     * 用户详情页数据初始化
     * **/
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public UserViewModel findUserManagementDetail(@RequestParam(value = "id", required = true) Long id) throws UnsupportedEncodingException {
        UserViewModel viewModel = new UserViewModel();
        //查询用户详情
        User user = userRepository.findOne(id);
        UserViewModel.createUserToModel(user, viewModel);
        //绑定第三方信息
        List<WechatUserChannel> channelList = wechatUserChannelRepository.findLatestByUser(id);
        if(CollectionUtils.isNotEmpty(channelList)){
            String bindingStr = StringUtils.isBlank(channelList.get(0).getOpenId()) ? "" : "微信：" + channelList.get(0).getOpenId();
            viewModel.setBinding(bindingStr);
        }
        //查询该用户的电销信息
        TelMarketingCenter center = telMarketingCenterRepository.findFirstByUser(user);
        if(center!=null)
            UserViewModel.createCenterToModel(center, viewModel);
        return viewModel;
    }

    /**
     * 翻页获取订单列表
     * **/
    @RequestMapping(value = "/findOrderInfoByUserId", method = RequestMethod.GET)
    public DataTablePageViewModel<OrderOperationInfoViewModel> findOrderInfoByUserId(@RequestParam(value = "id", required = true) Long id,
                                                                                     @RequestParam(value = "currentPage", required = true) Integer currentPage,
                                                                                     @RequestParam(value = "pageSize", required = true) Integer pageSize,
                                                                                     @RequestParam(value = "draw", required = false) Integer draw){
        Page<OrderOperationInfo> infoPage = outerUserService.findOrderInfoByUserId(id, currentPage, pageSize);
        PageInfo pageInfo = baseService.createPageInfo(infoPage);
        return new DataTablePageViewModel<>(pageInfo.getTotalElements(), pageInfo.getTotalElements(), draw, infoListToModels(infoPage.getContent()));
    }

    /**
     * 获取用户车辆列表
     * **/
    @RequestMapping(value = "/findAutoInfoByUserId", method = RequestMethod.GET)
    public DataTablePageViewModel<AutoViewModel> findAutoInfoByUserId(@RequestParam(value = "id", required = true) Long id,
                                                                                     @RequestParam(value = "currentPage", required = true) Integer currentPage,
                                                                                     @RequestParam(value = "pageSize", required = true) Integer pageSize,
                                                                                     @RequestParam(value = "draw", required = false) Integer draw){
        Page<Auto> autoPage = outerUserService.findAutoInfoByUserId(currentPage, pageSize, draw, id);
        PageInfo pageInfo = baseService.createPageInfo(autoPage);
        return new DataTablePageViewModel<>(pageInfo.getTotalElements(), pageInfo.getTotalElements(), draw, autoListToModels(autoPage.getContent()));
    }

    /**
     * 获取订单备注信息
     * **/
    @RequestMapping(value = "/purchaseOrder/{purchaseOrderId}")
    public Map<String, Object> getOrderHistories(@PathVariable Long purchaseOrderId) {
        Map<String, Object> historyMap = new HashMap<>();
        List<OrderProcessHistory> historyList = outerUserService.getOrderHistories(purchaseOrderId);
        List<OrderProcessHistoryViewModel> viewModelList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(historyList)) {
            historyList.forEach(orderProcessHistory -> viewModelList.add(OrderProcessHistoryViewModel.createViewModel(orderProcessHistory)));
        }
        historyMap.put("histories", viewModelList);
        return historyMap;
    }

    /**
     * 获取车辆详情
     *
     * @param autoId
     * @return
     */
    @RequestMapping(value = "/findUserAuto/{autoId}", method = RequestMethod.GET)
    public AutoViewModel findUserAuto(@PathVariable Long autoId) {
        if (autoId == null || autoId < 1) {
            throw new FieldValidtorException("find auto detail, id can not be null or less than 1");
        }
        Auto auto = autoRepository.findOne(autoId);
        return createViewModel(auto, true);
    }

    /**
     * 构建页面数据
     *
     * @param auto
     * @param isDetail 是否显示详情
     * @return PageViewData
     */
    private AutoViewModel createViewModel(Auto auto, boolean isDetail) {
        if (auto == null)
            return null;
        AutoViewModel viewModel = new AutoViewModel();
        viewModel.setId(auto.getId());
        viewModel.setLicensePlateNo(auto.getLicensePlateNo());//车牌号
        viewModel.setOwner(auto.getOwner());//车主
        /**
         * 用户信息
         */
        List<UserAuto> userAutoList = userAutoRepository.findByAuto(auto);
        if (userAutoList != null && userAutoList.size() > 0) {
            List<UserViewModel> userViewModelList = new ArrayList<>();
            List<Long> idList = new ArrayList<>();
            userAutoList.forEach(userAuto -> {
                if (userAuto != null && userAuto.getUser() != null
                    && StringUtils.isNotBlank(userAuto.getUser().getMobile())
                    && !idList.contains(userAuto.getUser().getId())) {
                    UserViewModel userViewModel = new UserViewModel();
                    userViewModel.setId(userAuto.getUser().getId());
                    userViewModel.setMobile(userAuto.getUser().getMobile());
                    userViewModelList.add(userViewModel);
                    idList.add(userViewModel.getId());
                }
            });
            viewModel.setUserViewModels(userViewModelList);
        }
        //设置详情页
        if (isDetail) {
            setDetail(auto, viewModel);
        }

        return viewModel;
    }

    private void setDetail(Auto auto, AutoViewModel viewModel) {
        viewModel.setIdentityType(auto.getIdentityType() == null ?
            "" : auto.getIdentityType().getName());//证件类型
        viewModel.setIdentity(auto.getIdentity());//证件号码
        viewModel.setVinNo(auto.getVinNo());//车架号
        viewModel.setEngineNo(auto.getEngineNo());//发动机号
        viewModel.setEnrollDate(auto.getEnrollDate() == null ?
            "" : DateUtils.getDateString(auto.getEnrollDate(), DateUtils.DATE_SHORTDATE_PATTERN));//初登日期
        if (auto.getAutoType() != null) {
            viewModel.setModel(StringUtils.trimToEmpty(auto.getAutoType().getModel()));//车型
            viewModel.setBrandCode(
                StringUtils.trimToEmpty(auto.getAutoType().getBrand())
                    + StringUtils.trimToEmpty(auto.getAutoType().getCode()));//品牌型号
        }
        //商业险
        Insurance insurance = insuranceRepository.findFirstByAutoIdOrderByIdDesc(auto.getId());
        if (insurance != null) {
            viewModel.setExpireDate(insurance.getExpireDate() == null ?
                "" : DateUtils.getDateString(insurance.getExpireDate(),
                DateUtils.DATE_SHORTDATE_PATTERN));//保险到期日
        } else {
            //交强险
            CompulsoryInsurance compulsoryInsurance = compulsoryInsuranceRepository.findFirstByAutoIdOrderByIdDesc(auto.getId());;
            if (compulsoryInsurance != null) {
                viewModel.setExpireDate(compulsoryInsurance.getExpireDate() == null ?
                    "" : DateUtils.getDateString(compulsoryInsurance.getExpireDate(),
                    DateUtils.DATE_SHORTDATE_PATTERN));//保险到期日
            }
        }
    }

    private OrderOperationInfoViewModel createInfoViewModel(OrderOperationInfo orderOperationInfo) {
        PurchaseOrder purchaseOrder = orderOperationInfo.getPurchaseOrder();
        Payment payment = purchaseOrderService.getPaymentByPurchaseOrder(purchaseOrder);
        if(payment != null && payment.getStatus() != null){
            payment.toDisplayText();
        }
        QuoteRecord quoteRecord = quoteRecordRepository.findOne(purchaseOrder.getObjId());;
        OrderOperationInfoViewModel viewModel = OrderOperationInfoViewModel.createViewModel(orderOperationInfo);
        InsuranceCompanyData companyData = new InsuranceCompanyData();
        companyData.setId(quoteRecord.getInsuranceCompany().getId());
        companyData.setName(quoteRecord.getInsuranceCompany().getName());
        viewModel.setInsuranceCompany(companyData);
        viewModel.setPaymentChannel(purchaseOrder.getChannel());
        viewModel.setPaymentStatus(null != payment ? payment.getStatus() : null);
        viewModel.setGift(purchaseOrderGiftService.getGiftDetail(purchaseOrder));
        viewModel.setCreateTime(DateUtils.getDateString(orderOperationInfo.getCreateTime(), DateUtils.DATE_LONGTIME24_PATTERN));
        viewModel.setUpdateTime(DateUtils.getDateString(orderOperationInfo.getUpdateTime(), DateUtils.DATE_LONGTIME24_PATTERN));
        viewModel.setAuto(createAutoViewModel(purchaseOrder.getAuto()));
        viewModel.setConfirmNo(orderOperationInfo.getConfirmNo());

        //中华联合分地区
        if (BeanUtil.equalsID(CIC_45000, quoteRecord.getInsuranceCompany())) {
            viewModel.setQuoteSource(quoteRecord.getType());
            viewModel.setInnerPay(quoteConfigService.isInnerPay(quoteRecord,purchaseOrder));
        }
        if(orderOperationInfo.getPurchaseOrder().getSourceChannel() != null){
            if(!StringUtils.isEmpty(orderOperationInfo.getPurchaseOrder().getSourceChannel().getIcon())){
                viewModel.setChannelIcon(resourceService.absoluteUrl(resourceService.getResourceAbsolutePath(resourceService.getProperties().getChannelPath()),
                    orderOperationInfo.getPurchaseOrder().getSourceChannel().getIcon()));
            }
        }
        if(orderOperationInfo.getCurrentStatus().getId().equals(OrderTransmissionStatus.Enum.ADDITION_PAID.getId())){
            Double paidPrice = purchaseOrderAmendService.getPaidAmountByOrderId(orderOperationInfo.getPurchaseOrder().getId());
            viewModel.setPaid(paidPrice.equals(new Double(0))?false:true);
        }
        Boolean isThirdPart = (orderOperationInfo.getPurchaseOrder().getSourceChannel() == null)?
            false:orderOperationInfo.getPurchaseOrder().getSourceChannel().isThirdPartnerChannel();
        viewModel.setThirdPart(isThirdPart);
        return viewModel;
    }

    private List<OrderOperationInfoViewModel> infoListToModels(List<OrderOperationInfo> infoList){
        List<OrderOperationInfoViewModel> list = new ArrayList<OrderOperationInfoViewModel>();
        infoList.forEach(info->list.add(createInfoViewModel(info)));
        return list;
    }

    private List<AutoViewModel> autoListToModels(List<Auto> autoList){
        List<AutoViewModel> autoViewList = new ArrayList<>();
        autoList.forEach(auto->autoViewList.add(createAutoViewModel(auto)));
        return autoViewList;
    }

    private AutoViewModel createAutoViewModel(Auto auto) {
        AutoViewModel viewModel = new AutoViewModel();
        viewModel.setId(auto.getId());
        viewModel.setLicensePlateNo(auto.getLicensePlateNo());//车牌号
        viewModel.setOwner(auto.getOwner());//车主
        return viewModel;
    }
}
