package com.cheche365.cheche.ordercenter.security;

import com.cheche365.cheche.core.model.InternalUser;
import com.cheche365.cheche.core.model.Permission;
import com.cheche365.cheche.core.model.Role;
import com.cheche365.cheche.core.model.TelMarketer;
import com.cheche365.cheche.core.repository.InternalUserRepository;
import com.cheche365.cheche.core.repository.InternalUserRoleRepository;
import com.cheche365.cheche.core.service.InternalUserRoleService;
import com.cheche365.cheche.manage.common.model.InternalUserDataPermission;
import com.cheche365.cheche.manage.common.repository.InternalUserDataPermissionRepository;
import com.cheche365.cheche.manage.common.repository.TelMarketerRepository;
import com.cheche365.cheche.manage.common.security.ManageCommonSecurityUser;
import com.cheche365.cheche.ordercenter.constants.ExceptionConstants;
import com.cheche365.cheche.ordercenter.constants.OrderCenterConstants;
import com.cheche365.cheche.manage.common.exception.NoPermissionLoginException;
import com.cheche365.cheche.ordercenter.constants.OrderCenterRedisConstants;
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
public class OrderCenterUserDetailsService implements UserDetailsService {

    @Autowired
    private InternalUserRepository internalUserRepository;

    @Autowired
    private InternalUserRoleRepository internalUserRoleRepository;

    @Autowired
    private InternalUserDataPermissionRepository internalUserDataPermissionRepository;
    @Autowired
    private TelMarketerRepository telMarketerRepository;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");


    @Override
    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
        InternalUser internalUser = internalUserRepository.findFirstByEmailAndDisable(email, false);
        if(internalUser == null) {
            throw new UsernameNotFoundException(ExceptionConstants.LOGIN_ERROR_CODE);
        }
        List<GrantedAuthority> authorities = buildUserAuthority(internalUser);
        bindDataPermission(internalUser);
        bindTelMarketer(internalUser);
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
            throw new NoPermissionLoginException(ExceptionConstants.AUTHENTICATION_LOCKED_CODE);
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
                throw new NoPermissionLoginException(ExceptionConstants.AUTHENTICATION_LOCKED_CODE);
            } else {
                changeCalendar.add(Calendar.DAY_OF_YEAR, -7);
                if (changeCalendar.compareTo(nowCalendar) < 1) {
                    stringRedisTemplate.opsForSet().add(OrderCenterRedisConstants.RESET_PASSWORD_LOCK_KEY, internalUser.getEmail());
                } else {
                    stringRedisTemplate.opsForSet().remove(OrderCenterRedisConstants.RESET_PASSWORD_LOCK_KEY, internalUser.getEmail());
                }
            }
        }
        // 验证用户是否有权限登录出单系统
        boolean enable = false;
        Permission orderCenterPermission = Permission.Enum.ORDER_CENTER;
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        List<String> permissionCodeList = internalUserRoleRepository.listPermissionCodeByInternalUserAndPermission(
            internalUser.getId(), orderCenterPermission.getCode() + "%");
        if(CollectionUtils.isNotEmpty(permissionCodeList)) {
            enable = true;
        }
        if(!enable) {
            throw new NoPermissionLoginException(ExceptionConstants.AUTHENCATION_ERROR_CODE);
        }
        permissionCodeList.forEach(permissionCode ->
            grantedAuthorities.add(new SimpleGrantedAuthority(OrderCenterConstants.ROLE_PREFIX + permissionCode)));
        return grantedAuthorities;
    }

    private void bindDataPermission(InternalUser internalUser){
        List<InternalUserDataPermission> permissions=internalUserDataPermissionRepository.findByInternalUser(internalUser);
        internalUser.setDataPermission(permissions);
    }
    private void bindTelMarketer(InternalUser internalUser){
        internalUser.setTelMarketer( telMarketerRepository.findFirstByUser(internalUser.getId()));
    }
}
