package com.cheche365.cheche.core.service;

import com.cheche365.cheche.core.model.Area;
import com.cheche365.cheche.core.model.AreaContactInfo;
import com.cheche365.cheche.core.repository.AreaContactInfoRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;



/**
 * Created by xu.yelong on 2015/11/13.
 */
@Service
public class AreaContactInfoService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AreaContactInfoRepository areaContactInfoRepository;


    public Page<AreaContactInfo> listQuotePhoto(Integer currentPage, Integer pageSize, String keyword, Integer keyType) {
        try {
            Pageable pageable = this.buildPageable(currentPage, pageSize);
            return this.findBySpecAndPaginate(keyword, keyType, pageable);
        } catch (Exception e) {
            logger.error("list AreaContactInfo by page has error", e);
        }
        return null;
    }

    public AreaContactInfo findById(Long id){
        return areaContactInfoRepository.findOne(id);
    }

    public AreaContactInfo findByArea(Area area){
        return areaContactInfoRepository.findFirstByArea(area);
    }

    private Pageable buildPageable(int currentPage, int pageSize) {
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        return new PageRequest(currentPage - 1, pageSize, sort);
    }


    private Page<AreaContactInfo> findBySpecAndPaginate(String keyword, Integer keyType, Pageable pageable) {
        return areaContactInfoRepository.findAll(new Specification<AreaContactInfo>() {
            @Override
            public Predicate toPredicate(Root<AreaContactInfo> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                CriteriaQuery<AreaContactInfo> criteriaQuery = cb.createQuery(AreaContactInfo.class);
                List<Predicate> predicateList = new ArrayList<Predicate>();
                if (!StringUtils.isEmpty(keyword)) {
                    Path<String> namePath=root.get("name");
                    Path<String> mobilePath = root.get("mobile");
                    Path<String> areaPath=root.get("area").get("name");
                    if (1 == keyType) {
                        predicateList.add(cb.like(areaPath, keyword + "%"));
                    } else if (2 == keyType) {
                        predicateList.add(cb.like(namePath, keyword + "%"));
                    }else if(3==keyType){
                        predicateList.add(cb.like(mobilePath, keyword + "%"));
                    }
                }
                Predicate[] predicates = new Predicate[predicateList.size()];
                predicates = predicateList.toArray(predicates);
                return criteriaQuery.where(predicates).getRestriction();
            }
        }, pageable);
    }

}
