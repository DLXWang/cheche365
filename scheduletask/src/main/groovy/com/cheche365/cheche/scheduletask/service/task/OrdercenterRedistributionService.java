package com.cheche365.cheche.scheduletask.service.task;

import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.*;
import com.cheche365.cheche.core.service.IInternalUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.*;

/**
 * Created by chenxiangyin on 2017/3/9.
 */
@Service("ordercenterRedistributionTaskService")
    public class OrdercenterRedistributionService {
        @Autowired
        private InternalUserRepository internalUserRepository;

    @Autowired
    private OrderOperationInfoRepository orderOperationInfoRepository;

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
        Long operatorId = ((Integer) paramMap.get("operator")).longValue();
        InternalUser operator = internalUserRepository.findOne(operatorId);
        InternalUser oldInternalUser = internalUserRepository.findOne(oldOperatorId);
        InternalUser newInternalUser = null;
        if (newOperatorId != null) {
            newInternalUser = internalUserRepository.findOne(newOperatorId);
        }
        String distributionType = paramMap.get("distributionType").toString();
        List<InternalUser> internalUserList = this.getInternalUserByType(distributionType, oldInternalUser, newInternalUser);
        List<OrderOperationInfo> assignerOrderOperationInfoList = orderOperationInfoRepository.findByAssigner(oldOperatorId,0,500);

        int threadNum = 5;
        ExecutorService ex = Executors.newFixedThreadPool(threadNum);
        while (assignerOrderOperationInfoList.size() > 0) {
            int index = 0;
            int dealSize = (int) Math.ceil((double) assignerOrderOperationInfoList.size() / (double) threadNum);
            List<Future> futures = new ArrayList<>(threadNum);
            for (int i = 0; i < threadNum; i++, index += dealSize) {
                int start = index;
                if (start >= assignerOrderOperationInfoList.size()) {
                    break;
                }
                int end = start + dealSize;
                end = end > assignerOrderOperationInfoList.size() ? assignerOrderOperationInfoList.size() : end;
                futures.add(ex.submit(new Task(assignerOrderOperationInfoList, internalUserList,  start, end, operator)));
            }
            for (Future future : futures) {
                try {
                    future.get();
                } catch (InterruptedException e) {
                    logger.debug("出单中心OrderOperationInfo指派跟进人异常", e);
                } catch (ExecutionException e) {
                    logger.debug("出单中心OrderOperationInfo指派跟进人异常", e);
                }
            }
            assignerOrderOperationInfoList = orderOperationInfoRepository.findByAssigner(oldInternalUser.getId(),0,500);
        }
        ex.shutdown();
    }

    private class Task implements Callable<Boolean> {
        private List<OrderOperationInfo> assignerOrderOperationInfoList;
        private List<InternalUser> internalUserList;
        private int start;
        private int end;
        private InternalUser operator;

        public Task(List<OrderOperationInfo> assignerOrderOperationInfoList, List<InternalUser> internalUserList,int start, int end, InternalUser operator) {
            this.assignerOrderOperationInfoList = assignerOrderOperationInfoList;
            this.internalUserList = internalUserList;
            this.start = start;
            this.end = end;
            this.operator = operator;
        }

        @Override
        public Boolean call() throws Exception {
            for (int i = start; i < end; i++) {
                OrderOperationInfo orderOperationInfo = assignerOrderOperationInfoList.get(i);
                InternalUser internalUser = internalUserService.getRandomInternalUser(internalUserList);
                orderOperationInfo.setAssigner(internalUser);
                orderOperationInfo.setOperator(operator);
            }
            orderOperationInfoRepository.save(assignerOrderOperationInfoList);
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
