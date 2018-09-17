package com.cheche365.cheche.manage.common.jsonfilter;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by zhengwei
 *
 * on 3/24/15.
 */
public class RestResponseEnvelope<T> {

    private T entity;

    public RestResponseEnvelope(T entity) {
        this.entity = entity;
    }

    public T getEntity() {
        return entity;
    }

}
