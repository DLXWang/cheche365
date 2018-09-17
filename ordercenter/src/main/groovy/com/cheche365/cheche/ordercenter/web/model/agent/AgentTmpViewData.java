package com.cheche365.cheche.ordercenter.web.model.agent;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.AgentTmp;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by wangshaobin on 2017/5/5.
 */
public class AgentTmpViewData {
    private Long id;//agent id
    private String agentName; //姓名
    private String agentMobile; //手机号
    private String identityType; //证件类型
    private String identityNumber; //证件号码
    private String cardNumber; //银行账号
    private String openingBank; //开户行
    private String bankBranch; //开户支行
    private String bankAccount; //账户名
    private String agentCompany; //代理公司名称
    private String comment; //备注
    private String operator; //操作人
    @NotNull
    private String rebate;//返点
    private Boolean enable;
    private String createTime;//创建时间
    private String updateTime;//更新时间
    private List<AgentRebateTmpViewData> agentRebate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getAgentMobile() {
        return agentMobile;
    }

    public void setAgentMobile(String agentMobile) {
        this.agentMobile = agentMobile;
    }

    public String getIdentityType() {
        return identityType;
    }

    public void setIdentityType(String identityType) {
        this.identityType = identityType;
    }

    public String getIdentityNumber() {
        return identityNumber;
    }

    public void setIdentityNumber(String identityNumber) {
        this.identityNumber = identityNumber;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getOpeningBank() {
        return openingBank;
    }

    public void setOpeningBank(String openingBank) {
        this.openingBank = openingBank;
    }

    public String getBankBranch() {
        return bankBranch;
    }

    public void setBankBranch(String bankBranch) {
        this.bankBranch = bankBranch;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getAgentCompany() {
        return agentCompany;
    }

    public void setAgentCompany(String agentCompany) {
        this.agentCompany = agentCompany;
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

    public String getRebate() {
        return rebate;
    }

    public void setRebate(String rebate) {
        this.rebate = rebate;
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

    public List<AgentRebateTmpViewData> getAgentRebate() {
        return agentRebate;
    }

    public void setAgentRebate(List<AgentRebateTmpViewData> agentRebate) {
        this.agentRebate = agentRebate;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    /**
     * organize AgentViewData for show
     * @param agent
     * @return
     * @throws Exception
     */
    public static AgentTmpViewData createViewData(AgentTmp agent) {
        AgentTmpViewData viewData = new AgentTmpViewData();
        viewData.setId(agent.getId());
        viewData.setAgentName(agent.getName());
        viewData.setAgentMobile(agent.getMobile());
        viewData.setIdentityType(agent.getIdentityType().getName());
        viewData.setIdentityNumber(agent.getIdentity());
        viewData.setCardNumber(agent.getCardNumber());
        viewData.setOpeningBank(agent.getOpeningBank());
        viewData.setBankBranch(agent.getBankBranch());
        viewData.setBankAccount(agent.getBankAccount());
        viewData.setComment(agent.getComment());
        viewData.setAgentCompany(agent.getAgentCompany() == null ? "" : agent.getAgentCompany().getName());
        viewData.setOperator(agent.getOperator().getName());
        viewData.setRebate(agent.getRebate() == null ? "" : agent.getRebate().toString());
        viewData.setCreateTime(DateUtils.getDateString(
            agent.getCreateTime(), DateUtils.DATE_SHORTDATE_PATTERN));
        viewData.setUpdateTime(agent.getUpdateTime() == null ? "" : DateUtils.getDateString(
            agent.getUpdateTime(), DateUtils.DATE_SHORTDATE_PATTERN));
        viewData.setEnable(agent.getEnable());

        return viewData;
    }
}
