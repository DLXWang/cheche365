package com.cheche365.cheche.operationcenter.web.controller.tide

import com.cheche365.cheche.core.model.ResultModel
import com.cheche365.cheche.manage.common.service.InternalUserManageService
import com.cheche365.cheche.manage.common.web.model.DataTablePageViewModel
import com.cheche365.cheche.operationcenter.service.tide.TideBranchManageService
import com.cheche365.cheche.operationcenter.service.tide.TideInstitutionManageService
import com.cheche365.cheche.operationcenter.web.model.tide.ContractViewModel
import com.cheche365.cheche.operationcenter.web.model.tide.InstitutionViewModel
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

/**
 * Created by yinJianBin on 2017/6/14.
 */
@RestController
@Slf4j
@RequestMapping("/operationcenter/tide/institution")
class TideInstitutionController {

    @Autowired
    private TideBranchManageService tideBranchManageService
    @Autowired
    private TideInstitutionManageService tideInstitutionManageService
    @Autowired
    private InternalUserManageService internalUserManageService;


    @RequestMapping(value = "", method = RequestMethod.GET)
    DataTablePageViewModel<ContractViewModel> list(InstitutionViewModel paramModel) {
        def page = tideInstitutionManageService.getPage(paramModel)
        def dataTablePageViewModel = new DataTablePageViewModel<>(
                iTotalRecords: page.totalElements,
                iTotalDisplayRecords: page.totalElements,
                draw: paramModel.draw,
                aaData: page.getContent().collect { InstitutionViewModel.buildViewData(it) }
        )
        return dataTablePageViewModel
    }


    @RequestMapping(value = "/add", method = RequestMethod.POST)
    ResultModel add(@RequestBody InstitutionViewModel viewModel) {
        def institutionList = tideInstitutionManageService.getByInstitutionNameAndBranchId(viewModel.institutionName, viewModel.branchId)
        if (institutionList) {
            return new ResultModel(false, "该保险公司(分支公司级)已经存在!")
        }
        tideInstitutionManageService.save(viewModel)
        return new ResultModel()
    }

}
