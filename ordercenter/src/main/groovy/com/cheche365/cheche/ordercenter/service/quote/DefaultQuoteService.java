package com.cheche365.cheche.ordercenter.service.quote;

import com.alibaba.fastjson.JSON;
import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.constants.WebConstants;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.mongodb.repository.MoApplicationLogRepository;
import com.cheche365.cheche.core.repository.*;
import com.cheche365.cheche.core.service.AutoService;
import com.cheche365.cheche.core.service.GiftService;
import com.cheche365.cheche.core.util.AutoUtils;
import com.cheche365.cheche.core.util.BeanUtil;
import com.cheche365.cheche.core.util.CacheUtil;
import com.cheche365.cheche.manage.common.model.ActivityMonitorUrl;
import com.cheche365.cheche.manage.common.repository.ActivityMonitorUrlRepository;
import com.cheche365.cheche.manage.common.model.PurchaseOrderExtend;
import com.cheche365.cheche.manage.common.service.sms.SMSHelper;
import com.cheche365.cheche.ordercenter.model.QuoteQuery;
import com.cheche365.cheche.manage.common.service.gift.OrderCenterGiftService;
import com.cheche365.cheche.ordercenter.service.user.OrderCenterInternalUserManageService;
import com.cheche365.cheche.ordercenter.util.HttpClientUtils;
import com.cheche365.cheche.ordercenter.web.model.ActivityMonitorUrlViewModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by wangfei on 2015/10/23.
 */
@Service
public class DefaultQuoteService {
    private static Logger logger = LoggerFactory.getLogger(DefaultQuoteService.class);
    protected static String BASE_REQUEST_PATH;
    protected static final String VERSION = "v1.3";
    // 临时变更车辆信息修改接口版本号
    protected static final String VERSION_1_6 = "v1.6";
    private static String PATH_GET_COMPANIES;
    private static String PATH_GET_QUOTE;
    private static String PATH_CHECK_INTERNAL_USER;
    private static String PATH_GET_MARKETINGS;
    private static String PATH_SAVE_QUOTE;
    private static String PATH_GET_SUPPLEMENT_PARAMS;
    private static final String USER_GIFT_PAGE_SIZE = "6";
    private static final String USER_GIFT_STATUS = "valid";
    protected static final String VERSION_1_5 = "v1.5";

    static {
        BASE_REQUEST_PATH = WebConstants.getDomainURL();
        PATH_GET_COMPANIES = BASE_REQUEST_PATH + "/" + VERSION + "/companies";
        PATH_GET_QUOTE = BASE_REQUEST_PATH + "/" + VERSION + "/quotes/default";
        PATH_CHECK_INTERNAL_USER = BASE_REQUEST_PATH + "/" + VERSION + "/users/login/internal";
        PATH_SAVE_QUOTE = BASE_REQUEST_PATH + "/" + VERSION + "/quotes";
        PATH_GET_MARKETINGS = BASE_REQUEST_PATH + "/" + VERSION + "/marketings/active";
        PATH_GET_SUPPLEMENT_PARAMS = BASE_REQUEST_PATH + "/" + VERSION_1_5 + "/quotes/params/format";
    }

    @Autowired
    private AutoService autoService;

    @Autowired
    private SMSHelper smsHelper;

    @Autowired
    private QuoteRecordRepository quoteRecordRepository;

    @Autowired
    private QuoteSupplementInfoRepository quoteSupplementInfoRepository;

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private OrderCenterInternalUserManageService orderCenterInternalUserManageService;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private GiftService giftService;

    @Autowired
    private ActivityMonitorUrlRepository activityMonitorUrlRepository;

    @Autowired
    private MoApplicationLogRepository applicationLogMongoRepository;

    @Autowired
    private OrderCenterGiftService orderCenterGiftService;

    /**
     * 获取保险公司
     * @param perfectDriverId
     * @param licensePlateNo
     * @return
     */
    public String getSupportCompanies(CloseableHttpClient httpClient,Long perfectDriverId, String licensePlateNo,String userAgent,String uniqueId) {
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("area", AutoUtils.getAreaOfAuto(licensePlateNo).getId().toString());
        Map<String,String> headerMap=new HashMap(){{put(HTTP.USER_AGENT,userAgent);put(WebConstants.ORDER_CENTER_TOKEN, uniqueId);}};
        String response = HttpClientUtils.doGetWithHeader(httpClient, PATH_GET_COMPANIES, paramsMap,headerMap);
        logger.debug("get companies api return json : {}", response);
        return response;
    }

