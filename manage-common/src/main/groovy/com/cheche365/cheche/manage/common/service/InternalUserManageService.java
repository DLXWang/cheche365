package com.cheche365.cheche.manage.common.service;

import com.cheche365.cheche.core.model.InternalUser;
import com.cheche365.cheche.core.repository.InternalUserRepository;
import com.cheche365.cheche.core.service.IInternalUserService;
import com.cheche365.cheche.manage.common.constants.ManageCommonConstants;
import com.cheche365.cheche.manage.common.security.ManageCommonSecurityUser;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by guoweifu on 2015/9/14.
 */
@Service("internalUserManageService")
public class InternalUserManageService extends BaseService<InternalUser, Object> {

    @Autowired
    private InternalUserRepository internalUserRepository;

    @Autowired
    private IInternalUserService internalUserService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public boolean modifyPassword(Long id, String password) {
        boolean isSuccess = internalUserService.modifyPassword(id, password);
        if (isSuccess) {
            InternalUser internalUser = internalUserRepository.findOne(id);
            Boolean isMember = stringRedisTemplate.opsForSet().isMember(ManageCommonConstants.RESET_PASSWORD_KEY, internalUser.getEmail());
            if (isMember) {
                stringRedisTemplate.opsForSet().remove(ManageCommonConstants.RESET_PASSWORD_KEY, internalUser.getEmail());
            }
        }
        return isSuccess;
    }

    public InternalUser getCurrentInternalUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ManageCommonSecurityUser user = (ManageCommonSecurityUser) authentication.getPrincipal();
        return user.getInternalUser();
    }
    public InternalUser getCurrentInternalUserOrSystem() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return InternalUser.ENUM.SYSTEM;
        }
        ManageCommonSecurityUser user = (ManageCommonSecurityUser) authentication.getPrincipal();
        return user.getInternalUser();
    }

    public List<String> listAuthority() {
        List<String> permissionCodeList = new ArrayList<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ManageCommonSecurityUser user = (ManageCommonSecurityUser) authentication.getPrincipal();
        Collection<GrantedAuthority> grantedAuthorityList = user.getAuthorities();
        if (!CollectionUtils.isEmpty(grantedAuthorityList)) {
            grantedAuthorityList.stream().forEach(grantedAuthority ->
                    permissionCodeList.add(grantedAuthority.getAuthority().substring(ManageCommonConstants.ROLE_PREFIX.length())));
        }
        return permissionCodeList;
    }
}
