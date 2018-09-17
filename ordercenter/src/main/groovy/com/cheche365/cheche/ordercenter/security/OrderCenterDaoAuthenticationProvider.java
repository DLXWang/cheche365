package com.cheche365.cheche.ordercenter.security;

import com.cheche365.cheche.manage.common.exception.NoPermissionLoginException;
import com.cheche365.cheche.ordercenter.constants.ExceptionConstants;
import com.cheche365.cheche.ordercenter.constants.OrderCenterRedisConstants;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Assert;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sunhuazhong on 2015/12/24.
 */
public class OrderCenterDaoAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    private PasswordEncoder passwordEncoder;

    private String userNotFoundEncodedPassword;

    private UserDetailsService userDetailsService;

    private StringRedisTemplate stringRedisTemplate;

    public OrderCenterDaoAuthenticationProvider() {
        setPasswordEncoder(new OrderCenterPasswordEncode());
    }

    @SuppressWarnings("deprecation")
    protected void additionalAuthenticationChecks(UserDetails userDetails,
                                                  UsernamePasswordAuthenticationToken authentication)
        throws AuthenticationException {

        if (authentication.getCredentials() == null) {
            logger.debug("Authentication failed: no credentials provided");

            throw new BadCredentialsException(messages.getMessage(
                "AbstractUserDetailsAuthenticationProvider.badCredentials",
                "Bad credentials"));
        }

        String presentedPassword = authentication.getCredentials().toString();

        // 检查是否超出错误次数
        Object t = stringRedisTemplate.opsForHash().get(OrderCenterRedisConstants.USER_LOCK_KEY, userDetails.getUsername());
        Integer count = t == null ? OrderCenterRedisConstants.PASSWORD_ERROR_COUNT : Integer.valueOf(t.toString());
        if (count == 0) {
            // 用户已锁定
            throw new NoPermissionLoginException(ExceptionConstants.AUTHENTICATION_LOCKED_CODE);
        }

        if (!passwordEncoder.matches(presentedPassword, userDetails.getPassword())) {
            logger.debug("Authentication failed: password does not match stored value");

//            throw new BadCredentialsException(messages.getMessage(
//                "AbstractUserDetailsAuthenticationProvider.badCredentials",
//                "Bad credentials"));
            // 记录登录错误次数
            count -= 1;
            stringRedisTemplate.opsForHash().put(OrderCenterRedisConstants.USER_LOCK_KEY, userDetails.getUsername(), count.toString());

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 24);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 0);
            stringRedisTemplate.expireAt(OrderCenterRedisConstants.USER_LOCK_KEY, calendar.getTime());
            if (count == 0) {
                throw new NoPermissionLoginException(ExceptionConstants.AUTHENTICATION_LOCKED_CODE);
            } else {
                throw new BadCredentialsException("密码错误，还剩" + count + "次机会");
            }
        } else {
            stringRedisTemplate.opsForHash().delete(OrderCenterRedisConstants.USER_LOCK_KEY, userDetails.getUsername());
        }

        if (!checkSafePassword(presentedPassword)) {
            stringRedisTemplate.opsForSet().add(OrderCenterRedisConstants.RESET_PASSWORD_KEY, authentication.getPrincipal().toString());
        }
    }

    private boolean checkSafePassword(String password) {
        if (password.matches("\\w+")) {
            Pattern p1 = Pattern.compile("[a-z]+");
            Pattern p2 = Pattern.compile("[A-Z]+");
            Pattern p3 = Pattern.compile("[0-9]+");
            Matcher m = p1.matcher(password);
            if (!m.find())
                return false;
            else {
                m.reset().usePattern(p2);
                if (!m.find())
                    return false;
                else {
                    m.reset().usePattern(p3);
                    if (!m.find())
                        return false;
                    else {
                        return true;
                    }
                }
            }
        } else {
            return false;
        }
    }

    protected void doAfterPropertiesSet() throws Exception {
        Assert.notNull(this.userDetailsService, "A UserDetailsService must be set");
    }

    protected final UserDetails retrieveUser(String username,
                                             UsernamePasswordAuthenticationToken authentication)
        throws AuthenticationException {
        UserDetails loadedUser;

        try {
            loadedUser = this.getUserDetailsService().loadUserByUsername(username);
        } catch (UsernameNotFoundException notFound) {
            if (authentication.getCredentials() != null) {
                String presentedPassword = authentication.getCredentials().toString();
                passwordEncoder.matches(presentedPassword, userNotFoundEncodedPassword);
            }
            throw notFound;
        } catch (Exception repositoryProblem) {
            throw new InternalAuthenticationServiceException(
                repositoryProblem.getMessage(), repositoryProblem);
        }

        if (loadedUser == null) {
            throw new InternalAuthenticationServiceException(
                "UserDetailsService returned null, which is an interface contract violation");
        }
        return loadedUser;
    }

    /**
     * Sets the PasswordEncoder instance to be used to encode and validate passwords. If
     * not set, the password will be compared as plain text.
     * <p>
     * For systems which are already using salted password which are encoded with a
     * previous release, the encoder should be of type
     * {@code org.springframework.security.authentication.encoding.PasswordEncoder}.
     * Otherwise, the recommended approach is to use
     * {@code org.springframework.security.crypto.password.PasswordEncoder}.
     *
     * @param passwordEncoder
     *         must be an instance of one of the {@code PasswordEncoder}
     *         types.
     */
    public void setPasswordEncoder(Object passwordEncoder) {
        Assert.notNull(passwordEncoder, "passwordEncoder cannot be null");
        setPasswordEncoder((PasswordEncoder) passwordEncoder);
    }

    private void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        Assert.notNull(passwordEncoder, "passwordEncoder cannot be null");

        this.userNotFoundEncodedPassword = passwordEncoder.encode("12345678");
        this.passwordEncoder = passwordEncoder;
    }

    protected PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }

    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    protected UserDetailsService getUserDetailsService() {
        return userDetailsService;
    }

    public StringRedisTemplate getStringRedisTemplate() {
        return stringRedisTemplate;
    }

    public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }
}
