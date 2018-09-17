package com.cheche365.cheche.rest.web.controller;

import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.model.agent.Ethnic;
import com.cheche365.cheche.core.service.DictionaryService;
import com.cheche365.cheche.web.ContextResource;
import com.cheche365.cheche.web.response.RestResponseEnvelope;
import com.cheche365.cheche.web.version.VersionedResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by WF on 2015/7/16.
 */

@RestController
@RequestMapping("/"+ ContextResource.VERSION_NO+"/dictionary")
@VersionedResource(from = "1.0")
public class DictionaryResource extends ContextResource {

    @Autowired
    DictionaryService dictionaryService;

    @RequestMapping(value="/giftstatus", method= RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope<Auto>> getAll() {

        Iterable<GiftStatus> giftStatus = this.dictionaryService.getAll();

        RestResponseEnvelope envelope = new RestResponseEnvelope(giftStatus);

        return new ResponseEntity<>(envelope, HttpStatus.OK);

    }

    @RequestMapping(value="/gifttype", method= RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope<Auto>> getgifttype(){

        Iterable<GiftType> giftType = this.dictionaryService.getgiftype();

        RestResponseEnvelope envelope = new RestResponseEnvelope(giftType);

        return new ResponseEntity<>(envelope, HttpStatus.OK);
    }

    @RequestMapping(value="/areatype", method= RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope<Auto>> getareatype(){

        Iterable<AreaType> areatype = this.dictionaryService.getareatype();

        RestResponseEnvelope envelope = new RestResponseEnvelope(areatype);

        return new ResponseEntity<>(envelope, HttpStatus.OK);
    }

    @RequestMapping(value="/channel", method= RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope<Auto>> getchannel(){

        Iterable<Channel> channel = this.dictionaryService.getchannel();

        RestResponseEnvelope envelope = new RestResponseEnvelope(channel);

        return new ResponseEntity<>(envelope, HttpStatus.OK);

    }

    @RequestMapping(value="/gender", method= RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope<Auto>> getgender(){

        Iterable<Gender> gender = this.dictionaryService.getgender();

        RestResponseEnvelope envelope = new RestResponseEnvelope(gender);

        return new ResponseEntity<>(envelope, HttpStatus.OK);

    }

    @RequestMapping(value="/glasstype", method= RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope<Auto>> getglasstype(){

        Iterable<GlassType> glasstype = this.dictionaryService.getglasstype();

        RestResponseEnvelope envelope = new RestResponseEnvelope(glasstype);

        return new ResponseEntity<>(envelope, HttpStatus.OK);

    }

    @RequestMapping(value="/identitytype", method= RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope<Auto>> getidentitytype(){

        Iterable<IdentityType> identitytype = this.dictionaryService.getidentitytype();

        RestResponseEnvelope envelope = new RestResponseEnvelope(identitytype);

        return new ResponseEntity<>(envelope, HttpStatus.OK);

    }

    @RequestMapping(value="/orderstatus", method= RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope<Auto>> getorderstatus(){

        Iterable<OrderStatus> orderstatus = this.dictionaryService.getorderstatus();

        RestResponseEnvelope envelope = new RestResponseEnvelope(orderstatus);

        return new ResponseEntity<>(envelope, HttpStatus.OK);

    }

    @RequestMapping(value="/ordertype", method= RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope<Auto>> getordertype(){

        Iterable<OrderType> ordertype = this.dictionaryService.getordertype();

        RestResponseEnvelope envelope = new RestResponseEnvelope(ordertype);

        return new ResponseEntity<>(envelope, HttpStatus.OK);

    }

    @RequestMapping(value="/paymentchannel", method= RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope<Auto>> getpaymentchannel(){

        Iterable<PaymentChannel> paymentchannel = this.dictionaryService.getpaymentchannel();

        RestResponseEnvelope envelope = new RestResponseEnvelope(paymentchannel);

        return new ResponseEntity<>(envelope, HttpStatus.OK);

    }

    @RequestMapping(value="/paymentstatus", method= RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope<Auto>> getpaymentstatusl(){

        Iterable<PaymentStatus> paymentstatus = this.dictionaryService.getpaymentstatusl();

        RestResponseEnvelope envelope = new RestResponseEnvelope(paymentstatus);

        return new ResponseEntity<>(envelope, HttpStatus.OK);

    }

    @RequestMapping(value="/cancelreasons", method= RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope<List<OrderCancelReasonType>>> listOrderCancelReasonType() {
        List<OrderCancelReasonType> reasonList = dictionaryService.listOrderCancelReasonType();
        return new ResponseEntity<>(new RestResponseEnvelope(reasonList), HttpStatus.OK);
    }

    @RequestMapping(value="/ethnic", method= RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope<Ethnic>> ethnics() {
        Iterable<Ethnic> ethnics = dictionaryService.getEthnics();
        return new ResponseEntity<>(new RestResponseEnvelope(ethnics), HttpStatus.OK);
    }
}
