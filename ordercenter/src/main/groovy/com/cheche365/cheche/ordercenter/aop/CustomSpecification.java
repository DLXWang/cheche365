package com.cheche365.cheche.ordercenter.aop;

import com.cheche365.cheche.ordercenter.annotation.DataPermission;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yellow on 2017/6/13.
 */
public abstract class CustomSpecification<T> implements Specification<T> {
    public List<Predicate> predicateList=new ArrayList<>();



    @Override
    @DataPermission(code="OC1", handler = "specificationConditionHandler")
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        return toPredicate(new SpecificationParam(root, criteriaQuery, criteriaBuilder, new ArrayList<Predicate>()));
    }

    public abstract Predicate toPredicate(SpecificationParam specificationParam);

    public class SpecificationParam {
        private List<Predicate> predicateList;
        private Root<T> root;
        private CriteriaQuery<?> criteriaQuery;
        private CriteriaBuilder criteriaBuilder;

        public SpecificationParam() {
        }

        public SpecificationParam(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder, List<Predicate> predicateList) {
            this.criteriaBuilder = criteriaBuilder;
            this.root = root;
            this.predicateList = predicateList;
            this.criteriaQuery = criteriaQuery;
        }

        public List<Predicate> getPredicateList() {
            return predicateList;
        }

        public void setPredicateList(List<Predicate> predicateList) {
            this.predicateList = predicateList;
        }

        public Root<T> getRoot() {
            return root;
        }

        public void setRoot(Root<T> root) {
            this.root = root;
        }

        public CriteriaBuilder getCriteriaBuilder() {
            return criteriaBuilder;
        }

        public void setCriteriaBuilder(CriteriaBuilder criteriaBuilder) {
            this.criteriaBuilder = criteriaBuilder;
        }
    }
}
