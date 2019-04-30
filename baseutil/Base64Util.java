package com.mxnavi.mobile.utils.baseutil;

import android.text.TextUtils;
import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

public class Base64Util {
    /**
     * 随机生成RSA密钥对
     *
     * @param keyLength 密钥长度，范围：512～2048
     * 一般1024
     * @return
     */
    // 非对称加密密钥算法
    public static final String RSA = "RSA";
    //加密填充方式
    public static final String ECB_PKCS1_PADDING = "RSA/ECB/PKCS1Padding";
    //秘钥默认长度
    public static final int DEFAULT_KEY_SIZE = 2048;
    public static final byte[] DEFAULT_SPLIT = "#PART#".getBytes();
    //生成秘钥对
    public static final KeyPair keyPair = Base64Util.generateRSAKeyPair(DEFAULT_KEY_SIZE);
    // 公钥秘钥对
    public static final RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
    // 私钥秘钥对
    public static final RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

    public static KeyPair generateRSAKeyPair(int keyLength) {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance(RSA);
            kpg.initialize(keyLength);
            return kpg.genKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 解密算法
     *
     * @param str 需要被解密的字符串
     * @return 已解密的字符串
     */
    public static String decode(String str) {
        byte[] bytes;
        try {
            bytes = Base64.decode(str, Base64.NO_WRAP);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        byte[] tmp = new byte[bytes.length];
        for (int i = 0; i < tmp.length; i++) {
            tmp[i] = (byte) (bytes[i] ^ 0x13);
        }
        String s = "";
        try {
            s = new String(tmp, "GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return s;
    }

    /**
     * 加密算法
     *
     * @param str 需要被加密的字符串
     * @return 已加密的字符串
     */
    public static String encode(String str) {
        byte[] baKeyword = null;
        try {
            byte[] bytes = str.getBytes();
            baKeyword = new byte[bytes.length];
            for (int i = 0; i < bytes.length; i++) {
                baKeyword[i] = (byte) (0x13 ^ bytes[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Base64.encodeToString(baKeyword, Base64.NO_WRAP);
    }

    public static void base64Encode(String content) {
        String encodedString = Base64.encodeToString(content.getBytes(), Base64.DEFAULT);
        L.d("Base64encode---->" + encodedString);
    }

    public static void base64Decode(String encodedString) {
        //Base64对用户名解密
        String decodedString = new String(Base64.decode(encodedString, Base64.DEFAULT));
        L.e("Base64decoded---->" + decodedString);
    }

    /**
     * md5加密（字符串）
     *
     * @param string
     * @return
     */
    public static String md5(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            String result = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }


}
