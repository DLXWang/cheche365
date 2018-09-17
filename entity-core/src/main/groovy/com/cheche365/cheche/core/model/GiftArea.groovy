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
 * Created by mahong on 2016/1/25.
 * 配置优惠券/兑换码支持的地区
 * 缺省值：默认支持所有已经开通的地区
 */
@Entity
public class GiftArea {

    private Long id;
    private Area area;
    private SourceType sourceType;
    private Long source;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "area", foreignKey = @ForeignKey(name = "FK_GIFT_AREA_REF_AREA", foreignKeyDefinition = "FOREIGN KEY (area) REFERENCES area(id)"))
    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    @ManyToOne
    @JoinColumn(name = "source_type", foreignKey = @ForeignKey(name = "FK_GIFT_AREA_REF_SOURCE_TYPE", foreignKeyDefinition = "FOREIGN KEY (source_type) REFERENCES source_type(id)"))
    public SourceType getSourceType() {
        return sourceType;
    }

    public void setSourceType(SourceType sourceType) {
        this.sourceType = sourceType;
    }

    @Column(columnDefinition = "BIGINT(20)")
    public Long getSource() {
        return source;
    }

    public void setSource(Long source) {
        this.source = source;
    }

    public static class Enum {

        public static Map GIFT_AREA_MAP
        static {
            GIFT_AREA_MAP = ApplicationContextHolder.getApplicationContext().getBean('giftAreaRepository')
                .findAll()
                .groupBy {GiftChannel.cacheKey(it.sourceType, it.source)}
                .collectEntries {[(it.key): it.value.area.id]}
        }

        static boolean containsArea(SourceType sourceType=SourceType.Enum.WECHATRED_2, Long source, Area area){
            def areas = GIFT_AREA_MAP.get(GiftChannel.cacheKey(sourceType, source))
            !areas || areas.contains(area.id)
        }

    }
}
