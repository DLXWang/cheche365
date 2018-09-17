package com.cheche365.cheche.core.model.agent

import com.cheche365.cheche.core.model.Area

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne

/**
 * Author:   shanxf
 * Date:     2018/6/7 16:31
 */
@Entity
class AgentInviteCodeArea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id

    @ManyToOne
    Area area

    @ManyToOne
    ChecheAgentInviteCode checheAgentInviteCode
}
