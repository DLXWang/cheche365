package com.cheche365.cheche.partner.utils

import com.cheche365.cheche.partner.config.app.Constant
import org.apache.commons.lang3.StringUtils

class PartnerEncodeUtil {

    static String encodeQueryString(String queryString) {
        if (StringUtils.isNoneBlank(queryString)) {
            try {
                return Constant.PARAM_NAME + "=" + URLEncoder.encode(queryString, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();  //should never happen
            }
        }
        return queryString;
    }

    static String decodeQueryString(String queryString) {
        if (StringUtils.isNoneBlank(queryString)) {
            try {
                return URLDecoder.decode(queryString, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();  //should never happen
            }
        }
        return queryString;
    }
}
