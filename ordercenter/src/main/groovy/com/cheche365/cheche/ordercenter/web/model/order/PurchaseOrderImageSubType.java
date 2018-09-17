package com.cheche365.cheche.ordercenter.web.model.order;

/**
 * Created by yinJianBin on 2016/8/24.
 */

import com.cheche365.cheche.core.model.PurchaseOrderImage;
import com.cheche365.cheche.core.model.PurchaseOrderImageType;

/**
 * 子类型
 */
public class PurchaseOrderImageSubType {

    private PurchaseOrderImageType subImageType;
    private PurchaseOrderImage imageInfo;
    private Boolean checkedFlag;

    public PurchaseOrderImageType getPurchaseOrderImageType() {
        return subImageType;
    }

    public void setPurchaseOrderImageType(PurchaseOrderImageType purchaseOrderImageType) {
        this.subImageType = purchaseOrderImageType;
    }

    public PurchaseOrderImage getPurchaseOrderImage() {
        return imageInfo;
    }

    public void setPurchaseOrderImage(PurchaseOrderImage purchaseOrderImage) {
        this.imageInfo = purchaseOrderImage;
    }

    public PurchaseOrderImageType getSubImageType() {
        return subImageType;
    }

    public void setSubImageType(PurchaseOrderImageType subImageType) {
        this.subImageType = subImageType;
    }

    public PurchaseOrderImage getImageInfo() {
        return imageInfo;
    }

    public void setImageInfo(PurchaseOrderImage imageInfo) {
        this.imageInfo = imageInfo;
    }

    public Boolean getCheckedFlag() {
        return checkedFlag;
    }

    public void setCheckedFlag(Boolean checkedFlag) {
        this.checkedFlag = checkedFlag;
    }
}
