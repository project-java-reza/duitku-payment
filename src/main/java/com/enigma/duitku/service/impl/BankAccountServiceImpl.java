package com.enigma.duitku.service.impl;

import com.enigma.duitku.entity.BankAccount;
import com.enigma.duitku.entity.User;
import com.enigma.duitku.exception.UserException;
import com.enigma.duitku.model.request.BankAccountRequest;
import com.enigma.duitku.model.response.BankAccountResponse;
import com.enigma.duitku.repository.BankAccountRepository;
import com.enigma.duitku.repository.UserRepository;
import com.enigma.duitku.service.BankAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BankAccountServiceImpl implements BankAccountService {

    private final UserRepository userRepository;

    private final BankAccountRepository bankAccountRepository;

    @Override
    public BankAccountResponse addAccount(BankAccountRequest request) throws UserException {

        Optional<User> optionalUser = userRepository.findById(request.getMobileNumber());

        if(optionalUser.isPresent()) {

            User user = optionalUser.get();

            BankAccount bankAccount = new BankAccount();
            bankAccount.setId(user.getMobilePhone());
            bankAccount.setAccountNo(request.getAccountNo());
            bankAccount.setBalance(request.getBalance());
            bankAccount.setBankName(request.getBankName());

            bankAccountRepository.saveAndFlush(bankAccount);

        } else {
            throw new UserException("Invalid nomor mobile, Please Enter Your Registered  Mobile Number!");
        }

        return BankAccountResponse.builder()
                .bankName(request.getBankName())
                .accountNo(request.getAccountNo())
                .balance(request.getBalance())
                .build();
    }
}