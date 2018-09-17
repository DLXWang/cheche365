package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.core.model.Gift;
import com.cheche365.cheche.core.model.GiftStatus;
import com.cheche365.cheche.core.service.GiftService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

/**
 * Created by WF on 2015/11/10.
 */
@Service
public class GiftExpireTask extends BaseTask  {
    Logger logger = LoggerFactory.getLogger(GiftExpireTask.class);

    @Autowired
    private GiftService giftService;


    @Override
    protected void doProcess() throws Exception {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime nowDateTime = LocalDateTime.now();
        long dataCount = giftService.getCountByExpireDate(dtf.format(nowDateTime));
        int pageSize = 1000;
        long pages = dataCount/pageSize + (dataCount%pageSize == 0 ? 0 : 1);
        int count = 0;
        for (int m = 0; m < pages; m++) {
            Pageable pageable = new PageRequest(0, pageSize);
            List<Gift> gifts = giftService.getExpireDatePutGray(dtf.format(nowDateTime), pageable);

            if (null == gifts) {
                logger.debug(" 定时任务扫描过期优惠券状态置为已过期 过期优惠券数量为0 task is finished");
                return;
            }
            for (Gift gift : gifts) {
                try {
                    if (Objects.isNull(gift.getStatus()) || "已创建".equals((Objects.isNull(gift.getStatus()) ? "已创建" : gift.getStatus().getStatus()))) {
                        gift.setStatus(GiftStatus.Enum.EXCEEDED_4);
                        giftService.save(gift);
                        count++;
                    }
                } catch (Exception e) {
                    logger.debug(" 定时任务扫描过期优惠券状态置为已过期 修改异常" + e);
                    continue;
                }
            }

        }
        logger.debug(" 定时任务扫描过期优惠券状态置为已过期 一共有" + count + "个优惠券过期 ");
    }
}
