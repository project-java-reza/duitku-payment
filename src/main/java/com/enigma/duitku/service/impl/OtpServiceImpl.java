package com.enigma.duitku.service.impl;

import com.enigma.duitku.service.OtpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpServiceImpl implements OtpService {

    private final Map<String, String> otpStorage = new HashMap<>();
    private final Random random = new Random();

    @Override
    public String generateOtp() {
        int otpValue = 1000 + random.nextInt(9000);
        String otpCode = String.valueOf(otpValue);

        log.info("Generated OTP: {}", otpCode);
        return otpCode;

    }

    @Override
    public void sendOtp(String mobileNumber, String otpCode) {
    // Implementasikan logika pengiriman OTP ke nomor telepon
        log.info("Mengirim OTP {} ke {}", otpCode, mobileNumber);
        otpStorage.put(mobileNumber, otpCode);
    }

    @Override
    public boolean verifyOtp(String mobileNumber, String enteredOtp) {
        String storedOtp = otpStorage.get(mobileNumber);
        return storedOtp != null && storedOtp.equals(enteredOtp);
    }

    @Override
    public void clearOtp(String mobileNumber) {
        otpStorage.remove(mobileNumber);
    }
}
