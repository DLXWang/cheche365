package com.cheche365.cheche.core.model

import com.cheche365.cheche.common.util.Cn2Spell
import com.cheche365.cheche.core.context.ApplicationContextHolder
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.apache.commons.lang3.builder.ToStringBuilder
import org.apache.commons.lang3.builder.ToStringStyle
import org.springframework.beans.BeanUtils

import javax.persistence.*

import static com.cheche365.cheche.core.model.AreaType.Enum.CITY_3
import static com.cheche365.cheche.core.model.AreaType.Enum.MUNICIPALITY_2

/**
 * 编码规则，使用国家统计局制定的行政区划代码标准：<br>
 * <a href="http://www.stats.gov.cn/tjsj/tjbz/xzqhdm/201401/t20140116_501070.html">最新县及县以上行政区划代码（截止2013年8月31日）</a>
 *
 */
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
class Area implements Serializable{
    private static final long serialVersionUID = 1L

    public static final String PROVINCE_LIKE_AREA_SUFFIX = "0000";  //省级行政单位区域码后缀，包括省，直辖市，特别行政区
    private Long id;
    private String name; //地区名
    private AreaType type; //区域类型：省；直辖市；城市；区/县；特别行政区
    private String shortCode;//区域缩写
    private Boolean active;//是否启用
    @JsonIgnore
    private Integer cityCode;
    private String postalCode;
    private List<Area> children;
    private boolean reform;  //是否费改

    @Id
    Long getId() {
        return id;
    }

    void setId(Long id) {
        this.id = id;
    }

    @Column(columnDefinition = "VARCHAR(100)", updatable= false)
    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    @ManyToOne
    @JoinColumn(name = "type", foreignKey = @ForeignKey(name = "FK_AREA_REF_AREA_TYPE", foreignKeyDefinition = "FOREIGN KEY (type) REFERENCES area_type(id)"))
    AreaType getType() {
        return type;
    }

    void setType(AreaType type) {
        this.type = type;
    }

    @Column(name = "city_code")
    Integer getCityCode() {
        return cityCode;
    }

    void setCityCode(Integer cityCode) {
        this.cityCode = cityCode;
    }


    @Column(columnDefinition = "LONG")
    String getPostalCode() {
        return postalCode;
    }

    void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }


    @Transient
    List<Area> getChildren() {
        return children;
    }

    void setChildren(List<Area> children) {
        this.children = children;
    }

    @Column(columnDefinition = "TINYINT(1)")
    boolean isReform() {
        return reform;
    }

    void setReform(boolean reform) {
        this.reform = reform;
    }


    static class Enum {

        public static Map<Long, Area> ALL_AREAS = new HashMap()
        public static Map<Long, Area> ALL_AREAS_PINYIN = [:]

        public static List<Area> BJAreas;  //just for testing

        public static List<Area> ACTIVE_AREAS;

        public static Area BJ
        public static Area SZ
        public static Area NJ
        public static Area TJ

        public static List<Area> CITIES

        static  {

            def areaRepository = ApplicationContextHolder.getApplicationContext()?.getBean('areaRepository')
            if(areaRepository) {

                List<Area> all = areaRepository.findAll();
                ALL_AREAS = all.collectEntries {
                    [it.id, it]
                }
                ALL_AREAS_PINYIN = all.collectEntries {
                    (it.shortCode) ? [it.id, [
                        simplePinyin: Cn2Spell.converterToFirstSpell(it.name.split("市")[0]),
                        pinyin      : Cn2Spell.converterToSpell(it.name.split("市")[0])
                    ]] : [:]
                }

                initBJarea(areaRepository);
                BJ = areaRepository.findOne(110000L)
                SZ = areaRepository.findOne(440300L)
                NJ = areaRepository.findOne(320100L)
                TJ = areaRepository.findOne(120000L)

                ACTIVE_AREAS = areaRepository.findShortAreasList();
                CITIES = areaRepository.findByType(Arrays.asList(MUNICIPALITY_2.getId(), CITY_3.getId()))
            }
        }

        static Area getValueByCode(Long code) {
            return ALL_AREAS.get(code);
        }

        static void initBJarea(areaRepository) {
            BJAreas = areaRepository.findAll().findAll { area ->
                area.getId().toString().startsWith("11") && (area.getType().getId() != 2)
            }
        }

        static List<Area> getActiveAreas(){
            return ACTIVE_AREAS;
        }

        static Area findByTypeAndShortCode(Long type, String shortCode) {
            ALL_AREAS.values().find {
                it.type?.id == type && it.shortCode?.contains(shortCode)
            }
        }

        static Area findByName(String name){
            return CITIES.find { it.name == name }
        }
    }

    @Column(columnDefinition = "VARCHAR(100)", updatable= false)
    String getShortCode() {
        return shortCode;
    }

    void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    Boolean isActive() {
        return active;
    }

    void setActive(Boolean active) {
        this.active = active;
    }

    static boolean isBJArea(Area area) {
        if (area != null) {
            return area.getId().toString().startsWith("11");
        }
        return false;
    }

    static boolean isSZArea(Area area) {
        if (area != null) {
            return area.getId().toString().startsWith("4403");
        }
        return false;
    }

    /**
     * 是否为直辖市
     */
    static boolean isMunicipalityArea(long areaId) {
        String areaIdStr = String.valueOf(areaId);
        return areaIdStr.startsWith("11") || areaIdStr.startsWith("12") || areaIdStr.startsWith("31") || areaIdStr.startsWith("50");
    }

    @Override
    String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    @Override
    int hashCode() {
        return id.hashCode();
    }

    @Override
    boolean equals(Object obj) {
        if (this.is(obj) ) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Area area = (Area) obj;
        return id.equals(area.getId());
    }

    Area clone() {
        Area target = new Area()
        BeanUtils.copyProperties(this, target)
        return target
    }
}
