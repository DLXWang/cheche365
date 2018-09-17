package com.cheche365.cheche.ordercenter.service.resource;

import com.cheche365.cheche.core.model.Role;
import com.cheche365.cheche.core.repository.RoleRepository;
import com.cheche365.cheche.manage.common.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by sunhuazhong on 2016/2/1.
 */
@Component
public class RoleResource extends BaseService<Role, Role> {
    @Autowired
    private RoleRepository roleRepository;

    public List<Role> findAll() {
        return super.getAll(roleRepository);
    }
}
