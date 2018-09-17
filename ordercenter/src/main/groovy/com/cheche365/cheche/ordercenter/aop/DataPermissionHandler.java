package com.cheche365.cheche.ordercenter.aop;

import com.cheche365.cheche.manage.common.model.InternalUserDataPermission;
import com.cheche365.cheche.ordercenter.model.PublicQuery;
import com.cheche365.cheche.ordercenter.web.model.telMarketingCenter.TelMarketingCenterRequestParams;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yellow on 2017/6/14.
 */
@Component
public class DataPermissionHandler {
    @Autowired
    SpecificationConditionHandler specificationConditionHandler;

    @Autowired
    PublicQueryConditionHandler publicQueryConditionHandler;

    public Map<String,PermissionHandler> handlerMap=new HashMap();

    private void init(){
        if(handlerMap.isEmpty()){
            handlerMap.put("specificationConditionHandler",specificationConditionHandler);
            handlerMap.put("publicQueryConditionHandler",publicQueryConditionHandler);
        }
    }

    public Object[] handle(Object[] args, List<InternalUserDataPermission> permissions, String handlerName){
        init();
        if(handlerMap.containsKey(handlerName)){
            args=handlerMap.get(handlerName).handle(args,permissions);
        }
        return args;
    }

    @Component
    public interface PermissionHandler{
        Object[] handle(Object[] args, List<InternalUserDataPermission> permissions);
    }


    @Component
    public class SpecificationConditionHandler implements PermissionHandler{
        @Override
        public Object[] handle(Object[] args, List<InternalUserDataPermission> permissions) {
            if (CollectionUtils.isNotEmpty(permissions)) {
                CustomSpecification.SpecificationParam param;

                for (Object arg : args) {
                    if (arg.getClass() == CustomSpecification.SpecificationParam.class) {
                        param = (CustomSpecification.SpecificationParam) arg;
                        for(InternalUserDataPermission permission:permissions){
                            Path path = param.getRoot().get(permission.getEntity());
                            String[] fields = permission.getField().split("\\.");
                            for (String field : fields) {
                                path = path.get(field);
                            }
                            CriteriaBuilder.In<Long> in = param.getCriteriaBuilder().in(path);
                            String[] values = permission.getValues().split(",");
                            for (int i = 0; i < values.length; i++) {
                                in.value(Long.parseLong(values[i]));
                                param.getPredicateList().add(in);
                            }
                        }
                    }
                }
            }
            return args;
        }
    }

    @Component
    public class PublicQueryConditionHandler implements PermissionHandler{
        @Override
        public Object[] handle(Object[] args, List<InternalUserDataPermission> permissions) {
            if (CollectionUtils.isNotEmpty(permissions)) {
                for (Object arg : args) {
                    if (arg.getClass() == PublicQuery.class) {
                        PublicQuery publicQuery = (PublicQuery) arg;
                        publicQuery.setPermissions(permissions);
                    }
                    if (arg.getClass() == TelMarketingCenterRequestParams.class) {
                        TelMarketingCenterRequestParams params = (TelMarketingCenterRequestParams) arg;
                        params.setPermissions(permissions);
                    }
                }
            }
            return args;
        }
    }
}
