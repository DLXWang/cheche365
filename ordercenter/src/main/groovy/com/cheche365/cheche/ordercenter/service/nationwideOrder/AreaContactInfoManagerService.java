package com.cheche365.cheche.ordercenter.service.nationwideOrder;

import com.cheche365.cheche.core.model.AreaContactInfo;
import com.cheche365.cheche.core.repository.AreaContactInfoRepository;
import com.cheche365.cheche.core.repository.OrderCooperationInfoRepository;
import com.cheche365.cheche.manage.common.service.BaseService;
import com.cheche365.cheche.manage.common.web.model.ResultModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by xu.yelong on 2015/11/12.
 */
@Service
public class AreaContactInfoManagerService extends BaseService{
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AreaContactInfoRepository areaContactInfoRepository;

    @Autowired
    private OrderCooperationInfoRepository orderCooperationInfoRepository;

    @Transactional
    public ResultModel update(AreaContactInfo areaContactInfo){
        try{
            // 保存分站信息
            areaContactInfoRepository.save(areaContactInfo);
            // 为订单设置分站信息
            orderCooperationInfoRepository.updateAreaContactInfo();
            return new ResultModel(true,"修改成功!");
        }catch (Exception ex){
            logger.error("save or update area contact info error", ex);
            return new ResultModel(false,"修改失败!");
        }
    }




}
