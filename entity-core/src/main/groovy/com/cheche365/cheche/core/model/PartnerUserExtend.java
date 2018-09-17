package com.cheche365.cheche.core.model;

import javax.persistence.*;

/**
 * @Author shanxf
 * @Date 2018/1/26  10:51
 */

@Entity
public class PartnerUserExtend {


    private Long id;
    private PartnerUser partnerUser;
    private String objectTable; //关联表名
    private Long objectId;//关联表id

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    public PartnerUser getPartnerUser() {
        return partnerUser;
    }

    public void setPartnerUser(PartnerUser partnerUser) {
        this.partnerUser = partnerUser;
    }

    public String getObjectTable() {
        return objectTable;
    }

    public void setObjectTable(String objectTable) {
        this.objectTable = objectTable;
    }

    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }
}
