package com.cheche365.cheche.parser.dto

import com.cheche365.cheche.core.serializer.AdditionalParametersSerializer
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import groovy.transform.Canonical

@Canonical
class InsuranceRequestObject {

    @JsonDeserialize(using = AdditionalParametersSerializer)
    Map additionalParameters

}

