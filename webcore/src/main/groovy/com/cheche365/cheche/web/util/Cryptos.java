package com.cheche365.cheche.web.util;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

public class Cryptos {

    private static final String AES = "AES";
    private static final int DEFAULT_AES_KEYSIZE = 128;


    /**
     * 使用AES加密原始字符串.
     *
     * @param input 原始输入字符数组
     * @param key   符合AES要求的密钥
     */
    public static String aesEncrypt(String input, String key) {
        byte[] bytes = aes(input.getBytes(), Base64.decodeBase64(key), Cipher.ENCRYPT_MODE);
        return Base64.encodeBase64String(bytes);
    }


    /**
     * 使用AES解密字符串, 返回原始字符串.
     *
     * @param input Hex编码的加密字符串
     * @param key   符合AES要求的密钥
     */
    public static String aesDecrypt(String input, String key) {
        byte[] decryptResult = aes(Base64.decodeBase64(input), Base64.decodeBase64(key), Cipher.DECRYPT_MODE);
        return new String(decryptResult);
    }


    /**
     * 使用AES加密或解密无编码的原始字节数组, 返回无编码的字节数组结果.
     *
     * @param input 原始字节数组
     * @param key   符合AES要求的密钥
     * @param mode  Cipher.ENCRYPT_MODE 或 Cipher.DECRYPT_MODE
     */
    private static byte[] aes(byte[] input, byte[] key, int mode) {
        try {
            SecretKey secretKey = new SecretKeySpec(key, AES);
            Cipher cipher = Cipher.getInstance(AES);
            cipher.init(mode, secretKey);
            return cipher.doFinal(input);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 生成AES密钥,返回字节数组, 默认长度为128位(16字节).
     */
    private static String generateAesKey() {
        return Base64.encodeBase64String(generateAesKey(DEFAULT_AES_KEYSIZE));
    }

    /**
     * 生成AES密钥,可选长度为128,192,256位.
     */
    private static byte[] generateAesKey(int keysize) {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(AES);
            keyGenerator.init(keysize);
            SecretKey secretKey = keyGenerator.generateKey();
            return secretKey.getEncoded();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        String text = "333333333333333";
        String key = "cr/ODs2jvOrrYPQm0FbQyA==";
        String enStr = aesEncrypt(text, key);
        System.out.println("aesEncrypt==" + enStr);
        String deStr = aesDecrypt(enStr, key);
        System.out.println("aesDecrypt==" + deStr);

    }


}
