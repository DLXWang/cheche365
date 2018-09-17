package com.cheche365.cheche.operationcenter.service.tide

import com.cheche365.cheche.core.model.InternalUser
import com.cheche365.cheche.core.model.tide.TideContract
import com.cheche365.cheche.core.model.tide.TideContractSupportArea
import com.cheche365.cheche.core.repository.AreaRepository
import com.cheche365.cheche.core.repository.tide.TideContractSupportAreaRepository
import com.cheche365.cheche.manage.common.service.BaseService
import com.cheche365.cheche.manage.common.service.InternalUserManageService
import com.cheche365.cheche.manage.common.service.TideLogAspectService
import com.cheche365.cheche.operationcenter.web.model.tide.ContractAreaViewModel
import com.cheche365.cheche.operationcenter.web.model.tide.ContractViewModel
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root

/**
 * Created by yinJianBin on 2018/4/19.
 */
@Service
@Slf4j
class TideContractSupportAreaManageService extends BaseService {

    @Autowired
    TideContractSupportAreaRepository tideContractSupportAreaRepository
    @Autowired
    InternalUserManageService internalUserManageService
    @Autowired
    TidePreconditionSpecification preconditionSpecification
    @Autowired
    AreaRepository areaRepository
    @Autowired
    TideLogAspectService tideLogAspectService

    Page<TideContractSupportArea> getPage(ContractViewModel paramModel) {
        Pageable pageable = buildPageable(paramModel.currentPage ?: 1, paramModel.pageSize ?: 1000, Sort.Direction.DESC, "id")
        return tideContractSupportAreaRepository.findAll(new Specification<TideContractSupportArea>() {
            @Override
            Predicate toPredicate(Root<TideContractSupportArea> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                def predicateList = []

                predicateList << preconditionSpecification.cratePlatformFilter(cb, root.get('tideContract').get('tideBranch').get('tidePlatform').get('id'))

                if (paramModel.id) {
                    predicateList << cb.equal(root.get("tideContract").get('id'), paramModel.id)
                }
                if (paramModel.areaName) {
                    predicateList << cb.like(root.get('supportArea').get('name'), "$paramModel.areaName%")
                }

                Predicate[] predicates = new Predicate[predicateList.size()]
                predicates = predicateList.toArray(predicates)
                return query.where(predicates).getRestriction()
            }
        }, pageable)
    }

    def getByContractAndArea(Long contractId, Long areaId) {
        tideContractSupportAreaRepository.findByTideContractIdAndSupportAreaId(contractId, areaId)
    }

    @Transactional
    def save(def tideContractSupportArea) {
        tideContractSupportAreaRepository.save(tideContractSupportArea)
    }

    def saveSupportArea(TideContract tideContract, cityIds, operator) {
        def instanceNo = new StringBuilder("增加支持投保的城市:")
        cityIds.each { areaId ->
            TideContractSupportArea contractSupportArea = new TideContractSupportArea()
            contractSupportArea.setTideContract(tideContract)
            def area = areaRepository.findOne(areaId as Long)
            instanceNo.append("$area.name  ")
            contractSupportArea.setSupportArea(area)
            contractSupportArea.setDisable(tideContract.disable)
            tideContractSupportAreaRepository.save(contractSupportArea)
        }
        tideLogAspectService.saveLog(instanceNo, '增加支持投保地区的城市', 'tide_support_area', tideContract.id, operator.id)
    }


    def getContractAreaViewModel(Long contractId) {
        def contractAreaList = tideContractSupportAreaRepository.findAllByTideContractId(contractId)
        def areaViewModel = contractAreaList.collect() {
            ContractAreaViewModel.buildViewData(it)
        }
        areaViewModel
    }

    def updateDisable(Long contractAreaId, Boolean disable) {
        def operation = !disable ? "启用" : "禁用"
        def contractArea = tideContractSupportAreaRepository.findOne(contractAreaId)
        updateDisable(contractArea, disable)
        def instanceNo = new StringBuilder("${operation}支持投保的地区: " + contractArea.supportArea.name)
        def operator = internalUserManageService.getCurrentInternalUser()
        tideLogAspectService.saveLog(instanceNo, '修改支持投保的地区', 'tide_support_area', contractArea.tideContract.id, operator.id)
    }

    @Transactional
    def updateDisableByContract(TideContract contract, InternalUser operator) {
        def disable = contract.disable
        def operation = !disable ? "启用" : "禁用"
        def instanceNo = new StringBuilder("批量${operation}支持投保的地区: ")
        def contractAreaList = tideContractSupportAreaRepository.findAllByTideContractId(contract.id)
        contractAreaList.each { contractArea ->
            updateDisable(contractArea, disable)
            instanceNo.append("${contractArea.supportArea.name} ")
        }
        tideLogAspectService.saveLog(instanceNo, '批量修改支持投保的地区', 'tide_support_area', contract.id, operator.id)
    }


    def updateDisable(TideContractSupportArea contractArea, Boolean disable) {
        contractArea.setDisable(disable)
        tideContractSupportAreaRepository.save(contractArea)
    }

}
