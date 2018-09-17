package com.cheche365.cheche.ordercenter.web.controller.quote;

import com.alibaba.fastjson.JSON;
import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.constants.WebConstants;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.mongodb.repository.MoApplicationLogRepository;
import com.cheche365.cheche.core.repository.*;
import com.cheche365.cheche.core.service.*;
import com.cheche365.cheche.core.util.AutoUtils;
import com.cheche365.cheche.core.util.CacheUtil;
import com.cheche365.cheche.manage.common.model.PurchaseOrderExtend;
import com.cheche365.cheche.manage.common.util.AssertUtil;
import com.cheche365.cheche.manage.common.web.model.ResultModel;
import com.cheche365.cheche.ordercenter.model.QuoteQuery;
import com.cheche365.cheche.ordercenter.service.quote.DefaultQuoteService;
import com.cheche365.cheche.ordercenter.service.quote.QuotePhoneService;
import com.cheche365.cheche.ordercenter.service.quote.QuotePhotoService;
import com.cheche365.cheche.ordercenter.service.quote.QuoteProcessorFactory;
import com.cheche365.cheche.ordercenter.service.user.OrderCenterInternalUserManageService;
import com.cheche365.cheche.ordercenter.web.controller.perfectdriver.QuoteRecordCacheController;
import com.cheche365.cheche.ordercenter.web.model.ActivityMonitorUrlViewModel;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * Created by wangfei on 2015/10/23.
 */
@RestController
@RequestMapping("/orderCenter/quote")
public class QuoteController {

    private Logger logger = LoggerFactory.getLogger(QuoteController.class);
    private static final String QUOTE_HTTPCLIENT_SESSION_KEY = "ordercenter_httpclient_key_";
    private static final String QUOTE_USER_AGENT_SESSION_KEY = "ordercenter_user_agent_key_";
    private static final String QUOTE_SOURCE_SESSION_KEY = "ordercenter_source_key_";
    private static final String QUOTE_SOURCE_ID_SESSION_KEY = "ordercenter_source_id_key_";
    private static final String QUOTE_UNIQUE_ID_HEADER_NAME = "uniqueId";

    @Autowired
    private DefaultQuoteService defaultQuoteService;

    @Autowired
    private AutoService autoService;

    @Autowired
    private QuotePhotoService quotePhotoService;

    @Autowired
    private QuotePhoneService quotePhoneService;

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Autowired
    private OrderCooperationInfoService orderCooperationInfoService;

    @Autowired
    private OrderCenterInternalUserManageService orderCenterInternalUserManageService;

    @Autowired
    private QuoteProcessorFactory quoteProcessorFactory;

    @Autowired
    private InsuranceCompanyRepository insuranceCompanyRepository;

    @Autowired
    private ManualQuoteLogRepository manualQuoteLogRepository;

    @Autowired
    private QuoteRecordRepository quoteRecordRepository;


    @Autowired
    private QuoteConfigService quoteConfigService;

    @Autowired
    private QuoteRecordService quoteRecordService;

    @Autowired
    private ChannelRebateRepository rebateRepository;

    @Autowired
    private GiftTypeRepository giftTypeRepository;

    @Autowired
    private AreaRepository areaRepository;

    @Autowired
    private AreaService areaService;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    private MoApplicationLogRepository applicationLogMongoRepository;


    @RequestMapping(value = "/{purchaseOrderId}/detail", method = RequestMethod.GET)
    public ModelAndView chooseOrderDetailPage(@PathVariable Long purchaseOrderId) {
        PurchaseOrder purchaseOrder = purchaseOrderService.findById(purchaseOrderId);
        if (orderCooperationInfoService.checkOrderMode(purchaseOrder)) {//全国出单中心订单详情
            OrderCooperationInfo orderCooperationInfo = orderCooperationInfoService.getByPurchaseOrder(purchaseOrder);
            return new ModelAndView("redirect:/page/nationwideOrder/order_detail.html?id=" + orderCooperationInfo.getId());
        } else {//北京出单中心订单详情
            return new ModelAndView("redirect:/page/order/order_detail.html?id=" + purchaseOrderId);
        }
    }

