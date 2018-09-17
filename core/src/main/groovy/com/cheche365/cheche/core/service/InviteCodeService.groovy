package com.cheche365.cheche.core.service

import com.cheche365.cheche.core.repository.CcAgentInviteCodeRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class InviteCodeService {

    @Autowired
    CcAgentInviteCodeRepository ccAgentInviteCodeRepository

    Boolean isChecheCode(String inviteCode){
        ccAgentInviteCodeRepository.findByInviteCode(inviteCode)
    }
}
