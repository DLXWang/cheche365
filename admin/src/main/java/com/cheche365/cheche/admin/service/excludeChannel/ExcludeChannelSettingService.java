package com.cheche365.cheche.admin.service.excludeChannel;

import com.cheche365.cheche.manage.common.model.TelMarketingCenterChannelFilter;
import com.cheche365.cheche.manage.common.model.TelMarketingCenterChannelFilterViewModel;
import com.cheche365.cheche.manage.common.repository.TelMarketingCenterChannelFilterRepository;
import com.cheche365.cheche.manage.common.service.InternalUserManageService;
import org.apache.commons.collections.IteratorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by wangshaobin on 2016/8/29.
 */
@Service
public class ExcludeChannelSettingService {

    @Autowired
    private InternalUserManageService internalUserManageService;

    @Autowired
    private TelMarketingCenterChannelFilterRepository taskExcludeChannelSettingRepository;


    public TelMarketingCenterChannelFilter findById(Long id) {
        return taskExcludeChannelSettingRepository.findOne(id);
    }

    public List<TelMarketingCenterChannelFilterViewModel> findAll() {
        List<TelMarketingCenterChannelFilterViewModel> reList = new ArrayList<TelMarketingCenterChannelFilterViewModel>();
        List<TelMarketingCenterChannelFilter> list = IteratorUtils.toList(taskExcludeChannelSettingRepository.findAll().iterator());
        if (!CollectionUtils.isEmpty(list)) {
            for (TelMarketingCenterChannelFilter setting : list) {
                reList.add(TelMarketingCenterChannelFilterViewModel.createViewModel(setting));
            }
        }
        return reList;
    }

    @Transactional
    public boolean save(TelMarketingCenterChannelFilterViewModel viewModel) {
        boolean flag = false;
        TelMarketingCenterChannelFilter setting = taskExcludeChannelSettingRepository.findOne(viewModel.getId());
        if (setting == null)
            return flag;
        setting.setExcludeChannels(viewModel.getExcludeChannels());
        setting.setOperator(internalUserManageService.getCurrentInternalUser());
        setting.setUpdateTime(new Date());
        taskExcludeChannelSettingRepository.save(setting);
        flag = true;
        return flag;
    }

}
