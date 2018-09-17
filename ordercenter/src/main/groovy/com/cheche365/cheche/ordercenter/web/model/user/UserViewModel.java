package com.cheche365.cheche.ordercenter.web.model.user;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.manage.common.model.TelMarketingCenter;
import com.cheche365.cheche.core.model.User;
import com.cheche365.cheche.manage.common.web.model.PageViewModel;
import com.cheche365.cheche.ordercenter.web.model.auto.AutoViewModel;
import com.cheche365.cheche.ordercenter.web.model.order.OrderOperationInfoViewModel;

import java.util.List;

/**
 * Created by wangshaobin on 2017/3/30.
 */
public class UserViewModel {
    private Long id;
    private String name;
    private String userId;
    private String mobile;//手机号
    private String binding;//第三方绑定
    private String regtime;//注册时间
    private String regChannel;//注册渠道
    private String regIp;//注册IP
    private String lastLoginTime;//最后登录时间
    private List<AutoViewModel> autos;//车辆信息
    private String email;//邮箱
    private String birthday;//出生日期
    private String type;//用户类型
    private String sex;//性别
    private String nickName;//昵称
    private String source;//用户来源
    private String audit;
    private String identity;//证件id
    private PageViewModel<OrderOperationInfoViewModel> orderInfoPage;//订单信息
    private String telMarketingCenterSource;//电销来源
    private String operator;//跟进人
    private String triggerTime;//预约时间
    private String createTime;//创建时间
    private String updateTime;//更新时间
    private String status;//状态
    private String balance;//余额
    private String outAmount;//提现总额

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getOutAmount() {
        return outAmount;
    }

    public void setOutAmount(String outAmount) {
        this.outAmount = outAmount;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getAudit() {
        return audit;
    }

    public void setAudit(String audit) {
        this.audit = audit;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public PageViewModel<OrderOperationInfoViewModel> getOrderInfoPage() {
        return orderInfoPage;
    }

    public void setOrderInfoPage(PageViewModel<OrderOperationInfoViewModel> orderInfoPage) {
        this.orderInfoPage = orderInfoPage;
    }

    public String getTelMarketingCenterSource() {
        return telMarketingCenterSource;
    }

    public void setTelMarketingCenterSource(String telMarketingCenterSource) {
        this.telMarketingCenterSource = telMarketingCenterSource;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getTriggerTime() {
        return triggerTime;
    }

    public void setTriggerTime(String triggerTime) {
        this.triggerTime = triggerTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getBinding() {
        return binding;
    }

    public void setBinding(String binding) {
        this.binding = binding;
    }

    public String getRegtime() {
        return regtime;
    }

    public void setRegtime(String regtime) {
        this.regtime = regtime;
    }

    public String getRegChannel() {
        return regChannel;
    }

    public void setRegChannel(String regChannel) {
        this.regChannel = regChannel;
    }

    public String getRegIp() {
        return regIp;
    }

    public void setRegIp(String regIp) {
        this.regIp = regIp;
    }

    public String getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(String lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public List<AutoViewModel> getAutos() {
        return autos;
    }

    public void setAutos(List<AutoViewModel> autos) {
        this.autos = autos;
    }


    public static void createUserToModel(User user, UserViewModel viewModel){
        viewModel.setName(user.getName());
        viewModel.setMobile(user.getMobile());
        viewModel.setEmail(user.getEmail());
        viewModel.setBirthday(DateUtils.getDateString(user.getBirthday(), DateUtils.DATE_LONGTIME24_PATTERN));
        viewModel.setType(user.getUserType()==null?"":user.getUserType().getName());
        viewModel.setSex(user.getGender()==null?"":user.getGender().getName());
        viewModel.setNickName(user.getNickName());
        viewModel.setRegChannel(user.getRegisterChannel()==null?"":user.getRegisterChannel().getDescription());
        viewModel.setSource(user.getUserSource()==null?"":user.getUserSource().getDescription());
        viewModel.setRegIp(user.getRegisterIp());
        viewModel.setAudit(String.valueOf(user.getAudit()));
        viewModel.setIdentity(user.getIdentity());
    }

    public static void createCenterToModel(TelMarketingCenter center, UserViewModel viewModel){
        /**
         * 电销来源、跟进人、预约时间、创建更新时间、状态
         * **/
        viewModel.setTelMarketingCenterSource(center.getSource()==null?"":center.getSource().getDescription());
        viewModel.setOperator(center.getOperator()==null?"":center.getOperator().getName());
        viewModel.setTriggerTime(center.getTriggerTime()==null?"":DateUtils.getDateString(center.getTriggerTime(), DateUtils.DATE_LONGTIME24_PATTERN));
        viewModel.setCreateTime(center.getCreateTime()==null?"":DateUtils.getDateString(center.getCreateTime(), DateUtils.DATE_LONGTIME24_PATTERN));
        viewModel.setUpdateTime(center.getUpdateTime()==null?"":DateUtils.getDateString(center.getUpdateTime(), DateUtils.DATE_LONGTIME24_PATTERN));
        viewModel.setStatus(center.getStatus()==null?"":center.getStatus().getDescription());
    }
}
