package com.cheche365.cheche.ordercenter.service.telMarketingCenter;

import com.cheche365.cheche.core.model.InternalUser;
import com.cheche365.cheche.core.model.TelMarketer;
import com.cheche365.cheche.core.repository.InternalUserRepository;
import com.cheche365.cheche.manage.common.repository.TelMarketerRepository;
import com.cheche365.cheche.manage.common.service.InternalUserManageService;
import com.cheche365.cheche.manage.common.web.model.DataTablePageViewModel;
import com.cheche365.cheche.ordercenter.model.TelMarketerViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by chenxiangyin on 2017/9/27.
 */
@Service
public class TelMarketerService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private TelMarketerRepository telMarketerRepository;
    @Autowired
    private InternalUserManageService internalUserManageService;
    @Autowired
    private InternalUserRepository internalUserRepository;
    @Autowired
    private EntityManager entityManager;

    public DataTablePageViewModel<TelMarketerViewModel> telMarketerList(Integer draw,Integer currentPage, Integer pageSize){
        List<TelMarketerViewModel> resultList = new ArrayList<>();
        Long marketerNum = telMarketerRepository.countAllTelMarketer();
        List<Object[]> telMarketerList = telMarketerRepository.findAllTelMarketer(pageSize, (currentPage - 1) * pageSize);
        telMarketerList.forEach(
            telMarketer ->{
                TelMarketerViewModel model = new TelMarketerViewModel();
                model.setId(Long.valueOf(telMarketer[0].toString()));
                model.setName(telMarketer[1].toString());
                model.setBindTel(telMarketer[2].toString());
                model.setCno(telMarketer[3].toString());
                resultList.add(model);
            }
        );
        return new DataTablePageViewModel<>(marketerNum, marketerNum, draw, resultList);
    }

    @Transactional
    public void edit(List<TelMarketerViewModel> formList) {
        List<TelMarketer> marketers = new ArrayList<>();
        for(TelMarketerViewModel model:formList){
            InternalUser user = internalUserRepository.findOne(model.getId());
             TelMarketer marketer = telMarketerRepository.findFirstByUser(model.getId());
            if(marketer == null){
                marketer = new TelMarketer();
                marketer.setUser(model.getId());
                marketer.setCreateTime(new Date());
            }
            marketer.setCno(model.getCno());
            marketer.setBindTel(model.getBindTel());
            marketer.setUpdateTime(new Date());
            marketer.setOperator(internalUserManageService.getCurrentInternalUser().getId());
            marketers.add(marketer);
        }
        telMarketerRepository.save(marketers);
    }
}
