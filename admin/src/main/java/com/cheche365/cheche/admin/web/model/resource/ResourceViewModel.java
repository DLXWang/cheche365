package com.cheche365.cheche.admin.web.model.resource;

/**
 * Created by wangfei on 2015/9/11.
 */
public class ResourceViewModel {
    private Long id;
    private String name;
    private Integer level;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }
}
