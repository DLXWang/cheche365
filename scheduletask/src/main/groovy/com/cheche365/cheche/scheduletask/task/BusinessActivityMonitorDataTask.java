package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.core.util.RuntimeUtil;
import com.cheche365.cheche.scheduletask.service.task.BusinessActivityMonitorDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 商务活动监控数据
 * 执行时间：1小时一次
 * 将redis中的数据保存到数据库中
 * Created by sunhuazhong on 2015/8/28.
 */
@Service
public class BusinessActivityMonitorDataTask extends BaseTask {

    @Autowired
    private BusinessActivityMonitorDataService businessActivityMonitorDataService;

    /**
     * 执行任务详细内容
     *
     * @return
     */
    @Override
    public void doProcess() {
        //商务活动监控数据
        if (RuntimeUtil.isProductionEnv()) businessActivityMonitorDataService.updateBusinessActivityMonitorData();
    }

}
