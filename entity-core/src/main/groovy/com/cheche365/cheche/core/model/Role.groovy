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
import javax.persistence.ManyToOne
import javax.persistence.OneToMany

@Entity
public class Role {

    private Long id;
    private String name;
    private RoleType type;//角色类型：user,	internal user,partner,vendor,...
    private String description;
    private boolean disable;
    private Integer level;// 0普通 1特殊
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
    @JoinColumn(name = "type", foreignKey = @ForeignKey(name = "FK_ROLE_REF_ROLE_TYPE", foreignKeyDefinition = "FOREIGN KEY (type) REFERENCES role_type(id)"))
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

    @Column(columnDefinition = "tinyint(1)")
    public boolean isDisable() {
        return disable;
    }

    public void setDisable(boolean disable) {
        this.disable = disable;
    }

    @Column
    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    public Set<RolePermission> getRolePermissions() {
        return rolePermissions;
    }

    public void setRolePermissions(Set<RolePermission> rolePermissions) {
        this.rolePermissions = rolePermissions;
    }

    public static class Enum {
        //客服人员：客服
        public static Role INTERNAL_USER_ROLE_CUSTOMER;
        //客服人员：内勤
        public static Role INTERNAL_USER_ROLE_INTERNAL;
        //客服人员：外勤
        public static Role INTERNAL_USER_ROLE_EXTERNAL;
        //客服人员：管理员
        public static Role INTERNAL_USER_ROLE_ADMIN;
        //客服人员：订单状态变更
        public static Role INTERNAL_USER_ROLE_STATUS_CHANGE;
        //内部人员：CPS渠道
        public static Role INTERNAL_USER_ROLE_CPS;
        //录入人员
        public static Role INTERNAL_USER_ROLE_INPUT;
        //内部人员：电话专员
        public static Role INTERNAL_USER_ROLE_TEL_COMMISSIONER;
        //内部人员：电话主管
        public static Role INTERNAL_USER_ROLE_TEL_MASTER;
        //内部人员
        public static List<Role> INTERNAL_USER_ROLE_ALL;
        //电销经理
        public static Role INTERNAL_USER_ROLE_TEL_MANAGER;
        //toA报价数据指定跟进人
        public static Role INTERNAL_USER_ROLE_TOA_QUOTE_OPERATOR;

        static {
            ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext();
            if (applicationContext != null) {
                def roleRepository = applicationContext.getBean('roleRepository');
                INTERNAL_USER_ROLE_ALL = roleRepository.findByType(RoleType.Enum.INTERNAL_USER);
                INTERNAL_USER_ROLE_CUSTOMER = roleRepository.findFirstByNameAndType("出单中心出单员", RoleType.Enum.INTERNAL_USER);
                INTERNAL_USER_ROLE_INTERNAL = roleRepository.findFirstByNameAndType("出单中心内勤", RoleType.Enum.INTERNAL_USER);
                INTERNAL_USER_ROLE_EXTERNAL = roleRepository.findFirstByNameAndType("出单中心外勤", RoleType.Enum.INTERNAL_USER);
                INTERNAL_USER_ROLE_ADMIN = roleRepository.findFirstByNameAndType("出单中心管理员", RoleType.Enum.INTERNAL_USER);
                INTERNAL_USER_ROLE_STATUS_CHANGE = roleRepository.findFirstByNameAndType("出单中心修改状态", RoleType.Enum.INTERNAL_USER);
                INTERNAL_USER_ROLE_CPS = roleRepository.findFirstByNameAndType("出单中心CPS", RoleType.Enum.INTERNAL_USER);
                INTERNAL_USER_ROLE_INPUT = roleRepository.findFirstByNameAndType("出单中心录单员", RoleType.Enum.INTERNAL_USER);
                INTERNAL_USER_ROLE_TEL_COMMISSIONER = roleRepository.findFirstByNameAndType("电话专员", RoleType.Enum.INTERNAL_USER);
                INTERNAL_USER_ROLE_TEL_MASTER = roleRepository.findFirstByNameAndType("电话主管", RoleType.Enum.INTERNAL_USER);
                INTERNAL_USER_ROLE_TEL_MANAGER = roleRepository.findFirstByNameAndType("电销经理", RoleType.Enum.INTERNAL_USER);
                INTERNAL_USER_ROLE_TOA_QUOTE_OPERATOR = roleRepository.findFirstByNameAndType("toA报价电销跟进人", RoleType.Enum.INTERNAL_USER);
            } else {
                throw new BusinessException(BusinessException.Code.INTERNAL_SERVICE_ERROR, "Role 初始化失败");
            }
        }

        public static List<Role> allOrderCenterRoles() {
            return Arrays.asList(
                    INTERNAL_USER_ROLE_CUSTOMER, INTERNAL_USER_ROLE_INTERNAL, INTERNAL_USER_ROLE_ADMIN,
                    INTERNAL_USER_ROLE_INPUT, INTERNAL_USER_ROLE_TEL_COMMISSIONER, INTERNAL_USER_ROLE_TEL_MASTER, INTERNAL_USER_ROLE_TOA_QUOTE_OPERATOR
            );
        }

        public static List<String> allOrderCenterRoleIds() {
            return Arrays.asList(
                    INTERNAL_USER_ROLE_CUSTOMER.getId().toString(),
                    INTERNAL_USER_ROLE_INTERNAL.getId().toString(),
                    INTERNAL_USER_ROLE_ADMIN.getId().toString(),
                    INTERNAL_USER_ROLE_INPUT.getId().toString(),
                    INTERNAL_USER_ROLE_TEL_COMMISSIONER.getId().toString(),
                    INTERNAL_USER_ROLE_TEL_MASTER.getId().toString(),
                    INTERNAL_USER_ROLE_TOA_QUOTE_OPERATOR.getId().toString()
            );
        }

        public static Role getRoleById(Long roleId) {
            List<Role> orderCenterRoleList = Role.Enum.INTERNAL_USER_ROLE_ALL;
            for (Role role : orderCenterRoleList) {
                if (role.getId().equals(roleId)) {
                    return role;
                }
            }
            return null;
        }


    }

    @Override
    public boolean equals(Object obj) {
        Role role=(Role)obj;
        return role.getId().equals(this.getId());
    }
}
