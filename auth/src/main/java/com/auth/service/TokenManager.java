package com.auth.service;


import org.springframework.util.DigestUtils;

import java.io.UnsupportedEncodingException;
import java.util.Random;

public class TokenManager {
    private static final String APP_KEY = "appkey";
    private static final String APP_SECRET = "appsecret";


    public static String getAppKey() {
        String md5Hex;
        try {
            byte[] password = (APP_KEY + APP_SECRET).getBytes("UTF-8");
            md5Hex = DigestUtils.md5DigestAsHex(password).toUpperCase();
        } catch (UnsupportedEncodingException exc) {
            return null;
        }
        return  md5Hex;
    }


    public static String generateToken (String username) {
        Random randNumber = new Random();
        Integer number = randNumber.nextInt(100000);
        String md5Hex;
        try {
            byte[] password = (username  + "auth" + number.toString()).getBytes("UTF-8");
            md5Hex = DigestUtils.md5DigestAsHex(password).toUpperCase();
        } catch (UnsupportedEncodingException exc) {
            return null;
        }
        return md5Hex;
    }
}
