package com.cheche365.cheche.core.service

import com.cheche365.cheche.common.util.Cn2Spell
import com.cheche365.cheche.core.context.ApplicationContextHolder
import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.Area
import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.InsuranceCompany
import com.cheche365.cheche.core.model.QuoteFlowConfig
import com.cheche365.cheche.core.model.agent.AgentInviteCodeArea
import com.cheche365.cheche.core.model.agent.AgentLevel
import com.cheche365.cheche.core.model.agent.ChannelAgent
import com.cheche365.cheche.core.model.agent.ChecheAgentInviteCode
import com.cheche365.cheche.core.repository.AreaRepository
import com.cheche365.cheche.core.repository.QuoteFlowConfigRepository
import com.cheche365.cheche.core.repository.agent.AgentInviteCodeAreaRepository
import com.cheche365.cheche.core.util.CacheUtil
import com.google.common.collect.Lists
import groovy.util.logging.Slf4j
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.math.NumberUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service

import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Path
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root

import static com.cheche365.cheche.core.model.InsuranceCompany.toInsuranceCompany
import static com.cheche365.cheche.core.model.Area.Enum.ALL_AREAS_PINYIN

/**
 * Created by xu.yelong on 2015/11/14.
 */
@Service
@Slf4j
public class AreaService {

    @Autowired
    private AreaRepository areaRepository;
    @Autowired
    private AutoService autoService;
    @Autowired
    private QuoteFlowConfigRepository quoteFlowConfigRepository
    @Autowired
    private AgentInviteCodeAreaRepository aicaRepo

    public static final String BEIJING_CODE = "110000";
    public static final String TIANJIN_CODE = "120000";
    public static final String SHANGHAI_CODE = "310000";
    public static final String CHONGQING_CODE = "500000";
    public static final List<Long> POPULAR_AREAS = [
        110000l, 310000l, 440100l, 440300l, 500000l
    ]

    /**
     * 获取所有省和直辖市列表
     *
     * @param typeIdList
     * @return
     */
    public List<Area> findProvinceAreaList(List<Long> typeIdList) {
        List<Area> provinceList = areaRepository.findAll(new Specification<Area>() {
            public Predicate toPredicate(Root<Area> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Path<String> typePath = root.get("type").get("id");
                query.where(typePath.in(typeIdList));
                return null;
            }
        });
      return provinceList;
    }

