package com.cheche365.cheche.ordercenter.service.order;

import com.cheche365.cheche.common.math.NumberUtils;
import com.cheche365.cheche.core.model.GlassType;
import com.cheche365.cheche.core.model.InsuranceType;
import com.cheche365.cheche.common.util.StringUtil;
import com.cheche365.cheche.ordercenter.web.model.order.OrderInsuranceViewModel;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by chenxiangyin on 2017/1/13.
 */
@Service
public class InsurancePdfParserService {

    private Logger logger = LoggerFactory.getLogger(OrderReverseGeneratorService.class);

    private OrderInsuranceViewModel setExtraCompulsory(PDFTextStripperByArea stripper) {
        OrderInsuranceViewModel orderInsuranceViewModel = new OrderInsuranceViewModel();
        orderInsuranceViewModel.setInsuredIdNo(getStripperValue("insuredIdNo", stripper));
        orderInsuranceViewModel.setAreaName(getStripperValue("area", stripper));
        String dateStr = getStripperValue("date", stripper);
        String[] dateArr = dateStr.split("起");
        String commercialEffectiveDate = dateArr[0].substring(0, 10).replaceAll("[^(\\\\u4e00-\\\\u9fa5)]", "-");
        String compulsoryExpireDate = dateArr[1].substring(1, 11).replaceAll("[^(\\\\u4e00-\\\\u9fa5)]", "-");
        String commercialEffectiveHour = StringUtil.convertNumber(dateArr[0].substring(11));
        String commercialExpireHour = StringUtil.convertNumber(dateArr[1].substring(12));
        orderInsuranceViewModel.setCompulsoryEffectiveDate(commercialEffectiveDate);
        orderInsuranceViewModel.setCompulsoryEffectiveHour(Integer.parseInt(commercialEffectiveHour));
        orderInsuranceViewModel.setCompulsoryExpireDate(compulsoryExpireDate);
        orderInsuranceViewModel.setCompulsoryExpireHour(Integer.parseInt(commercialExpireHour));
        orderInsuranceViewModel.setCompulsoryPolicyNo(getStripperValue("policyNo", stripper));

//        String compulsoryPremium = getStripperValue("compulsoryPremium", stripper);
//        orderInsuranceViewModel.setCompulsoryPremium(NumberUtils.toFinancialDouble(compulsoryPremium.substring(compulsoryPremium.indexOf("：") + 1, compulsoryPremium.indexOf("元"))));
        String autoTaxString = getStripperValue("autoTax", stripper);
        String s = autoTaxString.substring(autoTaxString.indexOf("：") + 1, autoTaxString.indexOf("元"));
        double autoTax = NumberUtils.toFinancialDouble(s);
        orderInsuranceViewModel.setAutoTax(autoTax);

        return orderInsuranceViewModel;
    }

    private OrderInsuranceViewModel setExtraCommercial(PDFTextStripperByArea stripper) {
        OrderInsuranceViewModel orderInsuranceViewModel = new OrderInsuranceViewModel();
        orderInsuranceViewModel.setDamageAmount(Double.parseDouble(getStripperValue("damageAmount", stripper)));
        orderInsuranceViewModel.setDamagePremium(Double.parseDouble(getStripperValue("damagePremium", stripper)));
        orderInsuranceViewModel.setThirdPartyAmount(Double.parseDouble(getStripperValue("thirdPartyAmount", stripper)));
        orderInsuranceViewModel.setThirdPartyPremium(Double.parseDouble(getStripperValue("thirdPartyPremium", stripper)));

        double driverAmountMoney = NumberUtils.toDouble(getStripperValue("driverAmount", stripper).substring(0, getStripperValue("driverAmount", stripper).indexOf("/")));
        orderInsuranceViewModel.setDriverAmount(driverAmountMoney);
        orderInsuranceViewModel.setDriverPremium(Double.parseDouble(getStripperValue("driverPremium", stripper)));

        double passengerAmountSingle = NumberUtils.toDouble(getStripperValue("passengerAmount", stripper).substring(0, getStripperValue("driverAmount", stripper).indexOf("/")));
        double passengerAmountNum = NumberUtils.toDouble(getStripperValue("passengerAmount", stripper).substring(getStripperValue("passengerAmount", stripper).indexOf("*") + 1, getStripperValue("driverAmount", stripper).length() - 1));
        double passengerAmount = passengerAmountNum * passengerAmountSingle;
        orderInsuranceViewModel.setPassengerAmount(passengerAmountSingle);
        orderInsuranceViewModel.setPassengerPremium(Double.parseDouble(getStripperValue("passengerPremium", stripper)));
        orderInsuranceViewModel.setGlassPremium(Double.parseDouble(getStripperValue("glassPremium", stripper)));
        orderInsuranceViewModel.setApplicantName(getStripperValue("applicantName", stripper));
        orderInsuranceViewModel.setCommercialPremium(NumberUtils.toFinancialDouble(getStripperValue("commercialPremium", stripper)));
        orderInsuranceViewModel.setCommercialPolicyNo(getStripperValue("policyNo", stripper));//报价商业险保单号

        String dateStr = getStripperValue("date", stripper);
        String[] dateArr = dateStr.split("起");
        String commercialEffectiveDate = dateArr[0].substring(1, 11).replaceAll("[^(\\\\u4e00-\\\\u9fa5)]", "-");
        String compulsoryExpireDate = dateArr[1].substring(1, 11).replaceAll("[^(\\\\u4e00-\\\\u9fa5)]", "-");
        String commercialEffectiveHout = StringUtil.convertNumber(dateArr[0].substring(12));
        String commercialExpireHour = StringUtil.convertNumber(dateArr[1].substring(12));
        orderInsuranceViewModel.setCommercialEffectiveDate(commercialEffectiveDate);
        orderInsuranceViewModel.setCommercialEffectiveHour(Integer.parseInt(commercialEffectiveHout));
        orderInsuranceViewModel.setCommercialExpireDate(compulsoryExpireDate);
        orderInsuranceViewModel.setCommercialExpireHour(Integer.parseInt(commercialExpireHour));

        String glassTypeString = getStripperValue("glassType", stripper).trim();
        String glassType = glassTypeString.substring(glassTypeString.indexOf("（") + 1, glassTypeString.indexOf("）"));
        Long glassTypeLong = glassType.equals("国产") ? GlassType.Enum.DOMESTIC_1.getId() : GlassType.Enum.IMPORT_2.getId();
        orderInsuranceViewModel.setGlassType(glassTypeLong);
        orderInsuranceViewModel.setIop(NumberUtils.toFinancialDouble(getStripperValue("iop", stripper)));

        return orderInsuranceViewModel;
    }

