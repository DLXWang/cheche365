package com.cheche365.cheche.rest.web.controller;

import com.cheche365.cheche.core.model.Area;
import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.model.InsuranceCompany;
import com.cheche365.cheche.core.service.AreaService;
import com.cheche365.cheche.core.service.InsuranceCompanyService;
import com.cheche365.cheche.core.util.CacheUtil;
import com.cheche365.cheche.web.ContextResource;
import com.cheche365.cheche.web.response.RestResponseEnvelope;
import com.cheche365.cheche.web.util.ClientTypeUtil;
import com.cheche365.cheche.web.version.VersionedResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


/**
 * Created by zhengwei on 4/3/15.
 */

@RestController
@RequestMapping("/" + ContextResource.VERSION_NO + "/companies")
@VersionedResource(from = "1.0")
public class InsuranceCompaniesResource extends ContextResource {

    @Autowired
    InsuranceCompanyService companyService;

    @Autowired
    private AreaService areaService;

    /**
     * 根据地区获取可报价的保险公司
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope> getCompaniesByArea(@RequestParam(value = "area", required = false) Long areaId) {
        Channel channel = ClientTypeUtil.getChannel(request);
        List<InsuranceCompany> companies = companyService.findCompaniesByAreaAndChannel(areaId, channel);
        List<Map> companyMap = CacheUtil.doListJacksonDeserialize(CacheUtil.doJacksonSerialize(companies), Map.class);
        if (channel.isLevelAgent()) {
            companyMap.forEach((Map insuranceCompany) ->
                insuranceCompany.put("policy", null)
            );
        }
        return new ResponseEntity<>(new RestResponseEnvelope(companyMap), HttpStatus.OK);
    }

    /**
     * 根据保险公司，获取支持区域
     */
    @RequestMapping(value = "/{id}/areas", method = RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope<Area>> getInsuranceCompanyArea(@PathVariable(value = "id") String companyId) {

        Channel channel = ClientTypeUtil.getChannel(request);
        List<Area> area = areaService.getAreasByCompany(companyId, channel);
        RestResponseEnvelope envelope = new RestResponseEnvelope(area, null);
        return new ResponseEntity<>(envelope, HttpStatus.OK);
    }


}
