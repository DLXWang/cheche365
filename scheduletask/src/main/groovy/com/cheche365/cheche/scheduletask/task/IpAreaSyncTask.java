package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.common.math.NumberUtils;
import com.cheche365.cheche.core.model.Area;
import com.cheche365.cheche.core.model.User;
import com.cheche365.cheche.core.model.UserLoginInfo;
import com.cheche365.cheche.core.repository.AreaRepository;
import com.cheche365.cheche.core.repository.UserLoginInfoRepository;
import com.cheche365.cheche.core.util.AddressUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * 处理用户登录信息中城市为空的数据
 * Created by xu.yelong on 2016/7/18.
 */
@Service
public class IpAreaSyncTask extends BaseTask {
    @Autowired
    private UserLoginInfoRepository userLoginInfoRepository;

    @Autowired
    private AreaRepository areaRepository;

    private Logger logger = LoggerFactory.getLogger(IpAreaSyncTask.class);

    private static final Integer LIMIT =1000;

    private static final Integer THREAD_NUM = 5;

    @Override
    protected void doProcess() throws Exception {

        Long maxId=0l;
        Long count = userLoginInfoRepository.countByAreaIsNull();
        List<UserLoginInfo> userLoginInfoList = userLoginInfoRepository.findByAreaIsNull(maxId,LIMIT);
        logger.debug("user login info data refresh begin, empty area count :-> {}" + count);
        int threadNum = THREAD_NUM;
        ExecutorService ex = Executors.newFixedThreadPool(threadNum);
        while (userLoginInfoList.size() > 0) {

            int index = 0;
            int dealSize = (int) Math.ceil((double) userLoginInfoList.size() / (double) threadNum);
            List<Future> futures = new ArrayList<>(threadNum);
            for (int i = 0; i < threadNum; i++, index += dealSize) {
                int start = index;
                if (start >= userLoginInfoList.size()) {
                    break;
                }
                int end = start + dealSize;
                end = end > userLoginInfoList.size() ? userLoginInfoList.size() : end;
                futures.add(ex.submit(new Task(userLoginInfoList, start, end)));
            }
            for (Future future : futures) {
                try {
                    future.get();
                } catch (InterruptedException e) {
                    logger.debug("用户登录IP同步城市信息异常", e);
                } catch (ExecutionException e) {
                    logger.debug("用户登录IP同步城市信息异常", e);
                }
            }
            maxId=userLoginInfoList.get(userLoginInfoList.size()-1).getId();
            userLoginInfoList = userLoginInfoRepository.findByAreaIsNull(maxId,LIMIT);
        }
        ex.shutdown();
    }


    private class Task implements Callable<Boolean> {
        private int start;
        private int end;
        private List<UserLoginInfo> userLoginInfoList;

        public Task(List<UserLoginInfo> userLoginInfoList, int start, int end) {
            this.userLoginInfoList = userLoginInfoList;
            this.start = start;
            this.end = end;
        }

        @Override
        public Boolean call() throws Exception {
            List<UserLoginInfo> changeList = new ArrayList<>();
            for (int i = start; i < end; i++) {
                UserLoginInfo userLoginInfo = userLoginInfoList.get(i);
                String ip = userLoginInfo.getLastLoginIp();
                if (StringUtils.isNotBlank(ip)) {
                    String cityCode = AddressUtil.ip2Location(ip);
                    if (cityCode != null && NumberUtils.isNumber(cityCode)) {
                        Area area = areaRepository.findFirstByCityCode(Integer.valueOf(cityCode));
                        userLoginInfo.setArea(area);
                        changeList.add(userLoginInfo);
                    }
                }
            }
            logger.debug("thread :->{} ,user login info data refresh over, refresh data count :-> {}", Thread.currentThread().getName(), changeList.size());
            userLoginInfoRepository.save(changeList);
            return true;
        }
    }
}
