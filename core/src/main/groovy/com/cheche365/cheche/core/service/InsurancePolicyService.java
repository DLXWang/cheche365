package com.cheche365.cheche.core.service;

import com.cheche365.cheche.core.model.abao.InsurancePolicy;
import com.cheche365.cheche.core.model.User;
import com.cheche365.cheche.core.repository.InsurancePolicyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Created by wangjiahuan on 2016/11/28 0028.
 */
@Service
public class InsurancePolicyService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    InsurancePolicyRepository insurancePolicyRepository;

    public Page<InsurancePolicy> findByUser(User user, Pageable pageable) {
        return insurancePolicyRepository.findByUser(user, pageable);
    }

    public Page<InsurancePolicy> findByStatus(User user, Pageable pageable) {
        return insurancePolicyRepository.findByStatus(user, pageable);
    }

    public Page<InsurancePolicy> findByEffectiveDate(User user, Pageable pageable) {
        return insurancePolicyRepository.findByEffectiveDate(user, pageable);
    }

    public Page<InsurancePolicy> findByExpireDate(User user, Pageable pageable) {
        return insurancePolicyRepository.findByExpireDate(user, pageable);
    }

}
