package com.cheche365.cheche.rest.web.controller;

import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.service.BankService;
import com.cheche365.cheche.core.service.WebPurchaseOrderService;
import com.cheche365.cheche.web.ContextResource;
import com.cheche365.cheche.web.response.RestResponseEnvelope;
import com.cheche365.cheche.web.version.VersionedResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by wangjiahuan on 2016/12/22 0022.
 */
@RestController
@RequestMapping("/" + ContextResource.VERSION_NO + "/banks")
@VersionedResource(from = "1.4")
public class BankResource extends ContextResource {

    @Autowired
    private BankService bankService;

    @Autowired
    private WebPurchaseOrderService orderService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<RestResponseEnvelope<List<Bank>>> findAll() {
        List<Bank> bankList = bankService.findAll();
        return new ResponseEntity<>(new RestResponseEnvelope(bankList), HttpStatus.OK);
    }

    @RequestMapping(value = "/cards/{orderNo}", method = RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope<List<BankCard>>> getBankCards(@PathVariable String orderNo) {
        User user = this.currentUser();
        PurchaseOrder order = orderService.checkOrder(orderNo, user);
        List<BankCard> bankCards = bankService.findByApplicantNameAndUser(order.getObjId(), user);
        return new ResponseEntity<>(new RestResponseEnvelope<>(bankCards), HttpStatus.OK);
    }

    @RequestMapping(value = "/queryCards", method = RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope<List<BankCard>>> queryBankCards() {
        User user = this.currentUser();
        List<BankCard> bankCards = bankService.findbyUser(user,getChannel());
        return new ResponseEntity<>(new RestResponseEnvelope<>(bankCards), HttpStatus.OK);
    }

    @RequestMapping(value = "/cards", method = RequestMethod.POST)
    public HttpEntity<RestResponseEnvelope<BankCard>> createBankCard(@RequestBody BankCard bankCard) {
        BankCard bankCardSaved = bankService.createBankCard(bankCard, this.currentUser(),getChannel());
        bankCardSaved.getBank().assembleLogoUrl(getChannel());
        return new ResponseEntity<>(new RestResponseEnvelope(bankCardSaved), HttpStatus.OK);
    }

    @RequestMapping(value = "/cards/{id}", method = RequestMethod.DELETE)
    public HttpEntity<RestResponseEnvelope<BankCard>> deleteBankCard(@PathVariable Long id) {
        BankCard bankCard = bankService.deleteBankCardById(id, this.currentUser());
        return new ResponseEntity<>(new RestResponseEnvelope(bankCard), HttpStatus.OK);
    }
}
