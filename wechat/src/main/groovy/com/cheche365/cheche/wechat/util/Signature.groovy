package com.cheche365.cheche.wechat.util;

import com.cheche365.cheche.core.WechatConstant
import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.util.MD5
import com.cheche365.cheche.web.util.ClientTypeUtil
import com.cheche365.cheche.wechat.model.PrePayResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import static com.cheche365.cheche.core.model.Channel.Enum.WE_CHAT_APP_39;

class Signature {
    private static Logger logger = LoggerFactory.getLogger(Signature.class);

    /**
     * 签名算法
     * @param o 要参与签名的数据对象
     * @return 签名
     * @throws IllegalAccessException
     */
     static String getSign(Object o) {
        ArrayList<String> list = new ArrayList<String>();
        Class cls = o.getClass();
        while (!(Object.class == cls)) {
            Field[] fields = cls.getDeclaredFields();
            for (Field f : fields) {
                f.setAccessible(true);
                try {
                    if (!f.isSynthetic() && f.get(o) != null && f.get(o) != "") {
                        list.add(f.getName() + "=" + f.get(o) + "&");
                    }
                } catch (IllegalAccessException e) {
                    logger.error("can't sign the object.", e);
                }
            }
            cls = cls.getSuperclass();
        }
        int size = list.size();
        String [] arrayToSort = list.toArray(new String[size]);
        Arrays.sort(arrayToSort, String.CASE_INSENSITIVE_ORDER);
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < size; i ++) {
            sb.append(arrayToSort[i]);
        }
        String result = sb.toString();
        result += "key=" + WechatConstant.API_SECRET;
        if (logger.isDebugEnabled()) {
            logger.debug("Sign Before MD5:" + result);
        }
        result = MD5.MD5Encode(result).toUpperCase();
        if (logger.isDebugEnabled()) {
            logger.debug("Sign Result:" + result);
        }
        return result;
    }

    static String getSign(Map<String,Object> map){
        List<String> list = new ArrayList<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() != null && entry.getValue() != "") {
                list.add(entry.getKey() + "=" + entry.getValue() + "&");
            }
        }
        int size = list.size();
        String [] arrayToSort = list.toArray(new String[size]);
        Arrays.sort(arrayToSort, String.CASE_INSENSITIVE_ORDER);
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < size; i ++) {
            sb.append(arrayToSort[i]);
        }
        String result = sb.toString();
        result += "key=" + WechatConstant.API_SECRET;
        if (logger.isDebugEnabled()) {
            logger.debug("Sign Before MD5  :" + result);
        }
        result = MD5.MD5Encode(result).toUpperCase();
        if (logger.isDebugEnabled()) {
            logger.debug("Sign Result:" + result);
        }
        return result;
    }

    static String getSign(Channel channel, PrePayResult prepayResult, HttpServletRequest request){
        getSign(toSignParams(channel, prepayResult, request))
    }

    static Map toSignParams(Channel channel, PrePayResult prepayResult, HttpServletRequest request){
        if(Channel.selfApp().contains(channel) ){
            [
                package: prepayResult.getPackage(),
                partnerid: prepayResult.getPartnerId(),
                prepayid: prepayResult.getPrePayId(),
                appid: prepayResult.getAppId(),
                timestamp: prepayResult.getTimeStamp(),
                noncestr: prepayResult.getNonceString()
            ]
        } else {
            [
               package: publicAccountALike(channel, request) ? "prepay_id=" + prepayResult.getPrePayId() : "MWEB",
               appId: prepayResult.getAppId(),
               timeStamp: prepayResult.getTimeStamp(),
               nonceStr: prepayResult.getNonceString(),
               signType: prepayResult.getSignType()

            ]
        }
    }

    static publicAccountALike(Channel channel, HttpServletRequest request){
        WE_CHAT_APP_39==channel || ClientTypeUtil.inWechat(request)
    }

}
