package com.cheche365.cheche.scheduletask.util.banner;


/**
 * Created by xu.yelong on 2016/8/21.
 */
public class ActivityPageJsonObject {
    private String code;
    private Share share;
    private Info info;
    private Boolean thirdPartner;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Share getShare() {
        return share;
    }

    public void setShare(Share share) {
        this.share = share;
    }

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }

    public Boolean getThirdPartner() {
        return thirdPartner;
    }

    public void setThirdPartner(Boolean thirdPartner) {
        this.thirdPartner = thirdPartner;
    }

    public class Share{
        private String wechatTitle;
        private String wechatSubTitle;
        private String alipayTitle;
        private String alipaySubTitle;
        private String imgUrl;

        public String getWechatTitle() {
            return wechatTitle;
        }

        public void setWechatTitle(String wechatTitle) {
            this.wechatTitle = wechatTitle;
        }

        public String getWechatSubTitle() {
            return wechatSubTitle;
        }

        public void setWechatSubTitle(String wechatSubTitle) {
            this.wechatSubTitle = wechatSubTitle;
        }

        public String getAlipayTitle() {
            return alipayTitle;
        }

        public void setAlipayTitle(String alipayTitle) {
            this.alipayTitle = alipayTitle;
        }

        public String getAlipaySubTitle() {
            return alipaySubTitle;
        }

        public void setAlipaySubTitle(String alipaySubTitle) {
            this.alipaySubTitle = alipaySubTitle;
        }

        public String getImgUrl() {
            return imgUrl;
        }

        public void setImgUrl(String imgUrl) {
            this.imgUrl = imgUrl;
        }
    }

    public class Info{
        private String title;
        private String subTitle;
        private String topImage;
        private Rule[] rules;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getSubTitle() {
            return subTitle;
        }

        public void setSubTitle(String subTitle) {
            this.subTitle = subTitle;
        }

        public Rule[] getRules() {
            return rules;
        }

        public void setRules(Rule[] rules) {
            this.rules = rules;
        }

        public String getTopImage() {
            return topImage;
        }

        public void setTopImage(String topImage) {
            this.topImage = topImage;
        }
    }

    public class Rule{
        private String des;

        public Rule(String des){
            this.des=des;
        }

        public String getDes() {
            return des;
        }

        public void setDes(String des) {
            this.des = des;
        }
    }

}


