package com.cheche365.cheche.manage.common.security;

import com.cheche365.cheche.core.model.InternalUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * Created by sunhuazhong on 2016/6/2.
 */
public class ManageCommonSecurityUser extends User {
    private InternalUser internalUser;

    public InternalUser getInternalUser() {
        return internalUser;
    }

    public void setInternalUser(InternalUser internalUser) {
        this.internalUser = internalUser;
    }

    public ManageCommonSecurityUser(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    public ManageCommonSecurityUser(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
    }

    public ManageCommonSecurityUser(String username, String password, boolean enabled,
                                    boolean accountNonExpired, boolean credentialsNonExpired,
                                    boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities,
                                    InternalUser internalUser) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.internalUser = internalUser;
    }
}
