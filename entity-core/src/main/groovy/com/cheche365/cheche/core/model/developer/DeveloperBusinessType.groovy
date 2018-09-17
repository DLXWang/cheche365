package com.cheche365.cheche.core.model.developer

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne

/**
 * Created by zhengwei on 07/03/2018.
 */

@Entity
@JsonIgnoreProperties(ignoreUnknown = true, value = ["parent"])
class DeveloperBusinessType {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    Long id

    @ManyToOne
    DeveloperBusinessType parent

    @Column(columnDefinition = "VARCHAR(45)")
    String description

}
