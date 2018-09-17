package com.cheche365.cheche.operationcenter.service.tide

import com.cheche365.cheche.core.model.InsuranceCompany
import com.cheche365.cheche.core.model.InternalUser
import com.cheche365.cheche.core.model.tide.TideBranch
import com.cheche365.cheche.core.model.tide.TideContract
import com.cheche365.cheche.core.model.tide.TideInstitution
import com.cheche365.cheche.core.model.tide.UploadFile
import com.cheche365.cheche.core.repository.AreaRepository
import com.cheche365.cheche.core.repository.tide.*
import com.cheche365.cheche.manage.common.constants.TideConstants
import com.cheche365.cheche.manage.common.service.BaseService
import com.cheche365.cheche.manage.common.service.InternalUserManageService
import com.cheche365.cheche.manage.common.service.TideLogAspectService
import com.cheche365.cheche.operationcenter.web.model.tide.ContractHistoryViewModel
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

import static com.cheche365.cheche.manage.common.constants.TideConstants.OPERATION_TYPE_UPDATE
/**
 * Created by yinJianBin on 2018/4/19.
 */
@Service
@Slf4j
class TideContractManageService extends BaseService {

    @Autowired
    TideContractSupportAreaManageService tideContractSupportAreaManageService
    @Autowired
    TideContractRepository tideContractRepository
    @Autowired
    TideContractSupportAreaRepository tideContractSupportAreaRepository
    @Autowired
    TidePlatformInternalUserRepository tidePlatformInternalUserRepository
    @Autowired
    TideBranchRepository tideBranchRepository
    @Autowired
    InternalUserManageService internalUserManageService
    @Autowired
    UploadFileRepository uploadFileRepository
    @Autowired
    AreaRepository areaRepository
    @Autowired
    TidePreconditionSpecification preconditionSpecification
    @Autowired
    UploadFileManageService uploadFileManageService
    @Autowired
    TideLogAspectService tideLogAspectService
    @Autowired
    TideContractHistoryManageService tideContractHistoryManageService

    Page<TideContract> getPage(ContractViewModel paramModel) {
        Pageable pageable = buildPageable(paramModel.currentPage, paramModel.pageSize, Sort.Direction.DESC, "updateTime")
        return tideContractRepository.findAll(new Specification<TideContract>() {
            @Override
            Predicate toPredicate(Root<TideContract> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                def predicateList = []
                if (paramModel.platformId) {
                    predicateList << cb.equal(root.get('tideBranch').get('tidePlatform').get('id'), paramModel.platformId)
                } else {
                    predicateList << preconditionSpecification.cratePlatformFilter(cb, root.get('tideBranch').get('tidePlatform').get('id'))
                }
                if (paramModel.branchId) {
                    predicateList << cb.equal(root.get('tideBranch').get('id'), paramModel.branchId)
                }
                if (paramModel.institutionId) {
                    predicateList << cb.equal(root.get('tideInstitution').get('id'), paramModel.institutionId as Long)
                }
                if (paramModel.contractName) {
                    predicateList << cb.like(root.get('contractName'), "%$paramModel.contractName%")
                }
                Predicate[] predicates = new Predicate[predicateList.size()];
                predicates = predicateList.toArray(predicates)
                return query.where(predicates).getRestriction()
            }
        }, pageable)
    }

    @Transactional
    def save(ContractViewModel viewModel) {
        def tideContract = new TideContract()
        tideContract.setTideBranch(new TideBranch(id: viewModel.branchId as Long))
        tideContract.setTideInstitution(new TideInstitution(id: viewModel.institutionId as Long))
        tideContract.setInsuranceCompany(new InsuranceCompany(id: viewModel.insuranceCompanyId as Long))
        tideContract.setContractName(viewModel.contractName)
        tideContract.setContractCode(viewModel.contractCode)
        tideContract.setPartnerUserName(viewModel.partnerUserName)
        tideContract.setPartnerPassword(viewModel.partnerPassword)
        tideContract.setLoginUrl(viewModel.loginUrl)
        tideContract.setOrderCode(viewModel.orderCode)
        def operator = internalUserManageService.getCurrentInternalUser()
        tideContract.setOperator(operator)
        tideContract.setDescription(viewModel.description)
        tideContract.setUpdateTime(new Date())
        tideContractRepository.save(tideContract)
        //保存关联文件
        saveFiles(tideContract, viewModel.fileIds, operator)
        //保存关联的投保地区
        tideContractSupportAreaManageService.saveSupportArea(tideContract, viewModel.cityIds, operator)

    }