    @RequestMapping(value = "/{source}/renewal/auto", method = RequestMethod.PUT)
    public void mergeRenewalAuto(@PathVariable String source,
                                 @RequestParam(value = "userId", required = true) Long userId,
                                 @RequestParam(value = "licensePlateNo", required = true) String licensePlateNo,
                                 @RequestParam(value = "sourceId", required = true) Long sourceId) {
        logger.debug("do merge auto info from auto to {} table by params: userId -> {}, licensePlateNo -> {}, sourceId -> {}",
            source, userId, licensePlateNo, sourceId);
        DefaultQuoteService.QuoteSource quoteSource = DefaultQuoteService.QuoteSource.format(source);
        Auto auto = autoService.findLatestByUserAndPlateNo(userId, licensePlateNo);
        switch (quoteSource) {
            case SOURCE_PHOTO:
                quotePhotoService.updateByAuto(sourceId, auto, 2);
                break;
            case SOURCE_PHONE:
                quotePhoneService.updateByAuto(sourceId, auto, 2);
                break;
            default:
                throw new IllegalArgumentException("unknown source -> " + quoteSource);
        }
    }

    @RequestMapping(value = "/{source}/validation", method = RequestMethod.GET)
    public ResultModel validQuoteAuto(@PathVariable String source,
                                      @RequestParam(value = "sourceId", required = true) Long sourceId) {
        logger.debug("do valid quote six required auto elements before choose company to quote by params: source -> {}, sourceId -> {}",
            source, sourceId);
        DefaultQuoteService.QuoteSource quoteSource = DefaultQuoteService.QuoteSource.format(source);
        switch (quoteSource) {
            case SOURCE_PHOTO:
                QuotePhoto quotePhoto = quotePhotoService.findById(sourceId);
                AssertUtil.notNull(quotePhoto, "can not find quotePhoto by id -> " + sourceId);
                return doAutoValidationBeforeQuote(quotePhoto.getLicensePlateNo(), quotePhoto.getOwner(),
                    quotePhoto.getVinNo(), quotePhoto.getEngineNo(), quotePhoto.getIdentity(), quotePhoto.getEnrollDate());
            case SOURCE_PHONE:
                QuotePhone quotePhone = quotePhoneService.getById(sourceId);
                AssertUtil.notNull(quotePhone, "can not find quotePhone by id -> " + sourceId);
                return doAutoValidationBeforeQuote(quotePhone.getLicensePlateNo(), quotePhone.getOwner(),
                    quotePhone.getVinNo(), quotePhone.getEngineNo(), quotePhone.getIdentity(), quotePhone.getEnrollDate());
            default:
                throw new IllegalArgumentException("unknown source -> " + quoteSource);
        }
    }

    private ResultModel doAutoValidationBeforeQuote(String licensePlateNo, String owner, String vinNo, String engineNo,
                                                    String identity, Date enrollDate) {
        boolean pass = StringUtils.isNotBlank(licensePlateNo) && StringUtils.isNotBlank(owner) && StringUtils.isNotBlank(vinNo)
            && StringUtils.isNotBlank(engineNo) && StringUtils.isNotBlank(identity) && null != enrollDate;
        return pass ? new ResultModel() : new ResultModel(false, "车辆信息不全，无法添加其他保险公司进行报价，请返回录入页补填车辆信息！");
    }

    @RequestMapping(value = "/{licensePlateNo}/companies", method = RequestMethod.GET)
    public ResultModel getAllCompanies(@PathVariable String licensePlateNo, HttpServletRequest request) {
        if (logger.isDebugEnabled()) {
            logger.debug("get support companies by licensePlateNo -> {}", licensePlateNo);
        }

        ResultModel result = doLicensePlateNoValidation(licensePlateNo);
        String uniqueId = getUniqueIdFromHeader(request);
        CloseableHttpClient httpClient = getHttpClientFromRedis(uniqueId, request);
        String userAgent = getUserAgentFromSession(uniqueId, request);
        Long sourceId = (Long) getSessionAttribute(QUOTE_SOURCE_ID_SESSION_KEY + uniqueId, request);
        String source = (String) getSessionAttribute(QUOTE_SOURCE_SESSION_KEY + uniqueId, request);
        return null != result ? result : new ResultModel(true, quoteProcessorFactory.getProcessor(source).getSupportCompanies(httpClient, sourceId, licensePlateNo, userAgent, uniqueId));
    }

