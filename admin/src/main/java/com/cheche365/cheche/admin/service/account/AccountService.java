package com.cheche365.cheche.admin.service.account;

import com.cheche365.cheche.manage.common.service.BaseService;
import com.cheche365.cheche.manage.common.service.InternalUserManageService;
import com.cheche365.cheche.core.model.InternalUser;
import com.cheche365.cheche.core.repository.InternalUserRepository;
import com.cheche365.cheche.core.service.InternalUserRoleService;
import com.cheche365.cheche.core.service.InternalUserService;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liyh on 2015/9/9
 */
@Service
@Transactional
public class AccountService extends BaseService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private InternalUserRepository internalUserRepository;

    @Autowired
    private InternalUserRoleService internalUserRoleService;

    @Autowired
    private InternalUserService internalUserService;

    @Autowired
    private InternalUserManageService internalUserManageService;

    public boolean addOrUpdate(InternalUser internalUser, String roleIds) {
        try {
            // 保存内部用户
            internalUser = internalUserRepository.save(internalUser);
            createInternalRoles(internalUser.getId(), roleIds);
            return true;
        } catch (Exception ex) {
            logger.error("add account has error", ex);
            return false;
        }
    }

    public boolean isEmailExist(InternalUser internalUser) {
        // 判断该邮箱是否已被使用
        if (internalUserRepository.findFirstByEmail(internalUser.getEmail()) != null) {
            return false;
        }
        return true;
    }

    public InternalUser findOne(Long id) {
        return internalUserRepository.findOne(id);
    }


    public Page<InternalUser> list(Integer currentPage, Integer pageSize, String keyword, Integer userType) {
        try {
            return this.findBySpecAndPaginate(keyword,
                this.buildPageable(currentPage, pageSize), userType);
        } catch (Exception e) {
            logger.error("find account info by page has error", e);
            return null;
        }
    }

    /**
     * 构建分页信息
     *
     * @param currentPage 当前页面
     * @param pageSize    每页显示数
     * @return Pageable
     */
    private Pageable buildPageable(int currentPage, int pageSize) {
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        return new PageRequest(currentPage - 1, pageSize, sort);
    }

    /**
     * 分页查询
     *
     * @param keyword  关键字
     * @param pageable 分页信息
     * @return Page<Partner>
     */
    private Page<InternalUser> findBySpecAndPaginate(String keyword, Pageable pageable, Integer userType) throws Exception {
        return internalUserRepository.findAll(new Specification<InternalUser>() {
            @Override
            public Predicate toPredicate(Root<InternalUser> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                CriteriaQuery<InternalUser> criteriaQuery = cb.createQuery(InternalUser.class);

                //条件构造
                List<Predicate> predicateList = new ArrayList<>();
                predicateList.add(cb.equal(root.get("internalUserType"), userType));

                Path<String> emailPath = root.get("email");
                if (StringUtils.isNotBlank(keyword)) {
                    Path<String> namePath = root.get("name");
                    Path<String> mobilePath = root.get("mobile");
                    predicateList.add(cb.or(
                        cb.like(namePath, keyword + "%"),
                        cb.like(emailPath, keyword + "%"),
                        cb.like(mobilePath, keyword + "%")
                    ));
                }

                Predicate predicate = cb.notEqual(emailPath, InternalUserService.EMAIL_SUPERMAN);
                predicateList.add(predicate);
                //超级管理员能看到超级用户
                if (internalUserService.isSuperMan(internalUserManageService.getCurrentInternalUser())) {
                    predicateList.remove(predicate);
                }

                Predicate[] predicates = new Predicate[predicateList.size()];
                predicates = predicateList.toArray(predicates);
                return criteriaQuery.where(predicates).getRestriction();
            }
        }, pageable);
    }

    public boolean updateStatus(Long id, boolean status) {
        try {
            InternalUser internalUser = internalUserRepository.findOne(id);
            internalUser.setDisable(!status);
            internalUserRepository.save(internalUser);
            return true;
        } catch (Exception e) {
            logger.error("update status have error!", e);
            return false;
        }
    }

    private void createInternalRoles(Long internalUserId, String roleIds) throws Exception {
        InternalUser user = internalUserRepository.findOne(internalUserId);
        internalUserRoleService.deleteAllInternalUserRoles(user);
        internalUserRoleService.saveByStrRoleIdList(user, roleIds);
    }
}
