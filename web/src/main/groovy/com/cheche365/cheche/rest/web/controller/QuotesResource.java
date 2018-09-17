package com.cheche365.cheche.rest.web.controller;


import com.alibaba.common.lang.StringUtil;
import com.cheche365.cheche.core.constants.WebConstants;
import com.cheche365.cheche.core.exception.BusinessException;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.model.agent.ChannelAgent;
import com.cheche365.cheche.core.repository.QuoteRecordRepository;
import com.cheche365.cheche.core.service.*;
import com.cheche365.cheche.core.service.agent.ChannelRebateService;
import com.cheche365.cheche.core.service.agent.QuoteHistoryService;
import com.cheche365.cheche.core.util.ValidationUtil;
import com.cheche365.cheche.rest.model.QuoteDiscountResult;
import com.cheche365.cheche.rest.model.QuoteQuery;
import com.cheche365.cheche.rest.processor.order.OrderProcessor;
import com.cheche365.cheche.rest.processor.quote.QuoteProcessor;
import com.cheche365.cheche.rest.service.QuoteMarketingService;
import com.cheche365.cheche.rest.service.vl.VehicleLicenseService;
import com.cheche365.cheche.rest.validator.QuoteQueryValidator;
import com.cheche365.cheche.web.ContextResource;
import com.cheche365.cheche.web.counter.annotation.CountApiInvoke;
import com.cheche365.cheche.web.response.RestResponseEnvelope;
import com.cheche365.cheche.web.service.ChannelAgentService;
import com.cheche365.cheche.web.service.order.discount.AgentDiscountService;
import com.cheche365.cheche.web.service.order.discount.strategy.DiscountCalculator;
import com.cheche365.cheche.web.service.system.QRURL;
import com.cheche365.cheche.web.util.ClientTypeUtil;
import com.cheche365.cheche.web.version.VersionedResource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.cheche365.cheche.core.exception.BusinessException.Code.COMMON_KNOWN_REASON_ERROR;
import static com.cheche365.cheche.core.service.QuoteRecordCacheService.persistQRParamHashKey;
import static com.cheche365.cheche.core.service.SupplementInfoService.assembleQuoteSupplementInfo;
import static com.cheche365.cheche.core.service.SupplementInfoService.updateQuoteRecord;
import static com.cheche365.cheche.rest.QuoterFactory.assembleSupplementInfo;

/**
 * Created by zhengwei on 3/19/15.
 */

@RestController
@RequestMapping("/" + ContextResource.VERSION_NO + "/quotes")
@VersionedResource(from = "1.0")
public class QuotesResource extends ContextResource {

    private Logger logger = LoggerFactory.getLogger(QuotesResource.class);

    @Autowired
    private AutoService autoService;
    @Autowired
    private QuoteService quoteService;
    @Autowired
    private QuoteRecordService quoteRecordService;
    @Autowired
    private QuoteRecordCacheService cacheService;
    @Autowired
    private DiscountCalculator discountCalculator;
    @Autowired
    private QuoteRecordRepository quoteRecordRepository;
    @Autowired
    private QuoteMarketingService quoteMarketingService;
    @Autowired
    private QuoteProcessor quoteProcessor;
    @Autowired
    private OrderProcessor orderProcessor;
    @Autowired
    private QuoteSupplementInfoService quoteSupplementInfoService;
    @Autowired
    private VehicleLicenseService vlService;
    @Autowired
    private QuoteHistoryService quoteHistoryService;
    @Autowired
    private ChannelRebateService channelRebateService;
    @Autowired
    private AgentDiscountService agentDiscountService;
    @Autowired
    private ChannelAgentService channelAgentService;
    @Autowired
    private QRURL qrPage;

    private static final Map DEFAULT_ASYNC_RESPONSE = new HashMap(){{
        put("message", "success");
    }};

    @VersionedResource(from = "1.2", to = "1.5")
    @RequestMapping(value = "/default", method = RequestMethod.POST)
    @CountApiInvoke(value = "quote")
    public HttpEntity<RestResponseEnvelope> defaultQuote(HttpServletRequest request, @RequestBody @Valid QuoteQuery query, BindingResult bindingResult) {

        QuoteQueryValidator.validateQuoteRequest(query, bindingResult, true);

        Map quoteContext = quoteProcessor.formatQuoteRequest(query, request);

        List<InsuranceCompany> targetCompanies = query.getPref().getInsuranceCompanies();
        Boolean syncQuote = targetCompanies.size() == 1 && !query.getPref().isServerPush();
        if (syncQuote) {
            QuoteRecord quoteRecord = quoteProcessor.doSyncQuote(targetCompanies.get(0), quoteContext);
            return new ResponseEntity<>(new RestResponseEnvelope(quoteRecord), HttpStatus.OK);

        } else {
            quoteProcessor.doAsyncQuote(quoteContext);
            return getResponseEntity(DEFAULT_ASYNC_RESPONSE);
        }
    }

