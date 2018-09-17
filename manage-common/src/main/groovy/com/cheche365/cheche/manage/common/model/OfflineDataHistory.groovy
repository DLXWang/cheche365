package com.cheche365.cheche.manage.common.model

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

/**
 * 线下导入的数据版本信息,为了防止重复上传
 * Created by yinjianbin on 2018/1/19.
 */
@Entity
class OfflineDataHistory {

    private Long id    //ID
    private Long purchaseOrderId    //订单Id
    private String policyNo    //保单号
    private Integer dataSource    //数据来源 1:泛华,2:保险公司,3:泛华补充
    private Long historyId    //上传批次Id
    private String sourceId     //数据来源Id(记录excel批次以及行号)
    private String dataVersion    //数据版本
    private String comment    //备注


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long getId() {
        return id
    }

    void setId(Long id) {
        this.id = id
    }

    @Column(columnDefinition = "bigint(20)")
    Long getPurchaseOrderId() {
        return purchaseOrderId
    }

    void setPurchaseOrderId(Long purchaseOrderId) {
        this.purchaseOrderId = purchaseOrderId
    }

    @Column(columnDefinition = "VARCHAR(45)")
    String getPolicyNo() {
        return policyNo
    }

    void setPolicyNo(String policyNo) {
        this.policyNo = policyNo
    }

    @Column(columnDefinition = "tinyint(1)")
    Integer getDataSource() {
        return dataSource
    }

    void setDataSource(Integer dataSource) {
        this.dataSource = dataSource
    }

    @Column(columnDefinition = "bigint(20)")
    Long getHistoryId() {
        return historyId
    }

    void setHistoryId(Long historyId) {
        this.historyId = historyId
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getSourceId() {
        return sourceId
    }

    void setSourceId(String sourceId) {
        this.sourceId = sourceId
    }

    @Column(columnDefinition = "CHAR(32)")
    String getDataVersion() {
        return dataVersion
    }

    void setDataVersion(String dataVersion) {
        this.dataVersion = dataVersion
    }

    @Column(columnDefinition = "VARCHAR(200)")
    String getComment() {
        return comment
    }

    void setComment(String comment) {
        this.comment = comment
    }
}
