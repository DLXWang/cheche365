package com.cheche365.cheche.cpic.flow

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder

import static com.cheche365.cheche.parser.Constants._DATE_FORMAT3
import static com.cheche365.cheche.parser.Constants._SUPPLEMENT_INFO_BRAND_CODE_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._SUPPLEMENT_INFO_CAR_MODEL_LIST_TEMPLATE_QUOTING



/**
 * CPIC流程步骤所需的常量
 */
class Constants {

    static final _STATUS_CODE_CPIC_GETCOOKIES_ERROR                         = 25001L
    static final _STATUS_CODE_CPIC_SUBMITVEHICLEBASEINFO_ERROR              = _STATUS_CODE_CPIC_GETCOOKIES_ERROR + 1
    static final _STATUS_CODE_CPIC_SUBMITVEHICLEINFO_ERROR                  = _STATUS_CODE_CPIC_SUBMITVEHICLEBASEINFO_ERROR + 1
    static final _STATUS_CODE_CPIC_INITVEHICLEDDETAILINFO_ERROR             = _STATUS_CODE_CPIC_SUBMITVEHICLEINFO_ERROR + 1
    static final _STATUS_CODE_CPIC_SUBMITVEHICLEDETAILINFO_ERROR            = _STATUS_CODE_CPIC_INITVEHICLEDDETAILINFO_ERROR + 1
    static final _STATUS_CODE_CPIC_INITQUOTATION_ERROR                      = _STATUS_CODE_CPIC_SUBMITVEHICLEDETAILINFO_ERROR + 1
    static final _STATUS_CODE_CPIC_CALCPREMIUM_ERROR                        = _STATUS_CODE_CPIC_INITQUOTATION_ERROR + 1
    static final _STATUS_CODE_CPIC_INITTRAVELTAX_ERROR                      = _STATUS_CODE_CPIC_CALCPREMIUM_ERROR + 1
    static final _STATUS_CODE_CPIC_CALCTRAVELTAX_ERROR                      = _STATUS_CODE_CPIC_INITTRAVELTAX_ERROR + 1
    static final _STATUS_CODE_CPIC_QUOTEUNDERWRITING_ERROR                  = _STATUS_CODE_CPIC_CALCTRAVELTAX_ERROR + 1
    static final _STATUS_CODE_CPIC_TAXUNDERWRITING_ERROR                    = _STATUS_CODE_CPIC_QUOTEUNDERWRITING_ERROR + 1
    static final _STATUS_CODE_CPIC_POLICYDECISION_ERROR                     = _STATUS_CODE_CPIC_TAXUNDERWRITING_ERROR + 1
    static final _STATUS_CODE_CPIC_QUERYVEHICLEMODELBYVINANDENGINENO_ERROR  = _STATUS_CODE_CPIC_POLICYDECISION_ERROR + 1
    static final _STATUS_CODE_CPIC_LOADCITYBRANCHCODE_ERROR                 = _STATUS_CODE_CPIC_QUERYVEHICLEMODELBYVINANDENGINENO_ERROR + 1

    static final DateTimeFormatter _DATETIME_FORMAT                             = new DateTimeFormatterBuilder().appendPattern('yyyy-MM-dd').toFormatter()

    static final DateFormat _DATE_FORMAT                                        = new SimpleDateFormat('yyyy-MM-dd')

    static final int _EARLY_INSURED_DAY                                         = 90

    static final DateTimeFormatter _DATETIME_FORMAT_NEW  = new DateTimeFormatterBuilder().appendPattern('yyyy-MM-dd HH:mm').toFormatter()

    static final _AUTOTYPE_EXTRACTOR = { context ->
        context.vehicleInfo?.vehicleBrand ? [context.vehicleInfo.vehicleBrand] : []
    }

    static final get_VEHICLE_MODEL_SUPPLEMENT_INFO_MAPPINGS() {
        [
            default: [
                _SUPPLEMENT_INFO_BRAND_CODE_TEMPLATE_QUOTING,
                _SUPPLEMENT_INFO_CAR_MODEL_LIST_TEMPLATE_QUOTING
            ]
        ]
    }

    static final _VEHICLE_INFO_EXTRACTOR = { context ->
        if ((context.baseInfoResult || context.vehicleInfo)) {
            [
                vinNo     : context.vehicleInfo?.carVin ?: context.baseInfoResult?.CTP?.carVin ?: context.baseInfoResult?.DX9?.carVin,
                engineNo  : context.vehicleInfo?.engineNo ?: context.baseInfoResult?.CTP?.engineNo ?: context.baseInfoResult?.DX9?.engineNo,
                enrollDate: context.vehicleInfo?.enrollDate ? _DATE_FORMAT3.parse(context.vehicleInfo.enrollDate) : null,
                brandCode : context.vehicleInfo?.modeName,
            ].with {
                it.vinNo && it.engineNo ? it : null
            }
        }
    }

    static final _AUTO_INFO_EXTRACTOR = { context ->
        def vehicleInfo = context.selectedCarModel ?: context.vehicleInfo
        if (vehicleInfo && vehicleInfo?.seatCount) {
            [
                autoType: [
                    seats: vehicleInfo?.seatCount as int
                ]
            ]
        }
    }

}
