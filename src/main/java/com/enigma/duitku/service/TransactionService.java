package com.enigma.duitku.service;

import com.enigma.duitku.entity.Transaction;
import com.enigma.duitku.exception.UserException;
import com.enigma.duitku.model.request.TransactionRequest;
import com.enigma.duitku.model.response.TransactionResponse;

import java.util.List;

public interface TransactionService {

    TransactionResponse addTransaction(String receiver, String description, String transationType, Double amount, String mobileNumber);

    Transaction viewTransactionId(String transationId);

    List<TransactionResponse> viewAllTransaction();
}
