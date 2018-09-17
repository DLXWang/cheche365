package com.cheche365.cheche.rest.web.controller;

import com.cheche365.cheche.core.service.InsurancePackageService;
import com.cheche365.cheche.web.ContextResource;
import com.cheche365.cheche.web.response.RestResponseEnvelope;
import com.cheche365.cheche.web.version.VersionedResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by chennan on 2015/12/15.
 */
@RestController
@RequestMapping("/" + ContextResource.VERSION_NO + "/packages")
@VersionedResource(from = "1.0")
public class InsurancePackageResource extends ContextResource {


    @RequestMapping(value="/", method= RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope> getInsurancePackage(){

        RestResponseEnvelope envelope = new RestResponseEnvelope(InsurancePackageService.getInsurancePackage(getChannel()));
        return new ResponseEntity<>(envelope, HttpStatus.OK);
    }

}
