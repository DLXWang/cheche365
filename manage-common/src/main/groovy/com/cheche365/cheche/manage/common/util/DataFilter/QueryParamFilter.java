package com.cheche365.cheche.manage.common.util.DataFilter;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.manage.common.model.PublicQuery;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.*;
import java.util.Date;
import java.util.List;

/**
 * Created by yellow on 2017/11/4.
 * 列表默认查询条件过滤
 */
@Component
public class QueryParamFilter extends DataFilter {
    private Logger logger= LoggerFactory.getLogger(QueryParamFilter.class);
    @Override
    public void bindConditions(Root<?> root, List<Predicate> predicateList, CriteriaBuilder cb, PublicQuery query) {
        if (CollectionUtils.isEmpty(predicateList)) {
            Path<Date> createTimePath;
            try{
               createTimePath = root.get("createTime");
            }catch (RuntimeException e){
                logger.debug("can not filter data ,entity no field create time ");
                return;
            }
            Expression<Date> startDateExpression = cb.literal(DateUtils.getCustomDate(new Date(), -30, 0, 0, 0));
            Expression<Date> endDateExpression = cb.literal(new Date());
            predicateList.add(cb.between(createTimePath, startDateExpression, endDateExpression));
        }
    }
}