    @VersionedResource(from = "1.6")
    @RequestMapping(value = "/default", method = RequestMethod.POST)
    @CountApiInvoke(value = "quote")
    public HttpEntity<RestResponseEnvelope> simpleQuote(HttpServletRequest request, @RequestBody @Valid QuoteQuery query, BindingResult bindingResult) {

        QuoteQueryValidator.validateQuoteRequest(query, bindingResult, true);

        Map quoteContext = quoteProcessor.formatQuoteRequest(query, request);

        quoteProcessor.fillCompanyIds(quoteContext);

        quoteProcessor.saveQuoteRequest(quoteContext);

        QuoteRecord quoteRecord = quoteProcessor.doSimplifiedQuote(quoteContext);

        return getResponseEntity(quoteRecord != null ? quoteRecord : DEFAULT_ASYNC_RESPONSE);
    }

    @VersionedResource(from = "1.6")
    @RequestMapping(value = "{quoteRecordKey}", method = RequestMethod.POST)
    public HttpEntity<RestResponseEnvelope<Map>> saveQuote1_6(@PathVariable String quoteRecordKey, HttpServletRequest request) {
        QuoteRecord cachedQuoteRecord = this.cacheService.getQuoteRecordByHashKey(quoteRecordKey);
        if (null == cachedQuoteRecord) {
            throw new BusinessException(BusinessException.Code.OBJECT_NOT_EXIST, "系统无法读取报价信息.");
        }
        Object state = session.getAttribute(SESSION_KEY_PARTNER_STATE);
        if (null != cachedQuoteRecord.getChannel().getApiPartner() && "ershouche".equals(cachedQuoteRecord.getChannel().getApiPartner().getCode()) &&
            null != state && "1".equals(state.toString()) && CollectionUtils.isNotEmpty(cachedQuoteRecord.getQuoteFieldStatus())) {
            String qfsStr = "";
            for (QuoteFieldStatus qfs : cachedQuoteRecord.getQuoteFieldStatus()) {
                if ("damage".equals(qfs.getFiledName()) || "engine".equals(qfs.getFiledName())) {
                    qfsStr += qfs.getDescription() + "<br/>";
                }
            }
            if (StringUtil.isNotBlank(qfsStr)) {
                throw new BusinessException(COMMON_KNOWN_REASON_ERROR, qfsStr.substring(0, qfsStr.lastIndexOf("<br/>")));
            }
        }

        Auto afterSave = this.autoService.saveOrMerge(cachedQuoteRecord.getAuto(), this.currentUser(), new StringBuilder());
        logger.debug("Finish save the auto, id : {} ", afterSave.getId());

        cachedQuoteRecord.setAuto(afterSave);

        String targetKey = this.cacheService.getQuoteRecordKeyByHashKey(quoteRecordKey);
        Map quoteRecordParam = cacheService.getQuoteRecordParamByHashKey(cachedQuoteRecord.getQuoteRecordKey());
        logger.debug ("save报价结果 qr：{}", cachedQuoteRecord);
        logger.debug ("save报价结果 additional parameters ：{}", quoteRecordParam);
        QuoteRecord recordAfterSave = this.quoteService.saveRecord(updateQuoteRecord(cachedQuoteRecord, quoteRecordParam), this.currentUser(), ClientTypeUtil.getChannel(request));
        cacheService.cachePersistentState(persistQRParamHashKey(recordAfterSave.getId()), quoteRecordParam);
        cacheService.cacheSavedQuoteRecord(session.getId(), this.currentUser(), targetKey);
        quoteSupplementInfoService.save(assembleQuoteSupplementInfo(afterSave, recordAfterSave, quoteRecordParam));

        logger.debug("Finish the quote record save process, quote record id:{},insurance package: {} ", recordAfterSave.getId(), recordAfterSave.getInsurancePackage().toString());

        Map<String, Object> result = new HashMap<>();
        result.put("quoteRecordId", cachedQuoteRecord.getId());

        ChannelAgent channelAgent = channelAgentService.getCurrentChannelAgent(recordAfterSave);
        if(getChannel().isLevelAgent() && channelAgent != null){
            result.put("discounts", agentDiscountService.calculateDiscountsByChannelAgent(recordAfterSave, channelAgent).getDiscounts());
        }else if (recordAfterSave.getChannel().isAgentChannel()) {
            result.put("discounts", agentDiscountService.calculateDiscounts(recordAfterSave, channelRebateService.getChannelRebate(recordAfterSave, null)).getDiscounts());
        }
        return new ResponseEntity<>(new RestResponseEnvelope(result), HttpStatus.OK);
    }

