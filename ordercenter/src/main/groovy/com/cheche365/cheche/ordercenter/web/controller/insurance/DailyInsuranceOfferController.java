package com.cheche365.cheche.ordercenter.web.controller.insurance;

import com.cheche365.cheche.core.annotation.VisitorPermission;
import com.cheche365.cheche.manage.common.exception.FileUploadException;
import com.cheche365.cheche.manage.common.util.ResponseOutUtil;
import com.cheche365.cheche.ordercenter.service.insurance.DailyInsuranceOfferUploadService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by yinJianBin on 2017/3/3.
 */
@Component
@RequestMapping("/orderCenter/dailyInsuranceOffer")
public class DailyInsuranceOfferController {

    private org.slf4j.Logger logger = LoggerFactory.getLogger(DailyInsuranceOfferController.class);

    private static final String SUCCESS = "success";

    @Autowired
    private DailyInsuranceOfferUploadService dailyInsuranceOfferUploadService;

    @VisitorPermission("or0803")
    @RequestMapping(value = "/report/upload", method = RequestMethod.POST)
    public void uploadReport(@RequestParam(value = "codeFile", required = false) MultipartFile file, HttpServletResponse response) throws IOException {
        try {
            dailyInsuranceOfferUploadService.importReport(file);
            ResponseOutUtil.outPrint(response, SUCCESS);
        } catch (FileUploadException e) {
            ResponseOutUtil.outPrint(response, e.getMessage());
            logger.error("更新按天买保险分享活动报表失败", e);
        } catch (Exception e) {
            logger.error("更新按天买保险分享活动报表失败", e);
        }
    }


}
