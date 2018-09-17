package com.cheche365.cheche.rest.web.controller;

import com.cheche365.cheche.core.model.developer.DeveloperInfo;
import com.cheche365.cheche.core.repository.developer.DeveloperBusinessTypeRepository;
import com.cheche365.cheche.core.repository.developer.DeveloperTypeRepository;
import com.cheche365.cheche.web.ContextResource;
import com.cheche365.cheche.web.service.developer.DeveloperService;
import com.cheche365.cheche.web.version.VersionedResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

/**
 * Created by zhengwei on 08/03/2018.
 */

@RestController
@RequestMapping("/" + ContextResource.VERSION_NO + "/developers")
@VersionedResource(from = "1.6")
public class DevelopersResource extends ContextResource {

    @Autowired
    private DeveloperTypeRepository developerTypeRepository;

    @Autowired
    private DeveloperBusinessTypeRepository developerBusinessTypeRepository;

    @Autowired
    private DeveloperService developerService;

    @RequestMapping(value = "/types", method = RequestMethod.GET)
    public HttpEntity devTypes(){
        return getResponseEntity(developerTypeRepository.findAll());
    }

    /**
     * 如果parent为空就表示查最顶层的business type
     * @param parent
     * @return
     */
    @RequestMapping(value = "/businessTypes", method = RequestMethod.GET)
    public HttpEntity devBusinessTypes(@RequestParam(value = "parent", required = false) Long parent){

        return getResponseEntity(
            null==parent ? developerBusinessTypeRepository.findTopLevelTypes() : developerBusinessTypeRepository.findByParent(parent)
        );
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public HttpEntity addDev(@RequestBody DeveloperInfo devInfo){
        developerService.add(devInfo);
        return getResponseEntity( new HashMap(){{
            put("message", "success");
        }});
    }
}
