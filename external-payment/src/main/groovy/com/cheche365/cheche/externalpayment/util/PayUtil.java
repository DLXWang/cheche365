package com.cheche365.cheche.externalpayment.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * Created by chenqc on 2016/11/22.
 */
public class PayUtil {

    private static Logger logger = LoggerFactory.getLogger(PayUtil.class);

    /**
     * map排序
     *
     * @param map
     * @return
     */
    public static Map<String, String> sortMapByKey(Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }

        Map<String, String> sortMap = new TreeMap<String, String>(
            new Comparator<String>() {
                public int compare(String obj1, String obj2) {
                    // 降序排序
                    return obj1.compareTo(obj2);
                }
            });
        sortMap.putAll(map);
        return sortMap;
    }

    /**
     * 获取签名(带合作码)
     *
     * @param map
     * @return
     */
    public static String getSign(String cooperationCode, Map<String, String> map) {
        StringBuilder paras = new StringBuilder();
        paras.append(cooperationCode);
        Map<String, String> sortMap = PayUtil.sortMapByKey(map);
        int i = 0;
        for (Entry<String, String> entry : sortMap.entrySet()) {
            if (entry.getValue() != null && !"".equals(entry.getValue()) && !"checkValue".equals(entry.getKey())) {
                if (i == 0) {
                    paras.append(entry.getKey() + "=" + entry.getValue());
                } else {
                    paras.append("&" + entry.getKey() + "=" + entry.getValue());
                }
                i++;
            }
        }
        logger.info("sing加密前字符串》》》" + paras.toString());
        String sign = PayUtil.MD5(paras.toString()).toLowerCase();
        return sign;
    }



    /**
     * MD5加密
     *
     * @param s
     * @return
     */
    public final static String MD5(String s) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        try {
            byte[] btInput = s.getBytes(Charset.forName("UTF-8"));
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
