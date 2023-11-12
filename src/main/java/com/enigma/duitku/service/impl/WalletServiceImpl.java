package com.enigma.duitku.service.impl;

import com.enigma.duitku.entity.*;
import com.enigma.duitku.exception.BankAccountException;
import com.enigma.duitku.exception.BeneficiaryException;
import com.enigma.duitku.exception.UserException;
import com.enigma.duitku.exception.WalletException;
import com.enigma.duitku.model.request.TransactionRequest;
import com.enigma.duitku.model.request.WalletRequest;
import com.enigma.duitku.model.response.TransactionResponse;
import com.enigma.duitku.model.response.UserResponse;
import com.enigma.duitku.model.response.WalletResponse;
import com.enigma.duitku.repository.BankAccountRepository;
import com.enigma.duitku.repository.UserRepository;
import com.enigma.duitku.repository.WalletRepository;
import com.enigma.duitku.security.JwtUtils;
import com.enigma.duitku.service.TransactionService;
import com.enigma.duitku.service.UserService;
import com.enigma.duitku.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.TransactionException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;

    private final UserRepository userRepository;

    private final UserService userService;

    private final TransactionService transactionService;

    private final JwtUtils jwtUtils;

    private final BankAccountRepository bankAccountRepository;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public TransactionResponse transferMoneytoBeneficiary(TransactionRequest request, String token) throws BankAccountException, WalletException, BeneficiaryException, UserException{


        String loggedInUserId = jwtUtils.extractUserId(token);
        User validateUser = userService.getById(loggedInUserId);

        if (validateUser != null) {
            Wallet wallet = validateUser.getWallet();

            List<Beneficiary> listofbeneficiaries = wallet.getListOfBeneficiaries();
            Iterator<Beneficiary> iterator = listofbeneficiaries.iterator();
            Beneficiary targetBeneficiary = null;

            while (iterator.hasNext()) {
                Beneficiary b = iterator.next();
                if (Objects.equals(b.getMobileNumber(), request.getBeneficiaryMobileNumber())) {
                    targetBeneficiary = b;
                    break;
                }
            }

            log.info("Beneficiary Mobile number " + request.getBeneficiaryMobileNumber());

            if (targetBeneficiary != null) {
                Optional<Wallet> optionalWallet = walletRepository.findById(validateUser.getMobileNumber());

                if (optionalWallet.isPresent()) {
                    Double availableBalance = optionalWallet.get().getBalance();

                    if (availableBalance >= request.getAmount()) {
                        TransactionRequest transaction = new TransactionRequest();
                        targetBeneficiary.getAccountNo();
                        transaction.setDescription(request.getDescription());
                        transaction.setTransactionType("Beneficiary Transaction");
                        transaction.setReceiver(request.getReceiver());
                        transaction.setAmount(request.getAmount());
                        transactionService.addTransaction(transaction, token);

                        if (transaction != null) {
                            wallet.setBalance(availableBalance - request.getAmount());
                            walletRepository.save(wallet);
                            return TransactionResponse.builder()
                                    .amount(transaction.getAmount())
                                    .receiver(transaction.getReceiver())
                                    .transactionType(transaction.getTransactionType())
                                    .description(transaction.getDescription())
                                    .build();
                        } else {
                            throw new TransactionException("Transaction Failed");
                        }
                    } else {
                        throw new WalletException("Insufficient Funds. Available Bank Balance : " + availableBalance);
                    }
                } else {
                    throw new BankAccountException("No Registered Bank Account Found With This Mobile Number : " + validateUser.getMobileNumber());
                }
            } else {
                throw new BeneficiaryException("No Registered Beneficiary found with this Mobile Number: " + request.getBeneficiaryMobileNumber());
            }
        } else {
            throw new UserException("Please Login in");
        }
    }


    @Override
    public Wallet getById(String id, String token) throws UserException {
        String loggedInUserId = jwtUtils.extractUserId(token);
        User validateUser = userService.getById(loggedInUserId);

        if(validateUser != null) {
            return walletRepository.findById(id).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Wallet Not Found"));
        } else {
            throw new UserException("Please Login in");
        }
    }

}
