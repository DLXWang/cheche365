package com.cheche365.cheche.scheduletask.util.banner;

import org.apache.commons.collections.CollectionUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by xu.yelong on 2016/8/12.
 */
public class MarketingBannerUtil {

    private static final Integer X_DENSITY = 300;//水平分辨率

    private static final Integer Y_DENSITY = 300;//垂直分辨率

    private static final Integer DENSITY_UNIT = 1;//像素尺寸单位

    public static void draw(MarketingBanner marketingBanner) throws IOException {
        if (CollectionUtils.isEmpty(marketingBanner.getBannerElementList())) {
            return;
        }
        Paint.printBackground(marketingBanner);
        marketingBanner.getBannerElementList().forEach(bannerElement -> {
            if (BannerElement.BannerImage.class.isInstance(bannerElement)) {
                Paint.PaintTools.DRAW_IMAGE.renderer(bannerElement);
            } else if (BannerElement.BannerFont.class.isInstance(bannerElement)) {
                Paint.PaintTools.DRAW_FRONT.renderer(bannerElement);
            }
        });
        BufferedImage image = Paint.dispose();
        output(marketingBanner.getTarget(), image);
    }

    public static void output(String fileLocation, BufferedImage image) throws IOException {
        ImageIO.write(image, "jpg", new File(fileLocation));
//        FileOutputStream fos = new FileOutputStream(fileLocation);
//        BufferedOutputStream bos = new BufferedOutputStream(fos);
//        JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(bos);
//        JPEGEncodeParam jpegEncodeParam = encoder.getDefaultJPEGEncodeParam(image);
//        jpegEncodeParam.setDensityUnit(DENSITY_UNIT);
//        jpegEncodeParam.setXDensity(X_DENSITY);
//        jpegEncodeParam.setYDensity(Y_DENSITY);
//        encoder.encode(image, jpegEncodeParam);
//        bos.close();
    }

//    public static void main(String[] args) {
//        MarketingBanner marketingBanner = new MarketingBanner(220, 750, Color.WHITE, 0, Color.WHITE, "D:/banner/banner.jpg");
//        BannerElement bannerElement=new BannerElement();
//        BannerElement.BannerFont mainTitle=bannerElement.new BannerFont();
//        mainTitle.setText("国寿财险优惠购 买就送加油卡");
//        mainTitle.setX(32);
//        mainTitle.setY(126);
//        mainTitle.setColor(new Color(51, 51, 51));
//        mainTitle.setFont(new Font("黑体", 0, 30));
//
//        BannerElement.BannerFont subTitle=bannerElement.new BannerFont();
//        subTitle.setText("100元加油卡任性送");
//        subTitle.setX(32);
//        subTitle.setY(170);
//        subTitle.setColor(new Color(153, 153, 153));
//        subTitle.setFont(new Font("黑体", 0, 26));
//
//        BannerElement.BannerImage logo=bannerElement.new BannerImage();
//        logo.setWidth(188);
//        logo.setHeight(48);
//        logo.setX(32);
//        logo.setY(24);
//        try {
//            logo.setBufferedImage(ImageIO.read(new FileInputStream("D:/banner/renshou.png")));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        BannerElement.BannerImage image=bannerElement.new BannerImage();
//        image.setWidth(268);
//        image.setHeight(220);
//        image.setX(482);
//        image.setY(0);
//        try {
//            image.setBufferedImage(ImageIO.read(new FileInputStream("D:/banner/daiyan.png")));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        marketingBanner.addElement(logo);
//        marketingBanner.addElement(image);
//        marketingBanner.addElement(mainTitle);
//        marketingBanner.addElement(subTitle);
//        try {
//            MarketingBannerUtil.draw(marketingBanner);
//        } catch (IOException e) {
//            logger.error("create image error ,path ->{}", marketingBanner.getTarget(), e);
//        }
//
//    }
}
