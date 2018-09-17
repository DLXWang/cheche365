package com.cheche365.cheche.scheduletask.util.banner;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xu.yelong on 2016/8/12.
 */
public class MarketingBanner {
    private Integer height;
    private Integer width;
    private Color background;
    private Integer border;
    private Color borderColor;
    private String target;
    private List<BannerElement> bannerElementList =new ArrayList<>();

    /**
     *
     * @param height 高度
     * @param width  宽度
     * @param background 背景色
     * @param border 边框
     * @param borderColor 边框颜色
     * @param target 输出地址
     */
    public MarketingBanner(Integer height,Integer width,Color background,Integer border,Color borderColor,String target){
        this.height=height;
        this.width=width;
        this.background=background;
        this.border=border;
        this.borderColor=borderColor;
        this.target=target;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getBorder() {
        return border;
    }

    public void setBorder(Integer border) {
        this.border = border;
    }

    public Color getBackground() {
        return background;
    }

    public void setBackground(Color background) {
        this.background = background;
    }

    public Color getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
    }

    public List<BannerElement> getBannerElementList() {
        return bannerElementList;
    }

    public void addElement(BannerElement element){
        this.getBannerElementList().add(element);
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}
