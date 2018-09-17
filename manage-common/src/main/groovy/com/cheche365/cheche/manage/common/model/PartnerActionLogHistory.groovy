package com.cheche365.cheche.manage.common.model

import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.DescribableEntity
import com.cheche365.cheche.core.model.InternalUser
import com.cheche365.cheche.core.model.Partner
import org.springframework.stereotype.Component

import javax.persistence.*

/**
 * 合作商管理操作日志历史表
 * Created by zhangpengcheng on 2018/4/14.
 */
@Entity
class PartnerActionLogHistory extends DescribableEntity {

    private InternalUser operator;              //操作人
    private Integer status;             //状态
    private String operationContent;             //操作内容
    private Channel channel;         //渠道
    private Partner partner;          //合作伙伴





    @ManyToOne
    @JoinColumn(name = "operator", foreignKey = @ForeignKey(name = "FK_PARTNER_ACTION_LOG_HISTORY_REF_INTERNAL_USER", foreignKeyDefinition = "FOREIGN KEY (operator) REFERENCES internal_user(id)"))
    public InternalUser getOperator() {
        return operator
    }

    public void setOperator(InternalUser operator) {
        this.operator = operator
    }

    @Column(columnDefinition = "tinyint(1)")
    Integer getStatus() {
        return status
    }

    void setStatus(Integer status) {
        this.status = status
    }

    @Column(columnDefinition = "VARCHAR(2000)")
    String getOperationContent() {
        return operationContent
    }

    void setOperationContent(String operationContent) {
        this.operationContent = operationContent
    }


    @ManyToOne
    @JoinColumn(name = "channel", foreignKey = @ForeignKey(name = "FK_PARTNER_ACTION_LOG_HISTORY_REF_CHANNEL", foreignKeyDefinition = "FOREIGN KEY (channel) REFERENCES channel(id)"))
    Channel getChannel() {
        return channel
    }

    void setChannel(Channel channel) {
        this.channel = channel
    }

    @ManyToOne
    @JoinColumn(name = "partner", foreignKey = @ForeignKey(name = "FK_PARTNER_ACTION_LOG_HISTORY_REF_PARTNER", foreignKeyDefinition = "FOREIGN KEY (partner) REFERENCES partner(id)"))
    Partner getPartner() {
        return partner
    }

    void setPartner(Partner partner) {
        this.partner = partner
    }

}
