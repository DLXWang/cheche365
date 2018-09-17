package com.cheche365.cheche.core.model.agent

import com.fasterxml.jackson.annotation.JsonIgnore

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

/**
 * Author:   shanxf
 * Date:     2018/9/10 11:25
 */
@Entity
class Ethnic implements Serializable{

    private static final long serialVersionUID = 1L

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id
    @Column(columnDefinition = "VARCHAR(20)")
    public String name
    @Column(columnDefinition = "tinyint(1)")
    @JsonIgnore
    public Boolean disable
}
