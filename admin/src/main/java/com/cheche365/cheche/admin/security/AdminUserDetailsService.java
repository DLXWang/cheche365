package com.cheche365.cheche.admin.security;

import com.cheche365.cheche.manage.common.constants.ManageCommonConstants;
import com.cheche365.cheche.core.model.InternalUser;
import com.cheche365.cheche.core.model.Permission;
import com.cheche365.cheche.core.repository.InternalUserRepository;
import com.cheche365.cheche.core.repository.InternalUserRoleRepository;
import com.cheche365.cheche.manage.common.exception.NoPermissionLoginException;
import com.cheche365.cheche.manage.common.security.ManageCommonSecurityUser;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by sunhuazhong on 2015/4/28.
 */
public class AdminUserDetailsService implements UserDetailsService {

    @Autowired
    private InternalUserRepository internalUserRepository;

    @Autowired
    private InternalUserRoleRepository internalUserRoleRepository;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException, NoPermissionLoginException {
        // 验证用户是否存在
        InternalUser internalUser = internalUserRepository.findFirstByEmailAndDisable(email, false);
        if(internalUser == null) {
            throw new UsernameNotFoundException(ManageCommonConstants.LOGIN_ERROR_CODE);
        }

        List<GrantedAuthority> authorities = buildUserAuthority(internalUser);
        return buildUserForAuthentication(internalUser, authorities);
    }

    private ManageCommonSecurityUser buildUserForAuthentication(
            InternalUser internalUser, List<GrantedAuthority> authorities) {
        boolean enabled = true;
        boolean accountNonExpired = true;
        boolean credentialsNonExpired = true;
        boolean accountNonLocked = true;
        return new ManageCommonSecurityUser(internalUser.getEmail(), internalUser.getPassword(),
            enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities, internalUser);
    }

    private List<GrantedAuthority> buildUserAuthority(InternalUser internalUser) {
        // 验证用户是否被锁定
        if (internalUser.isLock()) {
            throw new NoPermissionLoginException(ManageCommonConstants.AUTHENTICATION_LOCKED_CODE);
        }

        Date changePasswordTime = internalUser.getChangePasswordTime();
        if (changePasswordTime != null) {
            Calendar changeCalendar = Calendar.getInstance();
            changeCalendar.setTime(changePasswordTime);
            changeCalendar.set(Calendar.HOUR_OF_DAY, 0);
            changeCalendar.set(Calendar.MILLISECOND, 0);
            changeCalendar.set(Calendar.MINUTE, 0);
            changeCalendar.set(Calendar.SECOND, 0);
            changeCalendar.add(Calendar.YEAR, 1);

            Calendar nowCalendar = Calendar.getInstance();
            nowCalendar.set(Calendar.HOUR_OF_DAY, 0);
            nowCalendar.set(Calendar.MILLISECOND, 0);
            nowCalendar.set(Calendar.MINUTE, 0);
            nowCalendar.set(Calendar.SECOND, 0);

            if (changeCalendar.compareTo(nowCalendar) < 0) {
                internalUser.setLock(true);
                internalUserRepository.save(internalUser);
                throw new NoPermissionLoginException(ManageCommonConstants.AUTHENTICATION_LOCKED_CODE);
            } else {
                changeCalendar.add(Calendar.DAY_OF_YEAR, -7);
                if (changeCalendar.compareTo(nowCalendar) < 1) {
                    stringRedisTemplate.opsForSet().add(ManageCommonConstants.RESET_PASSWORD_LOCK_KEY, internalUser.getEmail());
                } else {
                    stringRedisTemplate.opsForSet().remove(ManageCommonConstants.RESET_PASSWORD_LOCK_KEY, internalUser.getEmail());
                }
            }
        }
        // 验证用户是否有权限登录管理系统
        boolean enable = false;
        Permission adminPermission = Permission.Enum.ADMIN;
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        List<String> permissionCodeList = internalUserRoleRepository.listPermissionCodeByInternalUserAndPermission(
            internalUser.getId(), adminPermission.getCode() + "%");
        if(CollectionUtils.isNotEmpty(permissionCodeList)) {
            enable = true;
        }
        if(!enable) {
            throw new NoPermissionLoginException(ManageCommonConstants.AUTHENTICATION_ERROR_CODE);
        }
        permissionCodeList.forEach(permissionCode ->
            grantedAuthorities.add(new SimpleGrantedAuthority(ManageCommonConstants.ROLE_PREFIX + permissionCode)));
        return grantedAuthorities;
    }
}
