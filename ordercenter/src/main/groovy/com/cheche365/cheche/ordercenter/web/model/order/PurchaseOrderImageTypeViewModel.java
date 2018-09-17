package com.cheche365.cheche.ordercenter.web.model.order;

import com.cheche365.cheche.core.model.PurchaseOrderImage;
import com.cheche365.cheche.core.model.PurchaseOrderImageType;

import java.util.List;


public class PurchaseOrderImageTypeViewModel {

    private Long purchaseOrderId;

    private PurchaseOrderImageType purchaseOrderImageType;

    private List<PurchaseOrderImageSubType> subTypeList;

    public Long getPurchaseOrderId() {
        return purchaseOrderId;
    }

    private Boolean parentCheckedFlag;//大类型是否被选中


    public void setPurchaseOrderId(Long purchaseOrderId) {
        this.purchaseOrderId = purchaseOrderId;
    }

    public PurchaseOrderImageType getPurchaseOrderImageType() {
        return purchaseOrderImageType;
    }

    public void setPurchaseOrderImageType(PurchaseOrderImageType purchaseOrderImageType) {
        this.purchaseOrderImageType = purchaseOrderImageType;
    }

    public List<PurchaseOrderImageSubType> getSubTypeList() {
        return subTypeList;
    }

    public void setSubTypeList(List<PurchaseOrderImageSubType> subTypeList) {
        this.subTypeList = subTypeList;
    }

    public Boolean getParentCheckedFlag() {
        return parentCheckedFlag;
    }

    public void setParentCheckedFlag(Boolean parentCheckedFlag) {
        this.parentCheckedFlag = parentCheckedFlag;
    }
}
