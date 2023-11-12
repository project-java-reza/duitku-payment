package com.enigma.duitku.service.impl;

import com.enigma.duitku.entity.BankAccount;
import com.enigma.duitku.entity.Transaction;
import com.enigma.duitku.entity.User;
import com.enigma.duitku.entity.Wallet;
import com.enigma.duitku.exception.UserException;
import com.enigma.duitku.model.request.BankAccountRequest;
import com.enigma.duitku.model.request.TransactionRequest;
import com.enigma.duitku.model.response.BankAccountResponse;
import com.enigma.duitku.model.response.TransactionResponse;
import com.enigma.duitku.repository.BankAccountRepository;
import com.enigma.duitku.repository.TransactionRepository;
import com.enigma.duitku.repository.UserRepository;
import com.enigma.duitku.repository.WalletRepository;
import com.enigma.duitku.security.JwtUtils;
import com.enigma.duitku.service.BankAccountService;
import com.enigma.duitku.service.TransactionService;
import com.enigma.duitku.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.TransactionException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.swing.text.html.Option;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BankAccountServiceImpl implements BankAccountService {

    private final UserRepository userRepository;

    private final BankAccountRepository bankAccountRepository;

    private final TransactionRepository transactionRepository;

    private final TransactionService transactionService;

    private final WalletRepository walletRepository;

    private final JwtUtils jwtUtils;

    private final UserService userService;

    @Override
    public BankAccountResponse addAccount(BankAccountRequest request, String token) {

        String loggedInUserId = jwtUtils.extractUserId(token);
        User user = userService.getById(loggedInUserId);

        if(user != null) {

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
                    .errors("Invalid mobile number, Please Enter Your Registered  Mobile Number!")
                    .build();
        }
    }

    @Override
    public BankAccount getById(String id, String token) throws UserException {
        String loggedInUserId = jwtUtils.extractUserId(token);
        User user = userService.getById(loggedInUserId);

        if(user != null) {
            return bankAccountRepository.findById(id).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bank Account Not found"));
        } else {
            throw new UserException("Plese Login In!");
        }

    }

    @Override
    public BankAccountResponse removeAccountBank(User user, String token) throws UserException {

        String loggedInUserId = jwtUtils.extractUserId(token);
        User userValidate = userService.getById(loggedInUserId);

        if(userValidate != null) {
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

        } else {
            throw new UserException("Plese Login In!");
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public BankAccountResponse topUpWallet(BankAccountRequest request, String token) throws UserException, TransactionException {
        Optional<BankAccount> optionalBankAccount = bankAccountRepository.findById(request.getMobileNumber());

        String loggedInUserId = jwtUtils.extractUserId(token);
        User user = userService.getById(loggedInUserId);

        if(user != null) {
            return optionalBankAccount.map(bankAccount -> {
                double amount = request.getAmount();

                if (bankAccount.getBalance() >= amount){
                    bankAccount.setBalance(bankAccount.getBalance() - amount);

                    Wallet userWallet = user.getWallet();
                    userWallet.setBalance(userWallet.getBalance() + amount);

                    TransactionRequest transaction = new TransactionRequest();
                    user.getMobileNumber();
                    transaction.setDescription("Wallet Top Up");
                    transaction.setTransactionType("E-Wallet Transaction");
                    transaction.setBankName(request.getBankName());
                    transaction.setReceiver(request.getReceiver());
                    transaction.setAmount(request.getAmount());

                    try {
                        transactionService.addTransaction(transaction, token);
                    } catch (UserException e) {
                        throw new RuntimeException(e);
                    }

                    bankAccountRepository.saveAndFlush(bankAccount);

                    return BankAccountResponse.builder()
                            .mobileNumber(request.getMobileNumber())
                            .bankName(bankAccount.getBankName())
                            .balance(bankAccount.getBalance())
                            .accountNo(bankAccount.getAccountNo())
                            .build();
                }else {
                    return BankAccountResponse.builder()
                            .errors("Top Up Failed")
                            .build();
                }
            }).orElseThrow(() -> new RuntimeException("User Not found"));
        } else {
            throw new UserException("Plese Login In!");
        }
    }

    @Override
    public Page<BankAccountResponse> getAllBankAccount(Integer page, Integer size, String token) throws UserException{

        String loggedInUserId = jwtUtils.extractUserId(token);
        User user = userService.getById(loggedInUserId);

        if(user != null) {
            Pageable pageable = PageRequest.of(page, size);
            Page<BankAccount> bankAccounts = bankAccountRepository.findAll(pageable);
            List<BankAccountResponse> bankAccountResponses = new ArrayList<>();
            for(BankAccount bankAccount: bankAccounts.getContent()) {
                BankAccountResponse bankAccountResponse = BankAccountResponse.builder()
                        .balance(bankAccount.getBalance())
                        .accountNo(bankAccount.getAccountNo())
                        .bankName(bankAccount.getBankName())
                        .mobileNumber(bankAccount.getBankName())
                        .build();
                bankAccountResponses.add(bankAccountResponse);
            }
            return new PageImpl<>(bankAccountResponses, pageable, bankAccounts.getTotalElements());
        }else {
            throw new UserException("Please Login In!");
        }
    }
}
