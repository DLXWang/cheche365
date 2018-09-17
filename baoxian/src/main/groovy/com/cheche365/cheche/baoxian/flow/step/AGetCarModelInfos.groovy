package com.cheche365.cheche.baoxian.flow.step

import com.cheche365.cheche.baoxian.flow.step.v2.ABaoXianCommonStep
import groovy.transform.InheritConstructors
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.Constants._DATE_FORMAT3

/**
 * 车型列表查询父类。根据车型品牌名称和VIN码查询车型列表信息，先走品牌型号查询，在走VIN码查询，将两次查询结果合并
 */
@Component
@InheritConstructors(
    constructorAnnotations = true,
    parameterAnnotations = true
)
abstract class AGetCarModelInfos extends ABaoXianCommonStep {

      private static final _API_PATH_GET_CAR_MODEL_INFOS = ''


    @Override
    run(context) {

        def params = [
            pageSize: 50,
            pageNum : 1,
            carInfo : [
                vehicleName :  getVehicleNameOrVinNo(context),
                carLicenseNo: context.auto.licensePlateNo, //非必传项
                registDate  : _DATE_FORMAT3.format(context.auto.enrollDate) //非必传项
            ]
        ]

        def result = send context,prefix + _API_PATH_GET_CAR_MODEL_INFOS, params

        getCarModelFSRV context, result
    }


    abstract protected getVehicleNameOrVinNo(context)

    abstract protected getCarModelFSRV(context, result)

}
