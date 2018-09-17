package com.cheche365.cheche.ordercenter.service.resource;

import com.cheche365.cheche.core.model.BusinessActivity;
import com.cheche365.cheche.core.model.CooperationMode;
import com.cheche365.cheche.core.repository.BusinessActivityRepository;
import com.cheche365.cheche.manage.common.service.BaseService;
import com.cheche365.cheche.ordercenter.web.model.order.BusinessActivityViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunhuazhong on 2015/8/13.
 */
@Component
public class CPSChannelResource extends BaseService<BusinessActivity, BusinessActivity> {

    @Autowired
    private BusinessActivityRepository businessActivityRepository;

    public List<BusinessActivity> listAll() {
        return businessActivityRepository.findByCooperationMode(CooperationMode.Enum.CPS);
    }

    public List<BusinessActivityViewModel> createViewData(List<BusinessActivity> cpsBusinessActivityList) {
        if (cpsBusinessActivityList == null)
            return null;

        List<BusinessActivityViewModel> viewDataList = new ArrayList<>();
        cpsBusinessActivityList.forEach(agent -> {
            BusinessActivityViewModel viewData = new BusinessActivityViewModel();
            viewData.setId(agent.getId());
            viewData.setName(agent.getName());
            viewDataList.add(viewData);
        });

        return viewDataList;
    }
}
