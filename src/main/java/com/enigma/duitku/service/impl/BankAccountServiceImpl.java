package com.enigma.duitku.service.impl;

import com.enigma.duitku.entity.BankAccount;
import com.enigma.duitku.entity.User;
import com.enigma.duitku.entity.Wallet;
import com.enigma.duitku.exception.UserException;
import com.enigma.duitku.model.request.BankAccountRequest;
import com.enigma.duitku.model.response.BankAccountResponse;
import com.enigma.duitku.repository.BankAccountRepository;
import com.enigma.duitku.repository.UserRepository;
import com.enigma.duitku.service.BankAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BankAccountServiceImpl implements BankAccountService {

    private final UserRepository userRepository;

    private final BankAccountRepository bankAccountRepository;

    @Override
    public BankAccountResponse addAccount(BankAccountRequest request) {

        Optional<User> optionalUser = userRepository.findById(request.getMobileNumber());

        if(optionalUser.isPresent()) {

            User user = optionalUser.get();

            BankAccount bankAccount = new BankAccount();
            bankAccount.setId(user.getMobileNumber());
            bankAccount.setAccountNo(request.getAccountNo());
            bankAccount.setBalance(request.getBalance());
            bankAccount.setBankName(request.getBankName());

            bankAccountRepository.saveAndFlush(bankAccount);

            return BankAccountResponse.builder()
                    .mobileNumber(request.getMobileNumber())
                    .bankName(request.getBankName())
                    .accountNo(request.getAccountNo())
                    .balance(request.getBalance())
                    .build();

        } else {
            return BankAccountResponse.builder()
                    .errors("Invalid nomor mobile, Please Enter Your Registered  Mobile Number!")
                    .build();
        }
    }

    @Override
    public BankAccount getById(String id) {
        return bankAccountRepository.findById(id).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bank Account Not found"));
    }

    @Override
    public BankAccountResponse removeAccountBank(User user) {

       BankAccount bankAccount = bankAccountRepository.getById(user.getMobileNumber());

        if(bankAccount!=null) {
            bankAccountRepository.delete(bankAccount);

            return BankAccountResponse.builder()
                    .bankName(bankAccount.getBankName())
                    .build();
        } else {
            return BankAccountResponse.builder()
                    .errors("Failed to delete account bank")
                    .build();
        }

    }

    @Override
    public BankAccountResponse topUpWallet(BankAccountRequest request) {
        Optional<BankAccount> optionalBankAccount = bankAccountRepository.findById(request.getMobileNumber());

        Optional<User> optionalUser = userRepository.findById(request.getMobileNumber());

        return optionalBankAccount.map(bankAccount -> {
            double amount = request.getAmount();

            if (bankAccount.getBalance() >= amount){
                bankAccount.setBalance(bankAccount.getBalance() - amount);

                Wallet userWallet = optionalUser.get().getWallet();
                userWallet.setBalance(userWallet.getBalance() + amount);

                bankAccountRepository.saveAndFlush(bankAccount);

                return BankAccountResponse.builder()
                        .mobileNumber(request.getMobileNumber())
                        .bankName("Top Up Success")
                        .balance(bankAccount.getBalance())
                        .accountNo(bankAccount.getAccountNo())
                        .build();
            }else {
                return BankAccountResponse.builder()
                        .errors("Top Up Failed")
                        .build();
            }
        }).orElseThrow(() -> new RuntimeException("User Not found"));
    }
}
