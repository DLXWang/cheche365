package com.cheche365.cheche.manage.common.util.DataFilter;

import com.cheche365.cheche.manage.common.model.InternalUserDataPermission;
import com.cheche365.cheche.manage.common.model.PublicQuery;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * Created by yellow on 2017/11/4.
 * 列表数据权限过滤
 */
@Component
public class PermissionFilter  extends DataFilter {
    @Override
    public void bindConditions(Root<?> root, List<Predicate> predicateList, CriteriaBuilder cb, PublicQuery query) {
        if (CollectionUtils.isNotEmpty(query.getPermissions())) {
            for (InternalUserDataPermission permission : query.getPermissions()) {
                Path path = root.get(permission.getEntity());
                String[] fields = permission.getField().split("\\.");
                for (String field : fields) {
                    path = path.get(field);
                }
                CriteriaBuilder.In<Long> cin = cb.in(path);
                String[] values = permission.getValues().split(",");
                for (int i = 0; i < values.length; i++) {
                    cin.value(Long.parseLong(values[i]));
                }
                predicateList.add(cb.not(cin));
            }
        }
    }
}
