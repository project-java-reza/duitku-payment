package com.enigma.duitku.service;

public interface OtpService {

    String generateOtp();

    void sendOtp(String mobileNumber, String otpCode);
    boolean verifyOtp(String mobileNumber, String enteredOtp);
    void clearOtp(String mobileNumber);

}
