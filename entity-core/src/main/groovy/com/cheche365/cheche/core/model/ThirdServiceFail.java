package com.cheche365.cheche.core.model;

import javax.persistence.*;

/**
 * Created by chenxiaozhe on 16-1-15.
 */
@Entity
public class ThirdServiceFail extends DescribableEntity {
    private Long orderId;
    private Long companyId;
    private String message;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "TEXT", nullable = true)
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }
}
