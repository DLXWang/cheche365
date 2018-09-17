package com.cheche365.cheche.manage.common.service.sms;

import com.cheche365.cheche.core.model.ScheduleCondition;
import com.cheche365.cheche.core.model.ScheduleMessage;
import com.cheche365.cheche.core.model.SmsTemplate;
import com.cheche365.cheche.core.repository.ScheduleConditionRepository;
import com.cheche365.cheche.core.repository.ScheduleMessageRepository;
import com.cheche365.cheche.core.repository.SmsTemplateRepository;
import com.cheche365.cheche.manage.common.constants.SMSKeyTypeEnum;
import com.cheche365.cheche.manage.common.model.PublicQuery;
import com.cheche365.cheche.manage.common.service.BaseService;
import com.cheche365.cheche.manage.common.service.InternalUserManageService;
import com.cheche365.cheche.manage.common.util.AssertUtil;
import com.cheche365.cheche.manage.common.web.model.sms.ScheduleMessageViewModel;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by lyh on 2015/10/13.
 */
@Service
@Transactional
public class ScheduleMessageService extends BaseService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SmsTemplateRepository smsTemplateRepository;

    @Autowired
    private ScheduleMessageRepository scheduleMessageRepository;

    @Autowired
    private ScheduleConditionRepository scheduleConditionRepository;

    @Autowired
    private InternalUserManageService internalUserManageService;

    // 保存条件触发短信
    public void addScheduleMessage(ScheduleMessageViewModel scheduleMessageModel) {
        scheduleMessageRepository.save(this.createScheduleMessage(scheduleMessageModel));
    }

    /**
     * 组建条件触发短信对象
     *
     * @param viewModel
     * @return
     */
    private ScheduleMessage createScheduleMessage(ScheduleMessageViewModel viewModel) {
        ScheduleMessage scheduleMessage = new ScheduleMessage();
        SmsTemplate smsTemplate = smsTemplateRepository.findOne(viewModel.getSmsTemplateId());
        scheduleMessage.setSmsTemplate(smsTemplate);

        ScheduleCondition condition = scheduleConditionRepository.findOne(viewModel.getConditionId());
        scheduleMessage.setScheduleCondition(condition);
        if (StringUtils.isNotBlank(viewModel.getComment())) {
            scheduleMessage.setComment(viewModel.getComment().replace("\n", "\\r\\n"));
        }
        scheduleMessage.setCreateTime(scheduleMessage.getCreateTime() == null ? Calendar.getInstance().getTime() : scheduleMessage.getCreateTime());
        scheduleMessage.setUpdateTime(Calendar.getInstance().getTime());
        scheduleMessage.setOperator(internalUserManageService.getCurrentInternalUser());
        return scheduleMessage;
    }

    //获取某一条条件触发短信信息
    public ScheduleMessageViewModel findById(Long id) {
        ScheduleMessage scheduleMessage = scheduleMessageRepository.findOne(id);
        AssertUtil.notNull(scheduleMessage, "can not find scheduleMessage by id -> " + id);
        return ScheduleMessageViewModel.createViewData(scheduleMessage);
    }

    // 更新条件触发短信
    public void updateScheduleMessage(Long scheduleMessageId, ScheduleMessageViewModel viewModel) {
        ScheduleMessage scheduleMessage = scheduleMessageRepository.findOne(scheduleMessageId);
        AssertUtil.notNull(scheduleMessage, "can not find scheduleMessage by id -> " + scheduleMessageId);
        SmsTemplate smsTemplate = smsTemplateRepository.findOne(viewModel.getSmsTemplateId());
        if (StringUtils.isNotBlank(viewModel.getComment())) {
            scheduleMessage.setComment(viewModel.getComment().replace("\n", "\\r\\n"));
        }
        scheduleMessage.setSmsTemplate(smsTemplate);
        scheduleMessage.setUpdateTime(Calendar.getInstance().getTime());
        scheduleMessage.setOperator(internalUserManageService.getCurrentInternalUser());
        scheduleMessageRepository.save(scheduleMessage);
    }

    public Page<ScheduleMessage> getScheduleMessageByPage(PublicQuery query) {
        try {
            return findBySpecAndPaginate(query,
                super.buildPageable(query.getCurrentPage(), query.getPageSize(), Sort.Direction.DESC, BaseService.SORT_CREATE_TIME));
        } catch (Exception e) {
            logger.error("get ScheduleMessage by page has error", e);
        }
        return null;
    }

    /**
     * 分页查询
     *
     * @param pageable 分页信息
     * @return Page<ScheduleMessage>
     */
    private Page<ScheduleMessage> findBySpecAndPaginate(PublicQuery publicQuery, Pageable pageable) throws Exception {
        return scheduleMessageRepository.findAll(new Specification<ScheduleMessage>() {
            @Override
            public Predicate toPredicate(Root<ScheduleMessage> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                CriteriaQuery<ScheduleMessage> criteriaQuery = cb.createQuery(ScheduleMessage.class);
                //条件构造
                List<Predicate> predicateList = new ArrayList<>();
                if (StringUtils.isNotBlank(publicQuery.getKeyword())) {
                    // 模板名
                    if (publicQuery.getKeyType() == SMSKeyTypeEnum.TEMPLATE_NAME.ordinal()) {
                        Path<String> templateNamePath = root.get("smsTemplate").get("name");
                        predicateList.add(cb.like(templateNamePath, publicQuery.getKeyword() + "%"));
                    }
                    // 模板号
                    else if (publicQuery.getKeyType() == SMSKeyTypeEnum.TEMPLATE_NO.ordinal()) {
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
                    else if (publicQuery.getKeyType() == SMSKeyTypeEnum.COMMENT.ordinal()) {
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

    //修改状态
    public void changeStatus(Long scheduleMessageId, Integer operationType) {
        ScheduleMessage scheduleMessage = scheduleMessageRepository.findOne(scheduleMessageId);
        scheduleMessage.setDisable(operationType == 0 ? false : true);
        scheduleMessage.setUpdateTime(Calendar.getInstance().getTime());
        scheduleMessageRepository.save(scheduleMessage);
    }
}
