package com.orders;

import com.orders.models.Token;
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


    public static Token generateToken () {
        Random randNumber = new Random();
        Integer number = randNumber.nextInt(1000);
        String md5Hex;
        try {
            byte[] password = (APP_SECRET + APP_KEY + "billing" + number.toString()).getBytes("UTF-8");
            md5Hex = DigestUtils.md5DigestAsHex(password).toUpperCase();
        } catch (UnsupportedEncodingException exc) {
            return null;
        }
        return new Token(md5Hex);
    }
}
