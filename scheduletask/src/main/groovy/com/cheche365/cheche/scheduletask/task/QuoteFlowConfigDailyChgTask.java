package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.core.model.InternalUser;
import com.cheche365.cheche.core.model.QuoteFlowConfig;
import com.cheche365.cheche.core.service.InternalUserService;
import com.cheche365.cheche.manage.common.model.QuoteFlowConfigOperateLog;
import com.cheche365.cheche.manage.common.repository.QuoteFlowConfigOperateLogRepository;
import com.cheche365.cheche.manage.common.service.QuoteFlowConfigService;
import com.cheche365.cheche.manage.common.web.model.QuoteFlowConfigQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 电销数据推送定时任务
 * Created by cxy on 2017/7/19.
 */
@Service
public class QuoteFlowConfigDailyChgTask extends BaseTask {
    private Logger logger = LoggerFactory.getLogger(QuoteFlowConfigDailyChgTask.class);
    @Autowired
    private QuoteFlowConfigOperateLogRepository logRepository;
    @Autowired
    private QuoteFlowConfigService quoteFlowConfigService;
    @Autowired
    private InternalUserService internalUserService;


    @Override
    protected void doProcess() throws Exception {
            List<QuoteFlowConfigOperateLog> logList = logRepository.findLogCurrenDay();
            logger.debug("运营中心 报价上下线定时任务 {} 个数据",logList.size());
            //InternalUser systemUser = internalUserService.getSystemInternalUser();
            if(logList.size() > 0){
                Map<QuoteFlowConfig, List<QuoteFlowConfigOperateLog>> logListMap = logList.stream().collect(Collectors.groupingBy(QuoteFlowConfigOperateLog::getQuoteFlowConfig));
                for (QuoteFlowConfig config : logListMap.keySet()) {
                    QuoteFlowConfigQuery query = new QuoteFlowConfigQuery();
                    List<QuoteFlowConfigOperateLog> subLogList = logListMap.get(config);
                    for(QuoteFlowConfigOperateLog log:subLogList){
                        if(log.getOperationType().equals(QuoteFlowConfigOperateLog.OperationType.ON_OOF_LINE.getIndex())){
                            query.setId(config.getId());
                            query.setEnable(log.getOperationValue() == 1?true:false);
                            query.setReason(log.getComment());
                            query.setUser(InternalUser.ENUM.SYSTEM);
                            query.setOperateTime(QuoteFlowConfigQuery.OperationTime.NOW.getIndex());
                        }else if(log.getOperationType().equals(QuoteFlowConfigOperateLog.OperationType.ACCESS_MODE.getIndex())){
                            query.setQuoteWay(log.getOperationValue());
                        }
                    }
                    try{
                    quoteFlowConfigService.edit(query);
                    }catch(Exception e){
                        logger.error("运营中心 报价上下线定时任务出现异常",e);
                    }
                }
            }
    }
}
