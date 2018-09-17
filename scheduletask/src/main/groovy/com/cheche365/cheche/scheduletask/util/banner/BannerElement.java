package com.cheche365.cheche.scheduletask.util.banner;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by xu.yelong on 2016/8/12.
 */
public class BannerElement {
    private Integer height;
    private Integer width;
    private Integer x;
    private Integer y;

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

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public class BannerImage extends BannerElement{
        private BufferedImage bufferedImage;

        public BufferedImage getBufferedImage() {
            return bufferedImage;
        }

        public void setBufferedImage(BufferedImage bufferedImage) {
            this.bufferedImage = bufferedImage;
        }
    }

    public class BannerFont extends BannerElement{
        private Font font;
        private Color color;
        private String text;

        public BannerFont(){}

        public BannerFont(Font font,Color color,String text){
            this.font=font;
            this.color=color;
            this.text=text;
        }

        public Font getFont() {
            return font;
        }

        public void setFont(Font font) {
            this.font = font;
        }

        public Color getColor() {
            return color;
        }

        public void setColor(Color color) {
            this.color = color;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

}
