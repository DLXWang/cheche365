package com.cheche365.cheche.ordercenter.test;

import com.cheche365.cheche.core.app.config.CoreConfig;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.manage.common.util.AssertUtil;
import com.cheche365.cheche.manage.common.web.model.PageInfo;
import com.cheche365.cheche.ordercenter.app.config.OrderCenterConfig;
import com.cheche365.cheche.ordercenter.service.nationwideOrder.InstitutionManageService;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by sunhuazhong on 2015/11/17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        classes = {OrderCenterConfig.class, CoreConfig.class}
)
@EnableAutoConfiguration
@EnableWebMvc
@WebAppConfiguration
@EnableSpringDataWebSupport
@TransactionConfiguration
public class InstitutionServiceTest {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private InstitutionManageService institutionManageService;

    @Test
    public void testAdd() {
        Institution institution = new Institution();
        institution.setName("天朝公司");
        institution.setComment("天朝公司");
        institution.setContactName("张三");
        institution.setContactEmail("zhangsan@cheche365.com");
        institution.setContactMobile("13661362871");
        institution.setContactQq("2286313710");
        institution.setChecheName("李四");
        institution.setChecheEmail("lisi@cheche365.com");
        institution.setChecheMobile("15010066753");
        institution.setChecheQq("258075045");
        List<InstitutionBankAccount> bankAccountList = new ArrayList<>();
        InstitutionBankAccount bankAccount1 = new InstitutionBankAccount();
        bankAccount1.setBank("建设银行");
        bankAccount1.setAccountName("张三1");
        bankAccount1.setAccountNo("zhangsan1");
        bankAccountList.add(bankAccount1);
        InstitutionBankAccount bankAccount2 = new InstitutionBankAccount();
        bankAccount2.setBank("建设银行");
        bankAccount2.setAccountName("张三2");
        bankAccount2.setAccountNo("zhangsan2");
        bankAccountList.add(bankAccount1);
        bankAccountList.add(bankAccount2);
        institution.setBankAccountList(bankAccountList);
        List<InstitutionRebate> rebateList = new ArrayList<>();
        InstitutionRebate rebate1 = new InstitutionRebate();
        Area area1 = new Area();
        area1.setId(120000l);
        rebate1.setArea(area1);
        InsuranceCompany insuranceCompany1 = new InsuranceCompany();
        insuranceCompany1.setId(10000l);
        rebate1.setInsuranceCompany(insuranceCompany1);
        rebate1.setCommercialRebate(10.00);
        rebate1.setCompulsoryRebate(12.00);
        InstitutionRebate rebate2 = new InstitutionRebate();
        Area area2 = new Area();
        area2.setId(130100l);
        rebate2.setArea(area2);
        InsuranceCompany insuranceCompany2 = new InsuranceCompany();
        insuranceCompany2.setId(15000l);
        rebate2.setInsuranceCompany(insuranceCompany2);
        rebate2.setCommercialRebate(20.00);
        rebate2.setCompulsoryRebate(22.00);
        rebateList.add(rebate1);
        rebateList.add(rebate2);
        institution.setRebateList(rebateList);
        boolean isSuccess = institutionManageService.addInstitution(institution);
        AssertUtil.isTrue(isSuccess);
    }

    @Test
    public void testUpdate() {
        Institution institution = new Institution();
        institution.setId(4l);
        institution.setName("天朝公司");
        institution.setComment("天朝公司");
        institution.setContactName("张三");
        institution.setContactEmail("zhangsan@cheche365.com");
        institution.setContactMobile("13661362871");
        institution.setContactQq("2286313710");
        institution.setChecheName("李四");
        institution.setChecheEmail("lisi@cheche365.com");
        institution.setChecheMobile("15010066753");
        institution.setChecheQq("258075045");
        List<InstitutionBankAccount> bankAccountList = new ArrayList<>();
        InstitutionBankAccount bankAccount1 = new InstitutionBankAccount();
        bankAccount1.setBank("建设银行");
        bankAccount1.setAccountName("张三11");
        bankAccount1.setAccountNo("zhangsan11");
        bankAccountList.add(bankAccount1);
        InstitutionBankAccount bankAccount2 = new InstitutionBankAccount();
        bankAccount2.setBank("建设银行");
        bankAccount2.setAccountName("张三22");
        bankAccount2.setAccountNo("zhangsan22");
        bankAccountList.add(bankAccount1);
        bankAccountList.add(bankAccount2);
        institution.setBankAccountList(bankAccountList);
        List<InstitutionRebate> rebateList = new ArrayList<>();
        InstitutionRebate rebate1 = new InstitutionRebate();
        Area area1 = new Area();
        area1.setId(120000l);
        rebate1.setArea(area1);
        InsuranceCompany insuranceCompany1 = new InsuranceCompany();
        insuranceCompany1.setId(10000l);
        rebate1.setInsuranceCompany(insuranceCompany1);
        rebate1.setCommercialRebate(30.00);
        rebate1.setCompulsoryRebate(32.00);
        InstitutionRebate rebate2 = new InstitutionRebate();
        Area area2 = new Area();
        area2.setId(130100l);
        rebate2.setArea(area2);
        InsuranceCompany insuranceCompany2 = new InsuranceCompany();
        insuranceCompany2.setId(15000l);
        rebate2.setInsuranceCompany(insuranceCompany2);
        rebate2.setCommercialRebate(40.00);
        rebate2.setCompulsoryRebate(42.00);
        rebateList.add(rebate1);
        rebateList.add(rebate2);
        institution.setRebateList(rebateList);
        boolean isSuccess = institutionManageService.updateInstitution(institution.getId(), institution);
        AssertUtil.isTrue(isSuccess);
    }

