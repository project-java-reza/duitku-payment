package com.enigma.duitku.service.impl;

import com.enigma.duitku.entity.Admin;
import com.enigma.duitku.entity.User;
import com.enigma.duitku.entity.Wallet;
import com.enigma.duitku.exception.UserException;
import com.enigma.duitku.model.response.UserResponse;
import com.enigma.duitku.repository.AdminRepository;
import com.enigma.duitku.repository.UserRepository;
import com.enigma.duitku.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final UserRepository userRepository;

    @Override
    public Admin create(Admin admin) throws UserException {

        // TODO 1 Getting Optional Admin by Mobile Number
        Optional<Admin> findUser = adminRepository.findById(admin.getMobileNumber());

        // TODO 2 Checking if User with the Mobile Number already exists
        if(findUser.isEmpty()) {

            // TODO 3 Creating a new Wallet for the User
            Wallet wallet = new Wallet();
            wallet.setBalance(0.0);
            wallet.setId(admin.getMobileNumber());
            admin.setWallet(wallet);

            // TODO 4 Saving and Flushing the User to the Repository
            Admin addAdmin=adminRepository.saveAndFlush(admin);

            // TODO 5 Checking if User is successfully added
            if(addAdmin!=null) {
                return addAdmin;
            } else {
                throw new UserException("Sorry, sign up unsuccessfull!");
            }

        } else {
            throw new UserException("Admin already Registered With The Mobile Number: " +  admin.getMobileNumber());
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Override
    public Page<UserResponse> getAll(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = userRepository.findAll(pageable);
        List<UserResponse> userResponses = new ArrayList<>();
        for (User user : users.getContent()) {
            UserResponse userResponse = new UserResponse(user.getMobileNumber(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getDateOfBirth());
            userResponses.add(userResponse);
        }
        return new PageImpl<>(userResponses, pageable, users.getTotalElements());
    }
}
