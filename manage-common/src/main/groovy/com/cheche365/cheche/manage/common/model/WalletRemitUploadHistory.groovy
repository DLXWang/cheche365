package com.cheche365.cheche.manage.common.model

import com.cheche365.cheche.core.model.DescribableEntity
import com.cheche365.cheche.core.model.InternalUser

import javax.persistence.*

/**
 * 财务提现信息统计报表上传历史表
 * Created by yinJianBin on 2018/4/5.
 */
@Entity
class WalletRemitUploadHistory extends DescribableEntity {

    private InternalUser operator;              //操作人
    private String filePath;                //上传文件路径
    private Integer status;             //状态
    private String fileName;             //上传文件名称
    private String errorFileName;             //含有失败原因的文件
    private Integer successSize = 0        //保存成功数量


    @ManyToOne
    @JoinColumn(name = "operator", foreignKey = @ForeignKey(name = "FK_WALLET_REMIT_UPLOAD_HISTORY_REF_INTERNAL_USER", foreignKeyDefinition = "FOREIGN KEY (operator) REFERENCES internal_user(id)"))
    public InternalUser getOperator() {
        return operator;
    }

    public void setOperator(InternalUser operator) {
        this.operator = operator;
    }

    @Column(columnDefinition = "VARCHAR(150)")
    String getFilePath() {
        return filePath
    }

    void setFilePath(String filePath) {
        this.filePath = filePath
    }


    @Column(columnDefinition = "tinyint(1)")
    Integer getStatus() {
        return status
    }

    void setStatus(Integer status) {
        this.status = status
    }

    @Column(columnDefinition = "VARCHAR(100)")
    String getFileName() {
        return fileName
    }

    void setFileName(String fileName) {
        this.fileName = fileName
    }


    @Column(columnDefinition = "INTEGER(11)")
    Integer getSuccessSize() {
        return successSize
    }

    void setSuccessSize(Integer successSize) {
        this.successSize = successSize
    }

    @Column
    String getErrorFileName() {
        return errorFileName
    }

    void setErrorFileName(String errorFileName) {
        this.errorFileName = errorFileName
    }
}
