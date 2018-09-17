package com.cheche365.cheche.core.util;

import com.cheche365.cheche.common.util.DateUtils;

/**
 * Created by sunhuazhong on 2015/10/9.
 */
public class QRCodeChannelUtil {
    private static String qrcodeChannel = "qrcode:channel:statistics:";

    private static String statisticsDateKey = "qrcode:channel:statistics:date";

    public static String getQRCodeChannelDateKey() {
        return statisticsDateKey;
    }

    public static String getKey(String dateStr) {
        return qrcodeChannel + dateStr;
    }

    public static String getCurrentKey() {
        return qrcodeChannel + DateUtils.getCurrentDateString(DateUtils.DATE_SHORTDATE_PATTERN);
    }

    public static String getCurrentHashKeyForScan(Long qrCodeChannelId) {
        return qrCodeChannelId + ":" + DateUtils.getCurrentDateString(DateUtils.DATE_LONGTIME24_HOUR_PATTERN) + ":scan";
    }

    public static String getCurrentHashKeyForSubscribe(Long qrCodeChannelId) {
        return qrCodeChannelId + ":" + DateUtils.getCurrentDateString(DateUtils.DATE_LONGTIME24_HOUR_PATTERN) + ":subscribe";
    }
}
