package com.cheche365.cheche.operationcenter.web.model.marketing;

/**
 * Created by chenxiangyin on 2017/9/4.
 */
public class AccessDetailData {

    private Long id;//活动id
    private String sourceId;
    private String source;
    private String mobileNum;
    private String url;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getMobileNum() {
        return mobileNum;
    }

    public void setMobileNum(String mobileNum) {
        this.mobileNum = mobileNum;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public enum AccessDetailSource {
        SOURCE_BAIDU(1, "百度"), SOURCE_360(2, "360"),SOURCE_SOUGOU(3,"搜狗"),SOURCE_SHENMA(4,"神马"),SOURCE_GOOGLE(5,"谷歌"),SOURCE_BING(6,"必应"),SOURCE_YAHOO(7,"雅虎"),SOURCE_SOSO(8,"搜搜");
        private Integer index;
        private String name;
        AccessDetailSource(Integer index, String name) {
            this.index = index;
            this.name = name;
        }
        public Integer getIndex() {
            return index;
        }
        public String getName() {
            return name;
        }
        public static String getName(int index) {
            for (AccessDetailSource values : AccessDetailSource.values()) {
                if (values.getIndex() == index) {
                    return values.name;
                }
            }
            return null;
        }
    }
}