    private ResultModel doLicensePlateNoValidation(String licensePlateNo) {
        Area area;
        if (StringUtils.isNotBlank(licensePlateNo)) {
            area = AutoUtils.getAreaOfAuto(licensePlateNo);
            if (null == area) {
                logger.warn("can not find area by license_plate_no -> {}", licensePlateNo);
                return new ResultModel(false, "暂不支持车牌号所对应的地区报价");
            }
        } else {
            logger.warn("the license_plate_no of quote_photo is empty, can not get quote info.");
            return new ResultModel(false, "车牌号为空，无法获取报价");
        }
        return null;
    }

    @RequestMapping(value = "/{companyId}", method = RequestMethod.POST)
    public ResultModel quote(@PathVariable Long companyId, @RequestBody QuoteQuery quoteQuery, HttpServletRequest request) {
        if (logger.isDebugEnabled()) {
            logger.debug("do quote for company -> {}, source -> {}, sourceId -> {}", companyId, quoteQuery.getSource(), quoteQuery.getSourceId());
        }
        InsuranceCompany insuranceCompany = insuranceCompanyRepository.findOne(companyId);
        if (!insuranceCompany.quote()) {
            return new ResultModel(false, insuranceCompany.getName() + "不能进行自动报价，请选择手动报价");
        }
        ResultModel result = doUserValidation(quoteQuery.getSource(), quoteQuery.getSourceId(), quoteQuery.getSourceIdStr());
        String uniqueId = getUniqueIdFromHeader(request);
        CloseableHttpClient httpClient = getHttpClientFromRedis(uniqueId, request);
        String userAgent = getUserAgentFromSession(uniqueId, request);
        String source = (String) getSessionAttribute(QUOTE_SOURCE_SESSION_KEY + uniqueId, request);
        return null != result ? result : new ResultModel(true, quoteProcessorFactory.getProcessor(source).quote(httpClient, companyId, quoteQuery, userAgent, uniqueId));
    }

    @RequestMapping(value = "/{userId}/login", method = RequestMethod.POST)
    public ResultModel checkInternalUser(@PathVariable Long userId,
                                         @RequestParam(value = "channelId") Long channelId,
                                         @RequestParam(value = "source") String source,
                                         @RequestParam(value = "sourceId") Long sourceId,
                                         HttpServletRequest request) {
        logger.debug("do check internal user for quote, userId -> {}, channelId -> {}", userId, channelId);
        String userAgent = getReqUserAgent(channelId);
        // CloseableHttpClient client = HttpClientUtils.newHttpClient();
        // setHttpClient(uniqueId, client, request);
        InternalUser internalUser = orderCenterInternalUserManageService.getCurrentInternalUser();
        logger.debug("internal user.email {} will quote for web user.id {}", internalUser.getEmail(), userId);
        String result = defaultQuoteService.login(null, userId, userAgent, internalUser);
        JSONObject jsonObject = JSONObject.fromObject(result);
        if (JSONNull.getInstance().equals(jsonObject.get("data"))) {
            return new ResultModel(false, String.valueOf(jsonObject.get("message")));
        }
        JSONObject son = (JSONObject) jsonObject.get("data");
        if (son != null && son.get("result").equals("success")) {
            String uniqueId = son.get("token").toString();
            logger.info("set userAgent to session by key : {}", QUOTE_USER_AGENT_SESSION_KEY + uniqueId);
            setSessionAttribute(QUOTE_USER_AGENT_SESSION_KEY + uniqueId, userAgent, request);

            logger.info("set quote source to session by key : {}", QUOTE_SOURCE_SESSION_KEY + uniqueId);
            setSessionAttribute(QUOTE_SOURCE_SESSION_KEY + uniqueId, source, request);

            logger.info("set sourceId to session by key : {}", QUOTE_SOURCE_ID_SESSION_KEY + uniqueId);
            setSessionAttribute(QUOTE_SOURCE_ID_SESSION_KEY + uniqueId, sourceId, request);
        }
        return new ResultModel(true, result);
    }


