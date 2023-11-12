package com.enigma.duitku.service;

import com.enigma.duitku.entity.Transaction;
import com.enigma.duitku.entity.Wallet;
import com.enigma.duitku.exception.BankAccountException;
import com.enigma.duitku.exception.BeneficiaryException;
import com.enigma.duitku.exception.UserException;
import com.enigma.duitku.exception.WalletException;
import com.enigma.duitku.model.request.TransactionRequest;
import com.enigma.duitku.model.request.WalletRequest;
import com.enigma.duitku.model.response.TransactionResponse;
import com.enigma.duitku.model.response.WalletResponse;

import java.util.List;

public interface WalletService {

        TransactionResponse transferMoneytoBeneficiary(TransactionRequest transaction, String token) throws BankAccountException, WalletException, BeneficiaryException, UserException;
        Wallet getById(String id, String token) throws UserException;
}
