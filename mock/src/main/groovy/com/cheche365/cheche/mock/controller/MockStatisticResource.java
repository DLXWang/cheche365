package com.cheche365.cheche.mock.controller;

import com.cheche365.cheche.core.constants.WebConstants;
import com.cheche365.cheche.core.model.ActivityMonitorData;
import com.cheche365.cheche.core.repository.BusinessActivityRepository;
import com.cheche365.cheche.core.service.AccessStatisticsService;
import com.cheche365.cheche.web.counter.annotation.NonProduction;
import com.cheche365.cheche.web.version.VersionedResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by zhengwei on 6/8/17.
 */

@RestController
@RequestMapping("/" + WebConstants.VERSION_NO + "/mock/statistic")
@VersionedResource(from = "1.5")
class MockStatisticResource{

    @Autowired
    AccessStatisticsService service;

    @Autowired
    BusinessActivityRepository baRepo;

    @NonProduction
    @RequestMapping(value="", method= RequestMethod.GET)
    @ResponseBody
    public List<ActivityMonitorData> find( @RequestParam("statTime") int statTime,
                                          @RequestParam("baId") Long baId, @RequestParam("withArea") boolean withArea)throws Exception{
        if(withArea) {
            return service.totalAPI(statTime, baRepo.findOne(baId),"AREA");
        } else {
            return service.totalAPI(statTime, baRepo.findOne(baId),"NO_AREA");
        }
    }


    public Date parseDate(String date) throws Exception{
        return  new SimpleDateFormat("yyyy-MM-dd HH").parse(date);
    }

}
