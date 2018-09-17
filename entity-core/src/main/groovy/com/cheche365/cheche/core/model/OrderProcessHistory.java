package com.cheche365.cheche.core.model;

import com.cheche365.cheche.core.service.listener.EntityChangeListener;

import javax.persistence.*;
import java.util.Date;

/**
 * 订单处理历史表
 * Created by sunhuazhong on 2015/11/13.
 */
@Entity
//@EntityListeners({AuditingEntityListener.class})
@EntityListeners(EntityChangeListener.class)
public class OrderProcessHistory {
    private Long id;
    private PurchaseOrder purchaseOrder;//订单
    private Long originalStatus;//原出单状态，关联合作出单状态表或独立出单状态表
    private Long currentStatus;//新出单状态，关联合作出单状态表或独立出单状态表
    private OrderProcessType orderProcessType;//订单处理类型
    private String comment;//备注
    //    @CreatedDate
    private Date createTime;//创建时间
    //    @CreatedBy
    private InternalUser operator;//操作人

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "purchaseOrder", foreignKey=@ForeignKey(name="FK_ORDER_PROCESS_HISTORY_REF_PURCHASE_ORDER", foreignKeyDefinition="FOREIGN KEY (purchase_order) REFERENCES purchase_order(id)"))
    public PurchaseOrder getPurchaseOrder() {
        return purchaseOrder;
    }

    public void setPurchaseOrder(PurchaseOrder purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

    @Column(columnDefinition = "bigint(20)")
    public Long getOriginalStatus() {
        return originalStatus;
    }

    public void setOriginalStatus(Long originalStatus) {
        this.originalStatus = originalStatus;
    }

    @Column(columnDefinition = "bigint(20)")
    public Long getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(Long currentStatus) {
        this.currentStatus = currentStatus;
    }

    @ManyToOne
    @JoinColumn(name = "orderProcessType", foreignKey=@ForeignKey(name="FK_ORDER_PROCESS_HISTORY_REF_TYPE", foreignKeyDefinition="FOREIGN KEY (order_process_type) REFERENCES order_process_type(id)"))
    public OrderProcessType getOrderProcessType() {
        return orderProcessType;
    }

    public void setOrderProcessType(OrderProcessType orderProcessType) {
        this.orderProcessType = orderProcessType;
    }

    @Column(columnDefinition = "varchar(2000)")
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Column(columnDefinition = "datetime")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @ManyToOne
    @JoinColumn(name = "operator", foreignKey=@ForeignKey(name="FK_ORDER_PROCESS_HISTORY_REF_OPERATOR", foreignKeyDefinition="FOREIGN KEY (operator) REFERENCES internal_user(id)"))
    public InternalUser getOperator() {
        return operator;
    }

    public void setOperator(InternalUser operator) {
        this.operator = operator;
    }
}
