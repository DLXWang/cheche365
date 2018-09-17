package com.cheche365.cheche.ordercenter.service.telMarketingCenter

import com.cheche365.cheche.common.util.DateUtils
import com.cheche365.cheche.core.model.Auto
import com.cheche365.cheche.core.model.CompulsoryInsurance
import com.cheche365.cheche.core.model.Insurance
import com.cheche365.cheche.core.model.MarketingSuccess
import com.cheche365.cheche.core.model.OrderStatus
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.QuotePhoto
import com.cheche365.cheche.core.model.User
import com.cheche365.cheche.core.model.UserAuto
import com.cheche365.cheche.core.model.VehicleContact
import com.cheche365.cheche.core.repository.CompulsoryInsuranceRepository
import com.cheche365.cheche.core.repository.InsuranceRepository
import com.cheche365.cheche.core.repository.MarketingSuccessRepository
import com.cheche365.cheche.core.repository.PurchaseOrderRepository
import com.cheche365.cheche.core.repository.QuotePhotoRepository
import com.cheche365.cheche.core.repository.UserAutoRepository
import com.cheche365.cheche.core.repository.VehicleContactRepository
import com.cheche365.cheche.core.service.AutoService
import com.cheche365.cheche.core.service.CompulsoryInsuranceService
import com.cheche365.cheche.core.service.InsuranceService
import com.cheche365.cheche.core.service.UserService
import com.cheche365.cheche.core.model.TelMarketingCenterSource
import com.cheche365.cheche.manage.common.service.BaseService
import com.cheche365.cheche.ordercenter.web.model.auto.AutoViewModel
import org.apache.commons.collections.CollectionUtils
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Service

/**
 * 电销查询车辆相关信息
 * Created by zhangtc on 2018/3/15.
 */
@Service
class TelMarketingCenterAutoService extends BaseService {

    private Logger logger = LoggerFactory.getLogger(this.getClass())

    @Autowired
    MongoTemplate mongoTemplate
    @Autowired
    InsuranceService insuranceService
    @Autowired
    CompulsoryInsuranceService compulsoryInsuranceService
    @Autowired
    AutoService autoService
    @Autowired
    UserService userService
    @Autowired
    MarketingSuccessRepository marketingSuccessRepository
    @Autowired
    VehicleContactRepository vehicleContactRepository
    @Autowired
    QuotePhotoRepository quotePhotoRepository
    @Autowired
    InsuranceRepository insuranceRepository
    @Autowired
    CompulsoryInsuranceRepository compulsoryInsuranceRepository
    @Autowired
    PurchaseOrderRepository purchaseOrderRepository
    @Autowired
    UserAutoRepository userAutoRepository

    protected List<AutoViewModel> getAutoInfo(Long sourceId, User user, String mobile) {
        logger.debug("开始获取车辆信息!{}", sourceId)
        List<AutoViewModel> autoInfoList = new ArrayList<>()
        if (TelMarketingCenterSource.Enum.fromMarketing(sourceId)) {
            List<MarketingSuccess> marketingSuccessList = marketingSuccessRepository.findByMarketingCodeAndMobile(TelMarketingCenterSource.Enum.getMarketingId(sourceId), mobile)
            for (MarketingSuccess marketingSuccess : marketingSuccessList) {
                if (marketingSuccess != null && marketingSuccess.getLicensePlateNo() != null) {
                    autoInfoList.add(AutoViewModel.createViewModel(marketingSuccess, autoService))
                }
            }
        } else if (sourceId == TelMarketingCenterSource.Enum.ARTIFICIAL_IMPORT.getId()) {
            List<VehicleContact> vehicleContactList = vehicleContactRepository.findByMobile(mobile)
            vehicleContactList.forEach() { vehicleContact -> autoInfoList.add(addExpireDate(AutoViewModel.createViewModel(vehicleContact), null)) }
        } else if (sourceId == TelMarketingCenterSource.Enum.PHOTO_APPOINTMENT.getId()
            || sourceId == TelMarketingCenterSource.Enum.ALIPAY_REFUELING_SERVICE_PHOTO_APPOINTMENT.getId()
            || sourceId == TelMarketingCenterSource.Enum.BAIDU_PHOTO_APPOINTMENT.getId()
            || sourceId == TelMarketingCenterSource.Enum.TUHU_PHOTO_APPOINTMENT.getId()
            || sourceId == TelMarketingCenterSource.Enum.CHE_XIANG_PHOTO_APPOINTMENT.getId()) {
            List<QuotePhoto> quotePhotoList = quotePhotoRepository.findByUser(user)
            quotePhotoList.forEach() { quotePhoto -> autoInfoList.add(addExpireDate(AutoViewModel.createViewModel(quotePhoto), null)) }
        } else if (sourceId == TelMarketingCenterSource.Enum.INSURANCE_EXPIRE_DATE.getId()
            || sourceId == TelMarketingCenterSource.Enum.COMPULSORY_INSURANCE_EXPIRE_DATE.getId()) {
            Date startDate = DateUtils.getDate(Calendar.getInstance().getTime(), DateUtils.DATE_LONGTIME24_START_PATTERN)
            Date endDate = DateUtils.getDate(DateUtils.calculateDateByDay(startDate, 90), DateUtils.DATE_LONGTIME24_END_PATTERN)
            List<Auto> autoList = null
            if (sourceId == TelMarketingCenterSource.Enum.INSURANCE_EXPIRE_DATE.getId()) {
                autoList = this.insuranceRepository.findAutoByUserAndExpireDate(user, startDate, endDate)
            } else {
                autoList = this.compulsoryInsuranceRepository.findAutoByUserAndExpireDate(user, startDate, endDate)
            }
            for (Auto auto : autoList) {
                if (!auto.isDisable()) {
                    autoInfoList.add(addExpireDate(AutoViewModel.createViewModel(auto), auto))
                }
            }
        } else if (sourceId == TelMarketingCenterSource.Enum.ORDERS_UNPAY.getId()) {
            if (user == null) {
                user = userService.getUserByMobile(mobile)
            }
            List<PurchaseOrder> purchaseOrderList = purchaseOrderRepository.findEffectiveOrdersByStatusAndApplicant(Arrays.asList(OrderStatus.Enum.PENDING_PAYMENT_1.getId().toString()), user.getId())
            for (PurchaseOrder purchaseOrder : purchaseOrderList) {
                Auto auto = purchaseOrder.getAuto()
                if (auto != null && !auto.isDisable()) {
                    autoInfoList.add(addExpireDate(AutoViewModel.createViewModel(auto), auto))
                }
            }
        } else if (sourceId == TelMarketingCenterSource.Enum.ORDERS_LAST_YEAR_UNPAY.getId()) {
            logger.debug("上年未成单订单{}开始获取车辆信息!", sourceId)
            if (user == null) {
                user = userService.getUserByMobile(mobile)
            }
            List<PurchaseOrder> purchaseOrderList = purchaseOrderRepository.findUnOrdersByStatusAndApplicant(Arrays.asList(OrderStatus.Enum.CANCELED_6.getId().toString(),
                OrderStatus.Enum.INSURE_FAILURE_7.getId().toString(), OrderStatus.Enum.REFUNDED_9.getId().toString()), user.getId())
            logger.debug("上年未成单订单{}车辆信息条目为{}", sourceId, CollectionUtils.isEmpty(purchaseOrderList) ? 0 : purchaseOrderList.size())
            for (PurchaseOrder purchaseOrder : purchaseOrderList) {
                Auto auto = purchaseOrder.getAuto()
                if (auto != null && !auto.isDisable()) {
                    autoInfoList.add(addExpireDate(AutoViewModel.createViewModel(auto), auto))
                }
            }
        }
        if (autoInfoList.size() == 0) {
            user = userService.getBindingUser(mobile)
            if (user != null) {
                List<UserAuto> userAutoList = userAutoRepository.findByUser(user)
                for (UserAuto userAuto : userAutoList) {
                    Auto auto = userAuto.getAuto()
                    if (!auto.isDisable()) {
                        autoInfoList.add(addExpireDate(AutoViewModel.createViewModel(auto), auto))
                    }
                }
            }
        }
        return autoInfoList
    }

