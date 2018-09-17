package com.cheche365.cheche.core.service;

import com.cheche365.cheche.core.model.RebateRate;
import com.cheche365.cheche.core.mongodb.repository.RebateRateMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by shanxf on 2017/8/28.
 */

@Service
public class RebateRateMongoService {

    @Autowired
    private RebateRateMongoRepository rebateRateMongoRepository;

    public Map getRebateRate() {
        Map rebateRateResult = new HashMap();
        List<RebateRate> rebateRateList = rebateRateMongoRepository.findAll();
        if (rebateRateList != null) {
            Map<String, List<RebateRate>> rebateRateGroupBy =
                rebateRateList.stream().collect(Collectors.groupingBy(RebateRate::getArea));
            rebateRateToMap(rebateRateResult, rebateRateGroupBy);
        }
        return rebateRateResult;
    }

    private void rebateRateToMap(Map rebateRateResult, Map<String, List<RebateRate>> rebateRateGroupBy) {
        for (String area : rebateRateGroupBy.keySet()) {
            List<RebateRate> rebateRates = rebateRateGroupBy.get(area);
            List<Map> areaRebateRateMap = new ArrayList<>();
            for (RebateRate rebateRate : rebateRates) {
                Map rebateRateMap = new HashMap();
                rebateRateMap.put("insuranceCompany", rebateRate.getInsuranceCompany());
                rebateRateMap.put("compulsoryRebateRate", rebateRate.getCompulsoryRebateRate());
                rebateRateMap.put("insuranceRebateRate", rebateRate.getInsuranceRebateRate());
                areaRebateRateMap.add(rebateRateMap);
            }
            rebateRateResult.put(area, areaRebateRateMap);
        }
    }
}
