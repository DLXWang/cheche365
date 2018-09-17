package com.cheche365.cheche.core.service;

import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.InternalUserRoleRepository;
import com.cheche365.cheche.core.repository.UserRoleRepository;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liqiang on 3/17/15.
 */
@Service
@Transactional
public class SecurityService {

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private InternalUserRoleRepository internalUserRoleRepository;

    public void assign(User user, Role role) {
        UserRole dbUserRole = userRoleRepository.findFirstByUserAndRole(user, role);
        if(dbUserRole == null) {
            UserRole userRole = new UserRole();
            userRole.setUser(user);
            userRole.setRole(role);
            userRoleRepository.save(userRole); //FIXME: optimistic lock issue
        }
    }

    public List<Role> getRoles(InternalUser user) {
        List<Role> roles = new ArrayList<>();
        List<InternalUserRole> internalUserRoles = internalUserRoleRepository.findByInternalUser(user);
        if(CollectionUtils.isNotEmpty(internalUserRoles)) {
            for(InternalUserRole internalUserRole : internalUserRoles) {
                if(!internalUserRole.getRole().isDisable()) {
                    roles.add(internalUserRole.getRole());
                }
            }
        }
        return roles;
    }


    public boolean assign(InternalUser user, Role role) {
        InternalUserRole internalUserRole = internalUserRoleRepository.findFirstByInternalUserAndRole(user, role);
        if(internalUserRole == null) {
            internalUserRole = new InternalUserRole();
            internalUserRole.setRole(role);
            internalUserRole.setInternalUser(user);
            internalUserRoleRepository.save(internalUserRole);
        }
        return true;
    }

}