    /**
     * 根据省id查询市的列表
     *
     * @param provinceId
     * @param endPosition
     * @param childType
     * @return
     */
    public List<Area> findCityAreaListByProvinceId(String provinceId, int endPosition, Long childType) {
        //校验省是否存在
        if (StringUtils.isBlank(provinceId)) {
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "区域id为空.id:" + provinceId);
        }
        if (!NumberUtils.isNumber(provinceId)) {
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "区域id不存在.id:" + provinceId);
        }
        if (areaRepository.countById(Long.valueOf(provinceId)) == 0) {
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "区域id不存在.id:" + provinceId);
        }
        if (provinceId.length() > endPosition) {
            List<Area> cityAreaList = areaRepository.findCityAreaListByProvinceId(provinceId.substring(0, endPosition) + "%", childType);
            return cityAreaList;
        }
        return null;
    }

    public Area findById(Long id){
        return areaRepository.findOne(id);
    }

    /**
     * 查询所有可用报价的区域
     * @return
     */
    public List<Area> getEnalbeAreas(){
        return areaRepository.findByActive(true);
    }

    /**
     * 根据id的集合 得到name的集合
     * @param ids
     * @return
     */
    public List<String> getPermissionNameById(List<Long> ids){
       return areaRepository.findPermissionNames(ids);
    }

    String getAreasWithChildren(Long versionMills) {
        String areasContents = "[]"
        if (versionMills == 0L) {
            def allAreas = areaRepository.findAll().collect { area ->
                [
                    id       : area.id,
                    name     : area.name,
                    type     : area.type.id,
                    shortCode: area.shortCode
                ]
            }
            def provinceAreas = allAreas.findAll { [1L, 2L, 5L].contains(it.type) }
            def cityAreas = allAreas.findAll { 3L == it.type }
            def districtAreas = allAreas.findAll { 4L == it.type }
            cityAreas.each { cityArea ->
                cityArea.children = districtAreas.findAll { districtArea -> cityArea.id.toString().substring(0, 4) == districtArea.id.toString().substring(0, 4) }
            }
            provinceAreas.each { provinceArea ->
                provinceArea.children = (provinceArea.type == 2L ? districtAreas : cityAreas).findAll { cityArea -> provinceArea.id.toString().substring(0, 2) == cityArea.id.toString().substring(0, 2) }
            }

            areasContents = CacheUtil.doJacksonSerialize(provinceAreas)
        }

        StringBuffer buf = new StringBuffer()
        buf.append("{").append("\"code\":").append("200").append(",").append("\"message\":").append("null").append(",");
        buf.append("\"debugMessage\":").append("null").append(",").append("\"data\":").append("{ \"areaList\":");
        buf.append(areasContents).append(",").append(" \"versionMillis\":").append((versionMills == 0L) ? System.currentTimeMillis() : versionMills).append("} ").append("}")
        return buf.toString()
    }

    @Cacheable(value = "areaGroupIncludeSI",keyGenerator = "cacheKeyGenerator")
    List getAreasWithSupplementInfo(Channel channel, ChannelAgent channelAgent) {
        List<Area> areaList = null
        if (Channel.findAgentChannel(channel) == Channel.Enum.PARTNER_CHEBAOYI_67) {
            areaList = getInviteCodeAreas(channelAgent)
        }

        if (!areaList) {
            areaList = quoteFlowConfigRepository.findAreasByChannel(channel.parent)
        }

        def aliasAreas = areaList.collect { area ->
            (area.shortCode ?: "").split(",").collect {
                [
                    id            : area.id,
                    name          : area.shortCode?.contains(",") ? area.name + "(" + it + ")" : area.name,
                    type          : area.type.id,
                    shortCode     : it,
                    popularArea   : POPULAR_AREAS.contains(area.id),
                    supplementInfo: autoService.getSupplementInfo(area.getId(), true, null),
                    pinyin        : ALL_AREAS_PINYIN.get(area.id)?.pinyin,
                    simplePinyin  : ALL_AREAS_PINYIN.get(area.id)?.simplePinyin
                ]
            }
        }.flatten().sort { a, b -> a.pinyin <=> b.pinyin }

        aliasAreas
    }

    public List<Map> findProvinceAreaMap(List<Long> typeIdList) {
        List<Map> areaDTOList = Lists.newArrayList();
        List<Area> areaList = areaRepository.findAll(new Specification<Area>() {

            public Predicate toPredicate(Root<Area> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Path<String> typePath = root.get("type").get("id");
                query.where(typePath.in(typeIdList));
                return null;
            }
        });
        areaList.each { area -> areaDTOList.add([id: area.getId(), type: area.getType().getId(), name: area.getName()]) }
        return areaDTOList;
    }

    public List<Map> findCityAreaMapByProvinceId(String provinceId, int endPosition, Long childType) {
        //校验省是否存在
        List<Map> cityAreaDTOList = Lists.newArrayList();
        if (StringUtils.isBlank(provinceId)) {
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "区域id为空.id:" + provinceId);
        }
        if (!NumberUtils.isNumber(provinceId)) {
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "区域id不存在.id:" + provinceId);
        }
        if (areaRepository.countById(Long.valueOf(provinceId)) == 0l) {
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "区域id不存在.id:" + provinceId);
        }
        if (provinceId.length() > endPosition) {
            List<Area> cityAreaList = areaRepository.findCityAreaListByProvinceId(provinceId.substring(0, endPosition) + "%", childType);
            cityAreaList.each { area ->
                cityAreaDTOList.add([id: area.getId(), type: area.getType().getId(), name: area.getName()])
            }
        }
        return cityAreaDTOList;
    }

    private static Map<String, Object> formatData(List<Area> areas) {
        def areaGroupMap = new TreeMap<>(), popularAreas = []

        areas.each { area ->
            def splitAreas = (area.shortCode ?: "").split(",").collect {
                [
                    id          : area.id,
                    shortCode   : it,
                    name        : area.shortCode?.contains(",") ? area.name + "(" + it + ")" : area.name,
                    pinyin      : ALL_AREAS_PINYIN.get(area.id)?.pinyin,
                    simplePinyin: ALL_AREAS_PINYIN.get(area.id)?.simplePinyin,
                ]
            }

            def pinyinFirstLetter = splitAreas[0].pinyin.substring(0, 1).toUpperCase()
            areaGroupMap."$pinyinFirstLetter" = areaGroupMap."$pinyinFirstLetter" ?: []
            areaGroupMap."$pinyinFirstLetter".addAll(splitAreas)

            if (POPULAR_AREAS.contains(area.id)) {
                popularAreas.addAll(splitAreas)
            }
        }

        ["popularAreas": popularAreas, "groupAreas": this.sort(areaGroupMap)]
    }


    private static Map<String, List<Object>> sort(Map<String, List<Object>> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }

        for (String key : map.keySet()) {
            Collections.sort(map.get(key), new Comparator<Object>() {

                @Override
                int compare(Object o1, Object o2) {
                    ((String) ((Map) o1).get("pinyin")) <=> ((String) ((Map) o2).get("pinyin"))
                }
            });
        }
        return map;
    }

    public Area findProvinceByCityId(String cityId) {
        return areaRepository.findOne(Long.valueOf(toProvinceId(cityId)));
    }

    private String toProvinceId(String cityId) {
        return cityId.endsWith(Area.PROVINCE_LIKE_AREA_SUFFIX) ?
            cityId :
            cityId.substring(0, 2) + Area.PROVINCE_LIKE_AREA_SUFFIX;

    }

    @Cacheable(value = "areaGroup",keyGenerator = "cacheKeyGenerator")
    Map getAreas(String insuranceCompanyId, Channel channel) {
        InsuranceCompany insuranceCompany
        List<QuoteFlowConfig> quoteFlowConfigList
        if (insuranceCompanyId) {
            insuranceCompany = toInsuranceCompany(Long.parseLong(insuranceCompanyId))
            quoteFlowConfigList = quoteFlowConfigRepository.findByChannelAndInsuranceCompany(channel?.parent, insuranceCompany)
        } else {
            quoteFlowConfigList = quoteFlowConfigRepository.findByChannel(channel?.parent)
        }
        formatData(quoteFlowConfigList.with { it.area }.toList().unique())
    }

    Map cheBaoYiAreas(ChannelAgent channelAgent) {
        List<Area> inviteCodeAreas = getInviteCodeAreas(channelAgent)
        if (!inviteCodeAreas) {
            return null
        }
        formatData(inviteCodeAreas)
    }

    List<Area> getInviteCodeAreas(ChannelAgent channelAgent) {
        ChecheAgentInviteCode checheInviteCode = ApplicationContextHolder.getApplicationContext().getBean('channelAgentInfoService').findChecheInviteCode(channelAgent)

        List<AgentInviteCodeArea> codeAreas = aicaRepo.findByChecheAgentInviteCode(checheInviteCode)
        //'95692402' 张总生产得车车邀请码
        if (!codeAreas || (channelAgent.agentLevel != AgentLevel.Enum.SALE_DIRECTOR_1 && ['95692402'].contains(checheInviteCode.inviteCode))) {
            return [Area.Enum.BJ]//如果没查到该邀请配置的地区，默认给北京地区
        }

        Boolean allAreasSupport = codeAreas.find { !it.area }
        if (allAreasSupport) {
            return null
        }
        codeAreas.area
    }

    List<Area> getAreasByCompany(String insuranceCompanyId, Channel channel) {
        InsuranceCompany insuranceCompany = toInsuranceCompany(Long.parseLong(insuranceCompanyId))
        List<QuoteFlowConfig> quoteFlowConfigList = quoteFlowConfigRepository.findByChannelAndInsuranceCompany(channel?.parent, insuranceCompany);
        quoteFlowConfigList.with { it.area }.toList().unique()
    }
}
