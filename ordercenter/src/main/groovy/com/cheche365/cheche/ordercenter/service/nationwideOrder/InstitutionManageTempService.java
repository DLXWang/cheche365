package com.cheche365.cheche.ordercenter.service.nationwideOrder;

import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.InstitutionBankAccountTempRepository;
import com.cheche365.cheche.core.repository.InstitutionRebateTempRepository;
import com.cheche365.cheche.core.repository.InstitutionTempRepository;
import com.cheche365.cheche.core.service.InstitutionRebateHistoryTempService;
import com.cheche365.cheche.core.service.InstitutionTempService;
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
public class InstitutionManageTempService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private InstitutionTempRepository institutionTempRepository;

    @Autowired
    private InstitutionBankAccountTempRepository institutionBankAccountTempRepository;

    @Autowired
    private InstitutionRebateTempRepository institutionRebateTempRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private IInternalUserManageService internalUserManageService;

    @Autowired
    private InstitutionTempService institutionTempService;

    @Autowired
    private InstitutionRebateHistoryTempService institutionRebateHistoryTempService;

    public boolean addInstitutionTemp(InstitutionTemp tempInstitutionTemp) {
        // 保存出单机构
        tempInstitutionTemp = saveInstitutionTemp(tempInstitutionTemp);

        // 保存银行账户
        saveBankAccount(tempInstitutionTemp);

        // 保存城市，保险公司，商业险交强险佣金
        saveRebate(tempInstitutionTemp);
        return true;
    }

    private InstitutionTemp saveInstitutionTemp(InstitutionTemp tempInstitutionTemp) {
        InternalUser internalUser = internalUserManageService.getCurrentInternalUser();
        if (tempInstitutionTemp.getId() != null && tempInstitutionTemp.getId() != 0) {
            InstitutionTemp institutionTemp = findById(tempInstitutionTemp.getId());
            String[] properties = new String[]{
                    "name", "comment",
                    "contactName", "contactMobile", "contactEmail", "contactQq",
                    "checheName", "checheMobile", "checheEmail", "checheQq"
            };
            BeanUtil.copyPropertiesContain(tempInstitutionTemp, institutionTemp, properties);
            institutionTemp.setUpdateTime(Calendar.getInstance().getTime());
            institutionTemp.setOperator(internalUser);
            institutionTemp = institutionTempRepository.save(institutionTemp);
            tempInstitutionTemp.setId(institutionTemp.getId());
            // 删除该出单机构的所有银行账户
            List<InstitutionBankAccountTemp> institutionTempBankAccountList = institutionBankAccountTempRepository.findByInstitutionTemp(institutionTemp);
            if (CollectionUtils.isNotEmpty(institutionTempBankAccountList)) {
                institutionBankAccountTempRepository.delete(institutionTempBankAccountList);
            }

        } else {
            InstitutionTemp institutionTemp = new InstitutionTemp();
            String[] properties = new String[]{
                    "name", "comment",
                    "contactName", "contactMobile", "contactEmail", "contactQq",
                    "checheName", "checheMobile", "checheEmail", "checheQq"
            };
            BeanUtil.copyPropertiesContain(tempInstitutionTemp, institutionTemp, properties);
            institutionTemp.setEnable(false);
            institutionTemp.setCreateTime(Calendar.getInstance().getTime());
            institutionTemp.setUpdateTime(Calendar.getInstance().getTime());
            institutionTemp.setOperator(internalUser);
            institutionTemp = institutionTempRepository.save(institutionTemp);
            tempInstitutionTemp.setId(institutionTemp.getId());
        }
        return tempInstitutionTemp;
    }

    public void saveRebate(InstitutionTemp tempInstitutionTemp) {
        List<InstitutionRebateTemp> oldRebateList = institutionRebateTempRepository.findByInstitutionTemp(tempInstitutionTemp);
        InternalUser internalUser = internalUserManageService.getCurrentInternalUser();
        List<InstitutionRebateTemp> newRebateList = tempInstitutionTemp.getRebateListTemp();
        int i = 0;
        while (i < oldRebateList.size()) {
            InstitutionRebateTemp oldInstitutionRebateTemp = oldRebateList.get(i);
            Boolean del = false;
            for (InstitutionRebateTemp newInstitutionRebateTemp : newRebateList) {
                newInstitutionRebateTemp.setInstitutionTemp(tempInstitutionTemp);
                if (oldInstitutionRebateTemp.getArea().getId().equals(newInstitutionRebateTemp.getArea().getId())
                        && oldInstitutionRebateTemp.getInsuranceCompany().getId().equals(newInstitutionRebateTemp.getInsuranceCompany().getId())) {
                    if (!oldInstitutionRebateTemp.getCommercialRebate().equals(newInstitutionRebateTemp.getCommercialRebate())
                            || !oldInstitutionRebateTemp.getCompulsoryRebate().equals(newInstitutionRebateTemp.getCompulsoryRebate())) {
                        newInstitutionRebateTemp.setId(oldInstitutionRebateTemp.getId());
                        newInstitutionRebateTemp.setCreateTime(oldInstitutionRebateTemp.getCreateTime());
                        newInstitutionRebateTemp.setUpdateTime(new Date());
                        newInstitutionRebateTemp.setOperator(internalUser);
                        institutionRebateTempRepository.save(newInstitutionRebateTemp);
                        institutionRebateHistoryTempService.save(newInstitutionRebateTemp, internalUser, InstitutionRebateHistory.OPERATION.UPD);
                    }
                    newRebateList.remove(newInstitutionRebateTemp);
                    del = true;
                    break;
                }
            }
            if (del) {
                oldRebateList.remove(oldInstitutionRebateTemp);
            } else {
                i++;
            }
        }
        for (InstitutionRebateTemp institutionRebateTemp : oldRebateList) {
            institutionRebateTempRepository.delete(institutionRebateTemp);
            institutionRebateHistoryTempService.save(institutionRebateTemp, internalUser, InstitutionRebateHistory.OPERATION.DEL);
        }
        for (InstitutionRebateTemp institutionRebateTemp : newRebateList) {
            institutionRebateTemp.setInstitutionTemp(tempInstitutionTemp);
            institutionRebateTemp.setCreateTime(new Date());
            institutionRebateTemp.setUpdateTime(new Date());
            institutionRebateTemp.setOperator(internalUser);
            institutionRebateTempRepository.save(institutionRebateTemp);
            institutionRebateHistoryTempService.save(institutionRebateTemp, internalUser, InstitutionRebateHistory.OPERATION.ADD);
        }
    }

    private void saveBankAccount(InstitutionTemp tempInstitutionTemp) {
        InternalUser internalUser = internalUserManageService.getCurrentInternalUser();
        if (CollectionUtils.isNotEmpty(tempInstitutionTemp.getbankAccountTempList())) {
            for (InstitutionBankAccountTemp tempBankAccountTemp : tempInstitutionTemp.getbankAccountTempList()) {
                tempBankAccountTemp.setInstitutionTemp(tempInstitutionTemp);
                tempBankAccountTemp.setCreateTime(Calendar.getInstance().getTime());
                tempBankAccountTemp.setUpdateTime(Calendar.getInstance().getTime());
                tempBankAccountTemp.setOperator(internalUser);
                institutionBankAccountTempRepository.save(tempBankAccountTemp);
            }
        }
    }

    public InstitutionTemp findById(Long id) {
        return institutionTempService.findById(id);
    }

    public boolean updateInstitutionTemp(Long institutionTempId, InstitutionTemp institutionTemp) {
        InstitutionTemp originalInstitutionTemp = findById(institutionTempId);
        AssertUtil.notNull(originalInstitutionTemp, "can not find institutionTemp by id -> " + institutionTempId);
        return this.addInstitutionTemp(institutionTemp);
    }

    public Map<String, Object> search(Integer currentPage, Integer pageSize, String keyword) {
        try {
            Map<String, Object> institutionTempMap = new HashMap<>();
            String condition = StringUtils.isNotBlank(keyword) ? keyword + "%" : "%";
            Integer totalElement = institutionTempRepository.countAssignedNameAndAreaName(condition);
            if (totalElement == null || totalElement == 0) {
                PageInfo pageInfo = new PageInfo();
                pageInfo.setTotalElements(0);
                pageInfo.setTotalPage(1);
                institutionTempMap.put("pageInfo", pageInfo);
                institutionTempMap.put("content", null);
                return institutionTempMap;
            }

            List<InstitutionTemp> institutionTempList = findByPage(currentPage, pageSize, keyword);
            List<InstitutionTemp> institutionTempDetailList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(institutionTempList)) {
                for (InstitutionTemp institutionTemp : institutionTempList) {
                    institutionTempDetailList.add(findById(institutionTemp.getId()));
                }
            }
            PageInfo pageInfo = new PageInfo();
            pageInfo.setTotalElements(totalElement);
            pageInfo.setTotalPage(totalElement.equals(pageSize) ? 1 : (totalElement / pageSize + 1));
            institutionTempMap.put("pageInfo", pageInfo);
            institutionTempMap.put("content", institutionTempDetailList);
            return institutionTempMap;
        } catch (Exception e) {
            logger.error("find institutionTemp info by page has error", e);
        }
        return null;
    }

    private List<InstitutionTemp> findByPage(Integer currentPage, Integer pageSize, String keyword) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<InstitutionTemp> query = cb.createQuery(InstitutionTemp.class);
        Root<InstitutionRebateTemp> institutionTempRebateRoot = query.from(InstitutionRebateTemp.class);
        Join<InstitutionRebateTemp, Area> areaJoin = institutionTempRebateRoot.join("area");

        Path<InstitutionTemp> institutionTempPath = institutionTempRebateRoot.<InstitutionTemp>get("institutionTemp");
        Path<String> namePath = institutionTempPath.get("name");
        Path<String> areaPath = areaJoin.get("name");

        List<Predicate> predicateList = new ArrayList<>();
        if (StringUtils.isNotBlank(keyword)) {
            predicateList.add(cb.or(
                    cb.like(namePath, keyword + "%"),
                    cb.like(areaPath, keyword + "%")
            ));
        }

        Predicate[] predicates = new Predicate[predicateList.size()];
        predicates = predicateList.toArray(predicates);
        query.select(institutionTempPath).distinct(true).where(predicates).orderBy(cb.desc(institutionTempPath.get("createTime")));
        TypedQuery<InstitutionTemp> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((currentPage - 1) * pageSize);
        typedQuery.setMaxResults(pageSize);
        return typedQuery.getResultList();
    }

    public boolean switchStatus(Long institutionTempId, Integer operationType) {
        try {
            InstitutionTemp institutionTemp = institutionTempRepository.findOne(institutionTempId);
            AssertUtil.notNull(institutionTemp, "can not find institutionTemp by id -> " + institutionTempId);
            // 启用或禁用出单机构
            institutionTemp.setEnable(operationType == 1);
            institutionTemp.setUpdateTime(Calendar.getInstance().getTime());
            institutionTemp.setOperator(internalUserManageService.getCurrentInternalUser());
            institutionTempRepository.save(institutionTemp);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public List<InstitutionTemp> listEnable() {
        return institutionTempService.listEnable();
    }

    public List<Area> listArea(Long institutionTempId) {
        return institutionTempService.listArea(institutionTempId);
    }

    public List<InsuranceCompany> listInsuranceCompany(Long institutionTempId) {
        return institutionTempService.listInsuranceCompany(institutionTempId);
    }

    public List<InstitutionRebateTemp> listInstitutionRebateTemp(Long areaId, Long insuranceCompanyId) {
        return institutionTempService.listInstitutionRebate(areaId, insuranceCompanyId);
    }

    public InstitutionRebateTemp findInstitutionRebateTemp(Long areaId, Long insuranceCompanyId, Long institutionTempId) {
        return institutionTempService.findInstitutionRebate(areaId, insuranceCompanyId, institutionTempId);
    }

    public InstitutionRebateTemp findInstitutionRebateTempById(Long id) {
        return institutionRebateTempRepository.findOne(id);
    }

    public boolean checkName(Long institutionTempId, String name) {
        return institutionTempService.checkName(institutionTempId, name);
    }

    public List<Area> findAreaByInstitutionTempAndEnable(Boolean enable) {
        return institutionRebateTempRepository.findAreaByInstitutionTempAndEnable(enable);
    }

}
