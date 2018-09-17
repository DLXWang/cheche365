package com.cheche365.cheche.taikang.flow.step

import com.cheche365.cheche.common.flow.IStep
import static com.cheche365.cheche.parser.Constants._ERROR_MESSAGE_AUTO_TYPE_CODE_ERROR
import static com.cheche365.cheche.parser.util.BusinessUtils.checkVehicleSupplementInfo
import static com.cheche365.cheche.taikang.util.BusinessUtils.sendParamsAndReceive
import static com.cheche365.flow.core.util.FlowUtils.getAutoTypeCodeErrorFSRV
import static com.cheche365.cheche.parser.Constants._DATE_FORMAT3



/**
 * 查车的抽象方法
 * Created by xuecl on 2018/06/5.
 */
abstract class ACarModelQuery implements IStep {

    protected static final _RESPONSE_FLAG = '4'
    private static final _FUNCTION = 'carModelQuery'

    @Override
    run(context) {

        def auto = context.auto
        def applyContent = [
            carInfo: [
                insuredCode   : context.cityCode,//投保地编码
                licenseNo     : auto.licensePlateNo, //车牌号
                engineNo      : auto.engineNo,//发动机号
                frameNo       : auto.vinNo,//车架号
                enrollDate    : _DATE_FORMAT3.format(auto.enrollDate),//初登日期
                ecdemicVehicle: '0',//外地车标志
                newVehicle    : '0',//新旧车标志
                vehicleModel  : auto.autoType.code - '牌',//车辆型号
                //marketYear:'',//上市年份!
            ] + vehicleModelConditions(context)
        ]

        def result = sendParamsAndReceive context, _FUNCTION, applyContent, log

        def carModelList = result.apply_content.data?.carModelList
        dealResultFSRV(context, result, carModelList)
    }

    /**
     * 用于更新第二次第三次选择的车，到推送列表中
     * @param context
     * @param carModelList 查车接口返回车型列表
     * @return
     */
    protected static updateCarModelList(context, carModelList) {
        // 如果不存在已选择车型，则直接推送补充信息或报错至前台
        context.selectedCarModel = carModelList && !carModelList.isEmpty() ? carModelList.first() : null

        if (!context.selectedCarModel) {
            checkVehicleSupplementInfo(context, context.optionsByCode, context.getVehicleOption) ?:
                getAutoTypeCodeErrorFSRV(context, _ERROR_MESSAGE_AUTO_TYPE_CODE_ERROR)
        } else {
            def vehicleList = context.additionalParameters.autoModel?.options?.byCode
            if (vehicleList) {
                def upFlag = false
                // 更新返回前台列表中的已经选择的车型
                def selected = context.getVehicleOption(context, context.selectedCarModel)
                context.additionalParameters.autoModel.options.byCode = vehicleList.collect {
                    vehicle ->
                        if (vehicle.value == selected.value) {
                            upFlag = true
                            selected
                        } else {
                            vehicle
                        }
                }
                if (!upFlag) {
                    vehicleList << selected
                }
            }
            null
        }
    }

    /**
     * 根据返回列表中的 responseFlag 添加对应请求参数
     */
    abstract protected vehicleModelConditions(context)

    /**
     * 对返回结果判断是否含有 carModelKey 进而判断是否继续查车
     */
    abstract protected dealResultFSRV(context, result, carModelList)

}
