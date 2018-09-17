package com.cheche365.cheche.core.model.abao

import javax.persistence.*

/**
 * Created by wangjiahuan on 2016/11/16 0016.
 */
@Entity
class TagType {

    private Long id;
    private String name;
    private String description;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long getId() {
        return id
    }

    void setId(Long id) {
        this.id = id
    }

    @Column(columnDefinition = "VARCHAR(100)")
    String getName() {
        return name
    }

    void setName(String name) {
        this.name = name
    }

    @Column(columnDefinition = "VARCHAR(500)")
    String getDescription() {
        return description
    }

    void setDescription(String description) {
        this.description = description
    }

}
