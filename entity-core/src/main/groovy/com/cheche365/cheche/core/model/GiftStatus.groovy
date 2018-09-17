package com.cheche365.cheche.core.model

import com.cheche365.cheche.core.repository.GiftStatusRepository
import com.cheche365.cheche.core.util.RuntimeUtil
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder

import javax.persistence.*

@Entity
public class GiftStatus {
    private Long id;
    /*
    * 福利状态：已创建；已发送；已使用；已过期；已取消
    */
    private String status;
    private String description;
    private String rank;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long getId() {
        return id;
    }

    void setId(Long id) {
        this.id = id;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    String getStatus() {
        return status;
    }

    void setStatus(String status) {
        this.status = status;
    }

    @Column(columnDefinition = "VARCHAR(2000)")
    String getDescription() {
        return description;
    }

    void setDescription(String description) {
        this.description = description;
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getRank() {
        return rank;
    }

    void setRank(String rank) {
        this.rank = rank;
    }

    static class Enum {
        public static GiftStatus CREATED_1, USED_3, EXCEEDED_4, CANCLED_5, WAITDELIVERED_6;

        public static List<Long> ALL_DISPLAYABLE_IDS
        public static List<Long> ALL_VALID_IDS

        public static List<GiftStatus> ALL_VALID_STATUS

        public static List<GiftStatus> ALL_NEED_SYNC_STATUS
        public static List<Long> ALL_NEED_SYNC_STATUS_IDS

        static {
            RuntimeUtil.loadEnum(GiftStatusRepository, GiftStatus, Enum)

            ALL_DISPLAYABLE_IDS = [CREATED_1.getId(), USED_3.getId(), EXCEEDED_4.getId(), WAITDELIVERED_6.getId()]
            ALL_VALID_IDS = [CREATED_1.getId()]

            ALL_VALID_STATUS = [CREATED_1]

            ALL_NEED_SYNC_STATUS =[CREATED_1, USED_3, EXCEEDED_4]
            ALL_NEED_SYNC_STATUS_IDS = [CREATED_1.getId(), USED_3.getId(), EXCEEDED_4.getId()]
        }

    }

    @Override
    public boolean equals(Object o) {
        return getClass().equals(o.getClass()) && EqualsBuilder.reflectionEquals(this, o, 'status', 'description', 'rank');
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, 'name', 'description', 'rank');
    }
}
