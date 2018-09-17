package com.cheche365.cheche.operationcenter.web.controller.wechat.channel;

import com.cheche365.cheche.core.annotation.VisitorPermission;
import com.cheche365.cheche.core.model.QRCodeChannel;
import com.cheche365.cheche.core.service.ResourceService;
import com.cheche365.cheche.manage.common.exception.FieldValidtorException;
import com.cheche365.cheche.manage.common.model.PublicQuery;
import com.cheche365.cheche.operationcenter.service.wechat.channel.ForeverQCChannelService;
import com.cheche365.cheche.operationcenter.service.wechat.channel.QCChannelService;
import com.cheche365.cheche.operationcenter.service.wechat.channel.QRCodeType;
import com.cheche365.cheche.operationcenter.service.wechat.channel.TempQCChannelService;
import com.cheche365.cheche.operationcenter.web.model.DataTablesPageViewModel;
import com.cheche365.cheche.manage.common.web.model.PageInfo;
import com.cheche365.cheche.operationcenter.web.model.wechat.channel.QRCodeChannelViewModel;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by wangfei on 2015/7/28.
 */
@RestController
@RequestMapping("/operationcenter/qcchannels")
public class QCChannelController {
    private Logger logger = LoggerFactory.getLogger(QCChannelController.class);

    @Autowired
    private TempQCChannelService tempQCChannelService;

    @Autowired
    private ForeverQCChannelService foreverQCChannelService;

    @Autowired
    private ResourceService resourceService;

    @RequestMapping(value = "/dump", method = RequestMethod.GET)
    public boolean dump() {
        logger.info("dump qrcode channel start");
        tempQCChannelService.dump();
        logger.info("dump qrcode channel end");
        return true;
    }

    @RequestMapping(value = "/ids/{qrCodeType}", method = RequestMethod.GET)
    public String generateChannelNo(@PathVariable String qrCodeType) {
        logger.info("generate qrCode channelNo by qrCodeType : {}", qrCodeType);

        QRCodeType type = QRCodeType.format(qrCodeType);
        if (null == type)
            throw new RuntimeException("unSupport qrCode type -> " + qrCodeType);

        return this.getQCChannelService(type).getNextChannelNo(type);
    }

    @RequestMapping(value = "/temp", method = RequestMethod.POST)
//    @VisitorPermission("op020102") 新建一个临时二维码不加权限控制
    public Map<String, String> addTemp(@Valid QRCodeChannelViewModel model, BindingResult result) {
        return this.add(model, result);
    }

    @RequestMapping(value = "/temp/batch", method = RequestMethod.POST)
    @VisitorPermission("op020103")
    public Map<String, String> addBatchTemp(@Valid QRCodeChannelViewModel model, BindingResult result) {
        return this.add(model, result);
    }


    @RequestMapping(value = "/forever", method = RequestMethod.POST)
    @VisitorPermission("op020202")
    public Map<String, String> addForever(@Valid QRCodeChannelViewModel model, BindingResult result) {
        return this.add(model, result);
    }


    @RequestMapping(value = "/forever/batch", method = RequestMethod.POST)
    @VisitorPermission("op020203")
    public Map<String, String> addBatchForever(@Valid QRCodeChannelViewModel model, BindingResult result) {
        return this.add(model, result);
    }

    private Map<String, String> add(QRCodeChannelViewModel model, BindingResult result) {
        if (logger.isDebugEnabled()) {
            logger.debug("add new qrCode channel start");
        }

        if (result.hasErrors())
            throw new RuntimeException("some required info has been missed");

        QRCodeType type = QRCodeType.format(model.getQrCodeType());
        logger.info("qrCode channel type -> {}", type);
        if (null == type)
            throw new RuntimeException("unSupport qrCode type -> " + model.getQrCodeType());

        if (null != this.getQCChannelService(type).getByChannelCode(model.getCode()))
            throw new RuntimeException(model.getCode() + " has been used");

        List<QRCodeChannel> qrCodeChannelList = this.getQCChannelService(type).addChannels(model, type);
        if (null == qrCodeChannelList || qrCodeChannelList.isEmpty())
            throw new RuntimeException("创建渠道失败");

        StringBuffer buffer = new StringBuffer("");
        qrCodeChannelList.forEach(qrCodeChannel -> buffer.append(qrCodeChannel.getId()).append(","));
        Map<String, String> stringMap = new HashMap<>();
        stringMap.put("ids", buffer.toString().substring(0, buffer.toString().length() - 1));

        return stringMap;
    }


