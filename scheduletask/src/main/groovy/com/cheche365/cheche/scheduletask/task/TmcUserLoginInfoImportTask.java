package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.scheduletask.service.task.TmcUserLoginInfoImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by xu.yelong on 2016/11/17.
 * 用户登录数据进入电销
 */
@Service
public class TmcUserLoginInfoImportTask extends BaseTask {

    @Autowired
    private TmcUserLoginInfoImportService tmcUserLoginInfoImportService;

    @Override
    protected void doProcess() throws Exception {
        tmcUserLoginInfoImportService.importData();
    }
}
