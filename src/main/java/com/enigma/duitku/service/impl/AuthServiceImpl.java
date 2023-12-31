package com.enigma.duitku.service.impl;

import com.enigma.duitku.entity.*;
import com.enigma.duitku.entity.constant.ERole;
import com.enigma.duitku.entity.constant.EWalletType;
import com.enigma.duitku.exception.OtpVerificationException;
import com.enigma.duitku.exception.UserException;
import com.enigma.duitku.model.request.AuthRequest;
import com.enigma.duitku.model.response.LoginResponse;
import com.enigma.duitku.model.response.RegisterResponse;
import com.enigma.duitku.repository.UserCredentialRepository;
import com.enigma.duitku.security.BCryptUtils;
import com.enigma.duitku.security.JwtUtils;
import com.enigma.duitku.service.*;
import com.enigma.duitku.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserCredentialRepository userCredentialRepository;
    private final BCryptUtils bCryptUtils;
    private final UserService userService;
    private final RoleService roleService;
    private final AdminService adminService;
    private final OtpService otpService;
    private final ValidationUtil validationUtil;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public RegisterResponse registerUsers(AuthRequest authRequest, String otpCode) throws UserException, OtpVerificationException {
        try {
            log.info("OTP CODE: " + otpCode);
            if(otpService.verifyOtp(authRequest.getMobileNumber(), otpCode)) {
                Role role = roleService.getOrSave(ERole.ROLE_USER);
                UserCredential credential= UserCredential.builder()
                        .mobileNumber(authRequest.getMobileNumber())
                        .password(bCryptUtils.hashPassword(authRequest.getPassword()))
                        .roles(List.of(role))
                        .build();
                userCredentialRepository.saveAndFlush(credential);

                User user = User.builder()
                        .firstName(authRequest.getFirstName())
                        .lastName(authRequest.getLastName())
                        .mobileNumber(authRequest.getMobileNumber())
                        .email(authRequest.getEmail())
                        .dateOfBirth(authRequest.getDateOfbirth())
                        .walletType(EWalletType.BASIC)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .userCredential(credential)
                        .build();
                userService.create(user);

                // hapus OTP setelah di verifikasi
                otpService.clearOtp(authRequest.getMobileNumber());

                Wallet wallet = new Wallet();
                wallet.setBalance(0.0);

                return RegisterResponse.builder()
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .mobileNumber(user.getMobileNumber())
                        .balance(wallet.getBalance())
                        .email(user.getEmail())
                        .dateOfBirth(user.getDateOfBirth())
                        .build();
            } else {
                throw new OtpVerificationException("Kode OTP tidak valid");
            }

        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists");
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public RegisterResponse registerAdmin(AuthRequest authRequest)throws UserException {
        try {
            Role role = roleService.getOrSave(ERole.ROLE_ADMIN);
            UserCredential credential= UserCredential.builder()
                    .mobileNumber(authRequest.getMobileNumber())
                    .password(bCryptUtils.hashPassword(authRequest.getPassword()))
                    .roles(List.of(role))
                    .build();
            userCredentialRepository.saveAndFlush(credential);

            Admin admin = Admin.builder()
                    .firstName(authRequest.getFirstName())
                    .lastName(authRequest.getLastName())
                    .mobileNumber(authRequest.getMobileNumber())
                    .email(authRequest.getEmail())
                    .dateOfBirth(authRequest.getDateOfbirth())
                    .walletType(EWalletType.PREMIUM)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .userCredential(credential)
                    .build();
            adminService.create(admin);

            Wallet wallet = new Wallet();
            wallet.setBalance(0.0);

            return RegisterResponse.builder()
                    .firstName(admin.getFirstName())
                    .lastName(admin.getLastName())
                    .mobileNumber(admin.getMobileNumber())
                    .balance(wallet.getBalance())
                    .email(admin.getEmail())
                    .dateOfBirth(admin.getDateOfBirth())
                    .build();

        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Admin already exists");
        }
    }

    @Override
    public LoginResponse login(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getMobileNumber(),
                request.getPassword()
        ));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailImpl userDetails = (UserDetailImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());

        String token = jwtUtils.generateToken(userDetails.getMobileNumber());
        return LoginResponse.builder()
                .mobileNumber(userDetails.getMobileNumber())
                .roles(roles)
                .token(token)
                .build();
    }

}
