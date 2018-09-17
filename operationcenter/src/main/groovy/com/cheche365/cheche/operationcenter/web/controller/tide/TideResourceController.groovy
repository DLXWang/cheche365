package com.cheche365.cheche.operationcenter.web.controller.tide

import com.cheche365.cheche.core.model.InsuranceCompany
import com.cheche365.cheche.core.model.tide.UploadFile
import com.cheche365.cheche.core.service.InstitutionService
import com.cheche365.cheche.core.service.ResourceService
import com.cheche365.cheche.manage.common.constants.TideConstants
import com.cheche365.cheche.manage.common.service.InternalUserManageService
import com.cheche365.cheche.operationcenter.service.channelRebate.ChannelRebateManageService
import com.cheche365.cheche.operationcenter.service.resource.AreaResource
import com.cheche365.cheche.operationcenter.service.tide.*
import com.cheche365.cheche.operationcenter.web.model.area.AreaViewData
import com.cheche365.cheche.operationcenter.web.model.tide.ContractViewModel
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/operationcenter/tide/resource")
class TideResourceController {

    final String CRIC_INSURANCE_COMPANY_LIST = 'cricInsuranceCompanyModels'

    @Autowired
    private TideContractManageService tideContractManageService
    @Autowired
    private TidePlatformManageService tidePlatformManageService
    @Autowired
    private TideBranchManageService tideBranchManageService
    @Autowired
    private TideInstitutionManageService tideInstitutionManageService
    @Autowired
    private TideContractSupportAreaManageService tideContractSupportAreaManageService
    @Autowired
    private TidePlatformInternalUserManageService tidePlatformInternalUserManageService
    @Autowired
    private InternalUserManageService internalUserManageService;
    @Autowired
    private ResourceService resourceService;
    @Autowired
    InstitutionService institutionService
    @Autowired
    AreaResource areaResource
    @Autowired
    UploadFileManageService uploadFileManageService
    @Autowired
    ChannelRebateManageService channelRebateManageService
    @Autowired
    TideContractHistoryManageService tideContractHistoryManageService
    @Autowired
    RedisTemplate redisTemplate

    @RequestMapping(value = "constants", method = RequestMethod.GET)
    Map constants() {
        return [
                INSURANCETYPE_MAP    : TideConstants.INSURANCETYPE_MAP,
                CARTYPE_MAP          : TideConstants.CARTYPE_MAP,
                REBATESTATUS_MAP     : TideConstants.REBATESTATUS_MAP,
                AUTOTAXRETURNTYPE_MAP: TideConstants.AUTOTAXRETURNTYPE_MAP,
                STATUS_MAP           : TideConstants.STATUS_MAP
        ]
    }

    /**
     * 根据当前登陆的内部用户获取该用户关联的平台机构(潮汐系统使用)
     * @return
     */
    @RequestMapping(value = "/platform", method = RequestMethod.GET)
    def getPlatform() {
        def platformList = tidePlatformInternalUserManageService.getByInternalUserId(internalUserManageService.getCurrentInternalUser().id)*.tidePlatform
        return platformList.collect() {
            [
                    id  : it.id,
                    name: it.name
            ]
        }
    }

    /**
     * 获取保险公司
     * @return
     */
    @RequestMapping(value = "/insuranceCompanys", method = RequestMethod.GET)
    def getInsuranceCompanys() {
        def length = redisTemplate.opsForList().size(CRIC_INSURANCE_COMPANY_LIST)
        if (length > 0) {
            return redisTemplate.opsForList().range(CRIC_INSURANCE_COMPANY_LIST, 0, length - 1)
        } else {
            def insuranceCompanyList = InsuranceCompany.cricCompanies()
            def insuranceModelList = insuranceCompanyList.collect() {
                [
                        id  : it.id,
                        name: it.name
                ]
            }
            redisTemplate.opsForList().rightPushAll(CRIC_INSURANCE_COMPANY_LIST, insuranceModelList)
            return insuranceModelList
        }
    }

    /**
     * 根据平台机构id获取对应的营业部
     * @return
     */
    @RequestMapping(value = "/branch", method = RequestMethod.GET)
    def getTideBranch(@RequestParam(required = false) Long platformId) {
        def tideBranchList = tideBranchManageService.getByPlatform(platformId)
        return tideBranchList.collect {
            [
                    "id"  : it.id,
                    "name": it.branchName
            ]
        }
    }

    /**
     * 根据营业部id获取对应的分支公司级保险公司
     *
     * @param keyWord
     * @return
     */
    @RequestMapping(value = "/tideInstitution", method = RequestMethod.GET)
    def institutionByKeyWord(@RequestParam(value = "branchId", required = false) Long branchId,
                             @RequestParam(value = "platformId", required = false) Long platformId) {
        def institutionList = tideInstitutionManageService.getByBranch(branchId, platformId)
        institutionList.collect() {
            [
                    id  : it.id,
                    name: it.institutionName
            ]
        }
    }

    @RequestMapping(value = "/contract/{contractName}")
    def getContractByName(@PathVariable(required = false) String contractName) {
        def contractList = tideContractManageService.getByName(contractName)
        contractList.collect {
            [
                    id  : it.id,
                    name: it.contractName
            ]
        }
    }


    @RequestMapping(value = "/contract")
    def getAllContract(@RequestParam(required = false) String contractName) {
        def contractList = tideContractManageService.getByName(contractName)
        contractList.collect {
            [
                    id           : it.id,
                    name         : it.contractName,
                    expireDate   : it.expireDate,
                    effectiveDate: it.effectiveDate
            ]
        }
    }

    /**
     * 获取所有的省和直辖市
     * @param contractId
     * @param areaName
     * @return
     */
    @RequestMapping(value = "/area", method = RequestMethod.GET)
    List<AreaViewData> getProvinces(
            @RequestParam(required = false) Long contractId,
            @RequestParam(required = false) String areaName) {
        ContractViewModel param = new ContractViewModel(id: contractId, areaName: areaName)
        def areaList = tideContractSupportAreaManageService.getPage(param).getContent()*.supportArea
        return areaResource.createAreaViewDataList(areaList.unique())
    }


    @RequestMapping(value = "/contract/{contractId}/file", method = RequestMethod.GET)
    def getContractFiles(@PathVariable Long contractId) {
        uploadFileManageService.getViewModelBySourceTypeAndSourceId(UploadFile.Enum.SOURCE_TYPE_TIDE_CONTRACT, contractId)
    }

    @RequestMapping(value = "/contract/{contractId}/area", method = RequestMethod.GET)
    def getContractAreas(@PathVariable Long contractId) {
        tideContractSupportAreaManageService.getContractAreaViewModel(contractId)
    }

    @RequestMapping(value = "/contract/{contractId}/history", method = RequestMethod.GET)
    def getContractHistories(@PathVariable Long contractId) {
        tideContractHistoryManageService.getViewModelByContractId(contractId, TideConstants.OPERATION_TYPE_RENEWAL)
    }

    /**
     * 获取所有的省和直辖市
     *
     * @return
     */
    @RequestMapping(value = "/provinces", method = RequestMethod.GET)
    public List<AreaViewData> getProvinces() {
        return areaResource.createAreaViewDataList(areaResource.getprovincesAndDirectCitys());
    }

    /**
     * 获取省下面的市
     *
     * @return
     */
    @RequestMapping(value = "/{province}/cities", method = RequestMethod.GET)
    public List<AreaViewData> getCityAreaListByProvinceId(@PathVariable String province) {
        return areaResource.createAreaViewDataList(channelRebateManageService.getCityAreaListByProvinceId(province));
    }
}
