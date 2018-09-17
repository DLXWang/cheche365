package com.cheche365.cheche.admin.security;

import com.cheche365.cheche.common.util.HashUtils;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Created by sunhuazhong on 2015/4/28.
 */
public class AdminPasswordEncode implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        return HashUtils.getMD5(rawPassword.toString());
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (encodedPassword.equals(encode(rawPassword))) {
            return true;
        }
        return false;
    }
}
