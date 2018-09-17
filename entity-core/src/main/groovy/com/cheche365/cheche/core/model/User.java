package com.cheche365.cheche.core.model;

import com.cheche365.cheche.common.util.MobileUtil;
import com.cheche365.cheche.core.repository.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class User extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -274604362460418883L;
    private String name;
    private String nickName;
    private String mobile;
    private Boolean sex;
    private Gender gender;
    private String email;
    private Date birthday;//生日
    private String employeeNum;//车保易渠道：工号
    private IdentityType identityType;//证件类型,1.身份证,2.护照,3.军官证
    private String identity;//证件id
    private UserType userType;
    private Boolean bound;
    private String registerIp;
    private Channel registerChannel;
    private Area area;// 所在城市
    private UserSource userSource;
    private OrderSourceType sourceType;//订单来源类型，区分CPS渠道，大客户，滴滴专车增补保险
    private String sourceId;//订单来源对象id
    private int audit;
    //Transient
    private Boolean wechatUser;
    private Boolean alipayUser;// 是否是支付宝用户，不保存db
    private boolean registerUser;
    private Boolean unsubscribed;
    private Boolean useDefaultEmail;

    @Column(columnDefinition = "DATE")
    @JsonIgnore()
    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @ManyToOne
    @JoinColumn(name = "gender", foreignKey = @ForeignKey(name = "FK_USER_REF_GENDER", foreignKeyDefinition = "FOREIGN KEY (gender) REFERENCES gender(id)"))
    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    @ManyToOne
    @JoinColumn(name = "identityType", foreignKey = @ForeignKey(name = "FK_USER_REF_IDENTITY_TYPE", foreignKeyDefinition = "FOREIGN KEY (identity_type) REFERENCES identity_type(id)"))
    @JsonIgnore()
    public IdentityType getIdentityType() {
        return identityType;
    }

    public void setIdentityType(IdentityType identityType) {
        this.identityType = identityType;
    }

    @ManyToOne
    @JoinColumn(name = "userType", foreignKey = @ForeignKey(name = "FK_USER_REF_USER_TYPE", foreignKeyDefinition = "FOREIGN KEY (user_type) REFERENCES user_type(id)"))
    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    @Column(columnDefinition = "tinyint(1)")
    public Boolean isBound() {
        return bound;
    }

    public void setBound(Boolean bound) {
        this.bound = bound;
    }

    @Column(columnDefinition = "VARCHAR(200)")
    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    @Column(columnDefinition = "TINYINT(1)")
    @JsonIgnore()
    public Boolean getSex() {
        return sex;
    }

    public void setSex(Boolean sex) {
        this.sex = sex;
    }


    @Transient
    public Boolean isWechatUser() {
        return wechatUser;
    }

    public void setWechatUser(Boolean wechatUser) {
        this.wechatUser = wechatUser;
    }

    @Transient
    public boolean isRegisterUser() {
        return registerUser;
    }

    public void setRegisterUser(boolean registerUser) {
        this.registerUser = registerUser;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getRegisterIp() {
        return registerIp;
    }

    public void setRegisterIp(String registerIp) {
        this.registerIp = registerIp;
    }

    @ManyToOne
    @JoinColumn(name = "registerChannel", foreignKey = @ForeignKey(name = "FK_USER_REF_CHANNEL", foreignKeyDefinition = "FOREIGN KEY (register_channel) REFERENCES channel(id)"))
    public Channel getRegisterChannel() {
        return registerChannel;
    }

    public void setRegisterChannel(Channel registerChannel) {
        this.registerChannel = registerChannel;
    }

    @ManyToOne
    @JoinColumn(name = "userSource", foreignKey = @ForeignKey(name = "FK_USER_REF_USER_SOURCE", foreignKeyDefinition = "FOREIGN KEY (user_source) REFERENCES user_source(id)"))
    public UserSource getUserSource() {
        return userSource;
    }

    public void setUserSource(UserSource userSource) {
        this.userSource = userSource;
    }

    @Transient
    public Boolean getAlipayUser() {
        return alipayUser;
    }

    public void setAlipayUser(Boolean alipayUser) {
        this.alipayUser = alipayUser;
    }

    @PrePersist
    @PreUpdate
    public void setDefaultAudit() {
        if (this.audit == 0) this.audit = 1;
    }

    @Column(columnDefinition = "int(1)")
    public int getAudit() {
        return audit;
    }

    public void setAudit(int audit) {
        this.audit = audit;
    }

    @Transient
    public Boolean isUnsubscribed() {
        return unsubscribed;
    }

    public void setUnsubscribed(Boolean unsubscribed) {
        this.unsubscribed = unsubscribed;
    }

    @ManyToOne
    @JoinColumn(name = "sourceType", foreignKey = @ForeignKey(name = "FK_USER_REF_SOURCEs_TYPE", foreignKeyDefinition = "FOREIGN KEY (`order_source_type`) REFERENCES `order_source_type` (`id`)"))
    public OrderSourceType getSourceType() {
        return sourceType;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceType(OrderSourceType sourceType) {
        this.sourceType = sourceType;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getEmployeeNum() {
        return employeeNum;
    }

    public void setEmployeeNum(String employeeNum) {
        this.employeeNum = employeeNum;
    }

    @ManyToOne
    @JoinColumn(name = "area", foreignKey = @ForeignKey(name = "FK_USER_REF_AREA", foreignKeyDefinition = "FOREIGN KEY (area) REFERENCES area(id)"))
    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    @Transient
    public String getEncyptMobile(){
        return MobileUtil.getEncyptMobile(mobile);
    }

    @Column(columnDefinition = "TINYINT(1)")
    public Boolean getUseDefaultEmail() {
        return useDefaultEmail;
    }

    public void setUseDefaultEmail(Boolean useDefaultEmail) {
        this.useDefaultEmail = useDefaultEmail;
    }
}