    @Transactional
    def update(ContractViewModel viewModel) {
        def operator = internalUserManageService.getCurrentInternalUser()
        def tideContract = tideContractRepository.findOne(viewModel.id)
        if (viewModel.description) {
            updateDescription(tideContract, viewModel, operator)
        } else {
            updateCompanyInfo(tideContract, viewModel, operator)
        }
    }

    def updateDescription(TideContract tideContract, ContractViewModel viewModel, InternalUser operator) {
        tideContract.setDescription(viewModel.description)
        tideContract.setOperator(operator)
        tideContractRepository.save(tideContract)
    }

    def updateCompanyInfo(TideContract tideContract, ContractViewModel viewModel, InternalUser operator) {
        viewModel.partnerUserName && tideContract.setPartnerUserName(viewModel.partnerUserName)
        viewModel.partnerPassword && tideContract.setPartnerPassword(viewModel.partnerPassword)
        viewModel.loginUrl && tideContract.setLoginUrl(viewModel.loginUrl)
        viewModel.orderCode && tideContract.setOrderCode(viewModel.orderCode)
        tideContract.setOperator(operator)
        tideContractRepository.save(tideContract)
        //保存修改历史
        tideContractHistoryManageService.add(tideContract, operator, OPERATION_TYPE_UPDATE)
        //保存操作日志
        tideLogAspectService.saveLog("修改保险公司承保系统登陆使用信息", viewModel.toString(), 'contract_insurance_company', tideContract.id, operator.id)
    }

    def saveFiles(TideContract tideContract, fileIds, operator) {
        fileIds.each { fileId ->
            def uploadFile = uploadFileRepository.findOne(fileId as Long)
            uploadFile.setSourceId(tideContract.id)
            uploadFile.setStatus(TideConstants.STATUS_EFFECTIVE_ING)
            uploadFile.setOperator(operator)
            uploadFileRepository.save(uploadFile)
        }
    }

    @Transactional
    def updateDisable(Long contractId, Boolean disable) {
        def contract = tideContractRepository.findOne(contractId)
        def operator = internalUserManageService.getCurrentInternalUser()
        contract.setDisable(disable)
        contract.setOperator(operator)
        tideContractRepository.save(contract)
        //关联更新所有投保地区
        tideContractSupportAreaManageService.updateDisableByContract(contract, operator)
        // 关联更新所有点位
        tideRebateManageService.updateDisableByContract(contract, operator)
        //记录操作日志
        tideLogAspectService.saveLog("${disable ? '禁用' : '启用'}合约", '', 'contract_disable', contract.id, operator.id)
    }
    @Autowired
    TideRebateManageService tideRebateManageService

    def countByContractCodeExact(String contractCode) {
        tideContractRepository.countAllByContractCode(contractCode)
    }

    def getByName(String contractName) {
        getPage(new ContractViewModel(
                contractName: contractName,
                currentPage: 1,
                pageSize: 1000
        )).content
    }

    def getById(Long contractId) {
        tideContractRepository.findOne(contractId)
    }

    ContractViewModel getContractInfo(Long contractId) {
        def contract = tideContractRepository.findOne(contractId)
        def viewModel = ContractViewModel.buildViewData(contract)
        def areaViewModel = tideContractSupportAreaManageService.getContractAreaViewModel(contractId)
        viewModel.setAreaViewModel(areaViewModel)
        def fileViewModel = uploadFileManageService.getViewModelBySourceTypeAndSourceId(UploadFile.Enum.SOURCE_TYPE_TIDE_CONTRACT, contractId)
        viewModel.setFileViewModel(fileViewModel)
        def historyViewModel = tideContractHistoryManageService.getViewModelByContractId(contractId, TideConstants.OPERATION_TYPE_RENEWAL)
        viewModel.setHistoryViewModel(historyViewModel)
        viewModel
    }

    def renewal(ContractHistoryViewModel viewModel) {
        def contract = tideContractRepository.findOne(viewModel.contractId)
        def operator = internalUserManageService.getCurrentInternalUser()
        tideContractHistoryManageService.renewal(contract, viewModel.effectiveDate, viewModel.expireDate, operator)
        tideLogAspectService.saveLog("新增续约记录", viewModel.toString(), 'contract_effect', viewModel.contractId, operator.id)
    }
}
