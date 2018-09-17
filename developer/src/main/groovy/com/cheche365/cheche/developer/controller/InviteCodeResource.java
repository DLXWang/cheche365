package com.cheche365.cheche.developer.controller;

import com.cheche365.cheche.core.model.agent.ChecheAgentInviteCode;
import com.cheche365.cheche.core.repository.CcAgentInviteCodeRepository;
import com.cheche365.cheche.core.service.PurchaseOrderIdService;
import com.cheche365.cheche.web.response.RestResponseEnvelope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Calendar;

@RestController
@RequestMapping("/internal/developer/inviteCodeResource")
public class InviteCodeResource {


    @Autowired
    PurchaseOrderIdService purchaseOrderIdService;

    @Autowired
    CcAgentInviteCodeRepository ccAgentInviteCodeRepository;


    @RequestMapping(value = "/inviteCode",method = RequestMethod.POST)
    public HttpEntity<RestResponseEnvelope> inviteCode(){

        ChecheAgentInviteCode checheCode = new ChecheAgentInviteCode();

        checheCode.setInviteCode(purchaseOrderIdService.getInviteCode());
        checheCode.setEnable(true);
        checheCode.setCreateTime(Calendar.getInstance().getTime());
        ccAgentInviteCodeRepository.save(checheCode);

        return new ResponseEntity<>(new RestResponseEnvelope("邀请码："+checheCode.getInviteCode(),"","邀请码创建成功！"), HttpStatus.OK);
    }

}
