package com.cheche365.cheche.wechat.util;

import org.bouncycastle.util.encoders.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.AlgorithmParameters;
import java.security.Key;
import java.security.Security;

/**
 * Created by chenqc on 2016/10/25.
 */
public class WechatAppDecryptor {

    //算法名
    public static final String KEY_ALGORITHM = "AES";
    //加解密算法/模式/填充方式
    //可以任意选择，为了方便后面与iOS端的加密解密，采用与其相同的模式与填充方式
    //ECB模式只用密钥即可对数据进行加密解密，CBC模式需要添加一个参数iv
    public static final String CIPHER_ALGORITHM = "AES/CBC/PKCS7Padding";

    //生成密钥
    public static byte[] generateKey() throws Exception{
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        KeyGenerator keyGenerator = KeyGenerator.getInstance(KEY_ALGORITHM);
        keyGenerator.init(128);
        SecretKey key = keyGenerator.generateKey();
        return key.getEncoded();
    }

    //生成iv
    public static AlgorithmParameters generateIV(byte [] iv) throws Exception{
        //iv 为一个 16 字节的数组，这里采用和 iOS 端一样的构造方法，数据全为0
        AlgorithmParameters params = AlgorithmParameters.getInstance(KEY_ALGORITHM);
        params.init(new IvParameterSpec(iv));
        return params;
    }

    //转化成JAVA的密钥格式
    public static Key convertToKey(byte[] keyBytes) throws Exception{
        return new SecretKeySpec(keyBytes,KEY_ALGORITHM);
    }

    //加密
    public static byte[] encrypt(byte[] data,byte[] keyBytes,AlgorithmParameters iv) throws Exception {
        //转化为密钥
        Key key = convertToKey(keyBytes);
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        //设置为加密模式
        cipher.init(Cipher.ENCRYPT_MODE, key,iv);
        return cipher.doFinal(data);
    }

    //小程序解密算法参见： https://mp.weixin.qq.com/debug/wxadoc/dev/api/signature.html?t=1476197485163
    public static byte[] decrypt(String dataStr,String keyStr, String ivStr) throws Exception{
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        byte[] encryptedData = Base64.decode(dataStr);
        byte[] keyBytes = Base64.decode(keyStr);
        AlgorithmParameters iv = WechatAppDecryptor.generateIV(Base64.decode(ivStr));
        Key key = convertToKey(keyBytes);
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        //设置为解密模式
        cipher.init(Cipher.DECRYPT_MODE, key,iv);
        return cipher.doFinal(encryptedData);
    }
}
