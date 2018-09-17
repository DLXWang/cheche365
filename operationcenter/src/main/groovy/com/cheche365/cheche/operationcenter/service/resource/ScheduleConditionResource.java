package com.cheche365.cheche.operationcenter.service.resource;

import com.cheche365.cheche.core.model.Marketing;
import com.cheche365.cheche.core.model.ScheduleCondition;
import com.cheche365.cheche.core.repository.ScheduleConditionRepository;
import com.cheche365.cheche.manage.common.service.BaseService;
import com.cheche365.cheche.operationcenter.web.model.partner.ScheduleConditionViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by lyh on 2015/10/13.
 */
@Component
public class ScheduleConditionResource extends BaseService<Marketing, Marketing> {

    @Autowired
    private ScheduleConditionRepository scheduleConditionRepository;

    public List<ScheduleConditionViewModel> createViewData() {
        List<ScheduleConditionViewModel> viewDataList = new ArrayList<>();
        Iterable<ScheduleCondition> tIterable = scheduleConditionRepository.findAll();
        Iterator<ScheduleCondition> tIterator = tIterable.iterator();
        while (tIterator.hasNext()) {
            ScheduleConditionViewModel viewModel = new ScheduleConditionViewModel();
            ScheduleCondition sc = tIterator.next();
            if(sc instanceof ScheduleCondition){
                viewModel.setId(sc.getId());
                viewModel.setCondition(sc.getDescription());
                viewDataList.add(viewModel);
            }
        }
        return viewDataList;
    }
}