    private String getReqUserAgent(Long channelId) {
        AssertUtil.notNull(channelId, "channelId can not be null.");
        String userAgent = new StringBuilder(WebConstants.ORDER_CENTER_USER_AGENT_KEY).append(".").append(channelId.toString()).toString();
        logger.info("get request user-agent is {}, param is channelId : {}", userAgent, channelId);
        return userAgent.toString();
    }

    private ResultModel doUserValidation(String source, Long sourceId, String sourceIdStr) {
        User user;
        DefaultQuoteService.QuoteSource quoteSource = DefaultQuoteService.QuoteSource.format(source);
        switch (quoteSource) {
            case SOURCE_PHOTO:
                QuotePhoto quotePhoto = quotePhotoService.findById(sourceId);
                user = quotePhoto.getUser();
                break;
            case SOURCE_PHONE:
                QuotePhone quotePhone = quotePhoneService.getById(sourceId);
                user = quotePhone.getUser();
                break;
            case SOURCE_ORDER:
                PurchaseOrder purchaseOrder = purchaseOrderService.findById(sourceId);
                user = purchaseOrder.getApplicant();
            case SOURCE_RECORD:
                MoApplicationLog log;
                if (sourceId != null) {
                    log = applicationLogMongoRepository.findById(sourceId);
                } else {
                    log = applicationLogMongoRepository.findById(sourceIdStr);
                }
                QuoteRecord record = CacheUtil.doJacksonDeserialize(JSON.toJSONString(log.getLogMessage()), QuoteRecord.class);
                user = record.getApplicant();
                break;
            case SOURCE_RENEW_INSURANCE:
                PurchaseOrder order = purchaseOrderService.findById(sourceId);
                user = order.getApplicant();
                break;
            default:
                throw new IllegalArgumentException("unknown source -> " + source);
        }
        if (null == user) {
            return new ResultModel(false, "无用户信息，无法继续报价");
        }
        return null;
    }

    @RequestMapping(value = "/amend/paidAmount", method = RequestMethod.GET)
    public ResultModel getAmendQuotePaidAmount(@RequestParam(value = "companyId", required = true) Long companyId,
                                               @RequestParam(value = "quoteRecordId", required = true) Long quoteRecordId, HttpServletRequest request) {
        logger.debug("do get amend paidAmount value before commit quote, companyId -> {}", companyId);
        String uniqueId = getUniqueIdFromHeader(request);
        String source = (String) getSessionAttribute(QUOTE_SOURCE_SESSION_KEY + uniqueId, request);
        CloseableHttpClient httpClient = getHttpClientFromRedis(uniqueId, request);
        String userAgent = getUserAgentFromSession(uniqueId, request);
        QuoteRecord quoteRecord = quoteRecordRepository.findOne(quoteRecordId);
        return new ResultModel(true, quoteProcessorFactory.getProcessor(source).getPaidAmount(quoteRecord,
            httpClient, userAgent, uniqueId));
    }

    @RequestMapping(value = "/paidAmount", method = RequestMethod.GET)
    public ResultModel getQuotePaidAmount(@RequestParam(value = "companyId", required = true) Long companyId, HttpServletRequest request) {
        logger.debug("do get paidAmount value before commit quote, companyId -> {}", companyId);

        String uniqueId = getUniqueIdFromHeader(request);
        String source = (String) getSessionAttribute(QUOTE_SOURCE_SESSION_KEY + uniqueId, request);
        Long sourceId = (Long) getSessionAttribute(QUOTE_SOURCE_ID_SESSION_KEY + uniqueId, request);
        CloseableHttpClient httpClient = getHttpClientFromRedis(uniqueId, request);
        String userAgent = getUserAgentFromSession(uniqueId, request);

        QuoteRecordCache quoteRecordCache = quoteProcessorFactory.getProcessor(source).getQuoteRecordCache(source, sourceId,
            companyId, QuoteRecordCacheController.quoteRecordCacheType_3);
        return new ResultModel(true, quoteProcessorFactory.getProcessor(source).getPaidAmount(quoteRecordCache.getQuoteRecord(),
            httpClient, userAgent, uniqueId));
    }


