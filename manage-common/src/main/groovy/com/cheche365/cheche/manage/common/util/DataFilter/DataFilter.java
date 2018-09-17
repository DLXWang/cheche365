package com.cheche365.cheche.manage.common.util.DataFilter;

import com.cheche365.cheche.manage.common.model.PublicQuery;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * Created by yellow on 2017/11/4.
 */
public abstract class DataFilter {
    public static void process(Root<?> root, List<Predicate> predicateList, CriteriaBuilder cb, PublicQuery query, DataFilter... filters) {
        for (DataFilter dataFilter : filters) {
            dataFilter.bindConditions(root, predicateList, cb, query);
        }
    }

     abstract void bindConditions(Root<?> root, List<Predicate> predicateList, CriteriaBuilder cb, PublicQuery query);
}
