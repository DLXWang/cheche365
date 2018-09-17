package com.cheche365.cheche.core.model;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by guoweifu on 2015/10/12.
 */
@Entity
public class FilterUser {

    private Long id;
    private String name;
    private SqlTemplate sqlTemplate;
    private String parameter;
    private boolean disable;
    private String comment;
    @Column(columnDefinition = "DATETIME")
    private Date createTime;
    @Column (columnDefinition = "DATETIME")
    private Date updateTime;
    private InternalUser operator;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(columnDefinition = "VARCHAR(1000)")
    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    @Column(columnDefinition = "VARCHAR(20)")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(columnDefinition = "tinyint(1)")
    public boolean isDisable() {
        return disable;
    }

    public void setDisable(boolean disable) {
        this.disable = disable;
    }

    @ManyToOne
    @JoinColumn(name = "sql_template", foreignKey=@ForeignKey(name="FK_FILTER_USER_REF_SQL_TEMPLATE", foreignKeyDefinition="FOREIGN KEY (`sql_template`) REFERENCES `sql_template` (`id`)"))
    public SqlTemplate getSqlTemplate() {
        return sqlTemplate;
    }

    public void setSqlTemplate(SqlTemplate sqlTemplate) {
        this.sqlTemplate = sqlTemplate;
    }

    @Column(columnDefinition = "VARCHAR(200)")
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @ManyToOne
    @JoinColumn(name = "operator", foreignKey=@ForeignKey(name="FK_FILTER_USER_REF_OPERATOR", foreignKeyDefinition="FOREIGN KEY (operator) REFERENCES internal_user (id)"))
    public InternalUser getOperator() {
        return operator;
    }

    public void setOperator(InternalUser operator) {
        this.operator = operator;
    }
}
