package com.cheche365.cheche.ordercenter.web.controller.nationwideOrder;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.service.AreaService;
import com.cheche365.cheche.core.service.AutoService;
import com.cheche365.cheche.core.service.InstitutionRebateHistoryTempService;
import com.cheche365.cheche.core.service.InsuranceCompanyService;
import com.cheche365.cheche.core.util.AutoUtils;
import com.cheche365.cheche.core.util.BeanUtil;
import com.cheche365.cheche.manage.common.exception.FieldValidtorException;
import com.cheche365.cheche.ordercenter.service.nationwideOrder.InstitutionManageTempService;
import com.cheche365.cheche.ordercenter.service.user.OrderCenterInternalUserManageService;
import com.cheche365.cheche.ordercenter.web.model.InsuranceCompanyData;
import com.cheche365.cheche.manage.common.web.model.PageInfo;
import com.cheche365.cheche.manage.common.web.model.PageViewModel;
import com.cheche365.cheche.manage.common.web.model.ResultModel;
import com.cheche365.cheche.ordercenter.web.model.area.AreaViewData;
import com.cheche365.cheche.ordercenter.web.model.nationwideOrder.InstitutionBankAccountViewModel;
import com.cheche365.cheche.ordercenter.web.model.nationwideOrder.InstitutionHistoryRebateHistoryViewModel;
import com.cheche365.cheche.ordercenter.web.model.nationwideOrder.InstitutionRebateViewModel;
import com.cheche365.cheche.ordercenter.web.model.nationwideOrder.InstitutionViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 出单机构
 * Created by sunhuazhong on 2015/11/16.
 */
@RestController
@RequestMapping("/orderCenter/nationwide/institutionTemp")
public class InstitutionTempController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private InstitutionManageTempService institutionManageTempService;

    @Autowired
    private AutoService autoService;

    @Autowired
    private InstitutionRebateHistoryTempService institutionRebateHistoryTempService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private InsuranceCompanyService insuranceCompanyService;

    @Autowired
    private AreaService areaService;

    @Autowired
    private OrderCenterInternalUserManageService orderCenterInternalUserManageService;

    private static final String INSTITUTION_OPERATE_TAG = "institution.rebate.operate.id";

    /**
     * 新增出单机构
     *
     * @param model
     * @param result
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.POST)
//    @VisitorPermission("op010202")
    @Transactional
    public ResultModel add(@Valid @RequestBody InstitutionViewModel model, BindingResult result) {
        if (result.hasErrors()) {
            throw new RuntimeException("some required info has been missed");
        }
        InstitutionTemp institutionTemp = createInstitution(model);
        // 验证出单机构名称
        boolean isSameName = institutionManageTempService.checkName(institutionTemp.getId(), institutionTemp.getName());
        if (!isSameName) {
            return new ResultModel(false, "该出单机构名称已存在");
        }
        boolean isSuccess = institutionManageTempService.addInstitutionTemp(institutionTemp);
        if (isSuccess) {
            return new ResultModel(true, "新建出单机构成功");
        } else {
            return new ResultModel(false, "新建出单机构失败");
        }
    }

    /**
     * 修改出单机构
     *
     * @param institutionId
     * @param model
     * @param result
     * @return
     */
    @RequestMapping(value = "/{institutionId}", method = RequestMethod.PUT)
