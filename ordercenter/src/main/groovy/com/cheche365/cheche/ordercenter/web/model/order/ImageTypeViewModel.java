package com.cheche365.cheche.ordercenter.web.model.order;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/18.
 */
public class ImageTypeViewModel {
    @JsonProperty
    private String name;//种类名称

    @JsonProperty
    private List<ImageInfo> imageList;//该种类所有照片

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ImageTypeViewModel() {
    }

    public ImageTypeViewModel(String name) {
        this.name = name;
        this.imageList = getImageList();
    }

    public List<ImageInfo> getImageList() {
        List<ImageInfo> imageInfoList = new ArrayList<>();
        imageInfoList.add(new ImageInfo("vvvvv","vvvvv",true,"https://res.cheche365.com/res/web/imgs/home/cheche-app.jpg"));
        imageInfoList.add(new ImageInfo("fffff","fffff",true,"https://res.cheche365.com/res/web/imgs/home/cheche-app.jpg"));
        imageInfoList.add(new ImageInfo("eeeee","eeeee",true,"https://res.cheche365.com/res/web/imgs/home/cheche-app.jpg"));
        return imageInfoList;
    }

    public void setImageList(List<ImageInfo> imageList) {
        this.imageList = imageList;
    }

    static class ImageInfo {

        //照片名称
        @JsonProperty
        private String name;

        //照片描述
        @JsonProperty
        private String desc;

        //审核状态
        @JsonProperty
        private Boolean checkStatus;

        //图片地址
        @JsonProperty
        private String addressUrl;

        public ImageInfo() {
        }

        public ImageInfo(String name, String desc, Boolean checkStatus, String addressUrl) {
            this.name = name;
            this.desc = desc;
            this.checkStatus = checkStatus;
            this.addressUrl = addressUrl;
        }
    }
}
