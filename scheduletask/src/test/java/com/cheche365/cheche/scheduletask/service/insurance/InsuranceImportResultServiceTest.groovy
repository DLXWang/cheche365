package com.cheche365.cheche.scheduletask.service.insurance

import com.cheche365.cheche.core.app.config.CoreConfig
import com.cheche365.cheche.manage.common.repository.OfflineOrderImportHistoryRepository
import com.cheche365.cheche.scheduletask.app.config.ScheduleTaskConfig
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

/**
 * Created by yinJianBin on 2017/11/7.
 */
@RunWith(SpringJUnit4ClassRunner)
@ContextConfiguration(
        classes = [CoreConfig, ScheduleTaskConfig]
)
class InsuranceImportResultServiceTest extends GroovyTestCase {

    @Autowired
    OfflineOrderImportHistoryRepository historyRepository
    @Autowired
    InsuranceImportResultService insuranceImportResultService

    void setUp() {
        super.setUp()

    }

    void tearDown() {

    }

    @Test
    void testProcessData() {
        println 'test start'
        def history = historyRepository.findOne(46L)
        insuranceImportResultService.processData(history)
    }
}
