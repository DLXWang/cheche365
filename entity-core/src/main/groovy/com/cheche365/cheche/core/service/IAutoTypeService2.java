package com.cheche365.cheche.core.service;

import com.cheche365.cheche.core.model.Area;
import com.cheche365.cheche.core.model.Auto;
import com.cheche365.cheche.core.model.InsuranceCompany;

import java.util.List;
import java.util.Map;

/**
 * 二代车型服务
 * Created by Huabin on 2016/8/31.
 */
public interface IAutoTypeService2 {

    /**
     * 根据城市和车辆返回保险公司对应的车型列表信息
     * @param area 城市
     * @param auto 车辆信息
     * @param insuranceCompanies 保险公司列表
     * @return 返回保险公司->车型列表映射，长度应该和传入的insuranceCompanies长度一致
     */
    Map<InsuranceCompany, List> getAutoTypes(Area area, Auto auto, List<InsuranceCompany> insuranceCompanies);

}
