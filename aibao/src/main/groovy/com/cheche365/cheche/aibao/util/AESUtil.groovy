package com.cheche365.cheche.aibao.util

/**
 * Created by LIU GUO on 2018/7/24.
 */
import org.apache.commons.codec.binary.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class AESUtil {

    private static final String defaultCharset = 'UTF-8'
    private static final String KEY_AES = 'AES'
    private static final String KEY_MD5 = 'MD5'
    private static MessageDigest md5Digest
    static {
        try {
            md5Digest = MessageDigest.getInstance(KEY_MD5)
        } catch (NoSuchAlgorithmException e) {

        }
    }

    //加密方法使用示例：
    static void main(String[] args) throws Exception {
        String key = 'WXBJCZTEST'
        String data = '{\'head\':{\'platform\':\'ABKJ\',\'requestType\':10,\'timeStamp\':\'\',\'extTransactionNo\':\'\',\'localTransactionNo\':\'\',\'systemId\':\'\',\'MD5\':\'\',\'errorCode\':\'\',\'errorMessage\':\'\'},\'body\':{\'mainInfo\':{\'channelId\':\'CH10000020\',\'agentCode\':\'\',\'userId\':\'ABKJ999999\'}}}'
        String encrypted = AESUtil.encrypt(data, key)
        String decrypted = AESUtil.decrypt(encrypted, key)
        System.out.println('加密后的密文\n' + encrypted)
        System.out.println('解密后的报文:\n' + decrypted)
    }
    /**
     * 加密
     *
     * @param data 需要加密的内容
     * @param key 加密密码
     * @return
     */
    static String encrypt(String data, String key) {
        return doAES(data, key, Cipher.ENCRYPT_MODE)
    }
    /**
     * 解密
     *
     * @param data 待解密内容
     * @param key 解密密钥
     * @return
     */
    static String decrypt(String data, String key) {
        return doAES(data, key, Cipher.DECRYPT_MODE)
    }

    /**
     * 加解密
     *
     * @param data
     * @param key
     * @param mode
     * @return
     */
    private static String doAES(String data, String key, int mode) {
        try {
            boolean encrypt = mode == Cipher.ENCRYPT_MODE
            byte[] content
            if (encrypt) {
                content = data.getBytes(defaultCharset)
            } else {
                content = Base64.decodeBase64(data.getBytes())
            }
            SecretKeySpec keySpec = new SecretKeySpec(md5Digest.digest(key.getBytes(defaultCharset))
                , KEY_AES)
            Cipher cipher = Cipher.getInstance(KEY_AES)// 创建密码器
            cipher.init(mode, keySpec)// 初始化
            byte[] result = cipher.doFinal(content)
            if (encrypt) {
                return new String(Base64.encodeBase64(result))
            } else {
                return new String(result, defaultCharset)
            }
        } catch (Exception e) {
            throw e
        }
    }
}

