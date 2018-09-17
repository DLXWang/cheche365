package com.cheche365.cheche.manage.common.model;

import com.cheche365.cheche.core.model.InternalUser;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class TelMarketingCenterHistory {
    private Long id;//主键
    private TelMarketingCenter telMarketingCenter;//电销中心
    private String dealResult;//处理结果
    private Date createTime;//创建时间
    private String comment;//备注
    private InternalUser operator;//操作人
    private Integer type;//1-正常;2-短信;3-报价;4-成单；5-系统操作; 6-预约
    private TelMarketingCenterStatus status;
    private String resultDetail;//其他结果（非开通城市等）

    private List<TelMarketingCenterOrder> centerOrderList = new ArrayList<TelMarketingCenterOrder>();

    @Fetch(FetchMode.SELECT)
    @OneToMany(mappedBy = "telMarketingCenterHistory", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    public List<TelMarketingCenterOrder> getCenterOrderList() {
        return centerOrderList;
    }

    public void setCenterOrderList(List<TelMarketingCenterOrder> centerOrderList) {
        this.centerOrderList = centerOrderList;
    }

    public static final Integer TYPE_NORMAL = 1;
    public static final Integer TYPE_SMS = 2;
    public static final Integer TYPE_QUOTE = 3;
    public static final Integer TYPE_DEAL = 4;
    public static final Integer TYPE_SYSTEM_OPERATION = 5;
    public static final Integer TYPE_APPOITMENT = 6;



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "telMarketingCenter", foreignKey=@ForeignKey(name="FK_TEL_MARKETING_CENTER_HISTORY_REF_TEL_MARKETING_CENTER", foreignKeyDefinition="FOREIGN KEY (telMarketingCenter) REFERENCES tel_marketing_center(id)"))
    public TelMarketingCenter getTelMarketingCenter() {
        return telMarketingCenter;
    }

    public void setTelMarketingCenter(TelMarketingCenter telMarketingCenter) {
        this.telMarketingCenter = telMarketingCenter;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getDealResult() {
        return dealResult;
    }


    public void setDealResult(String dealResult) {
        this.dealResult = dealResult;
    }

    @Column(columnDefinition = "DATETIME")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Column(columnDefinition = "VARCHAR(200)")
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @ManyToOne
    @JoinColumn(name = "operator", foreignKey=@ForeignKey(name="FK_TEL_MARKETING_CENTER_HISTORY_REF_OPERATOR", foreignKeyDefinition="FOREIGN KEY (operator) REFERENCES internal_user(id)"))
    public InternalUser getOperator() {
        return operator;
    }

    public void setOperator(InternalUser operator) {
        this.operator = operator;
    }

    @Column(columnDefinition = "TINYINT(1)")
    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @ManyToOne
    @JoinColumn(name = "status", foreignKey=@ForeignKey(name="tel_marketing_center_history_ibfk_3", foreignKeyDefinition="FOREIGN KEY (status) REFERENCES tel_marketing_center_status(id)"))
    public TelMarketingCenterStatus getStatus() {
        return status;
    }

    public void setStatus(TelMarketingCenterStatus status) {
        this.status = status;
    }


    @Column(columnDefinition = "VARCHAR(50)")
    public String getResultDetail() { return resultDetail; }

    public void setResultDetail(String resultDetail) { this.resultDetail = resultDetail; }
}