    @RequestMapping(value = "/{companyId}/quoteRecord", method = RequestMethod.POST)
    public ResultModel saveQuote(@PathVariable Long companyId, @RequestBody QuoteRecord quoteRecord,
                                 HttpServletRequest request) {
        if (logger.isDebugEnabled()) {
            logger.debug("do save quote for areaId -> {}, companyId -> {}", quoteRecord.getArea().getId(), companyId);
        }
        String uniqueId = getUniqueIdFromHeader(request);
        String source = (String) getSessionAttribute(QUOTE_SOURCE_SESSION_KEY + uniqueId, request);
        Long sourceId = (Long) getSessionAttribute(QUOTE_SOURCE_ID_SESSION_KEY + uniqueId, request);


        CloseableHttpClient httpClient = getHttpClientFromRedis(uniqueId, request);
        String userAgent = getUserAgentFromSession(uniqueId, request);
        return new ResultModel(true, quoteProcessorFactory.getProcessor(source).saveQuote(source, sourceId, companyId,
            httpClient, quoteRecord, userAgent, uniqueId));
    }

    //获取用户可用的优惠券
    @RequestMapping(value = "/gifts", method = RequestMethod.GET)
    public ResultModel getGifts(@RequestParam(value = "page", required = true) Integer page,
                                @RequestParam(value = "quoteRecordId", required = true) Long quoteRecordId,
                                @RequestParam(value = "purchaseOrderId", required = false) Long purchaseOrderId,
                                HttpServletRequest request) {
        logger.info("do get user enable gifts by page -> {}, quoteRecordId -> {}", page, quoteRecordId);
        String uniqueId = getUniqueIdFromHeader(request);
        CloseableHttpClient httpClient = getHttpClientFromRedis(uniqueId, request);
        String userAgent = getUserAgentFromSession(uniqueId, request);
        return new ResultModel(true, defaultQuoteService.getGifts(httpClient, userAgent, page, quoteRecordId, purchaseOrderId, uniqueId));
    }

    //获取广告来源列表
    @RequestMapping(value = "/getMonitorUrls", method = RequestMethod.GET)
    public List<ActivityMonitorUrlViewModel> getMonitorUrls() {
        return defaultQuoteService.getMonitorUrls();
    }

    /**
     * 查询车辆信息
     * 登录状态下只需要licensePlateNo
     */

    @RequestMapping(value = "/quoteAutoInfo", method = RequestMethod.GET)
    public String getAutoInfo(@RequestParam(value = "owner", required = false) String owner,
                              @RequestParam(value = "identity", required = false) String identity,
                              @RequestParam(value = "identityType", required = false) String identityType,
                              @RequestParam(value = "licensePlateNo", required = true) String licensePlateNo,
                              HttpServletRequest request) {

        logger.info("do get quote auto info by page -> {}, licensePlateNo -> {}", licensePlateNo);
        return defaultQuoteService.getVehicleQuoteInfo(owner, identity, identityType, licensePlateNo);
    }

    @RequestMapping(value = "/{quoteRecordId}/marketings", method = RequestMethod.GET)
    public ResultModel getMarketings(@PathVariable Long quoteRecordId, HttpServletRequest request) {
        logger.info("do get marketings by quoteRecordId -> {}", quoteRecordId);
        String uniqueId = getUniqueIdFromHeader(request);
        CloseableHttpClient httpClient = getHttpClientFromRedis(uniqueId, request);
        String userAgent = getUserAgentFromSession(uniqueId, request);
        return new ResultModel(true, defaultQuoteService.getMarketings(httpClient, userAgent, quoteRecordId, uniqueId));
    }

    //领取活动
    @RequestMapping(value = "/marketings/{key}/{code}", method = RequestMethod.POST)
    public ResultModel addMarketing(@PathVariable String key, @PathVariable String code, HttpServletRequest request) {
        logger.info("do join marketing by code -> {} ,key -> {}", code, key);
        String uniqueId = getUniqueIdFromHeader(request);
        CloseableHttpClient httpClient = getHttpClientFromRedis(uniqueId, request);
        String userAgent = getUserAgentFromSession(uniqueId, request);
        return new ResultModel(true, defaultQuoteService.addMarketing(httpClient, code, key, userAgent, uniqueId));
    }

