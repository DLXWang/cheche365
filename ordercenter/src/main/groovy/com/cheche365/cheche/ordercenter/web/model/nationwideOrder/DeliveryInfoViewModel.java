package com.cheche365.cheche.ordercenter.web.model.nationwideOrder;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.DeliveryInfo;
import com.cheche365.cheche.core.model.InternalUser;
import com.cheche365.cheche.core.util.BeanUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * Created by sunhuazhong on 2015/11/21.
 */
public class DeliveryInfoViewModel {
    private Long id;
    private String expressCompany;//快递公司
    private String trackingNo;//快递单号
    private String deliveryMan;//送货员
    private String mobile;//送货员手机号
    private String deliveryTime;//送货时间
    private String createTime;//创建时间
    private String updateTime;//修改时间
    private String operatorName;//操作人
    private String commercialPolicyNo;//商业险保单号
    private String compulsoryPolicyNo;//交强险保单号

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getExpressCompany() {
        return expressCompany;
    }

    public void setExpressCompany(String expressCompany) {
        this.expressCompany = expressCompany;
    }

    public String getTrackingNo() {
        return trackingNo;
    }

    public void setTrackingNo(String trackingNo) {
        this.trackingNo = trackingNo;
    }

    public String getDeliveryMan() {
        return deliveryMan;
    }

    public void setDeliveryMan(String deliveryMan) {
        this.deliveryMan = deliveryMan;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getCommercialPolicyNo() {
        return commercialPolicyNo;
    }

    public void setCommercialPolicyNo(String commercialPolicyNo) {
        this.commercialPolicyNo = commercialPolicyNo;
    }

    public String getCompulsoryPolicyNo() {
        return compulsoryPolicyNo;
    }

    public void setCompulsoryPolicyNo(String compulsoryPolicyNo) {
        this.compulsoryPolicyNo = compulsoryPolicyNo;
    }

    public static DeliveryInfoViewModel createViewModel(DeliveryInfo deliveryInfo) {
        if(deliveryInfo == null) {
            return null;
        }
        DeliveryInfoViewModel viewModel = new DeliveryInfoViewModel();
        viewModel.setId(deliveryInfo.getId());
        viewModel.setExpressCompany(deliveryInfo.getExpressCompany());
        viewModel.setTrackingNo(deliveryInfo.getTrackingNo());
        viewModel.setDeliveryMan(deliveryInfo.getDeliveryMan());
        viewModel.setMobile(deliveryInfo.getMobile());
        viewModel.setDeliveryTime(deliveryInfo.getDeliveryTime() == null ?
            "" : DateUtils.getDateString(deliveryInfo.getDeliveryTime(), DateUtils.DATE_LONGTIME24_PATTERN));
        viewModel.setCreateTime(DateUtils.getDateString(deliveryInfo.getCreateTime(), DateUtils.DATE_LONGTIME24_PATTERN));
        viewModel.setUpdateTime(DateUtils.getDateString(deliveryInfo.getUpdateTime(), DateUtils.DATE_LONGTIME24_PATTERN));
        viewModel.setOperatorName(deliveryInfo.getOperator() != null ?
            deliveryInfo.getOperator().getName() : "");
        return viewModel;
    }

    public static DeliveryInfo createModel(DeliveryInfoViewModel viewModel) {
        if (null == viewModel) {
            return null;
        }
        DeliveryInfo deliveryInfo = new DeliveryInfo();
        String[] contains = new String[]{"id", "expressCompany", "trackingNo", "deliveryMan", "mobile"};
        BeanUtil.copyPropertiesContain(viewModel, deliveryInfo, contains);
        deliveryInfo.setDeliveryTime(StringUtils.isNoneBlank(viewModel.getDeliveryTime()) ?
            DateUtils.getDate(viewModel.getDeliveryTime(), DateUtils.DATE_LONGTIME24_PATTERN) : null);
        return deliveryInfo;
    }
}
