package com.cheche365.cheche.core.service;

import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by sunhuazhong on 2015/11/17.
 */
@Service
public class InstitutionTempService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private InstitutionTempRepository institutionRepository;

    @Autowired
    private InstitutionBankAccountTempRepository institutionBankAccountTempRepository;

    @Autowired
    private InstitutionRebateTempRepository institutionRebateTempRepository;

    @Autowired
    private AreaRepository areaRepository;

    @Autowired
    private InsuranceCompanyRepository insuranceCompanyRepository;

    public InstitutionTemp findById(Long id) {
        InstitutionTemp institutionTemp = institutionRepository.findOne(id);
        institutionTemp.setbankAccountTempList(institutionBankAccountTempRepository.findByInstitutionTemp(institutionTemp));
        institutionTemp.setRebateListTemp(institutionRebateTempRepository.findByInstitutionTemp(institutionTemp));
        return institutionTemp;
    }

    public List<InstitutionTemp> listEnable() {
        return institutionRepository.findByEnable(true);
    }

    public List<Area> listArea(Long institutionId) {
        InstitutionTemp institutionTemp = findById(institutionId);
        List<Area> areaList = institutionRebateTempRepository.findAreaByInstitutionTemp(institutionTemp);
        return areaList;
    }

    public List<InsuranceCompany> listInsuranceCompany(Long institutionId) {
        InstitutionTemp institutionTemp = findById(institutionId);
        List<InsuranceCompany> insuranceCompanyList = institutionRebateTempRepository.findInsuranceCompanyByInstitutionTemp(institutionTemp);
        return insuranceCompanyList;
    }

    public List<InstitutionRebateTemp> listInstitutionRebate(Long areaId, Long insuranceCompanyId) {
        Area area = areaRepository.findOne(areaId);
        InsuranceCompany insuranceCompany = insuranceCompanyRepository.findOne(insuranceCompanyId);
        return institutionRebateTempRepository.findByAreaAndInsuranceCompany(area, insuranceCompany);
    }

    public InstitutionRebateTemp findInstitutionRebate(Long areaId, Long insuranceCompanyId, Long institutionId) {
        Area area = areaRepository.findOne(areaId);
        InsuranceCompany insuranceCompany = insuranceCompanyRepository.findOne(insuranceCompanyId);
        InstitutionTemp institution = institutionRepository.findOne(institutionId);
        return institutionRebateTempRepository.findFirstByAreaAndInsuranceCompanyAndInstitutionTemp(area, insuranceCompany, institution);
    }

    public boolean checkName(Long institutionId, String name) {
        InstitutionTemp institutionTemp = institutionRepository.findFirstByName(name);
        if (institutionTemp == null) {
            return true;
        } else {
            if (institutionId != null && institutionTemp.getId().equals(institutionId)) {
                return true;
            }
            return false;
        }
    }

    public boolean checkEnableByAreaAndInsuranceCompany(Area area, InsuranceCompany insuranceCompany) {
        Long count = institutionRebateTempRepository.countByAreaAndInsuranceCompany(area.getId(), insuranceCompany.getId());
        return count != null && count > 0;
    }
}
