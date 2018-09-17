package com.cheche365.cheche.externalapi.signature.tk

import groovy.json.JsonOutput
import org.apache.commons.codec.digest.DigestUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import sun.misc.BASE64Decoder
import sun.misc.BASE64Encoder
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec
import java.security.InvalidKeyException

/**
 * Created by wen on 2018/5/22.
 */
class TKSignature {

    static final Logger logger = LoggerFactory.getLogger(TKSignature.class);

    public static String encryptECB(String key, String value) {
        try {
            logger.info("加密前: "+value);
            SecretKey secretKey = new SecretKeySpec(key.getBytes("UTF-8"), "DES");
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] binaryData = cipher.doFinal(value.getBytes("UTF-8"));
            BASE64Encoder encoder = new BASE64Encoder();
            String result = encoder.encode(binaryData);
            logger.info("加密后: "+result);
            return result;
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            logger.info("Invalid DES key, not encrypting");
            return null;
        } catch (Exception e1) {
            e1.printStackTrace();
            logger.info("Error in encryption, not encrypting");
            return null;
        }
    }

    public static String decryptECB(String key, String value) {
        try {
            BASE64Decoder decoder = new BASE64Decoder();
            byte[] binaryValue = decoder.decodeBuffer(value);
            SecretKey secretKey = new SecretKeySpec(key.getBytes("UTF-8"), "DES");
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] data = cipher.doFinal(binaryValue);
            String decryptData=new String(data, "UTF-8");
            logger.info("解密后:"+decryptData);
            return decryptData;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String addToken(String json, String key) {
        Map<String, Object> map = new HashMap<String, Object>();
        String token = DigestUtils.md5Hex(json + key);
        logger.info("md5加密:"+token);
        map.put("token", token);
        return JsonOutput.toJson(map);
    }

}
