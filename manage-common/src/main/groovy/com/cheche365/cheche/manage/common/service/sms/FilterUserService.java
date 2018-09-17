package com.cheche365.cheche.manage.common.service.sms;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.FilterUser;
import com.cheche365.cheche.core.model.SqlParameter;
import com.cheche365.cheche.core.model.SqlTemplate;
import com.cheche365.cheche.core.repository.FilterUserRepository;
import com.cheche365.cheche.core.repository.SqlParameterRepository;
import com.cheche365.cheche.core.repository.SqlTemplateRepository;
import com.cheche365.cheche.core.repository.UserRepository;
import com.cheche365.cheche.manage.common.constants.SMSKeyTypeEnum;
import com.cheche365.cheche.manage.common.constants.SMSMessageConstants;
import com.cheche365.cheche.manage.common.model.PublicQuery;
import com.cheche365.cheche.manage.common.service.BaseService;
import com.cheche365.cheche.manage.common.service.InternalUserManageService;
import com.cheche365.cheche.manage.common.web.model.PageInfo;
import com.cheche365.cheche.manage.common.web.model.PageViewModel;
import com.cheche365.cheche.manage.common.web.model.ResultModel;
import com.cheche365.cheche.manage.common.web.model.sms.FilterUserViewModel;
import com.cheche365.cheche.manage.common.web.model.sms.SqlTemplateViewModel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

