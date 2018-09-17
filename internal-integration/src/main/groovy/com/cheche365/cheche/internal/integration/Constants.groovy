package com.cheche365.cheche.internal.integration

import com.cheche365.cheche.core.util.ProfileProperties

/**
 * Created by zhengwei on 6/23/17.
 */
class Constants {

    static final String NA_DOMAIN
    static {
        def properties = new ProfileProperties('internal-integration.properties')
        NA_DOMAIN = properties.getProperty('na.base.url')
    }
}
