package com.cheche365.cheche.admin.service.auto;

import com.cheche365.cheche.admin.constants.KeyTypeEnum;
import com.cheche365.cheche.manage.common.service.BaseService;
import com.cheche365.cheche.core.model.Auto;
import com.cheche365.cheche.core.repository.AutoRepository;
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
 * Created by guoweifu on 2015/9/7.
 */
@Service
public class AutoManagementService extends BaseService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AutoRepository autoRepository;

    public Page<Auto> listAuto(Integer currentPage, Integer pageSize, String keyword, Integer keyType) {
        try {
            Pageable pageable = this.buildPageable(currentPage, pageSize);
            Page<Auto> autoPage = null;
            if (StringUtils.isNotBlank(keyword) && keyType == KeyTypeEnum.MOBILE.getIndex()) {//根据手机号查询
                autoPage = this.autoRepository.findAutoByMobile(keyword, pageable);
            } else {//查询全部或根据车牌号查询
                autoPage = this.findBySpecAndPaginate(keyword, keyType, pageable);
            }
            return autoPage;
        } catch (Exception e) {
            logger.error("listAuto by page has error", e);
        }
        return null;
    }

    private Page<Auto> findBySpecAndPaginate(String keyword, Integer keyType, Pageable pageable) {
        return autoRepository.findAll(new Specification<Auto>() {
            @Override
            public Predicate toPredicate(Root<Auto> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                CriteriaQuery<Auto> criteriaQuery = cb.createQuery(Auto.class);

                //条件构造
                List<Predicate> predicateList = new ArrayList<>();
                if (StringUtils.isNotBlank(keyword)) {
                    // 车牌号
                    if (KeyTypeEnum.LICENSE_PLATE_NO.getIndex() == keyType) {
                        Path<String> licensePlateNoPath = root.get("licensePlateNo");
                        predicateList.add(cb.like(licensePlateNoPath, keyword + "%"));
                    }
                }
                // 只显示有效
                Path<Boolean> disablePath = root.get("disable");
                predicateList.add(cb.equal(disablePath, Boolean.FALSE));

                Predicate[] predicates = new Predicate[predicateList.size()];
                predicates = predicateList.toArray(predicates);
                return criteriaQuery.where(predicates).getRestriction();
            }
        }, pageable);
    }

    /**
     * 构建分页信息 以ID倒序排序
     *
     * @param currentPage 当前页面
     * @param pageSize    每页显示数
     * @return Pageable
     */
    private Pageable buildPageable(int currentPage, int pageSize) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        return new PageRequest(currentPage - 1, pageSize, sort);
    }

    public Auto findById(Long autoId) {
        return autoRepository.findOne(autoId);
    }
}
