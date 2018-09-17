package com.cheche365.cheche.core.service

import com.cheche365.cheche.common.util.DateUtils
import com.cheche365.cheche.common.util.HashUtils
import com.cheche365.cheche.core.constants.WebConstants
import com.cheche365.cheche.core.model.*
import com.cheche365.cheche.core.model.mongo.MongoUser
import com.cheche365.cheche.core.util.CacheUtil
import groovy.util.logging.Slf4j
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import javax.servlet.http.HttpSession
import java.util.concurrent.TimeUnit

import static com.cheche365.cheche.core.exception.Constants.FIELD_PATH_MAPPING
import static com.cheche365.cheche.core.exception.Constants.ADDITIONAL_PARAM_KEYS
import static com.cheche365.cheche.core.util.AutoUtils.VEHICLE_LICENSE_ENCRYPT_PROPS_NEW
import static java.util.UUID.randomUUID

@Slf4j
@Service
class QuoteRecordCacheService {

    @Autowired
    private RedisTemplate redisTemplate
    @Autowired
    private QuoteConfigService quoteConfigService
    @Autowired
    private DoubleDBService mongoDBService
    @Autowired(required = false)
    private HttpSession session

    public static final List IGNORE_WHEN_SERIALIZE = ['quote_limit_checker'] //todo 重用QuoterFactory下常量

    /**
     * 缓存报价、报价hashKey、报价参数
     * this method will be called multi-thread, so can't use any session method
     */
    void cacheQuoteRecord(QuoteRecord keyQuoteRecord, QuoteRecord valueQuoteRecord, Map<String, Object> keyParameters, Map<String, Object> valueParameters) {

        String key = quoteRecordKey(keyQuoteRecord, keyParameters)
        String hashKey = HashUtils.getMD5(key)
        valueQuoteRecord.quoteRecordKey = hashKey //保存quoteRecordKey的MD5值，便于前端使用
        valueParameters.quoteRecordHashKey = hashKey //api接口报价下单后需要根据hashKey清除缓存

        //api接口报价缓存20分钟，其他缓存2小时
        Boolean apiQuote = valueQuoteRecord.apiQuote()
        int timeout = apiQuote ? 20 : 2
        TimeUnit timeUnit = apiQuote ? TimeUnit.MINUTES : TimeUnit.HOURS

        CacheUtil.putValueWithExpire(redisTemplate, key, CacheUtil.doJacksonSerialize(valueQuoteRecord), timeout, timeUnit)
        CacheUtil.putValueWithExpire(redisTemplate, quoteRecordHashKey(hashKey), key, timeout, timeUnit)

        if(valueParameters) {
            CacheUtil.putValueWithExpire(redisTemplate, quoteRecordParamHashKey(hashKey), CacheUtil.doJacksonSerialize(valueParameters.findAll {!IGNORE_WHEN_SERIALIZE.contains(it.key)}), timeout, timeUnit)
        }

        log.debug("Already cached the quote record with key : {}, hashKey : {}, apiQuote : {} , timeout : {} , qr :{} , additionalParam : {} ", key, hashKey, apiQuote, timeout, valueQuoteRecord, valueParameters)
    }

    QuoteRecord getQuoteRecordFromCache(QuoteRecord quoteRecord, Map additionalParameters) {
        //接口报价也支持缓存，下单后清除报价缓存，一个报价只能生成一个订单
//        if (quoteConfigService.quoteCacheNotSupported(quoteRecord)) {
//            log.debug("insuranceCompany : {}, area : {}, channel : {} , 报价不支持缓存.",
//                quoteRecord.insuranceCompany.name, quoteRecord.area.name, quoteRecord.channel.name)
//            return null
//        }

        String quoteRecordKey = quoteRecordKey(quoteRecord, additionalParameters)
        QuoteRecord qr = CacheUtil.getValue(this.redisTemplate, quoteRecordKey, QuoteRecord.class)
        log.debug("get quote record from cache, qr :{}", qr)
        return qr
    }

    void clearCachedQR(String hashKey){
        log.debug("Already clear the quote record cache with hashKey : {}", hashKey)
        if (hashKey) {
            if (getQuoteRecordKeyByHashKey(hashKey)) {
                redisTemplate.delete(getQuoteRecordKeyByHashKey(hashKey))
            }
            if (quoteRecordParamHashKey(hashKey)) {
                redisTemplate.delete(quoteRecordParamHashKey(hashKey))
            }
        }
    }

    QuoteRecord getQuoteRecordByHashKey(String hashKey) {
        String quoteRecordKey = getQuoteRecordKeyByHashKey(hashKey)
        QuoteRecord qr = CacheUtil.getValue(this.redisTemplate, quoteRecordKey, QuoteRecord.class)
        log.debug("get quote record from cache by hash key, qr :{}", qr)
        return qr
    }

