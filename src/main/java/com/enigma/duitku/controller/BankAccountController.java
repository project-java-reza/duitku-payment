package com.enigma.duitku.controller;

import com.enigma.duitku.entity.BankAccount;
import com.enigma.duitku.entity.User;
import com.enigma.duitku.exception.UserException;
import com.enigma.duitku.model.request.BankAccountRequest;
import com.enigma.duitku.model.response.BankAccountResponse;
import com.enigma.duitku.model.response.CommonResponse;
import com.enigma.duitku.model.response.PagingResponse;
import com.enigma.duitku.security.AuthTokenFilter;
import com.enigma.duitku.service.BankAccountService;
import lombok.RequiredArgsConstructor;
import org.hibernate.TransactionException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/bankaccount")
public class BankAccountController {

    private final BankAccountService bankAccountService;

    private final AuthTokenFilter authTokenFilter;

    @PostMapping("/add")
    public ResponseEntity<?> addBankAccount(@RequestBody BankAccountRequest request, HttpServletRequest httpServletRequest)  {
        try {
            String jwtToken = authTokenFilter.parseJwt(httpServletRequest);
            BankAccountResponse bankAccountResponse = bankAccountService.addAccount(request, jwtToken);

            if(bankAccountResponse.getErrors() != null || request.getMobileNumber().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(CommonResponse.builder()
                                .statusCode(HttpStatus.BAD_REQUEST.value())
                                .data(bankAccountResponse)
                                .message("Failed created bank account")
                                .build());

            } else {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(CommonResponse.builder()
                                .statusCode(HttpStatus.CREATED.value())
                                .data(bankAccountResponse)
                                .message("Successfully created bank account")
                                .build());
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CommonResponse.<BankAccountResponse>builder()
                            .statusCode(HttpStatus.NOT_FOUND.value())
                            .message("Failed add account " + e.getMessage())
                            .build());
        }

    }

    @GetMapping("/profile/{id}")
   public ResponseEntity<?> getViewProfileBankAccount(@PathVariable String id, HttpServletRequest httpServletRequest) throws UserException {
        try {
            String jwtToken = authTokenFilter.parseJwt(httpServletRequest);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(CommonResponse.builder()
                            .statusCode(HttpStatus.OK.value())
                            .message("Successfully get bank account by id")
                            .data(bankAccountService.getById(id, jwtToken))
                            .build());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CommonResponse.<BankAccountResponse>builder()
                            .statusCode(HttpStatus.NOT_FOUND.value())
                            .message("Profile id not found" + e.getMessage())
                            .build());
        }


   }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteBankAccount(@RequestBody User user, HttpServletRequest httpServletRequest) throws UserException{
        try {
            String jwtToken = authTokenFilter.parseJwt(httpServletRequest);
            bankAccountService.removeAccountBank(user, jwtToken);

            BankAccount bankAccount = new BankAccount();
            return ResponseEntity.status(HttpStatus.OK)
                    .body(CommonResponse.builder()
                            .statusCode(HttpStatus.OK.value())
                            .message(String.valueOf("Sucessfully delete account bank"))
                            .build());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CommonResponse.<BankAccountResponse>builder()
                            .statusCode(HttpStatus.NOT_FOUND.value())
                            .message("Delete Failed " + e.getMessage())
                            .build());
        }
    }

    @PostMapping("/topup")
    public ResponseEntity<?> topUpWallet(@RequestBody BankAccountRequest request, HttpServletRequest httpServletRequest) throws UserException, TransactionException {
        try {
            String jwtToken = authTokenFilter.parseJwt(httpServletRequest);
            BankAccountResponse topUp = bankAccountService.topUpWallet(request, jwtToken);

            if(topUp.getErrors() != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(CommonResponse.builder()
                                .statusCode(HttpStatus.BAD_REQUEST.value())
                                .data(topUp)
                                .message("Failed Top Up")
                                .build());

            } else {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(CommonResponse.<BankAccountResponse>builder()
                                .statusCode(HttpStatus.OK.value())
                                .data(topUp)
                                .message("Top Up Successful")
                                .build());
            }

        }catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CommonResponse.<BankAccountResponse>builder()
                            .statusCode(HttpStatus.NOT_FOUND.value())
                            .message("Top Up Failed. " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/viewall")
    public ResponseEntity<?> viewAllBankAccount(
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "5") Integer size,
            HttpServletRequest httpServletRequest
    ) throws UserException{

        try {
            String jwtToken = authTokenFilter.parseJwt(httpServletRequest);

            if(jwtToken != null) {
                Page<BankAccountResponse> bankAccountResponses = bankAccountService.getAllBankAccount(page, size, jwtToken);
                PagingResponse pagingResponse = PagingResponse.builder()
                        .currentPage(page)
                        .totalPage(bankAccountResponses.getTotalPages())
                        .size(size)
                        .build();

                return ResponseEntity.status(HttpStatus.OK)
                        .body(CommonResponse.builder()
                                .statusCode(HttpStatus.OK.value())
                                .message("View all successfull")
                                .data(bankAccountResponses.getContent())
                                .paging(pagingResponse)
                                .build());
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(CommonResponse.builder()
                                .statusCode(HttpStatus.UNAUTHORIZED.value())
                                .build());
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CommonResponse.<BankAccountResponse>builder()
                            .statusCode(HttpStatus.NOT_FOUND.value())
                            .message("View all bank account " + e.getMessage())
                            .build());
        }
    }
}
