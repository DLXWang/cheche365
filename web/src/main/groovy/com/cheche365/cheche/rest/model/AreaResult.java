package com.cheche365.cheche.rest.model;

import com.cheche365.cheche.core.model.Area;

import java.util.List;

/**
 * Created by mahong on 2015/6/15.
 */
public class AreaResult {
    private List<Area> areas;
    private Long lastModified;
    private boolean needUpdate;

    public List<Area> getAreas() {
        return areas;
    }

    public void setAreas(List<Area> areas) {
        this.areas = areas;
    }

    public Long getLastModified() {
        return lastModified;
    }

    public void setLastModified(Long lastModified) {
        this.lastModified = lastModified;
    }

    public boolean isNeedUpdate() {
        return needUpdate;
    }

    public void setNeedUpdate(boolean needUpdate) {
        this.needUpdate = needUpdate;
    }
}
