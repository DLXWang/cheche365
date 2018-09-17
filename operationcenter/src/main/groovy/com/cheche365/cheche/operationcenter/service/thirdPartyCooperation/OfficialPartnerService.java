package com.cheche365.cheche.operationcenter.service.thirdPartyCooperation;

import com.cheche365.cheche.core.model.Partner;
import com.cheche365.cheche.core.repository.PartnerRepository;
import com.cheche365.cheche.operationcenter.model.OfficialPartnerQuery;
import com.cheche365.cheche.operationcenter.web.model.thirdParty.OfficiaPartnerViewModel;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Service
public class OfficialPartnerService {

    @Autowired
    private PartnerRepository partnerRepository;


    public boolean checkPartnerName(String name) {
        Partner partner = partnerRepository.findFirstByName(name);
        if (partner == null) {
            return true;
        } else {
            return false;
        }
    }

    public void saveOfficiaPartner(OfficiaPartnerViewModel model) {
        partnerRepository.save(this.creteOfficiaPartner(model));
    }

    private Partner creteOfficiaPartner(OfficiaPartnerViewModel model) {
        Partner partner = new Partner();
        partner.setName(model.getName());
        partner.setComment(model.getComment());
        return partner;
    }

    public Pageable buildPageable(Integer currentPage, Integer pageSize) {
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        return new PageRequest(currentPage - 1, pageSize, sort);
    }

    public Page<Partner> findChannelAgentList(OfficialPartnerQuery dataQuery, Pageable pageable) {

        return partnerRepository.findAll((root, query, cb) -> {
            CriteriaQuery<Partner> criteriaQuery = cb.createQuery(Partner.class);
            //条件构造
            List<Predicate> predicateList = new ArrayList<>();
            if(StringUtils.isNotBlank(dataQuery.getName())){
                Path<String> name = root.get("name");
                predicateList.add(cb.like(name,"%"+ dataQuery.getName() + "%"));
            }
            Predicate[] predicates = new Predicate[predicateList.size()];
            predicates = predicateList.toArray(predicates);
            return criteriaQuery.where(predicates).getRestriction();
        }, pageable);
    }
}