    @RequestMapping(value = "/users/address", method = RequestMethod.GET)
    public ResultModel getUserAddress(@RequestParam(value = "userId", required = true) Long userId,
                                      HttpServletRequest request) {
        String uniqueId = getUniqueIdFromHeader(request);
        CloseableHttpClient httpClient = getHttpClientFromRedis(uniqueId, request);
        String userAgent = getUserAgentFromSession(uniqueId, request);
        return new ResultModel(true, defaultQuoteService.getUserAddress(httpClient, userAgent, uniqueId));
    }

    @RequestMapping(value = "/licensePlateNo/address", method = RequestMethod.GET)
    public Address getUserAddressByAuto(@RequestParam(value = "licensePlateNo", required = true) String licensePlateNo,
                                        HttpServletRequest request) {
        Area city = AutoUtils.getAreaOfAuto(licensePlateNo);
        Area province = areaService.findProvinceByCityId(city.getId().toString());
        Address address = new Address();
        address.setProvince(String.valueOf(province.getId()));
        address.setCity(city.getId().toString());
        return address;
    }

    @RequestMapping(value = "/areas/provinces", method = RequestMethod.GET)
    public ResultModel getProvinces() {
        return new ResultModel(true, defaultQuoteService.getProvinces());
    }

    @RequestMapping(value = "/areas/{provinceId}/cities", method = RequestMethod.GET)
    public ResultModel getCities(@PathVariable String provinceId) {
        return new ResultModel(true, defaultQuoteService.getCities(provinceId));
    }

    @RequestMapping(value = "/areas/{cityId}/districts", method = RequestMethod.GET)
    public ResultModel getDistricts(@PathVariable String cityId) {
        return new ResultModel(true, defaultQuoteService.getDistricts(cityId));
    }

    @RequestMapping(value = "/{quoteRecordId}/order", method = RequestMethod.POST)
    public ResultModel createOrder(@PathVariable Long quoteRecordId, @RequestBody PurchaseOrderExtend purchaseOrder,
                                   HttpServletRequest request) {
        InternalUser internalUser = orderCenterInternalUserManageService.getCurrentInternalUser();
        logger.debug("internal user.email {} do generate order for quoteRecordId -> {}", internalUser.getEmail(), quoteRecordId);
        String uniqueId = getUniqueIdFromHeader(request);
        CloseableHttpClient httpClient = getHttpClientFromRedis(uniqueId, request);
        String userAgent = getUserAgentFromSession(uniqueId, request);
        return new ResultModel(true, defaultQuoteService.saveOrder(httpClient, purchaseOrder, quoteRecordId, userAgent, uniqueId));
    }

    @RequestMapping(value = "supplementOrder/{orderNo}/order", method = RequestMethod.POST)
    public ResultModel supplementOrder(@PathVariable String orderNo, @RequestBody PurchaseOrderExtend purchaseOrder,
                                       HttpServletRequest request) {
        InternalUser internalUser = orderCenterInternalUserManageService.getCurrentInternalUser();
        logger.debug("internal user.email {} do generate order for quoteRecordId -> {}", internalUser.getEmail(), orderNo);
        String uniqueId = getUniqueIdFromHeader(request);
        CloseableHttpClient httpClient = getHttpClientFromRedis(uniqueId, request);
        String userAgent = getUserAgentFromSession(uniqueId, request);
        return new ResultModel(true, defaultQuoteService.supplementOrder(httpClient, purchaseOrder, orderNo, userAgent, uniqueId));
    }

    @RequestMapping(value = "/{companyId}/sms", method = RequestMethod.POST)
    public ResultModel sendMessage(@PathVariable Long companyId, @RequestBody QuoteRecord quoteRecord, HttpServletRequest request) {
        if (logger.isDebugEnabled()) {
            logger.debug("do send quote message, companyId -> {}, mobile -> {}", companyId, quoteRecord.getApplicant().getMobile());
        }
        String uniqueId = getUniqueIdFromHeader(request);
        String source = (String) getSessionAttribute(QUOTE_SOURCE_SESSION_KEY + uniqueId, request);
        Long sourceId = (Long) getSessionAttribute(QUOTE_SOURCE_ID_SESSION_KEY + uniqueId, request);

        quoteProcessorFactory.getProcessor(source).sendQuoteMsg(source, sourceId, companyId, quoteRecord);
        return new ResultModel();
    }

