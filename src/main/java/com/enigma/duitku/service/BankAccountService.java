package com.enigma.duitku.service;

import com.enigma.duitku.entity.BankAccount;
import com.enigma.duitku.entity.User;
import com.enigma.duitku.exception.UserException;
import com.enigma.duitku.model.request.BankAccountRequest;
import com.enigma.duitku.model.response.BankAccountResponse;
import org.hibernate.TransactionException;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BankAccountService {

    BankAccountResponse addAccount(BankAccountRequest request, String token) throws UserException;
    BankAccount getById(String id, String token) throws UserException;
    BankAccountResponse removeAccountBank(String id , String token) throws UserException;
    BankAccountResponse topUpWallet(BankAccountRequest request, String token) throws UserException, TransactionException;
    Page<BankAccountResponse> getAllBankAccount(Integer page, Integer size, String token) throws UserException;
}
