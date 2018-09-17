package com.cheche365.cheche.core.service;

import com.cheche365.cheche.core.model.*;

import java.util.List;

/**
 * Created by Shanxf on 2016/10/9 .
 */
public interface IAutoVehicleLicenseService {
    /*
    * 根据当前传进来的渠道和服务类型信息返回
     * 支持服务的列表按优先级正续排列
     */
    List<AutoVehicleLicenseServiceItem> getAutoVehicleLicenseServiceItem(Channel channel,AutoServiceType autoServiceType);

}
