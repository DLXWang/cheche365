package com.cheche365.cheche.ordercenter.web.model.telMarketingCenter;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.manage.common.model.TelMarketingCenter;
import com.cheche365.cheche.manage.common.model.TelMarketingCenterHistory;
import com.cheche365.cheche.core.service.ResourceService;
import com.cheche365.cheche.common.util.MobileUtil;
import org.apache.commons.lang3.StringUtils;

public class TelMarketingCenterHistoryViewModel {

    private Long hisId; // TelMarketingCenterHistoryId
    private Long id; // TelMarketingCenter id
    private String mobile;//联系方式
    private String dealResult;//处理结果
    private String createTime;//创建时间
    private String comment;//备注
    private String operator;//操作人
    private String name;//姓名
    private String expireTime;//车险到期日
    private Long sourceId;//来源id
    private String sourceName;//来源名称
    private Long statusId;//处理结果id
    private String statusName;//处理结果
    private String callPercentage;//拨打百分比
    private String effectivePercentage;//有效、无效百分比
    private String averageOrderTime;//平均多少分成单
    private String orderByCallTimes;//平均接通多少次成单
    private String statusX;//数据状态x栏
    private String statusY;//数据状态Y栏
    private String orderX;//成单x栏
    private String orderY;//成单y栏
    private String orderPercent;//成单百分比
    private String isTelMaster;//是否是电话主管
    private Long registerChannelId;//用户注册渠道Id
    private String resultDetail;//处理结果
    private String channelIcon;//渠道LOGO
    private String orderStatus;//出单状态
    private String orderNo;//订单号

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public Long getHisId() {
        return hisId;
    }

