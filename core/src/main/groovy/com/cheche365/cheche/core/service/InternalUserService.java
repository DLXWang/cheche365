package com.cheche365.cheche.core.service;

import com.cheche365.cheche.common.util.HashUtils;
import com.cheche365.cheche.core.model.InternalUser;
import com.cheche365.cheche.core.model.InternalUserRole;
import com.cheche365.cheche.core.model.Role;
import com.cheche365.cheche.core.repository.InternalUserRepository;
import com.cheche365.cheche.core.repository.InternalUserRoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zhengwei on 3/23/15.
 */

@Service
@Transactional
public class InternalUserService implements IInternalUserService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final String EMAIL_SUPERMAN = "superman@cheche365.com";

    @Autowired
    private InternalUserRepository internalUserRepository;

    @Autowired
    private InternalUserRoleRepository internalUserRoleRepository;

    @Override
    public void save(InternalUser internalUser) {
        internalUserRepository.save(internalUser);
    }

    @Override
    public void delete(InternalUser internalUser) {
        internalUserRepository.delete(internalUser);
    }

    @Override
    public void delete(Long id) {
        internalUserRepository.delete(id);
    }

    @Override
    public InternalUser getInternalUserById(long id) {
        return this.internalUserRepository.findOne(id);
    }

    @Override
    public InternalUser getInternalUserByEmail(String email) {
        return internalUserRepository.findFirstByEmail(email);
    }

    @Override
    public InternalUser getSystemInternalUser() {
        return internalUserRepository.findFirstByName("system");
    }

    @Override
    public List<InternalUser> listAllEnableCustomer() {
        List<InternalUser> internalUserList = this.listAllCustomer();

        List<InternalUser> enableInternalUserList = new ArrayList<>();
        internalUserList.forEach(internalUser -> {
            if (!internalUser.isDisable() && internalUser.getInternalUserType() == 1)
                enableInternalUserList.add(internalUser);
        });

        return enableInternalUserList;
    }

    @Override
    public InternalUser getRandomInternalUser(List<InternalUser> internalUserList) {
        if (internalUserList == null || internalUserList.isEmpty()) {
            return null;
        }
        int index = (int) (Math.random() * internalUserList.size());
        return internalUserList.get(index);
    }

    @Override
    public List<InternalUser> listAllCustomer() {
        Role customerRole = Role.Enum.INTERNAL_USER_ROLE_CUSTOMER;
        List<InternalUserRole> customerInternalUserRoles = internalUserRoleRepository.findByRole(customerRole);

        List<InternalUser> resultList = new ArrayList<>();
        for (InternalUserRole internalUserRole : customerInternalUserRoles) {
            InternalUser internalUser = internalUserRole.getInternalUser();
            if (internalUser != null && !resultList.contains(internalUser)) {
                resultList.add(internalUser);
            }
        }
        return resultList;
    }

    /**
     * 获取所有可用内勤
     *
     * @return
     */
    @Override
    public List<InternalUser> listAllEnableInternal() {
        return internalUserRoleRepository.getInternalUsersByRoleAndDisable(Role.Enum.INTERNAL_USER_ROLE_INTERNAL, false);
    }

    /**
     * 获取所有可用录单员
     *
     * @return
     */
    @Override
    public List<InternalUser> listAllEnableInput() {
        return internalUserRoleRepository.getInternalUsersByRoleAndDisable(Role.Enum.INTERNAL_USER_ROLE_INPUT, false);
    }

    /**
     * 获取所有可用管理员
     *
     * @return
     */
    @Override
    public List<InternalUser> listAllEnableAdmin() {
        return internalUserRoleRepository.getInternalUsersByRoleAndDisable(Role.Enum.INTERNAL_USER_ROLE_ADMIN, false);
    }

    /**
     * 获取所有可用电话专员
     *
     * @return
     */
    @Override
    public List<InternalUser> listAllEnableTelCommissioner() {
        return internalUserRoleRepository.getInternalUsersByRoleAndDisable(Role.Enum.INTERNAL_USER_ROLE_TEL_COMMISSIONER, false);
    }

    /**
     * 获取所有可用电话主管
     *
     * @return
     */
    public List<InternalUser> listAllEnableTelMaster() {
        return internalUserRoleRepository.getInternalUsersByRoleAndDisable(Role.Enum.INTERNAL_USER_ROLE_TEL_MASTER, false);
    }

    /**
     * 获取所有可用电话主管及专员
     *
     * @return
     */
    public List<InternalUser> listAllEnableTelemarketer() {
        List telemarketer = listAllEnableTelCommissioner();
        telemarketer.addAll(listAllEnableTelMaster());
        return telemarketer;
    }

    @Override
    public List<InternalUser> listAllEnableTelCommissionerExceptOne(InternalUser internalUser) {
        List<InternalUser> viewDataList = this.listAllEnableTelCommissioner();
        List<InternalUser> dataList = new ArrayList<>();
        if (internalUser == null)
            return viewDataList;
        viewDataList.forEach(userViewData -> {
            if (!userViewData.getId().equals(internalUser.getId()))
                dataList.add(userViewData);
        });
        return dataList;
    }

    @Override
    public List<InternalUser> listAllEnableCustomerExceptOne(InternalUser internalUser) {
        List<InternalUser> viewDataList = this.listAllEnableCustomer();
        List<InternalUser> dataList = new ArrayList<>();

        if (internalUser == null)
            return viewDataList;

        viewDataList.forEach(userViewData -> {
            if (!userViewData.getId().equals(internalUser.getId()))
                dataList.add(userViewData);
        });

        return dataList;
    }

    @Override
    public InternalUser getRandomCustomer() {
        List<InternalUser> internalUserList = this.listAllEnableCustomer();
        return this.getRandomInternalUser(internalUserList);
    }

    @Override
    public InternalUser getRandomInputter() {
        List<InternalUser> internalUserList = this.listAllEnableInput();
        return this.getRandomInternalUser(internalUserList);
    }

    @Override
    public InternalUser getRandomAdmin() {
        List<InternalUser> internalUserList = this.listAllEnableAdmin();
        return this.getRandomInternalUser(internalUserList);
    }

    public InternalUser findByEmailAndPassword(String email, String password) {
        return internalUserRepository.findFirstByEmailAndPasswordAndDisable(email, password, false);
    }

    @Override
    public boolean modifyPassword(Long id, String password) {
        try {
            InternalUser internalUser = internalUserRepository.findOne(id);
            internalUser.setPassword(HashUtils.getMD5(password));
            internalUser.setLock(false);
            internalUser.setChangePasswordTime(new Date());
            internalUserRepository.save(internalUser);
            return true;
        } catch (Exception e) {
            logger.error("update internal user password has error", e);
            return false;
        }
    }

    /**
     * 是否为超级用户
     *
     * @param internalUser
     * @return
     */
    public boolean isSuperMan(InternalUser internalUser) {
        return null != internalUser && EMAIL_SUPERMAN.equals(internalUser.getEmail());
    }

    //根据id 查找内部用户
    public InternalUser findByID(Long id) {
        return internalUserRepository.findOne(id);
    }

    //TOA出单员
    @Override
    public InternalUser getRandomTOACustomer() {
        return findByID(74l);
    }

}
