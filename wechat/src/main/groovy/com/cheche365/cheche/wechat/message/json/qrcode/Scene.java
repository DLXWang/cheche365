package com.cheche365.cheche.wechat.message.json.qrcode;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by liqiang on 7/15/15.
 */
public class Scene {
    @JsonProperty("scene_id")
    private long sceneId;
    @JsonProperty("scene_str")
    private String sceneStr;

    public long getSceneId() {
        return sceneId;
    }

    public void setSceneId(long sceneId) {
        this.sceneId = sceneId;
    }

    public String getSceneStr() {
        return sceneStr;
    }

    public void setSceneStr(String sceneStr) {
        this.sceneStr = sceneStr;
    }
}
