package com.cheche365.cheche.operationcenter.web.controller.tide

import com.cheche365.cheche.core.model.ResultModel
import com.cheche365.cheche.operationcenter.service.tide.TideRebateDraftBoxService
import com.cheche365.cheche.operationcenter.web.model.tide.RebateDraftBoxViewModel
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@Slf4j
@RequestMapping("/operationcenter/tide/rebate/draft")
class TideRebateDraftController {
    @Autowired
    private TideRebateDraftBoxService draftBoxService

    @RequestMapping(value = "add", method = RequestMethod.POST)
    ResultModel addDraft(@RequestBody RebateDraftBoxViewModel draftBoxViewModel) {
        draftBoxService.saveDraft(draftBoxViewModel)
        new ResultModel()
    }

    @RequestMapping(value = "update", method = RequestMethod.POST)
    ResultModel updateDraft(@RequestBody RebateDraftBoxViewModel draftBoxViewModel) {
        draftBoxService.updateDraft(draftBoxViewModel)
        new ResultModel()
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    List<RebateDraftBoxViewModel> getDrafts() {
        return draftBoxService.findByCurrentUser()
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    RebateDraftBoxViewModel getDraft(@PathVariable Long id) {
        return draftBoxService.findById(id)
    }

    @RequestMapping(value = "public", method = RequestMethod.POST)
    ResultModel publicDraft(@RequestBody RebateDraftBoxViewModel draftBoxViewModel) {
        draftBoxService.publicDraft(draftBoxViewModel)
        new ResultModel()
    }

    @RequestMapping(value = "remove", method = RequestMethod.GET)
    ResultModel removeDraft(@RequestParam Long id) {
        draftBoxService.removeDraft(id)
        new ResultModel()
    }
}
