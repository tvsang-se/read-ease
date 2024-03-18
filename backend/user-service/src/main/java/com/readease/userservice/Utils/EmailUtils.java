package com.readease.userservice.Utils;

public class EmailUtils {
    public static String getVerificationUrl(String clientHost, String token) {
        return clientHost + "/quen-mat-khau?token=" + token;
    }

}
