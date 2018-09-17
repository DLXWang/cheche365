package com.cheche365.cheche.operationcenter.web.controller.partner;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.annotation.VisitorPermission;
import com.cheche365.cheche.core.service.ResourceService;
import com.cheche365.cheche.core.util.FileUtil;
import com.cheche365.cheche.manage.common.exception.FieldValidtorException;
import com.cheche365.cheche.manage.common.model.PublicQuery;
import com.cheche365.cheche.operationcenter.service.partner.IPartnerService;
import com.cheche365.cheche.operationcenter.service.partner.PartnerService;
import com.cheche365.cheche.manage.common.util.AssertUtil;
import com.cheche365.cheche.operationcenter.web.model.DataTablesPageViewModel;
import com.cheche365.cheche.manage.common.web.model.ResultModel;
import com.cheche365.cheche.operationcenter.web.model.partner.CooperationModeViewModel;
import com.cheche365.cheche.operationcenter.web.model.partner.PartnerAttachmentViewModel;
import com.cheche365.cheche.operationcenter.web.model.partner.PartnerViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.*;
import java.util.List;

/**
 * Created by sunhuazhong on 2015/8/26.
 */
@RestController
@RequestMapping("/operationcenter/partners")
public class PartnerController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IPartnerService partnerService;

    @Autowired
    private ResourceService resourceService;

    private final static String DATE_FORMAT_TEXT = "yyyyMMddHHmmssSSS";

    /**
     * 新增合作商
     * @param model
     * @param result
     * @return
     */
    @RequestMapping(value = "",method = RequestMethod.POST)
    @VisitorPermission("op010101")
    public ResultModel add(@Valid PartnerViewModel model, BindingResult result) {
        logger.info("add new partner start...");
        if (result.hasErrors())
            return new ResultModel(false, "请将信息填写完整");

        partnerService.addPartner(model);
        return new ResultModel();
    }

    /**
     * 修改合作商
     * @param partnerId
     * @param model
     * @param result
     * @return
     */
    @RequestMapping(value = "/{partnerId}",method = RequestMethod.PUT)
    @VisitorPermission("op010102")
    public ResultModel update(@PathVariable Long partnerId, @Valid PartnerViewModel model, BindingResult result) {
        logger.info("update partner's info by id -> {}", partnerId);
        if (result.hasErrors())
            return new ResultModel(false, "请将信息填写完整");

        partnerService.updatePartner(model);
        return new ResultModel();
    }

    /**
     * 删除合作商
     * @param partnerId
     */
    @RequestMapping(value = "/{partnerId}",method = RequestMethod.DELETE)
    public boolean delete(@PathVariable Long partnerId) {
        if(logger.isDebugEnabled()) {
            logger.debug("delete partner start");
        }

        return partnerService.delete(partnerId);
    }

    /**
     * 根据条件查询合作商
     * @return
     */
    @RequestMapping(value = "",method = RequestMethod.GET)
    @VisitorPermission("op0101")
    public DataTablesPageViewModel search(PublicQuery query) {
        return partnerService.search(query);
    }

    /**
     * 获取合作商详情
     * @param partnerId
     * @return
     */
    @RequestMapping(value = "/{partnerId}",method = RequestMethod.GET)
    public PartnerViewModel findOne(@PathVariable Long partnerId) {
        if(logger.isDebugEnabled()) {
            logger.debug("get partner detail,id:" + partnerId);
        }

        if(partnerId == null || partnerId < 1){
            throw new FieldValidtorException("find partner detail, id can not be null or less than 1");
        }

        return partnerService.findById(partnerId);
    }

    /**
     * 启用或禁用合作商
     * @param partnerId
     * @param operationType，1-启用，0-禁用
     * @return
     */
    @RequestMapping(value = "/{partnerId}/{operationType}",method = RequestMethod.PUT)
    @VisitorPermission("op010103")
    public ResultModel operation(@PathVariable Long partnerId, @PathVariable Integer operationType) {
        logger.info("switch partner to enable or disable by id -> {}, operationType -> {}", partnerId, operationType);
        if (partnerId == null || partnerId < 1)
            throw new FieldValidtorException("operation partner, id can not be null or less than 1");

        partnerService.switchStatus(partnerId, operationType);
        return new ResultModel();
    }

    /**
     * 上传合作商文件，包括合同和技术文档
     * 参照AutoShopController中的uploadImages方法
     * @throws Exception
     */
    @RequestMapping(value = "/{partnerId}/{fileType}/{partnerAttachmentId}",method = RequestMethod.POST)
    public PartnerAttachmentViewModel uploadFile(@PathVariable Integer fileType, @PathVariable Long partnerAttachmentId,
                                                 @PathVariable Long partnerId, MultipartFile file) throws Exception {
        logger.info("upload partner file by params: fileType -> {}, partnerAttachmentId -> {}, partnerId -> {}",
            fileType, partnerAttachmentId, partnerId);

        AssertUtil.notNull(file, "文件不可为空");
        String basePath = resourceService.getResourceAbsolutePath(resourceService.getProperties().getPartnerPath());
        if (!new File(basePath).exists())
            if (!new File(basePath).mkdirs())
                throw new RuntimeException("创建存储文件目录失败");

        String originalFileName = file.getOriginalFilename();
        String suffix = originalFileName.substring(originalFileName.lastIndexOf("."));
        String fileName = DateUtils.getCurrentDateString(DATE_FORMAT_TEXT) + suffix;
        File targetFile = new File(basePath, fileName);
        AssertUtil.notExists(targetFile, "已存在相同名字的文件");
        FileUtil.writeFile(targetFile.getAbsolutePath(),file.getBytes());
        return partnerService.addPartnerAttachmentFiles(fileType, partnerAttachmentId, partnerId, targetFile.getAbsolutePath(),
            originalFileName);
    }

    /**
     * 删除合作商文件，包括合同和技术文档
     * 参照AutoShopController中的removePicture方法
     * @param fileType 文件类型，1-合同；2-技术文档
     * @return
     */
    @RequestMapping(value = "/{partnerId}/{fileType}/{partnerAttachmentId}",method = RequestMethod.DELETE)
    public PartnerAttachmentViewModel removeFile(@PathVariable Long partnerId, @PathVariable Integer fileType, @PathVariable Long partnerAttachmentId) {
        logger.info("remove partner file by params: fileType -> {}, partnerAttachmentId -> {}", fileType, partnerAttachmentId);
        return partnerService.removePartnerAttachmentFiles(partnerId, fileType, partnerAttachmentId);
    }

    /**
     * 下载合作商文件，包括合同
     * 参照QCChannelController中的downLoadImages方法
     * @param partnerId
     * @param fileType 文件类型，1-合同；
     * @param response
     */
    @RequestMapping(value = "/{partnerId}/pactfile/{fileType}")
    @VisitorPermission("op010105")
    public void downloadPact(@PathVariable Long partnerId, @PathVariable Integer fileType, HttpServletResponse response) {
        logger.info("download partner file by id -> {}, fileType -> {}", partnerId, fileType);

        PartnerViewModel model = partnerService.findById(partnerId);
        if (PartnerService.FileType.CONTRACT.getValue().equals(fileType)) {
            this.downloadFile(model.getPartnerAttachment().getContractUrl(), model.getPartnerAttachment().getContractName(),
                "application/octet-stream", response);
        } else {
            this.downloadFile(model.getPartnerAttachment().getTechnicalDocumentUrl(), model.getPartnerAttachment().getTechnicalDocumentName(),
                "application/octet-stream", response);
        }
    }

    /**
     * 下载合作商文件，包括技术文档
     * 参照QCChannelController中的downLoadImages方法
     * @param partnerId
     * @param fileType 文件类型，2-技术文档
     * @param response
     */
    @RequestMapping(value = "/{partnerId}/docfile/{fileType}")
    @VisitorPermission("op010106")
    public void downloadDoc(@PathVariable Long partnerId, @PathVariable Integer fileType, HttpServletResponse response) {
        logger.info("download partner file by id -> {}, fileType -> {}", partnerId, fileType);

        PartnerViewModel model = partnerService.findById(partnerId);
        if (PartnerService.FileType.CONTRACT.getValue().equals(fileType)) {
            this.downloadFile(model.getPartnerAttachment().getContractUrl(), model.getPartnerAttachment().getContractName(),
                "application/octet-stream", response);
        } else {
            this.downloadFile(model.getPartnerAttachment().getTechnicalDocumentUrl(), model.getPartnerAttachment().getTechnicalDocumentName(),
                "application/octet-stream", response);
        }
    }
    private void downloadFile(String path, String fileName, String contentType, HttpServletResponse response) {
        InputStream in = null;
        OutputStream out = null;
        try {
            File file = new File(path);
            response.setContentType(contentType);
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + new String((fileName).getBytes(), "iso-8859-1"));
            response.setHeader("Content-Length",String.valueOf(file.length()));

            in = new BufferedInputStream(new FileInputStream(file));
            out = new BufferedOutputStream(response.getOutputStream());

            byte[] data = new byte[1024];
            int len;
            while (-1 != (len = in.read(data, 0, data.length))) {
                out.write(data, 0, len);
            }
        } catch (Exception ex) {
            logger.error("download file" + path + fileName + " has error", ex);
        } finally {
            try {
                if (in != null)
                    in.close();

                if(out != null)
                    out.close();
            } catch (IOException e) {
                logger.error("close stream has error", e);
            }
        }
    }

    /**
     * 获取合作商的合作方式
     * @return
     */
    @RequestMapping(value = "/cooperationModes/{partnerId}", method = RequestMethod.GET)
    public List<CooperationModeViewModel> getCooperationModes(@PathVariable Long partnerId) {
        if(partnerId == null || partnerId < 1){
            throw new FieldValidtorException("find cooperationModes, id can not be null or less than 1");
        }
        return partnerService.getCooperationModeByPartnerId(partnerId);
    }

    /**
     * 验证合作商名称唯一性
     * @param name
     * @return
     */
    @RequestMapping(value = "/check",method = RequestMethod.GET)
    public boolean checkCode(@RequestParam(value = "name",required = true) String name) {
        if(logger.isDebugEnabled()) {
            logger.debug("check partner name is unique, name:" + name);
        }
        
        return partnerService.checkPartnerName(name);
    }
}
