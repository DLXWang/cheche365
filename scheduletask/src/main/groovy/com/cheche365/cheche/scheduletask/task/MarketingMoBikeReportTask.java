package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.MarketingSuccess;
import com.cheche365.cheche.core.repository.MarketingRepository;
import com.cheche365.cheche.core.repository.MarketingSuccessRepository;
import com.cheche365.cheche.email.model.EmailInfo;
import com.cheche365.cheche.scheduletask.model.Marketing201708003ReportModel;
import com.cheche365.cheche.scheduletask.model.MarketingMoBikeReportModel;
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
public class MarketingMoBikeReportTask extends BaseTask {
    private static final List MARKETING_ID = Arrays.asList(91,92,93,94);



    private String emailconfigPath = "/emailconfig/marketing_mobike_report.yml";

    @Autowired
    private MarketingSuccessRepository marketingSuccessRepository;

    @Override
    protected void doProcess() throws Exception {
        messageInfoList.add(getMessageInfo());
    }


    private MessageInfo getMessageInfo() throws IOException {
       Date startDate=DateUtils.getCustomDate(new Date(),-1,0,0,0);
       Date endDate=DateUtils.getCustomDate(new Date(),-1,23,59,59);

        List<MarketingSuccess> marketingSuccessList = marketingSuccessRepository.findByMarketingIdAndCreateTime(MARKETING_ID,startDate,endDate);
        List<MarketingMoBikeReportModel> emailDataList = new ArrayList<>();

        marketingSuccessList.forEach(marketingSuccess -> {
            MarketingMoBikeReportModel model = new MarketingMoBikeReportModel();
            model.setMobile(marketingSuccess.getMobile());
            model.setLicensePlateNo(marketingSuccess.getLicensePlateNo());
            model.setMarketingName(marketingSuccess.getMarketing().getName());
            emailDataList.add(model);
        });
        EmailInfo emailInfo = assembleEmailInfo(this.emailconfigPath, new HashMap() {{
            put("prevDate", DateUtils.getDateString(DateUtils.getCustomDate(new Date(), -1,0,0,0),DateUtils.DATE_SHORTDATE_PATTERN));
        }});
        addSimpleAttachment(emailInfo, this.emailconfigPath,new HashMap() {{
            put("prevDate", DateUtils.getDateString(DateUtils.getCustomDate(new Date(), -1,0,0,0),DateUtils.DATE_SHORTDATE_PATTERN));
        }}, emailDataList);
        return MessageInfo.createMessageInfo(emailInfo);
    }
}
