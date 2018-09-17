package com.cheche365.cheche.operationcenter.web.model.marketing;

/**
 * Created by yinJianBin on 2017/4/21.
 */
public class GiftRequestParams {

    private String giftName;            //礼品名称
    private String description;         //描述
    private String giftType;            //礼品类型
    private Integer giftStatus;          //礼品状态
    private String useType;             //使用类型
    private Integer deliveryFlag;       //是否配送

    private Integer currentPage;
    private Integer pageSize;
    private Integer draw;               //datatables渲染用

    public String getGiftName() {
        return giftName;
    }

    public void setGiftName(String giftName) {
        this.giftName = giftName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGiftType() {
        return giftType;
    }

    public void setGiftType(String giftType) {
        this.giftType = giftType;
    }

    public Integer getGiftStatus() {
        return giftStatus;
    }

    public void setGiftStatus(Integer giftStatus) {
        this.giftStatus = giftStatus;
    }

    public Integer getDraw() {
        return draw;
    }

    public void setDraw(Integer draw) {
        this.draw = draw;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getUseType() {
        return useType;
    }

    public void setUseType(String useType) {
        this.useType = useType;
    }

    public Integer getDeliveryFlag() {
        return deliveryFlag;
    }

    public void setDeliveryFlag(Integer deliveryFlag) {
        this.deliveryFlag = deliveryFlag;
    }

    @Override
    public String toString() {
        return new StringBuilder("{").append("giftName:").append(this.getGiftName()).append("-giftType:").append(this.getGiftType()).append("-giftStatus:").append(this.getGiftStatus()).append("-deliveryFlag").append(this.getDeliveryFlag()).append("}").toString();
    }

}
