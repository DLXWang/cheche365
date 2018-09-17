package com.cheche365.cheche.core.model

import com.cheche365.cheche.core.context.ApplicationContextHolder

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.ForeignKey
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

/**
 * Created by xu.yelong on 2016/8/22.
 */
@Entity
class PurchaseOrderImageType {

    private Long id;
    private String name;
    private Long parentId;
    private InternalUser operator;
    private Date createTime;
    private Date updateTime;
    private String sampleUrl;
    private boolean reusable;
    private Integer externalType;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(columnDefinition = "VARCHAR(50)")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(columnDefinition = "BIGINT(20)")
    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    @ManyToOne
    @JoinColumn(name = "operator", foreignKey = @ForeignKey(name = "FK_PURCHASE_ORDER_IMAGE_TYPE_REF_INTERNAL_USER", foreignKeyDefinition = "FOREIGN KEY (operator) REFERENCES internal_user(id)"))
    public InternalUser getOperator() {
        return operator;
    }

    public void setOperator(InternalUser operator) {
        this.operator = operator;
    }

    @Column(columnDefinition = "DATETIME")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Column(columnDefinition = "DATETIME")
    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Column(columnDefinition = "VARCHAR(100)")
    public String getSampleUrl() {
        return sampleUrl;
    }

    public void setSampleUrl(String sampleUrl) {
        this.sampleUrl = sampleUrl;
    }

    @Column()
    public boolean isReusable() {
        return reusable;
    }

    public void setReusable(boolean reusable) {
        this.reusable = reusable;
    }

    @Column(columnDefinition = "bigint(4)")
    public Integer getExternalType() {
        return externalType
    }

    public void setExternalType(Integer externalType) {
        this.externalType = externalType
    }

    @Override
    boolean equals(Object o) {
        if (this.is(o)) return true;
        if (o == null || !getClass().is(o.getClass())) return false;

        PurchaseOrderImageType imageType = (PurchaseOrderImageType) o;

        return id == imageType.id;

    }

    @Override
    int hashCode() {
        return id.hashCode();
    }

    static class Enum {

        public static List<PurchaseOrderImageType> ALL = []
        public static List<PurchaseOrderImageType> REUSABLE = []
        public static List<PurchaseOrderImageType> IDENTITY_PATH_GROUP = []
        public static List<PurchaseOrderImageType> AUTO_PATH_GROUP = []
        public static Map<Long, Map<String, String>> STATUS_UPLOAD_MAP = [:]

        public static PurchaseOrderImageType IDENTITY
        public static PurchaseOrderImageType DRIVING_LICENSE
        public static PurchaseOrderImageType VALIDATE_CAR
        public static PurchaseOrderImageType ANSWERN_VALIDATE_CAR
        public static PurchaseOrderImageType BAOXIAN_IMAGE_TYPE
        public static PurchaseOrderImageType SINOSAFE_IMAGE_TYPE
        public static PurchaseOrderImageType API_CUSTOM_IMAGE_TYPE
        public static PurchaseOrderImageType AGENT_PARSER_IMAGE_TYPE
        public static PurchaseOrderImageType VEHICLE_EXAMINATIOS_IMAGE_TYPE
        public static Long API_CUSTOM_IMAGE_SUB_TYPE = 6001L


        static {
            def imageTypeRepository = ApplicationContextHolder.getApplicationContext().getBean('purchaseOrderImageTypeRepository')
            ALL = imageTypeRepository.findAll()
            REUSABLE = ALL.findAll { it.reusable }
            IDENTITY_PATH_GROUP = REUSABLE.findAll { it.parentId == 1 || it.parentId == 6 }
            AUTO_PATH_GROUP = REUSABLE - IDENTITY_PATH_GROUP
            IDENTITY = ALL.find { it.id == 1L }
            DRIVING_LICENSE = ALL.find { it.id == 2L }
            VALIDATE_CAR = ALL.find { it.id == 3L }
            ANSWERN_VALIDATE_CAR = ALL.find { it.id == 300L }
            BAOXIAN_IMAGE_TYPE = ALL.find { it.id == 4000L }
            SINOSAFE_IMAGE_TYPE = ALL.find { it.id == 5000L }
            API_CUSTOM_IMAGE_TYPE = ALL.find { it.id == 6000L }
            AGENT_PARSER_IMAGE_TYPE = ALL.find { it.id == 7000L }
            VEHICLE_EXAMINATIOS_IMAGE_TYPE = ALL.find {it.id == 7020L}

            STATUS_UPLOAD_MAP = [
                (IDENTITY)            : [
                    'title': '被保险人身份证',
                    'desc' : ''
                ],
                (DRIVING_LICENSE)     : [
                    'title': '行驶证正本',
                    'desc' : ''
                ],
                (VALIDATE_CAR)        : [
                    'title': '验车照片',
                    'desc' : '四角带车牌带车全貌各1张，共4张，车架号与当日的购物小票或是日期报纸合照1张'
                ],
                (ANSWERN_VALIDATE_CAR): [
                    'title': '验车照片',
                    'desc' : '四角带车牌带车全貌各1张，共4张，车架号与当日的购物小票或是日期报纸合照1张'
                ],
                (AGENT_PARSER_IMAGE_TYPE):[
                    'title': '验车照片',
                    'desc' : '四角带车牌带车全貌各1张，共4张，车架号与当日的购物小票或是日期报纸合照1张'
                ],
                (VEHICLE_EXAMINATIOS_IMAGE_TYPE): [
                    'title': '验车照片',
                    'desc' : '四角带车牌带车全貌各1张，共4张，车架号与当日的购物小票或是日期报纸合照1张'
                ]
            ]
        }

        static Map getUploadDesc(PurchaseOrderImageType parentImageType) {
            PurchaseOrderImageType.Enum.STATUS_UPLOAD_MAP.get(parentImageType) ?: ['title': '', 'desc': '']
        }
    }
}
