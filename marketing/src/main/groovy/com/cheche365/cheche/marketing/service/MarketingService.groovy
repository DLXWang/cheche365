package com.cheche365.cheche.marketing.service

import com.cheche365.cheche.common.util.DateUtils
import com.cheche365.cheche.common.util.DoubleUtils
import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.message.RedisPublisher
import com.cheche365.cheche.core.model.*
import com.cheche365.cheche.core.mongodb.repository.MoAttendMarketingPartnerRepository
import com.cheche365.cheche.core.repository.*
import com.cheche365.cheche.core.service.*
import com.cheche365.cheche.core.service.sms.ConditionTriggerHandler
import com.cheche365.cheche.core.service.sms.ConditionTriggerUtil
import com.cheche365.cheche.core.util.ValidationUtil
import com.cheche365.cheche.marketing.model.AttendResult
import com.cheche365.cheche.web.ContextResource
import com.cheche365.cheche.core.repository.WechatUserInfoRepository
import com.cheche365.cheche.web.service.security.throttle.SmsValidationUtils
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.ApplicationEventPublisherAware
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import javax.servlet.http.HttpServletRequest
import java.text.ParseException
import java.text.SimpleDateFormat

import static com.cheche365.cheche.core.message.IPFilterMessage.CODE_MARKETING

/***************************************************************************/
/*                              MarketingService.java                 */
/*   文   件 名: MarketingService.java                                  */
/*   模  块： 动态活动运营平台                                                */
/*   功  能:  活动服务父类

/*   初始创建:2015/5/12                                            */
/*   版本更新:V1.0                                                         */
/*   版权所有:北京车与车科技有限公司                                       */
/***************************************************************************/


@Service
@Transactional
class MarketingService extends ContextResource implements ApplicationEventPublisherAware {

    private Logger logger = LoggerFactory.getLogger(MarketingService.class);

    public static final  SimpleDateFormat SDF = new SimpleDateFormat("yyyy年MM月dd日")

    @Autowired
    protected MarketingRepository marketingRepository;
    @Autowired
    protected MarketingSuccessRepository marketingSuccessRepository;
    @Autowired
    protected RedisTemplate redisTemplate;
    @Autowired
    private QuoteRecordRepository qrRepo
    @Autowired
    protected ConditionTriggerHandler conditionTriggerHandler;
    @Autowired
    protected WechatUserInfoRepository wechatUserInfoRepository;
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    public InsuranceCompanyRepository companyRepository;
    protected ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    GiftRepository giftRepository;
    @Autowired
    ChannelRepository channelRepository;
    @Autowired
    GiftAreaRepository giftAreaRepository;
    @Autowired
    AreaService areaService;
    @Autowired
    AreaRepository areaRepository;
    @Autowired
    IResourceService resourceService;
    @Autowired
    ModuleService moduleService;
    @Autowired
    GiftInsuranceCompanyRepository giftInsuranceCompanyRepository;
    @Autowired
    MarketingRuleRepository marketingRuleRepository;
    @Autowired
    DisplayMessageService displayMessageService;
    @Autowired
    private MarketingServiceFactory marketingServiceFactory;
    @Autowired
    private AgentService agentService
    @Autowired
    private QuoteFlowConfigRepository quoteFlowConfigRepo

    @Autowired
    private MoAttendMarketingPartnerRepository moAttendMarketingListRepository
    @Autowired
    private PartnerUserExtendRepository partnerAttendRecordRepository
    @Autowired
    private PartnerUserRepository partnerUserRepository
    @Autowired(required = false)
    HttpServletRequest request
    @Autowired
    private RedisPublisher redisPublisher


    List attends(code) {
        List<MoAttendMarketingPartner> moAttendMarketingLists = moAttendMarketingListRepository.findByMarketingCode(code)
        List<Map> dataMap = moAttendMarketingLists[0].data
        return dataMap
            .findAll { it -> compareDate(it.date) }
            .sort { a, b -> SDF.parse(b.date) <=> SDF.parse(a.date) }
    }

    Object attend(Marketing marketing, User user, Channel channel, Map<String, Object> payload) {

        String mobile =  payload.mobile ?: user?.mobile
        def ms = toMS(marketing.getAmount() as Double ,marketing, mobile, channel)
        ms.licensePlateNo = payload.licensePlateNo
        ms.owner = payload.owner
        MarketingSuccess afterSave = marketingSuccessRepository.save(ms)
        sendSimpleMessage marketing, channel, mobile
        this.doAfterAttend afterSave, user, payload
    }


