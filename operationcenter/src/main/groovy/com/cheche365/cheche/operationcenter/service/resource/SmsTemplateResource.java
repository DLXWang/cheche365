package com.cheche365.cheche.operationcenter.service.resource;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.MessageVariable;
import com.cheche365.cheche.core.model.SmsTemplate;
import com.cheche365.cheche.core.repository.MessageVariableRepository;
import com.cheche365.cheche.core.repository.SmsTemplateRepository;
import com.cheche365.cheche.manage.common.service.BaseService;
import com.cheche365.cheche.manage.common.web.model.PageInfo;
import com.cheche365.cheche.manage.common.web.model.PageViewModel;
import com.cheche365.cheche.manage.common.web.model.sms.MessageVariableViewModel;
import com.cheche365.cheche.manage.common.web.model.sms.SmsTemplateViewModel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by lyh on 2015/10/13.
 */
@Component
public class SmsTemplateResource extends BaseService<SmsTemplate, SmsTemplate> {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SmsTemplateRepository smsTemplateRepository;

    @Autowired
    private MessageVariableRepository messageVariableRepository;

    public PageViewModel<SmsTemplateViewModel> list(Integer currentPage, Integer pageSize, String keyword) {
        try {
            Page<SmsTemplate> smsTemplate = this.findBySpecAndPaginate(keyword,
                this.buildPageable(currentPage, pageSize));
            return this.createResult(smsTemplate);
        } catch (Exception e) {
            logger.error("find smsTemplate info by page has error", e);
        }
        return null;
    }

    /**
     * 封装展示层实体
     *
     * @param page 分页信息
     * @return PageViewModel<PageViewData>
     */
    public PageViewModel<SmsTemplateViewModel> createResult(Page page) throws Exception {
        PageViewModel model = new PageViewModel<SmsTemplateViewModel>();

        PageInfo pageInfo = new PageInfo();
        pageInfo.setTotalElements(page.getTotalElements());
        pageInfo.setTotalPage(page.getTotalPages());
        model.setPageInfo(pageInfo);

        List<SmsTemplateViewModel> pageViewDataList = new ArrayList<>();
        for (SmsTemplate smsTemplate : (List<SmsTemplate>) page.getContent()) {
            SmsTemplateViewModel viewData = createViewData(smsTemplate);
            pageViewDataList.add(viewData);
        }
        model.setViewList(pageViewDataList);

        return model;
    }

    /**
     * 构建分页信息
     *
     * @param currentPage 当前页面
     * @param pageSize    每页显示数
     * @return Pageable
     */
    private Pageable buildPageable(int currentPage, int pageSize) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        return new PageRequest(currentPage - 1, pageSize, sort);
    }

    /**
     * 分页查询
     *
     * @param keyword  关键字
     * @param pageable 分页信息
     * @return Page<SmsTemplate>
     */
    private Page<SmsTemplate> findBySpecAndPaginate(String keyword, Pageable pageable) throws Exception {
        return smsTemplateRepository.findAll(new Specification<SmsTemplate>() {
            @Override
            public Predicate toPredicate(Root<SmsTemplate> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                CriteriaQuery<SmsTemplate> criteriaQuery = cb.createQuery(SmsTemplate.class);

                //条件构造
                List<Predicate> predicateList = new ArrayList<>();
                if (StringUtils.isNotBlank(keyword)) {
                    Path<String> commentPath = root.get("comment");
                    predicateList.add(cb.like(commentPath, keyword + "%"));
                }
                predicateList.add(cb.equal(root.get("disable"), 0));//只获取已启用的短信模板
                Predicate[] predicates = new Predicate[predicateList.size()];
                predicates = predicateList.toArray(predicates);
                return criteriaQuery.where(predicates).getRestriction();
            }
        }, pageable);
    }

    /**
     * 组建短信对象，返回到前端显示
     *
     * @param smsTemplate
     * @return
     */
    private SmsTemplateViewModel createViewData(SmsTemplate smsTemplate) {
        SmsTemplateViewModel viewModel = new SmsTemplateViewModel();
        viewModel.setId(smsTemplate.getId());//序号
        viewModel.setZucpCode(smsTemplate.getZucpCode());//漫道模板号
        viewModel.setYxtCode(smsTemplate.getYxtCode());//盈信通模板号
        viewModel.setName(smsTemplate.getName());//模板名称
        viewModel.setContent(smsTemplate.getContent());//短信内容
        viewModel.setComment(smsTemplate.getComment());//备注
        viewModel.setCreateTime(DateUtils.getDateString(smsTemplate.getCreateTime(), DateUtils.DATE_LONGTIME24_PATTERN));//创建时间
        viewModel.setUpdateTime(DateUtils.getDateString(smsTemplate.getUpdateTime(), DateUtils.DATE_LONGTIME24_PATTERN));//更新时间
        viewModel.setOperator(smsTemplate.getOperator() == null ? "" : smsTemplate.getOperator().getName());//操作人
        return viewModel;
    }

    public List<MessageVariableViewModel> getMessageVariable(){
        List<MessageVariableViewModel> viewDataList = new ArrayList<>();
        Iterable<MessageVariable> tIterable = messageVariableRepository.findAll();
        Iterator<MessageVariable> tIterator = tIterable.iterator();
        while (tIterator.hasNext()) {
            MessageVariableViewModel viewModel = new MessageVariableViewModel();
            MessageVariable sc = tIterator.next();
                viewModel.setId(sc.getId());
                viewModel.setCode(sc.getCode());
                viewModel.setName(sc.getName());
                viewModel.setType(sc.getType());
                viewModel.setLength(sc.getLength());
                viewModel.setPlaceholder(sc.getPlaceholder());
                viewDataList.add(viewModel);
        }
        return viewDataList;
    }
}
