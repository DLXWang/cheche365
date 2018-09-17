package com.cheche365.cheche.rest.web.controller;

import com.cheche365.cheche.core.model.Area;
import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.model.agent.ChannelAgent;
import com.cheche365.cheche.core.service.AreaService;
import com.cheche365.cheche.core.util.CacheUtil;
import com.cheche365.cheche.web.ContextResource;
import com.cheche365.cheche.web.model.useragent.UserAgentHeader;
import com.cheche365.cheche.web.response.RestResponseEnvelope;
import com.cheche365.cheche.web.util.ClientTypeUtil;
import com.cheche365.cheche.web.version.VersionedResource;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * add by chenxz
 * 区域相关Resource
 */
@RestController
@RequestMapping("/" + ContextResource.VERSION_NO + "/areas")
@VersionedResource(from = "1.0")
public class AreasResource extends ContextResource {

    @Autowired
    private AreaService areaService;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    //由于json序列化比较慢,下列方法采用buf.append方式组装json
    @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String getAreasJson(@RequestParam(value = "versionMillis") Long versionMillis) {
        return areaService.getAreasWithChildren(versionMillis);
    }

    @RequestMapping(value = "/group", method = RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope<Map<String, String>>> getGroupAreas(HttpServletRequest request, @RequestParam(value = "companyId", required = false) String insuranceCompanyId
    ) {
        Channel channel = ClientTypeUtil.getChannel(request);

        Map resultMap = null;
        if (Channel.findAgentChannel(channel).equals(Channel.Enum.PARTNER_CHEBAOYI_67)) {
            resultMap = areaService.cheBaoYiAreas(currentChannelAgent());
        }
        if (resultMap == null) {
            resultMap = areaService.getAreas(insuranceCompanyId, channel);
        }

        return new ResponseEntity<>(new RestResponseEnvelope(resultMap), HttpStatus.OK);
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope<List<Area>>> getAvailableAreas(@RequestParam(value = "versionMillis") Long versionMillis) {
        Channel channel = ClientTypeUtil.getChannel(request);
        UserAgentHeader userAgent = ClientTypeUtil.getUserAgentHeaderByRequest(request);
        Boolean oldVersion = Channel.selfApp().contains(channel) && userAgent.getVer().compareTo("2.1.3") <= 0;

        List aliasAreas = areaService.getAreasWithSupplementInfo(channel, safeGetCurrentAgent());

        Map<String,Object> areasMap = new HashMap<>();
        areasMap.put("versionMillis",oldVersion ? System.currentTimeMillis() : versionMillis);
        areasMap.put("areaList",aliasAreas);

        return new ResponseEntity<>(new RestResponseEnvelope(areasMap), HttpStatus.OK);
    }

    @RequestMapping(value = "/provinces", method = RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope<List>> getProvinces() {
        List provinceList = areaService.findProvinceAreaMap(Lists.newArrayList(1L, 2L, 5L));
        return new ResponseEntity<>(new RestResponseEnvelope(provinceList), HttpStatus.OK);
    }

    @RequestMapping(value = "/provinces/{cityId}", method = RequestMethod.GET)
    public HttpEntity<?> getProvinceByCityId(@PathVariable String cityId) {
        return new ResponseEntity<>(areaService.findProvinceByCityId(cityId), HttpStatus.OK);
    }

    @RequestMapping(value = "/{province}/cities", method = RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope<List<Map>>> getCityAreaListByProvinceId(@PathVariable String province) {
        RestResponseEnvelope envelope = new RestResponseEnvelope("");
        if (StringUtils.isNotBlank(province)) {
            Long childType = 3L;
            List<Map> provinceAreaList = areaService.findCityAreaMapByProvinceId(province, 2, childType);
            if (logger.isDebugEnabled()) {
                logger.debug("getCityAreaListByProvinceId is finished. result:" + CacheUtil.doJacksonSerialize(provinceAreaList));
            }
            envelope = new RestResponseEnvelope(provinceAreaList);
        }
        return new ResponseEntity<>(envelope, HttpStatus.OK);
    }

    @RequestMapping(value = "/{city}/districts", method = RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope<List<Map>>> getDistrictAreaListCityId(@PathVariable String city) {
        RestResponseEnvelope envelope = new RestResponseEnvelope("");
        if (StringUtils.isNotBlank(city)) {
            Long childType = 4L;
            int endPosition = (AreaService.BEIJING_CODE.equals(city) || AreaService.TIANJIN_CODE.equals(city) || AreaService.SHANGHAI_CODE.equals(city) || AreaService.CHONGQING_CODE.equals(city)) ? 2 : 4;
            List<Map> districtList = areaService.findCityAreaMapByProvinceId(city, endPosition, childType);
            if (logger.isDebugEnabled()) {
                logger.debug("getDistrictAreaListCityId is finished. result:" + CacheUtil.doJacksonSerialize(districtList));
            }
            envelope = new RestResponseEnvelope(districtList);
        }
        return new ResponseEntity<>(envelope, HttpStatus.OK);
    }

}
