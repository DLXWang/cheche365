package com.cheche365.cheche.operationcenter.web.controller.thirdPartyCooperation;

import com.cheche365.cheche.core.service.ResourceService;
import com.cheche365.cheche.core.util.FileUtil;
import com.cheche365.cheche.manage.common.util.AssertUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

/**
 * 第三方合作模块图片上传接口
 */

@RestController
@RequestMapping("/operationcenter/thirdParty")
public class ThirdPartyImageUploadController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private ResourceService resourceService;

    @RequestMapping(value = "/imageUpload", method = RequestMethod.POST)
    public void imageUpload(@RequestParam(value = "uploadImageFile") MultipartFile imageFile, HttpServletResponse response, HttpServletRequest request) throws Exception {

        String channelCode = request.getParameter("channelCode");
        String newFileName = channelCode + ".png";
        String path = resourceService.getResourceAbsolutePath(resourceService.getProperties().getChannelPath());
//        String path = "C:/file/image";
        String[] suffixArray = {".png"};
        BufferedImage bufferedImage = ImageIO.read(imageFile.getInputStream());
        if (checkFileName(imageFile, suffixArray,bufferedImage)) {
            String filePath = saveImageFile(imageFile, newFileName, path);
            if (filePath == null) {
                this.outPrint(response, "error");
                return;
            }
            this.outPrint(response, filePath);
        } else {
            this.outPrint(response, "error");
        }

    }


    // 文件名格式，后缀名判断
    private boolean checkFileName(MultipartFile fileName, String[] suffixArray,BufferedImage bufferedImage) {
        boolean checkResult = false;
        // 判断文件格式
        String suffix = fileName.getOriginalFilename().substring(fileName.getOriginalFilename().lastIndexOf(".")).toLowerCase();
        for (int i = 0; i < suffixArray.length; i++) {
            if (suffix.equals(suffixArray[i]))
                if (bufferedImage.getHeight() == 200 && bufferedImage.getWidth() == 200)
                    checkResult = true;
        }
        return checkResult;
    }

    // 图片文件存储
    private String saveImageFile(MultipartFile file, String fileName, String path) throws IOException {
        AssertUtil.notNull(file, "文件不可为空");
        if (!new File(path).exists())
            if (!new File(path).mkdirs())
                throw new RuntimeException("创建存储文件目录失败");
        long startTime = 0;
        File targetFile = new File(path, fileName);
        if (targetFile.exists()) {
            startTime = new Date().getTime();
            FileUtil.deleteFile(fileName);
        }
//        AssertUtil.notExists(targetFile, "已存在相同名字的文件");

        FileUtil.writeFile(targetFile.getAbsolutePath(), file.getBytes());
        long endTime = new Date().getTime();
        long cha = endTime-startTime;
        logger.debug("时间差 ----------------" + cha);
        return resourceService.absoluteUrl(path + "/", fileName);
    }

    private void outPrint(HttpServletResponse response, String ajaxString) throws Exception {
        PrintWriter out = null;
        try {
            response.setContentType("text/html; charset=utf-8");
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("X-Frame-Options", "SAMEORIGIN");
            out = response.getWriter();
            out.write(ajaxString);
            out.flush();
        } catch (Exception ex) {
            throw new RuntimeException("write return string has error", ex);
        } finally {
            if (out != null)
                out.close();
        }
    }


}
