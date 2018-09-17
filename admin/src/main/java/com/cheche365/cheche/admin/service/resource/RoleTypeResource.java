package com.cheche365.cheche.admin.service.resource;

import com.cheche365.cheche.admin.web.model.role.RoleViewModel;
import com.cheche365.cheche.core.model.Role;
import com.cheche365.cheche.core.model.RoleType;
import com.cheche365.cheche.core.repository.RoleRepository;
import com.cheche365.cheche.core.repository.RoleTypeRepository;
import com.cheche365.cheche.manage.common.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liyh on 2015/9/9
 */
@Component
public class RoleTypeResource extends BaseService<Role, Object> {

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private RoleTypeRepository roleTypeRepository;

    public List<Role> listAll(Long userType) {
        if(userType != null){
            RoleType roleType = roleTypeRepository.findOne(userType);
            return roleRepository.findByType(roleType);
        }else{
            return getAll(roleRepository);
        }

    }

    public List<RoleViewModel> createViewData(List<Role> roleTypeList) {
        if (roleTypeList == null) {
            return null;
        }
        List<RoleViewModel> viewDataList = new ArrayList<>();
        for(int i=0;i<roleTypeList.size();i++){
            RoleViewModel viewData = new RoleViewModel();
            viewData.setId(roleTypeList.get(i).getId());
            viewData.setName(roleTypeList.get(i).getName());
            viewDataList.add(viewData);
        }
        return viewDataList;
    }
}
