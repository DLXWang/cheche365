package com.cheche365.cheche.ordercenter.model;


/**
 * Created by wangfei on 2015/12/16.
 */
public class PublicQuery extends com.cheche365.cheche.manage.common.model.PublicQuery{
    private Integer pageSize;
    private Integer currentPage;
    private Integer draw;
    private String keyword;
    private String owner;//车主
    private String licensePlateNo;//车牌号
    private String mobile;//手机号
    private String orderNo;//订单号
    private Long orderStatus;//订单状态
    private Long paymentStatus;//支付状态
    private Long paymentChannel;//支付方式
    private Long area;//地区
    private String sort;//排序
    private String orderStartDate;//出单开始日期
    private String orderEndDate;//出单结束日期
    private String orderDir;
    private Long channel;//平台(支付方式)

    private Long newYearPackStatus;//礼物订单管理状态
    private Integer disable;//拍照信息有效状态
    private Integer visited;//拍照信息回访状态
    private String quoteEntrance;////拍照信息来源
    private Long insuranceCompany;
    private Long sourceChannel;



    @Override
    public Integer getPageSize() {
        return pageSize;
    }

    @Override
    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public Integer getCurrentPage() {
        return currentPage;
    }

    @Override
    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    @Override
    public Integer getDraw() {
        return draw;
    }

    @Override
    public void setDraw(Integer draw) {
        this.draw = draw;
    }

    @Override
    public String getKeyword() {
        return keyword;
    }

    @Override
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getLicensePlateNo() {
        return licensePlateNo;
    }

    public void setLicensePlateNo(String licensePlateNo) {
        this.licensePlateNo = licensePlateNo;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Long getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Long orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Long getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(Long paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public Long getPaymentChannel() {
        return paymentChannel;
    }

    public void setPaymentChannel(Long paymentChannel) {
        this.paymentChannel = paymentChannel;
    }

    public Long getArea() {
        return area;
    }

    public void setArea(Long area) {
        this.area = area;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getOrderStartDate() {
        return orderStartDate;
    }

    public void setOrderStartDate(String orderStartDate) {
        this.orderStartDate = orderStartDate;
    }

    public String getOrderEndDate() {
        return orderEndDate;
    }

    public void setOrderEndDate(String orderEndDate) {
        this.orderEndDate = orderEndDate;
    }

    public String getOrderDir() {
        return orderDir;
    }

    public void setOrderDir(String orderDir) {
        this.orderDir = orderDir;
    }

    public Long getChannel() {
        return channel;
    }

    public void setChannel(Long channel) {
        this.channel = channel;
    }


    public Long getNewYearPackStatus() {
        return newYearPackStatus;
    }

    public void setNewYearPackStatus(Long newYearPackStatus) {
        this.newYearPackStatus = newYearPackStatus;
    }

    public Integer getDisable() {
        return disable;
    }

    public void setDisable(Integer disable) {
        this.disable = disable;
    }

    public Integer getVisited() {
        return visited;
    }

    public void setVisited(Integer visited) {
        this.visited = visited;
    }

    public String getQuoteEntrance() {
        return quoteEntrance;
    }

    public void setQuoteEntrance(String quoteEntrance) {
        this.quoteEntrance = quoteEntrance;
    }

    public Long getInsuranceCompany() {
        return insuranceCompany;
    }

    public void setInsuranceCompany(Long insuranceCompany) {
        this.insuranceCompany = insuranceCompany;
    }

    public Long getSourceChannel() {
        return sourceChannel;
    }

    public void setSourceChannel(Long sourceChannel) {
        this.sourceChannel = sourceChannel;
    }


}
