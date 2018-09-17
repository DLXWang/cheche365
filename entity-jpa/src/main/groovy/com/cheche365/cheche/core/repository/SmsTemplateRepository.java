package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.SmsTemplate;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by guoweifu on 2015/10/8.
 */
@Repository
public interface SmsTemplateRepository extends PagingAndSortingRepository<SmsTemplate, Long> , JpaSpecificationExecutor<SmsTemplate> {

    SmsTemplate findFirstByName(String name);
    SmsTemplate findFirstByZucpCode(String zucpCode);

}
