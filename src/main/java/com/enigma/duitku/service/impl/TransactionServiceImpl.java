package com.enigma.duitku.service.impl;

import com.enigma.duitku.entity.Transaction;
import com.enigma.duitku.entity.User;
import com.enigma.duitku.entity.Wallet;
import com.enigma.duitku.exception.UserException;
import com.enigma.duitku.model.request.TransactionRequest;
import com.enigma.duitku.model.response.TransactionResponse;
import com.enigma.duitku.repository.TransactionRepository;
import com.enigma.duitku.repository.UserRepository;
import com.enigma.duitku.repository.WalletRepository;
import com.enigma.duitku.service.TransactionService;
import com.enigma.duitku.service.UserService;
import com.enigma.duitku.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    private final WalletRepository walletRepository;

    private final UserService userService;

    private final UserRepository userRepository;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public TransactionResponse addTransaction(String receiver, String description, String transationType, Double amount, String mobileNumber) {

        Optional<User> optionalUser = userRepository.findById(mobileNumber);

        if(optionalUser.isPresent()) {

            User user = optionalUser.get();

            Wallet wallet = user.getWallet();

            Transaction transaction = new Transaction();

            transaction.setAmount(amount);
            transaction.setLocalDate(LocalDateTime.now());
            transaction.setDescription(description);
            transaction.setReceiver(receiver);
            transaction.setType(transationType);

            transaction.setWalletId(wallet.getId());

            List<Transaction> listoftransactions = wallet.getListOfTransactions();

            listoftransactions.add(transaction);

            wallet.setListOfTransactions(listoftransactions);

            walletRepository.saveAndFlush(wallet);
            transactionRepository.saveAndFlush(transaction);

            return TransactionResponse.builder()
                    .amount(transaction.getAmount())
                    .description(transaction.getDescription())
                    .transactionType(transaction.getType())
                    .build();
        } else {
            return TransactionResponse.builder().build();
        }
    }

    private User getUserFromRequest(TransactionRequest request) {
        User user = userService.getById(request.getUserId());
        if (user == null) {
            throw new RuntimeException("User not found for ID: " + request.getUserId());
        }
        return user;
    }

    @Override
    public Transaction viewTransactionId(String transationId) {
        return null;
    }

    @Override
    public List<TransactionResponse> viewAllTransaction() {
        return null;
    }
}
