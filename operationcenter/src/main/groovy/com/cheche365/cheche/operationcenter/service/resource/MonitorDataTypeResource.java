package com.cheche365.cheche.operationcenter.service.resource;

import com.cheche365.cheche.core.model.MonitorDataType;
import com.cheche365.cheche.core.repository.MonitorDataTypeRepository;
import com.cheche365.cheche.manage.common.service.BaseService;
import com.cheche365.cheche.operationcenter.web.model.partner.MonitorDataTypeViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangfei on 2015/6/16.
 */
@Component
public class MonitorDataTypeResource extends BaseService<MonitorDataType, MonitorDataType> {

    @Autowired
    private MonitorDataTypeRepository monitorDataTypeRepository;

    public List<MonitorDataType> listAll() {
        return super.getAll(monitorDataTypeRepository);
    }

    public List<MonitorDataType> listEnable() {
        return monitorDataTypeRepository.findByShowFlag(true);
    }

    public List<MonitorDataTypeViewModel> createViewData(List<MonitorDataType> monitorDataTypeList) {
        if (monitorDataTypeList == null)
            return null;

        List<MonitorDataTypeViewModel> viewDataList = new ArrayList<>();
        monitorDataTypeList.forEach(monitorDataType -> {
            MonitorDataTypeViewModel viewData = new MonitorDataTypeViewModel();
            viewData.setId(monitorDataType.getId());
            viewData.setName(monitorDataType.getName());
            viewDataList.add(viewData);
        });

        return viewDataList;
    }
}
