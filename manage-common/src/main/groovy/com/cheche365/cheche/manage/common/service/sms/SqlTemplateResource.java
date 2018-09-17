package com.cheche365.cheche.manage.common.service.sms;

import com.cheche365.cheche.core.model.Marketing;
import com.cheche365.cheche.core.model.SqlParameter;
import com.cheche365.cheche.core.model.SqlTemplate;
import com.cheche365.cheche.core.repository.MarketingRepository;
import com.cheche365.cheche.core.repository.SqlParameterRepository;
import com.cheche365.cheche.core.repository.SqlTemplateRepository;
import com.cheche365.cheche.manage.common.constants.SMSMessageConstants;
import com.cheche365.cheche.manage.common.service.BaseService;
import com.cheche365.cheche.manage.common.web.model.MarketingViewModel;
import com.cheche365.cheche.manage.common.web.model.sms.SqlParameterViewModel;
import com.cheche365.cheche.manage.common.web.model.sms.SqlTemplateViewModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by guoweifu on 2015/10/13.
 */
@Component
public class SqlTemplateResource extends BaseService<SqlTemplate,SqlTemplate> {

    @Autowired
    private SqlTemplateRepository sqlTemplateRepository;

    @Autowired
    private SqlParameterRepository sqlParameterRepository;

    @Autowired
    private MarketingRepository marketingRepository;

    /**
     * 获取所有sql模板
     *
     * @return
     */
    public List<SqlTemplateViewModel> getAllSqlTemplates() {
        List<SqlTemplateViewModel> sqlTemplateViewModelList = new ArrayList<>();
        List<SqlTemplate> sqlTemplateList = super.getAll(sqlTemplateRepository);
        for (SqlTemplate sqlTemplate : sqlTemplateList) {
            SqlTemplateViewModel viewData = new SqlTemplateViewModel();
            viewData.setId(sqlTemplate.getId());
            viewData.setName(sqlTemplate.getName());
            viewData.setContent(sqlTemplate.getContent());
            sqlTemplateViewModelList.add(viewData);
        }

        return sqlTemplateViewModelList;
    }

    /**
     * 获取sql模板
     *
     * @param sqlTemplateId
     * @return
     */
    public SqlTemplateViewModel getSqlTemplate(Long sqlTemplateId) {
        SqlTemplate sqlTemplate = sqlTemplateRepository.findOne(sqlTemplateId);
        return this.createViewData(sqlTemplate);
    }

    public SqlTemplateViewModel createViewData(SqlTemplate sqlTemplate){
        SqlTemplateViewModel viewData = new SqlTemplateViewModel();
        viewData.setId(sqlTemplate.getId());
        viewData.setName(sqlTemplate.getName());
        String content = sqlTemplate.getContent();
        viewData.setContent(content);

        List<SqlParameterViewModel> sqlTemplateViewModelList = new ArrayList<>();
        if(StringUtils.isNotBlank(content)){
            Pattern pattern = Pattern.compile(SMSMessageConstants.MESSAGE_PATTERN);
            Matcher matcher = pattern.matcher(content);
            while (matcher.find()) {
                String parameterCode = matcher.group(0);
                //获取参数
                SqlParameter sqlParameter = sqlParameterRepository.findFirstByCode(parameterCode);
                if(sqlParameter!=null){
                    SqlParameterViewModel sqlParameterViewModel = new SqlParameterViewModel();
                    sqlParameterViewModel.setId(sqlParameter.getId());
                    sqlParameterViewModel.setName(sqlParameter.getName());
                    sqlParameterViewModel.setCode(sqlParameter.getCode());
                    sqlParameterViewModel.setType(sqlParameter.getType());
                    sqlParameterViewModel.setPlaceholder(sqlParameter.getPlaceholder());
                    sqlParameterViewModel.setLength(sqlParameter.getLength());
                    if(sqlParameter.getType().toLowerCase().contains("select")) {
                        if(sqlParameter.getCode().toLowerCase().contains("marketing")) {
                            List<MarketingViewModel> marketingViewModelList = new ArrayList<>();
                            Iterable<Marketing> marketingIterable = marketingRepository.findAll();
                            Iterator<Marketing> marketingIterator = marketingIterable.iterator();
                            while(marketingIterator.hasNext()) {
                                Marketing marketing = marketingIterator.next();
                                MarketingViewModel viewModel = new MarketingViewModel();
                                viewModel.setId(marketing.getId());
                                if(StringUtils.isNotEmpty(marketing.getMarketingType())) {
                                    viewModel.setName(marketing.getName() + "(" + marketing.getMarketingType().toUpperCase() + ")");
                                } else {
                                    viewModel.setName(marketing.getName() + "(M)");
                                }
                                marketingViewModelList.add(viewModel);
                                sqlParameterViewModel.setMarketingViewModelList(marketingViewModelList);
                            }
                        }
                    }
                    sqlTemplateViewModelList.add(sqlParameterViewModel);
                }
            }
            viewData.setSqlParameterViewModelList(sqlTemplateViewModelList);
        }

        return viewData;
    }
}
