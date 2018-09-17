package com.cheche365.cheche.ordercenter.service.insurance;

import com.cheche365.cheche.core.app.config.CoreConfig;
import com.cheche365.cheche.manage.common.exception.FileUploadException;
import com.cheche365.cheche.ordercenter.app.config.OrderCenterConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * Created by yinJianBin on 2017/3/6.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        classes = {OrderCenterConfig.class, CoreConfig.class}
)
@EnableAutoConfiguration
@EnableWebMvc
@WebAppConfiguration
@EnableSpringDataWebSupport
public class DailyInsuranceOfferUploadServiceTest {

    private org.slf4j.Logger logger = LoggerFactory.getLogger(DailyInsuranceOfferUploadServiceTest.class);

    @Autowired
    private DailyInsuranceOfferUploadService dailyInsuranceOfferUploadService;

    @Test
    public void testImportReport() {
        try {
            dailyInsuranceOfferUploadService.importReport(null);
        } catch (FileUploadException e) {
            logger.error("更新按天买保险分享活动报表失败", e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
