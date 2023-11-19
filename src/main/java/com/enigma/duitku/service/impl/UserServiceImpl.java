package com.enigma.duitku.service.impl;

import com.enigma.duitku.entity.User;
import com.enigma.duitku.entity.Wallet;
import com.enigma.duitku.entity.constant.EWalletType;
import com.enigma.duitku.exception.UserException;
import com.enigma.duitku.repository.UserRepository;
import com.enigma.duitku.service.UserService;
import com.enigma.duitku.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final ValidationUtil validationUtil;

    @Override
    public User create(User user) throws UserException {
        validationUtil.validate(user);
        try {
            Optional<User> findUser = userRepository.findById(user.getMobileNumber());

            if(findUser.isEmpty()) {

                Wallet wallet = new Wallet();
                wallet.setBalance(0.0);
                wallet.setId(user.getMobileNumber());
                wallet.setWalletType(EWalletType.BASIC);
                user.setWallet(wallet);

                User addUser=userRepository.saveAndFlush(user);

                if(addUser !=null) {
                    return addUser;
                } else {
                    throw new UserException("Sorry, sign up unsuccessfull!");
                }

            } else {
                throw new UserException("User already Registered With The Mobile Number: " +  user.getMobileNumber());
            }

        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "email already used");
        }
    }

    @Override
    public User getById(String id) {
        return userRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "customer not found"));
    }

    @Override
    public User update(User user) {
        User currentUser= getById(user.getMobileNumber());
        if (currentUser!= null) {
            return userRepository.save(user);
        } else {
            throw new RuntimeException("User by Number " + user.getMobileNumber() + " No Found. Unable to update.");
        }
    }

    @Override
    public void deleteById(String id) {
        User user= getById(id);
        userRepository.delete(user);
    }
}
