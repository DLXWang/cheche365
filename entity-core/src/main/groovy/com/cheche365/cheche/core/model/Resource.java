package com.cheche365.cheche.core.model;

import javax.persistence.*;
import java.util.List;

/**
 * Created by wangfei on 2015/9/11.
 */
@Entity
public class Resource {
    private Long id;
    private String name;
    private Resource parent;
    private ResourceType resourceType;
    private Integer level;
    private List<Permission> permissions;

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

    @ManyToOne
    @JoinColumn(name = "parent", foreignKey=@ForeignKey(name="FK_RESOURCE_REF_PARENT_RESOURCE", foreignKeyDefinition="FOREIGN KEY (parent) REFERENCES resource(id)"))
    public Resource getParent() {
        return parent;
    }

    public void setParent(Resource parent) {
        this.parent = parent;
    }

    @ManyToOne
    @JoinColumn(name = "resourceType", foreignKey=@ForeignKey(name="FK_RESOURCE_REF_RESOURCE_TYPE", foreignKeyDefinition="FOREIGN KEY (resource_type) REFERENCES resource_type(id)"))
    public ResourceType getResourceType() {
        return resourceType;
    }

    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    @Column(columnDefinition = "tinyint(1)")
    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    @ManyToMany(mappedBy = "resources")
    public List<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }
}
