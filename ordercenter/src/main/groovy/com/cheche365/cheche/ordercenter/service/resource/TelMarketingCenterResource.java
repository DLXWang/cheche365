package com.cheche365.cheche.ordercenter.service.resource;

import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.InternalUserRoleRepository;
import com.cheche365.cheche.core.repository.RoleRepository;
import com.cheche365.cheche.core.repository.TelMarketingCenterSourceRepository;
import com.cheche365.cheche.manage.common.model.TelMarketingCenter;
import com.cheche365.cheche.core.model.TelMarketingCenterSource;
import com.cheche365.cheche.manage.common.model.TelMarketingCenterStatus;
import com.cheche365.cheche.manage.common.service.BaseService;
import com.cheche365.cheche.ordercenter.constants.TelMarketingCenterType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by lyh on 2015/11/6.
 */
@Component
public class TelMarketingCenterResource extends BaseService<TelMarketingCenter, TelMarketingCenter> {
    @Autowired
    TelMarketingCenterSourceRepository telMarketingCenterSourceRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    InternalUserRoleRepository internalUserRoleRepository;

    public List<TelMarketingCenterStatus> listAllStatus() {
        return TelMarketingCenterStatus.Enum.ALLSTATUS;
    }

    public List<TelMarketingCenterSource> listAllSource() {
        return telMarketingCenterSourceRepository.findByEnable(true);
    }

    public List<InternalUser> listAllTelMarketingOperator() {
        List<InternalUser> userList  = new ArrayList<>();
        Role role = Role.Enum.INTERNAL_USER_ROLE_TEL_COMMISSIONER;
        List<InternalUserRole> userRoleList = internalUserRoleRepository.findByRole(role);
        userRoleList.forEach(internalUserRole -> userList.add(internalUserRole.getInternalUser()));
        return userList;
    }

    public List<TelMarketingCenterType> getTelMarketingCenterType() {
        return TelMarketingCenterType.Enum.getAllType();
    }
}