    private PDFTextStripperByArea getCompulsoryStripper(PDFTextStripperByArea stripper) {

        stripper.addRegion("policyNo", new Rectangle(434, 142, 120, 12));//保险单号
        stripper.addRegion("insuredName", new Rectangle(84, 156, 70, 15));//被保险人

        stripper.addRegion("insuredIdNo", new Rectangle(182, 178, 100, 30));//被保险人身份证号码

        stripper.addRegion("area", new Rectangle(84, 191, 60, 15));//投保区域
        stripper.addRegion("licensePlateNo", new Rectangle(110, 208, 70, 16));//号牌号码

        stripper.addRegion("engineNo", new Rectangle(110, 225, 70, 16));//发动机号
        stripper.addRegion("vinNo", new Rectangle(330, 225, 100, 16));//车架号

        stripper.addRegion("brand", new Rectangle(110, 241, 120, 16));//厂牌型号
        stripper.addRegion("enrollDate", new Rectangle(488, 259, 100, 14));//初次登记日期


        stripper.addRegion("discount", new Rectangle(504, 328, 64, 14));//费率浮动
        stripper.addRegion("enrollDate", new Rectangle(354, 342, 34, 14));//初次登记日期

        stripper.addRegion("date", new Rectangle(102, 360, 250, 14));//保险期间

        //        stripper.addRegion("compulsoryPremium", new Rectangle(23, 344, 550, 14));//保险费合计

        stripper.addRegion("autoTax", new Rectangle(476, 429, 100, 16));


        return stripper;
    }

