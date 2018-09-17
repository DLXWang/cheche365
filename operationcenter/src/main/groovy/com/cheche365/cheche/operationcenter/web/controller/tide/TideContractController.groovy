package com.cheche365.cheche.operationcenter.web.controller.tide

import com.cheche365.cheche.core.model.ResultModel
import com.cheche365.cheche.core.service.InstitutionService
import com.cheche365.cheche.core.service.ResourceService
import com.cheche365.cheche.core.util.CacheUtil
import com.cheche365.cheche.core.util.FileUtil
import com.cheche365.cheche.manage.common.service.InternalUserManageService
import com.cheche365.cheche.manage.common.util.AssertUtil
import com.cheche365.cheche.manage.common.util.ResponseOutUtil
import com.cheche365.cheche.manage.common.web.model.DataTablePageViewModel
import com.cheche365.cheche.operationcenter.service.resource.AreaResource
import com.cheche365.cheche.operationcenter.service.tide.*
import com.cheche365.cheche.operationcenter.web.model.tide.ContractHistoryViewModel
import com.cheche365.cheche.operationcenter.web.model.tide.ContractViewModel
import groovy.util.logging.Slf4j
import org.apache.commons.lang.RandomStringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

import javax.servlet.http.HttpServletResponse
import java.text.SimpleDateFormat

import static com.cheche365.cheche.core.model.tide.UploadFile.Enum.*

/**
 * Created by yinJianBin on 2017/6/14.
 */
@RestController
@Slf4j
@RequestMapping("/operationcenter/tide/contract")
class TideContractController {

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
    private InternalUserManageService internalUserManageService;
    @Autowired
    private ResourceService resourceService;
    @Autowired
    InstitutionService institutionService
    @Autowired
    AreaResource areaResource
    @Autowired
    UploadFileManageService uploadFileManageService


    @RequestMapping(value = "", method = RequestMethod.GET)
    DataTablePageViewModel<ContractViewModel> list(ContractViewModel paramModel) {
        def page = tideContractManageService.getPage(paramModel)
        def dataTablePageViewModel = new DataTablePageViewModel<>(
                iTotalRecords: page.totalElements,
                iTotalDisplayRecords: page.totalElements,
                draw: paramModel.draw,
                aaData: page.getContent().collect { ContractViewModel.buildViewData(it) }
        )
        return dataTablePageViewModel
    }


    @RequestMapping(value = "/add", method = RequestMethod.POST)
    ResultModel add(@RequestBody ContractViewModel viewModel) {
        def contractSize = tideContractManageService.countByContractCodeExact(viewModel.contractCode)
        if (contractSize > 0) {
            return new ResultModel(false, "该合约编号已经存在!")
        }
        tideContractManageService.save(viewModel)
        return new ResultModel()
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    ResultModel update(@RequestBody ContractViewModel viewModel) {
        tideContractManageService.update(viewModel)
        return new ResultModel()
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    void upload(@RequestParam(value = "codeFile", required = false) MultipartFile file,
                @RequestParam(value = "contractId", required = false) Long contractId,
                HttpServletResponse response) {
        AssertUtil.notNull(file, "文件不可为空")
        String originalFileName = file.getOriginalFilename()
        String basePath = resourceService.getResourceAbsolutePath(resourceService.getProperties().getTideContractPath())
        basePath = basePath + new SimpleDateFormat("yyyyMM").format(new Date()) + File.separator + RandomStringUtils.randomAlphanumeric(4)
        if (!new File(basePath).exists()) {
            if (!new File(basePath).mkdirs()) throw new RuntimeException("创建存储文件目录失败")
        }
        def newFilePath = basePath + File.separator + originalFileName
        FileUtil.writeFile(newFilePath, file.getBytes())
        def status = contractId ? STATUS_ACTIVE : STATUS_DISABLE
        def uploadFile = uploadFileManageService.save(newFilePath, originalFileName, SOURCE_TYPE_TIDE_CONTRACT, contractId, status, internalUserManageService.getCurrentInternalUser())
        def result = [
                id      : uploadFile.id,
                fileName: uploadFile.fileName
        ]
        ResponseOutUtil.outPrint(response, CacheUtil.doJacksonSerialize(result))
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    ContractViewModel getContractInfo(@PathVariable Long id) {
        ContractViewModel viewModel = tideContractManageService.getContractInfo(id)
        viewModel
    }

    @RequestMapping(value = "/{contractId}/{disable}", method = RequestMethod.POST)
    ResultModel updateContractStatus(@PathVariable Long contractId, @PathVariable Boolean disable) {
        tideContractManageService.updateDisable(contractId, disable)
        return new ResultModel()
    }


    @RequestMapping(value = "/contractArea/{contractAreaId}/{disable}", method = RequestMethod.POST)
    ResultModel updateContractAreaStatus(@PathVariable Long contractAreaId, @PathVariable Boolean disable) {
        tideContractSupportAreaManageService.updateDisable(contractAreaId, disable)
        return new ResultModel()
    }

    @RequestMapping(value = "/contractArea/add", method = RequestMethod.POST)
    def addContractArea(@RequestBody ContractViewModel viewModel) {
        def resultMap = [:]
        def cityIds = viewModel.cityIds
        def contract = tideContractManageService.getById(viewModel.id)
        for (Long cityId : cityIds) {
            def contractArea = tideContractSupportAreaManageService.getByContractAndArea(contract.id, cityId)
            if (contractArea) {
                resultMap[false] = "该合约已经关联投保地区(" + contractArea.supportArea.name + ")!"
                return resultMap
            }
        }
        tideContractSupportAreaManageService.saveSupportArea(contract, cityIds, internalUserManageService.getCurrentInternalUser())
        def viewModelList = tideContractSupportAreaManageService.getContractAreaViewModel(contract.id)
        resultMap[true] = viewModelList
        return resultMap
    }

    @RequestMapping(value = "/file/del/{fileId}", method = RequestMethod.POST)
    def delContractFile(@PathVariable Long fileId) {
        uploadFileManageService.deleteFile(fileId)
        new ResultModel()
    }

    @RequestMapping(value = "/renewal", method = RequestMethod.POST)
    def renewal(@RequestBody ContractHistoryViewModel model) {
        tideContractManageService.renewal(model)
        return new ResultModel()
    }

}
