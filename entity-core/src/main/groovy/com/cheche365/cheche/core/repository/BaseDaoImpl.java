package com.cheche365.cheche.core.repository;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.internal.CriteriaImpl;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.Transformers;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by chenxiaozhe on 15-7-24.
 * 由于JPA方式,加载关联对象,可能有时候关联对象暂时是不需要的,会发出多余的sql,可以采用hibernate的方式,设置FetchMode,现提供hibernate方式操作数据库.
 * 在写Repository时,可同时使用hibernate和JPA两种方式。参见AreaRepository
 */
public class BaseDaoImpl<T> implements BaseDao<T> {

    @PersistenceContext
    private EntityManager entityManager;


    private Class<?> entityClass;


    public BaseDaoImpl() {
        entityClass = Reflections.getClassGeneralType(getClass());
    }


    public EntityManager getEntityManager() {
        return entityManager;
    }


    public Session getSession() {
        return (Session) getEntityManager().getDelegate();
    }


    public void flush() {
        getSession().flush();
    }


    public void clear() {
        getSession().clear();
    }

    // -------------- QL Query -------------- //


    public <E> Page<E> find(Page<E> page, String qlString, Object... parameter) {
        // get count
        if (!page.isDisabled() && !page.isNotCount()) {
            String countQlString = "select count(*) " + removeSelect(removeOrders(qlString));
            Query query = createQuery(countQlString, parameter);
            List<Object> list = query.list();
            if (list.size() > 0) {
                page.setTotalElements(Long.valueOf(list.get(0).toString()));
            } else {
                page.setTotalElements(list.size());
            }
            if (page.getTotalElements() < 1) {
                return page;
            }
        }
        // order by
        String ql = qlString;
        if (StringUtils.isNotBlank(page.getSort())) {
            ql += " order by " + page.getSort();
        }
        Query query = createQuery(ql, parameter);
        // set page
        if (!page.isDisabled()) {
            query.setFirstResult(page.getFirstResult());
            query.setMaxResults(page.getMaxResults());
        }
        page.setContent(query.list());
        return page;
    }

    public <E> List<E> find(String qlString, Object... parameter) {
        Query query = createQuery(qlString, parameter);
        return query.list();
    }


    public int update(String qlString, Object... parameter) {
        return createQuery(qlString, parameter).executeUpdate();
    }

    public Query createQuery(String qlString, Object... parameter) {
        Query query = getSession().createQuery(qlString);
        setParameter(query, parameter);
        return query;
    }

    // -------------- SQL Query -------------- //


    public <E> Page<E> findBySql(Page<E> page, String sqlString, Object... parameter) {
        return findBySql(page, sqlString, null, parameter);
    }

    public <E> Page<E> findBySql(Page<E> page, String sqlString, Class<?> resultClass, Object... parameter) {
        // get count
        if (!page.isDisabled() && !page.isNotCount()) {
            String countSqlString = "select count(*) " + removeSelect(removeOrders(sqlString));
            Query query = createSqlQuery(countSqlString, parameter);
            List<Object> list = query.list();
            if (list.size() > 0) {
                page.setTotalElements(Long.valueOf(list.get(0).toString()));
            } else {
                page.setTotalElements(list.size());
            }
            if (page.getTotalElements() < 1) {
                return page;
            }
        }
        // order by
        String sql = sqlString;
        if (StringUtils.isNotBlank(page.getSort())) {
            sql += " order by " + page.getSort();
        }
        SQLQuery query = createSqlQuery(sql, parameter);
        // set page
        if (!page.isDisabled()) {
            query.setFirstResult(page.getFirstResult());
            query.setMaxResults(page.getMaxResults());
        }
        setResultTransformer(query, resultClass);
        page.setContent(query.list());
        return page;
    }


    public <E> List<E> findBySql(String sqlString, Object... parameter) {
        return findBySql(sqlString, null, parameter);
    }


    public <E> List<E> findBySql(String sqlString, Class<?> resultClass, Object... parameter) {
        SQLQuery query = createSqlQuery(sqlString, parameter);
        setResultTransformer(query, resultClass);
        return query.list();
    }


