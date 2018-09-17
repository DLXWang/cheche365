package com.cheche365.cheche.web.service.mobile;

import com.cheche365.cheche.core.model.Area;
import com.cheche365.cheche.core.model.MobileArea;
import com.cheche365.cheche.core.repository.AreaRepository;
import com.cheche365.cheche.core.repository.MobileAreaRepository;
import com.cheche365.cheche.externalapi.api.location.MobileLocationAPI;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by xu.yelong on 2016/7/15.
 */
@Service
public class MobileAreaService {

    @Autowired
    private MobileAreaRepository mobileAreaRepository;

    @Autowired
    private AreaRepository areaRepository;

    @Autowired
    public MobileLocationAPI locationAPI;


    private Logger logger = LoggerFactory.getLogger(MobileAreaService.class);

    @Transactional
    public void save(String mobile){
        MobileArea mobileArea = mobileAreaRepository.findByMobile(mobile);
        if(mobileArea==null){
            mobileArea=new MobileArea();
            mobileArea.setMobile(mobile);
        }
        try {
            Object city= locationAPI.call(mobile);
            if(null==city || StringUtils.isEmpty(city.toString())){
                return;
            }
            Area area = areaRepository.findByName(city.toString());
            if(area==null){
                return;
            }
            mobileArea.setArea(area);
            mobileAreaRepository.save(mobileArea);
        } catch (Exception e) {
            logger.debug("get area from api error :mobile -> {}",mobile,e);
        }
    }


}
