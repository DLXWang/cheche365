package com.cheche365.cheche.parser.dto

import com.cheche365.cheche.core.model.VehicleLicense
import groovy.transform.Canonical

@Canonical
class AutoTypeRequestObject extends InsuranceRequestObject {

    VehicleLicense vehicleLicense

}
