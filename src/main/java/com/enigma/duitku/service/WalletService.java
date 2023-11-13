package com.enigma.duitku.service;

import com.enigma.duitku.entity.Transaction;
import com.enigma.duitku.entity.User;
import com.enigma.duitku.entity.Wallet;
import com.enigma.duitku.exception.*;
import com.enigma.duitku.model.request.TransactionRequest;
import com.enigma.duitku.model.request.WalletRequest;
import com.enigma.duitku.model.response.TransactionResponse;
import com.enigma.duitku.model.response.WalletResponse;

import java.util.List;

public interface WalletService {

        TransactionResponse transferMoneytoBeneficiary(TransactionRequest transaction, String token) throws BankAccountException, WalletException, BeneficiaryException, UserException;

        TransactionResponse transferMoneytoUser(TransactionRequest request, String token) throws UserException, TargetUserNotFoundException, UserNotFoundException;

        Wallet getById(String id, String token) throws UserException;
}
