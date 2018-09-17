package com.cheche365.cheche.core.model;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xu.yelong on 2016/8/23.
 */
@Entity
public class PurchaseOrderImageStatus {
    private Long id;
    private PurchaseOrder purchaseOrder;
    private Integer status = 0;

    public static class STATUS {
        //未上传
        public static final Integer WAITING_UPLOAD = 0;
        //待审核
        public static final Integer WAITING_AUDIT = 1;
        //审核成功
        public static final Integer SUCCESS = 2;
        //审核失败
        public static final Integer FAIL = 3;


    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "purchase_order", foreignKey = @ForeignKey(name = "FK_IMAGE_STATUS_REF_PURCHASE_ORDER", foreignKeyDefinition = "FOREIGN KEY (purchase_order) REFERENCES purchase_order(id)"))
    public PurchaseOrder getPurchaseOrder() {
        return purchaseOrder;
    }

    public void setPurchaseOrder(PurchaseOrder purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

    @Column(columnDefinition = "SMALLINT(1)")
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
