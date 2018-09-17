package com.cheche365.cheche.wechat.message.json;

import java.util.List;

/**
 * Created by liqiang on 3/27/15.
 */
public class CustomerMessage {

    private String touser;
    private String msgtype;

    private Text text;
    private Music music;
    private Video video;
    private Voice voice;
    private News news;
    private Image image;
    private CustomerService customerService;


    public Text getText() {
        return text;
    }

    public void setText(Text text) {
        msgtype = "text";
        this.text = text;
    }

    public Music getMusic() {
        return music;
    }

    public void setMusic(Music music) {
        msgtype = "music";
        this.music = music;
    }

    public Video getVideo() {
        return video;
    }

    public void setVideo(Video video) {
        msgtype = "video";
        this.video = video;
    }

    public Voice getVoice() {
        return voice;
    }

    public void setVoice(Voice voice) {
        msgtype = "voice";
        this.voice = voice;
    }

    public News getNews() {
        return news;
    }

    public void setNews(News news) {
        msgtype = "news";
        this.news = news;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        msgtype = "image";
        this.image = image;
    }

    public CustomerService getCustomerservice() {
        return customerService;
    }

    public void setCustomerservice(CustomerService customerService) {
        this.customerService = customerService;
    }

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public String getTouser() {
        return touser;
    }

    public void setTouser(String touser) {
        this.touser = touser;
    }


    public static class CustomerService {
        private String kf_account;

        public String getKf_account() {
            return kf_account;
        }

        public void setKf_account(String kf_account) {
            this.kf_account = kf_account;
        }
    }

    public static class Music {
        private String title;
        private String description;
        private String musicurl;
        private String hqmusicurl;
        private String thumb_media_id;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getMusicurl() {
            return musicurl;
        }

        public void setMusicurl(String musicurl) {
            this.musicurl = musicurl;
        }

        public String getHqmusicurl() {
            return hqmusicurl;
        }

        public void setHqmusicurl(String hqmusicurl) {
            this.hqmusicurl = hqmusicurl;
        }

        public String getThumb_media_id() {
            return thumb_media_id;
        }

        public void setThumb_media_id(String thumb_media_id) {
            this.thumb_media_id = thumb_media_id;
        }
    }

    public static class Image {
        String media_id;

        public String getMedia_id() {
            return media_id;
        }

        public void setMedia_id(String media_id) {
            this.media_id = media_id;
        }
    }

    public static class Text {
        private String content;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    public static class Video {
        private String media_id;
        private String thumb_media_id;
        private String title;
        private String description;

        public String getMedia_id() {
            return media_id;
        }

        public void setMedia_id(String media_id) {
            this.media_id = media_id;
        }

        public String getThumb_media_id() {
            return thumb_media_id;
        }

        public void setThumb_media_id(String thumb_media_id) {
            this.thumb_media_id = thumb_media_id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    public static class Voice {
        private String media_id;

        public String getMedia_id() {
            return media_id;
        }

        public void setMedia_id(String media_id) {
            this.media_id = media_id;
        }
    }

    public static class News {
        private List<Article> articles;

        public List<Article> getArticles() {
            return articles;
        }

        public void setArticles(List<Article> articles) {
            this.articles = articles;
        }
    }


    public static class Article {
        private String title;
        private String description;
        private String url;
        private String picurl;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getPicurl() {
            return picurl;
        }

        public void setPicurl(String picurl) {
            this.picurl = picurl;
        }
    }
}
