package com.cheche365.cheche.core.model

import com.cheche365.cheche.core.context.ApplicationContextHolder
import com.cheche365.cheche.core.service.IResourceService
import org.springframework.context.ApplicationContext

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Transient

/**
 * Created by wangjiahuan on 2016/12/22 0022.
 */
@Entity
class Bank {

    private Long id
    private String code
    private String name
    private String shortName
    private String description
    private String logoUrl
    private String backGroundImg


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long getId() {
        return id
    }

    void setId(Long id) {
        this.id = id
    }

    @Column(columnDefinition = "VARCHAR(10)")
    String getCode() {
        return code
    }

    void setCode(String code) {
        this.code = code
    }

    @Column(columnDefinition = "VARCHAR(45)")
    String getName() {
        return name
    }

    void setName(String name) {
        this.name = name
    }

    @Column(columnDefinition = "VARCHAR(50)")
    String getShortName() {
        return shortName
    }

    void setShortName(String shortName) {
        this.shortName = shortName
    }

    @Column(columnDefinition = "VARCHAR(4500)")
    String getDescription() {
        return description
    }

    void setDescription(String description) {
        this.description = description
    }

    void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl
    }

    @Transient
    String getBackGroundImg() {
        return Bank.Enum.bg_url_prefix + this.shortName + ".png"
    }

    void setBackGroundImg(String backGroundImg) {
        this.backGroundImg = backGroundImg
    }

    static class Enum{
        public static String logo_url_prefix
        public static String bg_url_prefix
        static {
            ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext()
            IResourceService resourceService = applicationContext.getBean(IResourceService.class)
            logo_url_prefix = resourceService.absoluteUrl(resourceService.getResourceAbsolutePath(resourceService.getProperties().getBankLogoPath()), "")
            bg_url_prefix = resourceService.absoluteUrl(resourceService.getResourceAbsolutePath(resourceService.getProperties().getBankBgPath()), "")
        }

    }

    @Transient
    String getLogoUrl() {
        return Bank.Enum.logo_url_prefix + this.logoUrl + ".png"
    }

    void assembleLogoUrl(Channel channel){
        if (channel.isLevelAgent()) {
            this.setLogoUrl(this.getShortName() + "_A")
        } else {
            this.setLogoUrl(this.getShortName())
        }
    }
}
