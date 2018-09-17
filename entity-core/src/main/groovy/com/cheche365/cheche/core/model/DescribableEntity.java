package com.cheche365.cheche.core.model;

import com.cheche365.cheche.core.repository.BaseEntity;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by zhengwei on 11/6/15.
 * 有description字段的实体类。
 */

@MappedSuperclass
public class DescribableEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -3897653387037745849L;
    private String description;

    @Column(columnDefinition = "VARCHAR(2000)")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void appendDescription(String desc) {

        String happenedAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
        String head = StringUtils.isBlank(this.getDescription()) ? "" : (this.getDescription() + ";");
        String fullDesc = head + happenedAt + " " + desc;

        if (fullDesc.length() > 2000) {
            fullDesc = fullDesc.substring(fullDesc.length() - 2000);
        }
        this.setDescription(fullDesc);
    }
}
