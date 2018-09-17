package com.cheche365.cheche.operationcenter.service.marketingRule;

import com.cheche365.cheche.core.model.GiftType;
import com.cheche365.cheche.core.model.GiftTypeUseType;
import com.cheche365.cheche.core.repository.GiftTypeRepository;
import com.cheche365.cheche.manage.common.service.InternalUserManageService;
import com.cheche365.cheche.operationcenter.web.model.marketing.GiftRequestParams;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by yinJianBin on 2017/4/21.
 */
@Service
public class GiftTypeService {

    @Autowired
    private GiftTypeRepository giftTypeRepository;

    @Autowired
    private InternalUserManageService internalUserManageService;


    public Page<GiftType> list(GiftRequestParams params) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(params.getCurrentPage() - 1, params.getPageSize(), sort);
        Page<GiftType> giftTypePage = this.findPageByParam(params, pageable);

        return giftTypePage;
    }

    private Page<GiftType> findPageByParam(GiftRequestParams params, Pageable pageable) {
        Page<GiftType> page = giftTypeRepository.findAll(new Specification<GiftType>() {
            @Override
            public Predicate toPredicate(Root<GiftType> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicateList = new ArrayList<Predicate>();

                if (StringUtils.isNotEmpty(params.getGiftName())) {
                    predicateList.add(cb.like(root.get("name"), "%" + params.getGiftName() + "%"));
                }
                if (StringUtils.isNotEmpty(params.getGiftType())) {
                    predicateList.add(cb.equal(root.get("category"), params.getGiftType()));
                }
                if (params.getGiftStatus() != null) {
                    predicateList.add(cb.equal(root.get("disable"), params.getGiftStatus() > 0));
                }

                Predicate[] predicates = new Predicate[predicateList.size()];
                predicates = predicateList.toArray(predicates);
                return query.where(predicates).getRestriction();
            }
        }, pageable);
        return page;
    }

    /**
     * 新增礼物类型
     *
     * @param params
     */
    public void add(GiftRequestParams params) {
        GiftType giftType = new GiftType();
        giftType.setName(params.getGiftName());
        giftType.setDescription(params.getDescription());
        if (StringUtils.isNotEmpty(params.getGiftType())) {
            giftType.setCategory(NumberUtils.toInt(params.getGiftType()));
            giftType.setCategoryName(GiftType.Enum.CATEGORY_MAPPING.get(giftType.getCategory()));
        }
        giftType.setCreateTime(new Date());
        giftType.setDisable(false);
        giftType.setOperator(internalUserManageService.getCurrentInternalUser());
        giftType.setUseType(StringUtils.isEmpty(params.getUseType()) ? null : GiftTypeUseType.Enum.findById(NumberUtils.toLong(params.getUseType())));
        giftType.setDeliveryFlag(params.getDeliveryFlag() > 0);

        giftTypeRepository.save(giftType);
    }

    public List<String> searchByName(String paramWord, Integer pageSize) {
        return giftTypeRepository.findByNameLike(paramWord, pageSize);
    }

    public void updateDisable(Long id, Integer disable) {
        GiftType giftType = giftTypeRepository.findOne(id);
        if (giftType != null) {
            giftType.setDisable(disable > 0);
            giftType.setOperator(internalUserManageService.getCurrentInternalUser());
            giftType.setUpdateTime(new Date());

            giftTypeRepository.save(giftType);
        }
    }

    public List<GiftType> uniqueCheck(String giftName, String category, String useType) {
        return giftTypeRepository.findByNameAndCategoryAndUseType(giftName, category, useType);
    }
}
