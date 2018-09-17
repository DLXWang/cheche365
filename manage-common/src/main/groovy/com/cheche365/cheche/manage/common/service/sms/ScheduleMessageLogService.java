package com.cheche365.cheche.manage.common.service.sms;

import com.cheche365.cheche.core.model.ScheduleMessageLog;
import com.cheche365.cheche.core.repository.ScheduleMessageLogRepository;
import com.cheche365.cheche.manage.common.constants.SMSKeyTypeEnum;
import com.cheche365.cheche.manage.common.service.BaseService;
import com.cheche365.cheche.manage.common.web.model.sms.MessageLogQuery;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by lyh on 2015/10/14.
 */
@Service
@Transactional
public class ScheduleMessageLogService extends BaseService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ScheduleMessageLogRepository scheduleMessageLogRepository;

    public Page<ScheduleMessageLog> getScheduleMessageLogByPage(MessageLogQuery query) {
        try {
            return findBySpecAndPaginate(query,
                    super.buildPageable(query.getCurrentPage(), query.getPageSize(), Sort.Direction.DESC, BaseService.SORT_ID));
        } catch (Exception e) {
            logger.error("get ScheduleMessageLog by page has error", e);
        }
        return null;
    }

    /**
     * 分页查询
     *
     * @param pageable 分页信息
     * @return Page<ScheduleMessageLog>
     */
    private Page<ScheduleMessageLog> findBySpecAndPaginate(MessageLogQuery publicQuery, Pageable pageable) throws Exception {
        return scheduleMessageLogRepository.findAll((root, query, cb) -> {
            CriteriaQuery<ScheduleMessageLog> criteriaQuery = cb.createQuery(ScheduleMessageLog.class);

            Path<String> mobilePath = root.get("mobile");
            //条件构造
            List<Predicate> predicateList = new ArrayList<>();
            if (StringUtils.isNotBlank(publicQuery.getKeyword())) {
                // 手机号
                if (publicQuery.getKeyType() == SMSKeyTypeEnum.MOBILE.ordinal()) {
                    predicateList.add(cb.like(mobilePath, publicQuery.getKeyword() + "%"));
                }
                // 模板号
                else if (publicQuery.getKeyType() == SMSKeyTypeEnum.TEMPLATE_NO.ordinal()) {
                    Path<String> templateZucpCodePath = root.get("scheduleMessage").get("smsTemplate").get("zucpCode");
                    Path<String> templateYxtCodePath = root.get("scheduleMessage").get("smsTemplate").get("yxtCode");
                    predicateList.add(cb.or(
                            cb.like(templateZucpCodePath, publicQuery.getKeyword() + "%"),
                            cb.like(templateYxtCodePath, publicQuery.getKeyword() + "%")));
                }
            }
            //发送状态
            if (publicQuery.getStatus() != null) {
                Path<Integer> statusPath = root.get("status");
                predicateList.add(cb.equal(statusPath, publicQuery.getStatus()));
            }

            Predicate[] predicates = new Predicate[predicateList.size()];
            predicates = predicateList.toArray(predicates);
            return criteriaQuery.where(predicates).getRestriction();
        }, pageable);
    }
}
