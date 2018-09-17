package com.cheche365.cheche.sinosig.flow

import com.cheche365.cheche.common.flow.FlowBuilder
import com.cheche365.cheche.common.flow.FlowChain
import com.cheche365.cheche.parser.flow.steps.CheckInsurancesCheckList
import com.cheche365.cheche.parser.flow.steps.CheckRenewalFlow
import com.cheche365.cheche.parser.flow.steps.ExtractAutoInfo
import com.cheche365.cheche.parser.flow.steps.ExtractInsuranceBasicInfo
import com.cheche365.cheche.parser.flow.steps.ExtractInsuranceInfo
import com.cheche365.cheche.parser.flow.steps.UpdateAutoInfo
import com.cheche365.cheche.parser.flow.steps.UpdateSupplementInfo
import com.cheche365.flow.business.flow.step.ExtractVehicleInfo
import com.cheche365.cheche.parser.flow.steps.ProcessVehicleLicenseStagedResult
import com.cheche365.cheche.parser.flow.steps.QuotePostProcessor
import com.cheche365.cheche.sinosig.flow.step.PremiumBIKindList
import com.cheche365.cheche.sinosig.flow.step.PremiumRenewalBI
import com.cheche365.cheche.sinosig.flow.step.SelectCarModel
import com.cheche365.cheche.sinosig.flow.step.TravelCity
import com.cheche365.cheche.sinosig.flow.step.CheckRenewal
import com.cheche365.cheche.sinosig.flow.step.GetCarModelsList
import com.cheche365.cheche.sinosig.flow.step.GetId
import com.cheche365.cheche.sinosig.flow.step.PremiumBI
import com.cheche365.cheche.sinosig.flow.step.PremiumBIDate
import com.cheche365.cheche.sinosig.flow.step.PremiumCI
import com.cheche365.cheche.sinosig.flow.step.SaveBaseInfo
import com.cheche365.cheche.sinosig.flow.step.SavePremium
import com.cheche365.cheche.sinosig.flow.step.StepZeroOne
import com.cheche365.cheche.sinosig.flow.step.insuranceinfo.GetRenewalInsurancePackage



/**
 * Created by suyq on 2015/8/19.
 * 阳光报价流程定义
 */
class Flows {

    static final _STEP_NAME_CLAZZ_MAPPINGS = [
        获取城市信息   : TravelCity,
        获取token  : GetId,
        获取车型信息列表 : GetCarModelsList,
        验证车辆信息   : SaveBaseInfo,
        验证车型信息   : StepZeroOne,
        选择车型    :SelectCarModel,
        抽取Auto信息 : ExtractAutoInfo,
        更新Auto信息 : UpdateAutoInfo,
        计算交强险    : PremiumCI,
        校验商业险起保日期: PremiumBIDate,
        计算商业险    : PremiumBI,
        计算续保商业险  : PremiumRenewalBI,
        获取报价信息   : PremiumBIKindList,
        核保       : SavePremium,

        报价后处理器   : QuotePostProcessor,
        检查是否续保     : CheckRenewal,
        检查续保通道流程 : CheckRenewalFlow,
        检查险种清单  : CheckInsurancesCheckList,
        抽取车辆信息 : ExtractVehicleInfo,
        处理行驶证阶段性结果: ProcessVehicleLicenseStagedResult,
        更新补充信息 : UpdateSupplementInfo,

        抽取保险信息   : ExtractInsuranceInfo,
        抽取保险基本信息   : ExtractInsuranceBasicInfo,
        获取续保套餐    : GetRenewalInsurancePackage
    ]

    private static get_FLOW_BUILDER() {
        new FlowBuilder(nameClazzMappings: _STEP_NAME_CLAZZ_MAPPINGS)
    }

    /**
     * 阳光官网是先报交强后报商业的，
     * 但是我们的续保通道流程需要获取去年的套餐，
     * 而这只能在“计算商业险”步骤中获得，
     * 所以我们不得不调换二者的顺序，
     * 来保证交强险一定可以得到一个非空的套餐
     */
    /**
     * “续保客户”可以在计算商业险时直接获得交强险报价，
     * 但是为了简化流程，我们在“计算商业险”的步骤中禁止报交强险“isTra = 0”，
     * 交强险只能由“计算交强险”步骤来计算
     */
    static final _QUOTING_FLOW_COMM_TYPE1 = _FLOW_BUILDER {
        获取城市信息 >> 获取token >> [
            (false): {
                获取车型信息列表
            }
        ] >> 验证车辆信息 >> 验证车型信息 >> [
            (true): {
                选择车型
            }
        ]  >> 抽取Auto信息 >> 检查是否续保 >> [
            (false): {
                检查续保通道流程
            }
        ] >> 更新补充信息 >> 计算交强险 >> loop {
            校验商业险起保日期 >> 获取报价信息
        } >> 检查是否续保 >> [
            (false): {
                计算商业险
            }
        ] >> 计算续保商业险 >> 检查险种清单 >> 更新Auto信息 >> 报价后处理器
    }

    private static final _UNDERWRITING_FLOW_DEFAULT = _FLOW_BUILDER {
        核保
    }

    static final _INSURING_FLOW_TYPE1 = new FlowChain(flows: [_QUOTING_FLOW_COMM_TYPE1, _UNDERWRITING_FLOW_DEFAULT])


    static final _INSURANCE_BASIC_INFO_FLOW_DEFAULT = _FLOW_BUILDER {
        获取城市信息 >> 获取token >> [
            (true) : {
                验证车辆信息 >> 验证车型信息 >> 检查是否续保 >> [
                    (true):{
                        校验商业险起保日期 >> 获取续保套餐
                    }
                ]
            }
        ] >> 抽取保险基本信息
    }


    static final _INSURANCE_INFO_FLOW_DEFAULT = _FLOW_BUILDER {
        获取城市信息 >> 获取token >> [
            (true) : {
                验证车辆信息 >> 验证车型信息 >> 检查是否续保 >> [
                    (true):{
                        校验商业险起保日期 >> 获取续保套餐
                    }
                ]
            }
        ] >> 抽取车辆信息 >> 处理行驶证阶段性结果 >> 抽取保险基本信息 >> 抽取保险信息
    }

}
