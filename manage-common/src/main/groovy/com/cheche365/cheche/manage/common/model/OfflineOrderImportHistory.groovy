package com.cheche365.cheche.manage.common.model

import com.cheche365.cheche.core.model.Area
import com.cheche365.cheche.core.model.InternalUser

import javax.persistence.*

/**
 * 线下数据导入 , 泛华数据
 * Created by yinJianBin on 2017/11/5.
 */
@Entity
class OfflineOrderImportHistory {

    private Long id;                //ID
    private Date startTime;             //开始时间
    private Date endTime;               //结束时间
    private InternalUser operator;              //操作人
    private Date createTime;                //上传时间
    private Date updateTime;                //修改时间
    private String filePath;                //上传文件路径
    private String successPath;             //成功文件路径
    private String failedPath;              //失败文件路径
    private Boolean status;             //状态
    private String comment;             //备注
    private String fileName;             //上传文件名称
    private Integer type;               //上传文件类型  1 泛华 ,2 保险公司
    private Area area;                  //地区
    private String description;         //描述
    private Date balanceTime         //	结算时间
    private Integer successSize = 0        //保存成功数量

    public static final Integer TYPE_FANHUA = 1
    public static final Integer TYPE_COMPANY = 2
    public static final Integer TYPE_FANHUA_ADDED = 3
    public static final Integer TYPE_FANHUA_TEMP = 4

    static Map<Integer, String> getDataTypeMap() {
        Map<Integer, String> dataTypeMap = [:]
        dataTypeMap.put(TYPE_FANHUA, "泛华")
        dataTypeMap.put(TYPE_COMPANY, "保险公司")
        dataTypeMap.put(TYPE_FANHUA_ADDED, "泛华补充")
        dataTypeMap.put(TYPE_FANHUA_TEMP, "泛华台帐")
        return dataTypeMap
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "operator", foreignKey = @ForeignKey(name = "FK_TEL_MARKETING_CENTER_ORDER_REF_INTERNAL_USER", foreignKeyDefinition = "FOREIGN KEY (operator) REFERENCES internal_user(id)"))
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

    @Column(columnDefinition = "DATETIME")
    Date getStartTime() {
        return startTime
    }

    void setStartTime(Date startTime) {
        this.startTime = startTime
    }

    @Column(columnDefinition = "DATETIME")
    Date getEndTime() {
        return endTime
    }

    void setEndTime(Date endTime) {
        this.endTime = endTime
    }

    @Column(columnDefinition = "VARCHAR(150)")
    String getFilePath() {
        return filePath
    }

    void setFilePath(String filePath) {
        this.filePath = filePath
    }

    @Column(columnDefinition = "VARCHAR(150)")
    String getSuccessPath() {
        return successPath
    }

    void setSuccessPath(String successPath) {
        this.successPath = successPath
    }

    @Column(columnDefinition = "VARCHAR(150)")
    String getFailedPath() {
        return failedPath
    }

    void setFailedPath(String failedPath) {
        this.failedPath = failedPath
    }

    @Column(columnDefinition = "tinyint(1)")
    Boolean getStatus() {
        return status
    }

    void setStatus(Boolean status) {
        this.status = status
    }

    @Column(columnDefinition = "VARCHAR(200)")
    String getComment() {
        return comment
    }

    void setComment(String comment) {
        this.comment = comment
    }

    @Column(columnDefinition = "VARCHAR(100)")
    String getFileName() {
        return fileName
    }

    void setFileName(String fileName) {
        this.fileName = fileName
    }

    @Column(columnDefinition = "tinyint(1)")
    Integer getType() {
        return type
    }

    void setType(Integer type) {
        this.type = type
    }

    @ManyToOne
    @JoinColumn(name = "area", foreignKey = @ForeignKey(name = "FK_OFFLINE_ORDER_IMPORT_HISTORY_REF_AREA", foreignKeyDefinition = "FOREIGN KEY (area) REFERENCES area(id)"))
    Area getArea() {
        return area
    }

    void setArea(Area area) {
        this.area = area
    }

    @Column(columnDefinition = "VARCHAR(100)")
    String getDescription() {
        return description
    }

    void setDescription(String description) {
        this.description = description
    }

    @Column(columnDefinition = "DATETIME")
    Date getBalanceTime() {
        return balanceTime
    }

    void setBalanceTime(Date balanceTime) {
        this.balanceTime = balanceTime
    }

    @Column(columnDefinition = "INTEGER(11)")
    Integer getSuccessSize() {
        return successSize
    }

    void setSuccessSize(Integer successSize) {
        this.successSize = successSize
    }
}
