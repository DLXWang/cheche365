package com.cheche365.cheche.core.constants;

import com.cheche365.cheche.core.context.ApplicationContextHolder;
import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.service.spi.ISchemaService;
import com.cheche365.cheche.core.util.ProfileProperties;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Calendar;
import java.util.Properties;

import static com.cheche365.cheche.core.model.ApiPartner.Enum.BDINSUR_PARTNER_50;
import static com.cheche365.cheche.core.model.Channel.Enum.PARTNER_BDINSUR_215;

/**
 * Created by zhengwei on 4/24/15.
 */
public class WebConstants {

    public static final String API_VERSION = "v1.7";
    public static final String M_ROOT_PATH = "/m/index.html";
    public static final String AGENT_ROOT_PATH = "/a/index.html";
    public static final String BDINSUR_ROOT_PATH = "/bdinsur/index.html";
    private static final String FRONT_CALLBACK_PATH = "/callback/index.html";
    private static final String SERVER_CALLBACK_PATH = "/api/callback/pay/";
    public static final String WEB_ROOT_PATH = "/website/index.html";
    public static final String MARKETING_ROOT_PATH = "/marketing/m/";

    public static final int DEFAULT_REDIS_TIME_OUT = 2 * 60 * 60;
    public static final String SESSION_KEY_USER = "user";
    public static final String SESSION_KEY_CHANNEL_AGENT = "channel_agent";
    public static final String SESSION_KEY_PARTNER_UID = "partner_uid";
    public static final String SESSION_KEY_USER_CALLBACK = "user_callback";
    public static final String SESSION_KEY_QUOTE_RECORD = "quote_record";
    public static final String SESSION_KEY_SAVED_QUOTE_RECORD = "saved_quote_record_";
    public static final String SESSION_KEY_CLIENT_TYPE = "client_type";
    public static final String SESSION_KEY_WECHAT_USER_INFO = "wechatUserInfo";
    public static final String SESSION_KEY_WECHAT_OPEN_ID = "wechatOpenId";
    private static final String SESSION_KEY_MARKETING_UUID = "marketingUUID_";
    public static final String SESSION_KEY_TURN_OFF_REFER_RULE_ENGINE_QUOTE = "turn_off_refer_rule_engine_quote";
    public static final String SUPPLEMENT_INFO_SUPPORT_LIST = "supplementInfoSupportList";
    public static final String PERSISTENT_STATE = "persistentState";
    public static final String ADDITIONAL_QR_DATA = "additionalQRData:qrid:";
    public static final String CAPTCHA_IMAGE_FLAG = "captchaImageFlag";
    public static final String AUTO_MODEL_OPTIONS = "autoModelOptions";
    public static final String QUOTE_FLOW_TYPE = "flowType";
    public static final String OAUTH_CHANNEL = "oauth_channel";

    public static final String WECHAT_APP_HEADER = "CheChe-WA-CX"; //车险微信小程序session header
    public static final String CLAIM_APP_HEADER = "CheChe-WA-CLAIM"; //要不要赔微信小程序session header

    public static String WECHAT_USER_AGENT_KEY = "MicroMessenger";
    public static String ALIPAY_USER_AGENT_KEY = "Alipay";
    public static String ORDER_CENTER_USER_AGENT_KEY = "cheche.support.client.orderCenter";
    public static String NON_AUTO_USER_AGENT_KEY = "cheche.nonauto.channel";

    public static final String VERSION_NO = "{v:^[v]\\d{1,2}$|^[v]\\d{1,2}[.]{1,1}\\d{0,2}$}";  //写在spring注解里用这个

    public static final int PAGE_SIZE = 10;
    public static final int ORDER_EXPIRE_INTERVAL = 24;
    public static final int ORDER_EXPIRE_INTERVAL_TIMEUNIT = Calendar.HOUR;

    public static final String COMMON_MARKETING_CODE = "201608002";
    public static final String COMMON_MARKETING_ID = "70";
    public static final String COMMON_MARKETING_LOGIC_SYMBOL_AND = "&&";
    public static final String COMMON_MARKETING_SYMBOL_SPLIT = "_";
    public static final String COMMON_MARKETING_SYMBOL_GIFT_SPLIT = "\\|";

    public static final String IMAGE_BANNER_PATH = "image" + File.separator + "banner/";

    public static final String PUT_VALUE_WITH = "appointments_count_value_with";
    public static final String PUT_VALUE_ORDER_COUNT = "order_value_count";
    public static final String PUT_VALUE_ORDER_COUNT_MONEY = "order_value_count_money";

    public static final String NOT_CHECK_ALLOW_QUOTE = "not_check_allow_quote";
    public static final String NOT_CHECK_ALLOW_PAY = "not_check_allow_pay";
    public static final String PERIOD_NOT_ALLOWED_PAY = "period_not_allow_pay";
    public static final String NOT_CHECK_CHANNEL = "not_check_channel_";
    public static final String NOT_INTERCEPT_SMS = "not_intercept_sms";