    @VersionedResource(from = "1.2", to = "1.5")
    @RequestMapping(value = "", method = RequestMethod.POST)
    public HttpEntity<RestResponseEnvelope<Map>> saveQuote(@RequestBody @Valid QuoteRecord quoteRecord, HttpServletRequest request) {
        return this.saveQuote1_6(quoteRecord.getQuoteRecordKey(), request);
    }

    @RequestMapping(value = "{quoteRecordId}/order", method = RequestMethod.PUT)
    @VersionedResource(from = "1.1")
    public HttpEntity<RestResponseEnvelope> placeOrder(HttpServletRequest request, @PathVariable String quoteRecordId, @RequestBody @Valid PurchaseOrder purchaseOrder) {

        if(purchaseOrder.getDeliveryAddress() != null && !ValidationUtil.validMobile(purchaseOrder.getDeliveryAddress().getMobile())){
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "配送地址手机号格式错误");
        }

        this.logger.debug("Kick the place order process on. qrId : {}" , quoteRecordId);
        //解密投保人和被保人姓名和身份证号
        autoService.decryptPurchaseOrder(purchaseOrder,request.getSession().getId());
        // 为了防止additionalParameters后续修改影响purchaseOrder序列化，须clone TODO purchaseOrder.additionalParameters后续可置为null
        Map<String, Object> additionalParameters = (null == purchaseOrder.getAdditionalParameters()) ? new HashMap<>() : ObjectUtils.clone(purchaseOrder.getAdditionalParameters());
        additionalParameters.put(WebConstants.SUPPLEMENT_INFO_SUPPORT_LIST, assembleSupplementInfo());

        Object responseEnvelope = orderProcessor.doService(quoteRecordId, purchaseOrder, additionalParameters);

