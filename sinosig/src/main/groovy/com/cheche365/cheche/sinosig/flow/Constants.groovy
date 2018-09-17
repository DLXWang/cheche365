package com.cheche365.cheche.sinosig.flow

import static com.cheche365.cheche.parser.Constants._DATE_FORMAT3
import static com.cheche365.cheche.parser.Constants._SUPPLEMENT_INFO_BRAND_CODE_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._SUPPLEMENT_INFO_CAR_MODEL_LIST_TEMPLATE_QUOTING



/**
 * 阳光流程步骤所需的常量
 */
class Constants {

    static final _CITY_SUPPLEMENT_INFO_MAPPINGS = [

        default : []
    ]

    static final _AUTOTYPE_EXTRACTOR = { context ->
        context.vehicleInfo?.brandName ? [context.vehicleInfo.brandName] : []
    }

    static final _VEHICLE_INFO_EXTRACTOR = { context ->
        def selectedCarModel = context.selectedCarModel
        if (selectedCarModel) {
            [
                vinNo     : selectedCarModel.frameNo?.trim(),
                engineNo  : selectedCarModel.engineNo?.trim(),
                enrollDate: selectedCarModel.enroll ? _DATE_FORMAT3.parse(selectedCarModel.enroll) : null,
                brandCode : selectedCarModel.queryVehicle
            ].with {
                it.vinNo && it.engineNo ? it : null
            }
        }
    }

    static final get_VEHICLE_MODEL_SUPPLEMENT_INFO_MAPPINGS() {
        [
            default: [
                _SUPPLEMENT_INFO_BRAND_CODE_TEMPLATE_QUOTING,
                _SUPPLEMENT_INFO_CAR_MODEL_LIST_TEMPLATE_QUOTING
            ]
        ]
    }

    static final _AUTO_INFO_EXTRACTOR = { context ->
        def vehicleInfo = context.selectedCarModel ?: context.selectCar
        if (vehicleInfo && vehicleInfo?.seat) {
            [
                autoType: [
                    seats: vehicleInfo.seat as int
                ]
            ]
        }
    }

}
