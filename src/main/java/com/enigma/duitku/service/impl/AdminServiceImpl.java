package com.enigma.duitku.service.impl;

import com.enigma.duitku.entity.Admin;
import com.enigma.duitku.entity.User;
import com.enigma.duitku.entity.Wallet;
import com.enigma.duitku.entity.constant.EWalletType;
import com.enigma.duitku.exception.UserException;
import com.enigma.duitku.model.response.UserResponse;
import com.enigma.duitku.repository.AdminRepository;
import com.enigma.duitku.repository.UserRepository;
import com.enigma.duitku.service.AdminService;
import com.enigma.duitku.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final ValidationUtil validationUtil;

    @Override
    public Admin create(Admin admin) throws UserException {
        validationUtil.validate(admin);

        Optional<Admin> findUser = adminRepository.findById(admin.getMobileNumber());

        if(findUser.isEmpty()) {
            Wallet wallet = new Wallet();
            wallet.setBalance(0.0);
            wallet.setId(admin.getMobileNumber());
            wallet.setWalletType(EWalletType.PREMIUM);
            admin.setWallet(wallet);

            Admin addAdmin=adminRepository.saveAndFlush(admin);

            if(addAdmin!=null) {
                return addAdmin;
            } else {
                throw new UserException("Sorry, sign up unsuccessfull!");
            }

        } else {
            throw new UserException("Admin already Registered With The Mobile Number: " +  admin.getMobileNumber());
        }
    }

    @Override
    public Admin getByIdAdmin(String id) {
        return adminRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found"));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Override
    public Page<UserResponse> getAll(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = userRepository.findAll(pageable);
        List<UserResponse> userResponses = new ArrayList<>();
        for (User user : users.getContent()) {
            UserResponse userResponse = new UserResponse(user.getMobileNumber(), user.getFirstName(), user.getLastName(), user.getEmail(),null );
            userResponses.add(userResponse);
        }
        return new PageImpl<>(userResponses, pageable, users.getTotalElements());
    }
}
