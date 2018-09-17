package com.cheche365.cheche.core.model.tide

import com.cheche365.cheche.core.model.DescribableEntity
import com.cheche365.cheche.core.model.InternalUser

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.ForeignKey
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

/**
 * Created by yinJianBin on 2018/4/19.
 */
@Entity
class UploadFile extends DescribableEntity {

    String filePath //文件路径
    String fileName //文件名称
    Integer sourceType  //文件来源
    Long sourceId   //关联表id
    Integer status = 0  //状态
    InternalUser operator //操作人

    static class Enum {
        static final Integer STATUS_DISABLE = 0
        static final Integer STATUS_ACTIVE = 1

        final static Integer SOURCE_TYPE_TIDE_CONTRACT = 1  //合约文件

    }

    @Column
    String getFilePath() {
        return filePath
    }

    @Column
    String getFileName() {
        return fileName
    }

    @Column
    Integer getSourceType() {
        return sourceType
    }

    @Column
    Long getSourceId() {
        return sourceId
    }

    @Column
    Integer getStatus() {
        return status
    }

    @ManyToOne
    @JoinColumn(name = "operator", foreignKey = @ForeignKey(name = "FK_UPLOAD_FILE_REF_INTERNAL_USER", foreignKeyDefinition = "FOREIGN KEY (operator) REFERENCES internal_user(id)"))
    InternalUser getOperator() {
        return operator
    }
}