@Service(value = "filterUserService")
public class FilterUserService extends BaseService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private FilterUserRepository filterUserRepository;

    @Autowired
    private SqlTemplateRepository sqlTemplateRepository;

    @Autowired
    private SqlParameterRepository sqlParameterRepository;

    @Autowired
    private SqlTemplateResource sqlTemplateResource;

    @Autowired
    private InternalUserManageService internalUserManageService;

    @Autowired
    private UserRepository userRepository;


    /**
     * 新建筛选用户功能
     *
     * @param viewData
     */
    public ResultModel add(FilterUserViewModel viewData) {
        try {
            if (filterUserRepository.findFirstByName(viewData.getName()) != null) {
                return new ResultModel(false, "名称已经存在");
            }
            //保存筛选用户功能
            filterUserRepository.save(this.createFilterUser(viewData));
            return new ResultModel();
        } catch (Exception e) {
            logger.error("add filter user has error", e);
        }
        return new ResultModel(false, "保存失败");
    }

    /**
     * 查询筛选用户功能详情
     *
     * @param id
     * @return
     */
    public FilterUserViewModel findById(Long id) {
        FilterUser filterUser = filterUserRepository.findOne(id);

        return this.createViewData(filterUser);
    }

    /**
     * 修改筛选用户功能
     *
     * @param id
     * @param viewData
     * @return
     */
    public ResultModel update(Long id, FilterUserViewModel viewData) {
        try {
            if (id == null || id == 0) {
                throw new Exception("filter user id is null");
            }
            Long count = filterUserRepository.countByIdNotAndName(id, viewData.getName());
            if (count != null && count > 0) {
                return new ResultModel(false, "名称已经存在");
            }
            viewData.setId(id);
            //保存筛选用户功能
            filterUserRepository.save(this.createFilterUser(viewData));

            return new ResultModel();
        } catch (Exception e) {
            logger.error("update filter user has error", e);
        }
        return new ResultModel(false, "保存失败");
    }

    /**
     * 查询筛选用户功能
     *
     * @return
     */
    public Page<FilterUser> getFilterUserByPage(PublicQuery query) {
        try {
            return findBySpecAndPaginate(query,
                    super.buildPageable(query.getCurrentPage(), query.getPageSize(), Sort.Direction.DESC, BaseService.SORT_CREATE_TIME));
        } catch (Exception e) {
            logger.error("get filter user by page has error", e);
        }
        return null;
    }

    /**
     * 分页查询
     *
     * @param pageable 分页信息
     * @return Page<FilterUser>
     */
    private Page<FilterUser> findBySpecAndPaginate(PublicQuery publicQuery, Pageable pageable) throws Exception {
        return filterUserRepository.findAll(new Specification<FilterUser>() {
            @Override
            public Predicate toPredicate(Root<FilterUser> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                CriteriaQuery<FilterUser> criteriaQuery = cb.createQuery(FilterUser.class);
                //条件构造
                List<Predicate> predicateList = new ArrayList<>();
                if (StringUtils.isNotBlank(publicQuery.getKeyword())) {
                    // 名字
                    if (publicQuery.getKeyType() == SMSKeyTypeEnum.FILTER_USER_NAME.ordinal()) {
                        //获取实体属性
                        Path<String> namePath = root.get("name");
                        predicateList.add(cb.like(namePath, publicQuery.getKeyword() + "%"));
                    }
                    // 备注
                    else if (publicQuery.getKeyType() == SMSKeyTypeEnum.COMMENT.ordinal()) {
                        //获取实体属性
                        Path<String> commentPath = root.get("comment");
                        predicateList.add(cb.like(commentPath, publicQuery.getKeyword() + "%"));
                    }
                }

                Predicate[] predicates = new Predicate[predicateList.size()];
                predicates = predicateList.toArray(predicates);
                return criteriaQuery.where(predicates).getRestriction();
            }
        }, pageable);
    }

    /**
     * 构建分页信息
     *
     * @param currentPage 当前页面
     * @param pageSize    每页显示数
     * @return Pageable
     */
    private Pageable buildPageable(int currentPage, int pageSize) {
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        return new PageRequest(currentPage - 1, pageSize, sort);
    }

    /**
     * 封装展示层实体
     *
     * @param page 分页信息
     * @return PageViewModel<PageViewData>
     */
    public PageViewModel<FilterUserViewModel> createResult(Page page) throws Exception {
        PageViewModel model = new PageViewModel<FilterUserViewModel>();

        PageInfo pageInfo = new PageInfo();
        pageInfo.setTotalElements(page.getTotalElements());
        pageInfo.setTotalPage(page.getTotalPages());
        model.setPageInfo(pageInfo);

        List<FilterUserViewModel> pageViewDataList = new ArrayList<>();
        for (FilterUser filterUser : (List<FilterUser>) page.getContent()) {
            FilterUserViewModel viewData = createViewData(filterUser);
            pageViewDataList.add(viewData);
        }
        model.setViewList(pageViewDataList);

        return model;
    }


    /**
     * 改变筛选用户功能状态
     *
     * @param id
     * @param disable
     * @return
     */
    public boolean changeStatus(Long id, Integer disable) {
        try {
            // 筛选用户功能
            FilterUser filterUser = filterUserRepository.findOne(id);

            // 启用或禁用
            filterUser.setDisable(disable == 1 ? true : false);
            filterUser.setUpdateTime(Calendar.getInstance().getTime());
            filterUserRepository.save(filterUser);

            return true;
        } catch (Exception e) {
            logger.error("change filter user status has error", e);
        }

        return false;
    }

    private FilterUser createFilterUser(FilterUserViewModel viewModel) {
        FilterUser filterUser = new FilterUser();
        if (viewModel.getId() != 0) {
            filterUser = filterUserRepository.findOne(viewModel.getId());
        }
        filterUser.setName(viewModel.getName());
        SqlTemplate sqlTemplate = sqlTemplateRepository.findOne(viewModel.getSqlTemplateId());
        filterUser.setSqlTemplate(sqlTemplate);
        String comment = viewModel.getComment();
        if (StringUtils.isNotBlank(comment)) {
            filterUser.setComment(comment.replace("\n", "\\r\\n"));
        }
        filterUser.setParameter(viewModel.getParameter());
        filterUser.setCreateTime(viewModel.getId() == 0 ? Calendar.getInstance().getTime() : filterUser.getCreateTime());
        filterUser.setUpdateTime(Calendar.getInstance().getTime());
        filterUser.setDisable(viewModel.getId() == 0 ? true : filterUser.isDisable());
        filterUser.setOperator(internalUserManageService.getCurrentInternalUser());

        return filterUser;
    }

    public FilterUserViewModel createViewData(FilterUser filterUser) {
        FilterUserViewModel viewData = new FilterUserViewModel();
        viewData.setId(filterUser.getId());
        viewData.setName(filterUser.getName());
        SqlTemplate sqlTemplate = filterUser.getSqlTemplate();
        if (sqlTemplate != null) {
            String parameter = filterUser.getParameter();
            String content = replaceParameter(sqlTemplate, parameter);
            viewData.setContent(content);
            SqlTemplateViewModel sqlTemplateViewModel = sqlTemplateResource.createViewData(sqlTemplate);
            viewData.setSqlTemplateViewModel(sqlTemplateViewModel);
            viewData.setSqlTemplateId(sqlTemplate.getId());
        }

        viewData.setParameter(filterUser.getParameter());
        viewData.setComment(filterUser.getComment());
        viewData.setDisable(filterUser.isDisable() ? 1 : 0);
        viewData.setCreateTime(DateUtils.getDateString(filterUser.getCreateTime(), DateUtils.DATE_LONGTIME24_PATTERN));
        viewData.setUpdateTime(DateUtils.getDateString(filterUser.getUpdateTime(), DateUtils.DATE_LONGTIME24_PATTERN));
        viewData.setOperator(filterUser.getOperator().getName());

        return viewData;
    }

    private String replaceParameter(SqlTemplate sqlTemplate, String parameter) {
        Pattern pattern = Pattern.compile(SMSMessageConstants.MESSAGE_PATTERN);
        Matcher matcher = pattern.matcher(sqlTemplate.getContent());
        String[] parameterArr = parameter.split("&");
        int index = 0;
        String content = sqlTemplate.getContent();
        while (matcher.find()) {
            String sqlParameterCode = matcher.group(0);
            SqlParameter sqlParameter = sqlParameterRepository.findFirstByCode(sqlParameterCode);
            if (SMSMessageConstants.SQL_PARAMETER_TYPE_VARCHAR.equals(sqlParameter.getType())
                    || SMSMessageConstants.SQL_PARAMETER_TYPE_DATE.equals(sqlParameter.getType())
                    || SMSMessageConstants.SQL_PARAMETER_TYPE_TIME.equals(sqlParameter.getType())) {
                content = content.replace(sqlParameterCode, "'" + parameterArr[index] + "'");
            } else if (SMSMessageConstants.SQL_PARAMETER_TYPE_MULTI_SELECT.equals(sqlParameter.getType())) {
                String sqlParameterValue = "";
                for (String parameterValue : parameterArr[index].split(",")) {
                    sqlParameterValue += "'" + parameterValue + "',";
                }
                content = content.replace(sqlParameterCode, sqlParameterValue.substring(0, sqlParameterValue.length() - 1));
            } else {
                content = content.replace(sqlParameterCode, parameterArr[index]);
            }
            index++;
        }
        return content;
    }

    //获取发送用户组的用户数量
    public int getFilterUserCount(Long filterUserId) {
        FilterUser filterUser = filterUserRepository.findOne(filterUserId);
        if (filterUser == null) {
            return 0;
        }
        String sqlContent = filterUser.getSqlTemplate().getContent().replaceAll(SMSMessageConstants.MESSAGE_PATTERN, "?");
        String[] sqlParameters = StringUtils.isEmpty(filterUser.getParameter()) ? null : filterUser.getParameter().split("&");
        Integer allFilterUserMobileCount = userRepository.findUserMobileCount(
                removeSelect(removeOrders(sqlContent)), sqlParameters);
        return allFilterUserMobileCount;
    }

    private String removeSelect(String qlString) {
        int beginPos = qlString.toLowerCase().indexOf("from");
        String key = qlString.substring(6, beginPos).trim();
        return "select count(" + key + ") " + qlString.substring(beginPos);
    }

    private String removeOrders(String qlString) {
        Pattern p = Pattern.compile("order\\s*by[\\w|\\W|\\s|\\S]*", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(qlString);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, "");
        }
        m.appendTail(sb);
        return sb.toString();
    }

}
