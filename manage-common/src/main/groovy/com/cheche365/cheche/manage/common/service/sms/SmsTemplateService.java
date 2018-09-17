package com.cheche365.cheche.manage.common.service.sms;

import com.cheche365.cheche.core.model.SmsTemplate;
import com.cheche365.cheche.core.repository.MessageVariableRepository;
import com.cheche365.cheche.core.repository.SmsTemplateRepository;
import com.cheche365.cheche.manage.common.constants.SMSKeyTypeEnum;
import com.cheche365.cheche.manage.common.constants.SMSMessageConstants;
import com.cheche365.cheche.manage.common.model.PublicQuery;
import com.cheche365.cheche.manage.common.service.BaseService;
import com.cheche365.cheche.manage.common.service.InternalUserManageService;
import com.cheche365.cheche.manage.common.web.model.ResultModel;
import com.cheche365.cheche.manage.common.web.model.sms.SmsTemplateViewModel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by guoweifu on 2015/10/8.
 */

@Service(value = "smsTemplateService")
public class SmsTemplateService extends BaseService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SmsTemplateRepository smsTemplateRepository;

    @Autowired
    private InternalUserManageService internalUserManageService;

    @Autowired
    private MessageVariableRepository messageVariableRepository;

    /**
     * 新建短信模板
     *
     * @param viewData
     */
    public ResultModel add(SmsTemplateViewModel viewData) {
        try {
            if (smsTemplateRepository.findFirstByName(viewData.getName()) != null) {
                return new ResultModel(false, "名称已经存在");
            }
            if (smsTemplateRepository.findFirstByZucpCode(viewData.getZucpCode()) != null) {
                return new ResultModel(false, "模板号已经存在");
            }

            //验证短信内容的有效性（短信变量）
            Pattern pattern = Pattern.compile(SMSMessageConstants.MESSAGE_PATTERN);
            Matcher matcher = pattern.matcher(viewData.getContent());
            while (matcher.find()) {
                String variable = matcher.group(0);
                int count = messageVariableRepository.countByCode(variable);
                if (count == 0) {
                    return new ResultModel(false, "表达式 " + variable + " 不存在");
                }
            }

            //保存短信模板
            smsTemplateRepository.save(this.createSmsTemplate(viewData));

            return new ResultModel();
        } catch (Exception e) {
            logger.error("add sms template has error", e);
        }
        return new ResultModel(false, "保存失败");
    }

    /**
     * 查询短信模板详情
     *
     * @param id
     * @return
     */
    public SmsTemplate findById(Long id) {
        return smsTemplateRepository.findOne(id);
    }

    /**
     * 修改短信模板备注
     *
     * @param id
     * @param comment
     * @return
     */
    public boolean updateComment(Long id, String comment) {
        try {
            if (id == null || id == 0) {
                throw new Exception("sms template id is null");
            }
            // 短信模板
            SmsTemplate smsTemplate = smsTemplateRepository.findOne(id);

            //保存短信模板
            if (StringUtils.isNotBlank(comment)) {
                smsTemplate.setComment(comment.replace("\n", "\\r\\n"));
            }
            smsTemplate.setUpdateTime(Calendar.getInstance().getTime());
            smsTemplateRepository.save(smsTemplate);

            return true;
        } catch (Exception e) {
            logger.error("update sms template has error", e);
        }
        return false;
    }

    public Page<SmsTemplate> getSmsTemplateByPage(PublicQuery query) {
        try {
            return findBySpecAndPaginate(query,
                    super.buildPageable(query.getCurrentPage(), query.getPageSize(), Sort.Direction.DESC, BaseService.SORT_CREATE_TIME));
        } catch (Exception e) {
            logger.error("get SmsTemplate by page has error", e);
        }
        return null;
    }


    /**
     * 分页查询
     *
     * @param pageable 分页信息
     * @return Page<SmsTemplate>
     */
    public Page<SmsTemplate> findBySpecAndPaginate(PublicQuery publicQuery, Pageable pageable) throws Exception {
        return smsTemplateRepository.findAll(new Specification<SmsTemplate>() {
            @Override
            public Predicate toPredicate(Root<SmsTemplate> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                CriteriaQuery<SmsTemplate> criteriaQuery = cb.createQuery(SmsTemplate.class);

                //条件构造
                List<Predicate> predicateList = new ArrayList<>();

                if (StringUtils.isNotBlank(publicQuery.getKeyword())) {

                    if (publicQuery.getKeyType() == SMSKeyTypeEnum.TEMPLATE_NO.ordinal()) {//模板号
                        //获取实体属性
                        Path<String> zucpCodePath = root.get("zucpCode");
                        Path<String> yxtCodePath = root.get("yxtCode");
                        predicateList.add(cb.or(cb.like(zucpCodePath, publicQuery.getKeyword() + "%"), cb.like(yxtCodePath, publicQuery.getKeyword() + "%")));
                    } else if (publicQuery.getKeyType() == SMSKeyTypeEnum.TEMPLATE_NAME.ordinal()) {// 模板名称
                        //获取实体属性
                        Path<String> namePath = root.get("name");
                        predicateList.add(cb.like(namePath, publicQuery.getKeyword() + "%"));
                    } else if (publicQuery.getKeyType() == SMSKeyTypeEnum.CONTENT.ordinal()) {//短信内容
                        //获取实体属性
                        Path<String> contentPath = root.get("content");
                        predicateList.add(cb.like(contentPath, "%" + publicQuery.getKeyword() + "%"));
                    }
                }

                Predicate[] predicates = new Predicate[predicateList.size()];
                predicates = predicateList.toArray(predicates);
                return criteriaQuery.where(predicates).getRestriction();
            }
        }, pageable);
    }

    /**
     * 改变短信模板状态
     *
     * @param id
     * @param disable
     * @return
     */
    public boolean changeStatus(Long id, Integer disable) {
        try {
            // 短信模板
            SmsTemplate smsTemplate = smsTemplateRepository.findOne(id);

            // 启用或禁用
            smsTemplate.setDisable(disable == 1 ? true : false);
            smsTemplate.setUpdateTime(Calendar.getInstance().getTime());
            smsTemplateRepository.save(smsTemplate);

            return true;
        } catch (Exception e) {
            logger.error("change sms template status has error", e);
        }

        return false;
    }


    private SmsTemplate createSmsTemplate(SmsTemplateViewModel viewModel) {
        SmsTemplate smsTemplate = new SmsTemplate();
        if (viewModel.getId() != 0) {
            smsTemplate = smsTemplateRepository.findOne(viewModel.getId());
        }
        smsTemplate.setName(viewModel.getName());
        smsTemplate.setContent(viewModel.getContent());
        String comment = viewModel.getComment();
        if (StringUtils.isNotBlank(comment)) {
            smsTemplate.setComment(comment.replace("\n", "\\r\\n"));
        }
        smsTemplate.setYxtCode(viewModel.getYxtCode());
        smsTemplate.setZucpCode(viewModel.getZucpCode());
        smsTemplate.setCreateTime(viewModel.getId() == 0 ? Calendar.getInstance().getTime() : smsTemplate.getCreateTime());
        smsTemplate.setUpdateTime(Calendar.getInstance().getTime());
        smsTemplate.setOperator(internalUserManageService.getCurrentInternalUser());

        return smsTemplate;
    }
}