    /**
     * 报价信息改变更新原数据
     * @param quoteQuery
     */
    protected void updateOriginalInfoBeforeQuote(QuoteQuery quoteQuery) {
        if (null == quoteQuery.getAuto()) {
            return;
        }
    }

    /**
     * 报价
     * @param httpClient
     * @param companyId
     * @param quoteQuery
     * @param userAgent
     * @return
     */
    public String quote(CloseableHttpClient httpClient, Long companyId, QuoteQuery quoteQuery, String userAgent,String uniqueId) {
        if("quoteRecord".equals(quoteQuery.getSource())){
            //从上次的报价记录中获取auto赋值给quoteQuery中的auto
            Auto auto = quoteQuery.getAuto();
            MoApplicationLog log;
            if(quoteQuery.getSourceId() != null){
                log = applicationLogMongoRepository.findById(quoteQuery.getSourceId());
            }else{
                log = applicationLogMongoRepository.findById(quoteQuery.getSourceIdStr());
            }
            QuoteRecord record =  CacheUtil.doJacksonDeserialize(JSON.toJSONString(log.getLogMessage()), QuoteRecord.class);
            BeanUtil.copyPropertiesIgnore(record.getAuto(), auto);
        }else if("renewInsurance".equals(quoteQuery.getSource())){
            Auto auto = quoteQuery.getAuto();
            PurchaseOrder order = purchaseOrderRepository.findOne(quoteQuery.getSourceId());
            BeanUtil.copyPropertiesIgnore(order.getAuto(), auto);
        }else{
            updateOriginalInfoBeforeQuote(quoteQuery);
            getRequestAuto(quoteQuery);
        }
        String reqJsonStr;
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
            reqJsonStr = mapper.writeValueAsString(quoteQuery);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Fail to serialize quoteQuery to json", e);
        }
        logger.debug("quote api request jsonStr -> {}, companyId -> {}", reqJsonStr, companyId);
        Map<String,String> headerMap=new HashMap(){{put(HTTP.USER_AGENT, userAgent);put(WebConstants.ORDER_CENTER_TOKEN, uniqueId);}};
        String response = HttpClientUtils.doPostWithJsonAndHeader(httpClient, PATH_GET_QUOTE, reqJsonStr, headerMap);
        logger.debug("get quote api return json : {}, companyId -> {}", response, companyId);
        return response;
    }

    /**
     * 组织报价Auto
     * @param quoteQuery
     */
    protected void getRequestAuto(QuoteQuery quoteQuery) {}

    /**
     * 登录
     * @param client
     * @param userId
     * @param userAgent
     * @param internalUser
     * @return
     */
    public String login(CloseableHttpClient client, Long userId, String userAgent, InternalUser internalUser) {
        Map<String, String> reqMap = new HashMap<>();
        reqMap.put("email", internalUser.getEmail());
        reqMap.put("password", internalUser.getPassword());
        reqMap.put("userId", userId.toString());
        Map<String,String> headerMap=new HashMap(){{put(HTTP.USER_AGENT, userAgent);}};
        String response = HttpClientUtils.doPostWithHeader(client, PATH_CHECK_INTERNAL_USER, reqMap, headerMap);
        logger.debug("get check internal user login api return json : {}", response);
        return response;
    }

    /**
     * 保存报价
     * @param source
     * @param sourceId
     * @param companyId
     * @param client
     * @param quoteRecord
     * @param userAgent
     * @return
     */
    public String saveQuote(String source, Long sourceId, Long companyId, CloseableHttpClient client, QuoteRecord quoteRecord,
                            String userAgent,String uniqueId) {
        String reqJsonStr = CacheUtil.doJacksonSerialize(quoteRecord);
        logger.debug("do save quote api request jsonStr -> {}", reqJsonStr);
        Map<String,String> headerMap=new HashMap(){{put(HTTP.USER_AGENT, userAgent);put(WebConstants.ORDER_CENTER_TOKEN, uniqueId);}};
        String response = HttpClientUtils.doPostWithJsonAndHeader(client, PATH_SAVE_QUOTE, reqJsonStr, headerMap);
        logger.debug("get save quote api return json : {}", response);
        return response;
    }

    /**
     * 用户地址
     * @param client
     * @param userAgent
     * @return
     */
    public String getUserAddress(CloseableHttpClient client, String userAgent,String uniqueId) {
        String PATH_USER_ADDRESS = BASE_REQUEST_PATH + "/" + VERSION + "/users/address?size=3&page=0";
        Map<String,String> headerMap=new HashMap(){{put(HTTP.USER_AGENT, userAgent);put(WebConstants.ORDER_CENTER_TOKEN, uniqueId);}};
        String response = HttpClientUtils.doGetWithHeader(client, PATH_USER_ADDRESS, null, headerMap);
        logger.debug("get user address api return json : {}", response);
        return response;
    }

    /**
     * 获取可参加活动
     * @param client
     * @param userAgent
     * @param quoteRecordId
     * @return
     */
    public String getMarketings(CloseableHttpClient client, String userAgent, Long quoteRecordId,String uniqueId) {
        Map<String, String> reqMap = new HashMap<>();
        reqMap.put("quoteRecordId", quoteRecordId.toString());
        Map<String,String> headerMap=new HashMap(){{put(HTTP.USER_AGENT, userAgent);put(WebConstants.ORDER_CENTER_TOKEN, uniqueId);}};
        String response = HttpClientUtils.doGetWithHeader(client, PATH_GET_MARKETINGS, reqMap, headerMap);
        logger.debug("get marketings api return json : {}", response);
        return response;
    }

    /**
     * 参加活动
     * @param client
     * @param code
     * @param userAgent
     * @return
     */
    public String addMarketing(CloseableHttpClient client, String code,String key, String userAgent,String uniqueId) {
        String reqPath = BASE_REQUEST_PATH + "/" + VERSION + "/quotes/"+key+"/discount/" + code;
        Map<String, String> reqMap = new HashMap<>();
        reqMap.put("userType", "ordercenter");
        Map<String,String> headerMap=new HashMap(){{put(HTTP.USER_AGENT, userAgent);put(WebConstants.ORDER_CENTER_TOKEN, uniqueId);}};
        String response = HttpClientUtils.doPostWithJsonAndHeader(client, reqPath, CacheUtil.doJacksonSerialize(reqMap), headerMap);
        logger.debug("get add marketing api return json : {}", response);
        return response;
    }



    /**
     * 获取用户可用优惠券
     * @param client
     * @param userAgent
     * @param page
     * @param quoteRecordId
     * @return
     */
    public String getGifts(CloseableHttpClient client, String userAgent, Integer page, Long quoteRecordId,Long purchaseOrderId,String uniqueId) {
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("status", USER_GIFT_STATUS);
        paramsMap.put("page", page.toString());
        paramsMap.put("size", USER_GIFT_PAGE_SIZE);
        paramsMap.put("quoteRecordId", quoteRecordId.toString());
        if(purchaseOrderId != null && purchaseOrderId != 0){
            paramsMap.put("purchaseOrderId", purchaseOrderId.toString());
        }
        logger.debug("request user enable gifts api json for quoteId {} : {}", quoteRecordId,  CacheUtil.doJacksonSerialize(paramsMap));
        String reqPath = BASE_REQUEST_PATH + "/" + VERSION + "/gifts";
        Map<String,String> headerMap=new HashMap(){{put(HTTP.USER_AGENT,userAgent);put(WebConstants.ORDER_CENTER_TOKEN, uniqueId);}};
        String response = HttpClientUtils.doGetWithHeader(client, reqPath, paramsMap, headerMap);
        logger.debug("get user enable gifts api return json : {}", response);
        //在返回结果的基础上 , 礼物集合再加上已经使用过的礼物
        JSONObject responseJson = JSONObject.fromObject(response);
        JSONObject pageJson = (JSONObject) responseJson.get("data");
        JSONArray contentJson = (JSONArray) pageJson.get("content");
        List<Gift> gifts = CacheUtil.doListJacksonDeserialize(contentJson.toString(), Gift.class);
        QuoteRecord quoteRecord = quoteRecordRepository.findOne(quoteRecordId);
        giftService.mergeUsedGifts(quoteRecord, purchaseOrderId, gifts);
        if (CollectionUtils.isEmpty(gifts)) {
            return response;
        }
        Page<Gift> giftPage = new Page<>((Integer) pageJson.get("number"), (Integer) pageJson.get("size"), gifts.size());
        giftPage.setContent(gifts);
        pageJson = CacheUtil.doJacksonDeserialize(CacheUtil.doJacksonSerialize(giftPage), JSONObject.class);
        responseJson.put("data", pageJson);

        response = responseJson.toString();
        logger.debug("get user enable gifts api return json : {}", response);
        return response;
    }

    /**
     * 未登录获取车辆信息
     * @param owner
     * @param identity
     * @param licensePlateNo
     * @return
     */
    public String getVehicleQuoteInfo(String owner, String identity,String identityType, String licensePlateNo) {
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("licensePlateNo",licensePlateNo);
        if(StringUtils.isNotBlank(owner)){
            paramsMap.put("owner",owner);
        }
        if(StringUtils.isNotBlank(identity)){
            paramsMap.put("identity",identity);
        }
        if(StringUtils.isNotBlank(identityType)){
            paramsMap.put("identityType",identityType);
        }
        logger.debug("request auto quote info api json for licensePlateNo {} : {}", licensePlateNo,  CacheUtil.doJacksonSerialize(paramsMap));
        String reqPath = BASE_REQUEST_PATH + "/" + VERSION_1_6 + "/autos/license";
        Map<String,String> headerMap=new HashMap(){{put(HTTP.USER_AGENT, orderCenterAgent());}};
        String response = HttpClientUtils.doGetWithHeader(null, reqPath, paramsMap,headerMap);
        JSONObject responseJson = JSONObject.fromObject(response);
        if(responseJson.get("code").toString().equals("200")){
            logger.debug("get auto quote info api return json : {}", response);
            return responseJson.get("data").toString();
        }
        return null;
    }

    /**
     * 获取省份
     * @return
     */
    public String getProvinces() {
        String PATH_PROVINCES_ADDRESS = BASE_REQUEST_PATH + "/" + VERSION + "/areas/provinces";
        Map<String,String> headerMap=new HashMap(){{put(HTTP.USER_AGENT, orderCenterAgent());}};
        String response = HttpClientUtils.doGetWithHeader(null, PATH_PROVINCES_ADDRESS, null,headerMap);
        logger.debug("get provinces api return json : {}", response);
        return response;
    }

    /**
     * 获取城市
     * @param provinceId
     * @return
     */
    public String getCities(String provinceId) {
        String PATH_CITIES_ADDRESS = BASE_REQUEST_PATH + "/" + VERSION + "/areas/" + provinceId + "/cities";
        Map<String,String> headerMap=new HashMap(){{put(HTTP.USER_AGENT, orderCenterAgent());}};
        String response = HttpClientUtils.doGetWithHeader(null, PATH_CITIES_ADDRESS, null,headerMap);
        logger.debug("get cities api return json : {}", response);
        return response;
    }

    /**
     * 获取区县
     * @param cityId
     * @return
     */
    public String getDistricts(String cityId) {
        String PATH_CITIES_ADDRESS = BASE_REQUEST_PATH + "/" + VERSION + "/areas/" + cityId + "/districts";
        Map<String,String> headerMap=new HashMap(){{put(HTTP.USER_AGENT, orderCenterAgent());}};
        String response = HttpClientUtils.doGetWithHeader(null, PATH_CITIES_ADDRESS, null, headerMap);
        logger.debug("get districts api return json : {}", response);
        return response;
    }

    /**
     * 提交订单
     * @param client
     * @param purchaseOrder
     * @param orderNo
     * @param userAgent
     * @return
     */
    public String supplementOrder(CloseableHttpClient client, PurchaseOrderExtend purchaseOrder, String orderNo, String userAgent,String uniqueId) {
        //电销自定义优惠
        if(!StringUtils.isBlank(purchaseOrder.getComment())){
            String newComm = orderCenterInternalUserManageService.getCurrentInternalUser().getName() + ":" + purchaseOrder.getComment();
            purchaseOrder.setComment(newComm);
        }
        String reqJson = CacheUtil.doJacksonSerialize(purchaseOrder);
        logger.debug("save supplement order api request json for orderNo {} : {}", orderNo, reqJson);
        Map<String,String> headerMap=new HashMap(){{put(HTTP.USER_AGENT, userAgent);put(WebConstants.ORDER_CENTER_TOKEN, uniqueId);}};
        String response = HttpClientUtils.doPutWithJsonAndHeader(client, BASE_REQUEST_PATH + "/" + VERSION + "/quotes/" + orderNo + "/order",
            reqJson, headerMap);
        logger.debug("save supplement order api return json for orderNo {} : {}", orderNo, response);
        return response;
    }


    /**
     * 提交订单
     * @param client
     * @param purchaseOrder
     * @param quoteId
     * @param userAgent
     * @return
     */
    public String saveOrder(CloseableHttpClient client, PurchaseOrderExtend purchaseOrder, Long quoteId, String userAgent,String uniqueId) {
        //电销自定义优惠
        if(!StringUtils.isBlank(purchaseOrder.getComment())){
            String newComm = orderCenterInternalUserManageService.getCurrentInternalUser().getName() + ":" + purchaseOrder.getComment();
            purchaseOrder.setComment(newComm);
        }
        orderCenterGiftService.doTelMarketingGift(purchaseOrder, quoteId);
        //额外赠送礼品
        orderCenterGiftService.doTelMarketingResendGift(purchaseOrder, quoteId);
        String reqJson = CacheUtil.doJacksonSerialize(purchaseOrder);
        logger.debug("save order api request json for quoteId {} : {}", quoteId, reqJson);
        Map<String,String> headerMap=new HashMap(){{put(HTTP.USER_AGENT, userAgent);put(WebConstants.ORDER_CENTER_TOKEN, uniqueId);}};
        String response = HttpClientUtils.doPutWithJsonAndHeader(client, BASE_REQUEST_PATH + "/" + VERSION + "/quotes/" + quoteId + "/order",
            reqJson, headerMap);
        logger.debug("save order api return json for quoteId {} : {}", quoteId, response);
        return response;
    }





    public List<ActivityMonitorUrlViewModel> getMonitorUrls() {
        List<ActivityMonitorUrl> urlList = activityMonitorUrlRepository.findByEnable(true);
        while (CollectionUtils.isEmpty(urlList))
            return null;
        List<ActivityMonitorUrlViewModel> modelList = new ArrayList<ActivityMonitorUrlViewModel>();
        for (ActivityMonitorUrl url : urlList){
            modelList.add(ActivityMonitorUrlViewModel.createViewModel(url));
        }
        return modelList;
    }

    /**
     * 报价短信
     * @param source
     * @param sourceId
     * @param companyId
     * @param quoteRecord
     */
    public void sendQuoteMsg(String source, Long sourceId, Long companyId, QuoteRecord quoteRecord) {
        smsHelper.sendQuoteDetailMsg(quoteRecord);
    }

    /**
     * 提交订单短信
     * @param mobile
     * @param orderNo
     */
    public void sendOrderMsg(String mobile, String orderNo) {
        smsHelper.sendCommitOrderMsg(orderNo);
    }

    /**
     * 获取缓存报价记录
     * @param quoteSource
     * @param quoteSourceId
     * @param companyId
     * @param type
     * @return
     */
    public QuoteRecordCache getQuoteRecordCache(String quoteSource, Long quoteSourceId, Long companyId, Integer type){
        return null;
    }

    /**
     * 报价补充信息填充
     * @param parameters
     * @param transferDate
     */
    protected void setSupplementInfo(QuoteQuery.AdditionalParameters parameters, Date transferDate) {
        SupplementInfo supplementInfo;
        if (null == parameters.getSupplementInfo()) {
            supplementInfo = new SupplementInfo();
        } else {
            supplementInfo = parameters.getSupplementInfo();
        }
        supplementInfo.setTransferDate(transferDate);
        parameters.setSupplementInfo(supplementInfo);
    }

    /**
     * 修改报价时保存quote_supplement_info所需要的补充信息
     * @param sourceId
     * @return
     */
    public SupplementInfo getSupplementInfo(Long sourceId) {
        return null;
    }

    /**
     * 保存补充信息表
     * @param supplementInfo
     * @param quoteRecord
     */
    public void saveQuoteSupplementInfo(SupplementInfo supplementInfo, QuoteRecord quoteRecord) {
        if (null == supplementInfo) return;
        //过户日期
        QuoteSupplementInfo quoteSupplementInfo = new QuoteSupplementInfo();
        if (null != supplementInfo.getTransferDate()) {

            quoteSupplementInfo.setAuto(quoteRecord.getAuto());
            quoteSupplementInfo.setQuoteRecord(quoteRecord);
            quoteSupplementInfo.setFieldPath("transferDate");
            quoteSupplementInfo.setValue(DateUtils.getDateString(supplementInfo.getTransferDate(), DateUtils.DATE_SHORTDATE_PATTERN));

            Calendar calendar = Calendar.getInstance();
            quoteSupplementInfo.setCreateTime(calendar.getTime());
            quoteSupplementInfo.setUpdateTime(calendar.getTime());

            quoteSupplementInfoRepository.save(quoteSupplementInfo);
        }
    }

    /**
     * 获取直减后报价金额
     * @param quoteRecord
     * @param client
     * @param userAgent
     * @return
     */
    public String getPaidAmount(QuoteRecord quoteRecord, CloseableHttpClient client,  String userAgent,String uniqueId) {
        String reqPath = BASE_REQUEST_PATH + "/" + VERSION + "/quotes/" + quoteRecord.getId() + "/paidAmount";
        Map<String,String> headerMap=new HashMap(){{put(HTTP.USER_AGENT, userAgent);put(WebConstants.ORDER_CENTER_TOKEN, uniqueId);}};
        String response = HttpClientUtils.doPostWithJsonAndHeader(client, reqPath, null, headerMap);
        logger.debug("get paidAmount api return json : {}", response);
        return response;
    }

    /**
     * 参加活动
     * @param client
     * @param code
     * @param userAgent
     * @return
     */
    public String manualMarketing(CloseableHttpClient client, String code,Long quoteRecordId, String userAgent,String uniqueId) {
        String reqPath = BASE_REQUEST_PATH + "/" + VERSION + "/quotes/persisted/"+quoteRecordId+"/discount/" + code;
        Map<String, String> reqMap = new HashMap<>();
        reqMap.put("userType", "ordercenter");
        Map<String,String> headerMap=new HashMap(){{put(HTTP.USER_AGENT, userAgent);put(WebConstants.ORDER_CENTER_TOKEN, uniqueId);}};
        String response = HttpClientUtils.doPostWithJsonAndHeader(client, reqPath, CacheUtil.doJacksonSerialize(reqMap), headerMap);
        logger.debug("get manual marketing api return json : {}", response);
        return response;
    }

    public Auto findAndCreateAuto(QuoteQuery quoteQuery) {
        Auto auto = quoteQuery.getAuto();
        if("quoteRecord".equals(quoteQuery.getSource())){
            MoApplicationLog log;
            if(quoteQuery.getSourceId() != null){
                log = applicationLogMongoRepository.findById(quoteQuery.getSourceId());
            }else{
                log = applicationLogMongoRepository.findById(quoteQuery.getSourceIdStr());
            }
            QuoteRecord record =  CacheUtil.doJacksonDeserialize(JSON.toJSONString(log.getLogMessage()), QuoteRecord.class);
            BeanUtil.copyPropertiesIgnore(record.getAuto(), auto);
            auto.setArea(AutoUtils.getAreaOfAuto(auto.getLicensePlateNo()));
        }else if("renewInsurance".equals(quoteQuery.getSource())){
            PurchaseOrder order = purchaseOrderRepository.findOne(quoteQuery.getSourceId());
            BeanUtil.copyPropertiesIgnore(order.getAuto(), auto);
            auto.setArea(AutoUtils.getAreaOfAuto(auto.getLicensePlateNo()));
        }else{
            getRequestAuto(quoteQuery);
        }
        auto.setCreateTime(DateUtils.getCurrentDate(DateUtils.DATE_LONGTIME24_PATTERN));
        auto.setUpdateTime(DateUtils.getCurrentDate(DateUtils.DATE_LONGTIME24_PATTERN));
        return autoService.saveOrMerge(auto,userRepository.findOne(quoteQuery.getUserId()),new StringBuilder());
    }

    /**
     * 获取补充信息
     * @param httpClient
     * @param quoteQuery
     * @param userAgent
     * @return
     */
    public String getSupplementParams(CloseableHttpClient httpClient, QuoteQuery quoteQuery, String userAgent,String uniqueId) {
        if("quoteRecord".equals(quoteQuery.getSource())){
            //从上次的报价记录中获取auto赋值给quoteQuery中的auto
            Auto auto = quoteQuery.getAuto();
            MoApplicationLog log;
            if(quoteQuery.getSourceId() != null){
                log = applicationLogMongoRepository.findById(quoteQuery.getSourceId());
            }else{
                log = applicationLogMongoRepository.findById(quoteQuery.getSourceIdStr());
            }
            QuoteRecord record =  CacheUtil.doJacksonDeserialize(JSON.toJSONString(log.getLogMessage()), QuoteRecord.class);
            BeanUtil.copyPropertiesIgnore(record.getAuto(), auto);
        }else if("renewInsurance".equals(quoteQuery.getSource())){
            Auto auto = quoteQuery.getAuto();
            PurchaseOrder order = purchaseOrderRepository.findOne(quoteQuery.getSourceId());
            BeanUtil.copyPropertiesIgnore(order.getAuto(), auto);
        }else{
            getRequestAuto(quoteQuery);
        }
        String reqJsonStr;
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
            reqJsonStr = mapper.writeValueAsString(quoteQuery);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Fail to serialize quoteQuery to json", e);
        }
        logger.debug("get supplementParams api request jsonStr -> {}, companyId -> {}", reqJsonStr);
        Map<String,String> headerMap=new HashMap(){{put(HTTP.USER_AGENT, userAgent);put(WebConstants.ORDER_CENTER_TOKEN, uniqueId);}};
        String response = HttpClientUtils.doPostWithJsonAndHeader(httpClient, PATH_GET_SUPPLEMENT_PARAMS, reqJsonStr, headerMap);
        logger.debug("get supplementParams api return json : {}, companyId -> {}", response);
        return response;
    }

    public static enum QuoteSource {
        SOURCE_PHOTO("photo"),
        SOURCE_PHONE("phone"),
        SOURCE_PERFECT_DRIVER("pd"),
        SOURCE_ORDER("order"),
        SOURCE_RECORD("quoteRecord"),
        SOURCE_RENEW_INSURANCE("renewInsurance");

        QuoteSource(String value) {
            this.value = value;
        }
        private final String value;
        public String getValue() {
            return value;
        }

        public static QuoteSource format(String type) {
            for (QuoteSource source : QuoteSource.values()) {
                if (source.value.equals(type)) {
                    return source;
                }
            }
            return null;
        }

        public static OcQuoteSource formatVal(String source) {
            return formatObj(format(source));
        }

        public static OcQuoteSource formatObj(DefaultQuoteService.QuoteSource quoteSource) {
            switch (quoteSource) {
                case SOURCE_PHONE:
                    return OcQuoteSource.Enum.QUOTE_SOURCE_PHONE;
                case SOURCE_PHOTO:
                    return OcQuoteSource.Enum.QUOTE_SOURCE_PHOTO;
                case SOURCE_PERFECT_DRIVER:
                    return OcQuoteSource.Enum.QUOTE_SOURCE_PERFECT_DRIVER;
                case SOURCE_ORDER:
                    return OcQuoteSource.Enum.QUOTE_SOURCE_ORDER;
                case SOURCE_RECORD:
                    return OcQuoteSource.Enum.QUOTE_SOURCE_RECORD;
                case SOURCE_RENEW_INSURANCE:
                    return OcQuoteSource.Enum.QUOTE_SOURCE_RENEW_INSURANCE;
                default:
                    throw new IllegalArgumentException("unknown quote source -> " + quoteSource);
            }
        }

    }

    private String orderCenterAgent(){
        return WebConstants.ORDER_CENTER_USER_AGENT_KEY+"."+Channel.Enum.ORDER_CENTER_11.getId();
    }


}
