package com.cheche365.cheche.core.service;

import com.cheche365.cheche.core.model.ThirdServiceFail;
import com.cheche365.cheche.core.repository.ThirdServiceFailRepository;
import groovy.util.logging.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
public class ThirdServiceFailService {

    @Autowired
    private ThirdServiceFailRepository thirdServiceFailRepository;

    public ThirdServiceFail saveThirdServiceFail(long companyId, long orderId, String logMessage) {
        ThirdServiceFail fail = new ThirdServiceFail();
        fail.setCompanyId(companyId);
        fail.setOrderId(orderId);
        fail.setCreateTime(new Date());
        fail.setMessage(logMessage);
        return thirdServiceFailRepository.save(fail);
    }

}
