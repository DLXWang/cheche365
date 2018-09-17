package com.cheche365.cheche.operationcenter.model

import com.fasterxml.jackson.annotation.JsonFormat
import groovy.transform.ToString
import org.springframework.format.annotation.DateTimeFormat

@ToString
class ChannelAgentQuery implements Serializable {
    private static final long serialVersionUID = 1L

    Integer pageSize
    Integer currentPage
    String title
    String userName
    String mobile
    String inviter
    Long agentLevel
    String identity
    Integer draw
    String sort//排序
    Long channel
    String shop
    String topLevelInviter
    String inviteCode
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    Date registerTimeStart
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    Date registerTimeEnd
    Long approveStatus //认证状态
}
