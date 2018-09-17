package com.cheche365.cheche.core.service;

import com.cheche365.cheche.core.model.InternalUser;

import java.util.List;

/**
 * Created by sunhuazhong on 2015/4/9.
 */
public interface IInternalUserService {
    public void save(InternalUser internalUser);

    public void delete(InternalUser internalUser);

    public void delete(Long id);

    public InternalUser getInternalUserById(long id);

    public InternalUser getInternalUserByEmail(String email);

    public InternalUser getSystemInternalUser();

    InternalUser getRandomInternalUser(List<InternalUser> internalUserList);

    List<InternalUser> listAllEnableCustomer();

    List<InternalUser> listAllCustomer();

    List<InternalUser> listAllEnableTelCommissioner();

    List<InternalUser> listAllEnableTelCommissionerExceptOne(InternalUser internalUser);

    List<InternalUser> listAllEnableCustomerExceptOne(InternalUser internalUser);

    InternalUser getRandomCustomer();

    InternalUser getRandomTOACustomer();

    InternalUser getRandomInputter();

    InternalUser getRandomAdmin();

    InternalUser findByEmailAndPassword(String email, String password);

    /**
     * 修改内部员工密码
     *
     * @param id
     * @param password
     */
    boolean modifyPassword(Long id, String password);

    List<InternalUser> listAllEnableInternal();

    List<InternalUser> listAllEnableInput();

    List<InternalUser> listAllEnableAdmin();

    boolean isSuperMan(InternalUser currentInternalUser);
}
