package com.cheche365.cheche.core.model.agent

import com.cheche365.cheche.core.model.Channel
import groovy.transform.Canonical

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.OneToOne

@Entity
@Canonical(excludes = ['id'])
class ChecheAgentInviteCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id

    @ManyToOne
    Channel channel

    @Column(columnDefinition = "VARCHAR(8)")
    String inviteCode

    @OneToOne
    ChannelAgent channelAgent

    /**
     * enable 代表该邀请码是否可用   enable = 0  代表不可用 enable = 1 表示该验证码可用
     */
    @Column(columnDefinition = "tinyint(1)")
    Boolean enable

    @Column(columnDefinition = "DATETIME")
    Date createTime

    @Column(columnDefinition = "varchar(20)")
    String applicantName


}
