package com.enigma.duitku.service;

import com.enigma.duitku.entity.Transaction;
import com.enigma.duitku.entity.User;
import com.enigma.duitku.exception.UserException;
import com.enigma.duitku.model.request.TransactionRequest;
import com.enigma.duitku.model.response.TransactionResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface TransactionService {

    TransactionResponse addTransaction(TransactionRequest request, String token) throws UserException;

    Transaction viewTransactionId(String walletId, String token) throws UserException;

    // Admin
    Page<TransactionResponse> viewAllTransaction(Integer page, Integer size) throws UserException;
}
