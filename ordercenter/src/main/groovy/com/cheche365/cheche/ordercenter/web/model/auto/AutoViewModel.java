package com.cheche365.cheche.ordercenter.web.model.auto;


import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.Auto;
import com.cheche365.cheche.core.model.MarketingSuccess;
import com.cheche365.cheche.core.model.QuotePhoto;
import com.cheche365.cheche.core.model.VehicleContact;
import com.cheche365.cheche.core.service.AutoService;
import com.cheche365.cheche.ordercenter.web.model.user.UserViewModel;

import java.util.List;

/**
 * Created by guoweifu on 2015/9/7.
 */
public class AutoViewModel {

    private Long id;

    private String owner;//车主姓名
    private String identityType;//证件类型,1.身份证,2.护照,3.军官证
    private String identity;//证件id

    private String licensePlateNo; //车牌号
    private String vinNo;//车架号
    private String engineNo; //发动机号
    private String enrollDate;//初登日期
    private String model;     //车型
    private String brandCode;//品牌型号
    private String expireDate;//保险到期日
    private String seats;//座位数
    private String compulsoryExpireDate;//交强险到期日
    private String commercialExpireDate;//商业险到期日


    private boolean disable;

    private List<UserViewModel> userViewModels;//用户信息

    public List<UserViewModel> getUserViewModels() {
        return userViewModels;
    }

    public void setUserViewModels(List<UserViewModel> userViewModels) {
        this.userViewModels = userViewModels;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getIdentityType() {
        return identityType;
    }

    public void setIdentityType(String identityType) {
        this.identityType = identityType;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getLicensePlateNo() {
        return licensePlateNo;
    }

    public void setLicensePlateNo(String licensePlateNo) {
        this.licensePlateNo = licensePlateNo;
    }

    public String getVinNo() {
        return vinNo;
    }

    public void setVinNo(String vinNo) {
        this.vinNo = vinNo;
    }

    public String getEngineNo() {
        return engineNo;
    }

    public void setEngineNo(String engineNo) {
        this.engineNo = engineNo;
    }

    public String getEnrollDate() {
        return enrollDate;
    }

    public void setEnrollDate(String enrollDate) {
        this.enrollDate = enrollDate;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getBrandCode() {
        return brandCode;
    }

    public void setBrandCode(String brandCode) {
        this.brandCode = brandCode;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public boolean isDisable() {
        return disable;
    }

    public void setDisable(boolean disable) {
        this.disable = disable;
    }

    public String getSeats() {
        return seats;
    }

    public void setSeats(String seats) {
        this.seats = seats;
    }

    public String getCompulsoryExpireDate() {
        return compulsoryExpireDate;
    }

    public void setCompulsoryExpireDate(String compulsoryExpireDate) {
        this.compulsoryExpireDate = compulsoryExpireDate;
    }

    public String getCommercialExpireDate() {
        return commercialExpireDate;
    }

    public void setCommercialExpireDate(String commercialExpireDate) {
        this.commercialExpireDate = commercialExpireDate;
    }

    /**
     * QuotePhoto实体类转展示类
     *
     * @param quotePhoto
     * @return
     */
    public static AutoViewModel createViewModel(QuotePhoto quotePhoto) {
        if (quotePhoto == null)
            return null;
        AutoViewModel autoViewModel = new AutoViewModel();
        autoViewModel.setId(quotePhoto.getId());
        autoViewModel.setLicensePlateNo(quotePhoto.getLicensePlateNo());
        autoViewModel.setEnrollDate(DateUtils.getDateString(quotePhoto.getEnrollDate(), DateUtils.DATE_LONGTIME24_PATTERN));
        autoViewModel.setOwner(quotePhoto.getOwner());
        autoViewModel.setIdentity(quotePhoto.getIdentity());
        autoViewModel.setEngineNo(quotePhoto.getEngineNo());
        autoViewModel.setVinNo(quotePhoto.getVinNo());
        if (quotePhoto.getModel() != null) {
            autoViewModel.setBrandCode(quotePhoto.getCode());
        }
        return autoViewModel;
    }

    /**
     * auto实体类转展示类
     *
     * @param auto
     * @return
     */
    public static AutoViewModel createViewModel(Auto auto) {
        if (auto == null)
            return null;
        AutoViewModel autoViewModel = new AutoViewModel();
        autoViewModel.setId(auto.getId());
        autoViewModel.setLicensePlateNo(auto.getLicensePlateNo());
        autoViewModel.setEnrollDate(DateUtils.getDateString(auto.getEnrollDate(), DateUtils.DATE_LONGTIME24_PATTERN));
        autoViewModel.setOwner(auto.getOwner());
        autoViewModel.setIdentity(auto.getIdentity());
        autoViewModel.setEngineNo(auto.getEngineNo());
        autoViewModel.setVinNo(auto.getVinNo());
        autoViewModel.setSeats(String.valueOf(auto.getSeats()));
        if (auto.getAutoType() != null) {
            autoViewModel.setBrandCode(auto.getAutoType().getCode());
        }
        return autoViewModel;
    }

    /**
     * VehicleLicense实体类转展示类
     *
     * @param vehicleContact
     * @return
     */
    public static AutoViewModel createViewModel(VehicleContact vehicleContact) {
        if (vehicleContact == null)
            return null;
        AutoViewModel autoViewModel = new AutoViewModel();
        autoViewModel.setId(vehicleContact.getVehicleLicense().getId());
        autoViewModel.setLicensePlateNo(vehicleContact.getVehicleLicense().getLicensePlateNo());
        autoViewModel.setEnrollDate(DateUtils.getDateString(vehicleContact.getVehicleLicense().getEnrollDate(), DateUtils.DATE_LONGTIME24_PATTERN));
        autoViewModel.setOwner(vehicleContact.getVehicleLicense().getOwner());
        autoViewModel.setIdentity(vehicleContact.getVehicleLicense().getIdentity());
        autoViewModel.setEngineNo(vehicleContact.getVehicleLicense().getEngineNo());
        autoViewModel.setVinNo(vehicleContact.getVehicleLicense().getVinNo());
        autoViewModel.setSeats(String.valueOf(vehicleContact.getVehicleLicense().getSeats()));
        return autoViewModel;
    }

    public static AutoViewModel createViewModel(MarketingSuccess marketingSuccess, AutoService autoService) {
        if (marketingSuccess == null || marketingSuccess.getLicensePlateNo() == null)
            return null;
        AutoViewModel autoViewModel = new AutoViewModel();
        List<Auto> autoList = autoService.getAutoByLicenseExact(marketingSuccess.getLicensePlateNo());
        if (autoList != null && autoList.size() > 0) {
            autoViewModel = createViewModel(autoList.get(0));
            autoViewModel.setOwner(marketingSuccess.getOwner());
            autoViewModel.setIdentity(null);
        } else {
            autoViewModel.setId(Double.valueOf((Math.random() * 9 + 1) * 1000000).longValue());
            autoViewModel.setLicensePlateNo(marketingSuccess.getLicensePlateNo());
            autoViewModel.setOwner(marketingSuccess.getOwner());
        }
        return autoViewModel;
    }
}