    Map getQuoteRecordParamByHashKey(String hashKey) {
        Map param = CacheUtil.getValue(this.redisTemplate, quoteRecordParamHashKey(hashKey), Map.class)
        log.debug("get quote record param from cache by hash key, param :{}", param)
        return param
    }

    String getQuoteRecordKeyByHashKey(String hashKey) {
        return CacheUtil.getValue(this.redisTemplate, quoteRecordHashKey(hashKey))
    }

    static String quoteRecordHashKey(String hashKey) {
        return "quote_record:hashcode:" + hashKey
    }

    static String quoteRecordParamHashKey(String hashKey) {
        return "quote_record_param:hashcode:" + hashKey
    }

    static String quoteRecordKey(QuoteRecord quoteRecord, Map additionalParameters) {
        [
            WebConstants.SESSION_KEY_QUOTE_RECORD,
            toQuoteRecordKey(quoteRecord),
            toAdditionalParamKey(additionalParameters)
        ].join('_')
    }

    static private String toQuoteRecordKey(QuoteRecord quoteRecord) {
        [
            quoteRecord.quoteFlowType.id,
            quoteRecord.type?.name,
            quoteRecord.channel.isAgentChannel(),
            quoteRecord.insurancePackage?.uniqueString,
            quoteRecord.insuranceCompany?.id,
            quoteRecord.area.id,
            quoteRecord.auto.licensePlateNo,
            quoteRecord.auto.owner,
            quoteRecord.auto.identity,
            quoteRecord.auto.vinNo,
            quoteRecord.auto.engineNo,
            DateUtils.getDateString(quoteRecord.auto.enrollDate, DateUtils.DATE_NUMBER),
            quoteRecord.auto.autoType?.code,
            quoteRecord.auto.autoType?.seats,
            quoteRecord.auto.fuelType?.id,
            quoteRecord.auto.useCharacter?.id,
            quoteRecord.auto.identityType?.id
        ].join('_')
    }

    static private String toAdditionalParamKey(Map param) {
        ADDITIONAL_PARAM_KEYS.collect {
            def value = param?.supplementInfo?.get(it)
            if (!value) {
                null
            } else if ('selectedAutoModel' == it) {
                value.value + '_' + value.meta.optionsSource
            } else if (value instanceof Date) {
                DateUtils.getDateString(value, DateUtils.DATE_NUMBER)
            } else if (value instanceof Long && ('date' == FIELD_PATH_MAPPING.get(it)?.fieldType)) {
                DateUtils.getDateString(new Date(value), DateUtils.DATE_NUMBER)
            } else {
                value
            }
        }.join('_')
    }

    @Transactional
    void saveQuoteRecordLog(QuoteRecord quoteRecord) {
        try {
            MoApplicationLog al = new MoApplicationLog(
                createTime: new Date(),
                logLevel: 0,
                logType: LogType.Enum.Quote_Cache_Record_31,
                objTable: 'quote_record',
                logMessage: CacheUtil.doJacksonSerialize(quoteRecord),
                user: quoteRecord.applicant ? MongoUser.toMongoUser(quoteRecord.applicant) : null
            )
            mongoDBService.saveApplicationLog(al)
        } catch (Exception e) {
            log.debug("save CacheQuoteRecordLog exception" + e.getMessage())
        }
    }

    static String enoughToQuoteFlag(String sessionId) {
        return "enoughToQuoteFlag:" + sessionId
    }

    void setEnoughToQuoteFlag(String sessionId, Boolean flag) {
        CacheUtil.putValueWithExpire(redisTemplate, enoughToQuoteFlag(sessionId), flag)
    }

    Boolean getEnoughToQuoteFlag(String sessionId) {
        CacheUtil.getValueToObject(redisTemplate, enoughToQuoteFlag(sessionId))
    }

    void cacheAutoModelOptions(Map extraMeta, Map info) {
        if (info != null) {
            def autoModelHashKey = randomUUID().toString()
            extraMeta << [autoModelHashKey: autoModelHashKey]
            CacheUtil.putObjectToCache(redisTemplate, WebConstants.AUTO_MODEL_OPTIONS, autoModelHashKey, info)
            log.debug("cache auto model options, autoModelHashKey:{}", autoModelHashKey)
        }
    }

    Map getAutoModelOptions(String hashKey) {
        return CacheUtil.getObjectFromCache(redisTemplate, WebConstants.AUTO_MODEL_OPTIONS, hashKey, Map.class)
    }

    static String captchaImageFlagKey(String sessionId, Auto auto) {
        return "captchaImageFlag:" + sessionId + ":" + auto.licensePlateNo
    }

    void cacheCaptchaImageFlag(String hashKey, Map info) {
        if (info != null) {
            CacheUtil.putObjectToCache(redisTemplate, WebConstants.CAPTCHA_IMAGE_FLAG, hashKey, info)
        }
    }

    Map getCaptchaImageFlag(String hashKey) {
        return CacheUtil.getObjectFromCache(redisTemplate, WebConstants.CAPTCHA_IMAGE_FLAG, hashKey, Map.class)
    }