        this.logger.debug("Finish the place order process, the purchase order NO is " + purchaseOrder.getOrderNo());
        return new ResponseEntity(responseEnvelope, HttpStatus.OK);

    }

    @VersionedResource(from = "1.3")
    @RequestMapping(value = "{quoteRecordHashKey}/discount", method = RequestMethod.POST)
    public HttpEntity<RestResponseEnvelope<QuoteDiscountResult>> quoteDiscountByPersistedQuoteRecord(@PathVariable String quoteRecordHashKey,
                                                                                                     HttpServletRequest request) {
        return quoteDiscount(quoteRecordHashKey, WebConstants.COMMON_MARKETING_CODE, request);
    }

    @VersionedResource(from = "1.3")
    @RequestMapping(value = "{quoteRecordHashKey}/discount/{marketingCode}", method = RequestMethod.POST)
    public HttpEntity<RestResponseEnvelope<QuoteDiscountResult>> quoteDiscount(@PathVariable String quoteRecordHashKey,
                                                                               @PathVariable String marketingCode,
                                                                               HttpServletRequest request) {
        QuoteRecord cachedQuoteRecord = this.cacheService.getQuoteRecordByHashKey(quoteRecordHashKey);
        if (null == cachedQuoteRecord) {
            throw new BusinessException(BusinessException.Code.OBJECT_NOT_EXIST, "系统无法读取报价信息.");
        }
        QuoteDiscountResult quoteDiscountResult = quoteMarketingService.getQuoteDiscountResult(cachedQuoteRecord, marketingCode, ClientTypeUtil.getChannel(request));
        RestResponseEnvelope envelope = new RestResponseEnvelope(quoteDiscountResult);
        return new ResponseEntity<>(envelope, HttpStatus.OK);
    }

    @VersionedResource(from = "1.3")
    @RequestMapping(value = "persisted/{quoteRecordId}/discount/{marketingCode}", method = RequestMethod.POST)
    public HttpEntity<RestResponseEnvelope<QuoteDiscountResult>> quoteDiscountByPersistedQuoteRecord(@PathVariable Long quoteRecordId,
                                                                                                     @PathVariable String marketingCode,
                                                                                                     HttpServletRequest request) {
        QuoteRecord cachedQuoteRecord = quoteService.getById(quoteRecordId);
        if (null == cachedQuoteRecord) {
            throw new BusinessException(BusinessException.Code.OBJECT_NOT_EXIST, "未找到对应报价信息.");
        }
        QuoteDiscountResult quoteDiscountResult = quoteMarketingService.getQuoteDiscountResult(cachedQuoteRecord, marketingCode, ClientTypeUtil.getChannel(request));
        RestResponseEnvelope envelope = new RestResponseEnvelope(quoteDiscountResult);
        return new ResponseEntity<>(envelope, HttpStatus.OK);
    }


    @VersionedResource(from = "1.3")
    @RequestMapping(value = "{quoteRecordId}/paidAmount", method = RequestMethod.POST)
    public HttpEntity<RestResponseEnvelope<Double>> calculatePaidAmount(@PathVariable Long quoteRecordId) {
        QuoteRecord quoteRecord = quoteService.getById(quoteRecordId);
        if (quoteRecord == null || quoteRecord.getApplicant() == null) {
            throw new BusinessException(BusinessException.Code.INTERNAL_SERVICE_ERROR, "传入参数错误");
        }

        discountCalculator.calculateQuoteRecord(quoteRecord);

        return new ResponseEntity<>(new RestResponseEnvelope(quoteRecord.getPaidAmount()), HttpStatus.OK);
    }

    @RequestMapping(value = "/quoterecord", method = RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope> quoteRecord(@RequestParam(value = "id", required = false) Long id,
                                                        @RequestParam(value = "cacheId", required = false) String cacheId,
                                                        @RequestParam(value = "nlId", required = false) String nlId) {
        QuoteRecord quoteRecord;
        Map additionalParameters = null;
        if (StringUtils.isNotEmpty(nlId)) {
            id = qrPage.cachedValue(nlId);
            if (id != null) {
                quoteRecord = quoteRecordRepository.findOne(id);
                logger.info("通过UUID查询报价，nlId：{}，从缓存中获取到的报价id：{}", nlId, id);
            } else {
                throw new BusinessException(BusinessException.Code.OBJECT_NOT_EXIST, "链接已过期");
            }
        } else if (StringUtils.isNotEmpty(cacheId)) {
            quoteRecord = cacheService.getQuoteRecordByHashKey(cacheId);
            additionalParameters = cacheService.getQuoteRecordParamByHashKey(cacheId);
            logger.info("通过缓存的hashKey查询报价，cacheId：{}", cacheId);
        } else if (id != null) {
            quoteRecord = quoteRecordRepository.findFirstByApplicantAndId(this.currentUser(), id);
            logger.info("通过报价Id查询报价，id：{}", id);
        } else {
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "输入参数不合法");
        }
        if (quoteRecord == null) {
            throw new BusinessException(BusinessException.Code.OBJECT_NOT_EXIST, "报价不存在");
        }

        quoteRecordService.fillUpAutoModel(quoteRecord, additionalParameters);

        if (quoteRecord.getChannel().isAgentChannel() && StringUtils.isEmpty(nlId)) { //通过uuid查询报价详情，即代理人下面的用户查看报价详情，不返回折扣信息
            agentDiscountService.calculateDiscounts(quoteRecord, channelRebateService.getChannelRebate(quoteRecord, null));
        }
        logger.info("查询到的报价详情为：{}", quoteRecord.toString());
        return getResponseEntity(quoteRecord);
    }


    @RequestMapping(value = "insurancepackage/default", method = RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope<InsurancePackage>> getDefaultInsurancePackage() {
        RestResponseEnvelope envelope = new RestResponseEnvelope(InsurancePackage.defaultPackages().get(0));
        return new ResponseEntity<>(envelope, HttpStatus.OK);
    }

    @RequestMapping(value = "insurancepackage/packagelist", method = RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope<InsurancePackage>> getInsurancePackageList() {
        RestResponseEnvelope envelope = new RestResponseEnvelope(InsurancePackage.defaultPackages());
        return new ResponseEntity<>(envelope, HttpStatus.OK);
    }

    @RequestMapping(value = "/{quoteRecordId}", method = RequestMethod.GET)
    @VersionedResource(from = "1.1")
    public HttpEntity<RestResponseEnvelope> getDetailQuoteRecord(@PathVariable Long quoteRecordId) {
        QuoteRecord quoteRecord = quoteRecordService.getByIdAndUser(quoteRecordId, currentUser());
        return new ResponseEntity<>(new RestResponseEnvelope(quoteRecord), HttpStatus.OK);
    }

    @RequestMapping(value = "/params/format", method = RequestMethod.POST)
    @VersionedResource(from = "1.4")
    public HttpEntity<RestResponseEnvelope> transformQuoteParams(@RequestBody @Valid QuoteQuery quoteQuery) {
        return getResponseEntity(vlService.formatQuoteParam(quoteQuery));
    }

    @RequestMapping(value = "/history", method = RequestMethod.GET)
    @VersionedResource(from = "1.5")
    public HttpEntity<RestResponseEnvelope> queryQuoteHistory(
            @RequestParam(value = "keyWords", required = false) String keyWords,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            HttpServletRequest request) {
        int start = (page == null || page <= 0) ? 0 : page;
        int pageNum = (size == null || size <= -1) ? WebConstants.PAGE_SIZE : size;
        Pageable pageable = new PageRequest(start, pageNum);

        return getResponseEntity(quoteHistoryService.findQuoteHistory(this.currentUser(), ClientTypeUtil.getChannel(request), keyWords, pageable));
    }
}
