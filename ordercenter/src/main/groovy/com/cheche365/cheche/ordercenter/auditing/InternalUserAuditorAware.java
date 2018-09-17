package com.cheche365.cheche.ordercenter.auditing;

import com.cheche365.cheche.core.model.InternalUser;
import com.cheche365.cheche.ordercenter.service.user.OrderCenterInternalUserManageService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

/**
 * 使用JPA注解在实体创建或更新的时候把操作时间或操作人一并更新到数据库里去
 * JPA Auditing：
 * 包括：@CreatedDate @LastModifiedDate @CreatedBy @LastModifiedBy
 * Created by sunhuazhong on 2016/2/15.
 */
@Component
public class InternalUserAuditorAware implements AuditorAware<InternalUser> {
    private Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    @Autowired
    private OrderCenterInternalUserManageService orderCenterInternalUserManageService;

    @Override
    public InternalUser getCurrentAuditor() {
        InternalUser internalUser = orderCenterInternalUserManageService.getCurrentInternalUser();
        logger.debug("通过AuditorAware获取当前审计员信息：{}", internalUser == null? "无" : internalUser.getEmail());
        return internalUser;
    }
}