    public int updateBySql(String sqlString, Object... parameter) {
        return createSqlQuery(sqlString, parameter).executeUpdate();
    }

    public SQLQuery createSqlQuery(String sqlString, Object... parameter) {
        SQLQuery query = getSession().createSQLQuery(sqlString);
        setParameter(query, parameter);
        return query;
    }


    // -------------- Criteria -------------- //


    public Page<T> find(Page<T> page) {
        return find(page, createDetachedCriteria());
    }


    public Page<T> find(Page<T> page, DetachedCriteria detachedCriteria) {
        return find(page, detachedCriteria, Criteria.DISTINCT_ROOT_ENTITY);
    }


    public Page<T> find(Page<T> page, DetachedCriteria detachedCriteria, ResultTransformer resultTransformer) {
        // get count
        if (!page.isDisabled() && !page.isNotCount()) {
            page.setTotalElements(count(detachedCriteria));
            if (page.getTotalElements() < 1) {
                return page;
            }
        }
        Criteria criteria = detachedCriteria.getExecutableCriteria(getSession());
        criteria.setResultTransformer(resultTransformer);
        // set page
        if (!page.isDisabled()) {
            criteria.setFirstResult(page.getFirstResult());
            criteria.setMaxResults(page.getMaxResults());
        }
        // order by
        if (StringUtils.isNotBlank(page.getSort())) {
            for (String order : StringUtils.split(page.getSort(), ",")) {
                String[] o = StringUtils.split(order, " ");
                if (o.length == 1) {
                    criteria.addOrder(Order.asc(o[0]));
                } else if (o.length == 2) {
                    if ("DESC".equals(o[1].toUpperCase())) {
                        criteria.addOrder(Order.desc(o[0]));
                    } else {
                        criteria.addOrder(Order.asc(o[0]));
                    }
                }
            }
        }
        page.setContent(criteria.list());
        return page;
    }


    public List<T> find(DetachedCriteria detachedCriteria) {
        return find(detachedCriteria, Criteria.DISTINCT_ROOT_ENTITY);
    }


    public List<T> find(DetachedCriteria detachedCriteria, ResultTransformer resultTransformer) {
        Criteria criteria = detachedCriteria.getExecutableCriteria(getSession());
        criteria.setResultTransformer(resultTransformer);
        return criteria.list();
    }


    public long count(DetachedCriteria detachedCriteria) {
        Criteria criteria = detachedCriteria.getExecutableCriteria(getSession());
        long totalCount = 0;
        try {
            // Get orders
            Field field = CriteriaImpl.class.getDeclaredField("orderEntries");
            field.setAccessible(true);
            List orderEntries = (List) field.get(criteria);
            // Remove orders
            field.set(criteria, new ArrayList());
            // Get count
            criteria.setProjection(Projections.rowCount());
            totalCount = Long.valueOf(criteria.uniqueResult().toString());
            // Clean count
            criteria.setProjection(null);
            // Restore orders
            field.set(criteria, orderEntries);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return totalCount;
    }


    public DetachedCriteria createDetachedCriteria(Criterion... criterion) {
        DetachedCriteria dc = DetachedCriteria.forClass(entityClass);
        for (Criterion c : criterion) {
            dc.add(c);
        }
        return dc;
    }

    // -------------- Query Tools -------------- //

    private void setResultTransformer(SQLQuery query, Class<?> resultClass) {
        if (resultClass != null) {
            if (resultClass == Map.class) {
                query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
            } else if (resultClass == List.class) {
                query.setResultTransformer(Transformers.TO_LIST);
            } else {
                query.addEntity(resultClass);
            }
        }
    }


    private void setParameter(Query query, Object... parameter) {
        if (parameter != null) {
            for (int i = 0; i < parameter.length; i++) {
                query.setParameter(i, parameter[i]);
            }
        }
    }


    private String removeSelect(String qlString) {
        int beginPos = qlString.toLowerCase().indexOf("from");
        return qlString.substring(beginPos);
    }

    private String removeOrders(String qlString) {
        Pattern p = Pattern.compile("order\\s*by[\\w|\\W|\\s|\\S]*", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(qlString);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, "");
        }
        m.appendTail(sb);
        return sb.toString();
    }
}
