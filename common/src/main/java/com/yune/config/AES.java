package com.yune.config;

import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
public class AES {
    private static final String key = "RyuLionRyuLionLV";

    //加密
    public static String encrypt(String plainText) throws Exception{
        //创建密码器
        Cipher cipher = Cipher.getInstance("AES");
        //根据密钥初始化密码器
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "AES");
        cipher.init(Cipher.ENCRYPT_MODE,secretKeySpec);
        //用密码器加密传入的字符串
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
        //以Base64编码返回。返回值是String
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    //解密
    public static String decrypt(String encryptedText) throws Exception{
        //创建密码器
        Cipher cipher = Cipher.getInstance("AES");
        //根据密钥初始化密码器
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "AES");
        cipher.init(Cipher.DECRYPT_MODE,secretKeySpec);
        byte[] decodeBytes = Base64.getDecoder().decode(encryptedText);
        byte[] decryptedBytes = cipher.doFinal(decodeBytes);
        return new String(decryptedBytes);

    }
}
