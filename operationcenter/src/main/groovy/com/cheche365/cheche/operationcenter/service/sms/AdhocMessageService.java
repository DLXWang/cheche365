package com.cheche365.cheche.operationcenter.service.sms;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.AdhocMessageRepository;
import com.cheche365.cheche.core.repository.FilterUserRepository;
import com.cheche365.cheche.core.repository.MessageVariableRepository;
import com.cheche365.cheche.core.repository.SmsTemplateRepository;
import com.cheche365.cheche.core.serializer.SerializerUtil;
import com.cheche365.cheche.web.service.system.SystemUrlGenerator;
import com.cheche365.cheche.core.util.CacheUtil;
import com.cheche365.cheche.core.util.URLUtils;
import com.cheche365.cheche.manage.common.constants.AdhocMessageEnum;
import com.cheche365.cheche.manage.common.constants.SMSKeyTypeEnum;
import com.cheche365.cheche.manage.common.constants.SMSMessageConstants;
import com.cheche365.cheche.manage.common.model.PublicQuery;
import com.cheche365.cheche.manage.common.service.BaseService;
import com.cheche365.cheche.manage.common.service.InternalUserManageService;
import com.cheche365.cheche.manage.common.util.AssertUtil;
import com.cheche365.cheche.operationcenter.util.ScheduleUtil;
import com.cheche365.cheche.manage.common.web.model.sms.AdhocMessageViewModel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 主动发送短信包含立即发送和定时发送两种，发送用户为单一用户和用户群。
 * 如果审核成功后的时间已经晚于发送时间，则立即发送短信。
 * 单一用户只选择立即发送。
 * 用户群可以选择立即发送和定时发送，必须要审核。
 * Created by lyh on 2015/10/8.
 */