    @Test
    public void testDetail() {
        Long institutionId = 4l;
        Institution institution = institutionManageService.findById(institutionId);
        List<InstitutionBankAccount> bankAccountList = institution.getBankAccountList();
        if (CollectionUtils.isNotEmpty(bankAccountList)) {
            for (InstitutionBankAccount bankAccount : bankAccountList) {
                logger.debug("开户行:{}，账户:{}，帐号:{}",
                        bankAccount.getBank(), bankAccount.getAccountName(), bankAccount.getAccountNo());
            }
        }
        List<InstitutionRebate> rebateList = institution.getRebateList();
        if (CollectionUtils.isNotEmpty(rebateList)) {
            for (InstitutionRebate rebate : rebateList) {
                logger.debug("城市:{}，保险公司:{}，商业险佣金:{}，交强险佣金:{}",
                        rebate.getArea().getName(), rebate.getInsuranceCompany().getName(), rebate.getCommercialRebate(), rebate.getCompulsoryRebate());
            }
        }
    }

    @Test
    public void testSearch() {
        Integer currentPage = 1;
        Integer pageSize = 20;
        String keyword = null;
        Map<String, Object> institutionMap = institutionManageService.search(currentPage, pageSize, keyword);
        PageInfo pageInfo = (PageInfo) institutionMap.get("pageInfo");
        logger.debug("查询数据总条数:{}", pageInfo.getTotalElements());
        logger.debug("查询数据总页数:{}", pageInfo.getTotalPage());
        if (institutionMap.get("content") != null) {
            List<Institution> institutionList = (List<Institution>) institutionMap.get("content");
            logger.debug("当前页数总条数:{}", institutionList.size());
        } else {
            logger.debug("当前页数总条数:0");
        }
    }

    @Test
    public void testSwitchStatus() {
        Long institutionId = 4l;
        Integer operationType = 1;
        boolean isSuccess = institutionManageService.switchStatus(institutionId, operationType);
        AssertUtil.isTrue(isSuccess);
    }

    @Test
    public void testListEnable() {
        List<Institution> institutionList = institutionManageService.listEnable();
        if (CollectionUtils.isEmpty(institutionList)) {
            logger.debug("list enable institution is empty");
        } else {
            logger.debug("list enable institution is not empty, size:{}", institutionList.size());
        }
    }

    @Test
    public void testListArea() {
        Long institutionId = 4l;
        List<Area> areaList = institutionManageService.listArea(institutionId);
        if (CollectionUtils.isEmpty(areaList)) {
            logger.debug("list area of institution is empty");
        } else {
            logger.debug("list area of institution is not empty, size:{}", areaList.size());
        }
    }

    @Test
    public void testListInsuranceCompany() {
        Long institutionId = 4l;
        List<InsuranceCompany> insuranceCompanyList = institutionManageService.listInsuranceCompany(institutionId);
        if (CollectionUtils.isEmpty(insuranceCompanyList)) {
            logger.debug("list insurance company of institution is empty");
        } else {
            logger.debug("list insurance company of institution is not empty, size:{}", insuranceCompanyList.size());
        }
    }

    @Test
    public void testListInstitution() {
        Long areaId = 120000l;
        Long insuranceCompanyId = 10000l;
        List<InstitutionRebate> institutionRebateList = institutionManageService.listInstitutionRebate(areaId, insuranceCompanyId);
        if (CollectionUtils.isEmpty(institutionRebateList)) {
            logger.debug("list institution of area and insurance company is empty");
        } else {
            logger.debug("list institution of area and insurance company is not empty, size:{}", institutionRebateList.size());
        }
    }

    @Test
    public void testCheckName() {
        Long institutionId = 4l;
        String name = "11";
        boolean isSuccess = institutionManageService.checkName(institutionId, name);
        if (isSuccess) {
            logger.debug("该名称可以使用");
        } else {
            logger.debug("该名称已经使用");
        }
    }
}
