package com.cheche365.cheche.externalpayment.constants

import com.cheche365.cheche.core.model.OrderStatus
import com.cheche365.cheche.core.model.OrderSubStatus
import com.cheche365.cheche.core.model.OrderTransmissionStatus
import com.cheche365.cheche.core.util.ProfileProperties
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class SinosafeConstant {

    private static Logger logger = LoggerFactory.getLogger(SinosafeConstant.class);

    static AREAS_REQUIRE_VERIFY_CODE = [110000L]
    public static final String PAY_URL
    public static final String BACK_URL
    static final String SINOSAFE_INSURE_SUCCESS = '7' //已核保
    static final String SINOSAFE_INSURE_IMAGE = '3' //补充影像
    static final String SINOSAFE_UNINSURE = '0' //未核保

    static {
        ProfileProperties prop = getProperties("/sinosafepay.properties")
        PAY_URL = prop.getProperty("pay_url")
        BACK_URL=prop.getProperty("back_url")
    }

    static ProfileProperties getProperties(String path) {
        Properties properties = new Properties()
        try {
            properties.load(SinosafeConstant.class.getResourceAsStream(path))
        } catch (IOException ex) {
            logger.error("load sinosafe pay properties files has error", ex)
        }
        return new ProfileProperties(properties)
    }

    static final SINOSAFE_ORDER_STATUS_MAPPING = [
        (SINOSAFE_UNINSURE) : OrderStatus.Enum.INSURE_FAILURE_7,
        (SINOSAFE_INSURE_IMAGE) : OrderStatus.Enum.INSURE_FAILURE_7,
        (SINOSAFE_INSURE_SUCCESS) : OrderStatus.Enum.PENDING_PAYMENT_1
    ]

    static final CHECHE_OPERATION_CENTER_ORDER_STATUS_MAPPING = [
        (OrderStatus.Enum.PENDING_PAYMENT_1): OrderTransmissionStatus.Enum.UNPAID,
        (OrderStatus.Enum.INSURE_FAILURE_7) : OrderTransmissionStatus.Enum.UNDERWRITING_FAILED,
        (OrderSubStatus.Enum.FAILED_1)    : OrderTransmissionStatus.Enum.UNCONFIRMED,
        (OrderStatus.Enum.CANCELED_6)       : OrderTransmissionStatus.Enum.CANCELED
    ]
}
