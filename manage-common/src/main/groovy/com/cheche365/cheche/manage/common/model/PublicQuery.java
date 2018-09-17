package com.cheche365.cheche.manage.common.model;

import java.util.List;

/**
 * Created by Administrator on 2017/3/14.
 */
public class PublicQuery {
    private Integer pageSize;
    private Integer currentPage;
    private Integer draw;
    private String keyword;
    private Integer keyType;
    private String qrCodeType;
    private List<InternalUserDataPermission> permissions;

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getDraw() {
        return draw;
    }

    public void setDraw(Integer draw) {
        this.draw = draw;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Integer getKeyType() {
        return keyType;
    }

    public void setKeyType(Integer keyType) {
        this.keyType = keyType;
    }

    public String getQrCodeType() {
        return qrCodeType;
    }

    public void setQrCodeType(String qrCodeType) {
        this.qrCodeType = qrCodeType;
    }

    public List<InternalUserDataPermission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<InternalUserDataPermission> permissions) {
        this.permissions = permissions;
    }
}
