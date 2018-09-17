package com.cheche365.cheche.scheduletask.service.task;

import com.cheche365.cheche.core.model.InternalUser;
import com.cheche365.cheche.manage.common.model.TelMarketingCenter;
import com.cheche365.cheche.manage.common.model.TelMarketingCenterHistory;
import com.cheche365.cheche.core.repository.InternalUserRepository;
import com.cheche365.cheche.manage.common.repository.TelMarketingCenterHistoryRepository;
import com.cheche365.cheche.manage.common.repository.TelMarketingCenterRepository;
import com.cheche365.cheche.core.service.IInternalUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.*;

/**
 * Created by xu.yelong on 2016-06-03.
 */
@Service("customerRedistributionTaskService")
public class CustomerRedistributionService {

    @Autowired
    private TelMarketingCenterRepository telMarketingCenterRepository;

    @Autowired
    private InternalUserRepository internalUserRepository;

    @Autowired
    private TelMarketingCenterHistoryRepository telMarketingCenterHistoryRepository;

    @Autowired
    private IInternalUserService internalUserService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Transactional
    public void redistributeByOperator(Map paramMap) {
        Long oldOperatorId = ((Integer) paramMap.get("oldOperatorId")).longValue();
        Long newOperatorId = null;
        if(paramMap.get("newOperatorId")!=null){
            newOperatorId=((Integer) paramMap.get("newOperatorId")).longValue();
        }
        String distributionType = paramMap.get("distributionType").toString();
        Long operatorId = ((Integer) paramMap.get("operator")).longValue();
        if (telMarketingCenterRepository.countByOperator(oldOperatorId) == 0) {
            return;
        }
        InternalUser operator = internalUserRepository.findOne(operatorId);
        InternalUser oldInternalUser = internalUserRepository.findOne(oldOperatorId);
        if (oldInternalUser == null) {
            return;
        }
        InternalUser newInternalUser = null;
        if (newOperatorId != null) {
            newInternalUser = internalUserRepository.findOne(newOperatorId);
        }
        List<InternalUser> internalUserList = this.getInternalUserByType(distributionType, oldInternalUser, newInternalUser);
        List<TelMarketingCenter> telMarketingCenterList = telMarketingCenterRepository.findPageByOperatorId(oldInternalUser.getId(), 0, 1000);
        int threadNum = 5;
        ExecutorService ex = Executors.newFixedThreadPool(threadNum);
        while (telMarketingCenterList.size() > 0) {
            int index = 0;
            int dealSize = (int) Math.ceil((double) telMarketingCenterList.size() / (double) threadNum);
            List<Future> futures = new ArrayList<>(threadNum);
            for (int i = 0; i < threadNum; i++, index += dealSize) {
                int start = index;
                if (start >= telMarketingCenterList.size()) {
                    break;
                }
                int end = start + dealSize;
                end = end > telMarketingCenterList.size() ? telMarketingCenterList.size() : end;
                futures.add(ex.submit(new Task(telMarketingCenterList, internalUserList, oldInternalUser, start, end, operator)));
            }
            for (Future future : futures) {
                try {
                    future.get();
                } catch (InterruptedException  | ExecutionException e) {
                    logger.debug("出单中心指派跟进人异常", e);
                }
            }
            telMarketingCenterList = telMarketingCenterRepository.findByOperator(oldInternalUser, 1000);
        }
        ex.shutdown();
    }

    private class Task implements Callable<Boolean> {
        private List<TelMarketingCenter> telMarketingCenterList;
        private List<InternalUser> internalUserList;
        private int start;
        private int end;
        private InternalUser oldInternalUser;
        private InternalUser operator;

        public Task(List<TelMarketingCenter> telMarketingCenterList, List<InternalUser> internalUserList, InternalUser oldInternalUser, int start, int end, InternalUser operator) {
            this.telMarketingCenterList = telMarketingCenterList;
            this.internalUserList = internalUserList;
            this.oldInternalUser = oldInternalUser;
            this.start = start;
            this.end = end;
            this.operator = operator;
        }

        @Override
        public Boolean call() throws Exception {
            List<TelMarketingCenterHistory> telMarketingCenterHistoryList = new ArrayList<>();
            for (int i = start; i < end; i++) {
                TelMarketingCenter telMarketingCenter = telMarketingCenterList.get(i);
                InternalUser internalUser = internalUserService.getRandomInternalUser(internalUserList);
                telMarketingCenter.setOperator(internalUser);
                telMarketingCenterHistoryList.add(createHistory(telMarketingCenter, oldInternalUser, internalUser, operator));
            }
            telMarketingCenterRepository.save(telMarketingCenterList);
            telMarketingCenterHistoryRepository.save(telMarketingCenterHistoryList);
            return true;
        }
    }

    private List<InternalUser> getInternalUserByType(String distributionType, InternalUser oldInternalUser, InternalUser newInternalUser) {
        if (CustomerReAssignMethodEnum.ONE_NEW_CUSTOM.getIndex().equals(distributionType)) {
            return Arrays.asList(newInternalUser);
        } else if (CustomerReAssignMethodEnum.RANDOM_ALL_CUSTOM.getIndex().equals(distributionType)) {
            return internalUserService.listAllEnableTelCommissioner();
        } else if (CustomerReAssignMethodEnum.RANDOM_ALL_CUSTOM_EXCEPT_ONE.getIndex().equals(distributionType)) {
            return internalUserService.listAllEnableTelCommissionerExceptOne(oldInternalUser);
        } else {
            return null;
        }
    }

    private TelMarketingCenterHistory createHistory(TelMarketingCenter telMarketingCenter, InternalUser oldOperator, InternalUser newOperator, InternalUser operator) {
        TelMarketingCenterHistory telMarketingCenterHistory = new TelMarketingCenterHistory();
        telMarketingCenterHistory.setOperator(operator);
        telMarketingCenterHistory.setTelMarketingCenter(telMarketingCenter);
        telMarketingCenterHistory.setDealResult("修改跟进人");
        telMarketingCenterHistory.setCreateTime(new Date());
        telMarketingCenterHistory.setComment((oldOperator != null ? oldOperator.getName() : "无") + "->" + newOperator.getName());
        telMarketingCenterHistory.setType(5);
        return telMarketingCenterHistory;
    }

    enum CustomerReAssignMethodEnum {
        ONE_NEW_CUSTOM("1"),
        RANDOM_ALL_CUSTOM("2"),
        RANDOM_ALL_CUSTOM_EXCEPT_ONE("3");

        private String index;

        CustomerReAssignMethodEnum(String index) {
            this.index = index;
        }

        public String getIndex() {
            return this.index;
        }
    }

}
