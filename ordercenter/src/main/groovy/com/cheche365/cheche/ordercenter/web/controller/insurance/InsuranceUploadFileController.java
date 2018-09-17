package com.cheche365.cheche.ordercenter.web.controller.insurance;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.service.ResourceService;
import com.cheche365.cheche.core.util.BeanUtil;
import com.cheche365.cheche.core.util.FileUtil;
import com.cheche365.cheche.manage.common.util.AssertUtil;
import com.cheche365.cheche.manage.common.util.ResponseOutUtil;
import com.cheche365.cheche.ordercenter.service.order.InsurancePdfParserService;
import com.cheche365.cheche.ordercenter.service.order.OrderReverseGeneratorService;
import com.cheche365.cheche.ordercenter.web.model.order.OrderInsuranceViewModel;
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
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by xu.yelong on 2015/12/9.
 * 上传保单扫描图片
 */
@RestController
@RequestMapping("/orderCenter/insurance/upload")
public class InsuranceUploadFileController {
    private Logger logger = LoggerFactory.getLogger(InsuranceUploadFileController.class);

    @Autowired
    private ResourceService resourceService;
    @Autowired
    private InsurancePdfParserService insurancePdfParserService;
    @Autowired
    private OrderReverseGeneratorService orderReverseGeneratorService;

    private final static String DATE_FORMAT_TEXT = "yyyyMMddHHmmssSSS";

//    @RequestMapping(value = "/commercial", method = RequestMethod.POST)
//    public void uploadCommercial(@RequestParam(value = "codeFile") MultipartFile commercialFile, @RequestParam(value = "orderNo") String orderNo, HttpServletResponse response) throws Exception {
//        File[] files = new File[1];
//        String insuranceInputFilePath = uploadFile(commercialFile, files);
//        if (insuranceInputFilePath == null) {
//            ResponseOutUtil.outPrint(response, "error");
//            return;
//        }
//        ResponseOutUtil.outPrint(response, insuranceInputFilePath);
//    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public void uploadCompulsory(@RequestParam(value = "codeFile") MultipartFile compulsoryFile, @RequestParam(value = "orderNo") String orderNo, HttpServletResponse response) throws Exception {
        File[] files = new File[1];
        String insuranceInputFilePath = uploadFile(compulsoryFile, files);
        if (insuranceInputFilePath == null) {
            ResponseOutUtil.outPrint(response, "error");
            return;
        }
        ResponseOutUtil.outPrint(response, insuranceInputFilePath);
    }

    public String uploadFile(MultipartFile file, File[] files) throws IOException {
        AssertUtil.notNull(file, "文件不可为空");
        String originalFileName = file.getOriginalFilename();
        String suffix = originalFileName.substring(originalFileName.lastIndexOf("."));
        String endfix;
        if (suffix.equals(".pdf")) {
            endfix = "pdf";
        } else {
            endfix = "img";
        }
        String basePath = resourceService.getResourceAbsolutePath(resourceService.getProperties().getInsurancePath());
        basePath += endfix + File.separator;
        basePath = basePath + new SimpleDateFormat("yyyy/MM/dd").format(new Date());
        if (!new File(basePath).exists())
            if (!new File(basePath).mkdirs())
                throw new RuntimeException("创建存储文件目录失败");
        String fileName = DateUtils.getCurrentDateString(DATE_FORMAT_TEXT) + suffix;
        files[0] = new File(basePath, fileName);
        AssertUtil.notExists(files[0], "已存在相同名字的文件");

        FileUtil.writeFile(files[0].getAbsolutePath(), file.getBytes());

        //    file.transferTo(files[0].getAbsoluteFile());
        return resourceService.absoluteUrl(basePath + "/", fileName);
    }


    private void convertViewModel(OrderInsuranceViewModel pdf, OrderInsuranceViewModel dataSource, String[] copyContains) {
        BeanUtil.copyPropertiesContain(pdf, dataSource, copyContains);
    }

}