//    @VisitorPermission("op010206")
    @Transactional
    public ResultModel update(@PathVariable Long institutionId, @Valid @RequestBody InstitutionViewModel model, BindingResult result) {
        if (model.getId() != null) {
            boolean notExist = stringRedisTemplate.opsForHash().putIfAbsent(INSTITUTION_OPERATE_TAG, model.getId().toString(), "");
            if (!notExist) {
                return new ResultModel(false, "系统正在处理当前出单机构数据，请稍候再操作");
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("update institution by id:{}", institutionId);
        }
        if (result.hasErrors()) {
            throw new RuntimeException("some required info has been missed");
        }
        try {
            InstitutionTemp institutionTemp = createInstitution(model);
            // 验证出单机构名称
            boolean isSameName = institutionManageTempService.checkName(institutionTemp.getId(), institutionTemp.getName());
            if (!isSameName) {
                return new ResultModel(false, "该出单机构名称已存在");
            }
            boolean isSuccess = institutionManageTempService.updateInstitutionTemp(institutionId, institutionTemp);
            stringRedisTemplate.opsForHash().delete(INSTITUTION_OPERATE_TAG, model.getId().toString());
            if (isSuccess) {
                return new ResultModel(true, "修改出单机构成功");
            } else {
                return new ResultModel(false, "修改出单机构失败");
            }
        } catch (Exception e) {
            logger.debug("update institution error ,agent id :{}", institutionId);
            return new ResultModel(false, "修改出单机构失败");
        } finally {
            stringRedisTemplate.opsForHash().delete(INSTITUTION_OPERATE_TAG, model.getId().toString());
        }

    }

    /**
     * 根据条件查询出单机构
     *
     * @param currentPage
     * @param pageSize
     * @param keyword
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
//    @VisitorPermission("op0102")
    public PageViewModel<InstitutionViewModel> search(@RequestParam(value = "currentPage", required = true) Integer currentPage,
                                                      @RequestParam(value = "pageSize", required = true) Integer pageSize,
                                                      @RequestParam(value = "keyword", required = false) String keyword) {
        if (currentPage == null || currentPage < 1) {
            throw new FieldValidtorException("list business activity, currentPage can not be null or less than 1");
        }

        if (pageSize == null || pageSize < 1) {
            throw new FieldValidtorException("list business activity, pageSize can not be null or less than 1");
        }

        Map<String, Object> institutionTempMap = institutionManageTempService.search(currentPage, pageSize, keyword);
        return createPageViewModel(institutionTempMap);
    }

    /**
     * 获取出单机构详情
     *
     * @param institutionId
     * @return
     */
    @RequestMapping(value = "/{institutionId}", method = RequestMethod.GET)
//    @VisitorPermission("op010203")
    public InstitutionViewModel findOne(@PathVariable Long institutionId) {
        if (institutionId == null || institutionId < 1) {
            throw new FieldValidtorException("find institution detail, id can not be null or less than 1");
        }

        InstitutionTemp institution = institutionManageTempService.findById(institutionId);
        return InstitutionViewModel.createDetailViewModel(institution);
    }

    /**
     * 启用或禁用出单机构
     *
     * @param institutionId
     * @param operationType，1-启用，0-禁用
     * @return
     */
    @RequestMapping(value = "/{institutionId}/{operationType}", method = RequestMethod.PUT)
//    @VisitorPermission("op010103")
    public ResultModel switchStatus(@PathVariable Long institutionId, @PathVariable Integer operationType) {
        if (institutionId == null || institutionId < 1) {
            throw new FieldValidtorException("operation institution, id can not be null or less than 1");
        }

        boolean isSuccess = institutionManageTempService.switchStatus(institutionId, operationType);
        if (isSuccess) {
            return new ResultModel();
        } else {
            return new ResultModel(false, "切换状态失败");
        }
    }

    /**
     * 获取可用的出单机构
     *
     * @return
     */
    @RequestMapping(value = "/enable", method = RequestMethod.GET)
    public List<InstitutionViewModel> listEnable() {
        List<InstitutionTemp> institutionTempList = institutionManageTempService.listEnable();
        if (CollectionUtils.isEmpty(institutionTempList)) {
            return null;
        }
        List<InstitutionViewModel> viewModelList = new ArrayList<>();
        for (InstitutionTemp institution : institutionTempList) {
            viewModelList.add(InstitutionViewModel.createViewModel(institution));
        }
        return viewModelList;
    }

    /**
     * 获取出单机构城市列表
     *
     * @param institutionId
     * @return
     */
    @RequestMapping(value = "/area/{institutionId}", method = RequestMethod.GET)
//    @VisitorPermission("op010203")
    public List<AreaViewData> listArea(@PathVariable Long institutionId) {
        if (institutionId == null || institutionId < 1) {
            throw new FieldValidtorException("find institution area info, id can not be null or less than 1");
        }

        List<Area> areaList = institutionManageTempService.listArea(institutionId);
        return createAreaViewModel(areaList);
    }

    /**
     * 获取出单机构保险公司列表
     *
     * @param institutionId
     * @return
     */
    @RequestMapping(value = "/insuranceCompany/{institutionId}", method = RequestMethod.GET)
//    @VisitorPermission("op010203")
    public List<InsuranceCompanyData> listInsuranceCompany(@PathVariable Long institutionId) {
        if (institutionId == null || institutionId < 1) {
            throw new FieldValidtorException("find institution insurance company info, id can not be null or less than 1");
        }

        List<InsuranceCompany> insuranceCompanyList = institutionManageTempService.listInsuranceCompany(institutionId);
        return createInsuranceCompanyViewModel(insuranceCompanyList);
    }


    /**
     * 获取指定区域和保险公司的出单机构佣金列表
     *
     * @param areaId
     * @param insuranceCompanyId
     * @return
     */
    @RequestMapping(value = "/assigned", method = RequestMethod.GET)
//    @VisitorPermission("op010203")
    public List<InstitutionRebateViewModel> listInstitutionRebate(@RequestParam(value = "areaId", required = true) Long areaId,
                                                                  @RequestParam(value = "insuranceCompanyId", required = true) Long insuranceCompanyId) {
        if (areaId == null || areaId < 1) {
            throw new FieldValidtorException("find institution rebate info, area id can not be null or less than 1");
        }

        if (insuranceCompanyId == null || insuranceCompanyId < 1) {
            throw new FieldValidtorException("find institution rebate info, insurance company id can not be null or less than 1");
        }

        List<InstitutionRebateTemp> institutionRebateList = institutionManageTempService.listInstitutionRebateTemp(areaId, insuranceCompanyId);
        return createInstitutionRebateViewModel(institutionRebateList);
    }

    /**
     * 根据所在城市和保险公司查询出单机构佣金
     *
     * @return
     */
    @RequestMapping(value = "/rebate", method = RequestMethod.GET)
    public List<InstitutionRebateViewModel> listInstitutionRebateByInsuranceCompanyAndArea(@RequestParam(value = "areaId", required = false) Long areaId,
                                                                                           @RequestParam(value = "licensePlateNo", required = false) String licensePlateNo,
                                                                                           @RequestParam(value = "insuranceCompanyId") Long insuranceCompanyId,
                                                                                           @RequestParam(value = "confirmOrderDate", required = false) String confirmOrderDate,
                                                                                           @RequestParam(value = "applicantDate", required = false) String applicantDate) {
        Date scopeDate;
        Area area;
        if (areaId != null) {
            area = areaService.findById(areaId);
        } else {
            area = AutoUtils.getAreaOfAuto(licensePlateNo);
        }
        if (confirmOrderDate != null) {
            scopeDate = DateUtils.getDate(confirmOrderDate, DateUtils.DATE_LONGTIME24_PATTERN);
        } else if (applicantDate != null) {
            scopeDate = DateUtils.getDate(applicantDate, DateUtils.DATE_LONGTIME24_PATTERN);
        } else {
            return null;
        }
        List<InstitutionRebateHistoryTemp> institutionRebateHistoryList = institutionRebateHistoryTempService.ListByAreaAndInsuranceCompanyAndDateTime(area, insuranceCompanyId, scopeDate);
        return createInstitutionRebateViewModelByHistory(institutionRebateHistoryList);
    }


    /**
     * 通过出单机构查询历史费率
     *
     * @param institutionId
     * @return
     */
    @RequestMapping(value = "/rebate/historyList", method = RequestMethod.GET)
//    @VisitorPermission("op0102")
    public List<InstitutionHistoryRebateHistoryViewModel> getRebateHistoryList(@RequestParam(value = "institutionId", required = false) Long institutionId) {
        List<InstitutionRebateHistoryTemp> rebateHistoryList = institutionRebateHistoryTempService.findByInstitutionId(institutionId);
        List<InstitutionHistoryRebateHistoryViewModel> rebateHistoryViewModels = new ArrayList<InstitutionHistoryRebateHistoryViewModel>();
        rebateHistoryList.forEach(rebateHistory -> {
            rebateHistoryViewModels.add(InstitutionHistoryRebateHistoryViewModel.createViewData(rebateHistory));
        });
        return rebateHistoryViewModels;
    }

    @RequestMapping(value = "/rebate/history", method = RequestMethod.POST)
    public ResultModel saveInstitutionRebateHistory(@Valid InstitutionHistoryRebateHistoryViewModel viewModel, BindingResult result) {
        if (result.hasErrors()) {
            logger.debug("update institution rebate history, validation has error");
            return new ResultModel(false, "请将信息填写完整");
        }
        InstitutionTemp institution = institutionManageTempService.findById(viewModel.getInstitution());
        if (institution == null) {
            return new ResultModel(false, "该出单机构不存在");
        }
        InstitutionRebateHistoryTemp history = createInstitutionRebateHistory(viewModel);
        InternalUser internalUser = orderCenterInternalUserManageService.getCurrentInternalUser();
        institutionRebateHistoryTempService.addInstitutionRebateHistoryTemp(history, internalUser);
        return new ResultModel(true, "保存成功");
    }


    private InstitutionRebateHistoryTemp createInstitutionRebateHistory(InstitutionHistoryRebateHistoryViewModel viewModel) {
        InstitutionRebateHistoryTemp institutionRebateHistory = new InstitutionRebateHistoryTemp();
        institutionRebateHistory.setInstitutionTemp(institutionManageTempService.findById(viewModel.getInstitution()));
        institutionRebateHistory.setInsuranceCompany(insuranceCompanyService.findById(viewModel.getInsuranceCompany()));
        institutionRebateHistory.setArea(areaService.findById(viewModel.getArea()));
        institutionRebateHistory.setCompulsoryRebate(viewModel.getCompulsoryRebate());
        institutionRebateHistory.setCommercialRebate(viewModel.getCommercialRebate());
        institutionRebateHistory.setStartTime(DateUtils.getDate(viewModel.getStartTime(), DateUtils.DATE_LONGTIME24_PATTERN));
        institutionRebateHistory.setOperation(InstitutionRebateHistory.OPERATION.ADD);
        return institutionRebateHistory;
    }

    private List<InstitutionRebateViewModel> createInstitutionRebateViewModel(List<InstitutionRebateTemp> institutionRebateList) {
        List<InstitutionRebateViewModel> institutionRebateViewModelList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(institutionRebateList)) {
            for (InstitutionRebateTemp institutionRebate : institutionRebateList) {
                institutionRebateViewModelList.add(InstitutionRebateViewModel.createViewModel(institutionRebate));
            }
        }
        return institutionRebateViewModelList;
    }

    private List<InstitutionRebateViewModel> createInstitutionRebateViewModelByHistory(List<InstitutionRebateHistoryTemp> institutionRebateHistoryListList) {
        List<InstitutionRebateViewModel> institutionRebateViewModelList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(institutionRebateHistoryListList)) {
            for (InstitutionRebateHistoryTemp institutionRebateHistory : institutionRebateHistoryListList) {
                InstitutionRebateTemp institutionRebate = new InstitutionRebateTemp();
                String[] properties = {"institutionTemp", "area", "insuranceCompany", "commercialRebate", "compulsoryRebate"};
                BeanUtil.copyPropertiesContain(institutionRebateHistory, institutionRebate, properties);
                institutionRebateViewModelList.add(InstitutionRebateViewModel.createViewModel(institutionRebate));
            }
        }
        return institutionRebateViewModelList;
    }

    private List<InsuranceCompanyData> createInsuranceCompanyViewModel(List<InsuranceCompany> insuranceCompanyList) {
        List<InsuranceCompanyData> insuranceCompanyDataList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(insuranceCompanyList)) {
            for (InsuranceCompany insuranceCompany : insuranceCompanyList) {
                insuranceCompanyDataList.add(InsuranceCompanyData.createViewModel(insuranceCompany));
            }
        }
        return insuranceCompanyDataList;
    }


    private List<AreaViewData> createAreaViewModel(List<Area> areaList) {
        List<AreaViewData> areaViewDataList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(areaList)) {
            for (Area area : areaList) {
                areaViewDataList.add(AreaViewData.createViewModel(area));
            }
        }
        return areaViewDataList;
    }

    /**
     * 组建出单机构对象
     *
     * @param viewModel
     * @return
     */
    private InstitutionTemp createInstitution(InstitutionViewModel viewModel) {
        InstitutionTemp institution = new InstitutionTemp();
        if (viewModel.getId() != null && viewModel.getId() != 0) {
            institution.setId(viewModel.getId());
        }
        String[] properties = new String[]{
            "name", "comment",
            "contactName", "contactMobile", "contactEmail", "contactQq",
            "checheName", "checheMobile", "checheEmail", "checheQq"
        };
        BeanUtil.copyPropertiesContain(viewModel, institution, properties);
        if (!CollectionUtils.isEmpty(viewModel.getBankAccountViewModelList())) {
            List<InstitutionBankAccountTemp> bankAccountList = new ArrayList<>();
            for (InstitutionBankAccountViewModel bankAccountViewModel : viewModel.getBankAccountViewModelList()) {
                InstitutionBankAccountTemp bankAccount = new InstitutionBankAccountTemp();
                if (bankAccountViewModel.getId() != null && bankAccountViewModel.getId() != 0) {
                    bankAccount.setId(bankAccountViewModel.getId());
                }
                bankAccount.setBank(bankAccountViewModel.getBank());
                bankAccount.setAccountName(bankAccountViewModel.getAccountName());
                bankAccount.setAccountNo(bankAccountViewModel.getAccountNo());
                bankAccountList.add(bankAccount);
            }
            institution.setbankAccountTempList(bankAccountList);
        }
        if (!CollectionUtils.isEmpty(viewModel.getRebateViewModelList())) {
            List<InstitutionRebateTemp> rebateList = new ArrayList<>();
            for (InstitutionRebateViewModel rebateViewModel : viewModel.getRebateViewModelList()) {
                InstitutionRebateTemp institutionRebate = new InstitutionRebateTemp();
                if (rebateViewModel.getId() != null && rebateViewModel.getId() != 0) {
                    institutionRebate.setId(rebateViewModel.getId());
                }
                Area area = new Area();
                area.setId(rebateViewModel.getAreaViewData().getId());
                institutionRebate.setArea(area);
                InsuranceCompany insuranceCompany = new InsuranceCompany();
                insuranceCompany.setId(rebateViewModel.getInsuranceCompanyData().getId());
                institutionRebate.setInsuranceCompany(insuranceCompany);
                institutionRebate.setCommercialRebate(rebateViewModel.getCommercialRebate());
                institutionRebate.setCompulsoryRebate(rebateViewModel.getCompulsoryRebate());
                rebateList.add(institutionRebate);
            }
            institution.setRebateListTemp(rebateList);
        }
        return institution;
    }

    private PageViewModel<InstitutionViewModel> createPageViewModel(Map<String, Object> institutionMap) {
        PageViewModel model = new PageViewModel<InstitutionViewModel>();
        model.setPageInfo((PageInfo) institutionMap.get("pageInfo"));
        List<InstitutionViewModel> pageViewDataList = new ArrayList<>();
        if (institutionMap.get("content") != null) {
            List<InstitutionTemp> institutionList = (List<InstitutionTemp>) institutionMap.get("content");
            for (InstitutionTemp institution : institutionList) {
                InstitutionViewModel viewModel = InstitutionViewModel.createDetailViewModel(institution);
                pageViewDataList.add(viewModel);
            }
        }
        model.setViewList(pageViewDataList);
        return model;
    }
}
