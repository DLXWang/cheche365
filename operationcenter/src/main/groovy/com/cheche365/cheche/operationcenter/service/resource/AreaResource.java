package com.cheche365.cheche.operationcenter.service.resource;

import com.cheche365.cheche.core.model.Area;
import com.cheche365.cheche.core.repository.AreaRepository;
import com.cheche365.cheche.core.service.AreaService;
import com.cheche365.cheche.core.util.BeanUtil;
import com.cheche365.cheche.core.util.CacheUtil;
import com.cheche365.cheche.manage.common.service.BaseService;
import com.cheche365.cheche.operationcenter.web.model.area.AreaViewData;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.cheche365.cheche.core.model.AreaType.Enum.CITY_3;
import static com.cheche365.cheche.core.model.AreaType.Enum.MUNICIPALITY_2;

/**
 * Created by sunhuazhong on 2015/7/21.
 */
@Component
public class AreaResource extends BaseService<Area, Area> {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final String CACHE_KEY="com:cheche365:cheche:operationcenter:areas:type23";

    @Autowired
    private AreaRepository areaRepository;

    @Autowired
    private AreaService areaService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

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
     * */
    public List<Area> getDirectCitys(){
        List typeIdList=new ArrayList<>();
        typeIdList.add(2L);
        return areaService.findProvinceAreaList(typeIdList);
    }

    /**
     * 获取所有的省份
     *
     * @return
     */
    public List<Area> getProvinces() {
        List typeIdList=new ArrayList<>();
        typeIdList.add(1L);
        typeIdList.add(5L);
        return areaService.findProvinceAreaList(typeIdList);
    }

    /**
     * 获取所有省和直辖市
     * */
    public List<Area> getprovincesAndDirectCitys(){
        List provinceAndDirectCityList=new ArrayList<Area>();
        provinceAndDirectCityList.addAll(getDirectCitys());
        provinceAndDirectCityList.addAll(getProvinces());
        return provinceAndDirectCityList;
    }

    public List<AreaViewData> createAreaViewDataList(List<Area> areas){
        List<AreaViewData> areaViewDatas=new ArrayList<AreaViewData>();
        for(Area area:areas){
            areaViewDatas.add(AreaViewData.createViewModel(area));
        }
        return areaViewDatas;
    }
    /**
     * 按关键字检索城市（直辖市、城市）
     * @param keyWord
     * @return
     */
    public List<AreaViewData> listByKeyWord(String keyWord){
        List<Area> areaList=this.listByCache();
        List<Area> resultList=new ArrayList<>();
        if(!CollectionUtils.isEmpty(areaList)){
            areaList.forEach(area -> {
                if(area.getName().contains(keyWord)){
                    resultList.add(area);
                }
            });
        }else{
            initAreaCache();
            listByKeyWord(keyWord);
        }
        return this.createAreaViewDataList(resultList);
    }

    private List<Area> listByCache(){
        String cacheAreasString= CacheUtil.getValue(this.stringRedisTemplate,CACHE_KEY);
        List<Area> areaList=new ArrayList<>();
        if(!StringUtils.isEmpty(cacheAreasString)){
            areaList= CacheUtil.doListJacksonDeserialize(cacheAreasString,Area.class);
            logger.debug("will get area list cache ,size :->{}",areaList.size());
        }
        return areaList;
    }

    private void initAreaCache(){
        List<Area> areaList = areaRepository.findByType(Arrays.asList(MUNICIPALITY_2.getId(), CITY_3.getId()));
        logger.debug("will cache area list size: -> {}",areaList.size());
        stringRedisTemplate.opsForValue().set(CACHE_KEY,CacheUtil.doJacksonSerialize(areaList));
    }

    public Area findById(Long areaId) {
        return areaRepository.findOne(areaId);
    }

}
