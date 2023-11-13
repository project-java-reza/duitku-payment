package com.enigma.duitku.service.impl;

import com.enigma.duitku.entity.Bill;
import com.enigma.duitku.entity.Transaction;
import com.enigma.duitku.entity.User;
import com.enigma.duitku.entity.Wallet;
import com.enigma.duitku.exception.UserException;
import com.enigma.duitku.exception.WalletException;
import com.enigma.duitku.model.request.TransactionRequest;
import com.enigma.duitku.model.response.TransactionResponse;
import com.enigma.duitku.repository.WalletRepository;
import com.enigma.duitku.security.JwtUtils;
import com.enigma.duitku.service.BillService;
import com.enigma.duitku.service.TransactionService;
import com.enigma.duitku.service.UserService;
import lombok.RequiredArgsConstructor;
import org.hibernate.TransactionException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BillServiceImpl implements BillService {

    private final JwtUtils jwtUtils;

    private final UserService userService;

    private final WalletRepository walletRepository;

    private final TransactionService transactionService;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public TransactionResponse billPayment(Bill bill, String token) throws WalletException, UserException {
        String loggedInUserId = jwtUtils.extractUserId(token);
        User user = userService.getById(loggedInUserId);

        if(user != null) {
            Wallet wallet = user.getWallet();
            Double availableBalane = wallet.getBalance();
            List<Bill> listOfBills = wallet.getListofBills();
            if(availableBalane >= bill.getAmount()) {
                TransactionRequest transaction = new TransactionRequest();
                transaction.setReceiver(bill.getReceiver());
                transaction.setBillType(bill.getBillType());
                transaction.setTransactionType("Bill Payment");
                transaction.setAmount(bill.getAmount());
                transaction.setDescription(bill.getDescription());
                transactionService.addTransaction(transaction, token);

                if(transaction != null) {
                    listOfBills.add(bill);
                    wallet.setBalance(availableBalane - bill.getAmount());
                    wallet.setListofBills(listOfBills);
                    walletRepository.saveAndFlush(wallet);
                    return TransactionResponse.builder()
                            .amount(transaction.getAmount())
                            .transactionType(transaction.getBillType())
                            .description(transaction.getDescription())
                            .receiver(transaction.getReceiver())
                            .build();
                } else {
                    throw new TransactionException("Transaction failed!");
                }

            } else {
                throw new WalletException("Insufficient Funds ! Available Wallet Balance : " + availableBalane);
            }
        } else {
            throw new UserException("Please login in");
        }
    }

    @Override
    public Page<Bill> viewBillPayments(Integer page, Integer size, String token) throws UserException {
        String loggedInUserId = jwtUtils.extractUserId(token);
        User user = userService.getById(loggedInUserId);

        if (user != null) {
            Wallet wallet = user.getWallet();

            List<Bill> billList = wallet.getListofBills();

            int start = (int) (page * size);
            int end = Math.min((start + size), billList.size());

            List<Bill> paginatedBillList = billList.subList(start, end);

            Pageable pageable = PageRequest.of(page, size);
            return new PageImpl<>(paginatedBillList, pageable, billList.size());
        } else {
            throw new UserException("Please login in !");
        }
    }
}

