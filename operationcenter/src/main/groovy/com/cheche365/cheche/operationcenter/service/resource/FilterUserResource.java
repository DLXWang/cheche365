package com.cheche365.cheche.operationcenter.service.resource;

import com.cheche365.cheche.core.model.FilterUser;
import com.cheche365.cheche.core.repository.FilterUserRepository;
import com.cheche365.cheche.manage.common.service.BaseService;
import com.cheche365.cheche.manage.common.web.model.sms.FilterUserViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guoweifu on 2015/10/13.
 */
@Component
public class FilterUserResource extends BaseService<FilterUser,FilterUser> {

    @Autowired
    private FilterUserRepository filterUserRepository;

    /**
     * 获取所有筛选用户功能
     * @return
     */
    public List<FilterUserViewModel> getAllEnableFilterUser(){
        List<FilterUserViewModel> filterUserViewModelList = new ArrayList<>();
        List<FilterUser> filterUserList = filterUserRepository.findByDisable(false);
        for(FilterUser filterUser : filterUserList){
            FilterUserViewModel viewData = new FilterUserViewModel();
            viewData.setId(filterUser.getId());
            viewData.setName(filterUser.getName());
            filterUserViewModelList.add(viewData);
        }
        return filterUserViewModelList;
    }

}
