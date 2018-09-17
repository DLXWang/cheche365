package com.cheche365.cheche.operationcenter.web.controller.tide

import com.cheche365.cheche.manage.common.model.PublicQuery
import com.cheche365.cheche.manage.common.web.model.DataTablePageViewModel
import com.cheche365.cheche.operationcenter.service.tide.TideLogService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@Slf4j
@RequestMapping("/operationcenter/tide/log")
class TideLogController {
    @Autowired
    private TideLogService logService

    @RequestMapping(value = "/{sourceTable}", method = RequestMethod.GET)
    DataTablePageViewModel log(@PathVariable String sourceTable, PublicQuery query) {
        def page = logService.getLogByPage(query,sourceTable)

        new DataTablePageViewModel(
                iTotalRecords: page.totalElements,
                iTotalDisplayRecords: page.totalElements,
                draw: query.draw,
                aaData: page.content.collect {
                    logService.tranformLogViewModel(it)
                }
        )
    }
}
