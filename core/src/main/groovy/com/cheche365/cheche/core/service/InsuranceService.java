package com.cheche365.cheche.core.service;

import com.cheche365.cheche.core.model.Auto;
import com.cheche365.cheche.core.model.Insurance;
import com.cheche365.cheche.core.model.QuoteRecord;
import com.cheche365.cheche.core.model.User;
import com.cheche365.cheche.core.repository.InsuranceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mahong on 2015/7/22.
 */
@Service
public class InsuranceService {

    @Autowired
    private InsuranceRepository insuranceRepository;

    public List<Insurance> findInsurancesByUser(User user, List<Long> orderIds) {
        return insuranceRepository.findInsurancesByUser(user.getId(), orderIds);
    }

    public Map<Long, Insurance> assambleInsurance(List<Insurance> insurances) {
        if (insurances == null) {
            return null;
        }

        Map<Long, Insurance> map = new HashMap<>();
        for (Insurance insurance : insurances) {
            map.put(insurance.getQuoteRecord().getId(), insurance);
        }
        return map;
    }

    public Insurance findByQuoteRecord(QuoteRecord quoteRecord) {
        return insuranceRepository.findFirstByQuoteRecordOrderByCreateTimeDesc(quoteRecord);
    }

    public Insurance findByQuoteRecordId(Long quoteRecordId) {
        return insuranceRepository.findByQuoteRecordId(quoteRecordId);
    }

    public Insurance findByAuto(Auto auto){
        return insuranceRepository.findFirstByAutoIdOrderByIdDesc(auto.getId());
    }
}
