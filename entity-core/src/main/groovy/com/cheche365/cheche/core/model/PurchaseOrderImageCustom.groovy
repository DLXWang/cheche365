package com.cheche365.cheche.core.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import groovy.transform.Canonical

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.ForeignKey
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity
@Canonical
@JsonIgnoreProperties(value = ['id'],ignoreUnknown = true)
class PurchaseOrderImageCustom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id

    @ManyToOne
    @JoinColumn(name = "purchaseOrder", foreignKey = @ForeignKey(name = "FK_IMAGE_CUSTOM_REF_ORDER", foreignKeyDefinition = "FOREIGN KEY (purchase_order) REFERENCES purchase_order(id)"))
    private PurchaseOrder purchaseOrder

    @Column(columnDefinition = "VARCHAR(500)")
    private String description

    @Column(columnDefinition = "tinyint(1)")
    private Boolean needUpload

    @Column(columnDefinition = "DATETIME")
    private Date createTime

    @Column(columnDefinition = "DATETIME")
    private Date updateTime

}
