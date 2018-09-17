package com.cheche365.cheche.rest.web.controller;


import com.cheche365.cheche.core.model.agent.ShopType;
import com.cheche365.cheche.core.repository.ShopTypeRepository;
import com.cheche365.cheche.web.ContextResource;
import com.cheche365.cheche.web.response.RestResponseEnvelope;
import com.cheche365.cheche.web.version.VersionedResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/" + ContextResource.VERSION_NO + "/shopType")
@VersionedResource(from = "1.6")
public class ShopTypeResource extends ContextResource {

    @Autowired
   ShopTypeRepository shopTypeRepository;

    @RequestMapping(value = "", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<RestResponseEnvelope> findAll() {
        Iterable<ShopType> shopTypes = shopTypeRepository.findAll();
        return getResponseEntity(shopTypes);
    }

}
