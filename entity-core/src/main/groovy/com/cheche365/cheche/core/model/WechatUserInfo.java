package com.cheche365.cheche.core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liqiang on 3/24/15.
 */

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class WechatUserInfo {

    private Long id;
    private User user;
    private String nickname;
    private Integer sex;
    private String language;
    private String city;
    private String province;
    private String country;
    private String headimgurl;

    private String unionid;

    private EmojiFilter filter = new EmojiFilter();


    @Column(name="nick_name")
    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = filter == null ? nickname : filter.filter(nickname);
    }

    @Column
    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    @Column
    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Column
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Column
    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    @Column
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Column(name="head_img_url")
    public String getHeadimgurl() {
        return headimgurl;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @OneToOne
    @JoinColumn
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Column(name = "union_id",columnDefinition = "VARCHAR(100)")
    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }
}

class EmojiFilter{
    private static Pattern unicodeOutliers = Pattern.compile("[\\ud83c\\udc00-\\ud83c\\udfff]|[\\ud83d\\udc00-\\ud83d\\udfff]|[\\u2600-\\u27ff]",
        Pattern.UNICODE_CASE | Pattern.CANON_EQ
            | Pattern.CASE_INSENSITIVE);

    public String filter(String string){
        if (StringUtils.isBlank(string)){
            return string;
        }
        Matcher unicodeOutlierMatcher = unicodeOutliers.matcher(string);
        return unicodeOutlierMatcher.replaceAll(" ");
    }
}
