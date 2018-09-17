package com.cheche365.cheche.admin.web.controller.auto;

import com.cheche365.cheche.admin.service.auto.AutoManagementService;
import com.cheche365.cheche.manage.common.web.model.PageInfo;
import com.cheche365.cheche.manage.common.web.model.PageViewModel;
import com.cheche365.cheche.admin.web.model.auto.AutoViewModel;
import com.cheche365.cheche.admin.web.model.user.UserViewModel;
import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.annotation.VisitorPermission;
import com.cheche365.cheche.core.model.Auto;
import com.cheche365.cheche.core.model.CompulsoryInsurance;
import com.cheche365.cheche.core.model.Insurance;
import com.cheche365.cheche.core.model.UserAuto;
import com.cheche365.cheche.core.service.CompulsoryInsuranceService;
import com.cheche365.cheche.core.service.InsuranceService;
import com.cheche365.cheche.core.service.UserAutoService;
import com.cheche365.cheche.manage.common.exception.FieldValidtorException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangfei on 2015/9/6.
 */
@RestController
@RequestMapping("/admin/auto")
public class AutoManagementController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private AutoManagementService autoManagementService;
    @Autowired
    private UserAutoService userAutoService;
    @Autowired
    private InsuranceService insuranceService;
    @Autowired
    private CompulsoryInsuranceService compulsoryInsuranceService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    @VisitorPermission("ad0201")
    public PageViewModel<AutoViewModel> findAll(@RequestParam(value = "currentPage", required = true) Integer currentPage,
                                                @RequestParam(value = "pageSize", required = true) Integer pageSize,
                                                @RequestParam(value = "keyword", required = false) String keyword,
                                                @RequestParam(value = "keyType", required = false) Integer keyType) {
        if (currentPage == null || currentPage < 1) {
            throw new FieldValidtorException("list auto info, currentPage can not be null or less than 1");
        }

        if (pageSize == null || pageSize < 1) {
            throw new FieldValidtorException("list auto info, pageSize can not be null or less than 1");
        }
        Page<Auto> autoPage = this.autoManagementService.listAuto(currentPage, pageSize, keyword, keyType);
        return createPageViewModel(autoPage);
    }

    /**
     * 获取车辆详情
     *
     * @param autoId
     * @return
     */
    @RequestMapping(value = "/{autoId}", method = RequestMethod.GET)
    @VisitorPermission("ad020101")
    public AutoViewModel findOne(@PathVariable Long autoId) {
        if (autoId == null || autoId < 1) {
            throw new FieldValidtorException("find auto detail, id can not be null or less than 1");
        }
        Auto auto = this.autoManagementService.findById(autoId);
        return createViewModel(auto, true);
    }

    /**
     * 封装展示层实体
     *
     * @param page 分页信息
     * @return PageViewModel<PageViewData>
     */
    public PageViewModel<AutoViewModel> createPageViewModel(Page page) {
        PageViewModel model = new PageViewModel<AutoViewModel>();

        PageInfo pageInfo = new PageInfo();
        pageInfo.setTotalElements(page.getTotalElements());
        pageInfo.setTotalPage(page.getTotalPages());
        model.setPageInfo(pageInfo);

        List<AutoViewModel> pageViewDataList = new ArrayList<>();
        for (Auto auto : (List<Auto>) page.getContent()) {
            pageViewDataList.add(createViewModel(auto, false));
        }
        model.setViewList(pageViewDataList);

        return model;
    }

    /**
     * 构建页面数据
     *
     * @param auto
     * @param isDetail 是否显示详情
     * @return PageViewData
     */
    private AutoViewModel createViewModel(Auto auto, boolean isDetail) {
        if (auto == null)
            return null;
        AutoViewModel viewModel = new AutoViewModel();
        viewModel.setId(auto.getId());
        viewModel.setLicensePlateNo(auto.getLicensePlateNo());//车牌号
        viewModel.setOwner(auto.getOwner());//车主
        /**
         * 用户信息
         */
        List<UserAuto> userAutoList = this.userAutoService.findByAuto(auto);
        if (userAutoList != null && userAutoList.size() > 0) {
            List<UserViewModel> userViewModelList = new ArrayList<>();
            List<Long> idList = new ArrayList<>();
            userAutoList.forEach(userAuto -> {
                if (userAuto != null && userAuto.getUser() != null
                        && StringUtils.isNotBlank(userAuto.getUser().getMobile())
                        && !idList.contains(userAuto.getUser().getId())) {
                    UserViewModel userViewModel = new UserViewModel();
                    userViewModel.setId(userAuto.getUser().getId());
                    userViewModel.setMobile(userAuto.getUser().getMobile());
                    userViewModelList.add(userViewModel);
                    idList.add(userViewModel.getId());
                }
            });
            viewModel.setUserViewModels(userViewModelList);
        }
        //设置详情页
        if (isDetail) {
            setDetail(auto, viewModel);
        }

        return viewModel;
    }

    private void setDetail(Auto auto, AutoViewModel viewModel) {
        viewModel.setIdentityType(auto.getIdentityType() == null ?
                "" : auto.getIdentityType().getName());//证件类型
        viewModel.setIdentity(auto.getIdentity());//证件号码
        viewModel.setVinNo(auto.getVinNo());//车架号
        viewModel.setEngineNo(auto.getEngineNo());//发动机号
        viewModel.setEnrollDate(auto.getEnrollDate() == null ?
                "" : DateUtils.getDateString(auto.getEnrollDate(), DateUtils.DATE_SHORTDATE_PATTERN));//初登日期
        if (auto.getAutoType() != null) {
            viewModel.setModel(StringUtils.trimToEmpty(auto.getAutoType().getModel()));//车型
            viewModel.setBrandCode(
                    StringUtils.trimToEmpty(auto.getAutoType().getBrand())
                            + StringUtils.trimToEmpty(auto.getAutoType().getCode()));//品牌型号
        }
        //商业险
        Insurance insurance = insuranceService.findByAuto(auto);
        if (insurance != null) {
            viewModel.setExpireDate(insurance.getExpireDate() == null ?
                    "" : DateUtils.getDateString(insurance.getExpireDate(),
                    DateUtils.DATE_SHORTDATE_PATTERN));//保险到期日
        } else {
            //交强险
            CompulsoryInsurance compulsoryInsurance = compulsoryInsuranceService.findByAuto(auto);
            if (compulsoryInsurance != null) {
                viewModel.setExpireDate(compulsoryInsurance.getExpireDate() == null ?
                        "" : DateUtils.getDateString(compulsoryInsurance.getExpireDate(),
                        DateUtils.DATE_SHORTDATE_PATTERN));//保险到期日
            }
        }
    }

}
