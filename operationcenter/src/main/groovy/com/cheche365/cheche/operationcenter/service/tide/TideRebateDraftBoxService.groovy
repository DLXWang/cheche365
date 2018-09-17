package com.cheche365.cheche.operationcenter.service.tide

import com.cheche365.cheche.core.model.Area
import com.cheche365.cheche.core.model.tide.TideContract
import com.cheche365.cheche.core.model.tide.TideContractRebate
import com.cheche365.cheche.core.model.tide.TideRebateDraftBox
import com.cheche365.cheche.core.repository.tide.TideRebateDraftBoxRepository
import com.cheche365.cheche.manage.common.service.InternalUserManageService
import com.cheche365.cheche.manage.common.annotation.TideLogAnnotation
import com.cheche365.cheche.operationcenter.web.model.tide.RebateDraftBoxViewModel
import com.cheche365.cheche.operationcenter.web.model.tide.RebateViewModel
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
@Slf4j
class TideRebateDraftBoxService {
    @Autowired
    private InternalUserManageService internalUserManageService
    @Autowired
    private TideRebateDraftBoxRepository draftBoxRepository

    def findByCurrentUser() {
        draftBoxRepository.findAllByStatusAndOperator(1, internalUserManageService.getCurrentInternalUser()).collect {
            new RebateDraftBoxViewModel(
                    id: it.id,
                    name: it.name,
                    createTime: it.createTime
            )
        }
    }

    def findById(Long id) {
        if (id) {
            draftBoxRepository.findOne(id).with {
                new RebateDraftBoxViewModel(
                        id: it.id,
                        name: it.name,
                        createTime: it.createTime,
                        contractRebateList: it.contractRebateList.collect { i ->
                            RebateViewModel.buildViewData(i)
                        }
                )
            }
        }
    }

    @TideLogAnnotation(table = "tide_contract_rebate", description = "新增点位草稿")
    def saveDraft(RebateDraftBoxViewModel model) {
        def draft = translateEntity(model)
        draftBoxRepository.save(draft).contractRebateList
    }

    @TideLogAnnotation(table = "tide_contract_rebate", description = "修改点位草稿")
    def updateDraft(RebateDraftBoxViewModel model) {
        saveDraft(model)
    }

    @TideLogAnnotation(table = "tide_contract_rebate", description = "发布点位草稿")
    def publicDraft(RebateDraftBoxViewModel model) {
        translateEntity(model).with {
            status = 0
            contractRebateList.each { i ->
                i.status = 1
                i.disable = i.tideContract.disable
            }
            draftBoxRepository.save(it).contractRebateList
        }
    }

    def removeDraft(Long id) {
        draftBoxRepository.delete(id)
    }

    private TideRebateDraftBox translateEntity(RebateDraftBoxViewModel model) {
        def draft
        if (model.id) {
            draft = draftBoxRepository.findOne(model.id)
        }

        if (!draft) {
            draft = new TideRebateDraftBox()
            draft.contractRebateList = []
        }

        draft.name = model.name
        draft.status = 1
        draft.operator = internalUserManageService.currentInternalUser
        draft.contractRebateList = model.contractRebateList.collect { it ->
            draft.contractRebateList
                    .find { i -> i.id == it.id }
                    .with { rebate ->
                if (!rebate) {
                    rebate = new TideContractRebate()
                }
                rebate.contractRebateCode = it.contractRebateCode
                rebate.tideContract = new TideContract(id: it.contractId)
                rebate.supportArea = new Area(id: it.supportAreaId)
                rebate.insuranceType = it.insuranceType
                rebate.carType = it.carType
                rebate.chooseCondition = it.chooseCondition
                rebate.originalCommecialRate = it.originalCommecialRate
                rebate.originalCompulsoryRate = it.originalCompulsoryRate
                rebate.autoTaxReturnType = it.autoTaxReturnType
                rebate.autoTaxReturnValue = it.autoTaxReturnValue
                rebate.marketCommercialRate = it.marketCommercialRate
                rebate.marketCompulsoryRate = it.marketCompulsoryRate
                rebate.marketAutoTaxReturnType = it.marketAutoTaxReturnType
                rebate.marketAutoTaxReturnValue = it.marketAutoTaxReturnValue
                rebate.effectiveDate = it.effectiveDate
                rebate.expireDate = it.expireDate
                rebate.status = 2
                rebate.operator = internalUserManageService.currentInternalUser

                return rebate
            }
        }
        return draft
    }
}
