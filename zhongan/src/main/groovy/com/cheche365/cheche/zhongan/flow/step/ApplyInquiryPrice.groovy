package com.cheche365.cheche.zhongan.flow.step

import com.cheche365.cheche.common.flow.IStep
import com.cheche365.cheche.core.model.Auto
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.flow.Constants.get_ROUTE_FLAG_CONTINUE
import static com.cheche365.cheche.common.util.ContactUtils.getRandomMobile
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getEnvProperty
import static com.cheche365.cheche.parser.Constants._DATE_FORMAT3
import static com.cheche365.cheche.parser.Constants._SUPPLEMENT_INFO_TRANSFER_DATE_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants.get_VALUABLE_HINT_AUTO_TYPE_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.util.BusinessUtils.checkVehicleSupplementInfo
import static com.cheche365.cheche.parser.util.BusinessUtils.getSelectedCarModelFSRV
import static com.cheche365.cheche.parser.util.BusinessUtils.getSupplementInfoFSRV
import static com.cheche365.cheche.parser.util.BusinessUtils.getValuableHintsFSRV
import static com.cheche365.cheche.zhongan.util.BusinessUtils.getStandardHintsFSRV
import static com.cheche365.cheche.zhongan.util.BusinessUtils.sendAndReceive




/**
 * 询价申请
 */
@Component
@Slf4j
class ApplyInquiryPrice implements IStep {

    private static final _SERVICE_NAME = 'zhongan.castle.policy.vehicleApplyInquiryPrice'

    @Override
    def run(Object context) {
        Auto auto = context.auto
        def vehicleModelCode = context.selectedCarModel?.vehicleModelCode
        def transferDate = context?.additionalParameters?.supplementInfo?.transferDate

        def params = [
            insurePlaceCode          : context?.cityCode,                                 //投保城市代码
            vehicleLicencePlateNo    : auto.licensePlateNo,                            //车牌号
            vehicleFrameNo           : auto.vinNo,                                     //车架号
            vehicleEngineNo          : auto.engineNo,                                  //发动机号
            vehicleRegisterDate      : _DATE_FORMAT3.format(context.auto?.enrollDate), //车辆登记注册日期 yyyy-MM-dd
            vehicleOwnerName         : auto.owner,                                     //车主名称
            vehicleOwnerCertificateNo: auto.identity,                                  //车主身份证
            vehiclePhoneNo           : auto.mobile ?: randomMobile,                    //车主手机号
            applicantName            : auto.owner,                                     //车主姓名
            isTransferCar            : transferDate ? '1' : '0',                       //是否过户 1：过户0：非过户
            transferDate             : transferDate ? _DATE_FORMAT3.format(transferDate) : '', //过户日期 如是过户则需传，格式为YYYY-MM-DD
            vehicleModelCode         : vehicleModelCode,                               //众安车型编码
            agentPeopleCode          : getEnvProperty(context, 'zhongan.agent_people_code'),
        ]

        def result = sendAndReceive(context, this.class.name, _SERVICE_NAME, params)
        log.debug '询价申请result：{}', result

        if ('0' == result.result) {
            context.insureFlowCode = result.insureFlowCode
            context.allKindItems = result.coverageInfoList
            log.debug '基础套餐 baseKindItems：{}', context.allKindItems
            log.info "进入询价申请阶段，询价申请正常，流程继续 "
            getContinueFSRV result
        } else if ('A12512' == result.result && !context.isApplyInquiryPriceAgain) {
            context.isApplyInquiryPriceAgain = 1
            log.debug "询价申请失败,重新选择车型 resultMessage : {}", result.resultMessage
            def modelList = result.platformVehicleModelList

            if (!modelList) {
                return getValuableHintsFSRV(context, [
                    _VALUABLE_HINT_AUTO_TYPE_TEMPLATE_QUOTING,
                ])
            }
            context.additionalParameters.supplementInfo.autoModel = null
            context.optionsByCode = modelList
            context.resultByCode = result
            if (context.additionalParameters.referToOtherAutoModel) {
                def selectedAutoModelValue = context.additionalParameters.supplementInfo?.selectedAutoModel
                log.info "询价返回车型，进入选车阶段，VehicleList: {},集成层给的selectedAutoModelValue :{}", modelList, selectedAutoModelValue
                def selectCarModelFSRV = getSelectedCarModelFSRV(context, modelList, result)
                if (_ROUTE_FLAG_CONTINUE == selectCarModelFSRV[0]) {
                    getContinueFSRV '车型列表变更'
                } else {
                    selectCarModelFSRV
                }
            } else {
                if (context.autoModel) {
                    context.additionalParameters.supplementInfo.autoModel = context.autoModel
                }
                getContinueFSRV '车型列表变更'
            }
        } else {
            if (!context.additionalParameters.supplementInfo.autoModel && 'A12512' == result.result) {
                return checkVehicleSupplementInfo(context)
            }
            log.error "询价申请失败 resultMessage : {}", result.resultMessage
            getStandardHintsFSRV result
        }
    }

}