    //汽车之家活动反射调用此方法，签名勿改
    void preCheck(Marketing marketing, String mobile, Channel channel) {

        if (marketing == null) {
            throw new BusinessException(BusinessException.Code.OBJECT_NOT_EXIST, "营销活动不存在");
        }
        if (new Date().before(marketing.getBeginDate())) {
            throw new BusinessException(BusinessException.Code.ILLEGAL_STATE, "活动尚未开始");
        }
        if (new Date().after(marketing.getEndDate())) {
            throw new BusinessException(BusinessException.Code.ILLEGAL_STATE, "活动已结束");
        }
        if (!StringUtils.isBlank(marketing.getChannel())) {
            if (channel == null || !marketing.getChannel().split(';').contains(channel.getId().toString())) {
                throw new BusinessException(BusinessException.Code.ILLEGAL_STATE, "当前渠道不支持此活动!");
            }
        }

        if(!ValidationUtil.validMobile(mobile)){
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "手机号格式校验失败");
        }
        if (this.needCheckIsAttend() && !marketing.multiAttend() && this.checkIsAttend(marketing, mobile)) {
            throw new BusinessException(BusinessException.Code.ILLEGAL_STATE, "该手机号已参加过活动，不可重复参加!");
        }

        SmsValidationUtils.validateRequest(CODE_MARKETING, request, redisTemplate, redisPublisher)
    }

    protected Object doAfterAttend(MarketingSuccess ms, User user, Map<String, Object> payload) {
        if(payload.uid){
            savePartnerUserExtend(payload,ms,channel)
        }
        return new AttendResult().setMessage("成功参与活动。");
    }

    protected needCheckIsAttend() {
        true
    }

    def checkIsAttend(Marketing marketing, String mobile) {
        marketingSuccessRepository.findFirstByMobileAndMarketingId(mobile, marketing.id) as Boolean ?: false
    }

    String shareCallback(Map params) {
        return "分享成功";
    }


    @Override
    void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    Marketing getMarketingByCode(String marketCode) {
        return marketingRepository.findFirstByCode(marketCode);
    }

    Map<String, Object> isAttend(String code, User user, Map<String,String> params) {
        Marketing marketing = this.getMarketingByCode(code);

        if (marketing == null) {
            throw new BusinessException(BusinessException.Code.OBJECT_NOT_EXIST, "营销活动不存在");
        }

        if (new Date().before(marketing.getBeginDate())) {
            throw new BusinessException(BusinessException.Code.ILLEGAL_STATE, "活动尚未开始");
        }
        if (new Date().after(marketing.getEndDate())) {
            throw new BusinessException(BusinessException.Code.ILLEGAL_STATE, "活动已结束");
        }

        if (!(marketing.attendWithoutLogin() && params.mobile || user)) {
            throw new BusinessException(BusinessException.Code.NONE_VALID_SESSION, "无用户信息，请登录。");
        }

        Map<String, Object> result = new HashMap<>()
        result.put("isAttend", this.checkIsAttend(marketing, params.mobile ?: user?.getMobile()))
        result
    }

    protected String activityName() {
        return "默认营销活动实现";
    }

    protected String getOauthRedirectUrl(Map stateParams) {
        throw new BusinessException(BusinessException.Code.UNEXPECTED_PARAMETER, "非预期活动代码" + stateParams.get("code"))
    }

    protected MarketingSuccess toMS(Double amount, Marketing marketing, String mobile, Channel channel){
        new MarketingSuccess(
            amount: amount,
            effectDate: new Date(),
            marketing: marketing,
            mobile: mobile,
            failureDate: DateUtils.getDate(marketing.getGiftExpireDate(), DateUtils.DATE_LONGTIME24_PATTERN),
            channel: channel,
            sourceChannel: channel?.name,
            businessActivity: businessActivity()
        )
    }

    Marketing getAvailableMarketing(Long channel, Long area) {
        return marketingRepository.findActiveMarketing(channel, area);
    }

    Boolean checkSupportInsurancePackage(QuoteRecord quoteRecord) {
        return DoubleUtils.moreThanZero(quoteRecord.getPremium()) && DoubleUtils.moreThanZero(quoteRecord.getCompulsoryPremium());
    }

    Boolean checkSupportMarketing(Marketing marketing, QuoteRecord quoteRecord) {
        if (!GiftInsuranceCompany.Enum.containsCompany(marketing.getId(), quoteRecord.getInsuranceCompany())) {
            logger.debug("QuoteRecord 为 {" + quoteRecord.toString() + "} ,不符合保险公司配置条件，过滤;");
            return false;
        }
        if (!GiftChannel.Enum.containsChannel(marketing.getId(), quoteRecord.getChannel())) {
            logger.debug("QuoteRecord 为 {" + quoteRecord.toString() + "} ,不符合渠道配置条件，过滤;");
            return false;
        }
        if (!GiftArea.Enum.containsArea(marketing.getId(), quoteRecord.getArea())) {
            logger.debug("QuoteRecord 为 {" + quoteRecord.toString() + "} ,不符合地区配置条件，过滤;");
            return false;
        }

        return checkSupportInsurancePackage(quoteRecord);
    }

    String marketingDetailUrl(Marketing marketing, Channel channel, QuoteRecord quoteRecord) {
        if (COMMON_MARKETING_CODE == marketing.getCode() && quoteRecord != null) {
            MarketingRule marketingRule = marketingRuleRepository.findFirstByAreaAndChannelAndInsuranceCompanyAndStatusAndMarketing(quoteRecord.getArea(), channel, quoteRecord.getInsuranceCompany(), MarketingRuleStatus.Enum.EFFECTIVE_2, marketing);
            if (null == marketingRule) {
                return "";
            }
            marketing.setName(marketingRule.getTitle());
            marketing.setShortName(marketingRule.getTitle());
            marketing.setDescription(marketingRule.getSubTitle());
            return resourceService.absoluteUrl(marketingRule.genUniqueLinkUrl("/marketing/m/activity/index.html"), "");
        } else {
            return resourceService.absoluteUrl(new StringBuilder("/marketing/m/").append(marketing.getCode()).append("/index.html").toString(), "");
        }
    }

    Set<Long> findSupportedInsuranceCompanies(Long marketingId, Long areaId, Channel channel) {
        GiftInsuranceCompany.Enum.findBySource(marketingId).intersect(quoteFlowConfigRepo.findInsuranceCompanyByAreaAndChannel(areaRepository.findOne(areaId), channel).id)
    }


    def format(Marketing marketing, Long area, Channel channel) {
        String activityPath = resourceService.getResourceAbsolutePath(resourceService.getProperties().getIosPath())
        findSupportedInsuranceCompanies(marketing.id, Long.valueOf(area), channel)
            .findResults { marketingRuleRepository.findFirstEffective(area, channel.id, it) }
            .collect { rule ->
            [
                name             : rule.title,
                shortName        : rule.title,
                description      : rule.subTitle,
                iconUrl          : resourceService.absoluteUrl(activityPath, rule.genUniqueBannerUrl('activity/201608002.jpg', rule.marketing.code + ".jpg")),
                url              : resourceService.absoluteUrl(rule.genUniqueLinkUrl('/marketing/m/activity/index.html')),
                code             : marketing.code,
                activityType     : marketing.activityType,
                target           : null, //以下字段在直通车情况使用，目前不支持，留着只是为了和前端兼容
                insuranceCompnyId: null
            ]
        } ?: [
            iconUrl: displayMessageService.genPicUrl("activity/marketing_placeholder.jpg", DisplayMessageService.RESOURCE_VERSION)
        ]
    }

    def supportMarketing(QuoteRecord qr) {

        List<Marketing> marketingList = [];
        if (canAttendMarketing(qr)) {
            marketingRepository.findActiveMarketing(qr.channel.id, qr.area.id, qr.insuranceCompany.id)?.with {
                if( marketingServiceFactory.getService(it.code).checkSupportMarketing(it, qr)){
                    marketingList << it
                }
            }
        }
        return marketingList
    }

    List<Marketing> findByQR(Long qrId){
        supportMarketing(qrRepo.findOne(qrId))
    }

    private Boolean canAttendMarketing(QuoteRecord quoteRecord) {
        !agentService.checkAgent(quoteRecord.applicant)
    }

    static boolean loginUnRequired(Marketing marketing){
        marketing.attendWithoutLogin()
    }

    boolean compareDate(String attendDate) {
        try {
            Calendar  calendar = new GregorianCalendar()
            calendar.setTime(new Date())
            calendar.add(calendar.DATE,-1)
            return SDF.parse(attendDate).before(calendar.getTime())
        } catch (ParseException e) {
            logger.info("parse date exception attendDate:{}", attendDate)
        }
        return false
    }

    protected sendSimpleMessage(Marketing marketing, Channel channel, String mobile) {
        ScheduleCondition scheduleCondition = MarketingScheduleCondition.Enum.getScheduleConditionByMarketing(marketing, channel)
        if (scheduleCondition != null) {
            ConditionTriggerUtil.sendSimpleMessage(conditionTriggerHandler, scheduleCondition, mobile, channel.id)
            logger.debug("触发活动短信发送条件，活动code: {}", marketing.getCode())
        } else {
            logger.debug("未触发短息发送条件，活动{}未配置触发条件", marketing.getCode())
        }
    }

    void savePartnerUserExtend(Map<String ,Object> payload, MarketingSuccess ms,Channel channel){

        logger.info("partner attend marketing param:{}",payload)
        PartnerUser partnerUser =partnerUserRepository.findFirstByPartnerAndPartnerId(channel.apiPartner,payload.uid)
        logger.info("by uid find partnerUserId:{}",partnerUser?.id)

        PartnerUserExtend par = new PartnerUserExtend().with {
            it.partnerUser = partnerUser
            it.objectTable = "marketing_success"
            it.objectId = ms.id
            it
        }
        partnerAttendRecordRepository.save(par)
    }

}
