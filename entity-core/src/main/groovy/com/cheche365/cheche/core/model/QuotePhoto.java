package com.cheche365.cheche.core.model;

import com.cheche365.cheche.core.service.listener.EntityChangeListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by wangfei on 2015/10/20.
 */
@Entity
@EntityListeners(EntityChangeListener.class)
public class QuotePhoto implements Serializable {

    private static final long serialVersionUID = -4030431137699841822L;
    private Long id;
    private String licensePlateNo;//车牌号
    private String owner;//车主
    private IdentityType identityType;
    private String identity;//车主身份证
    private String insuredName;//被保险人
    private IdentityType insuredIdType;
    private String insuredIdNo;//被保险人身份证
    private String vinNo;//车架号
    private String engineNo;//发动机号
    private Date enrollDate;//车辆注册日期
    private String model;//车型
    private String code;//品牌型号
    private Date expireDate;//失效日期
    private Boolean disable =true ;//是否失效 true失效 false有效
    private Boolean visited =false;//是否需回访 true已回访 false需回访
    private String comment;//备注
    private User user;//用户
    private UserImg userImg;//用户图片
    private Date createTime;//创建时间
    private Date updateTime;//更新时间
    private Marketing activity;//活动
    private InternalUser operator;
    private InternalUser responsible;
    private Date transferDate;//车辆过户日期
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getLicensePlateNo() {
        return licensePlateNo;
    }

    public void setLicensePlateNo(String licensePlateNo) {
        this.licensePlateNo = licensePlateNo;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    @ManyToOne
    @JoinColumn(name = "identity_type", foreignKey=@ForeignKey(name="FK_IDENTITY_TYPE_REF_IDENTITY_TYPE", foreignKeyDefinition="FOREIGN KEY (identity_type) REFERENCES identity_type(id)"))
    public IdentityType getIdentityType() {
        return identityType;
    }

    public void setIdentityType(IdentityType identityType) {
        this.identityType = identityType;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getInsuredName() {
        return insuredName;
    }

    public void setInsuredName(String insuredName) {
        this.insuredName = insuredName;
    }

    @ManyToOne
    @JoinColumn(name = "insured_id_type", foreignKey=@ForeignKey(name="FK_INSURED_ID_TYPE_REF_IDENTITY_TYPE", foreignKeyDefinition="FOREIGN KEY (insured_id_type) REFERENCES identity_type(id)"))
    public IdentityType getInsuredIdType() {
        return insuredIdType;
    }

    public void setInsuredIdType(IdentityType insuredIdType) {
        this.insuredIdType = insuredIdType;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getInsuredIdNo() {
        return insuredIdNo;
    }

    public void setInsuredIdNo(String insuredIdNo) {
        this.insuredIdNo = insuredIdNo;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getVinNo() {
        return vinNo;
    }

    public void setVinNo(String vinNo) {
        this.vinNo = vinNo;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getEngineNo() {
        return engineNo;
    }

    public void setEngineNo(String engineNo) {
        this.engineNo = engineNo;
    }

    @Column(columnDefinition = "DATE")
    public Date getEnrollDate() {
        return enrollDate;
    }

    public void setEnrollDate(Date enrollDate) {
        this.enrollDate = enrollDate;
    }

    @Column(columnDefinition = "VARCHAR(120)")
    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Column(columnDefinition = "DATE")
    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }


    @Column(columnDefinition = "VARCHAR(200)")
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @ManyToOne
    @JoinColumn(name = "user", foreignKey=@ForeignKey(name="FK_QUOTE_PHOTO_USER_REF_USER", foreignKeyDefinition="FOREIGN KEY (user) REFERENCES user(id)"))
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Column(columnDefinition = "DATETIME")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Column(columnDefinition = "DATETIME")
    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Column(columnDefinition = "tinyint(1)")
    public Boolean getDisable() {
        return disable;
    }

    public void setDisable(Boolean disable) {
        this.disable = disable;
    }

    @Column(columnDefinition = "tinyint(1)")
    public Boolean getVisited() {
        return visited;
    }

    public void setVisited(Boolean visited) {
        this.visited = visited;
    }

    @ManyToOne
    @JoinColumn(name = "userImg", foreignKey=@ForeignKey(name="FK_QUOTE_PHOTO_USER_IMG_REF_USER_IMG", foreignKeyDefinition="FOREIGN KEY (user_img) REFERENCES user_img(id)"))
    public UserImg getUserImg() {
        return userImg;
    }

    public void setUserImg(UserImg userImg) {
        this.userImg = userImg;
    }

    @ManyToOne
    @JoinColumn(name = "activity", foreignKey=@ForeignKey(name="FK_QUOTE_PHOTO_MARKING_REF_MARKING", foreignKeyDefinition="FOREIGN KEY (activity) REFERENCES marketing(id)"))
    public Marketing getActivity() {
        return activity;
    }

    public void setActivity(Marketing activity) {
        this.activity = activity;
    }

    @ManyToOne
    @JoinColumn(name = "operator", foreignKey=@ForeignKey(name="FK_QUOTE_PHOTO_OPERATOR_REF_INTERNAL_USER", foreignKeyDefinition="FOREIGN KEY (operator) REFERENCES internal_user(id)"))
    public InternalUser getOperator() {
        return operator;
    }

    public void setOperator(InternalUser operator) {
        this.operator = operator;
    }
    @ManyToOne
    @JoinColumn(name = "responsible", foreignKey=@ForeignKey(name="FK_QUOTE_PHOTO_RESPONSIBLE_REF_INTERNAL_USER", foreignKeyDefinition="FOREIGN KEY (responsible) REFERENCES internal_user(id)"))
    public InternalUser getResponsible() {
        return responsible;
    }

    public void setResponsible(InternalUser responsible) {
        this.responsible = responsible;
    }

    @Column(columnDefinition = "DATE")
    public Date getTransferDate() {
        return transferDate;
    }

    public void setTransferDate(Date transferDate) {
        this.transferDate = transferDate;
    }
}