@Service
@Transactional
public class AdhocMessageService extends BaseService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AdhocMessageRepository adhocMessageRepository;

    @Autowired
    private InternalUserManageService internalUserManageService;

    @Autowired
    private SmsTemplateRepository smsTemplateRepository;

    @Autowired
    private FilterUserRepository filterUserRepository;

    @Autowired
    private MessageVariableRepository messageVariableRepository;

    @Autowired
    private ImmediateSendAdhocMessageService immediateSendAdhocMessageService;

    @Autowired
    private SystemUrlGenerator systemUrlGenerator;

    // 保存主动短信
    public void addAdhocMessage(AdhocMessageViewModel adhocMessageModel) {
        AdhocMessage adhocMessage = adhocMessageRepository.save(this.createAdhocMessage(adhocMessageModel));
        // 单一用户立即发送短信，用户群的需要进行审核，添加立即发送短信逻辑
        if (StringUtils.isNotEmpty(adhocMessage.getMobile())) {
            immediateSendAdhocMessageService.sendSms(adhocMessage);
        }
    }

    //获取某一条主动短信信息
    public AdhocMessageViewModel findById(Long id) {
        AdhocMessage adhocMessage = adhocMessageRepository.findOne(id);
        AssertUtil.notNull(adhocMessage, "can not find adhoc message by id -> " + id);
        return this.createViewData(adhocMessage);
    }

    // 更新主动短信
    public void updateAdhocMessage(AdhocMessageViewModel viewModel) {
        Long adhocMessageId = viewModel.getId();
        AdhocMessage adhocMessage = adhocMessageRepository.findOne(adhocMessageId);
        AssertUtil.notNull(adhocMessage, "can not find adhoc message by id -> " + adhocMessageId);
        this.addAdhocMessage(viewModel);
    }

    public Page<AdhocMessage> getAdhocMessageByPage(PublicQuery query) {
        try {
            return findBySpecAndPaginate(query,
                    super.buildPageable(query.getCurrentPage(), query.getPageSize(), Sort.Direction.DESC, BaseService.SORT_CREATE_TIME));
        } catch (Exception e) {
            logger.error("get AdhocMessage by page has error", e);
        }
        return null;
    }

    /**
     * 分页查询
     *
     * @param pageable 分页信息
     * @return Page<adhocMessage>
     */
    private Page<AdhocMessage> findBySpecAndPaginate(PublicQuery publicQuery, Pageable pageable) throws Exception {
        return adhocMessageRepository.findAll(new Specification<AdhocMessage>() {
            @Override
            public Predicate toPredicate(Root<AdhocMessage> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                CriteriaQuery<AdhocMessage> criteriaQuery = cb.createQuery(AdhocMessage.class);

                //条件构造
                List<Predicate> predicateList = new ArrayList<>();

                //获取实体属性
                if (StringUtils.isNotBlank(publicQuery.getKeyword())) {
                    // 模板号
                    if (SMSKeyTypeEnum.TEMPLATE_NO.ordinal() == publicQuery.getKeyType()) {
                        Path<String> templateZucpCodePath = root.get("smsTemplate").get("zucpCode");
                        Path<String> templateYxtCodePath = root.get("smsTemplate").get("yxtCode");
                        predicateList.add(cb.or(
                                cb.like(templateZucpCodePath, publicQuery.getKeyword() + "%"),
                                cb.like(templateYxtCodePath, publicQuery.getKeyword() + "%")));
                    }
                    // 短信内容
                    else if (publicQuery.getKeyType() == SMSKeyTypeEnum.CONTENT.ordinal()) {
                        Path<String> SMSContentPath = root.get("smsTemplate").get("content");
                        predicateList.add(cb.like(SMSContentPath, "%" + StringUtils.trim(publicQuery.getKeyword()) + "%"));
                    }
                    // 备注
                    else if (SMSKeyTypeEnum.COMMENT.ordinal() == publicQuery.getKeyType()) {
                        Path<String> CommentPath = root.get("comment");
                        predicateList.add(cb.like(CommentPath, "%" + publicQuery.getKeyword() + "%"));
                    }
                }

                Predicate[] predicates = new Predicate[predicateList.size()];
                predicates = predicateList.toArray(predicates);
                return criteriaQuery.where(predicates).getRestriction();
            }
        }, pageable);
    }

    /**
     * 组建主动短信对象
     *
     * @param viewModel
     * @return
     */
    private AdhocMessage createAdhocMessage(AdhocMessageViewModel viewModel) {
        AdhocMessage adhocMessage = new AdhocMessage();
        if (viewModel.getId() > 0) {
            adhocMessage = adhocMessageRepository.findOne(viewModel.getId());
        }
        adhocMessage.setSendFlag(viewModel.getSendFlag());
        //用户群，需要审核
        if (viewModel.getFilterUserId() != null) {
            adhocMessage.setStatus(MessageStatus.Enum.WAIT_REVIEW);
            FilterUser filterUser = filterUserRepository.findOne(viewModel.getFilterUserId());
            adhocMessage.setFilterUser(filterUser);
            if (viewModel.getSendFlag() == AdhocMessageEnum.SEND_MESSAGE_TASK.ordinal()) {
                adhocMessage.setSendTime(DateUtils.getDate(viewModel.getSendTime(), DateUtils.DATE_LONGTIME24_PATTERN));
            }
        }
        //单一用户，立即发送
        else {
            adhocMessage.setMobile(viewModel.getMobile());
        }
        SmsTemplate smsTemplate = smsTemplateRepository.findOne(viewModel.getSmsTemplateId());
        adhocMessage.setSmsTemplate(smsTemplate);
        adhocMessage.setParameter(getParameters(viewModel.getParameter()));
        adhocMessage.setComment(viewModel.getComment());//备注
        adhocMessage.setCreateTime(Calendar.getInstance().getTime());
        adhocMessage.setUpdateTime(Calendar.getInstance().getTime());
        adhocMessage.setOperator(internalUserManageService.getCurrentInternalUser());
        return adhocMessage;
    }

    private String getParameters(String parameter) {
        String[] parameters = parameter.split(",");
        StringBuffer sb = new StringBuffer();
        for (String value : parameters) {
            if (URLUtils.isURL(value)) {
                sb.append(systemUrlGenerator.toShortUrl(value)).append(",");
            } else {
                sb.append(value).append(",");
            }
        }
        return sb.toString();
    }

    /**
     * 组建短信对象，返回到前端list显示
     *
     * @param adhocMessage
     * @return
     */
    public AdhocMessageViewModel createViewModel(AdhocMessage adhocMessage) {
        AdhocMessageViewModel viewModel = new AdhocMessageViewModel();
        viewModel.setId(adhocMessage.getId());
        if (adhocMessage.getFilterUser() != null) {
            viewModel.setFilterUserName(adhocMessage.getFilterUser().getName());
        }
        if (adhocMessage.getStatus() != null) {
            viewModel.setStatusId(adhocMessage.getStatus().getId());
            viewModel.setStatus(adhocMessage.getStatus().getStatus());
        }
        String smsContentView = getMessageContent(adhocMessage);
        viewModel.setZucpCode(adhocMessage.getSmsTemplate().getZucpCode());
        viewModel.setYxtCode(adhocMessage.getSmsTemplate().getYxtCode());
        viewModel.setSmsContentView(smsContentView);//页面展示编辑好的短信
        viewModel.setMobile(adhocMessage.getMobile());
        viewModel.setSendFlag(adhocMessage.getSendFlag());
        if (adhocMessage.getSendFlag() == AdhocMessageEnum.SEND_MESSAGE_TASK.ordinal()) {
            viewModel.setSendTime(DateUtils.getDateString(adhocMessage.getSendTime(), DateUtils.DATE_LONGTIME24_PATTERN));
        }
        viewModel.setSentCount(adhocMessage.getSentCount());
        viewModel.setTotalCount(adhocMessage.getTotalCount());
        if (adhocMessage.getTotalCount() != null && adhocMessage.getSentCount() != null) {
            if (adhocMessage.getTotalCount() == 0) {
                viewModel.setSendRate("");
            } else {
                viewModel.setSendRate((new DecimalFormat("#.##").format(
                        ((double) adhocMessage.getSentCount().intValue() / (double) adhocMessage.getTotalCount().intValue()) * 100)
                ) + "%");
            }
        }
        viewModel.setComment(adhocMessage.getComment());//备注
        viewModel.setCreateTime(DateUtils.getDateString(adhocMessage.getCreateTime(), DateUtils.DATE_LONGTIME24_PATTERN));
        viewModel.setUpdateTime(DateUtils.getDateString(adhocMessage.getUpdateTime(), DateUtils.DATE_LONGTIME24_PATTERN));
        viewModel.setOperator(adhocMessage.getOperator() == null ? "" : adhocMessage.getOperator().getName());//操作人
        return viewModel;
    }

    private String getMessageContent(AdhocMessage adhocMessage) {
        String smsContentView = adhocMessage.getSmsTemplate().getContent();
        Pattern pattern = Pattern.compile(SMSMessageConstants.MESSAGE_PATTERN);
        Matcher matcher = pattern.matcher(smsContentView);
        if (adhocMessage.getParameter() != null && adhocMessage.getParameter() != "") {
            String[] parameter = adhocMessage.getParameter().split(",");
            int i = 0;
            while (matcher.find()) {
                if (i < parameter.length) {
                    smsContentView = smsContentView.replace(matcher.group(0), parameter[i]);
                }
                i++;
            }
        }
        return smsContentView;
    }

    /**
     * 组建短信对象，返回到前端显示
     *
     * @param adhocMessage
     * @return
     */
    public static AdhocMessageViewModel createViewData(AdhocMessage adhocMessage) {
        AdhocMessageViewModel viewModel = new AdhocMessageViewModel();
        viewModel.setId(adhocMessage.getId());
        if (adhocMessage.getFilterUser() != null) {
            viewModel.setFilterUserId(adhocMessage.getFilterUser().getId());
            viewModel.setFilterUserName(adhocMessage.getFilterUser().getName());
        }
        viewModel.setSmsContent(adhocMessage.getSmsTemplate().getContent());
        viewModel.setSmsTemplateId(adhocMessage.getSmsTemplate().getId());
        viewModel.setSmsTemplateName(adhocMessage.getSmsTemplate().getName());
        viewModel.setMobile(adhocMessage.getMobile());
        viewModel.setParameter(adhocMessage.getParameter());
        viewModel.setSendFlag(adhocMessage.getSendFlag());
        viewModel.setSendTime(DateUtils.getDateString(adhocMessage.getSendTime(), DateUtils.DATE_LONGTIME24_PATTERN));
        viewModel.setComment(adhocMessage.getComment());//备注
        return viewModel;
    }

    //审核操作
    public ResultModel review(Long adhocMessageId, int reviewType) {
        ResultModel resultModel = new ResultModel();
        AdhocMessage adhocMessage = adhocMessageRepository.findOne(adhocMessageId);
        adhocMessage.setStatus(reviewType == AdhocMessageEnum.REVIEW_MESSAGE_SUCCESS.ordinal() ?
                MessageStatus.Enum.WAIT_SEND : MessageStatus.Enum.REVIEW_FAIL);
        adhocMessage.setUpdateTime(Calendar.getInstance().getTime());
        adhocMessage.setOperator(internalUserManageService.getCurrentInternalUser());
        adhocMessageRepository.save(adhocMessage);

        // 审核时间晚于定时发送时间时，改主动发送短信立即发送，添加立即发送短信逻辑
        if (adhocMessage.getStatus() != null && MessageStatus.Enum.WAIT_SEND.getId().equals(adhocMessage.getStatus().getId())) {
            ExecutorService threadPool = Executors.newSingleThreadExecutor();
            threadPool.execute((new AdhocMessageSendThread(adhocMessage)));
            return resultModel;
        }
        return resultModel;
    }

    private class AdhocMessageSendThread implements Runnable {

        private AdhocMessage adhocMessage;

        public AdhocMessageSendThread(AdhocMessage adhocMessage) {
            this.adhocMessage = adhocMessage;
        }

        @Override
        public void run() {
            logger.debug("execute thread to send adhoc message,id:{}", adhocMessage.getId());
            Date currentTime = DateUtils.getCurrentDate(DateUtils.DATE_LONGTIME24_PATTERN);
            // 单一用户审核过后立即发送，用户群审核过后如果发送时间晚于或等于当前时间则立即发送，否则创建定时任务发送
            if (adhocMessage.getSendTime() == null || currentTime.getTime() >= adhocMessage.getSendTime().getTime()) {
                int result = immediateSendAdhocMessageService.sendSms(adhocMessage);
                logger.debug("send adhoc message now, id:{},result:{}", adhocMessage.getId(), result);
            } else {
                logger.debug("create schedule task for adhoc message, id:{}", adhocMessage.getId());
                try {
                    String adhocMessageInString = CacheUtil.doJacksonSerialize(adhocMessage);
                    ScheduleUtil.createSchedule(adhocMessageInString);
//                    stringRedisTemplate.opsForList().leftPush(SMSMessageUtil.getTimerTaskQueueKey(), adhocMessageInString);
                } catch (Exception ex) {
                    logger.error("create schedule task for adhoc message error", ex);
                }
            }
        }
    }

    /**
     * 获取短信内容，并拼接
     *
     * @param smsTemplateId
     * @return
     */
    public AdhocMessageViewModel getSmsContentHtml(Long smsTemplateId) {
        AdhocMessageViewModel viewModel = new AdhocMessageViewModel();
        SmsTemplate smsTemplate = smsTemplateRepository.findOne(smsTemplateId);
        String smsContent = smsTemplate.getContent();
        List<MessageVariable> variable = new ArrayList();
        Pattern pattern = Pattern.compile(SMSMessageConstants.MESSAGE_PATTERN);
        Matcher matcher = pattern.matcher(smsContent);
        while (matcher.find()) {
            variable.add(messageVariableRepository.findByCode(matcher.group(0)));
        }
        viewModel.setSmsContent(smsTemplate.getContent());
        viewModel.setVariable(variable);
        return viewModel;
    }

    /**
     * 发送报价短信
     *
     * @param quoteRecord
     * @return
     */
    public int sendQuoteAdhocMessage(QuoteRecord quoteRecord) {
        AdhocMessage adhocMessage = new AdhocMessage();
        adhocMessage.setSendFlag(0);
        String mobile = quoteRecord.getApplicant().getMobile();
        adhocMessage.setMobile(mobile);
        adhocMessage.setSmsTemplate(SmsTemplate.Enum.CUSTOMER_QUOTE);
        String parameter = quoteRecord.getInsuranceCompany().getName() + "," + SerializerUtil.generateQuoteDetail(quoteRecord);
        adhocMessage.setParameter(parameter);
        adhocMessage.setComment("给手机：" + mobile + "发送报价信息");//备注
        adhocMessage.setCreateTime(Calendar.getInstance().getTime());
        adhocMessage.setUpdateTime(Calendar.getInstance().getTime());
        adhocMessage = adhocMessageRepository.save(adhocMessage);
        return immediateSendAdhocMessageService.sendMessage(adhocMessage);
    }

    /**
     * 发送人工报价提交订单短信
     *
     * @param mobile
     * @param orderNo
     * @return
     */
    public int sendOrderAdhocMessage(String mobile, String orderNo) {
        String tinyURL = systemUrlGenerator.toPaymentUrl(orderNo);
        AdhocMessage adhocMessage = new AdhocMessage();
        adhocMessage.setSendFlag(0);
        adhocMessage.setMobile(mobile);
        adhocMessage.setParameter(tinyURL);
        adhocMessage.setSmsTemplate(SmsTemplate.Enum.CUSTOMER_QUOTE_ORDER);
        adhocMessage.setComment("给手机：" + mobile + "发送人工报价提交订单短信");//备注
        adhocMessage.setCreateTime(Calendar.getInstance().getTime());
        adhocMessage.setUpdateTime(Calendar.getInstance().getTime());
        adhocMessage = adhocMessageRepository.save(adhocMessage);
        return immediateSendAdhocMessageService.sendMessage(adhocMessage);
    }
}
