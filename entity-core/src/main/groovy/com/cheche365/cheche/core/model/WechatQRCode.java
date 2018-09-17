package com.cheche365.cheche.core.model;

import javax.persistence.*;
import java.util.Date;

/**d
 * Created by liqiang on 7/9/15.
 */
@Entity
@Table(name="wechat_qrcode")
public class WechatQRCode {

    private long id;
    private long sceneId;
    private String sceneStr;
    private String actionName;
    private String ticket;
    private String url;
    private String imageURL;
    private Date createTime;
    private long expireSeconds;
    private String target;
    private String comments;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(columnDefinition = "bigint(20)")
    public long getSceneId() {
        return sceneId;
    }

    public void setSceneId(long sceneId) {
        this.sceneId = sceneId;
    }

    @Column(columnDefinition = "varchar(10)")
    public String getSceneStr() {
        return sceneStr;
    }

    public void setSceneStr(String sceneStr) {
        this.sceneStr = sceneStr;
    }

    @Column(columnDefinition = "varchar(20)")
    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        if (ActionName.valueOf(actionName) == null){
            throw new IllegalArgumentException("invalid action name : " + actionName);
        }
        this.actionName = actionName;
    }

    @Column(columnDefinition = "varchar(64)")
    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    @Column(columnDefinition = "varchar(200)")
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Column(name="image_url", columnDefinition = "varchar(200)")
    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    @Column(columnDefinition = "DATE")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Column(columnDefinition = "int(10)")
    public long getExpireSeconds() {
        return expireSeconds;
    }

    public void setExpireSeconds(long expireSeconds) {
        this.expireSeconds = expireSeconds;
    }

    @Column(columnDefinition = "varchar(20)")
    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    @Column(columnDefinition = "varchar(200)")
    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }


    public enum ActionName {
        QR_SCENE,
        QR_LIMIT_SCENE,
        QR_LIMIT_STR_SCENE
    }
}
