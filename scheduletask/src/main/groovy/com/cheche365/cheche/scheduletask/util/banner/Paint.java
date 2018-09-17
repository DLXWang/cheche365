package com.cheche365.cheche.scheduletask.util.banner;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by xu.yelong on 2016/8/12.
 */
public class Paint{
    private static BufferedImage image;

    private static Graphics2D graphics2D;

    public static void printBackground(MarketingBanner marketingBanner){
        image = new BufferedImage(marketingBanner.getWidth(), marketingBanner.getHeight(), BufferedImage.TYPE_INT_RGB);
        graphics2D = image.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setBackground(marketingBanner.getBackground());
        graphics2D.fillRect(0,0,marketingBanner.getWidth(), marketingBanner.getHeight());
    }

    public static BufferedImage dispose(){
        graphics2D.dispose();
        return image;
    }

    enum PaintTools{
        DRAW_FRONT(){
            public void renderer(BannerElement bannerElement){
                BannerElement.BannerFont bannerFont=(BannerElement.BannerFont)bannerElement;
                graphics2D.setFont(bannerFont.getFont());
                graphics2D.setColor(bannerFont.getColor());
                graphics2D.drawString(bannerFont.getText(),bannerFont.getX(),bannerFont.getY());
            }
        },
        DRAW_IMAGE(){
            public void renderer(BannerElement bannerElement) {
                BannerElement.BannerImage bannerImage=(BannerElement.BannerImage)bannerElement;
                ImageIcon imageIcon=new ImageIcon(bannerImage.getBufferedImage());
                imageIcon.setImage(imageIcon.getImage().getScaledInstance(bannerImage.getWidth(), bannerImage.getHeight(), 1));
                graphics2D.drawImage(imageIcon.getImage(),bannerImage.getX(),bannerImage.getY(),null);
            }
        };

        public abstract void renderer(BannerElement bannerElement);
    }
}



