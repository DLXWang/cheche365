package com.cheche365.cheche.operationcenter.test;

import com.cheche365.cheche.core.app.config.CoreConfig;
import com.cheche365.cheche.core.model.SmsTemplate;
import com.cheche365.cheche.operationcenter.app.config.OperationCenterConfig;
import com.cheche365.cheche.manage.common.model.PublicQuery;
import com.cheche365.cheche.manage.common.service.sms.SmsTemplateService;
import com.cheche365.cheche.manage.common.web.model.PageInfo;
import com.cheche365.cheche.manage.common.web.model.sms.SmsTemplateViewModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.ArrayList;
import java.util.List;


/**
 * 短信模板Service测试类
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
    classes = {CoreConfig.class, OperationCenterConfig.class}
)
@EnableAutoConfiguration
@EnableWebMvc
@WebAppConfiguration
@EnableSpringDataWebSupport
@TransactionConfiguration
public class SmsTemplateServiceTest {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SmsTemplateService smsTemplateService;

    @Test
    public void add() {
        SmsTemplateViewModel viewDate = new SmsTemplateViewModel();
        viewDate.setName("测试短信模板");
        viewDate.setComment("备注");
        viewDate.setContent("内容");
        viewDate.setZucpCode("漫道模板号");
        viewDate.setYxtCode("盈信通模板号");
//        viewDate.setOperator();

        smsTemplateService.add(viewDate);
    }

    @Test
    public void update() {
        SmsTemplateViewModel viewDate = new SmsTemplateViewModel();
        viewDate.setId(new Long(1));
        viewDate.setName("测试短信模板1");
        viewDate.setComment("备注1");
        viewDate.setContent("内容1");
        viewDate.setZucpCode("漫道模板号1");
        viewDate.setYxtCode("盈信通模板号1");
        smsTemplateService.updateComment(viewDate.getId(), "备注修改");
    }

    @Test
    public void testSearch() {
        try {
            PublicQuery query = new PublicQuery();
            query.setCurrentPage(1);
            query.setPageSize(20);
            query.setKeyword("");
            query.setKeyType(1);
            Page<SmsTemplate> smsTemplatePage = smsTemplateService.getSmsTemplateByPage(query);
            PageInfo pageInfo = smsTemplateService.createPageInfo(smsTemplatePage);
            logger.debug("短信模板元素数：" + pageInfo.getTotalElements());
            logger.debug("短信模板总页数：" + pageInfo.getTotalPage());
            List<SmsTemplateViewModel> modelList = new ArrayList<>();
            for (SmsTemplate smsTemplate : smsTemplatePage.getContent()) {
                modelList.add(SmsTemplateViewModel.createViewData(smsTemplate));
            }
            if (!CollectionUtils.isEmpty(modelList)) {
                for (SmsTemplateViewModel viewModel : modelList) {
                    logger.debug("短信模板序号：" + viewModel.getId());
                    logger.debug("短信模板名称：" + viewModel.getName());
                    logger.debug("短信模板备注：" + viewModel.getComment());
                    logger.debug("短信模板内容：" + viewModel.getContent());
                    logger.debug("短信模板号 漫道：" + viewModel.getZucpCode());
                    logger.debug("短信模板号 盈信通：" + viewModel.getYxtCode());
                    logger.debug("短信模板状态：" + (viewModel.getDisable() == 1 ? "已禁用" : "已启用"));
                }
            }
        } catch (Exception e) {
            logger.error("PageInfo has error", e);
        }
    }

    @Test
    public void detail() {
        Long id = new Long(1);
        SmsTemplateViewModel viewModel = SmsTemplateViewModel.createViewData(smsTemplateService.findById(id));
        logger.debug("短信模板序号：" + viewModel.getId());
        logger.debug("短信模板名称：" + viewModel.getName());
        logger.debug("短信模板备注：" + viewModel.getComment());
        logger.debug("短信模板内容：" + viewModel.getContent());
        logger.debug("短信模板号 漫道：" + viewModel.getZucpCode());
        logger.debug("短信模板号 盈信通：" + viewModel.getYxtCode());
    }

    @Test
    public void changeStatus() {
        Long id = new Long(1);
        Integer disable = new Integer(1);
        smsTemplateService.changeStatus(id, disable);
    }

}