    @RequestMapping(value = "/{channelIds}/{qrCodeType}/download")
    public void downLoadImagesTemp(@PathVariable String channelIds, @PathVariable String qrCodeType, HttpServletResponse response) {
        this.downLoadImages(channelIds, qrCodeType, response);
    }

    private void downLoadImages(String channelIds, String qrCodeType, HttpServletResponse response) {
        logger.info("download wechat qrcode by channelIds -> {}", channelIds);
        response.setHeader("X-Frame-Options", "SAMEORIGIN");

        QRCodeType type = QRCodeType.format(qrCodeType);
        logger.info("qrCode channel type -> {}", type);
        if (null == type)
            throw new RuntimeException("unSupport qrCode type -> " + qrCodeType);

        String[] splitIds = channelIds.split(",");
        if (splitIds.length == 1) {
            String imagePath = this.getQCChannelService(type).getWechatQRCodeImagePath(Long.valueOf(channelIds));
            QRCodeChannelViewModel viewModel = this.getQCChannelService(type).findOne(Long.valueOf(channelIds));
            this.downloadFile(imagePath, viewModel.getName() + ".png", "image/*", response);
        } else {
            Path filePath = Paths.get(resourceService.getResourceAbsolutePath(
                resourceService.getProperties().getWechatQRCodePath()),
                qrCodeType, "二维码包" + "_" + System.currentTimeMillis() + ".zip");
            String zipFilePath = filePath.toFile().getAbsolutePath();
            logger.info("临时二维码包存放路径：" + zipFilePath);
            try {
                ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFilePath), Charset.forName("gb2312"));
                Map<String, String> imagePathMap = this.getQCChannelService(type).getWechatQRCodeImagePath(splitIds);

                for (Map.Entry<String, String> entry : imagePathMap.entrySet()) {
                    zipFile(entry.getValue(), zos, entry.getKey() + ".png");
                }
                zos.close();
                this.downloadFile(zipFilePath, "二维码包.zip", "application/x-msdownload", response);
            } catch (IOException ex) {
                logger.error("download qrcode image package error.");
            } finally {
                // 删除临时生成的压缩包文件
                File file = new File(zipFilePath);
                if (file.exists()) {
                    file.delete();
                }
            }
        }
    }

    @RequestMapping(value = "/img/{qrCodeType}/{channelId}", method = RequestMethod.GET)
    public void loadImg(@PathVariable String qrCodeType, @PathVariable Long channelId, HttpServletResponse response) {
        logger.info("load qrCode image by channelId -> {}", channelId);

        QRCodeType type = QRCodeType.format(qrCodeType);
        logger.info("qrCode channel type -> {}", type);
        if (null == type)
            throw new RuntimeException("unSupport qrCode type -> " + qrCodeType);

        String imagePath = this.getQCChannelService(type).getWechatQRCodeImagePath(channelId);
        this.downloadFile(imagePath, "", "image/*", response);
    }

    @RequestMapping(value = "/updateChannel/{channelId}", method = RequestMethod.PUT)
    @VisitorPermission("op020208")
    public QRCodeChannelViewModel updateChannel(@PathVariable Long channelId, @Valid QRCodeChannelViewModel model, BindingResult result) {
        if (logger.isDebugEnabled()) {
            logger.debug("update forver qrcode channel start");
        }

        if (result.hasErrors())
            throw new RuntimeException("some required info has been missed");

        QRCodeType type = QRCodeType.format(model.getQrCodeType());
        logger.info("qrCode channel type -> {}", type);
        if (null == type)
            throw new RuntimeException("unSupport qrCode type -> " + model.getQrCodeType());

        if (model.getUpdateFlag() == null)
            throw new RuntimeException("choose update channel type");

        if (foreverQCChannelService.findOne(channelId) == null)
            throw new RuntimeException("old qrcode channel is not existed,id is " + channelId);

        return foreverQCChannelService.updateChannel(model, channelId);
    }

    @RequestMapping(value = "/{channelId}", method = RequestMethod.PUT)
    @VisitorPermission("op020207")
    public QRCodeChannelViewModel update(@PathVariable Long channelId, @Valid QRCodeChannelViewModel model, BindingResult result) {
        if (logger.isDebugEnabled()) {
            logger.debug("update forver qrcode channel start");
        }

        if (result.hasErrors())
            throw new RuntimeException("some required info has been missed");

        QRCodeType type = QRCodeType.format(model.getQrCodeType());
        logger.info("qrCode channel type -> {}", type);
        if (null == type)
            throw new RuntimeException("unSupport qrCode type -> " + model.getQrCodeType());

        if (model.getUpdateFlag() == null)
            throw new RuntimeException("choose update channel type");

        if (foreverQCChannelService.findOne(channelId) == null)
            throw new RuntimeException("old qrcode channel is not existed,id is " + channelId);

        return foreverQCChannelService.updateChannel(model, channelId);
    }

    @RequestMapping(value = "/temp", method = RequestMethod.GET)
    @VisitorPermission("op0201")
    public DataTablesPageViewModel<QRCodeChannelViewModel> findListTemp(PublicQuery query) {
        return this.findList(query);
    }

    @RequestMapping(value = "/forever", method = RequestMethod.GET)
    @VisitorPermission("op0202")
    public DataTablesPageViewModel<QRCodeChannelViewModel> findListForever(PublicQuery query) {
        return this.findList(query);
    }

    private DataTablesPageViewModel<QRCodeChannelViewModel> findList(PublicQuery query) {
        try {
            Page<QRCodeChannel> page = tempQCChannelService.getQRCodeChannelByPage(query);
            List<QRCodeChannelViewModel> modelList = tempQCChannelService.pageViewDataList(page);
            PageInfo pageInfo = tempQCChannelService.createPageInfo(page);
            return new DataTablesPageViewModel<>(pageInfo.getTotalElements(), pageInfo.getTotalElements(), query.getDraw(), modelList);
        } catch (Exception e) {
            logger.error("QCChannelController has error", e);
        }
        return null;
    }


    /**
     * 获取二维码渠道详情
     *
     * @param channelId
     * @return
     */
    @RequestMapping(value = "/temp/{qrCodeType}/{channelId}", method = RequestMethod.GET)
    @VisitorPermission("op020105")
    public QRCodeChannelViewModel findOneTemp(@PathVariable String qrCodeType, @PathVariable Long channelId) {
        return this.findOne(qrCodeType, channelId);
    }

    /**
     * 获取二维码渠道详情
     *
     * @param channelId
     * @return
     */
    @RequestMapping(value = "/forever/{qrCodeType}/{channelId}", method = RequestMethod.GET)
    @VisitorPermission("op020205")
    public QRCodeChannelViewModel findOneForever(@PathVariable String qrCodeType, @PathVariable Long channelId) {
        return this.findOne(qrCodeType, channelId);
    }

    private QRCodeChannelViewModel findOne(String qrCodeType, Long channelId) {
        logger.info("get qrcode channel detail,id:" + channelId);
        if (channelId == null || channelId < 1) {
            throw new FieldValidtorException("find qrcode channel detail, id can not be null or less than 1");
        }

        QRCodeType type = QRCodeType.format(qrCodeType);
        logger.info("qrCode channel type -> {}", type);
        if (null == type)
            throw new RuntimeException("unSupport qrCode type -> " + qrCodeType);

        return this.getQCChannelService(type).findOne(channelId);
    }

    /**
     * 导出指定二维码的扫描关注数
     *
     * @param channelCode
     * @param response
     */
    @RequestMapping(value = "/{qrCodeType}/{channelCode}/export/temp", method = RequestMethod.GET)
    @VisitorPermission("op020106")
    public void exportOneTemp(@PathVariable String qrCodeType, @PathVariable String channelCode, HttpServletResponse response) {
        this.exportScanAndSubscribeCount(qrCodeType, channelCode, response);
    }

    /**
     * 导出指定二维码的扫描关注数
     *
     * @param channelCode
     * @param response
     */
    @RequestMapping(value = "/{qrCodeType}/{channelCode}/export/forever", method = RequestMethod.GET)
    @VisitorPermission("op020206")
    public void exportOneForever(@PathVariable String qrCodeType, @PathVariable String channelCode, HttpServletResponse response) {
        this.exportScanAndSubscribeCount(qrCodeType, channelCode, response);
    }

    private void exportScanAndSubscribeCount(String qrCodeType, String channelCode, HttpServletResponse response) {
        logger.info("export qrcode scan and subscribe count,code:" + channelCode);
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setCharacterEncoding("utf-8");
        response.setHeader("X-Frame-Options", "SAMEORIGIN");

        QRCodeType type = QRCodeType.format(qrCodeType);
        logger.info("qrCode channel type -> {}", type);
        if (null == type)
            throw new RuntimeException("unSupport qrCode type -> " + qrCodeType);
        String channelName = tempQCChannelService.getByChannelCode(channelCode).getName();
        OutputStream out = null;
        try {
            response.setHeader("Content-Disposition",
                "attachment; filename=" + new String((channelName + "-扫描关注数结果.xls").getBytes(), "iso-8859-1"));
            HSSFWorkbook workbook = this.getQCChannelService(type).createScanAndSubscribeCountExportExcel(channelCode);
            out = response.getOutputStream();
            workbook.write(out);
            out.flush();
            out.close();
        } catch (Exception ex) {
            logger.error("export qrcode scan and subscribe count excel has error", ex);
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (Exception ex) {
                logger.error("export qrcode scan and subscribe count result, close OutputStream has error", ex);
            }
        }
    }

    /**
     * 导出查询出的渠道信息列表
     *
     * @param response
     */
    @RequestMapping(value = "/temp/export", method = RequestMethod.GET)
    @VisitorPermission("op020101")
    public void exportTempList(PublicQuery query, HttpServletResponse response) {
        this.exportList(query, response);
    }

    /**
     * 导出查询出的渠道信息列表
     *
     * @param response
     */
    @RequestMapping(value = "/forever/export", method = RequestMethod.GET)
    @VisitorPermission("op020201")
    public void exportForeverList(PublicQuery query, HttpServletResponse response) {
        this.exportList(query, response);
    }

    private void exportList(PublicQuery query, HttpServletResponse response) {
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setCharacterEncoding("utf-8");
        response.setHeader("X-Frame-Options", "SAMEORIGIN");

        QRCodeType type = QRCodeType.format(query.getQrCodeType());
        if (null == type)
            throw new RuntimeException("unSupport qrCode type -> " + query.getQrCodeType());

        OutputStream out = null;
        try {
            response.setHeader("Content-Disposition",
                "attachment; filename=" + new String(("二维码渠道信息查询结果.xls").getBytes(), "iso-8859-1"));
            HSSFWorkbook workbook = this.getQCChannelService(type).createExportExcel(query);
            out = response.getOutputStream();
            workbook.write(out);
            out.flush();
            out.close();
        } catch (Exception ex) {
            logger.error("export qrcode result has error", ex);
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (Exception ex) {
                logger.error("export qrcode  result, close OutputStream has error", ex);
            }
        }
    }

    private QCChannelService getQCChannelService(QRCodeType type) {
        switch (type) {
            case QR_SCENE:
                return tempQCChannelService;
            case QR_LIMIT_SCENE:
                return foreverQCChannelService;
        }
        return null;
    }

    private void downloadFile(String path, String fileName, String contentType, HttpServletResponse response) {
        InputStream in = null;
        OutputStream out = null;
        try {
            File file = new File(path);
            response.setContentType(contentType);
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + new String((fileName).getBytes(), "iso-8859-1"));
            response.setHeader("Content-Length", String.valueOf(file.length()));

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

                if (out != null)
                    out.close();
            } catch (IOException e) {
                logger.error("close stream has error", e);
            }
        }
    }

    private void zipFile(String filePath, ZipOutputStream zos, String fileName) {
        File file = new File(filePath);
        if (!file.exists()) {
            logger.warn("文件" + file.getName() + "不存在");
            return;
        }

        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            zos.putNextEntry(new ZipEntry(fileName));

            int count;
            byte data[] = new byte[1024];
            while ((count = bis.read(data, 0, data.length)) != -1) {
                zos.write(data, 0, count);
            }

            bis.close();
        } catch (IOException e) {
            logger.error("添加文件" + file.getName() + "至压缩文件失败");
        }
    }
}
