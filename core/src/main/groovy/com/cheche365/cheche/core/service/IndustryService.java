package com.cheche365.cheche.core.service;

import com.cheche365.cheche.core.model.abao.Industry;
import com.cheche365.cheche.core.repository.IndustryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by wangjiahuan on 2016/12/23 0023.
 */
@Service
public class IndustryService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    IndustryRepository industryRepository;
    public List<Industry> findByIndustryType(Long id){
        return industryRepository.findByIndustryType(id);
    }
}
