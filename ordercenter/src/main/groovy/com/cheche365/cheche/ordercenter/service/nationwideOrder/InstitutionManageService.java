package com.cheche365.cheche.ordercenter.service.nationwideOrder;

import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.InstitutionBankAccountRepository;
import com.cheche365.cheche.core.repository.InstitutionRebateRepository;
import com.cheche365.cheche.core.repository.InstitutionRepository;
import com.cheche365.cheche.core.service.InstitutionRebateHistoryService;
import com.cheche365.cheche.core.service.InstitutionService;
import com.cheche365.cheche.core.util.BeanUtil;
import com.cheche365.cheche.manage.common.util.AssertUtil;
import com.cheche365.cheche.manage.common.web.model.PageInfo;
import com.cheche365.cheche.ordercenter.service.user.IInternalUserManageService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.*;

/**
 * Created by sunhuazhong on 2015/11/16.
 */
@Service
@Transactional
public class InstitutionManageService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private InstitutionRepository institutionRepository;

    @Autowired
    private InstitutionBankAccountRepository institutionBankAccountRepository;

    @Autowired
    private InstitutionRebateRepository institutionRebateRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private IInternalUserManageService internalUserManageService;

    @Autowired
    private InstitutionService institutionService;

    @Autowired
    private InstitutionRebateHistoryService institutionRebateHistoryService;

    public boolean addInstitution(Institution tempInstitution) {
        // 保存出单机构
        tempInstitution = saveInstitution(tempInstitution);

        // 保存银行账户
        saveBankAccount(tempInstitution);

        // 保存城市，保险公司，商业险交强险佣金
        saveRebate(tempInstitution);
        return true;
    }

    private Institution saveInstitution(Institution tempInstitution) {
        InternalUser internalUser = internalUserManageService.getCurrentInternalUser();
        if (tempInstitution.getId() != null && tempInstitution.getId() != 0) {
            Institution institution = findById(tempInstitution.getId());
            String[] properties = new String[]{
                    "name", "comment",
                    "contactName", "contactMobile", "contactEmail", "contactQq",
                    "checheName", "checheMobile", "checheEmail", "checheQq"
            };
            BeanUtil.copyPropertiesContain(tempInstitution, institution, properties);
            institution.setUpdateTime(Calendar.getInstance().getTime());
            institution.setOperator(internalUser);
            institution = institutionRepository.save(institution);
            tempInstitution.setId(institution.getId());
            // 删除该出单机构的所有银行账户
            List<InstitutionBankAccount> institutionBankAccountList = institutionBankAccountRepository.findByInstitution(institution);
            if (CollectionUtils.isNotEmpty(institutionBankAccountList)) {
                institutionBankAccountRepository.delete(institutionBankAccountList);
            }

        } else {
            Institution institution = new Institution();
            String[] properties = new String[]{
                    "name", "comment",
                    "contactName", "contactMobile", "contactEmail", "contactQq",
                    "checheName", "checheMobile", "checheEmail", "checheQq"
            };
            BeanUtil.copyPropertiesContain(tempInstitution, institution, properties);
            institution.setEnable(false);
            institution.setCreateTime(Calendar.getInstance().getTime());
            institution.setUpdateTime(Calendar.getInstance().getTime());
            institution.setOperator(internalUser);
            institution = institutionRepository.save(institution);
            tempInstitution.setId(institution.getId());
        }
        return tempInstitution;
    }

    public void saveRebate(Institution tempInstitution) {
        List<InstitutionRebate> oldRebateList = institutionRebateRepository.findByInstitution(tempInstitution);
        InternalUser internalUser = internalUserManageService.getCurrentInternalUser();
        List<InstitutionRebate> newRebateList = tempInstitution.getRebateList();
        int i = 0;
        while (i < oldRebateList.size()) {
            InstitutionRebate oldInstitutionRebate = oldRebateList.get(i);
            Boolean del = false;
            for (InstitutionRebate newInstitutionRebate : newRebateList) {
                newInstitutionRebate.setInstitution(tempInstitution);
                if (oldInstitutionRebate.getArea().getId().equals(newInstitutionRebate.getArea().getId())
                        && oldInstitutionRebate.getInsuranceCompany().getId().equals(newInstitutionRebate.getInsuranceCompany().getId())) {
                    if (!oldInstitutionRebate.getCommercialRebate().equals(newInstitutionRebate.getCommercialRebate())
                            || !oldInstitutionRebate.getCompulsoryRebate().equals(newInstitutionRebate.getCompulsoryRebate())) {
                        newInstitutionRebate.setId(oldInstitutionRebate.getId());
                        newInstitutionRebate.setCreateTime(oldInstitutionRebate.getCreateTime());
                        newInstitutionRebate.setUpdateTime(new Date());
                        newInstitutionRebate.setOperator(internalUser);
                        institutionRebateRepository.save(newInstitutionRebate);
                        institutionRebateHistoryService.save(newInstitutionRebate, internalUser, InstitutionRebateHistory.OPERATION.UPD);
                    }
                    newRebateList.remove(newInstitutionRebate);
                    del = true;
                    break;
                }
            }
            if (del) {
                oldRebateList.remove(oldInstitutionRebate);
            } else {
                i++;
            }
        }
        for (InstitutionRebate institutionRebate : oldRebateList) {
            institutionRebateRepository.delete(institutionRebate);
            institutionRebateHistoryService.save(institutionRebate, internalUser, InstitutionRebateHistory.OPERATION.DEL);
        }
        for (InstitutionRebate institutionRebate : newRebateList) {
            institutionRebate.setInstitution(tempInstitution);
            institutionRebate.setCreateTime(new Date());
            institutionRebate.setUpdateTime(new Date());
            institutionRebate.setOperator(internalUser);
            institutionRebateRepository.save(institutionRebate);
            institutionRebateHistoryService.save(institutionRebate, internalUser, InstitutionRebateHistory.OPERATION.ADD);
        }
    }

    private void saveBankAccount(Institution tempInstitution) {
        InternalUser internalUser = internalUserManageService.getCurrentInternalUser();
        if (CollectionUtils.isNotEmpty(tempInstitution.getBankAccountList())) {
            for (InstitutionBankAccount tempBankAccount : tempInstitution.getBankAccountList()) {
                tempBankAccount.setInstitution(tempInstitution);
                tempBankAccount.setCreateTime(Calendar.getInstance().getTime());
                tempBankAccount.setUpdateTime(Calendar.getInstance().getTime());
                tempBankAccount.setOperator(internalUser);
                institutionBankAccountRepository.save(tempBankAccount);
            }
        }
    }

    public Institution findById(Long id) {
        return institutionService.findById(id);
    }

    public boolean updateInstitution(Long institutionId, Institution institution) {
        Institution originalInstitution = findById(institutionId);
        AssertUtil.notNull(originalInstitution, "can not find institution by id -> " + institutionId);
        return this.addInstitution(institution);
    }

    public Map<String, Object> search(Integer currentPage, Integer pageSize, String keyword) {
        try {
            Map<String, Object> institutionMap = new HashMap<>();
            String condition = StringUtils.isNotBlank(keyword) ? "%" + keyword + "%" : "%";
            Integer totalElement = institutionRepository.countAssignedNameAndAreaName(condition);
            if (totalElement == null || totalElement == 0) {
                PageInfo pageInfo = new PageInfo();
                pageInfo.setTotalElements(0);
                pageInfo.setTotalPage(1);
                institutionMap.put("pageInfo", pageInfo);
                institutionMap.put("content", null);
                return institutionMap;
            }

            List<Institution> institutionList = findByPage(currentPage, pageSize, keyword);
            List<Institution> institutionDetailList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(institutionList)) {
                for (Institution institution : institutionList) {
                    institutionDetailList.add(findById(institution.getId()));
                }
            }
            PageInfo pageInfo = new PageInfo();
            pageInfo.setTotalElements(totalElement);
            pageInfo.setTotalPage(totalElement.equals(pageSize) ? 1 : (totalElement / pageSize + 1));
            institutionMap.put("pageInfo", pageInfo);
            institutionMap.put("content", institutionDetailList);
            return institutionMap;
        } catch (Exception e) {
            logger.error("find institution info by page has error", e);
        }
        return null;
    }

    private List<Institution> findByPage(Integer currentPage, Integer pageSize, String keyword) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Institution> query = cb.createQuery(Institution.class);
        Root<InstitutionRebate> institutionRebateRoot = query.from(InstitutionRebate.class);
        Join<InstitutionRebate, Area> areaJoin = institutionRebateRoot.join("area");

        Path<Institution> institutionPath = institutionRebateRoot.<Institution>get("institution");
        Path<String> namePath = institutionPath.get("name");
        Path<String> areaPath = areaJoin.get("name");

        List<Predicate> predicateList = new ArrayList<>();
        if (StringUtils.isNotBlank(keyword)) {
            predicateList.add(cb.or(
                    cb.like(namePath, "%" + keyword + "%"),
                    cb.like(areaPath, "%" + keyword + "%")
            ));
        }

        Predicate[] predicates = new Predicate[predicateList.size()];
        predicates = predicateList.toArray(predicates);
        query.select(institutionPath).distinct(true).where(predicates).orderBy(cb.desc(institutionPath.get("createTime")));
        TypedQuery<Institution> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((currentPage - 1) * pageSize);
        typedQuery.setMaxResults(pageSize);
        return typedQuery.getResultList();
    }

    public boolean switchStatus(Long institutionId, Integer operationType) {
        try {
            Institution institution = institutionRepository.findOne(institutionId);
            AssertUtil.notNull(institution, "can not find institution by id -> " + institutionId);
            // 启用或禁用出单机构
            institution.setEnable(operationType == 1);
            institution.setUpdateTime(Calendar.getInstance().getTime());
            institution.setOperator(internalUserManageService.getCurrentInternalUser());
            institutionRepository.save(institution);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public List<Institution> listEnable() {
        return institutionService.listEnable();
    }

    public List<Area> listArea(Long institutionId) {
        return institutionService.listArea(institutionId);
    }

    public List<InsuranceCompany> listInsuranceCompany(Long institutionId) {
        return institutionService.listInsuranceCompany(institutionId);
    }

    public List<InstitutionRebate> listInstitutionRebate(Long areaId, Long insuranceCompanyId) {
        return institutionService.listInstitutionRebate(areaId, insuranceCompanyId);
    }

    public InstitutionRebate findInstitutionRebate(Long areaId, Long insuranceCompanyId, Long institutionId) {
        return institutionService.findInstitutionRebate(areaId, insuranceCompanyId, institutionId);
    }

    public InstitutionRebate findInstitutionRebateById(Long id) {
        return institutionRebateRepository.findOne(id);
    }

    public boolean checkName(Long institutionId, String name) {
        return institutionService.checkName(institutionId, name);
    }

    public List<Area> findAreaByInstitutionAndEnable(Boolean enable) {
        return institutionRebateRepository.findAreaByInstitutionAndEnable(enable);
    }

}
