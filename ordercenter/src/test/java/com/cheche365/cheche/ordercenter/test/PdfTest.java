package com.cheche365.cheche.ordercenter.test;

import com.cheche365.cheche.core.model.InsuranceType;
import com.cheche365.cheche.ordercenter.app.config.OrderCenterConfig;
import com.cheche365.cheche.ordercenter.service.order.InsurancePdfParserService;
import com.cheche365.cheche.common.util.StringUtil;
import com.cheche365.cheche.ordercenter.web.model.order.OrderInsuranceViewModel;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by xu.yelong on 2017/1/9.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
    classes = {OrderCenterConfig.class}
)
@EnableAutoConfiguration
@EnableWebMvc
@WebAppConfiguration
@EnableSpringDataWebSupport
@TransactionConfiguration
public class PdfTest {

    @Autowired
    InsurancePdfParserService insurancePdfParserService;


    @Test
    public void readPdf2() {
        File file = new File("C:/Users/Administrator/Desktop/comp.pdf");

        OrderInsuranceViewModel insuranceByPdfFile = insurancePdfParserService.getInsuranceByPdfFile(file, InsuranceType.Enum.COMPULSORY_2);
        System.out.println(insuranceByPdfFile);
    }


    @Test
    public void readPdf() {
        PDDocument document = null;
        try {
            document = PDDocument.load(new File("C:/Users/Administrator/Desktop/comp.pdf"));
//            PDFTextStripper textStripper = new PDFTextStripper();
            PDFTextStripperByArea stripper = new PDFTextStripperByArea();
            stripper.setSortByPosition(true);
            stripper.addRegion("compulsoryPremium", new Rectangle(23, 344, 550, 14));//保险费合计
            PDPage page = document.getPage(0);
            stripper.extractRegions(page);
            String compulsoryPremium = StringUtil.trim(stripper.getTextForRegion("compulsoryPremium").toString());
            System.out.println(stripper.getTextForRegion("compulsoryPremium"));
            document.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
