package com.cheche365.cheche.core.model;

import com.cheche365.cheche.core.exception.BusinessException;

import javax.persistence.*;

/**
 * Created by mahong on 2015/6/12.
 */
@Entity
public class SysVersion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "channel", foreignKey = @ForeignKey(name = "FK_SYS_VERSION_REF_CHANNEL", foreignKeyDefinition = "FOREIGN KEY (channel) REFERENCES channel(id)"))
    private Channel channel;

    @Column(name = "version", columnDefinition = "VARCHAR(20)")
    private String latestVersion;

    @Transient
    private boolean needUpdate; // 是否需要升级 0:否 1:是
    @Transient
    private Boolean isNeedUpdate; // 是否需要升级，安卓2.2.3版本字段写错，导致2.2.4强升不成功
    @Transient
    private String downloadUrl; // 下载链接，android不为空

    @Column(columnDefinition = "VARCHAR(20)")
    private String updateAdvice; // 升级建议 1. required 2. option

    @Column(columnDefinition = "VARCHAR(200)")
    private String reason;

    public String getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(String latestVersion) {
        this.latestVersion = latestVersion;
    }

    public boolean isNeedUpdate() {
        return needUpdate;
    }

    public void setNeedUpdate(boolean needUpdate) {
        this.needUpdate = needUpdate;
    }

    public Boolean getIsNeedUpdate() {
        return this.isNeedUpdate;
    }

    public void setIsNeedUpdate(Boolean isNeedUpdate) {
        this.isNeedUpdate = isNeedUpdate;
    }

    public String getUpdateAdvice() {
        return updateAdvice;
    }

    public void setUpdateAdvice(String updateAdvice) {
        this.updateAdvice = updateAdvice;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public static boolean checkVersionStyle(String version) {
        String regex = "^\\d+\\.\\d+\\.\\d+$|^1\\.0$";
        return version.matches(regex);
    }

    public static int compareVersion(String latestVersion, String version) {
        Integer latest = formatVersion(latestVersion);
        Integer current = formatVersion(version.equals("1.0") ? "1.0.0" : version);
        return latest.compareTo(current);
    }

    public static Integer formatVersion(String version){

        if(!checkVersionStyle(version)){
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "版本号格式错误");
        }

        return Integer.parseInt(version.replaceAll("\\.", ""));
    }

}
