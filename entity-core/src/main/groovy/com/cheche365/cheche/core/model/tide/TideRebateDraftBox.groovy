package com.cheche365.cheche.core.model.tide

import com.cheche365.cheche.core.model.InternalUser

import javax.persistence.*

/**
 * 潮汐系统: 点位草稿箱
 * Created by wanglei on 2018/05/03.
 */
@Entity
class TideRebateDraftBox implements Serializable {
    private static final long serialVersionUID = 1L

    Long id
    String name
    InternalUser operator
    Date createTime
    Integer status
    List<TideContractRebate> contractRebateList

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long getId() {
        return id
    }

    @Column(columnDefinition = "varchar(200)")
    String getName() {
        return name
    }

    @ManyToOne
    @JoinColumn(name = "operator", foreignKey = @ForeignKey(name = "FK_REBATE_DRAFT_REF_OPERATOR", foreignKeyDefinition = "FOREIGN KEY (operator) REFERENCES internal_user(id)"))
    InternalUser getOperator() {
        return operator
    }

    @Column(columnDefinition = "datetime")
    Date getCreateTime() {
        return createTime
    }

    @Column(columnDefinition = "tinyint(1)")
    Integer getStatus() {
        return status
    }

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "tide_draft_ref_rebate", joinColumns = @JoinColumn(name = "draft_id"), inverseJoinColumns = @JoinColumn(name = "rebate_id"))
    List<TideContractRebate> getContractRebateList() {
        return contractRebateList
    }

    @PrePersist
    void onCreate() {
        this.setCreateTime((new Date()))
    }
}
