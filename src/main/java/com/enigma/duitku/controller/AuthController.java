package com.enigma.duitku.controller;

import com.enigma.duitku.exception.OtpVerificationException;
import com.enigma.duitku.exception.UserException;
import com.enigma.duitku.model.request.AuthRequest;
import com.enigma.duitku.model.response.CommonResponse;
import com.enigma.duitku.model.response.LoginResponse;
import com.enigma.duitku.model.response.RegisterResponse;
import com.enigma.duitku.service.AuthService;
import com.enigma.duitku.service.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "api/auth")
public class AuthController {

    private final AuthService authService;
    private final OtpService otpService;

    @PostMapping(path = "/register/user")
    public ResponseEntity<?> registerUsers(@RequestBody AuthRequest authRequest, @RequestParam String enteredOtp)throws UserException, OtpVerificationException {
        RegisterResponse register = authService.registerUsers(authRequest, enteredOtp);
        CommonResponse<Object> commonResponse = CommonResponse.builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Successfully registered user")
                .data(register)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(commonResponse);
    }

    @PostMapping(path = "/register/admin")
    public ResponseEntity<?> registerAdmin(@RequestBody AuthRequest authRequest) throws UserException{
        RegisterResponse register = authService.registerAdmin(authRequest);
        CommonResponse<Object> commonResponse = CommonResponse.builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Successfully registered admin")
                .data(register)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(commonResponse);
    }

    /*
       @PostMapping("/generateOtp")
         public String generateOtp() {
         String otp = otpService.generateOtp();
         return otp;
         }
    */

    @PostMapping("/send")
    public ResponseEntity<String> sendOtp(@RequestParam String mobileNumber) {
        try {
            // Generate OTP
            String otpCode = otpService.generateOtp();

            // Kirim OTP
            otpService.sendOtp(mobileNumber, otpCode);

            return ResponseEntity.ok("OTP successfully sent to " + mobileNumber);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to send OTP");
        }
    }

    @PostMapping(path = "/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        LoginResponse responseLogin = authService.login(authRequest);
        CommonResponse<Object> commonResponse = CommonResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully Login")
                .data(responseLogin)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

}