    @RequestMapping(value = "/manualMarketings/{quoteRecordId}/{code}", method = RequestMethod.POST)
    public ResultModel manualMarketing(@PathVariable Long quoteRecordId, @PathVariable String code, HttpServletRequest request) {
        logger.info("do join marketing by code -> {} ,key -> {}", code, quoteRecordId);
        String uniqueId = getUniqueIdFromHeader(request);
        CloseableHttpClient httpClient = getHttpClientFromRedis(uniqueId, request);
        String userAgent = getUserAgentFromSession(uniqueId, request);
        return new ResultModel(true, defaultQuoteService.manualMarketing(httpClient, code, quoteRecordId, userAgent, uniqueId));
    }


    @RequestMapping(value = "/order/sms", method = RequestMethod.POST)
    public ResultModel sendOrderMessage(@RequestParam(value = "mobile", required = true) String mobile,
                                        @RequestParam(value = "orderNo", required = true) String orderNo) {
        if (logger.isDebugEnabled()) {
            logger.debug("do send commit order message, mobile -> {}", mobile);
        }
        defaultQuoteService.sendOrderMsg(mobile, orderNo);
        return new ResultModel();
    }

    @RequestMapping(value = "/{userId}/session", method = RequestMethod.DELETE)
    public void clearSession(@PathVariable Long userId, HttpServletRequest request) {
        if (logger.isDebugEnabled()) {
            logger.debug("do delete session attributes after create order.");
        }
        String uniqueId = getUniqueIdFromHeader(request);
        String[] sessionKeys = new String[]{
            QUOTE_HTTPCLIENT_SESSION_KEY + uniqueId,
            QUOTE_USER_AGENT_SESSION_KEY + uniqueId,
            QUOTE_SOURCE_SESSION_KEY + uniqueId,
            QUOTE_SOURCE_ID_SESSION_KEY + uniqueId
        };
        deleteSessionAttributes(sessionKeys, request);


    }

    @RequestMapping(value = "/findInsuranceCompany", method = RequestMethod.GET)
    public InsuranceCompany getInsuranceCompanyById(Long companyId) {
        return insuranceCompanyRepository.findOne(companyId);
    }

    @RequestMapping(value = "/saveManualQuoteLog", method = RequestMethod.POST)
    public ResultModel saveManualQuoteLog(@RequestParam(value = "quoteCode", required = true) String quoteCode,
                                          @RequestParam(value = "orderId", required = true) Long orderId) {
        ManualQuoteLog quoteLog = new ManualQuoteLog();
        quoteLog.setErrorCode(quoteCode);
        quoteLog.setPurchaseOrder(purchaseOrderService.findById(orderId));
        quoteLog.setOperator(orderCenterInternalUserManageService.getCurrentInternalUser());
        quoteLog.setOperateTime(DateUtils.getCurrentDate(DateUtils.DATE_LONGTIME24_PATTERN));
        manualQuoteLogRepository.save(quoteLog);
        return new ResultModel(true, "保存手动报价日志成功");
    }

    @RequestMapping(value = "/findAndCreateAuto", method = RequestMethod.POST)
    public Auto findAndCreateAuto(@RequestBody QuoteQuery quoteQuery, HttpServletRequest request) {
        String uniqueId = getUniqueIdFromHeader(request);
        String source = (String) getSessionAttribute(QUOTE_SOURCE_SESSION_KEY + uniqueId, request);
        return quoteProcessorFactory.getProcessor(source).findAndCreateAuto(quoteQuery);
    }

    @RequestMapping(value = "/log/{logId}", method = RequestMethod.GET)
    public String getQuoteInfo(@PathVariable String logId) {
        MoApplicationLog log;
        if (NumberUtils.isNumber(logId)) {
            log = applicationLogMongoRepository.findById(Long.parseLong(logId));
        } else {
            log = applicationLogMongoRepository.findById(logId);
        }

        return JSON.toJSONString(log.getLogMessage());
    }

