package com.cheche365.cheche.operationcenter.service.resource;

import com.cheche365.cheche.core.model.InternalUser;
import com.cheche365.cheche.core.model.InternalUserViewData;
import com.cheche365.cheche.core.repository.InternalUserRepository;
import com.cheche365.cheche.core.service.IInternalUserService;
import com.cheche365.cheche.core.util.BeanUtil;
import com.cheche365.cheche.manage.common.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangfei on 2015/6/11.
 */
@Component
public class InternalUserResource extends BaseService<InternalUser, InternalUser> {

    @Autowired
    private IInternalUserService internalUserService;

    @Autowired
    private InternalUserRepository internalUserRepository;

    public List<InternalUser> listAll() {
        return super.getAll(internalUserRepository);
    }

    public List<InternalUserViewData> createViewData(List<InternalUser> internalUserList) {
        if (internalUserList == null)
            return null;

        List<InternalUserViewData> internalUserViewDataList = new ArrayList<>();
        internalUserList.forEach(internalUser ->{
            InternalUserViewData viewData = new InternalUserViewData();
            String[] contains = new String[]{"id", "email", "name", "mobile", "password"};
            BeanUtil.copyPropertiesContain(internalUser, viewData, contains);
            internalUserViewDataList.add(viewData);
        });

        return internalUserViewDataList;
    }

    public List<InternalUser> findEnableInternalUsers() {
        return internalUserRepository.findByDisable(false);
    }

    public List<InternalUser> listAllCustomer() {
        return internalUserService.listAllCustomer();
    }

    public List<InternalUser> listAllEnableCustomer() {
        return internalUserService.listAllEnableCustomer();
    }

    public List<InternalUser> listAllEnableCustomerExceptOne(InternalUser internalUser) {
        return internalUserService.listAllEnableCustomerExceptOne(internalUser);
    }

    public InternalUser getRandomInternalUser(List<InternalUser> internalUserList) {
        return internalUserService.getRandomInternalUser(internalUserList);
    }

}
