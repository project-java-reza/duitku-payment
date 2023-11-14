package com.enigma.duitku.service.impl;

import com.enigma.duitku.entity.*;
import com.enigma.duitku.exception.TransferException;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
    public BankAccountResponse addAccount(BankAccountRequest request, String token) throws UserException {

        String loggedInUserId = jwtUtils.extractUserId(token);
        User user = userService.getById(loggedInUserId);

        if(user != null) {

            BankAccount bankAccount = new BankAccount();
            bankAccount.setId(request.getMobileNumber());
            bankAccount.setAccountNo(request.getAccountNo());
            bankAccount.setBalance(request.getBalance());
            bankAccount.setBankName(request.getBankName());
            bankAccount.setUser(user);

            bankAccountRepository.saveAndFlush(bankAccount);

            return BankAccountResponse.builder()
                    .mobileNumber(request.getMobileNumber())
                    .bankName(request.getBankName())
                    .accountNo(request.getAccountNo())
                    .balance(request.getBalance())
                    .build();
        } else {
            throw new UserException("Please Login in !");
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
    @Transactional(rollbackOn = Exception.class)
    public BankAccountResponse topUpWallet(BankAccountRequest request, String token) throws UserException, TransactionException, TransferException{
        Optional<BankAccount> optionalBankAccount = bankAccountRepository.findById(request.getMobileNumber());

        // TODO 1 Extracting User ID from JWT Token & Validate User
        String loggedInUserId = jwtUtils.extractUserId(token);
        User user = userService.getById(loggedInUserId);

        // TODO 2 Checking if User is not null
        if(user != null) {

            // TODO 3 Mapping Optional Bank Account to Bank Account Response
            return optionalBankAccount.map(bankAccount -> {

                // TODO 4 Extracting Amount from the Request
                double amount = request.getAmount();

                // TODO 5 Checking if Bank Account Balance is Sufficient
                if (bankAccount.getBalance() >= amount){

                    if (amount < 5.000) {
                        throw new TransferException("Minimum balance requirement not met. Minimum amount: 5,000" );
                    }

                    // TODO 6 Updating Bank Account and User Wallet Balances
                    bankAccount.setBalance(bankAccount.getBalance() - amount);

                    Wallet userWallet = user.getWallet();
                    userWallet.setBalance(userWallet.getBalance() + amount);

                    // TODO 7 Creating Transaction Request
                    TransactionRequest transaction = new TransactionRequest();
                    user.getMobileNumber();
                    transaction.setDescription("Wallet Top Up");
                    transaction.setTransactionType("E-Wallet Transaction");
                    transaction.setBankName(request.getBankName());
                    transaction.setReceiver(request.getReceiver());
                    transaction.setAmount(request.getAmount());

                    // TODO 8 Handling Transaction Service Exception
                    try {
                        transactionService.addTransaction(transaction, token);
                    } catch (UserException e) {
                        throw new RuntimeException(e);
                    }

                    // TODO 9 Saving Bank Account;
                    bankAccountRepository.saveAndFlush(bankAccount);

                    // TODO 10 Building and Returning Bank Account Response
                    return BankAccountResponse.builder()
                            .mobileNumber(request.getMobileNumber())
                            .bankName(bankAccount.getBankName())
                            .balance(bankAccount.getBalance())
                            .accountNo(bankAccount.getAccountNo())
                            .build();
                }else {
                    throw new TransferException("Insufficient Funds! Available Wallet Balance: " + bankAccount.getBalance());
                }
            }).orElseThrow(() -> new RuntimeException("User Not found"));
        } else {
            throw new UserException("Plese Login In!");
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Override
    public Page<BankAccountResponse> getAllBankAccount(Integer page, Integer size) throws UserException{

            Pageable pageable = PageRequest.of(page, size);
            Page<BankAccount> bankAccounts = bankAccountRepository.findAll(pageable);
            List<BankAccountResponse> bankAccountResponses = new ArrayList<>();
            for(BankAccount bankAccount: bankAccounts.getContent()) {
                BankAccountResponse bankAccountResponse = BankAccountResponse.builder()
                        .balance(bankAccount.getBalance())
                        .accountNo(bankAccount.getAccountNo())
                        .bankName(bankAccount.getBankName())
                        .mobileNumber(bankAccount.getId())
                        .build();
                bankAccountResponses.add(bankAccountResponse);
            }
            return new PageImpl<>(bankAccountResponses, pageable, bankAccounts.getTotalElements());
    }
}
