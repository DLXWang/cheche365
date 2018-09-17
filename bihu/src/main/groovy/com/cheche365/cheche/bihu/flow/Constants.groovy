package com.cheche365.cheche.bihu.flow

import com.cheche365.cheche.core.model.InsuranceCompany

import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.AXATP_55000
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.CCIC_240000
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.CHINALIFE_40000
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.CIC_45000
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.CPIC_25000
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.PICC_10000
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.PINGAN_20000
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.SINOSAFE_205000
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.SINOSIG_15000
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.TAIPING_30000
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.TIAN_100000
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.YDPIC_155000
import static com.cheche365.cheche.core.model.VehicleDataSource.Enum.BIHU_1
import static com.cheche365.cheche.parser.Constants._DATE_FORMAT3
import static com.cheche365.cheche.core.model.UseCharacter.Enum.*
import static com.cheche365.cheche.core.model.FuelType.Enum.*
import static com.cheche365.cheche.core.model.IdentityType.Enum.*

/**
 * 壁虎流程步骤所需的常量
 */
class Constants {

    static final _QUOTING_WAITING_SECONDS = 40 // 调用报价前接口等待时间

    static final _VEHICLE_INFO_EXTRACTOR = { context ->
        def vehicleInfo = context.vehicleInfo
        if (vehicleInfo) {
            [
                vinNo           : vehicleInfo.CarVin,
                engineNo        : vehicleInfo.EngineNo,
                owner           : vehicleInfo.LicenseOwner,
                enrollDate      : vehicleInfo.RegisterDate ? _DATE_FORMAT3.parse(vehicleInfo.RegisterDate) : null,
                brandCode       : vehicleInfo.ModleName - '牌',
                identity        : vehicleInfo.CredentislasNum,
                dataSource      : BIHU_1,
                seats           : vehicleInfo.SeatCount,
                newPrice        : vehicleInfo.PurchasePrice,
                isPublic        : vehicleInfo.IsPublic,
                licenseColorCode: vehicleInfo.LicenseColor,
                useCharacter    : _USE_CHARACTER_MAPPINGS[vehicleInfo.CarUsedType],
                fuelType        : _FUEL_TYPE_MAPPINGS[vehicleInfo.FuelType],
                identityType    : _IDENTITY_TYPE_MAPPINGS[vehicleInfo.IdType]
            ].with {
                it.vinNo && it.engineNo ? it : null
            }
        }
    }

    static final _PERSONNEL_INFO_EXTRACTOR = { context ->
        def vehicleInfo = context.vehicleInfo
        if (vehicleInfo) {
            [
                applicantName        : vehicleInfo.PostedName,
                applicantIdNo        : vehicleInfo.HolderIdCard,
                applicantIdentityType: _IDENTITY_TYPE_MAPPINGS[vehicleInfo.HolderIdType],
                applicantMobile      : vehicleInfo.HolderMobile,
                insuredName          : vehicleInfo.InsuredName,
                insuredIdNo          : vehicleInfo.InsuredIdCard,
                insuredIdentityType  : _IDENTITY_TYPE_MAPPINGS[vehicleInfo.InsuredIdType],
                insuredMobile        : vehicleInfo.InsuredMobile,
                insuranceCompany     : _INSURANCE_COMPANY_MAPPINGS[context.renewSource]?.with {
                    new InsuranceCompany(id: it.id, code: it.code, name: it.name)
                }
            ]
        }
    }

    static final _BIHU_LOAD_PERSISTENT_STATE = { context, persistentState ->
        [
            custKey: persistentState?.custKey
        ]
    }

    static final _BIHU_SAVE_PERSISTENT_STATE = { context ->
        [
            custKey: context.custKey
        ]
    }

    private static final _USE_CHARACTER_MAPPINGS = [
        1: FAMILY_21,
        2: ORGANIZATION_23,
        3: BUSINESS_22
    ]

    private static final _FUEL_TYPE_MAPPINGS = [
        1: GASOLINE_1,
        2: DIESEL_3,
        3: ELECTRICITY_2,
        5: NATURAL_GAS_4,
    ]

    private static final _IDENTITY_TYPE_MAPPINGS = [
        1: IDENTITYCARD,
        2: ORGANIZATION_CODE,
        3: PASSPORT,
        4: OFFICERARD,
        9: BUSINESS_LICENSE
    ]

    private static final _INSURANCE_COMPANY_MAPPINGS = [
        1   : CPIC_25000,
        2   : PINGAN_20000,
        4   : PICC_10000,
        8   : CHINALIFE_40000,
        16  : CIC_45000,
        32  : CCIC_240000,
        64  : SINOSIG_15000,
        128 : TAIPING_30000,
        256 : SINOSAFE_205000,
        512 : TIAN_100000,
        1024: YDPIC_155000,
        2048: AXATP_55000
    ]
}

