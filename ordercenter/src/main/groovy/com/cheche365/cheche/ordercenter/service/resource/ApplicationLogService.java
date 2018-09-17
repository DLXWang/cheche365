package com.cheche365.cheche.ordercenter.service.resource;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.LogType;
import com.cheche365.cheche.core.model.MoApplicationLog;
import com.cheche365.cheche.core.mongodb.repository.MoApplicationLogRepository;
import com.cheche365.cheche.core.repository.InternalUserRepository;
import com.cheche365.cheche.core.service.DoubleDBService;
import com.cheche365.cheche.manage.common.service.BaseService;
import com.cheche365.cheche.manage.common.service.InternalUserManageService;
import com.cheche365.cheche.ordercenter.web.model.ApplicationLogViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by xu.yelong on 2016/1/26.
 * 日志操作
 */
@Service
public class ApplicationLogService extends BaseService {

    @Autowired
    DoubleDBService doubleDBService;

    @Autowired
    private MoApplicationLogRepository applicationLogMongoRepository;

    @Autowired
    private InternalUserRepository internalUserRepository;

    @Autowired
    private InternalUserManageService internalUserManageService;

    public List<MoApplicationLog> getLog(String objId, String objTable){

        Long logType=LogType.Enum.COMMENT_CHANGE_27.getId();
        List<MoApplicationLog> mongoList = applicationLogMongoRepository.findByObjIdAndLogTypeAndObjTableOrderByCreateTime(objId, logType, objTable,new Sort(Sort.Direction.DESC, "createTime"));
        return mongoList;
    }

    public MoApplicationLog saveLog(MoApplicationLog applicationLog){
        applicationLog.setOpeartor(internalUserManageService.getCurrentInternalUser().getId());
        applicationLog.setCreateTime(new Date());
        applicationLog.setLogType(LogType.Enum.COMMENT_CHANGE_27);
        applicationLog.setLogLevel(2);
        return doubleDBService.saveApplicationLog(applicationLog);
    }


    public List<ApplicationLogViewModel> createViewModelList(List<MoApplicationLog> applicationLogList){
        List applicationLogViewModelList=new ArrayList<>();
        for(MoApplicationLog applicationLog : applicationLogList){
            applicationLogViewModelList.add(createViewModel(applicationLog));
        }
        return applicationLogViewModelList;
    }

    public ApplicationLogViewModel createViewModel(MoApplicationLog applicationLog){
        ApplicationLogViewModel applicationLogViewModel=new ApplicationLogViewModel();
        applicationLogViewModel.setId(applicationLog.getId());
        applicationLogViewModel.setCreateTime(DateUtils.getDateString(applicationLog.getCreateTime(), DateUtils.DATE_LONGTIME24_PATTERN));
        applicationLogViewModel.setInstanceNo(applicationLog.getInstanceNo());
        applicationLogViewModel.setLogId(applicationLog.getLogId());
        applicationLogViewModel.setLogLevel(applicationLog.getLogLevel());
        applicationLogViewModel.setLogMessage((String)applicationLog.getLogMessage());
        applicationLogViewModel.setLogType(applicationLog.getLogType().getId());
        applicationLogViewModel.setObjId(applicationLog.getObjId());
        applicationLogViewModel.setObjTable(applicationLog.getObjTable());
        if (applicationLog.getOpeartor() != null) {
            applicationLogViewModel.setOperator(applicationLog.getOpeartor());
            applicationLogViewModel.setOperatorName(internalUserRepository.findOne(applicationLog.getOpeartor()).getName());
        }
        return  applicationLogViewModel;
    }
}
