package com.cheche365.cheche.manage.common.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.io.File;

/**
 * Created by wangfei on 2015/8/27.
 */
public class AssertUtil extends Assert {

    public static void exists(File file, String message) {
        if (!file.exists()) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void notExists(File file, String message) {
        if (file.exists()) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void notEmpty(String s, String message) {
        if (StringUtils.isBlank(s)) {
            throw new IllegalArgumentException(message);
        }
    }
}
