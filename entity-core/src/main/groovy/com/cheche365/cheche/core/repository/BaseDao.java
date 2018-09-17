package com.cheche365.cheche.core.repository;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.transform.ResultTransformer;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * Created by chenxiaozhe on 15-7-24.
 * 由于JPA方式,加载关联对象,可能有时候关联对象暂时是不需要的,会发出多余的sql,可以采用hibernate的方式,设置FetchMode,现提供hibernate方式操作数据库.
 * 在写Repository时,可同时使用hibernate和JPA两种方式。参见AreaRepository
 */
public interface BaseDao<T> {

    public EntityManager getEntityManager();

    public Session getSession();


    public void flush();


    public void clear();

    // -------------- HQL Query -------------- //


    public <E> Page<E> find(Page<E> page, String qlString, Object... parameter);


    public <E> List<E> find(String qlString, Object... parameter);


    public int update(String qlString, Object... parameter);


    public Query createQuery(String qlString, Object... parameter);

    // -------------- SQL Query -------------- //


    public <E> Page<E> findBySql(Page<E> page, String sqlString, Object... parameter);


    public <E> Page<E> findBySql(Page<E> page, String sqlString, Class<?> resultClass, Object... parameter);

    public <E> List<E> findBySql(String sqlString, Object... parameter);

    public <E> List<E> findBySql(String sqlString, Class<?> resultClass, Object... parameter);


    public int updateBySql(String sqlString, Object... parameter);


    public Query createSqlQuery(String sqlString, Object... parameter);

    // -------------- Criteria -------------- //


    public Page<T> find(Page<T> page);


    public Page<T> find(Page<T> page, DetachedCriteria detachedCriteria);


    public Page<T> find(Page<T> page, DetachedCriteria detachedCriteria, ResultTransformer resultTransformer);


    public List<T> find(DetachedCriteria detachedCriteria);


    public List<T> find(DetachedCriteria detachedCriteria, ResultTransformer resultTransformer);


    public long count(DetachedCriteria detachedCriteria);


    public DetachedCriteria createDetachedCriteria(Criterion... criterions);
}
