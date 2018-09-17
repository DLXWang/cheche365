package com.cheche365.cheche.manage.common.constants;
import com.cheche365.cheche.core.util.RuntimeUtil;


/**
 * Created by chenxy on 2018/04/12.
 */
public class BaiduInsurConstant {
    public static String FTP_URL;
    public static String FTP_PWD;
    public static String FTP_PORT;
    public static String FTP_DERECTORY;
    public static String CHECHE_PRIVATE_KEY;
    public static String PARTNER_SP_NO;
    static{
        String prefix = (RuntimeUtil.isProductionEnv() ? "production." : "");
        FTP_URL = prefix + "ftp.url";
        FTP_PWD = prefix + "ftp.pwd";
        FTP_PORT = prefix + "ftp.port";
        FTP_DERECTORY = prefix + "ftp.directory";
        CHECHE_PRIVATE_KEY = prefix + "cheche.private.key";
        PARTNER_SP_NO = prefix + "partner.sp.no";
    }
}