    public final static String ALLOW_ORDER_PAY = "allow_order_pay"; //电销发短信后订单可支付

    public static final String SESSION_KEY_INTERNAL_USER = "internal_user";
    public static final String SESSION_KEY_IMPERSONATION_USER = "impersonation_user";
    public static final String SESSION_KEY_PARTNER_STATE = "partner_state";
    public static final String SESSION_KEY_ALLOW_QUOTE_TAG = "allow_quote_tag";
    public static final String SESSION_KEY_CPS_CHANNEL = "cps_channel";
    public static final String SESSION_KEY_ALLOW_SEND_SMS = "allow_send_sms";
    public static final String WEB_DEFAULT_CPS_MARK = "cps";
    public static final String ORDER_CENTER_TOKEN = "ORDER-CENTER-TOKEN";
    public static final String WECHAT_OAUTH_CALLBACK = "/wechat/oauth/callback";
    private static Logger logger = LoggerFactory.getLogger(WebConstants.class);

    private static String HOST;
    private static String PORT;

    public static final String SHORT_COMPANY_NAME = "车车";
    public static final String LONG_COMPANY_NAME = "北京车与车科技有限公司";
    public static final String IP = "ip";
    public static final String USER = "user";
    public static final String SESSION_MOBILE = "session_mobile";

    //未付款文本
    public final static String NO_PAID_TEXT = "等待付款";

    public final static String THIRD_PARTNER_MOBILE_BAIDU = "4000821056";
    public final static String CHECHE_CUSTOMER_SERVICE_MOBILE = "4000150999";

    public static final String CHANNEL_SERVICE_ITEMS = "channelServiceItems";

    public static final String ANSWERN_NOTIFY_EMAIL_CODE = "answern";
    public static final String BOTPY_NOTIFY_EMAIL_CODE = "botpy";
    public static final String CROCODILE_NOTIFY_EMAIL_CODE = "crocodile";


    public static String getDomain() {
        if (null == HOST) {  //延时加载web.properties中的domain属性，以防非web环境下空指针问题
            synchronized (WebConstants.class) {
                if (null == HOST) {
                    initDomain();
                }
            }
        }

        return StringUtils.isBlank(PORT) ? HOST : HOST + ":" + PORT;
    }

    public static String getHost() {  //延时加载web.properties中的domain属性，以防非web环境下空指针问题
        if (null == HOST) {    //无锁时检查
            synchronized (WebConstants.class) {
                if (null == HOST) {  //加锁后检查
                    initDomain();
                }
            }
        }
        return HOST;
    }

    public static String getDomainURL() {
        return getSchemaURL() + getDomain();
    }

    public static String getSecurityDomainURL(){
        return "https://" +getDomain();
    }

    public static String getDomainURL(boolean safety){
        return getSchemaURL(safety) + getDomain();
    }

    public static String getMarketingCodeURL(String marketingCode) {
        return getDomainURL() + MARKETING_ROOT_PATH + marketingCode + "/index.html";
    }

    public static String getMarketingUUIDSessionKey(String marketingCode) {
        return SESSION_KEY_MARKETING_UUID + marketingCode;
    }

    public static String getSchema() {
        try {
            ISchemaService schemaService = ApplicationContextHolder.getApplicationContext().getBean(ISchemaService.class);
            return schemaService.getSchema();
        }catch (Exception e){
            return "https";
        }
    }


    public static String getSchemaURL() {
        return getSchema() + "://";
    }

    public static String getSchemaURL(boolean safety) {
        return safety ? getSchemaURL() : "http://";
    }

    //M站 or Agent首页
    public static String getIndexPath(Channel channel) {
        return getDomainURL() + getRootPath(channel);
    }

    public static String getFrontCallbackPath() {
        return WebConstants.getDomainURL() + FRONT_CALLBACK_PATH;
    }

    public static String getServerCallbackPath() {
        return "https://" + getDomain() + SERVER_CALLBACK_PATH;
    }

    public static String getRootPath(Channel channel) {
        return channel.isThirdPartnerChannel() && BDINSUR_PARTNER_50.equals(channel.getApiPartner()) ? BDINSUR_ROOT_PATH : channel.isStandardAgent() ? AGENT_ROOT_PATH : M_ROOT_PATH;
    }

    public static String getPath(Channel channel) {
        return (PARTNER_BDINSUR_215.equals(channel)) ? "bdinsur" : (channel.isStandardAgent() ? "a" : "m");
    }

    private static void initDomain() {

        Properties properties = new Properties();
        try {
            properties.load(WebConstants.class.getResourceAsStream("/properties/web.properties"));
        } catch (Exception e) {
            logger.error("load web configuration file failed,", e);
        }

        ProfileProperties profileProperties = new ProfileProperties(properties);

        HOST = profileProperties.getProperty("cheche.domain");
        PORT = profileProperties.getProperty("cheche.domain.port");
    }

}
