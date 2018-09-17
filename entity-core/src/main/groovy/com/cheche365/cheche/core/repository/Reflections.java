package com.cheche365.cheche.core.repository;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by chenxiaozhe on 15-7-24.
 */
public class Reflections {

    public static <T> Class<T> getClassGeneralType(final Class clazz) {
        return getClassGeneralType(clazz, 0);
    }

    public static Class getClassGeneralType(final Class clazz, final int index) {
        Type genType = clazz.getGenericSuperclass();
        if (!(genType instanceof ParameterizedType)) {
            return Object.class;
        }
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        if (index >= params.length || index < 0) {
            return Object.class;
        }
        if (!(params[index] instanceof Class)) {
            return Object.class;
        }
        return (Class) params[index];
    }
}
