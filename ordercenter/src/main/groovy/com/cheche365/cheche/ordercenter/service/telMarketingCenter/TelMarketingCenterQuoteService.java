package com.cheche365.cheche.ordercenter.service.telMarketingCenter;

import com.alibaba.fastjson.JSON;
import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.common.util.DoubleUtils;
import com.cheche365.cheche.common.util.StringUtil;
import com.cheche365.cheche.core.model.AutoType;
import com.cheche365.cheche.core.model.MoApplicationLog;
import com.cheche365.cheche.core.mongodb.repository.MoApplicationLogRepository;
import com.cheche365.cheche.core.serializer.SerializerUtil;
import com.cheche365.cheche.core.util.CacheUtil;
import com.cheche365.cheche.manage.common.model.TelMarketingCenterRepeatInfo;
import com.cheche365.cheche.manage.common.repository.TelMarketingCenterRepeatInfoRepository;
import com.cheche365.cheche.manage.common.service.BaseService;
import com.cheche365.cheche.manage.common.service.QuoteFlowConfigService;
import com.cheche365.cheche.ordercenter.web.model.telMarketingCenter.QuoteCompanyData;
import com.cheche365.cheche.ordercenter.web.model.telMarketingCenter.QuoteHistoryDetailJsonObject;
import com.cheche365.cheche.ordercenter.web.model.telMarketingCenter.QuoteRecordExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TelMarketingCenterQuoteService extends BaseService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MoApplicationLogRepository applicationLogMongoRepository;
    @Autowired
    private TelMarketingCenterRepeatInfoRepository telMarketingCenterRepeatInfoRepository;
    @Autowired
    private QuoteFlowConfigService quoteFlowConfigService;

    public List<QuoteHistoryDetailJsonObject> getQuoteList(Long repeatId) {
        List<QuoteHistoryDetailJsonObject> dataList = new ArrayList<>();

        /******************   原MySql逻辑    start  ******************/
//        List<ApplicationLog> logList = applicationLogRepository.findBySourceTableAndRefRepeatId(TelMarketingCenterRepeatInfo.Enum.APPLICATION_LOG, repeatId);
        /******************   原MySql逻辑    end  ******************/


        /******************   MongoDB逻辑    start  ******************/
        List<TelMarketingCenterRepeatInfo> repeatInfoList = telMarketingCenterRepeatInfoRepository.findBySourceTableAndRepeatId(TelMarketingCenterRepeatInfo.Enum.APPLICATION_LOG, repeatId);
        if (CollectionUtils.isEmpty(repeatInfoList))
            return dataList;
        List<String> logIdStrList = new ArrayList<String>();
        List<Long> logIdLongList = new ArrayList<Long>();

        for (TelMarketingCenterRepeatInfo repeatInfo : repeatInfoList) {
            try {
                logIdLongList.add(Long.parseLong(repeatInfo.getSourceId()));
            } catch (NumberFormatException e) {
                logIdStrList.add(repeatInfo.getSourceId());
            }

        }


        String[] logIdStrArr = logIdStrList.toArray(new String[logIdStrList.size()]);
        Long[] logIdLongArr = logIdLongList.toArray(new Long[logIdLongList.size()]);

        logger.debug("get String log id ->{} Long repeat id ->{} ", logIdStrArr, logIdLongArr);


        List<MoApplicationLog> logList = applicationLogMongoRepository.findByIdInOrderByIdDesc(logIdStrArr);
        logList.addAll(applicationLogMongoRepository.findByIdInOrderByIdDesc(logIdLongArr));

        logger.debug("get application log from mongo db,size: ->{}", logList.size());

        /******************   MongoDB逻辑    end  ******************/

        List<QuoteRecordExt> quoteRecordList = this.convertToQuoteRecord(logList);
        Map<String, List<QuoteRecordExt>> autoRecordsMap = quoteRecordList.stream().collect(Collectors.groupingBy(QuoteRecordExt::getCarNo));
        for (String carNo : autoRecordsMap.keySet()) {
            QuoteHistoryDetailJsonObject quoteHistoryDetailJsonObject = new QuoteHistoryDetailJsonObject();
            List<QuoteRecordExt> quoteRecordExts = autoRecordsMap.get(carNo);
            QuoteRecordExt quoteRecordExt = quoteRecordExts.get(0);

            quoteHistoryDetailJsonObject.setCarNo(carNo);
            quoteHistoryDetailJsonObject.setCarVin(StringUtil.convertNull(quoteRecordExt.getCarVin()));
            quoteHistoryDetailJsonObject.setEngineNo(StringUtil.convertNull(quoteRecordExt.getEngineNo()));
            quoteHistoryDetailJsonObject.setEnrollDate(StringUtil.convertNull(quoteRecordExt.getEnrollDate()));
            quoteHistoryDetailJsonObject.setAutoTypeName(StringUtil.convertNull(quoteRecordExt.getAutoTypeName()));
            quoteHistoryDetailJsonObject.setAutoModel(StringUtil.convertNull(quoteRecordExt.getAutoModel()));
            quoteHistoryDetailJsonObject.setQuoteCompanyDataList(this.createQuoteCompanyData(quoteRecordExts));

            dataList.add(quoteHistoryDetailJsonObject);
        }
        return dataList;
    }

    private List<QuoteCompanyData> createQuoteCompanyData(List<QuoteRecordExt> quoteRecordExts) {
        List<QuoteCompanyData> quoteCompanyDataList = new ArrayList<>();
        Map<String, List<QuoteRecordExt>> companyQuoteRecordsMap = quoteRecordExts.stream().collect(Collectors.groupingBy(QuoteRecordExt::getInsuranceCompanyName));
        for (String insuranceCompanyName : companyQuoteRecordsMap.keySet()) {
            List<QuoteRecordExt> extList = companyQuoteRecordsMap.get(insuranceCompanyName);

            QuoteCompanyData quoteCompanyData = new QuoteCompanyData();
            quoteCompanyData.setCompanyName(insuranceCompanyName);
            quoteCompanyData.setQuoteNum(extList.size());
            quoteCompanyData.setQuoteRecordExtList(extList);

            quoteCompanyDataList.add(quoteCompanyData);
        }

        return quoteCompanyDataList;
    }


    /******************
     * 原MySql逻辑    start
     ******************/
    /*public List convertToQuoteRecord(List<ApplicationLog> logList) {
        List<QuoteRecordExt> quoteRecordList = new ArrayList<>();
        for (ApplicationLog applicationLog : logList) {
            QuoteRecordExt quoteRecordExt = CacheUtil.doJacksonDeserialize(applicationLog.getLogMessage(), QuoteRecordExt.class);
            quoteRecordExt.setCreateTime(applicationLog.getCreateTime());
            quoteRecordExt.setLogId(applicationLog.getId());
            quoteRecordExt.setCarNo(quoteRecordExt.getAuto().getLicensePlateNo());
            quoteRecordExt.setInsuranceCompanyName(quoteRecordExt.getInsuranceCompany().getName());
            quoteRecordExt.setQuoteDetailString(SerializerUtil.generatePremiumDetail(quoteRecordExt));
            quoteRecordExt.setTotalAmout(DoubleUtils.add(quoteRecordExt.getPremium(), quoteRecordExt.getCompulsoryPremium(), quoteRecordExt.getAutoTax()));
            quoteRecordExt.setQuoteKindNum(quoteRecordExt.getInsurancePackage().countQuotedFields());
            quoteRecordExt.setCreateTimeString(DateUtils.getDateString(applicationLog.getCreateTime(), DateUtils.DATE_LONGTIME24_PATTERN));
            quoteRecordExt.setUserId(applicationLog.getUser().getId());
            quoteRecordExt.setChannelName(quoteRecordExt.getChannel().getDescription());
            quoteRecordExt.setCarVin(quoteRecordExt.getAuto().getVinNo());
            quoteRecordExt.setEngineNo(quoteRecordExt.getAuto().getEngineNo());
            quoteRecordExt.setEnrollDate(DateUtils.getDateString(quoteRecordExt.getAuto().getEnrollDate(), DateUtils.DATE_SHORTDATE_PATTERN));
            AutoType autoType = quoteRecordExt.getAuto().getAutoType();
            quoteRecordExt.setAutoTypeName(autoType == null ? "" : autoType.getCode());
            quoteRecordExt.setAutoModel(autoType == null ? "" : autoType.getModel());
            quoteRecordExt.setQuoteKind(quoteFlowConfigService.realQuote(quoteRecordExt.getType(), quoteRecordExt.getInsuranceCompany()));

            quoteRecordExt.setApplicant(null);
            quoteRecordExt.setAuto(null);
            quoteRecordExt.setInsuranceCompany(null);
            quoteRecordExt.setArea(null);
            quoteRecordExt.setChannel(null);

            quoteRecordList.add(quoteRecordExt);
        }
        return quoteRecordList;
    }*/
    /******************   原MySql逻辑    end  ******************/


    /******************
     * MongoDB逻辑    start
     ******************/
    public List convertToQuoteRecord(List<MoApplicationLog> logList) {
        List<QuoteRecordExt> quoteRecordList = new ArrayList<>();
        for (MoApplicationLog applicationLog : logList) {
            QuoteRecordExt quoteRecordExt = CacheUtil.doJacksonDeserialize(JSON.toJSONString(applicationLog.getLogMessage()), QuoteRecordExt.class);
            quoteRecordExt.setCreateTime(applicationLog.getCreateTime());
            quoteRecordExt.setLogId(applicationLog.getId());
            quoteRecordExt.setCarNo(quoteRecordExt.getAuto().getLicensePlateNo());
            quoteRecordExt.setInsuranceCompanyName(quoteRecordExt.getInsuranceCompany() == null ? "未知" : quoteRecordExt.getInsuranceCompany().getName());
            quoteRecordExt.setQuoteDetailString(SerializerUtil.generatePremiumDetail(quoteRecordExt));
            quoteRecordExt.setTotalAmout(DoubleUtils.add(quoteRecordExt.getPremium(), quoteRecordExt.getCompulsoryPremium(), quoteRecordExt.getAutoTax()));
            quoteRecordExt.setQuoteKindNum(quoteRecordExt.getInsurancePackage().countQuotedFields());
            quoteRecordExt.setCreateTimeString(DateUtils.getDateString(applicationLog.getCreateTime(), DateUtils.DATE_LONGTIME24_PATTERN));
            quoteRecordExt.setUserId(applicationLog.getUser() != null ? applicationLog.getUser().getId() : null);
            quoteRecordExt.setChannelName(quoteRecordExt.getChannel().getDescription());
            quoteRecordExt.setCarVin(quoteRecordExt.getAuto().getVinNo());
            quoteRecordExt.setEngineNo(quoteRecordExt.getAuto().getEngineNo());
            quoteRecordExt.setEnrollDate(DateUtils.getDateString(quoteRecordExt.getAuto().getEnrollDate(), DateUtils.DATE_SHORTDATE_PATTERN));
            AutoType autoType = quoteRecordExt.getAuto().getAutoType();
            quoteRecordExt.setAutoTypeName(autoType == null ? "" : autoType.getCode());
            quoteRecordExt.setAutoModel(autoType == null ? "" : autoType.getModel());
            quoteRecordExt.setQuoteKind(quoteFlowConfigService.quoteType(quoteRecordExt.getType()));

            quoteRecordExt.setApplicant(null);
            quoteRecordExt.setAuto(null);
            quoteRecordExt.setInsuranceCompany(null);
            quoteRecordExt.setArea(null);
            quoteRecordExt.setChannel(null);

            quoteRecordList.add(quoteRecordExt);
        }
        return quoteRecordList;
    }
    /******************   MongoDB逻辑    end  ******************/

}


