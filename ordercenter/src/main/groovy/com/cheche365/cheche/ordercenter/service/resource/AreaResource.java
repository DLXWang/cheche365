package com.cheche365.cheche.ordercenter.service.resource;

import com.cheche365.cheche.common.util.StringUtil;
import com.cheche365.cheche.core.model.Area;
import com.cheche365.cheche.core.repository.AreaRepository;
import com.cheche365.cheche.core.service.AreaService;
import com.cheche365.cheche.core.util.BeanUtil;
import com.cheche365.cheche.core.util.CacheUtil;
import com.cheche365.cheche.manage.common.service.BaseService;
import com.cheche365.cheche.ordercenter.web.model.area.AreaViewData;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.cheche365.cheche.core.model.AreaType.Enum.*;

/**
 * Created by sunhuazhong on 2015/7/21.
 */
@Component
public class AreaResource extends BaseService<Area, Area> {

    private static final String CACHE_KEY = "com:cheche365:cheche:ordercenter:areas:type23";
    private static Logger logger = org.slf4j.LoggerFactory.getLogger(AreaResource.class);

    @Autowired
    private AreaRepository areaRepository;

    @Autowired
    private AreaService areaService;

    @Autowired
    private RedisTemplate redisTemplate;

    public List<AreaViewData> createViewData(List<Area> areas) {
        if (areas == null)
            return null;

        String[] contains = new String[]{"id", "name"};
        List<AreaViewData> viewDataList = new ArrayList<>();
        areas.forEach(area -> {
            AreaViewData viewData = new AreaViewData();
            BeanUtil.copyPropertiesContain(area, viewData, contains);
            viewDataList.add(viewData);
        });

        return viewDataList;
    }

    public List<Area> getAllEnableAreas() {
        return areaRepository.findByActive(true);
    }

    /**
     * 获取所有直辖市
     */
    public List<Area> getDirectCitys() {
        List typeIdList = new ArrayList<>();
        typeIdList.add(2L);
        return areaService.findProvinceAreaList(typeIdList);
    }

    /**
     * 获取所有的省份
     *
     * @return
     */
    public List<Area> getProvinces() {
        List typeIdList = new ArrayList<>();
        typeIdList.add(1L);
        typeIdList.add(5L);
        return areaService.findProvinceAreaList(typeIdList);
    }

    /**
     * 获取所有省和直辖市
     */
    public List<Area> getprovincesAndDirectCitys() {
        List provinceAndDirectCityList = new ArrayList<Area>();
        provinceAndDirectCityList.addAll(getDirectCitys());
        provinceAndDirectCityList.addAll(getProvinces());
        return provinceAndDirectCityList;
    }


    /**
     * 获取省下面的市
     *
     * @param province
     * @return
     */
    public List<Area> getCityAreaListByProvinceId(String province) {
        if (StringUtils.isNotBlank(province)) {
            //Long childType = 3L;
            Long childType = (AreaService.BEIJING_CODE.equals(province) || AreaService.TIANJIN_CODE.equals(province) || AreaService.SHANGHAI_CODE.equals(province) || AreaService.CHONGQING_CODE.equals(province)) ? 4L : 3L;
            List<Area> provinceAreaList = areaService.findCityAreaListByProvinceId(province, 2, childType);
            return provinceAreaList;
        }
        return null;
    }

    /**
     * 获取市上面的省
     */
    public Area getProvinceByCity(String city) {
        if ((AreaService.BEIJING_CODE.equals(city) || AreaService.TIANJIN_CODE.equals(city) || AreaService.SHANGHAI_CODE.equals(city) || AreaService.CHONGQING_CODE.equals(city))) {
            return areaService.findById(Long.parseLong(city));
        }
        Long childType = 1L;
        return areaService.findCityAreaListByProvinceId(city, 2, childType).get(0);
    }

    /**
     * 获取市下面的区或县
     *
     * @param city
     * @return
     */
    public List<Area> getDistrictAreaListCityId(String city) {
        if (StringUtils.isNotBlank(city) || StringUtil.toLong(city) > -1) {
            Area area = areaService.findById(StringUtil.toLong(city));
            if (area.getType().getId() == 4) {
                return new ArrayList<>();
            }
            Long childType = 4L;
            int endPosition = (AreaService.BEIJING_CODE.equals(city) || AreaService.TIANJIN_CODE.equals(city) || AreaService.SHANGHAI_CODE.equals(city) || AreaService.CHONGQING_CODE.equals(city)) ? 2 : 4;
            List<Area> districtList = areaService.findCityAreaListByProvinceId(city, endPosition, childType);
            return districtList;
        }
        return null;
    }

    public List<AreaViewData> createAreaViewDataList(List<Area> areas) {
        List<AreaViewData> areaViewDatas = new ArrayList<AreaViewData>();
        for (Area area : areas) {
            areaViewDatas.add(AreaViewData.createViewModel(area));
        }
        return areaViewDatas;
    }

    public List<Area> listByCache() {
        return listByCache(CITY_3.getId(), MUNICIPALITY_2.getId());
    }

    public List<Area> listByCache(Long... type) {
        List<Area> areaList = new ArrayList<>();
        for (Long typeId : type) {

            String cachedResult = (String) redisTemplate.opsForHash().get(CACHE_KEY, typeId.toString());
            if (cachedResult != null) {
                List<Area> cacheList = CacheUtil.doListJacksonDeserialize(cachedResult, Area.class);
                if (cacheList != null) {
                    areaList.addAll(cacheList);
                }
            }
        }
        return areaList;
    }

    private void initAreaCache() {
        List<Area> cityList = areaRepository.findByType(Arrays.asList(CITY_3.getId()));
        List<Area> municipalItyList = areaRepository.findByType(Arrays.asList(MUNICIPALITY_2.getId()));
        List<Area> provinceList = areaRepository.findByType(Arrays.asList(PROVINCE_1.getId()));
        logger.debug("will cache city list size: -> {}", cityList.size());
        logger.debug("will cache province list size: -> {}", provinceList.size());
        CacheUtil.putInHash(redisTemplate, CACHE_KEY, CITY_3.getId().toString(), CacheUtil.doJacksonSerialize(cityList));
        CacheUtil.putInHash(redisTemplate, CACHE_KEY, MUNICIPALITY_2.getId().toString(), CacheUtil.doJacksonSerialize(municipalItyList));
        CacheUtil.putInHash(redisTemplate, CACHE_KEY, PROVINCE_1.getId().toString(), CacheUtil.doJacksonSerialize(provinceList));
    }

    /**
     * 按关键字检索城市（直辖市、城市）
     *
     * @param keyWord
     * @return
     */
    public List<AreaViewData> listByKeyWord(String keyWord) {
        return listByKeyWord(keyWord, CITY_3.getId(), MUNICIPALITY_2.getId());
    }

    /**
     * 按关键字检索省（省，直辖市、城市）
     *
     * @param keyWord
     * @return
     */
    public List<AreaViewData> listByProvinceKeyWord(String keyWord) {
        return listByKeyWord(keyWord, PROVINCE_1.getId(), CITY_3.getId(), MUNICIPALITY_2.getId());
    }

    public List<AreaViewData> listByKeyWord(String keyWord, Long... areaCode) {
        List<Area> areaList = this.listByCache(areaCode);
        List<Area> resultList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(areaList)) {
            areaList.forEach(area -> {
                if (area.getName().contains(keyWord)) {
                    resultList.add(area);
                }
            });
        } else {
            initAreaCache();
            listByKeyWord(keyWord, areaCode);
        }
        return this.createAreaViewDataList(resultList);
    }
}
