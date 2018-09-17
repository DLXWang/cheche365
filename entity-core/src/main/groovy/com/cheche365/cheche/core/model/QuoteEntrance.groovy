package com.cheche365.cheche.core.model

import com.cheche365.cheche.core.util.RuntimeUtil
import com.google.common.collect.Maps

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

/**
 * Created by chennan on 2015/12/21.
 */
@Entity
public class QuoteEntrance implements Serializable {
    private static final long serialVersionUID = 1L

    private Long id;
    private String entrance;
    private String description;

    @Id
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(columnDefinition = "VARCHAR(100)")
    public String getEntrance() {
        return entrance;
    }

    public void setEntrance(String entrance) {
        this.entrance = entrance;
    }

    @Column(columnDefinition = "VARCHAR(2000)")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static class Enum {
        //报价
        public static QuoteEntrance QUOTE_1;
        //预约
        public static QuoteEntrance APPOINTMENT_2;

        public static Map<String, QuoteEntrance> ENTRANCES = Maps.newHashMap();

        static {
            RuntimeUtil.loadEnum('quoteEntranceRepository', QuoteEntrance, Enum)
            ENTRANCES.put("1", QUOTE_1);
            ENTRANCES.put("2", APPOINTMENT_2);
        }
    }
}
