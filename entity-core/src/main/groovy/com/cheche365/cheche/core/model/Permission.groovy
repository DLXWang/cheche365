package com.cheche365.cheche.core.model

import com.cheche365.cheche.core.context.ApplicationContextHolder
import com.cheche365.cheche.core.exception.BusinessException
import org.springframework.context.ApplicationContext

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.ForeignKey
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.JoinTable
import javax.persistence.ManyToMany
import javax.persistence.ManyToOne
import javax.persistence.OneToMany

@Entity
public class Permission {
    private Long id;
    private String name;
    private RoleType type;//角色类型：user,	internal user,partner,vendor,...
    private String description;
    private PermissionType permissionType;
    private List<Resource> resources;
    private String code;
    private Integer level; //0普通 1特殊
    private Set<RolePermission> rolePermissions;
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
    @JoinColumn(name="type", foreignKey=@ForeignKey(name="FK_PERMISSION_REF_ROLE_TYPE", foreignKeyDefinition="FOREIGN KEY (type) REFERENCES role_type(id)"))
    public RoleType getType() {
        return type;
    }

    public void setType(RoleType type) {
        this.type = type;
    }

    @Column(columnDefinition = "VARCHAR(2000)")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @ManyToOne
    @JoinColumn(name="permissionType", foreignKey=@ForeignKey(name="FK_PERMISSION_REF_PERMISSION_TYPE", foreignKeyDefinition="FOREIGN KEY (permission_type) REFERENCES permission_type(id)"))
    public PermissionType getPermissionType() {
        return permissionType;
    }

    public void setPermissionType(PermissionType permissionType) {
        this.permissionType = permissionType;
    }

    @ManyToMany
    @JoinTable(name="permission_resource",
        inverseJoinColumns =  @JoinColumn (name =  "resource"),
        joinColumns =  @JoinColumn (name =  "permission" ))
    public List<Resource> getResources() {
        return resources;
    }

    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }

    @Column(columnDefinition = "VARCHAR(20)")
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @OneToMany(mappedBy = "permission", fetch = FetchType.LAZY)
    public Set<RolePermission> getRolePermissions() {
        return rolePermissions;
    }

    public void setRolePermissions(Set<RolePermission> rolePermissions) {
        this.rolePermissions = rolePermissions;
    }

    @Column
    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public static class Enum{
        //出单中心
        public static Permission ORDER_CENTER;
        //运营中心
        public static Permission OPERATION_CENTER;
        //管理系统
        public static Permission ADMIN;
        //查看意向客户页
        public static Permission PURPOSE_CUSTOMER_SHOW;
        //编辑意向客户
        public static Permission PURPOSE_CUSTOMER_EDIT;
        //意向客户搜索信息
        public static Permission PURPOSE_CUSTOMER_FIND_SHOW;
        //意向客户搜索信息
        public static Permission PURPOSE_CUSTOMER_FIND_EDIT;
        //出单中心查看所有订单
        public static Permission INTERNAL_USER_PERMISSION_ALL_ORDER;
        //电销中心查看所有用户
        public static Permission TEL_MARKETING_CENTER_PERMISSION_ALL_USER;

        static {
            ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext();
            if (applicationContext != null) {
                def permissionRepository = applicationContext.getBean('permissionRepository');
                ORDER_CENTER = permissionRepository.findFirstByCode("or");
                OPERATION_CENTER = permissionRepository.findFirstByCode("op");
                ADMIN = permissionRepository.findFirstByCode("ad");
                PURPOSE_CUSTOMER_SHOW = permissionRepository.findFirstByCode("or0601");
                PURPOSE_CUSTOMER_EDIT = permissionRepository.findFirstByCode("or060101");
                PURPOSE_CUSTOMER_FIND_SHOW = permissionRepository.findFirstByCode("or060102");
                PURPOSE_CUSTOMER_FIND_EDIT = permissionRepository.findFirstByCode("or060103");
                INTERNAL_USER_PERMISSION_ALL_ORDER = permissionRepository.findFirstByCode("or0106");
                TEL_MARKETING_CENTER_PERMISSION_ALL_USER = permissionRepository.findFirstByCode("or0605");
            }else{
                throw new BusinessException(BusinessException.Code.INTERNAL_SERVICE_ERROR, "Permission 初始化失败");
            }
        }
    }
}
