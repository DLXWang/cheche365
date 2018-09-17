package com.cheche365.cheche.operationcenter.web.controller.tide

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.ResultModel
import com.cheche365.cheche.manage.common.model.PublicQuery
import com.cheche365.cheche.manage.common.web.model.DataTablePageViewModel
import com.cheche365.cheche.operationcenter.service.tide.TideRebateHistoryManageService
import com.cheche365.cheche.operationcenter.service.tide.TideRebateManageService
import com.cheche365.cheche.operationcenter.service.tide.TideRebateRecordService
import com.cheche365.cheche.operationcenter.web.model.tide.RebateHistoryViewModel
import com.cheche365.cheche.operationcenter.web.model.tide.RebateViewModel
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@Slf4j
@RequestMapping("/operationcenter/tide/rebate")
class TideRebateController {
    @Autowired
    private TideRebateManageService rebateManageService
    @Autowired
    private TideRebateHistoryManageService rebateHistoryManageService
    @Autowired
    private TideRebateRecordService rebateRecordService

    @RequestMapping(value = "", method = RequestMethod.GET)
    DataTablePageViewModel list(RebateViewModel paramModel) {
        def page = rebateManageService.getRebateByPage(paramModel)

        new DataTablePageViewModel(
                iTotalRecords: page.totalElements,
                iTotalDisplayRecords: page.totalElements,
                draw: paramModel.draw,
                aaData: page.content.collect { RebateViewModel.buildViewData(it) }
        )
    }

    @RequestMapping(value = "batchAdd", method = RequestMethod.POST)
    ResultModel batchSave(@RequestBody List<RebateViewModel> list) {
        rebateManageService.add(list)
        new ResultModel()
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    ResultModel save(@RequestBody RebateViewModel model) {
        rebateManageService.add(model)
        new ResultModel()
    }

    @RequestMapping(value = "disable", method = RequestMethod.GET)
    ResultModel changeDisable(@RequestParam("id") Long id, @RequestParam("disable") Boolean disable) {
        rebateManageService.changeDisable(id, disable)
        new ResultModel()
    }

    @RequestMapping(value = "update", method = RequestMethod.POST)
    ResultModel update(@RequestBody RebateViewModel model) {
        try {
            rebateManageService.update(model)
        } catch (BusinessException ex) {
            return new ResultModel(false, ex.message)
        }

        new ResultModel()
    }

    @RequestMapping(value = "history", method = RequestMethod.GET)
    DataTablePageViewModel history(PublicQuery query) {
        def page = rebateHistoryManageService.getRebateHistoryByPage(query)

        new DataTablePageViewModel(
                iTotalRecords: page.totalElements,
                iTotalDisplayRecords: page.totalElements,
                draw: query.draw,
                aaData: page.content.collect {
                    RebateHistoryViewModel.buildViewData(it)
                }
        )
    }

    @RequestMapping(value = "record", method = RequestMethod.GET)
    DataTablePageViewModel listRecord(RebateViewModel paramModel) {
        def page = rebateRecordService.getRebateByPage(paramModel)

        new DataTablePageViewModel(
            iTotalRecords: page.totalElements,
            iTotalDisplayRecords: page.totalElements,
            draw: paramModel.draw,
            aaData: page.content.collect { RebateViewModel.buildViewData(it) }
        )
    }

    @RequestMapping(value = "updateChangeStatus", method = RequestMethod.POST)
    ResultModel updateChangeStatus() {
        rebateManageService.updateChangeStatus()
        new ResultModel()
    }
}
