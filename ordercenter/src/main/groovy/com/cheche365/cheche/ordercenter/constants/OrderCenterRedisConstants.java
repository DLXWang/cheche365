package com.cheche365.cheche.ordercenter.constants;

/**
 * Created by sunhuazhong on 2015/12/24.
 */
public class OrderCenterRedisConstants {
    public static final String RESET_PASSWORD_KEY = "internalUser:reset:password:set";

    public static final Integer PASSWORD_ERROR_COUNT = 5;

    public static final String RESET_PASSWORD_LOCK_KEY = "internalUser:reset:password:lock:set";

    public static final String USER_LOCK_KEY = "internalUser:operation:lock";
}
