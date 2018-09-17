package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.service.IResourceService;
import com.cheche365.cheche.email.model.EmailInfo;
import com.cheche365.cheche.scheduletask.constants.TaskConstants;
import com.cheche365.cheche.scheduletask.model.MessageInfo;
import com.cheche365.cheche.scheduletask.model.PurchaseOrderInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * 删除支付二维码图片任务
 * 定时时间：每天晚上23点
 * Created by sunhuazhong on 2015/6/23.
 */
@Service
public class DeletePaymentWechatQrDirectoryTask extends BaseTask {

    Logger logger = LoggerFactory.getLogger(DeletePaymentWechatQrDirectoryTask.class);

    @Autowired
    private IResourceService resourceService;


    /**
     * 执行任务详细内容
     *
     * @return
     */
    @Override
    public void doProcess() throws Exception {
        // 删除当前日期之前的目录（不包括今天，昨天，前天的目录）
        String today = DateUtils.getCurrentDateString("yyyyMMdd");
        Path filePath = Paths.get(resourceService.getResourceAbsolutePath(resourceService.getProperties().getPaymentWechatQrPath()));
        File rootDirectory = filePath.toFile();
        if(rootDirectory.exists() && rootDirectory.isDirectory()) {
            String[] children = rootDirectory.list();
            //递归删除目录中的子目录下（不包括今天，昨天，前天的目录）
            for (String child : children) {
                if(logger.isDebugEnabled()) {
                    logger.debug("wechat qr directory path:" + rootDirectory.getAbsolutePath() + "\\" + child);
                }
                if(Integer.parseInt(today) - Integer.parseInt(child) > 2) {
                    File childFile = new File(rootDirectory, child);
                    if(childFile.isDirectory()) {
                        String[] items = childFile.list();
                        for (String item : items) {
                            new File(childFile, item).delete();
                        }
                    }
                    childFile.delete();
                }
            }
        }
    }
}
