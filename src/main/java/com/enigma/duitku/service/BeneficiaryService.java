package com.enigma.duitku.service;

import com.enigma.duitku.exception.BeneficiaryException;
import com.enigma.duitku.model.request.AuthRequest;
import com.enigma.duitku.model.request.BeneficiaryRequest;
import com.enigma.duitku.model.response.BeneficiaryResponse;

import javax.security.auth.login.LoginException;
import java.util.List;

public interface BeneficiaryService {

    BeneficiaryResponse addBeneficiary(BeneficiaryRequest request, String token);

    List<BeneficiaryResponse> viewAllBeneficiaries();

    String deleteByMobileNumber(String beneficiaryMobileNumber, String token) throws BeneficiaryException, LoginException;
}
