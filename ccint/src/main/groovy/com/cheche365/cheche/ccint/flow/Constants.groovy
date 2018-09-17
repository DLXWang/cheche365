package com.cheche365.cheche.ccint.flow

import static com.cheche365.cheche.common.Constants._DATE_FORMAT3
import static com.cheche365.cheche.core.model.UseCharacter.Enum.findUseCharacter
import static com.cheche365.cheche.core.model.VehicleType.Enum.findVehicleType


/**
 * 合合流程步骤所需的常量
 */
class Constants {

    static final _STATUS_CODE_CCINT_API_UPPER_LIMIT_ERROR = -20001L

    static final _VEHICLE_INFO_EXTRACTOR = { context ->
        def vehicleLicense = context.vehicleLicense
        if (vehicleLicense) {
            [
                licensePlateNo: vehicleLicense.vehicle_license_main_plate_num?.trim(),
                owner         : vehicleLicense.vehicle_license_main_owner?.trim(),
                vinNo         : vehicleLicense.vehicle_license_main_vin?.trim(),
                engineNo      : vehicleLicense.vehicle_license_main_engine_no?.trim(),
                enrollDate    : vehicleLicense.vehicle_license_main_register_date ? _DATE_FORMAT3.parse(vehicleLicense.vehicle_license_main_register_date) : null,
                issueDate     : vehicleLicense.issue_date ? _DATE_FORMAT3.parse(vehicleLicense.issue_date) : null,
                brandCode     : vehicleLicense?.vehicle_license_main_model,
                vehicleType   : findVehicleType(vehicleLicense.vehicle_license_main_vehicle_type?.trim()),
                useCharacter  : findUseCharacter(vehicleLicense.vehicle_license_main_user_character?.trim())
            ].with {
                it.licensePlateNo && it.vinNo && it.engineNo ? it : null
            }
        }
    }

}
