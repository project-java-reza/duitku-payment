package com.enigma.duitku.service.impl;

import com.enigma.duitku.entity.Beneficiary;
import com.enigma.duitku.entity.User;
import com.enigma.duitku.entity.Wallet;
import com.enigma.duitku.exception.BeneficiaryException;
import com.enigma.duitku.model.request.BeneficiaryRequest;
import com.enigma.duitku.model.response.BeneficiaryResponse;
import com.enigma.duitku.repository.BeneficiaryRepository;
import com.enigma.duitku.repository.UserRepository;
import com.enigma.duitku.repository.WalletRepository;
import com.enigma.duitku.security.JwtUtils;
import com.enigma.duitku.service.BeneficiaryService;
import com.enigma.duitku.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.security.auth.login.LoginException;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BeneficiaryServiceImpl implements BeneficiaryService {

    private final UserService userService;

    private final WalletRepository walletRepository;

    private final BeneficiaryRepository beneficiaryRepository;

    private final JwtUtils jwtUtils;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public BeneficiaryResponse addBeneficiary(BeneficiaryRequest request, String token) {

        // TODO 1: Validate user
        String loggedInUserId = jwtUtils.extractUserId(token);
        User user = userService.getById(loggedInUserId);

        if(user != null) {

            // TODO 2: Retrieve wallet and beneficiaries
            Wallet wallet = user.getWallet();
            List<Beneficiary> beneficiaryList = wallet.getListOfBeneficiaries();

            Beneficiary databasebeneficiary = null;

            // TODO 3: Check for existing beneficiary
            for (Beneficiary b : beneficiaryList) {
                if (Objects.equals(b.getMobileNumber(), request.getMobileNumber())) {
                    databasebeneficiary = b;
                    break;
                }
            }

            // TODO 4: Add beneficiary if not exist
            if (databasebeneficiary == null) {
                Beneficiary newBeneficiary = new Beneficiary();
                newBeneficiary.setMobileNumber(request.getMobileNumber());
                newBeneficiary.setName(request.getName());
                newBeneficiary.setBankName(request.getBankName());
                newBeneficiary.setAccountNo(request.getAccountNo());

                // TODO 5 : Save the newBeneficiary first
                newBeneficiary = beneficiaryRepository.save(newBeneficiary);

                // TODO 6 : Add the newBeneficiary to the list of beneficiaries
                beneficiaryList.add(newBeneficiary);
                wallet.setListOfBeneficiaries(beneficiaryList);

                // TODO 7 : Save the wallet with the updated list of beneficiaries
                walletRepository.save(wallet);
            } else {
                return BeneficiaryResponse.builder()
                        .errors("Failed to add account")
                        .build();
            }

            // TODO 8: Return BeneficiaryResponse
            return BeneficiaryResponse.builder()
                    .mobileNumber(request.getMobileNumber())
                    .bankName(request.getBankName())
                    .name(request.getName())
                    .accountNo(request.getAccountNo())
                    .build();
        }else {
            return BeneficiaryResponse.builder()
                    .errors("Mobile Number Not Registered!")
                    .build();
        }
    }

    @Override
    public List<BeneficiaryResponse> viewAllBeneficiaries() {

        List<Beneficiary> beneficiaries = beneficiaryRepository.findAll();
        List<BeneficiaryResponse> responses = new ArrayList<>();
        for(Beneficiary beneficiary : beneficiaries) {
            BeneficiaryResponse response = BeneficiaryResponse.builder()
                    .mobileNumber(beneficiary.getMobileNumber())
                    .accountNo(beneficiary.getAccountNo())
                    .name(beneficiary.getName())
                    .bankName(beneficiary.getBankName())
                    .build();
            responses.add(response);
        }
        return responses;
    }

    @Override
    public String deleteByMobileNumber(String beneficiaryMobileNumber, String token) throws BeneficiaryException, LoginException {

        String loggedInUserId = jwtUtils.extractUserId(token);
        User user = userService.getById(loggedInUserId);

        if(user != null) {
            Wallet wallet = user.getWallet();
            List<Beneficiary> beneficiaryList = wallet.getListOfBeneficiaries();

            if(!beneficiaryList.isEmpty()) {

                Beneficiary targetBeneficiary= null;

                for (Beneficiary b : beneficiaryList) {
                    if (Objects.equals(b.getMobileNumber(), beneficiaryMobileNumber)) {
                        targetBeneficiary= b;
                    }
                }

                if(targetBeneficiary != null) {
                    Beneficiary deleteBeneficiary = null;
                    Boolean flag = false;

                    for (Beneficiary b : beneficiaryList) {
                        if(Objects.equals(b.getMobileNumber(), beneficiaryMobileNumber)) {
                            deleteBeneficiary = b;
                            flag = true;
                        }
                    }

                    if(deleteBeneficiary != null && flag) {
                        beneficiaryList.remove(deleteBeneficiary);
                        wallet.setListOfBeneficiaries(beneficiaryList);
                        walletRepository.save(wallet);
                        beneficiaryRepository.delete(deleteBeneficiary);

                        return "Beneficiary has been Successfully deleted";
                    } else {
                        throw new BeneficiaryException("No Registered Beneficiary Found with this Mobile Number : " + beneficiaryMobileNumber);
                    }

                }else {
                    throw new BeneficiaryException("No Registered Beneficiary Found with this Mobile Number : " + beneficiaryMobileNumber);
                }

            }else {
                throw new LoginException("Please Log In !");
            }

        }
        return "Beneficiary has been Successfully deleted\"";
    }
}
