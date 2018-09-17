package com.cheche365.cheche.ordercenter.web.model.telMarketingCenter;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.manage.common.model.TelMarketingCenter;
import com.cheche365.cheche.manage.common.model.TelMarketingCenterHistory;
import com.cheche365.cheche.core.service.ResourceService;
import com.cheche365.cheche.manage.common.web.model.PageViewModel;
import com.cheche365.cheche.ordercenter.web.model.auto.AutoViewModel;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;

public class TelMarketingCenterViewModel {
    private Long id;//id
    private String triggerTime;//触发时间
    private String mobile;//号码
    private String encyptMobile;//加密手机号
    private String userName;//用户名
    private String expireTime;//到期日
    private Long sourceId;//来源id
    private String sourceName;//来源
    private Long statusId;//处理结果id
    private String statusName;//处理结果
    private String operator;//跟进人姓名
    private String processedNumber;//处理次数
    private String createTime;//创建时间，电话接入系统时间
    private String sourceCreateTime;//来源创建时间
    private String comment;//备注
    private Long registerChannelId;//用户注册渠道id
    private boolean invited=false;//用户是否被邀请参加推荐活动
    private List<AutoViewModel> autoInfoList;//车辆信息
    private List<TelMarketingCenterHistoryViewModel> dealHisList;//处理历史
    private PageViewModel<TelMarketingCenterRepeatViewModel> repeatList;//号码相关多条数据，营销表只会展示最后一条，但所有此号码相关的都会记录在repeat表中
    private String resultDetail;//处理结果
    private String channelIcon;//渠道图标
    private String countDown;//车险到期倒计时（天）
    private Long autoId;//报价id 查看详情车牌号需要

    public PageViewModel<TelMarketingCenterRepeatViewModel> getRepeatList() {
        return repeatList;
    }

    public void setRepeatList(PageViewModel<TelMarketingCenterRepeatViewModel> repeatList) {
        this.repeatList = repeatList;
    }

    public String getCountDown() { return countDown; }

    public void setCountDown(String countDown) { this.countDown = countDown; }

    private String orderNo;//订单号

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTriggerTime() {
        return triggerTime;
    }

