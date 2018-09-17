package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.MarketingSuccess;
import com.cheche365.cheche.core.repository.MarketingRepository;
import com.cheche365.cheche.core.repository.MarketingSuccessRepository;
import com.cheche365.cheche.email.model.EmailInfo;
import com.cheche365.cheche.scheduletask.model.Marketing201708003ReportModel;
import com.cheche365.cheche.scheduletask.model.MessageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * Created by yellow on 2017/9/13.
 */
@Service
public class Marketing201708003ReportTask extends BaseTask {
    private static final String MARKETING_CODE = "201708003";

    private static final String CACHE_PREV_ID = "schedulestask.marketing201708003.report.prev.id";

    private String emailconfigPath = "/emailconfig/marketing_201708003_report.yml";

    @Autowired
    private MarketingRepository marketingRepository;

    @Autowired
    private MarketingSuccessRepository marketingSuccessRepository;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void doProcess() throws Exception {
        messageInfoList.add(getMessageInfo());
    }


    private MessageInfo getMessageInfo() throws IOException {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, -10);

        Long prevId = stringRedisTemplate.opsForValue().get(CACHE_PREV_ID) == null ? marketingSuccessRepository.findMaxIdByTime(calendar.getTime()) : Long.valueOf(stringRedisTemplate.opsForValue().get(CACHE_PREV_ID));
        List<MarketingSuccess> marketingSuccessList = marketingSuccessRepository.findByMarketingIdAndLargeThanId(marketingRepository.findFirstByCode(MARKETING_CODE).getId(), prevId);
        List<Marketing201708003ReportModel> emailDataList = new ArrayList<>();

        marketingSuccessList.forEach(marketingSuccess -> {
            Marketing201708003ReportModel model = new Marketing201708003ReportModel();
            model.setMobile(marketingSuccess.getMobile());
            model.setEffectDate(DateUtils.getDateString(marketingSuccess.getEffectDate(), DateUtils.DATE_LONGTIME24_PATTERN));
            emailDataList.add(model);
            stringRedisTemplate.opsForValue().set(CACHE_PREV_ID, marketingSuccess.getId().toString());
        });
        EmailInfo emailInfo = assembleEmailInfo(this.emailconfigPath, new HashMap() {{
            put("currentDateTime", DateUtils.getDateString(new Date(), "yyyy年MM月dd日HH时mm分ss秒"));
        }});
        addSimpleAttachment(emailInfo, this.emailconfigPath, new HashMap() {{
            put("currentDateTime", DateUtils.getDateString(new Date(), "yyyy年MM月dd日HH时mm分ss秒"));
        }}, emailDataList);
        return MessageInfo.createMessageInfo(emailInfo);
    }

    @Override
    protected void sendOnOff() {
        send = true;
        if (dataSize == 0) {
            send = false;
            logger.info("邮件数据为空，将不发送此邮件");
        }
    }
}
