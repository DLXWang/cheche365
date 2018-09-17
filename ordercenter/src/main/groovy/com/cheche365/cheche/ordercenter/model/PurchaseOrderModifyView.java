package com.cheche365.cheche.ordercenter.model;

import com.cheche365.cheche.core.model.Address;
import com.cheche365.cheche.core.model.PurchaseOrder;

/**
 * Created by wangshaobin on 2016/12/13.
 */
public class PurchaseOrderModifyView {
    private Long orderId;//订单Id
    private Long originalAddressId;//原送单地址ID
    private Address newAddress;//新送单地址

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOriginalAddressId(Long originalAddressId) {
        this.originalAddressId = originalAddressId;
    }

    public void setNewAddress(Address newAddress) {
        this.newAddress = newAddress;
    }

    public Long getOriginalAddressId() {
        return originalAddressId;
    }

    public Address getNewAddress() {
        return newAddress;
    }
}
