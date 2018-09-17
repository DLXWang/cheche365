package com.cheche365.cheche.core.service;

import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.AutoVehicleLicenseServiceItemChannelRepository;
import com.cheche365.cheche.core.repository.AutoVehicleLicenseServiceItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * 车型库查询服务
 * TODO: 目前仅仅针对阳光车型API，所以仅仅注入一个externalService，今后有大地等接口时，这里就要变成List<IExternalAutoTypeService>
 * @author liqiang
 */
@Service
@Transactional
public class AutoVehicleLicenseService implements IAutoVehicleLicenseService {
    @Autowired
    private AutoVehicleLicenseServiceItemRepository autoVehicleLicenseServiceItemRepository;

    @Override
    public List<AutoVehicleLicenseServiceItem> getAutoVehicleLicenseServiceItem(Channel channel,AutoServiceType autoServiceType){

        return autoVehicleLicenseServiceItemRepository.getAutoVehicleLicenseServiceItemByChannelAndType(channel,autoServiceType);
   }
}
