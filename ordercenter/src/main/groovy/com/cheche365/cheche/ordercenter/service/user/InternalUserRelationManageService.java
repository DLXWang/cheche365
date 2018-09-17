package com.cheche365.cheche.ordercenter.service.user;

import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.InternalUserRelationRepository;
import com.cheche365.cheche.core.repository.InternalUserRepository;
import com.cheche365.cheche.manage.common.service.BaseService;
import com.cheche365.cheche.manage.common.web.model.ModelAndViewResult;
import com.cheche365.cheche.manage.common.web.model.PageViewModel;
import com.cheche365.cheche.ordercenter.web.model.user.InternalUserRelationData;
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
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import java.util.*;

/**
 * Created by sunhuazhong on 2015/7/9.
 */
@Service
@Transactional
public class InternalUserRelationManageService extends BaseService<InternalUserRelation, InternalUserRelationData> implements IInternalUserRelationManageService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private InternalUserRepository internalUserRepository;

    @Autowired
    private InternalUserRelationRepository internalUserRelationRepository;

    /**
     * bind user
     * @param customerId
     * @param internalId
     * @param externalId
     * @return
     */
    @Override
    public boolean add(Long customerId, Long internalId, Long externalId) {
        try {
            InternalUser customer = internalUserRepository.findOne(customerId);
            InternalUser internal = internalUserRepository.findOne(internalId);
            InternalUser external = internalUserRepository.findOne(externalId);
            if(internalUserRelationRepository.findFirstByCustomerUserAndInternalUserAndExternalUser(
                customer, internal, external) == null) {
                InternalUserRelation internalUserRelation = new InternalUserRelation();
                internalUserRelation.setCustomerUser(customer);
                internalUserRelation.setInternalUser(internal);
                internalUserRelation.setExternalUser(external);
                internalUserRelationRepository.save(internalUserRelation);
            }
            return true;
        } catch (Exception e) {
            logger.error("bind user has error", e);
        }

        return false;
    }

    @Override
    public ModelAndViewResult update(InternalUserRelationData internalUserRelationData) {
        ModelAndViewResult result = new ModelAndViewResult();
        try {
            InternalUserRelation originalInternalUserRelation = internalUserRelationRepository.findOne(internalUserRelationData.getId());
            if(originalInternalUserRelation == null) {
                result.setResult(ModelAndViewResult.RESULT_FAIL);
                result.setMessage("该用户组不存在。");
                return result;
            }
            InternalUserRelation tempInternalUserRelation  = internalUserRelationRepository
                .findFirstByCustomerUser_IdAndInternalUser_IdAndExternalUser_Id(
                    internalUserRelationData.getCustomerUserId(),
                    internalUserRelationData.getInternalUserId(),
                    internalUserRelationData.getExternalUserId());
            if(tempInternalUserRelation != null) {
                result.setResult(ModelAndViewResult.RESULT_FAIL);
                result.setMessage("该用户组配置已存在。");
                return result;
            }
            // 保存用户组
            internalUserRelationRepository.save(this.createInternalUserRelation(internalUserRelationData));

            result.setResult(ModelAndViewResult.RESULT_SUCCESS);
            result.setMessage("更新成功。");
            return result;
        } catch (Exception e) {
            logger.error("update internal user has error", e);
            result.setResult(ModelAndViewResult.RESULT_FAIL);
            result.setMessage("系统异常。");
            return result;
        }
    }

    /**
     * create internalUser db model
     * @param viewData
     * @return
     * @throws Exception
     */
    private InternalUserRelation createInternalUserRelation(InternalUserRelationData viewData) throws Exception {
        InternalUserRelation internalUserRelation = new InternalUserRelation();
        if (viewData.getId() != null) {
            internalUserRelation = internalUserRelationRepository.findOne(viewData.getId());
        }
        internalUserRelation.setCustomerUser(internalUserRepository.findOne(viewData.getCustomerUserId()));
        internalUserRelation.setInternalUser(internalUserRepository.findOne(viewData.getInternalUserId()));
        internalUserRelation.setExternalUser(internalUserRepository.findOne(viewData.getExternalUserId()));
        return internalUserRelation;
    }

    @Override
    public ModelAndViewResult delete(Long id) {
        ModelAndViewResult result = new ModelAndViewResult();
        try {
            if(id == null) {
                result.setResult(ModelAndViewResult.RESULT_FAIL);
                result.setMessage("无效参数。");
                return result;
            }
            InternalUserRelation internalUserRelation = internalUserRelationRepository.findOne(id);
            if(internalUserRelation == null) {
                result.setResult(ModelAndViewResult.RESULT_FAIL);
                result.setMessage("该用户组不存在。");
                return result;
            }
            internalUserRelationRepository.delete(internalUserRelation);

            result.setResult(ModelAndViewResult.RESULT_SUCCESS);
            result.setMessage("删除成功。");
        } catch (Exception e) {
            logger.error("delete internal user relation by id has error", e);
            result.setResult(ModelAndViewResult.RESULT_FAIL);
            result.setMessage("系统异常。");
        }
        return result;
    }

    @Override
    public ModelAndViewResult findOne(Long id) {
        ModelAndViewResult result = new ModelAndViewResult();
        try {
            InternalUserRelation internalUserRelation = internalUserRelationRepository.findOne(id);
            InternalUserRelationData viewData =  this.createViewData(internalUserRelation);
            result.setResult(ModelAndViewResult.RESULT_SUCCESS);
            Map<String, Object> objects = new HashMap<>();
            objects.put("model", viewData);
            result.setObjectMap(objects);
        } catch (Exception e) {
            logger.error("find internal user relation by id has error", e);
            result.setResult(ModelAndViewResult.RESULT_FAIL);
            result.setMessage("系统异常。");
        }
        return result;
    }

    @Override
    public PageViewModel<InternalUserRelationData> listInternalUserRelation(Integer currentPage, Integer pageSize, String keyword) {
        try {
            Page<InternalUserRelation> internalUserRelationPage = this.findBySpecAndPaginate(keyword,
                this.buildPageable(currentPage, pageSize));
            return super.createResult(internalUserRelationPage);
        } catch (Exception e) {
            logger.error("list internal user relation has error", e);
        }
        return null;
    }

    /**
     * list internal user relation by page
     * @param keyword
     * @param pageable
     * @return
     */
    private Page<InternalUserRelation> findBySpecAndPaginate(String keyword, Pageable pageable) throws Exception {
        return internalUserRelationRepository.findAll(new Specification<InternalUserRelation>() {
            @Override
            public Predicate toPredicate(Root<InternalUserRelation> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                CriteriaQuery<InternalUserRelation> criteriaQuery = cb.createQuery(InternalUserRelation.class);

                //条件构造
                List<Predicate> predicateList = new ArrayList<>();
                if (StringUtils.isNotBlank(keyword)) {
                    Path<String> customerUserNamePath = root.get("customerUser").get("name");
                    Path<String> internalUserNamePath = root.get("internalUser").get("name");
                    Path<String> externalUserNamePath = root.get("externalUser").get("name");
                    predicateList.add(cb.or(
                        cb.like(customerUserNamePath, keyword + "%"),
                        cb.like(internalUserNamePath, keyword + "%"),
                        cb.like(externalUserNamePath, keyword + "%")
                    ));
                }

                Predicate[] predicates = new Predicate[predicateList.size()];
                predicates = predicateList.toArray(predicates);
                return criteriaQuery.where(predicates).getRestriction();
            }
        }, pageable);
    }

    /**
     * create return view body list
     * @param internalUserRelationList
     * @return
     * @throws Exception
     */
    @Override
    public List<InternalUserRelationData> createList(List<InternalUserRelation> internalUserRelationList) throws Exception {
        List<InternalUserRelationData> internalUserRElationDataList = new ArrayList<>();
        for(InternalUserRelation internalUserRelation : internalUserRelationList){
            internalUserRElationDataList.add(this.createViewData(internalUserRelation));
        }
        return internalUserRElationDataList;
    }

    /**
     * organize InternalUserRelationData for show
     * @param internalUserRelation
     * @return
     * @throws Exception
     */
    private InternalUserRelationData createViewData(InternalUserRelation internalUserRelation) throws Exception {
        InternalUserRelationData viewData = new InternalUserRelationData();
        viewData.setId(internalUserRelation.getId());
        viewData.setCustomerUserId(internalUserRelation.getCustomerUser().getId());
        viewData.setCustomerUserName(internalUserRelation.getCustomerUser().getName());
        viewData.setInternalUserId(internalUserRelation.getInternalUser().getId());
        viewData.setInternalUserName(internalUserRelation.getInternalUser().getName());
        viewData.setExternalUserId(internalUserRelation.getExternalUser().getId());
        viewData.setExternalUserName(internalUserRelation.getExternalUser().getName());
        return viewData;
    }

    /**
     * create pageable
     * @param currentPage
     * @param pageSize
     * @return
     */
    private Pageable buildPageable(Integer currentPage, Integer pageSize) throws Exception {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        return new PageRequest(currentPage-1, pageSize, sort);
    }
}
