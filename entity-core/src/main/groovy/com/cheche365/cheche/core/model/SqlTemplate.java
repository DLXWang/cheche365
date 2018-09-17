package com.cheche365.cheche.core.model;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by guoweifu on 2015/10/8.
 */

@Entity
public class SqlTemplate {

    private Long id;
    private String name;
    private String content;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Column(columnDefinition = "VARCHAR(1000)")
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
