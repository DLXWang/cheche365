package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.core.context.ApplicationContextHolder;
import com.cheche365.cheche.core.model.Area;
import com.cheche365.cheche.core.model.MobileArea;
import com.cheche365.cheche.core.repository.AreaRepository;
import com.cheche365.cheche.core.repository.MobileAreaRepository;
import com.cheche365.cheche.manage.common.model.TelMarketingCenter;
import com.cheche365.cheche.manage.common.repository.TelMarketingCenterRepository;
import com.cheche365.cheche.scheduletask.service.common.TaskRunningService;
import com.cheche365.cheche.web.service.mobile.MobileAreaService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.*;

/**
 * 处理用户登录信息中城市为空的数据
 * Created by xu.yelong on 2016/7/18.
 */
@Service
public class MobileAreaSyncTask extends BaseTask {
    @Autowired
    TelMarketingCenterRepository telMarketingCenterRepository;
    @Autowired
    private AreaRepository areaRepository;
    @Autowired
    private MobileAreaRepository mobileAreaRepository;

    @Autowired
    private TaskRunningService taskRunningService;

    @Autowired
    private MobileAreaService mobileAreaService;

    private Logger logger = LoggerFactory.getLogger(MobileAreaSyncTask.class);
    private static final Integer LIMIT = 200;
    private static final Integer THREAD_NUM = 5;

    /**
     * 线程实时同步手机城市
     */
    @PostConstruct
    private void syncThread() {
        new Thread(new MobileAreaSyncThread()).start();
    }

    @Override
    protected void doProcess() throws Exception {
        Long maxId = 0l;
        List<TelMarketingCenter> telMarketingCenterList = telMarketingCenterRepository.findByAreaIsNull(maxId, LIMIT);
        int threadNum = THREAD_NUM;

        ExecutorService ex = Executors.newFixedThreadPool(THREAD_NUM);
        while (telMarketingCenterList.size() > 0) {
            int index = 0;
            int dealSize = (int) Math.ceil((double) telMarketingCenterList.size() / (double) THREAD_NUM);
            List<Future> futures = new ArrayList<>(THREAD_NUM);
            for (int i = 0; i < THREAD_NUM; i++, index += dealSize) {
                int start = index;
                if (start >= telMarketingCenterList.size()) {
                    break;
                }
                int end = start + dealSize;
                end = end > telMarketingCenterList.size() ? telMarketingCenterList.size() : end;
                futures.add(ex.submit(new Task(telMarketingCenterList, start, end)));
            }
            for (Future future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    logger.debug("用户登录手机号同步城市信息异常", e);
                }
            }
            maxId = telMarketingCenterList.get(telMarketingCenterList.size() - 1).getId();
            telMarketingCenterList = telMarketingCenterRepository.findByAreaIsNull(maxId, LIMIT);
            Thread.sleep(1000 * 10);
        }
        ex.shutdown();
    }


    private class Task implements Callable<Boolean> {
        private int start;
        private int end;
        private List<TelMarketingCenter> telMarketingCenterList;

        public Task(List<TelMarketingCenter> telMarketingCenterList, int start, int end) {
            this.telMarketingCenterList = telMarketingCenterList;
            this.start = start;
            this.end = end;
        }

        @Override
        public Boolean call() throws Exception {
            List<MobileArea> areaList = new ArrayList<>();
            for (int i = start; i < end; i++) {
                TelMarketingCenter telMarketingCenter = telMarketingCenterList.get(i);
                String mobile = telMarketingCenter.getMobile();
                MobileArea mobileArea = new MobileArea();
                if (StringUtils.isNotBlank(mobile)) {
                    Object cityName = mobileAreaService.locationAPI.call(mobile);
                    if (null!=cityName && StringUtils.isNotEmpty(cityName.toString())) {
                        Area area = areaRepository.findByName(cityName.toString());
                        mobileArea.setMobile(mobile);
                        mobileArea.setArea(area);
                        areaList.add(mobileArea);
                    }
                }
            }
            logger.debug("thread :->{} ,mobile area data refresh over,get tel data count->{}, refresh data count :-> {}", Thread.currentThread().getName(), telMarketingCenterList.size(), areaList.size());
            mobileAreaRepository.save(areaList);
            return true;
        }
    }


    class MobileAreaSyncObserver implements Observer {
        @Override
        public void update(Observable o, Object arg) {
            //重启线程
            logger.debug("5秒后重启线程[MobileAreaSyncThread]");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                logger.error("thread sleep over by exception", e);
            }

            new MobileAreaSyncThread().run();
        }
    }

    class MobileAreaSyncThread extends Observable implements Runnable {

        public void callRestart() {
            super.setChanged();
            super.notifyObservers();
        }

        @Override
        public void run() {
            logger.info("thread mobile area sync task begin,thread id --> {}",Thread.currentThread().getId());
            while(ApplicationContextHolder.getApplicationContext()==null){
                try {
                    Thread.sleep(1000 * 60);
                } catch (InterruptedException e) {
                    logger.error("mobile ares sync thread error",e);
                }
            }
            this.addObserver(new MobileAreaSyncObserver());
            int i=0;

            while (true) {
                //百度API 手机归属地查询 峰值为200/s ,线程处理两百条数据后 休眠10s
                if(i == 200){
                    try {
                        i=0;
                        logger.info("mobile area sync num :200,thread sleep 10000 s");
                        Thread.sleep(1000 *10);
                    } catch (InterruptedException e) {
                       logger.error("mobile ares sync thread error",e);
                    }
                }
                String mobile = taskRunningService.getRedisSyncMobile();
                if (StringUtils.isBlank(mobile)) {
                    continue;
                }
                logger.debug("mobile area sync task ,mobile -> {}" ,mobile);
                try {
                    mobileAreaService.save(mobile);
                    i++;
                } catch (Exception e) {
                    logger.error("同步手机地区发生异常", e);
                    this.callRestart();
                }
            }
        }
    }
}
