package com.cheche365.cheche.ordercenter.web.controller.wallet

import com.cheche365.cheche.common.util.DateUtils
import com.cheche365.cheche.core.annotation.VisitorPermission
import com.cheche365.cheche.core.service.ResourceService
import com.cheche365.cheche.manage.common.exception.FileUploadException
import com.cheche365.cheche.manage.common.model.PublicQuery
import com.cheche365.cheche.manage.common.model.WalletRemitUploadHistory
import com.cheche365.cheche.manage.common.util.ResponseOutUtil
import com.cheche365.cheche.manage.common.web.model.DataTablePageViewModel
import com.cheche365.cheche.ordercenter.service.wallet.WalletRemitManageService
import com.cheche365.cheche.ordercenter.web.model.wallet.WalletUploadHistoryModel
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

import javax.servlet.http.HttpServletResponse

import static com.cheche365.cheche.common.util.DateUtils.getDateString

/**
 * Created by yinJianBin on 2018/4/5.
 */
@RestController
@RequestMapping("/orderCenter/walletRemit")
@Slf4j
class WalletRemitController {
    private final String WALLET_REMIT_PROCESS_BACK_TEMPLATE = "打款状态回传模板.xlsx"
    @Autowired
    WalletRemitManageService walletRemitManageService
    @Autowired
    private ResourceService resourceService


    @VisitorPermission(value = "or110101")
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    void uploadFanhuaReport(
            @RequestParam(value = "codeFile", required = false) MultipartFile file,
            HttpServletResponse response) throws IOException {
        try {
            walletRemitManageService.uploadFile(file)
        } catch (FileUploadException fe) {
            log.debug("update wallet remit excel FileUploadException", fe)
            ResponseOutUtil.outPrint(response, fe.getMessage())
        } catch (Exception e) {
            log.debug("update wallet remit excel exception", e)
            ResponseOutUtil.outPrint(response, "系统异常!")
        }
        ResponseOutUtil.outPrint(response, "success")
    }

    @VisitorPermission(value = "or110101")
    @RequestMapping(value = "/upload/list", method = RequestMethod.GET)
    DataTablePageViewModel<WalletUploadHistoryModel> uploadHistoryList(PublicQuery publicQuery) {
        Page<WalletRemitUploadHistory> entityPage = walletRemitManageService.getUploadHistory(publicQuery.currentPage, publicQuery.pageSize)
        List<WalletUploadHistoryModel> modelList = entityPage.collect {
            new WalletUploadHistoryModel(
                    id: it.id,
                    createTime: getDateString(it.createTime, DateUtils.DATE_LONGTIME24_PATTERN),
                    fileName: it.fileName,
                    status: it.status == 1 ? "成功" : "失败",
                    operator: it.operator.name
            )
        }
        new DataTablePageViewModel<>(entityPage.totalElements, entityPage.totalElements, publicQuery.draw, modelList)
    }

    @RequestMapping(value = "/template/url", method = RequestMethod.GET)
    Map getTemplateUrl() {
        String templatePath = resourceService.getResourceAbsolutePath(resourceService.getProperties().getTemplatePath());
        String back_template = resourceService.absoluteUrl(templatePath, WALLET_REMIT_PROCESS_BACK_TEMPLATE);
        return ['back_template': back_template]
    }

}