    public void setTriggerTime(String triggerTime) {
        this.triggerTime = triggerTime;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getProcessedNumber() {
        return processedNumber;
    }

    public void setProcessedNumber(String processedNumber) {
        this.processedNumber = processedNumber;
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

    public Long getRegisterChannelId() {
        return registerChannelId;
    }

    public void setRegisterChannelId(Long registerChannelId) {
        this.registerChannelId = registerChannelId;
    }

    public boolean isInvited() {
        return invited;
    }

    public void setInvited(boolean invited) {
        this.invited = invited;
    }

    public List<AutoViewModel> getAutoInfoList() {
        return autoInfoList;
    }

    public void setAutoInfoList(List<AutoViewModel> autoInfoList) {
        this.autoInfoList = autoInfoList;
    }

    public List<TelMarketingCenterHistoryViewModel> getDealHisList() {
        return dealHisList;
    }

    public void setDealHisList(List<TelMarketingCenterHistoryViewModel> dealHisList) {
        this.dealHisList = dealHisList;
    }

    public String getSourceCreateTime() {
        return sourceCreateTime;
    }

    public void setSourceCreateTime(String sourceCreateTime) {
        this.sourceCreateTime = sourceCreateTime;
    }

    public String getChannelIcon() {
        return channelIcon;
    }

    public void setChannelIcon(String channelIcon) {
        this.channelIcon = channelIcon;
    }

    public Long getAutoId() {
        return autoId;
    }

    public void setAutoId(Long autoId) {
        this.autoId = autoId;
    }

    public static TelMarketingCenterViewModel createViewModel(TelMarketingCenter telMarketingCenter, ResourceService resourceService) {
        if(telMarketingCenter == null) {
            return null;
        }
        TelMarketingCenterViewModel telMarketingCenterViewModel = new TelMarketingCenterViewModel();
        telMarketingCenterViewModel.setId(telMarketingCenter.getId());
        telMarketingCenterViewModel.setMobile(telMarketingCenter.getMobile());
        telMarketingCenterViewModel.setEncyptMobile(telMarketingCenter.getEncyptMobile());
        telMarketingCenterViewModel.setUserName(telMarketingCenter.getUserName());
        telMarketingCenterViewModel.setOperator(telMarketingCenter.getOperator()==null?"":telMarketingCenter.getOperator().getName());
        telMarketingCenterViewModel.setProcessedNumber(telMarketingCenter.getProcessedNumber().toString());
        telMarketingCenterViewModel.setSourceId(telMarketingCenter.getSource() != null ? telMarketingCenter.getSource().getId() : new Long(0));
        telMarketingCenterViewModel.setSourceName((telMarketingCenter.getSource() != null) ? telMarketingCenter.getSource().getDescription() : "");
        telMarketingCenterViewModel.setStatusId((telMarketingCenter.getStatus() != null) ? telMarketingCenter.getStatus().getId() : new Long(0));
        telMarketingCenterViewModel.setStatusName((telMarketingCenter.getStatus() != null) ? telMarketingCenter.getStatus().getName() : "");
        telMarketingCenterViewModel.setCreateTime(DateUtils.getDateString(telMarketingCenter.getCreateTime(), DateUtils.DATE_LONGTIME24_PATTERN));//创建时间
        telMarketingCenterViewModel.setSourceCreateTime(DateUtils.getDateString(telMarketingCenter.getSourceCreateTime(), DateUtils.DATE_LONGTIME24_PATTERN));//来源时间
        telMarketingCenterViewModel.setExpireTime(DateUtils.getDateString(telMarketingCenter.getExpireTime(), DateUtils.DATE_SHORTDATE_PATTERN));//到期时间
        telMarketingCenterViewModel.setTriggerTime(DateUtils.getDateString(telMarketingCenter.getTriggerTime(), DateUtils.DATE_LONGTIME24_PATTERN));//预约时间
        if(telMarketingCenter.getUser() != null && telMarketingCenter.getUser().getRegisterChannel() != null) {
            telMarketingCenterViewModel.setRegisterChannelId(telMarketingCenter.getUser().getRegisterChannel().getId());
            if(!StringUtils.isEmpty(telMarketingCenter.getUser().getRegisterChannel().getIcon())){
                telMarketingCenterViewModel.setChannelIcon(resourceService.absoluteUrl(resourceService.getResourceAbsolutePath(resourceService.getProperties().getChannelPath()),
                    telMarketingCenter.getUser().getRegisterChannel().getIcon()));
            }
        }
        return telMarketingCenterViewModel;
    }

    public static TelMarketingCenterViewModel createViewModel(TelMarketingCenter telMarketingCenter, TelMarketingCenterHistory history, ResourceService resourceService) {
        TelMarketingCenterViewModel telMarketingCenterViewModel = createViewModel(telMarketingCenter, resourceService);
        if(history != null )
            telMarketingCenterViewModel.setComment(history.getComment());
        return telMarketingCenterViewModel;
    }

    /**
     * 计算车险到期倒计时天数.
     */
    public static String getDaysBetweenToString(Date begin, Date end) {
        long between = (end.getTime() - begin.getTime()) / 1000;
        if (between <= 0) {
            between = 0 - between;
        }
        long day1 = between / (24 * 3600) + 1;
        StringBuffer result = new StringBuffer();
        if (day1 > 0) result.append(day1).append("天");
        return result.toString();
    }
    public String getResultDetail() { return resultDetail;  }

    public void setResultDetail(String resultDetail) {  this.resultDetail = resultDetail;  }

    public String getOrderNo() {  return orderNo;  }

    public void setOrderNo(String orderNo) { this.orderNo = orderNo;  }

    public String getEncyptMobile() {
        return encyptMobile;
    }

    public void setEncyptMobile(String encyptMobile) {
        this.encyptMobile = encyptMobile;
    }

}