    /**
     * 是否支持手动报价
     *
     * @param licensePlateNo
     * @param channelId
     * @param insuranceCompanyIds
     * @return
     */
    @RequestMapping(value = "/supportManual", method = RequestMethod.GET)
    public Boolean getQuoteInfo(@RequestParam(value = "licensePlateNo") String licensePlateNo,
                                @RequestParam(value = "channelId") Long channelId,
                                @RequestParam(value = "insuranceCompanyIds") Long[] insuranceCompanyIds) {
        Area area = AutoUtils.getAreaOfAuto(licensePlateNo);
        Boolean result = true;
        for (Long insuranceCompanyId : insuranceCompanyIds) {
            InsuranceCompany insuranceCompany = insuranceCompanyRepository.findOne(insuranceCompanyId);
            if (!quoteConfigService.isSupportManualQuote(Channel.toChannel(channelId), area, insuranceCompany)) {
                result = false;
                break;
            }
        }
        return result;
    }

    @RequestMapping(value = "/getSupplementParams", method = RequestMethod.POST)
    public ResultModel getSupplementParams(@RequestBody QuoteQuery quoteQuery, HttpServletRequest request) {
        ResultModel result = doUserValidation(quoteQuery.getSource(), quoteQuery.getSourceId(), quoteQuery.getSourceIdStr());
        String uniqueId = getUniqueIdFromHeader(request);
        CloseableHttpClient httpClient = getHttpClientFromRedis(uniqueId, request);
        String userAgent = getUserAgentFromSession(uniqueId, request);
        String source = (String) getSessionAttribute(QUOTE_SOURCE_SESSION_KEY + uniqueId, request);
        return null != result ? result : new ResultModel(true, quoteProcessorFactory.getProcessor(source).getSupplementParams(httpClient, quoteQuery, userAgent, uniqueId));
    }

    @RequestMapping(value = "/rebate", method = RequestMethod.GET)
    public Double getRebate(@RequestParam(value = "quoteRecordId") Long quoteRecordId) {
        QuoteRecord quoteRecord = quoteRecordService.getById(quoteRecordId);
        if (quoteRecord.getChannel().isAgentChannel()) {
            ChannelRebate rebate = rebateRepository.findFirstByChannelAndAreaAndInsuranceCompanyAndStatus(Channel.findAgentChannel(quoteRecord.getChannel()), quoteRecord.getArea(), quoteRecord.getInsuranceCompany(), ChannelRebate.Enum.EFFECTIVED_1);
            if (rebate != null)
                return rebate.discountAmount(quoteRecord);
        }
        return 0.0;
    }

    private Object getSessionAttribute(String key, HttpServletRequest request) {
//        logger.info("get value from session by key : {}", key);
        return request.getSession().getAttribute(key);
    }

    private void setSessionAttribute(String key, Object value, HttpServletRequest request) {
//        logger.info("set value to session by key : {}", key);
        request.getSession().setAttribute(key, value);
    }


    private void deleteSessionAttributes(String[] keys, HttpServletRequest request) {
        if (keys.length == 0) return;
        for (String key : keys) {
            logger.info("delete attribute from session by key : {}", key);
            request.getSession().removeAttribute(key);
            redisTemplate.delete(key);
        }
    }

    private String getUniqueIdFromHeader(HttpServletRequest request) {
        String uniqueId = request.getHeader(QUOTE_UNIQUE_ID_HEADER_NAME);
        AssertUtil.notEmpty(uniqueId, "uniqueId can not be empty.");
        logger.info("get uniqueId from request header is {}", uniqueId);
        return StringUtils.trimToEmpty(uniqueId);
    }

    private CloseableHttpClient getHttpClientFromRedis(String uniqueId, HttpServletRequest request) {

        return null;
    }


    private String getUserAgentFromSession(String uniqueId, HttpServletRequest request) {
        String sessionKey = QUOTE_USER_AGENT_SESSION_KEY + uniqueId;
        String userAgent = (String) getSessionAttribute(sessionKey, request);
        AssertUtil.notEmpty(userAgent, "can not get userAgent from session by key: " + sessionKey);
        logger.info("get userAgent value {} from session by key {}", userAgent, sessionKey);
        return userAgent;
    }


}
