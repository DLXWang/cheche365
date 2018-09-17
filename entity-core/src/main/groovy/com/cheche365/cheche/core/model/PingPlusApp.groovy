package com.cheche365.cheche.core.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
class PingPlusApp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id

    @Column(columnDefinition = "VARCHAR(50)")
    String appId

    @Column(columnDefinition = "VARCHAR(50)")
    String displayName

    @Column(columnDefinition = "VARCHAR(50)")
    String user

    @Column(columnDefinition = "VARCHAR(50)")
    String parentApp

    @Column(columnDefinition = "VARCHAR(500)")
    String metadata

    @Column(columnDefinition = "VARCHAR(50)")
    String description
}
