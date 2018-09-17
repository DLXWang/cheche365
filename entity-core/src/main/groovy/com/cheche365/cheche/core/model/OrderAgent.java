package com.cheche365.cheche.core.model;

import javax.persistence.*;

/**
 * Created by xu.yelong on 2016/3/10.
 */
@Entity
public class OrderAgent extends DescribableEntity {

    private PurchaseOrder purchaseOrder;
    private Agent agent;

    @ManyToOne
    @JoinColumn(name = "purchaseOrder", foreignKey=@ForeignKey(name="FK_ORDER_AGENT_REF_PURCHASE_ORDER", foreignKeyDefinition="FOREIGN KEY (purchaseOrder) REFERENCES purchase_order(id)"))
    public PurchaseOrder getPurchaseOrder() {
        return purchaseOrder;
    }

    public void setPurchaseOrder(PurchaseOrder purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

    @ManyToOne
    @JoinColumn(name = "agent", foreignKey=@ForeignKey(name="FK_ORDER_AGENT_REF_AGENT", foreignKeyDefinition="FOREIGN KEY (agent) REFERENCES agent(id)"))
    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }
}