    static String enoughToQuoteReqSet(String sessionId) {
        return "enoughToQuoteReqSet:" + sessionId
    }

    Long addToQuoteReqSet(String sessionId, String quoteReqParam) {
        CacheUtil.putToSetWithExpire(redisTemplate, enoughToQuoteReqSet(sessionId), quoteReqParam, 2, TimeUnit.HOURS)
    }

    Boolean isMemberQuoteReqSet(String sessionId, String quoteReqParam) {
        CacheUtil.isMemberSet(redisTemplate, enoughToQuoteReqSet(sessionId), quoteReqParam)
    }

    static String getEncryptionAutoKey(String clientIdentifier) {
        return "encryptionAuto_" + clientIdentifier
    }

    void cacheEncryptionAuto(String key, Auto unencryptedAuto) {
        if (VEHICLE_LICENSE_ENCRYPT_PROPS_NEW.clone().asList().any { unencryptedAuto."$it"?.contains('*') }) {
            log.debug("由于车辆信息加*,不缓存, auto: {} ", CacheUtil.doJacksonSerialize(unencryptedAuto))
        } else {
            session.setAttribute(key, CacheUtil.doJacksonSerialize(unencryptedAuto))
            log.debug("以 {} 为key缓存车辆加密前信息, auto: {} ", key, CacheUtil.doJacksonSerialize(unencryptedAuto))
        }
    }

    Auto getEncryptionAuto(String key) {
        Object autoInString =session.getAttribute(key)
        if (autoInString == null || StringUtils.isBlank(autoInString.toString())) {
            if (log.isDebugEnabled()) {
                log.debug("缓存中未发现任何以" + key + "为key的车辆信息")
            }
            return null
        }
        return CacheUtil.doJacksonDeserialize(autoInString.toString(), Auto.class)
    }

    static String getEncryptionInsuranceBasicInfoKey(String clientIdentifier) {
        return "encryptionInsuranceBasicInfo_" + clientIdentifier
    }

    void cacheEncryptionInsuranceBasicInfo(String key, InsuranceBasicInfo insuranceBasicInfo) {
        session.setAttribute(key, CacheUtil.doJacksonSerialize(insuranceBasicInfo))
        log.debug("以 {} 为key缓存保险基本信息加密前信息, auto: {} ", key, CacheUtil.doJacksonSerialize(insuranceBasicInfo))
    }

    InsuranceBasicInfo getEncryptionInsuranceBasicInfo(String key) {
        Object autoInString =session.getAttribute(key)
        if (autoInString == null || StringUtils.isBlank(autoInString.toString())) {
            if (log.isDebugEnabled()) {
                log.debug("缓存中未发现任何以" + key + "为key的保险基本信息")
            }
            return null
        }
        CacheUtil.doJacksonDeserialize(autoInString.toString(), InsuranceBasicInfo.class)
    }

    static String persistQRParamHashKey(Long qrId) {
        return WebConstants.ADDITIONAL_QR_DATA + qrId
    }

    void cachePersistentState(String hashKey, Map persistentState) {
        if (persistentState != null) {
            CacheUtil.putObjectToCache(redisTemplate, WebConstants.PERSISTENT_STATE, hashKey, persistentState)
        }
    }

    Map getPersistentState(String hashKey) {
        return CacheUtil.getObjectFromCache(redisTemplate, WebConstants.PERSISTENT_STATE, hashKey, Map.class)
    }

    //小程序在用，各端API版本均升级到V1.6之后可删除
    void cacheSavedQuoteRecord(String clientIdentifier, User user, String targetKey) {
        String key = WebConstants.SESSION_KEY_SAVED_QUOTE_RECORD + clientIdentifier + "_" + user.getId()
        CacheUtil.putValueWithExpire(redisTemplate, key, targetKey)
        log.debug("以 saved_quote_record_sessionId_userId 为key 缓存报价, key sessionId_userId 为:{},targetKey 为：{}" + key, targetKey)
    }

    QuoteRecord getSavedQuoteRecord(String clientIdentifier, User user) {
        String key = WebConstants.SESSION_KEY_SAVED_QUOTE_RECORD + clientIdentifier + "_" + user.getId()
        String targetKey = CacheUtil.getValue(this.redisTemplate, key)

        if (targetKey == null || StringUtils.isBlank(targetKey)) {
            if (log.isDebugEnabled()) {
                log.debug("缓存中未发现任何以" + targetKey + "为key的报价信息")
            }
            return null
        }

        Object quoteRecordInString = CacheUtil.getValue(this.redisTemplate, targetKey)
        if (quoteRecordInString == null || StringUtils.isBlank(quoteRecordInString.toString())) {
            if (log.isDebugEnabled()) {
                log.debug("缓存中未发现任何以" + targetKey + "为key的报价信息")
            }
            return null
        }
        return CacheUtil.doJacksonDeserialize(quoteRecordInString.toString(), QuoteRecord.class)
    }

}
