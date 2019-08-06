package com.utils.common.rsa;

/**
 * Description:
 * Created by Quinin on 2019-08-06.
 **/
public class Base64DecodingException extends Exception {
    private String message;

    public Base64DecodingException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
