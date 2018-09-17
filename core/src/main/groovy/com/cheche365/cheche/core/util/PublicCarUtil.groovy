package com.cheche365.cheche.core.util

import com.cheche365.cheche.core.model.Auto
import com.cheche365.cheche.core.model.IdentityType

/**
 * Created by shanxf on 2017/11/21.
 *  不展示公户车
 */
class PublicCarUtil {


    static boolean isPublicType(Auto auto){
        return IdentityType.Enum.IDENTITYCARD != auto.identityType
    }

    static String getAutoIdentify(Auto auto){
        return  isPublicType(auto) ? null : auto.identity
    }
    
}
