package com.cheche365.cheche.scheduletask.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by mahong on 2016/2/25.
 * 第三方合作-百度地图 已报价未下单用户车辆信息同步
 */
@Service
public class BaiduAutoSyncTask extends BaseTask {

    Logger logger = LoggerFactory.getLogger(BaiduAutoSyncTask.class);


    @Override
    public void doProcess() {
        if (logger.isDebugEnabled()) {
            logger.debug("定时任务:第三方合作-百度地图已报价未下单用户车辆信息同步命令写入redis -- 开始");
        }


        if (logger.isDebugEnabled()) {
            logger.debug("定时任务:第三方合作-百度地图已报价未下单用户车辆信息同步命令写入redis -- 结束");
        }
    }
}
