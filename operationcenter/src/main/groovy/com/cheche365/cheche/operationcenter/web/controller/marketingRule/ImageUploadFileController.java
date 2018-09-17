package com.cheche365.cheche.operationcenter.web.controller.marketingRule;

import com.cheche365.cheche.common.util.DateUtils;
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

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by xu.yelong on 2015/12/9.
 * 上传保单扫描图片
 */
@RestController
@RequestMapping("/operationcenter/image/upload")
public class ImageUploadFileController {
    private Logger logger = LoggerFactory.getLogger(ImageUploadFileController.class);

    @Autowired
    private ResourceService resourceService;

    private final static String DATE_FORMAT_TEXT = "yyyyMMddHHmmssSSS";

    public static String TOP_IMAGE_URL = "activity/image/shared";
    public static String SHARED_ICON_URL = "activity/image/shared";

    @RequestMapping(value = "/sharedIcon", method = RequestMethod.POST)
    public void uploadCompulsory(@RequestParam(value = "UpLoadSharedFile") MultipartFile compulsoryFile, HttpServletResponse response) throws Exception {
        String middlePath = this.TOP_IMAGE_URL;
        String insuranceInputFilePath = uploadFile(compulsoryFile, middlePath);
        if (insuranceInputFilePath == null) {
            this.outPrint(response, "error");
            return;
        }
        this.outPrint(response, insuranceInputFilePath);
    }

    @RequestMapping(value = "/topImage", method = RequestMethod.POST)
    public void uploadCommercial(@RequestParam(value = "UpLoadActivityFile") MultipartFile commercialFile, HttpServletResponse response) throws Exception {
        String middlePath = this.SHARED_ICON_URL;
        String insuranceInputFilePath = uploadFile(commercialFile, middlePath);
        if (insuranceInputFilePath == null) {
            this.outPrint(response, "error");
            return;
        }
        this.outPrint(response, insuranceInputFilePath);
    }

    public String uploadFile(MultipartFile file,String middlePath) throws IOException {
        AssertUtil.notNull(file, "文件不可为空");
        String basePath = resourceService.getResourceAbsolutePath(resourceService.getProperties().getIosPath() + middlePath);
        if (!new File(basePath ).exists())
            if (!new File(basePath).mkdirs())
                throw new RuntimeException("创建存储文件目录失败");
        String originalFileName = file.getOriginalFilename();
        String suffix = originalFileName.substring(originalFileName.lastIndexOf("."));
        //判断文件格式
        String imgeArray [] = {".jpg",".jpeg",".png",".gif"};
        boolean suffixeq = false;
        for (int i = 0;i < imgeArray.length;i ++) {
            if(suffix.equals(imgeArray[i]))
                suffixeq = true;
        }
        if (!suffixeq)
            return null;
        String fileName = DateUtils.getCurrentDateString(DATE_FORMAT_TEXT) + suffix;
        File targetFile = new File(basePath, fileName);
        AssertUtil.notExists(targetFile, "已存在相同名字的文件");
        FileUtil.writeFile(targetFile.getAbsolutePath(),file.getBytes());
        return resourceService.absoluteUrl(basePath + "/",fileName);
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