    private AutoViewModel addExpireDateByAuto(Auto auto, AutoViewModel viewModel) {
        if (auto != null && viewModel != null) {
            Insurance insurance = insuranceService.findByAuto(auto)
            if (insurance != null) {
                viewModel.setCommercialExpireDate(insurance.getExpireDate() == null ?
                    "" : DateUtils.getDateString(insurance.getExpireDate(),
                    DateUtils.DATE_SHORTDATE_PATTERN))//保险到期日
            } else {
                //交强险
                CompulsoryInsurance compulsoryInsurance = compulsoryInsuranceService.findByAuto(auto)
                if (compulsoryInsurance != null) {
                    viewModel.setCompulsoryExpireDate(compulsoryInsurance.getExpireDate() == null ?
                        "" : DateUtils.getDateString(compulsoryInsurance.getExpireDate(),
                        DateUtils.DATE_SHORTDATE_PATTERN))//保险到期日
                }
            }
        }
        viewModel
    }

    private AutoViewModel addExpireDateByBihu(String licenseNo, AutoViewModel viewModel) {
        if (viewModel != null && StringUtils.isNotBlank(licenseNo)) {
            Map map = getBihuByLicenseNo(licenseNo)
            if (map != null && map.size() > 0) {
                viewModel.setSeats(String.valueOf(map.mongoSeats))
                viewModel.setCommercialExpireDate(String.valueOf(map.mongoCommercialEndDate))
                viewModel.setCompulsoryExpireDate(String.valueOf(map.mongoCompulsoryEndDate))
            }
        }
        viewModel
    }

    private AutoViewModel addExpireDate(AutoViewModel viewModel, Auto auto) {
        String licenseNo = viewModel.getLicensePlateNo()
        if (viewModel != null && StringUtils.isNotBlank(licenseNo)) {
            viewModel = addExpireDateByBihu(licenseNo, viewModel)
            if (StringUtils.isBlank(viewModel.commercialExpireDate) || StringUtils.isBlank(viewModel.compulsoryExpireDate)) {
                auto == null ? autoService.getAutoByLicenseExact(licenseNo) : auto
                viewModel = addExpireDateByAuto(auto, viewModel)
            }
        }
        viewModel
    }


    private Map getBihuByLicenseNo(licenseNo) {
        def mongoInfo = mongoTemplate.findOne(Query.query(Criteria.where('UserInfo.LicenseNo').is(licenseNo)), Map, 'bihu_insurance_info')
        if (mongoInfo != null) {
            def userInfo = mongoInfo?.UserInfo
            def result = [
                mongoSeats            : userInfo.SeatCount,
                mongoCompulsoryEndDate: userInfo.ForceExpireDate,
                mongoCommercialEndDate: userInfo.BusinessExpireDate
            ]
            return result
        } else {
            return [:]
        }
    }
}
