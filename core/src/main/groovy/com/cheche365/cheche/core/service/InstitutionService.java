package com.cheche365.cheche.core.service;

import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.*;
import com.cheche365.cheche.core.util.CacheUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunhuazhong on 2015/11/17.
 */
@Service
public class InstitutionService {
    private static final String CACHE_KEY = "com:cheche365:cheche:ordercenter:institution";
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private InstitutionRepository institutionRepository;

    @Autowired
    private InstitutionBankAccountRepository institutionBankAccountRepository;

    @Autowired
    private InstitutionRebateRepository institutionRebateRepository;

    @Autowired
    private AreaRepository areaRepository;

    @Autowired
    private InsuranceCompanyRepository insuranceCompanyRepository;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public Institution findById(Long id) {
        Institution institution = institutionRepository.findOne(id);
        institution.setBankAccountList(institutionBankAccountRepository.findByInstitution(institution));
        institution.setRebateList(institutionRebateRepository.findByInstitution(institution));
        return institution;
    }

    public List<Institution> listEnable() {
        return institutionRepository.findByEnable(true);
    }

    public List<Area> listArea(Long institutionId) {
        Institution institution = findById(institutionId);
        List<Area> areaList = institutionRebateRepository.findAreaByInstitution(institution);
        return areaList;
    }

    public List<InsuranceCompany> listInsuranceCompany(Long institutionId) {
        Institution institution = findById(institutionId);
        List<InsuranceCompany> insuranceCompanyList = institutionRebateRepository.findInsuranceCompanyByInstitution(institution);
        return insuranceCompanyList;
    }

    public List<InstitutionRebate> listInstitutionRebate(Long areaId, Long insuranceCompanyId) {
        Area area = areaRepository.findOne(areaId);
        InsuranceCompany insuranceCompany = insuranceCompanyRepository.findOne(insuranceCompanyId);
        return institutionRebateRepository.findByAreaAndInsuranceCompany(area, insuranceCompany);
    }

    public InstitutionRebate findInstitutionRebate(Long areaId, Long insuranceCompanyId, Long institutionId) {
        Area area = areaRepository.findOne(areaId);
        InsuranceCompany insuranceCompany = insuranceCompanyRepository.findOne(insuranceCompanyId);
        Institution institution = institutionRepository.findOne(institutionId);
        return institutionRebateRepository.findFirstByAreaAndInsuranceCompanyAndInstitution(area, insuranceCompany, institution);
    }

    public boolean checkName(Long institutionId, String name) {
        Institution institution = institutionRepository.findFirstByName(name);
        if (institution == null) {
            return true;
        } else {
            if (institutionId != null && institution.getId().equals(institutionId)) {
                return true;
            }
            return false;
        }
    }

    public boolean checkEnableByAreaAndInsuranceCompany(Area area, InsuranceCompany insuranceCompany) {
        Long count = institutionRebateRepository.countByAreaAndInsuranceCompany(area.getId(), insuranceCompany.getId());
        return count != null && count > 0;
    }

    public List<Institution> listByKeyWord(String keyWord) {
        List<Institution> institutionList = this.listByCache();
        List<Institution> resultList = new ArrayList<>();
        if (StringUtils.isBlank(keyWord)) return institutionList;
        if (!CollectionUtils.isEmpty(institutionList)) {
            institutionList.forEach(institution -> {
                if (institution.getName().contains(keyWord)) {
                    resultList.add(institution);
                }
            });
        } else {
            initInstitutionCache();
            listByKeyWord(keyWord);
        }
        return resultList;
    }

    private List<Institution> listByCache() {
        String cacheAString = CacheUtil.getValue(this.stringRedisTemplate, CACHE_KEY);
        List<Institution> institutionList = new ArrayList<>();
        if (!StringUtils.isEmpty(cacheAString)) {
            institutionList = CacheUtil.doListJacksonDeserialize(cacheAString, Institution.class);
            logger.debug("will get institution list cache ,size :->{}", institutionList.size());
        }
        return institutionList;
    }

    private void initInstitutionCache() {
        List<Institution> institutionList = institutionRepository.findByEnable(true);
        logger.debug("will cache institution list size: -> {}", institutionList.size());
        stringRedisTemplate.opsForValue().set(CACHE_KEY, CacheUtil.doJacksonSerialize(institutionList));
    }
}
