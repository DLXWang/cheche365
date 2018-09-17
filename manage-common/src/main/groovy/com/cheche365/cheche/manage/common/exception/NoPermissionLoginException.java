package com.cheche365.cheche.manage.common.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * Created by sunhuazhong on 2015/9/21.
 */
public class NoPermissionLoginException extends AuthenticationException {
    // ~ Constructors
    // ===================================================================================================

    /**
     * Constructs a <code>NoPermissionLoginException</code> with the specified message.
     *
     * @param msg the detail message.
     */
    public NoPermissionLoginException(String msg) {
        super(msg);
    }

    /**
     * Constructs a {@code NoPermissionLoginException} with the specified message and root
     * cause.
     *
     * @param msg the detail message.
     * @param t root cause
     */
    public NoPermissionLoginException(String msg, Throwable t) {
        super(msg, t);
    }
}
