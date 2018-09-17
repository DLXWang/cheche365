package com.cheche365.cheche.operationcenter.service.tide

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.Area
import com.cheche365.cheche.core.model.tide.TideContract
import com.cheche365.cheche.core.model.tide.TideContractRebate
import com.cheche365.cheche.core.repository.tide.TideContractRebateRepository
import com.cheche365.cheche.core.repository.tide.TideContractRepository
import com.cheche365.cheche.manage.common.annotation.TideLogAnnotation
import com.cheche365.cheche.manage.common.constants.TideConstants
import com.cheche365.cheche.manage.common.service.BaseService
import com.cheche365.cheche.manage.common.service.InternalUserManageService
import com.cheche365.cheche.operationcenter.web.model.tide.RebateViewModel
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service

import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root

@Service
@Slf4j
class TideRebateManageService extends BaseService {
    @Autowired
    private InternalUserManageService internalUserManageService
    @Autowired
    private TideContractRebateRepository rebateRepository
    @Autowired
    private TidePreconditionSpecification preconditionSpecification
    @Autowired
    private TideRebateHistoryManageService rebateHistoryManageService
    @Autowired
    private TideContractRepository contractRepository

    Page<TideContractRebate> getRebateByPage(RebateViewModel paramModel) {
        Pageable pageable = new PageRequest(paramModel.currentPage - 1, paramModel.pageSize, new Sort(Sort.Direction.DESC, ["updateTime", "id"]))

        return rebateRepository.findAll(new Specification<TideContractRebate>() {
            @Override
            Predicate toPredicate(Root<TideContractRebate> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                def predicate = cb.conjunction()
                def expressions = predicate.getExpressions()

                expressions << preconditionSpecification.cratePlatformFilter(cb, root.get('tideContract').get('tideBranch').get('tidePlatform').get('id'))

                expressions << cb.notEqual(root.get('status'), TideConstants.STATUS_DRAFT)

                if (paramModel.contractId) {
                    expressions << cb.equal(root.get('tideContract').get('id'), paramModel.contractId)
                }
                if (paramModel.supportAreaId) {
                    expressions << cb.equal(root.get('supportArea').get('id'), paramModel.supportAreaId)
                }
                if (paramModel.status != null) {
                    expressions << cb.equal(root.get('status'), paramModel.status)
                }
                if (paramModel.insuranceType) {
                    expressions << cb.equal(root.get('insuranceType'), paramModel.insuranceType as Long)
                }
                if (paramModel.carType) {
                    expressions << cb.equal(root.get('carType'), paramModel.carType as Long)
                }

                return predicate
            }
        }, pageable)
    }

    @TideLogAnnotation(table = "tide_contract_rebate", description = "新增点位信息")
    def add(RebateViewModel model) {
        return add([model])
    }

    @TideLogAnnotation(table = "tide_contract_rebate", description = "新增点位信息")
    def add(List<RebateViewModel> list) {
        List<TideContractRebate> rerbateList = list.collect {
            def contract = contractRepository.findOne(it.contractId)
            def now = new Date()
            def staus = TideConstants.STATUS_CREATE

            if (it.expireDate.format("yyyyMMdd") < now.format("yyyyMMdd")) {
                staus = TideConstants.STATUS_EXPIRED
            } else if (it.effectiveDate.format("yyyyMMdd") <= now.format("yyyyMMdd")) {
                staus = TideConstants.STATUS_EFFECTIVE_ING
            }
            new TideContractRebate(
                contractRebateCode: it.contractRebateCode,
                tideContract: new TideContract(id: it.contractId),
                supportArea: new Area(id: it.supportAreaId),
                insuranceType: it.insuranceType,
                carType: it.carType,
                chooseCondition: it.chooseCondition,
                originalCommecialRate: it.originalCommecialRate,
                originalCompulsoryRate: it.originalCompulsoryRate,
                autoTaxReturnType: it.autoTaxReturnType,
                autoTaxReturnValue: it.autoTaxReturnValue,
                marketCommercialRate: it.marketCommercialRate,
                marketCompulsoryRate: it.marketCompulsoryRate,
                marketAutoTaxReturnType: it.marketAutoTaxReturnType,
                marketAutoTaxReturnValue: it.marketAutoTaxReturnValue,
                effectiveDate: it.effectiveDate,
                expireDate: it.expireDate,
                status: staus,
                disable: contract.disable,
                operator: internalUserManageService.getCurrentInternalUser(),
                updateTime: now
            )
        }

        rebateRepository.save(rerbateList)
    }

    @TideLogAnnotation(table = "tide_contract_rebate", description = "修改点位状态")
    def changeDisable(Long id, Boolean disable) {
        def rebate = rebateRepository.findOne(id)
        rebate.disable = disable
        rebate.operator = internalUserManageService.getCurrentInternalUser()
        rebateRepository.save(rebate)
    }

    @TideLogAnnotation(table = "tide_contract_rebate", description = "修改点位信息")
    def update(RebateViewModel model) {
        def rebate = rebateRepository.findOne(model.id)

        if (rebate.version != model.version) {
            throw new BusinessException(BusinessException.Code.ORDER_STATUS_ERROR, "原点位信息已更改，请重新提交")
        }

        if (rebate.isChange(model)) {
            rebateHistoryManageService.add(rebate)

            rebate.effectiveDate = model.effectiveDate
            rebate.expireDate = model.expireDate
            rebate.originalCommecialRate = model.originalCommecialRate
            rebate.originalCompulsoryRate = model.originalCompulsoryRate
            rebate.autoTaxReturnType = model.autoTaxReturnType
            rebate.autoTaxReturnValue = model.autoTaxReturnValue
            rebate.marketCommercialRate = model.marketCommercialRate
            rebate.marketCompulsoryRate = model.marketCompulsoryRate
            rebate.marketAutoTaxReturnType = model.marketAutoTaxReturnType
            rebate.marketAutoTaxReturnValue = model.marketAutoTaxReturnValue
            rebate.status = rebate.tideContract.status

            rebate.operator = internalUserManageService.getCurrentInternalUser()

            rebateRepository.save(rebate)
        }
    }

    @TideLogAnnotation(table = "tide_contract_rebate", description = "修改合约状态,批量修改点位状态")
    def updateDisableByContract(TideContract tideContract, operator) {
        def rebateList = rebateRepository.findAllByTideContractId(tideContract.id)
        rebateList.collect() { rebate ->
            rebate.disable = tideContract.disable
            rebate.operator = operator
            rebateRepository.save(rebate)
        }
    }

    def updateChangeStatus() {
        def rebateList = getRebateByPage(new RebateViewModel(
            pageSize: 1000,
            currentPage: 1
        )).getContent()
        rebateList*.setUpdateTime(new Date())
        rebateRepository.save(rebateList)
    }
}