    private PDFTextStripperByArea getCommercialStripper(PDFTextStripperByArea stripper) {

        stripper.addRegion("policyNo", new Rectangle(425, 138, 90, 12));//保险单号
        stripper.addRegion("insuredName", new Rectangle(92, 168, 40, 14));//被保险人

        stripper.addRegion("licensePlateNo", new Rectangle(138, 184, 50, 14));//号 牌 号 码
        stripper.addRegion("brand", new Rectangle(336, 184, 70, 14));//厂 牌 型 号

        stripper.addRegion("vinNo", new Rectangle(138, 198, 150, 14));//VIN码/车架号
        stripper.addRegion("engineNo", new Rectangle(404, 198, 50, 14));//发 动 机 号

        stripper.addRegion("num", new Rectangle(138, 215, 96, 14));//核 定 载 客
        stripper.addRegion("weight", new Rectangle(317, 215, 50, 14));//核定载质量
        stripper.addRegion("enrollDate", new Rectangle(476, 215, 70, 14));//初次登记日期

        stripper.addRegion("usingType", new Rectangle(138, 228, 96, 14));//使用性质
        stripper.addRegion("mileage", new Rectangle(317, 228, 50, 14));//年平均行驶里程
        stripper.addRegion("vehicle", new Rectangle(476, 228, 70, 14));//机动车种类
        //------
        //432 机动车损失保险
        stripper.addRegion("isDamageIop", new Rectangle(252, 258, 54, 14));//不计免赔
        stripper.addRegion("damageDiscount", new Rectangle(320, 258, 73, 14));//费率浮动（+/-）
        stripper.addRegion("damageAmount", new Rectangle(396, 258, 90, 14));//保险金额/责任限额
        stripper.addRegion("damagePremium", new Rectangle(492, 258, 74, 14));//保险费（元）

        //458 第三者责任保险
        stripper.addRegion("isThirdPartyIop", new Rectangle(252, 276, 54, 14));//不计免赔
        stripper.addRegion("thirdPartyDiscount", new Rectangle(320, 276, 73, 14));//费率浮动（+/-）
        stripper.addRegion("thirdPartyAmount", new Rectangle(396, 276, 90, 14));//保险金额/责任限额
        stripper.addRegion("thirdPartyPremium", new Rectangle(492, 276, 74, 14));//保险费（元）

        //485 车上人员责任险（司机）
        stripper.addRegion("isDriverIop", new Rectangle(252, 291, 54, 14));//不计免赔
        stripper.addRegion("driverDiscount", new Rectangle(320, 291, 73, 14));//费率浮动（+/-）
        stripper.addRegion("driverAmount", new Rectangle(396, 291, 90, 14));//保险金额/责任限额
        stripper.addRegion("driverPremium", new Rectangle(492, 291, 74, 14));//保险费（元）

        //511 车上人员责任险（乘客）
        stripper.addRegion("isPassengerIop", new Rectangle(252, 307, 54, 14));//不计免赔
        stripper.addRegion("passengerDiscount", new Rectangle(320, 307, 73, 14));//费率浮动（+/-）
        stripper.addRegion("passengerAmount", new Rectangle(396, 307, 90, 14));//保险金额/责任限额
        stripper.addRegion("passengerPremium", new Rectangle(492, 307, 74, 14));//保险费（元）

        //537 玻璃单独破碎险（国产）
        stripper.addRegion("isGlassIop", new Rectangle(252, 323, 54, 14));//不计免赔
        stripper.addRegion("glassDiscount", new Rectangle(320, 323, 73, 14));//费率浮动（+/-）
        stripper.addRegion("glassAmount", new Rectangle(396, 323, 90, 14));//保险金额/责任限额
        stripper.addRegion("glassPremium", new Rectangle(492, 323, 74, 14));//保险费（元）
        stripper.addRegion("glassType", new Rectangle(37, 322, 100, 14));//玻璃类型

        //563 不计免赔率
        stripper.addRegion("discount", new Rectangle(320, 338, 73, 14));
        stripper.addRegion("iop", new Rectangle(492, 338, 74, 14));
        //-----

        stripper.addRegion("applicantName", new Rectangle(108, 368, 50, 14));//本保单投保人为
        stripper.addRegion("commercialPremium", new Rectangle(444, 384, 60, 14));//保险费合计
        stripper.addRegion("date", new Rectangle(72, 396, 259, 14));//保险期间
        return stripper;
    }


    public OrderInsuranceViewModel getInsuranceByPdfFile(File file, InsuranceType type) {
        try {
            PDDocument document = PDDocument.load(file);
            PDFTextStripperByArea stripper = new PDFTextStripperByArea();
            stripper.setSortByPosition(true);
            if (type.getId().equals(InsuranceType.Enum.COMPULSORY_2.getId())) {
                stripper = getCompulsoryStripper(stripper);
            } else {
                stripper = getCommercialStripper(stripper);
            }
            PDPage page = document.getPage(0);
            stripper.extractRegions(page);
            OrderInsuranceViewModel orderInsuranceViewModel;
            if (type.getId().equals(InsuranceType.Enum.COMPULSORY_2.getId())) {
                orderInsuranceViewModel = setExtraCompulsory(stripper);
            } else {
                orderInsuranceViewModel = setExtraCommercial(stripper);
            }
            orderInsuranceViewModel.setInsuredName(getStripperValue("insuredName", stripper));
            orderInsuranceViewModel.setLicensePlateNo(getStripperValue("licensePlateNo", stripper));
            orderInsuranceViewModel.setEngineNo(getStripperValue("engineNo", stripper));
            orderInsuranceViewModel.setEnrollDate(getStripperValue("enrollDate", stripper));
            orderInsuranceViewModel.setVinNo(getStripperValue("vinNo", stripper));
            orderInsuranceViewModel.setDiscount(Double.parseDouble(getStripperValue("discount", stripper)));
            orderInsuranceViewModel.setBrand(getStripperValue("brand", stripper));
            document.close();
            return orderInsuranceViewModel;
        } catch (IOException e) {
            logger.error("a file import error filename:" + file.getName() + " filetype:" + type.getName());
        }
        return null;

    }

    private String getStripperValue(String key, PDFTextStripperByArea stripper) {
        return StringUtil.trim(stripper.getTextForRegion(key).toString());
    }
}
