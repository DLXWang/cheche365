package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.core.model.Marketing;
import com.cheche365.cheche.core.model.MarketingRule;
import com.cheche365.cheche.scheduletask.service.task.MarketingRefreshService;
import org.apache.velocity.util.ArrayListWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xu.yelong on 2016/8/16.
 * 刷新配置活动的状态，生成相关banner图
 */
@Service
public class MarketingRefreshTask extends BaseTask {
    @Autowired
    private MarketingRefreshService MarketingRefreshService;

    private Logger logger= LoggerFactory.getLogger(MarketingRefreshTask.class);

    @Override
    protected  void doProcess() throws Exception {
        MarketingRefreshService.dispatcher();
    }
}
