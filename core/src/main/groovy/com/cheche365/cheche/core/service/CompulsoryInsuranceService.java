package com.cheche365.cheche.core.service;

import com.cheche365.cheche.core.model.Auto;
import com.cheche365.cheche.core.model.CompulsoryInsurance;
import com.cheche365.cheche.core.model.QuoteRecord;
import com.cheche365.cheche.core.model.User;
import com.cheche365.cheche.core.repository.CompulsoryInsuranceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mahong on 2015/7/22.
 */
@Service
public class CompulsoryInsuranceService {

    @Autowired
    private CompulsoryInsuranceRepository cip;

    public List<CompulsoryInsurance> findCompulsoryInsurancesByUser(User user, List<Long> orderIds) {
        return cip.findCompulsoryInsurancesByUser(user.getId(),orderIds);
    }

    public Map<Long, CompulsoryInsurance> assambleCompulsoryInsurance(List<CompulsoryInsurance> compulsoryInsurances) {
        if (compulsoryInsurances == null) {
            return null;
        }

        Map<Long, CompulsoryInsurance> map = new HashMap<>();
        for (CompulsoryInsurance compulsoryInsurance : compulsoryInsurances) {
            map.put(compulsoryInsurance.getQuoteRecord().getId(), compulsoryInsurance);
        }
        return map;
    }

    public CompulsoryInsurance findByQuoteRecord(QuoteRecord quoteRecord) {
        return cip.findFirstByQuoteRecordOrderByCreateTimeDesc(quoteRecord);
    }

    public CompulsoryInsurance findByQuoteRecordId(Long quoteRecordId) {
        return cip.findByQuoteRecordId(quoteRecordId);
    }

    public CompulsoryInsurance findByAuto(Auto auto){
        return cip.findFirstByAutoIdOrderByIdDesc(auto.getId());
    }
}
