package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.email.model.EmailInfo;
import com.cheche365.cheche.scheduletask.model.ChebaoyiLedgerInfo;
import com.cheche365.cheche.scheduletask.model.MessageInfo;
import com.cheche365.cheche.scheduletask.service.task.ChebaoyiLedgerService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 车保易台账
 * Created by liulu on 2018/7/30.
 */
@Service
public class ChebaoyiLedgerTask extends BaseTask {

    Logger logger = LoggerFactory.getLogger(ChebaoyiLedgerTask.class);
    private static final String START_TIME_CACHE_KEY = "schedulestask.ledger.chebaoyi";
    private String emailconfigPath = "/emailconfig/chebaoyi_ledger_email.yml";
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private ChebaoyiLedgerService chebaoyiLedgerService;


    @Override
    protected void doProcess() throws Exception {
        messageInfoList.add(getMessageInfo());
    }

    private MessageInfo getMessageInfo() throws IOException {
        String cachedStartDate = redisTemplate.opsForValue().get(START_TIME_CACHE_KEY);
        Date startTime;
        if (StringUtils.isBlank(cachedStartDate)) {
            startTime = DateUtils.getCustomDate(new Date(), -1, 15, 35, 0);
        } else {
            startTime = DateUtils.getDate(cachedStartDate, DateUtils.DATE_LONGTIME24_PATTERN);
        }
        Date endTime = DateUtils.getCustomDate(new Date(), 0, 15, 34, 59);

        //获取内容参数
        Map<String, List<ChebaoyiLedgerInfo>> paramMap = chebaoyiLedgerService.getContentParam(startTime,endTime);
        EmailInfo emailInfo = assembleEmailInfo(emailconfigPath, null);
        addAttachment(emailInfo, emailconfigPath, null, paramMap);
        return MessageInfo.createMessageInfo(emailInfo);
    }

}
