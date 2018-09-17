package com.cheche365.cheche.operationcenter.service.resource;

import com.cheche365.cheche.core.model.CooperationMode;
import com.cheche365.cheche.core.repository.CooperationModeRepository;
import com.cheche365.cheche.manage.common.service.BaseService;
import com.cheche365.cheche.operationcenter.web.model.partner.CooperationModeViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangfei on 2015/6/16.
 */
@Component
public class CooperationModeResource extends BaseService<CooperationMode, CooperationMode> {

    @Autowired
    private CooperationModeRepository cooperationModeRepository;

    public List<CooperationMode> listAll() {
        return super.getAll(cooperationModeRepository);
    }

    public List<CooperationMode> listAllExcludeMarketing() {
        return CooperationMode.Enum.BUSINESS_ACTIVITY_MODES;
    }
    public List<CooperationModeViewModel> createViewData(List<CooperationMode> cooperationModeList) {
        if (cooperationModeList == null)
            return null;

        List<CooperationModeViewModel> viewDataList = new ArrayList<>();
        cooperationModeList.forEach(cooperationMode -> {
            CooperationModeViewModel viewData = new CooperationModeViewModel();
            viewData.setId(cooperationMode.getId());
            viewData.setName(cooperationMode.getName());
            viewDataList.add(viewData);
        });

        return viewDataList;
    }
}
