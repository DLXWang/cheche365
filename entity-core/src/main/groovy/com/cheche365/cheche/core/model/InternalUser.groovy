package com.cheche365.cheche.core.model

import com.cheche365.cheche.core.context.ApplicationContextHolder
import org.apache.commons.lang3.builder.EqualsBuilder

import javax.persistence.*

@Entity
public class InternalUser implements Serializable {
    private static final long serialVersionUID = 1L

    private Long id;
    private String userId;//liqiang@cheche365.com
    private String name;
    private String mobile;
    private String email;
    private Gender gender;
    private String password;
    private boolean disable;//是否禁用
    @Column(columnDefinition = "DATETIME")
    private Date createTime;
    @Column(columnDefinition = "DATETIME")
    private Date updateTime;
    private Integer internalUserType;
    @Transient
    private List dataPermission;
    @Transient
    private TelMarketer telMarketer;
    private boolean lock;
    private Date changePasswordTime;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(columnDefinition = "VARCHAR(100)")
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    @Column(columnDefinition = "VARCHAR(100)")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @ManyToOne
    @JoinColumn(name = "gender", foreignKey = @ForeignKey(name = "FK_INTERNAL_USER_REF_GENDER", foreignKeyDefinition = "FOREIGN KEY (gender) REFERENCES gender(id)"))
    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Column(columnDefinition = "tinyint(1)")
    public boolean isDisable() {
        return disable;
    }

    public void setDisable(boolean disable) {
        this.disable = disable;
    }

    @Column(name = "[lock]",columnDefinition = "tinyint(1)")
    public boolean isLock() {
        return lock;
    }

    public void setLock(boolean lock) {
        this.lock = lock;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Column(columnDefinition = "tinyint(1)")
    public Integer getInternalUserType() {
        return internalUserType;
    }

    public void setInternalUserType(Integer internalUserType) {
        this.internalUserType = internalUserType;
    }

    @Transient
    public List getDataPermission() {
        return dataPermission;
    }

    public void setDataPermission(List dataPermission) {
        this.dataPermission = dataPermission;
    }

    @Transient
    public TelMarketer getTelMarketer() {
        return telMarketer;
    }

    public void setTelMarketer(TelMarketer telMarketer) {
        this.telMarketer = telMarketer;
    }

    @Column(columnDefinition = "DATETIME")
    public Date getChangePasswordTime() {
        return changePasswordTime;
    }

    public void setChangePasswordTime(Date changePasswordTime) {
        this.changePasswordTime = changePasswordTime;
    }

    public static class ENUM {
        public static InternalUser SYSTEM = null;

        static {
            def internalUserRepository = ApplicationContextHolder.getApplicationContext().getBean('internalUserRepository');
            SYSTEM = internalUserRepository.findFirstByName("system");
        }
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof InternalUser && EqualsBuilder.reflectionEquals(this, o);
    }
}
