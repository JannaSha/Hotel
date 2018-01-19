package com.gateway;



import org.springframework.util.DigestUtils;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.security.MessageDigest;

public class TokenManage {
    private final int TOKEN_VALIDATION_TIME_SECONDS = 1800;
    private final String APP_KEY = "appkey";
    private final String APP_SECRET = "appsecret";

//    private List<Timestamp> tokesValidationTimes;
//    private List<String> tokens;

    public String getAPP_KEY() {
        return APP_KEY;
    }

    public String getAPP_SECRET() {
        return APP_SECRET;
    }

    public String getHashAppParams() {
        String md5Hex;
        try {
            byte[] password = (APP_KEY + APP_SECRET).getBytes("UTF-8");
            md5Hex = DigestUtils.md5DigestAsHex(password).toUpperCase();
        } catch (UnsupportedEncodingException exc) {
            return null;
        }
//        String hash = "35454B055CC325EA1AF2126E27707052";
        return  md5Hex;
    }

    public boolean checkToken(Timestamp tokenGetTime) {
        boolean result = true;
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(tokenGetTime.getTime());
        cal.add(Calendar.SECOND, TOKEN_VALIDATION_TIME_SECONDS);
        Timestamp later = new Timestamp(cal.getTime().getTime());
        if (later.before((new Timestamp(System.currentTimeMillis())))) {
            result = false;
        }
        return result;
    }
}
