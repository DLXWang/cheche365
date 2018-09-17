package com.cheche365.cheche.picc.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.parser.Constants._CAR_MODEL_LIST_PAGING_SIZE
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_AUTO_TYPE_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_ENGINE_NO_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_LICENSE_PLATE_NO_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_OWNER_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_VIN_NO_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.util.BusinessUtils.getModelsByBrands
import static com.cheche365.cheche.parser.util.BusinessUtils.getSelectedCarModelFSRV
import static com.cheche365.cheche.parser.util.BusinessUtils.getValuableHintsFSRV
import static com.cheche365.cheche.picc.util.BusinessUtils.getAutoEngineNo
import static com.cheche365.cheche.picc.util.BusinessUtils.getAutoVinNo
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC



/**
 * Created by suyq on 2016/3/2.
 * 精友地区根据品牌型号获取车型列表
 */
@Component
@Slf4j
class FindVehicleBrandModel implements IStep {
    private static final _API_PATH_FIND_VEHICLE_BRAND_MODEL = '/ecar/car/carModel/findVehicleBrandModel'

    private static final _GET_VEHICLE_MODELS_BY_BRAND = { context, brand ->
        def args = generateRequestParams context, brand.brandCode
        context.client.post args, { resp, json ->
            json.body.queryVehicle
        }
    }

    @Override
    def run(Object context) {
        RESTClient client = context.client
        def auto = context.auto

        log.debug '用如下信息查找车型：{}、{}、{}、{}、{}、{}', auto.licensePlateNo, auto.owner, getAutoVinNo(context), getAutoEngineNo(context), auto.identity, auto.autoType?.code
        def args = generateRequestParams context, ''
        def result = client.post args, { resp, json ->
            json
        }

        def brandList = result.body?.queryBrand
        def brandModels = getModelsByBrands context, brandList, result.body?.queryVehicle, _GET_VEHICLE_MODELS_BY_BRAND
        if (brandModels) {
            context.originalVehicleModels = brandModels
            getSelectedCarModelFSRV context, brandList, result, [updateContext: { ctx, res, fsrv ->
                ctx.selectedCarModel = fsrv[2]
                ctx.vehicleInfo = fsrv[2]
            }]
        } else {
            log.error '无法获得车型，通常是车辆/人员信息有误导致的：{}', result
            getValuableHintsFSRV(context,
                [
                    _VALUABLE_HINT_LICENSE_PLATE_NO_TEMPLATE_QUOTING,
                    _VALUABLE_HINT_VIN_NO_TEMPLATE_QUOTING,
                    _VALUABLE_HINT_ENGINE_NO_TEMPLATE_QUOTING,
                    _VALUABLE_HINT_OWNER_TEMPLATE_QUOTING,
                    _VALUABLE_HINT_AUTO_TYPE_TEMPLATE_QUOTING
                ])
        }
    }


    //生成请求参数（1.根据品牌型号查询品牌列表 2.根据品牌code查询车型列表)
    private static generateRequestParams(context, brandCode) {
        [
            requestContentType: URLENC,
            contentType       : JSON,
            path              : _API_PATH_FIND_VEHICLE_BRAND_MODEL,
            body              : [
                'newVehicleBrandModelQuery.uniqueId'        : context.uniqueID,
                'newVehicleBrandModelQuery.requestType'     : '02',
                'newVehicleBrandModelQuery.areaCode'        : context.areaCode,
                'newVehicleBrandModelQuery.comCode'         : context.comCode,
                'newVehicleBrandModelQuery.vehicleName'     : context.auto.autoType?.code,
                'newVehicleBrandModelQuery.brandCode'       : brandCode,
                'newVehicleBrandModelQuery.priceConfigKind' : context.priceConfigKind,
                'newVehicleBrandModelQuery.pageSize'        : _CAR_MODEL_LIST_PAGING_SIZE
            ]
        ]
    }

}
