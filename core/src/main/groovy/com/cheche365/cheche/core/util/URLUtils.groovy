package com.cheche365.cheche.core.util

import org.apache.commons.lang3.StringUtils

import java.util.regex.Pattern

class URLUtils {

    static boolean isURL(String url) {
        String regex = "(((http|https|ftp)://){1}[a-zA-Z0-9]{1,}(-[a-zA-Z0-9]{1,}){1,}(:[0-9]{1,4})?.*)|(((http|https|ftp)://){1}([0-9]{1,3}\\.){3}[0-9]{1,3}(:[0-9]{1,4})?.*)|(((http|https|ftp)://){1}[w]{3}.[a-zA-Z0-9]{1,}(:[0-9]{1,4})?.*)";
        return Pattern.matches(regex, url);
    }

    static String baseUrl(String url) {
        return url.indexOf('?') > 0 ? url.substring(0, url.indexOf('?')) : url;
    }

    static Map<String, String> splitQuery(String queryString, boolean valueDecode = true) {
        if (StringUtils.isBlank(queryString)) {
            return [:]
        }

        queryString.split('&').collectEntries {
            def kvs = it.split('=', 2).with { kv ->
                kv.size() > 1 ? kv.toSpreadMap() : [(kv.first()): it.split('=') ? '' : kv.first()]
            }
            valueDecode ? kvs.collectEntries { key, value ->
                [(key): valueDecode ? URLDecoder.decode(value, "UTF-8") : value]
            } : kvs
        }
    }

    static String formatPath(String path, List pathParams) {
        pathParams.inject(path) { result, pathSegment ->
            (result =~ /\{(\w+)\}/).replaceFirst(pathSegment as String)
        }
    }

}