    public void setHisId(Long hisId) {
        this.hisId = hisId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getDealResult() {
        return dealResult;
    }

    public void setDealResult(String dealResult) {
        this.dealResult = dealResult;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public Long getStatusId() {
        return statusId;
    }

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getCallPercentage() {
        return callPercentage;
    }

    public void setCallPercentage(String callPercentage) {
        this.callPercentage = callPercentage;
    }

    public String getEffectivePercentage() {
        return effectivePercentage;
    }

    public void setEffectivePercentage(String effectivePercentage) {
        this.effectivePercentage = effectivePercentage;
    }

    public String getAverageOrderTime() {
        return averageOrderTime;
    }

    public void setAverageOrderTime(String averageOrderTime) {
        this.averageOrderTime = averageOrderTime;
    }

    public String getOrderByCallTimes() {
        return orderByCallTimes;
    }

    public void setOrderByCallTimes(String orderByCallTimes) {
        this.orderByCallTimes = orderByCallTimes;
    }

    public String getStatusX() {
        return statusX;
    }

    public void setStatusX(String statusX) {
        this.statusX = statusX;
    }

    public String getStatusY() {
        return statusY;
    }

    public void setStatusY(String statusY) {
        this.statusY = statusY;
    }

    public String getOrderX() {
        return orderX;
    }

    public void setOrderX(String orderX) {
        this.orderX = orderX;
    }

    public String getOrderY() {
        return orderY;
    }

    public void setOrderY(String orderY) {
        this.orderY = orderY;
    }

    public String getOrderPercent() {
        return orderPercent;
    }

    public void setOrderPercent(String orderPercent) {
        this.orderPercent = orderPercent;
    }

    public String getIsTelMaster() {
        return isTelMaster;
    }

    public void setIsTelMaster(String isTelMaster) {
        this.isTelMaster = isTelMaster;
    }

    public Long getRegisterChannelId() {
        return registerChannelId;
    }

    public void setRegisterChannelId(Long registerChannelId) {
        this.registerChannelId = registerChannelId;
    }

    public String getResultDetail() {  return resultDetail; }

    public void setResultDetail(String resultDetail) { this.resultDetail = resultDetail; }

    public String getChannelIcon() {
        return channelIcon;
    }

    public void setChannelIcon(String channelIcon) {
        this.channelIcon = channelIcon;
    }

    /**
     * his实体类转展示类
     * @param telMarketingCenterHistory
     * @return
     */
    public static TelMarketingCenterHistoryViewModel createSimpleViewModel(TelMarketingCenterHistory telMarketingCenterHistory) {
        if(telMarketingCenterHistory == null)
            return null;
        TelMarketingCenterHistoryViewModel hisViewModel = new TelMarketingCenterHistoryViewModel();
        hisViewModel.setDealResult(telMarketingCenterHistory.getDealResult());
        hisViewModel.setCreateTime(DateUtils.getDateString(telMarketingCenterHistory.getCreateTime(), DateUtils.DATE_LONGTIME24_PATTERN));
        hisViewModel.setComment(telMarketingCenterHistory.getOperator().getName() + "："
            + (telMarketingCenterHistory.getComment() == null ? "" : telMarketingCenterHistory.getComment()));
        hisViewModel.setResultDetail(telMarketingCenterHistory.getResultDetail());
        return hisViewModel;
    }

    /**
     * his实体类转展示类
     * @param telMarketingCenterHistory
     * @return
     */
    public static TelMarketingCenterHistoryViewModel createDetailsViewModel(TelMarketingCenterHistory telMarketingCenterHistory, ResourceService resourceService) {
        TelMarketingCenterHistoryViewModel viewModel = new TelMarketingCenterHistoryViewModel();
        TelMarketingCenter telMarketingCenter = telMarketingCenterHistory.getTelMarketingCenter();
        if (telMarketingCenter != null) {
            viewModel.setMobile(telMarketingCenter.getEncyptMobile());
            viewModel.setName(telMarketingCenter.getUserName());
            viewModel.setExpireTime(DateUtils.getDateString(telMarketingCenter.getExpireTime(), DateUtils.DATE_LONGTIME24_PATTERN));
            if (telMarketingCenter.getSource() != null) {
                viewModel.setSourceId(telMarketingCenter.getSource().getId());
                viewModel.setSourceName(telMarketingCenter.getSource().getDescription());
            }
            if(telMarketingCenter.getUser() != null && telMarketingCenter.getUser().getRegisterChannel() != null) {
                viewModel.setRegisterChannelId(telMarketingCenter.getUser().getRegisterChannel().getId());
                if(!StringUtils.isEmpty(telMarketingCenter.getUser().getRegisterChannel().getIcon())){
                    viewModel.setChannelIcon(resourceService.absoluteUrl(resourceService.getResourceAbsolutePath(resourceService.getProperties().getChannelPath()),
                        telMarketingCenter.getUser().getRegisterChannel().getIcon()));
                }
            }
        }
        if (telMarketingCenterHistory.getStatus() != null) {
            viewModel.setStatusId(telMarketingCenterHistory.getStatus().getId());
            viewModel.setStatusName(telMarketingCenterHistory.getStatus().getName());
        }
        viewModel.setComment(telMarketingCenterHistory.getOperator().getName() + "："
            + (telMarketingCenterHistory.getComment() == null ? "" : telMarketingCenterHistory.getComment()));
        viewModel.setResultDetail(telMarketingCenterHistory.getResultDetail());
        viewModel.setId(telMarketingCenter.getId());
        viewModel.setHisId(telMarketingCenterHistory.getId());
        viewModel.setOperator(telMarketingCenterHistory.getOperator().getName());
        viewModel.setDealResult(telMarketingCenterHistory.getDealResult());
        viewModel.setCreateTime(DateUtils.getDateString(telMarketingCenterHistory.getCreateTime(), DateUtils.DATE_LONGTIME24_PATTERN));

        return viewModel;
    }

    public static TelMarketingCenterHistoryViewModel getHistoryPageViewModelFromCenter(TelMarketingCenter center, ResourceService resourceService){
        TelMarketingCenterHistoryViewModel viewModel = new TelMarketingCenterHistoryViewModel();
        viewModel.setOperator(center.getOperator()!=null?center.getOperator().getName():"");
        viewModel.setMobile(center.getEncyptMobile());
        viewModel.setCreateTime(DateUtils.getDateString(center.getTriggerTime(),DateUtils.DATE_LONGTIME24_PATTERN));
        if(center.getUser() != null && center.getUser().getRegisterChannel() != null && !StringUtils.isEmpty(center.getUser().getRegisterChannel().getIcon())){
            viewModel.setChannelIcon(resourceService.absoluteUrl(resourceService.getResourceAbsolutePath(resourceService.getProperties().getChannelPath()),
                center.getUser().getRegisterChannel().getIcon()));
        }
        return viewModel;
    }

    public static TelMarketingCenterHistoryViewModel getHistoryPageViewModelFromObject(Object[] object, ResourceService resourceService){
        TelMarketingCenterHistoryViewModel viewModel = new TelMarketingCenterHistoryViewModel();
        viewModel.setOperator(object[1]!=null?String.valueOf(object[1]):"");
        String mobile = object[2]!=null?String.valueOf(object[2]):"";
        viewModel.setMobile(MobileUtil.getEncyptMobile(mobile));
        viewModel.setCreateTime(object[3]!=null?String.valueOf(object[3]).substring(0,19):"");
        if(object[4]!=null){
            viewModel.setChannelIcon(resourceService.absoluteUrl(resourceService.getResourceAbsolutePath(resourceService.getProperties().getChannelPath()),
                String.valueOf(object[4])));
        }
        viewModel.setOrderNo(object[5]!=null?String.valueOf(object[5]):"");
        viewModel.setOrderStatus(object[6]!=null?String.valueOf(object[6]):"");
        return viewModel;
    }
}
