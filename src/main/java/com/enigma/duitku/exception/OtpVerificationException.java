package com.enigma.duitku.exception;

public class OtpVerificationException extends Exception{

    public OtpVerificationException() {
    }

    public OtpVerificationException(String message) {
        super(message);
    }

    public OtpVerificationException(String message, Throwable cause) {
        super(message, cause);
    }

    public OtpVerificationException(Throwable cause) {
        super(cause);
    }

    public OtpVerificationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
