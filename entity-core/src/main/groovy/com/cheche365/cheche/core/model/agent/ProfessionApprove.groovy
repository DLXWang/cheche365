package com.cheche365.cheche.core.model.agent

import com.cheche365.cheche.core.repository.BaseEntity

import javax.persistence.Entity
import javax.persistence.ManyToOne
import javax.persistence.OneToOne

/**
 * Author:   shanxf
 * Date:     2018/9/10 14:43
 */
@Entity
class ProfessionApprove extends BaseEntity{

    private static final long serialVersionUID = 1L


    private ChannelAgent channelAgent

    private ApproveStatus approveStatus

    @OneToOne
    ChannelAgent getChannelAgent() {
        return channelAgent
    }

    void setChannelAgent(ChannelAgent channelAgent) {
        this.channelAgent = channelAgent
    }

    @ManyToOne
    ApproveStatus getApproveStatus() {
        return approveStatus
    }

    void setApproveStatus(ApproveStatus approveStatus) {
        this.approveStatus = approveStatus
    }
}
