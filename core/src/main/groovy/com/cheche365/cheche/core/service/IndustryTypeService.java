package com.cheche365.cheche.core.service;

import com.cheche365.cheche.core.model.abao.IndustryType;
import com.cheche365.cheche.core.repository.IndustryTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by wangjiahuan on 2016/12/23 0023.
 */
@Service
public class IndustryTypeService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    IndustryTypeRepository industryTypeRepository;

    public List<IndustryType> findByIndustry(){
        return industryTypeRepository.findByIndustry();
    }

    public List<IndustryType> findByParent(Long parent){
        return industryTypeRepository.findByParent(parent);
    }
}
