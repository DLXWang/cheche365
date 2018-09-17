package com.cheche365.cheche.core.model;


import javax.persistence.*;
import java.util.*;

@Entity
public class PurchaseOrderAuditing {
    private Long id;
    private Date createTime;
    private PurchaseOrderImage purchaseOrderImage;//图片
    private Integer status=0;
    private String hint;



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(columnDefinition = "DATETIME")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @ManyToOne
    @JoinColumn(name = "purchase_order_image", foreignKey=@ForeignKey(name="FK_PURCHASE_ORDER_AUDITING_REF_PURCHASE_ORDER_IMAGE", foreignKeyDefinition="FOREIGN KEY (purchase_order_image) REFERENCES purchase_order_image(id)"))
    public PurchaseOrderImage getPurchaseOrderImage() {
        return purchaseOrderImage;
    }

    public void setPurchaseOrderImage(PurchaseOrderImage purchaseOrderImage) {
        this.purchaseOrderImage = purchaseOrderImage;
    }

    @Column(columnDefinition = "SMALLINT(2)")
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }


}
